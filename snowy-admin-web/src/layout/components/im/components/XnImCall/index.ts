import XnImCallModal from './XnImCallModal.vue';
import { useCallService } from './callService';
import type { CallType, CallStatus, WebRTCState, IncomingCall, CallConfig, CallMessage } from './types';

export {
  XnImCallModal,
  useCallService,
  // 类型导出
  type CallType,
  type CallStatus,
  type WebRTCState,
  type IncomingCall,
  type CallConfig,
  type CallMessage
};

export default XnImCallModal; 