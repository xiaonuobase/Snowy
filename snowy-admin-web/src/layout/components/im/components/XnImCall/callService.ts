import { ref, reactive } from 'vue';
import { notification } from 'ant-design-vue';
import { CallType, CallStatus, WebRTCState, IncomingCall, CallConfig } from './types';

// 默认ICE服务器配置
const defaultIceServers = [
	{ urls: 'stun:stun.l.google.com:19302' },
	{ urls: 'stun:stun1.l.google.com:19302' },
	{ urls: 'stun:stun2.l.google.com:19302' },
	{ urls: 'stun:stun3.l.google.com:19302' },
	{ urls: 'stun:stun4.l.google.com:19302' }
];

/**
 * 创建通话服务
 * @param sendMessageFunc 发送消息到WebSocket的函数
 * @param config 可选配置
 * @returns 通话服务对象
 */
export function useCallService(
	sendMessageFunc: (message: any) => void,
	config?: Partial<CallConfig>
) {
	// WebRTC状态
	const state = reactive<WebRTCState>({
		localStream: null,
		remoteStream: null,
		peerConnection: null,
		callDuration: '00:00',
		callTimer: null,
		callStartTime: null,
		incomingCall: { fromUserId: '' },
		callModalVisible: false,
		callType: 'voice',
		callStatus: null,
		isCaller: false,
		peerUserType: '1',
		callRecordSent: false,
		groupCallPeers: new Map(),
		groupCallStreams: new Map(),
		groupCallParticipants: [],
		currentUserId: ''
	});

	// 音频元素引用
	const remoteAudioRef = ref<HTMLAudioElement | null>(null);

	// 合并配置
	const callConfig: CallConfig = {
		iceServers: config?.iceServers || defaultIceServers
	};

	/**
	 * 检查网络连接状态
	 */
	const checkNetworkConnection = () => {
		const connection = (navigator as any).connection || (navigator as any).mozConnection || (navigator as any).webkitConnection;
		if (connection) {
			console.log('网络连接类型:', connection.type);
			console.log('网络有效类型:', connection.effectiveType);
			console.log('下行带宽:', connection.downlink, 'Mbps');
			console.log('往返时间:', connection.rtt, 'ms');

			if (connection.downlink < 0.5 || connection.rtt > 500) {
				notification.warning({
					message: '网络连接不佳',
					description: '当前网络连接质量较差，可能影响通话质量'
				});
			}
		} else {
			console.log('无法获取网络连接信息');
		}
	};

	// ICE candidate 缓存：remoteDescription 未就绪时先存起来
	const pendingIceCandidates: any[] = [];

	// 动态创建的远端音频元素（当模板里没有 audio 节点时兜底）
	let dynamicRemoteAudioEl: HTMLAudioElement | null = null;

	/**
	 * remoteDescription 就绪后补加之前缓存的 ICE
	 */
	const applyPendingIceCandidates = () => {
		const pc = state.peerConnection;
		if (!pc || pc.signalingState === 'closed') return;
		if (!pc.remoteDescription) return;
		if (!pendingIceCandidates.length) return;

		const cached = pendingIceCandidates.splice(0, pendingIceCandidates.length);
		cached.forEach((c) => {
			try {
				pc.addIceCandidate(new RTCIceCandidate(c)).catch((e) => {
					console.warn('补加 ICE 候选失败，重新缓存:', e);
					pendingIceCandidates.push(c);
				});
			} catch (e) {
				console.warn('补加 ICE 候选异常，重新缓存:', e);
				pendingIceCandidates.push(c);
			}
		});
	};

	/**
	 * 统一 ontrack 合并：把新轨道合到 state.remoteStream 里，触发 UI/媒体元素重绑
	 */
	const mergeRemoteTracks = (track: MediaStreamTrack, eventStream?: MediaStream) => {
		const existing = state.remoteStream;

		if (!existing) {
			if (eventStream) {
				// 确保包含本次 track
				const tracks = eventStream.getTracks ? eventStream.getTracks() : [];
				if (tracks.findIndex((t) => t.id === track.id) === -1) {
					try {
						eventStream.addTrack(track);
					} catch (e) {
						// ignore
					}
				}
				return eventStream;
			}
			return new MediaStream([track]);
		}

		const existingTracks = existing.getTracks ? existing.getTracks() : [];
		const nextTracks: MediaStreamTrack[] = [];

		existingTracks.forEach((t) => {
			if (t.kind !== track.kind) {
				nextTracks.push(t);
				return;
			}
			// 同 kind：如果 id 不同，保留旧的，稍后再加新的
			nextTracks.push(t);
		});

		// 去重加入新轨道
		if (nextTracks.findIndex((t) => t.id === track.id) === -1) {
			nextTracks.push(track);
		}

		// 同 kind 的旧轨道可能已经失效：尽量只保留一个启用的
		const dedup: MediaStreamTrack[] = [];
		const seenKind = new Set<string>();
		nextTracks.forEach((t) => {
			if (!seenKind.has(t.kind)) {
				dedup.push(t);
				seenKind.add(t.kind);
			} else if (t.kind === 'audio' && t.enabled) {
				// 如果已经有 audio，再遇到 enabled 的 audio，用它替换最后一个 audio
				const idx = dedup.findIndex((x) => x.kind === 'audio');
				if (idx >= 0) dedup[idx] = t;
			} else if (t.kind === 'video' && t.enabled) {
				const idx = dedup.findIndex((x) => x.kind === 'video');
				if (idx >= 0) dedup[idx] = t;
			}
		});

		return new MediaStream(dedup);
	};

	/**
	 * 初始化通话
	 * @param userId 对方用户ID
	 */
	const initializeCall = async (userId: string) => {
		try {
			// 清理现有连接
			if (state.localStream) {
				state.localStream.getTracks().forEach(track => track.stop());
				state.localStream = null;
			}
			if (state.peerConnection) {
				state.peerConnection.close();
				state.peerConnection = null;
			}

			console.log('请求媒体设备权限...');
			try {
				state.localStream = await navigator.mediaDevices.getUserMedia({
					audio: true,
					video: state.callType === 'video'
				});
			} catch (mediaError: any) {
				console.error('获取媒体设备失败:', mediaError);
				// 视频通话失败时，回退到仅音频
				if (state.callType === 'video') {
					console.log('尝试回退到仅音频模式...');
					try {
						state.localStream = await navigator.mediaDevices.getUserMedia({ audio: true, video: false });
						notification.warning({
							message: '摄像头不可用',
							description: '无法访问摄像头，已切换到语音通话模式'
						});
						state.callType = 'voice';
					} catch (audioError: any) {
						console.error('获取音频设备也失败:', audioError);
						notification.error({
							message: '无法访问麦克风',
							description: audioError?.message || '无法建立通话，请检查麦克风权限'
						});
						return;
					}
				} else {
					notification.error({
						message: '无法访问麦克风',
						description: mediaError?.message || '请检查麦克风权限'
					});
					return;
				}
			}

			try {
				const tracks = state.localStream?.getTracks ? state.localStream.getTracks() : [];
				console.log('成功获取本地媒体流:', tracks.map((t: any) => `${t.kind}:${t.label}`));
				const audioTracks = state.localStream?.getAudioTracks ? state.localStream.getAudioTracks() : [];
				audioTracks.forEach((t: any) => {
					t.enabled = true;
				});
				if (!audioTracks.length) {
					console.warn('未获取到本地音频轨道（对端可能听不到声音）');
				}
			} catch (e) {
				// ignore
			}

			// 创建RTCPeerConnection
			state.peerConnection = new RTCPeerConnection({
				iceServers: callConfig.iceServers,
				iceTransportPolicy: 'all',
				bundlePolicy: 'max-bundle',
				rtcpMuxPolicy: 'require',
				sdpSemantics: 'unified-plan'
			});

			// 添加连接状态监听
			state.peerConnection.onconnectionstatechange = () => {
				console.log('连接状态变化:', state.peerConnection?.connectionState);
				if (state.peerConnection?.connectionState === 'connected') {
					console.log('连接成功建立!');
					state.callStatus = 'connected';
					// 仅在真正建立媒体连接后记录起始时间并启动计时，避免双方计时不一致
					if (!state.callStartTime) {
						state.callStartTime = Date.now();
					}
					if (!state.callTimer) {
						startCallTimer();
					}
				} else if (state.peerConnection?.connectionState === 'failed') {
					console.error('连接失败');
					notification.error({
						message: '连接失败',
						description: '无法建立媒体连接'
					});
					endCall();
				} else if (state.peerConnection?.connectionState === 'disconnected') {
					console.warn('连接断开');
					notification.warning({
						message: '连接已断开',
						description: '尝试重新连接中...'
					});
				}
			};

			// 添加ICE连接状态监听
			state.peerConnection.oniceconnectionstatechange = () => {
				console.log('ICE连接状态:', state.peerConnection?.iceConnectionState);
				if (state.peerConnection?.iceConnectionState === 'failed') {
					console.error('ICE连接失败');
					// 尝试ICE重启
					if (state.peerConnection?.restartIce) {
						console.log('尝试ICE重启');
						state.peerConnection.restartIce();
					}
				}
			};

			// 添加ICE收集状态监听
			state.peerConnection.onicegatheringstatechange = () => {
				console.log('ICE收集状态:', state.peerConnection?.iceGatheringState);
			};

			// 添加信令状态监听
			state.peerConnection.onsignalingstatechange = () => {
				console.log('信令状态:', state.peerConnection?.signalingState);
			};

			// 监听ICE候选
			state.peerConnection.onicecandidate = (event) => {
				if (event.candidate) {
					console.log('发送ICE候选:', event.candidate.candidate.substr(0, 50) + '...');
					const toUserId = state.incomingCall.fromUserId;
					if (!toUserId || toUserId === userId) {
						console.warn('ICE 目标用户ID异常，跳过发送:', { userId, toUserId });
						return;
					}
					sendMessageFunc({
						type: 'call_ice_candidate',
						fromUserId: userId,
						toUserId,
						candidate: event.candidate
					});
				} else {
					console.log('ICE候选收集完成');
				}
			};

			// 处理远程流
			state.peerConnection.ontrack = (event) => {
				console.log(`收到远程${event.track.kind}轨道`);
				try {
					const eventStream = event.streams && event.streams.length > 0 ? event.streams[0] : undefined;
					const merged = mergeRemoteTracks(event.track, eventStream);
					handleRemoteStream(merged);
				} catch (e) {
					console.warn('ontrack 合并失败，使用兜底流:', e);
					const fallback = (event.streams && event.streams[0]) ? event.streams[0] : new MediaStream([event.track]);
					handleRemoteStream(fallback);
				}
			};

			// 添加本地轨道
			state.localStream.getTracks().forEach(track => {
				console.log(`添加本地${track.kind}轨道`);
				state.peerConnection?.addTrack(track, state.localStream!);
			});

			// 创建offer
			try {
				console.log('创建offer...');
				const offer = await state.peerConnection.createOffer({
					offerToReceiveAudio: true,
					offerToReceiveVideo: state.callType === 'video'
				});

				console.log('设置本地描述...');
				await state.peerConnection.setLocalDescription(offer);

				// 不等待 ICE 收集：使用 trickle ICE（onicecandidate）即可，显著加速首包 offer
				console.log('发送offer到对方');
				// 保存当前用户ID用于之后的通信
				state.currentUserId = userId;

				// 重要：确保toUserId不是同一个用户
				// 用户无法和自己通话，如果toUserId和fromUserId相同，这是一个错误
				const toUserId = state.incomingCall.fromUserId;
				if (!toUserId || toUserId === userId) {
					console.error('通话目标错误：无法与自己通话', '当前用户:', userId, '目标用户:', toUserId);
					notification.error({
						message: '通话发起失败',
						description: '无法确定通话对象，请重试'
					});
					endCall();
					return;
				}

				console.log('initializeCall: 当前用户ID:', userId, '目标用户ID:', toUserId);

				// 发送offer到对方用户
				sendMessageFunc({
					type: 'call_offer',
					fromUserId: userId,
					toUserId: toUserId,
					callType: state.callType,
					sdp: state.peerConnection.localDescription
				});

				// 更新UI
				state.callModalVisible = true;
				state.callStatus = 'calling';

			} catch (offerError) {
				console.error('创建或设置offer失败:', offerError);
				notification.error({
					message: '通话初始化失败',
					description: offerError.message
				});
				endCall();
			}

		} catch (error) {
			console.error('初始化通话失败:', error);
			notification.error({
				message: '通话初始化失败',
				description: (error as Error).message
			});
			endCall();
		}
	};

	/**
	 * 接受通话
	 * @param userId 当前用户ID
	 */
	const acceptCall = async (userId: string) => {
		try {
			// 保存当前用户ID
			state.currentUserId = userId;
			console.log('接听通话: 当前用户ID:', userId);

			// 清理现有连接
			if (state.localStream) {
				state.localStream.getTracks().forEach(track => track.stop());
				state.localStream = null;
			}
			if (state.peerConnection) {
				state.peerConnection.close();
				state.peerConnection = null;
			}

			console.log('接听通话: 请求媒体权限...');
			try {
				state.localStream = await navigator.mediaDevices.getUserMedia({
					audio: true,
					video: state.callType === 'video'
				});
			} catch (mediaError: any) {
				console.error('获取媒体设备失败:', mediaError);
				if (state.callType === 'video') {
					console.log('尝试回退到仅音频模式...');
					state.localStream = await navigator.mediaDevices.getUserMedia({ audio: true, video: false });
					notification.warning({
						message: '摄像头不可用',
						description: '无法访问摄像头，已切换到语音通话模式'
					});
					state.callType = 'voice';
				} else {
					notification.error({
						message: '无法访问麦克风',
						description: mediaError?.message || '无法建立通话，请检查麦克风权限'
					});
					throw mediaError;
				}
			}

			try {
				const audioTracks = state.localStream?.getAudioTracks ? state.localStream.getAudioTracks() : [];
				audioTracks.forEach((t: any) => {
					t.enabled = true;
				});
				if (!audioTracks.length) {
					console.warn('接听通话: 未获取到本地音频轨道（对端可能听不到声音）');
				}
			} catch (e) {
				// ignore
			}

			// 创建RTCPeerConnection
			state.peerConnection = new RTCPeerConnection({
				iceServers: callConfig.iceServers
			});

			// 监听信令状态变化
			state.peerConnection.onsignalingstatechange = () => {
				console.log('接听通话: 信令状态变化:', state.peerConnection?.signalingState);
			};

			// 添加本地轨道
			state.localStream.getTracks().forEach(track => {
				console.log(`接听通话: 添加${track.kind}轨道`);
				state.peerConnection?.addTrack(track, state.localStream!);
			});

			// 处理ICE候选
			state.peerConnection.onicecandidate = (event) => {
				if (event.candidate) {
					console.log('接听通话: 发送ICE候选');
					sendMessageFunc({
						type: 'call_ice_candidate',
						fromUserId: userId,
						toUserId: state.incomingCall.fromUserId,
						candidate: event.candidate
					});
				}
			};

			// 监听ICE连接状态
			state.peerConnection.oniceconnectionstatechange = () => {
				console.log('接听通话: ICE连接状态:', state.peerConnection?.iceConnectionState);
			};

			// 监听连接状态
			state.peerConnection.onconnectionstatechange = () => {
				console.log('接听通话: 连接状态:', state.peerConnection?.connectionState);
				if (state.peerConnection?.connectionState === 'connected') {
					state.callStatus = 'connected';
					if (!state.callStartTime) {
						state.callStartTime = Date.now();
					}
					if (!state.callTimer) {
						startCallTimer();
					}
				}
			};

			// 处理远程流
			state.peerConnection.ontrack = (event) => {
				console.log(`接听通话: 收到远程${event.track.kind}轨道`);
				try {
					const eventStream = event.streams && event.streams.length > 0 ? event.streams[0] : undefined;
					const merged = mergeRemoteTracks(event.track, eventStream);
					handleRemoteStream(merged);
				} catch (e) {
					console.warn('接听通话 ontrack 合并失败，使用兜底流:', e);
					const fallback = (event.streams && event.streams[0]) ? event.streams[0] : new MediaStream([event.track]);
					handleRemoteStream(fallback);
				}
			};

			// 设置远程描述（必须在创建答复之前）
			if (state.incomingCall.sdp) {
				console.log('接听通话: 设置远程描述');
				console.log('接听通话: 收到的SDP类型:', state.incomingCall.sdp.type);

				try {
					// 先设置远程描述，这是正确的顺序
					await state.peerConnection.setRemoteDescription(new RTCSessionDescription(state.incomingCall.sdp as any));
					console.log('接听通话: 远程描述设置成功');

					// remoteDescription 就绪后补加之前缓存的 ICE
					applyPendingIceCandidates();

					// 然后创建应答
					console.log('接听通话: 创建应答');
					const answer = await state.peerConnection.createAnswer();
					console.log('接听通话: 应答创建成功');

					// 设置本地描述
					await state.peerConnection.setLocalDescription(answer);
					console.log('接听通话: 本地描述设置成功');

					// 发送应答
					console.log('接听通话: 发送应答');
					sendMessageFunc({
						type: 'call_answer',
						fromUserId: userId,
						toUserId: state.incomingCall.fromUserId,
						sdp: state.peerConnection.localDescription
					});
				} catch (sdpError) {
					console.error('接听通话: SDP处理失败:', sdpError);
					throw sdpError;
				}
			} else {
				console.error('接听通话: 没有收到远程SDP');
				throw new Error('没有收到远程SDP');
			}

			// 更新UI状态
			state.callStatus = 'connected';

		} catch (error) {
			console.error('接听通话失败:', error);
			notification.error({
				message: '接听失败',
				description: (error as Error).message
			});
			endCall();
		}
	};

	/**
	 * 拒绝通话
	 * @param userId 当前用户ID
	 */
	const rejectCall = (userId: string) => {
		sendMessageFunc({
			type: 'call_reject',
			fromUserId: userId,
			toUserId: state.incomingCall.fromUserId
		});
		endCall(true, { sendSignal: false });
	};

	/**
	 * 结束通话
	 */
	const emitCallRecordIfCaller = (status: string, durationSeconds: number) => {
		try {
			if (!state.isCaller) return;
			if (state.callRecordSent) return;

			const fromUserId = state.currentUserId || '';
			const toUserId = state.incomingCall?.fromUserId || '';
			if (!fromUserId || !toUserId) return;
			if (fromUserId === toUserId) return;

			const messageType = state.callType === 'voice' ? '5' : '6';
			const now = Date.now();
			const startTime = state.callStartTime || (now - Math.max(0, durationSeconds) * 1000);

			sendMessageFunc({
				type: messageType,
				fromUserId,
				toUserId,
				chatType: '1',
				toUserType: state.peerUserType || '1',
				fromUserType: '1',
				content: JSON.stringify({
					status: status || '通话结束',
					duration: durationSeconds || 0,
					startTime,
					endTime: now
				})
			});

			state.callRecordSent = true;
			console.log('已发送通话记录:', { fromUserId, toUserId, messageType, status, durationSeconds });
		} catch (e) {
			console.error('发送通话记录失败:', e);
		}
	};

	const endCall = (isActive = true, options?: { sendSignal?: boolean }) => {
		const sendSignal = options?.sendSignal !== false;
		// 记录当前状态
		const previousStatus = state.callStatus;
		console.log('结束通话，之前状态:', previousStatus);

		// 防止重复调用
		if (!state.callStatus) {
			console.log('通话已经结束，忽略重复调用');
			return;
		}

		// 如果是主动结束通话，显示通知
		if (isActive) {
			notification.info({
				message: '通话结束',
				description: '您已结束通话'
			});
		}

		// 主叫方统一落一条通话记录（不依赖弹窗卸载时机）
		if (state.callStatus === 'connected' || state.callStatus === 'calling') {
			const durationSeconds = state.callStartTime ? Math.floor((Date.now() - state.callStartTime) / 1000) : 0;
			if (state.callStatus === 'connected') {
				emitCallRecordIfCaller('通话结束', Math.max(0, durationSeconds));
			} else if (state.callStatus === 'calling') {
				// 呼叫中结束：主动取消或未接通
				emitCallRecordIfCaller(isActive ? '已取消' : '未接通', 0);
			}
		}

		// 如果是连接状态或正在呼叫，发送结束信号
		if (sendSignal && (state.callStatus === 'connected' || state.callStatus === 'calling')) {
			// 确定发送给谁
			let targetUserId = state.incomingCall.fromUserId;

			// 如果当前用户就是fromUserId，那么目标应该发送给其他人
			if (state.currentUserId === targetUserId) {
				console.warn('检测到fromUserId与currentUserId相同，这可能是一个错误');
				notification.warning({
					message: '通话ID异常',
					description: '通话ID配置错误，已尝试修复'
				});
				// 尝试从之前的消息中恢复正确的目标用户ID
				// 这是一个安全检查，避免自己和自己通话
				targetUserId = '';
			}

			if (targetUserId) {
				console.log('发送通话结束信号，发送方:', state.currentUserId, '接收方:', targetUserId);
				sendMessageFunc({
					type: 'call_end',
					fromUserId: state.currentUserId || '',
					toUserId: targetUserId
				});
			} else {
				console.warn('无法发送结束信号：未找到目标用户ID');
			}
		}

		// 停止本地流
		if (state.localStream) {
			state.localStream.getTracks().forEach(track => {
				track.stop();
				console.log(`停止本地${track.kind}轨道`);
			});
			state.localStream = null;
		}

		// 清理远程流
		state.remoteStream = null;

		// 清空 ICE 缓存
		pendingIceCandidates.splice(0, pendingIceCandidates.length);

		// 清理动态创建的远端音频元素
		try {
			if (dynamicRemoteAudioEl) {
				dynamicRemoteAudioEl.pause();
				// @ts-ignore
				dynamicRemoteAudioEl.srcObject = null;
				dynamicRemoteAudioEl.remove();
			}
		} catch (e) {
			// ignore
		}
		dynamicRemoteAudioEl = null;

		// 清理连接
		if (state.peerConnection) {
			state.peerConnection.onicecandidate = null;
			state.peerConnection.ontrack = null;
			state.peerConnection.oniceconnectionstatechange = null;
			state.peerConnection.onsignalingstatechange = null;
			state.peerConnection.onconnectionstatechange = null;

			state.peerConnection.close();
			state.peerConnection = null;
		}

		// 清理计时器
		if (state.callTimer) {
			clearInterval(state.callTimer);
			state.callTimer = null;
		}

		// 重置状态
		state.callStatus = null;
		state.callModalVisible = false;
		state.callDuration = '00:00';
		state.callStartTime = null;
		state.incomingCall = { fromUserId: '' };
		state.isCaller = false;
		state.peerUserType = '1';
		state.callRecordSent = false;

		console.log('通话已完全终止');
	};

	/**
	 * 处理远程媒体流
	 */
	const handleRemoteStream = (stream: MediaStream) => {
		console.log('处理远程流，轨道:', stream.getTracks().map(t => `${t.kind}:${t.enabled}`));
		state.remoteStream = stream;

		// 使用setTimeout确保DOM已完全渲染
		setTimeout(() => {
			console.log('延迟处理远程流');

			// 1) 视频通话：绑定远端视频（主窗口/小窗口都可能存在）
			if (state.callType === 'video') {
				const remoteVideo = document.getElementById('remoteVideo') as HTMLVideoElement;
				const remoteVideoSmall = document.getElementById('remoteVideoSmall') as HTMLVideoElement;
				if (remoteVideo) {
					console.log('设置远程视频流(主窗口)');
					remoteVideo.srcObject = stream;
					remoteVideo.muted = true;
					remoteVideo.play().catch(e => console.error('主窗口视频播放失败:', e));
				}
				if (remoteVideoSmall) {
					console.log('设置远程视频流(小窗口)');
					remoteVideoSmall.srcObject = stream;
					remoteVideoSmall.muted = true;
					remoteVideoSmall.play().catch(e => console.error('小窗口视频播放失败:', e));
				}
			}

			// 2) 音频：无论语音/视频通话，都将 audio track 绑定到 audio 元素
			const audioTracks = stream.getAudioTracks ? stream.getAudioTracks() : [];
			const audioStream = audioTracks && audioTracks.length > 0 ? new MediaStream(audioTracks) : stream;

			let audioEl: HTMLAudioElement | null = remoteAudioRef.value;
			if (!audioEl) {
				audioEl = document.getElementById('remoteAudio') as HTMLAudioElement;
			}
			if (!audioEl) {
				const container = document.getElementById('call-container-main') || document.body;
				dynamicRemoteAudioEl = document.createElement('audio');
				dynamicRemoteAudioEl.id = 'remoteAudio';
				dynamicRemoteAudioEl.autoplay = true;
				dynamicRemoteAudioEl.controls = false;
				dynamicRemoteAudioEl.style.display = 'none';
				dynamicRemoteAudioEl.setAttribute('playsinline', 'true');
				dynamicRemoteAudioEl.setAttribute('webkit-playsinline', 'true');
				container.appendChild(dynamicRemoteAudioEl);
				audioEl = dynamicRemoteAudioEl;
			}

			if (audioEl) {
				audioEl.srcObject = audioStream;
				audioEl.muted = false;
				audioEl.volume = 1.0;
				audioEl.play().then(() => console.log('远端音频开始播放')).catch(e => console.error('远端音频播放失败:', e));
			}
		}, 500); // 延迟500ms确保DOM已渲染
	};

	/**
	 * 开始计时器
	 */
	const startCallTimer = () => {
		if (state.callTimer) return;
		if (!state.callStartTime) {
			state.callStartTime = Date.now();
		}

		const updateDuration = () => {
			const start = state.callStartTime || Date.now();
			const elapsedSeconds = Math.max(0, Math.floor((Date.now() - start) / 1000));
			const minutes = Math.floor(elapsedSeconds / 60);
			const remainingSeconds = elapsedSeconds % 60;
			state.callDuration = `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`;
		};

		// 立即计算一次，避免等待1秒
		updateDuration();
		state.callTimer = setInterval(updateDuration, 1000);
	};

	/**
	 * 处理接收到的WebSocket消息
	 * @param message 接收到的消息
	 * @param currentUserId 当前用户ID
	 */
	const handleWebSocketMessage = (message: any, currentUserId: string) => {
		if (!message || !message.type || !message.type.startsWith('call_')) return;

		console.log('收到通话相关消息:', message.type, '当前用户:', currentUserId, '消息来源:', message.fromUserId);
		// 保存当前用户ID
		state.currentUserId = currentUserId;

		// 根据消息类型保存对话方ID
		if (message.fromUserId && message.fromUserId !== currentUserId) {
			if (message.type === 'call_offer' || message.type === 'call_answer') {
				// 如果是呼叫请求或应答，保存为呼入通话的来源用户
				state.incomingCall.fromUserId = message.fromUserId;
				console.log('保存通话对方ID:', message.fromUserId);
			}
		}

		switch (message.type) {
			case 'call_offer':
				console.log('收到通话邀请，来自:', message.fromUserId);
				state.isCaller = false;
				state.callType = message.callType;
				state.incomingCall = {
					fromUserId: message.fromUserId,
					sdp: message.sdp
				};
				console.log('保存通话邀请信息，fromUserId:', message.fromUserId);
				state.callModalVisible = true;
				state.callStatus = 'incoming';
				break;

			case 'call_answer':
				console.log('收到通话应答');
				if (state.peerConnection && state.peerConnection.signalingState !== 'closed') {
					console.log('当前信令状态:', state.peerConnection.signalingState);

					// 处理不同信令状态下的应答
					if (state.peerConnection.signalingState === 'have-local-offer') {
						// 正常情况：本地有offer，可以直接设置远程描述
						state.peerConnection.setRemoteDescription(new RTCSessionDescription(message.sdp))
							.then(() => {
								console.log('成功设置远程描述');
								state.callStatus = 'connected';
								applyPendingIceCandidates();
							})
							.catch(error => {
								console.error('设置远程描述失败:', error);
								notification.error({
									message: '连接失败',
									description: '无法建立媒体连接'
								});
								endCall();
							});
					} else if (state.peerConnection.signalingState === 'stable') {
						// 特殊情况：信令状态已经是stable，可能是由于某种原因重置了
						console.log('在stable状态收到应答，需要特殊处理');

						// 首先创建一个新的offer
						state.peerConnection.createOffer({
							offerToReceiveAudio: true,
							offerToReceiveVideo: state.callType === 'video'
						})
							.then(offer => {
								console.log('重新创建offer成功');
								return state.peerConnection?.setLocalDescription(offer);
							})
							.then(() => {
								console.log('重新设置本地描述成功，现在尝试设置收到的远程描述');
								// 延迟后尝试设置远程描述
								setTimeout(() => {
									if (state.peerConnection && state.peerConnection.signalingState === 'have-local-offer') {
										state.peerConnection.setRemoteDescription(new RTCSessionDescription(message.sdp))
											.then(() => {
												console.log('延迟后成功设置远程描述');
												state.callStatus = 'connected';
											})
											.catch(error => {
												console.error('延迟后设置远程描述仍然失败:', error);
												notification.error({
													message: '连接失败',
													description: '无法建立媒体连接'
												});
												endCall();
											});
									} else {
										console.error('延迟后信令状态仍然不正确:', state.peerConnection?.signalingState);
										endCall();
									}
								}, 500);
							})
							.catch(error => {
								console.error('重新创建offer失败:', error);
								endCall();
							});
					} else {
						console.warn('收到应答时连接状态错误:', state.peerConnection.signalingState);
						notification.warning({
							message: '连接状态错误',
							description: '通话连接状态不正确，无法建立通话'
						});
						// 尝试重置连接
						state.currentUserId = currentUserId; // 存储当前用户ID
						endCall();
					}
				} else {
					console.warn('收到应答时没有有效的连接');
				}
				break;

			case 'call_ice_candidate':
				console.log('收到ICE候选');
				if (state.peerConnection && state.peerConnection.signalingState !== 'closed') {
					try {
						// remoteDescription 未就绪时先缓存，避免丢 ICE
						if (!state.peerConnection.remoteDescription) {
							pendingIceCandidates.push(message.candidate);
							return;
						}
						state.peerConnection.addIceCandidate(new RTCIceCandidate(message.candidate))
							.then(() => console.log('成功添加ICE候选'))
							.catch(e => {
								console.error('添加ICE候选失败:', e);
								pendingIceCandidates.push(message.candidate);
							});
					} catch (e) {
						console.error('处理ICE候选异常:', e);
						pendingIceCandidates.push(message.candidate);
					}
				}
				break;

			case 'call_reject':
				console.log('通话被拒绝');
				notification.info({
					message: '通话被拒绝',
					description: '对方拒绝了您的通话请求'
				});
				// 收到拒绝时不要回发 call_end
				endCall(false, { sendSignal: false });
				break;

			case 'call_end':
				console.log('通话被结束，当前状态:', state.callStatus);
				if(state.callStatus){
					notification.info({
						message: '通话结束',
						description: '对方结束了通话'
					});
				}
				// 无论当前状态如何，都强制结束通话
				if (state.callStatus) {
					console.log('强制结束通话');
					// 收到对端结束时不要回发 call_end（避免回环）
					endCall(false, { sendSignal: false });
				}
				break;
		}
	};

	/**
	 * 开始视频通话
	 * @param userId 当前用户ID
	 * @param targetUserId 对方用户ID
	 */
	const startVideoCall = (userId: string, targetUserId: string, targetUserType: string = '1') => {
		state.callModalVisible = true;
		if (!targetUserId || targetUserId === userId) {
			console.error('无法与自己通话');
			notification.error({
				message: '通话发起失败',
				description: '无法与自己通话'
			});
			return;
		}

		// 保存目标用户ID到incomingCall，这样initializeCall可以使用它
		state.incomingCall = { fromUserId: targetUserId };
		state.isCaller = true;
		state.peerUserType = targetUserType || '1';
		state.callRecordSent = false;
		console.log('准备视频通话，目标用户:', targetUserId);

		checkNetworkConnection();
		state.callType = 'video';
		initializeCall(userId);
	};

	/**
	 * 开始语音通话
	 * @param userId 当前用户ID
	 * @param targetUserId 对方用户ID
	 */
	const startVoiceCall = (userId: string, targetUserId: string, targetUserType: string = '1') => {
		state.callModalVisible = true;
		if (!targetUserId || targetUserId === userId) {
			console.error('无法与自己通话');
			notification.error({
				message: '通话发起失败',
				description: '无法与自己通话'
			});
			return;
		}

		// 保存目标用户ID到incomingCall，这样initializeCall可以使用它
		state.incomingCall = { fromUserId: targetUserId };
		state.isCaller = true;
		state.peerUserType = targetUserType || '1';
		state.callRecordSent = false;
		console.log('准备语音通话，目标用户:', targetUserId);

		checkNetworkConnection();
		state.callType = 'voice';
		initializeCall(userId);
	};

	return {
		state,
		remoteAudioRef,
		startVideoCall,
		startVoiceCall,
		acceptCall,
		rejectCall,
		endCall,
		handleWebSocketMessage
	};
} 
