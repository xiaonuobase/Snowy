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
  callTimer: NodeJS.Timer | null;
  incomingCall: IncomingCall;
  callModalVisible: boolean;
  callType: CallType;
  callStatus: CallStatus;
  groupCallPeers: Map<string, RTCPeerConnection>;
  groupCallStreams: Map<string, MediaStream>;
  groupCallParticipants: string[];
  currentUserId?: string;
} 