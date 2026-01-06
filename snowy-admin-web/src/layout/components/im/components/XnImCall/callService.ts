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
    const connection = navigator.connection || (navigator as any).mozConnection || (navigator as any).webkitConnection;
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
      
      // 使用简单的媒体约束
      const constraints = {
        audio: true,
        video: state.callType === 'video'
      };
      
      console.log('请求媒体设备权限...');
      try {
        // 先检查设备是否可用
        const devices = await navigator.mediaDevices.enumerateDevices();
        const audioDevices = devices.filter(device => device.kind === 'audioinput');
        console.log('可用的音频设备:', audioDevices);

        if (audioDevices.length === 0) {
          throw new Error('未检测到麦克风设备');
        }

        // 如果是语音通话，只请求麦克风
        if (state.callType === 'voice') {
          state.localStream = await navigator.mediaDevices.getUserMedia({
            audio: {
              deviceId: audioDevices[0].deviceId ? { exact: audioDevices[0].deviceId } : undefined
            },
            video: false
          });
        } else {
          // 视频通话才请求摄像头
          const videoDevices = devices.filter(device => device.kind === 'videoinput');
          console.log('可用的视频设备:', videoDevices);
          
          if (videoDevices.length === 0) {
            throw new Error('未检测到摄像头设备');
          }

          state.localStream = await navigator.mediaDevices.getUserMedia({
            audio: {
              deviceId: audioDevices[0].deviceId ? { exact: audioDevices[0].deviceId } : undefined
            },
            video: {
              deviceId: videoDevices[0].deviceId ? { exact: videoDevices[0].deviceId } : undefined
            }
          });
        }
        console.log('成功获取本地媒体流:', state.localStream.getTracks().map(t => `${t.kind}:${t.label}`));
      } catch (mediaError) {
        console.error('获取媒体设备失败:', mediaError);
        
        // 如果是视频通话失败且错误包含视频相关信息，尝试回退到仅音频
        if (state.callType === 'video' && 
            (mediaError.name === 'NotFoundError' || 
             mediaError.message.toLowerCase().includes('video') || 
             mediaError.message.toLowerCase().includes('camera'))) {
          console.log('尝试回退到仅音频模式...');
          try {
            state.localStream = await navigator.mediaDevices.getUserMedia({
              audio: {
                deviceId: audioDevices[0].deviceId ? { exact: audioDevices[0].deviceId } : undefined
              },
              video: false
            });
            console.log('成功获取音频流:', state.localStream.getTracks().map(t => `${t.kind}:${t.label}`));
            // 视频通话失败但成功回退到音频，发出通知
            notification.warning({
              message: '摄像头不可用',
              description: '无法访问摄像头，已切换到语音通话模式'
            });
          } catch (audioError) {
            console.error('获取音频设备也失败:', audioError);
            notification.error({
              message: '无法访问麦克风',
              description: '无法建立通话，请检查麦克风权限'
            });
            return;
          }
        } else {
          notification.error({
            message: '无法访问' + (state.callType === 'video' ? '麦克风或摄像头' : '麦克风'),
            description: mediaError.message
          });
          return;
        }
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
          sendMessageFunc({
            type: 'call_ice_candidate',
            fromUserId: userId,
            toUserId: state.incomingCall.fromUserId || userId,
            candidate: event.candidate
          });
        } else {
          console.log('ICE候选收集完成');
        }
      };
      
      // 处理远程流
      state.peerConnection.ontrack = (event) => {
        console.log(`收到远程${event.track.kind}轨道`);
        // 确保只在track是有效的情况下处理
        if (event.streams && event.streams.length > 0) {
          handleRemoteStream(event.streams[0]);
        } else {
          console.warn('收到轨道但没有关联流');
          // 如果没有流，创建一个新流并添加轨道
          const newStream = new MediaStream([event.track]);
          handleRemoteStream(newStream);
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
        
        // 等待ICE收集完成或超时
        await new Promise((resolve) => {
          const checkState = () => {
            if (state.peerConnection?.iceGatheringState === 'complete') {
              console.log('ICE收集完成，发送offer');
              resolve(undefined);
            } else {
              setTimeout(checkState, 500);
            }
          };
          
          // 设置超时
          const timeout = setTimeout(() => {
            console.log('ICE收集超时，发送当前offer');
            resolve(undefined);
          }, 5000);
          
          // 检查状态
          checkState();
        });
        
        // 发送offer
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
      
      // 获取本地媒体流
      const constraints = {
        audio: true,
        video: state.callType === 'video'
      };
      
      console.log('接听通话: 请求媒体权限...');
      try {
        // 先检查设备是否可用
        const devices = await navigator.mediaDevices.enumerateDevices();
        const audioDevices = devices.filter(device => device.kind === 'audioinput');
        console.log('可用的音频设备:', audioDevices);

        if (audioDevices.length === 0) {
          throw new Error('未检测到麦克风设备');
        }

        // 如果是语音通话，只请求麦克风
        if (state.callType === 'voice') {
          state.localStream = await navigator.mediaDevices.getUserMedia({
            audio: {
              deviceId: audioDevices[0].deviceId ? { exact: audioDevices[0].deviceId } : undefined
            },
            video: false
          });
        } else {
          // 视频通话才请求摄像头
          const videoDevices = devices.filter(device => device.kind === 'videoinput');
          console.log('可用的视频设备:', videoDevices);
          
          if (videoDevices.length === 0) {
            throw new Error('未检测到摄像头设备');
          }

          state.localStream = await navigator.mediaDevices.getUserMedia({
            audio: {
              deviceId: audioDevices[0].deviceId ? { exact: audioDevices[0].deviceId } : undefined
            },
            video: {
              deviceId: videoDevices[0].deviceId ? { exact: videoDevices[0].deviceId } : undefined
            }
          });
        }
        console.log('接听通话: 已获取本地媒体流');
      } catch (mediaError) {
        console.error('获取媒体设备失败:', mediaError);
        
        // 如果是视频通话失败且错误包含视频相关信息，尝试回退到仅音频
        if (state.callType === 'video' && 
            (mediaError.name === 'NotFoundError' || 
             mediaError.message.toLowerCase().includes('video') || 
             mediaError.message.toLowerCase().includes('camera'))) {
          console.log('尝试回退到仅音频模式...');
          try {
            state.localStream = await navigator.mediaDevices.getUserMedia({
              audio: {
                deviceId: audioDevices[0].deviceId ? { exact: audioDevices[0].deviceId } : undefined
              },
              video: false
            });
            console.log('成功获取音频流，继续通话');
            // 视频通话失败但成功回退到音频，发出通知
            notification.warning({
              message: '摄像头不可用',
              description: '无法访问摄像头，已切换到语音通话模式'
            });
          } catch (audioError) {
            console.error('获取音频设备也失败:', audioError);
            notification.error({
              message: '无法访问麦克风',
              description: '无法建立通话，请检查麦克风权限'
            });
            throw audioError;
          }
        } else {
          notification.error({
            message: '无法访问' + (state.callType === 'video' ? '麦克风或摄像头' : '麦克风'),
            description: mediaError.message
          });
          throw mediaError;
        }
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
        handleRemoteStream(event.streams[0]);
      };
      
      // 设置远程描述（必须在创建答复之前）
      if (state.incomingCall.sdp) {
        console.log('接听通话: 设置远程描述');
        console.log('接听通话: 收到的SDP类型:', state.incomingCall.sdp.type);
        
        try {
          // 先设置远程描述，这是正确的顺序
          await state.peerConnection.setRemoteDescription(new RTCSessionDescription(state.incomingCall.sdp as any));
          console.log('接听通话: 远程描述设置成功');
          
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
    endCall();
  };

  /**
   * 结束通话
   */
  const endCall = (isActive = true) => {
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
    
    // 如果是连接状态或正在呼叫，发送结束信号
    if (state.callStatus === 'connected' || state.callStatus === 'calling') {
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
        return;
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
      
      if (state.callType === 'video') {
        const remoteVideo = document.getElementById('remoteVideo') as HTMLVideoElement;
        if (remoteVideo) {
          console.log('设置远程视频流');
          remoteVideo.srcObject = stream;
          remoteVideo.muted = false;
          remoteVideo.volume = 1.0;
          remoteVideo.play().catch(e => console.error('视频播放失败:', e));
        }
      } else {
        // 首先尝试使用ref
        if (remoteAudioRef.value) {
          console.log('使用ref设置远程音频流');
          remoteAudioRef.value.srcObject = stream;
          remoteAudioRef.value.muted = false;
          remoteAudioRef.value.volume = 1.0;
          
          remoteAudioRef.value.play()
            .then(() => console.log('音频开始播放'))
            .catch(e => console.error('音频播放失败:', e));
        } 
        // 然后尝试使用ID
        else {
          console.log('ref不可用，尝试使用ID查找');
          const audioElement = document.getElementById('remoteAudio') as HTMLAudioElement;
          
          if (audioElement) {
            console.log('使用ID找到音频元素');
            audioElement.srcObject = stream;
            audioElement.muted = false;
            audioElement.volume = 1.0;
            
            audioElement.play()
              .then(() => console.log('通过ID找到的音频元素开始播放'))
              .catch(e => console.error('通过ID找到的音频元素播放失败:', e));
          } 
          // 最后尝试创建新元素
          else {
            console.error('无法通过ref或ID找到音频元素，尝试直接创建');
            
            // 查找容器元素
            const container = document.getElementById('call-container-main');
            
            if (container) {
              console.log('找到容器元素，创建新音频元素');
              const newAudio = document.createElement('audio');
              newAudio.id = 'dynamic-audio';
              newAudio.autoplay = true;
              newAudio.controls = true;
              newAudio.style.width = '100%';
              newAudio.style.display = 'block';
              newAudio.style.margin = '10px 0';
              newAudio.srcObject = stream;
              
              // 插入到容器的开头
              container.insertBefore(newAudio, container.firstChild);
              
              newAudio.play()
                .then(() => console.log('动态创建的音频元素开始播放'))
                .catch(e => console.error('动态创建的音频元素播放失败:', e));
            } else {
              console.error('找不到任何容器元素来添加音频，尝试添加到body');
              // 最后的尝试：添加到body
              const newAudio = document.createElement('audio');
              newAudio.id = 'emergency-audio';
              newAudio.autoplay = true;
              newAudio.controls = true;
              newAudio.style.width = '300px';
              newAudio.style.position = 'fixed';
              newAudio.style.top = '10px';
              newAudio.style.left = '10px';
              newAudio.style.zIndex = '9999';
              newAudio.srcObject = stream;
              
              document.body.appendChild(newAudio);
              
              newAudio.play()
                .then(() => console.log('添加到body的音频元素开始播放'))
                .catch(e => console.error('添加到body的音频元素播放失败:', e));
            }
          }
        }
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
            state.peerConnection.addIceCandidate(new RTCIceCandidate(message.candidate))
              .then(() => console.log('成功添加ICE候选'))
              .catch(e => console.error('添加ICE候选失败:', e));
          } catch (e) {
            console.error('处理ICE候选异常:', e);
          }
        }
        break;
        
      case 'call_reject':
        console.log('通话被拒绝');
        notification.info({
          message: '通话被拒绝',
          description: '对方拒绝了您的通话请求'
        });
        endCall();
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
          endCall(false); // 传入false表示不是主动结束
        }
        break;
    }
  };

  /**
   * 开始视频通话
   * @param userId 当前用户ID
   * @param targetUserId 对方用户ID
   */
  const startVideoCall = (userId: string, targetUserId: string) => {
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
  const startVoiceCall = (userId: string, targetUserId: string) => {
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