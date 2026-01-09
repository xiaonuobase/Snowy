// 音视频通话相关类型定义

// 通话类型
export type CallType = 'voice' | 'video';

// 通话状态
export type CallStatus = 'calling' | 'incoming' | 'connected' | null;

// 通话消息类型
export interface CallMessage {
	type: string;
	fromUserId: string;
	toUserId: string;
	callType?: CallType;
	sdp?: RTCSessionDescription;
	candidate?: RTCIceCandidate;
	groupId?: string;
}

// 通话配置
export interface CallConfig {
	iceServers: RTCIceServer[];
}

// 入站通话信息
export interface IncomingCall {
	fromUserId: string;
	sdp?: RTCSessionDescription;
	groupId?: string;
	type?: string;
}

// WebRTC状态
export interface WebRTCState {
	localStream: MediaStream | null;
	remoteStream: MediaStream | null;
	peerConnection: RTCPeerConnection | null;
	callDuration: string;
	callTimer: ReturnType<typeof setInterval> | null;
	callStartTime?: number | null;
	incomingCall: IncomingCall;
	callModalVisible: boolean;
	callType: CallType;
	callStatus: CallStatus;
	// 是否由当前用户发起（用于只让主叫方发送通话记录）
	isCaller?: boolean;
	// 目标用户端类型（用于消息路由：1=B端 2=C端）
	peerUserType?: string;
	// 通话记录是否已发送（避免重复落记录）
	callRecordSent?: boolean;
	groupCallPeers: Map<string, RTCPeerConnection>;
	groupCallStreams: Map<string, MediaStream>;
	groupCallParticipants: string[];
	currentUserId?: string;
}
