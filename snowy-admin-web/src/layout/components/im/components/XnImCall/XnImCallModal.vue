<template>
  <a-modal
    v-model:open="callState.callModalVisible"
    :title="callState.callType === 'voice' ? '语音通话' : '视频通话'"
    :closable="false"
    :mask-closable="false"
    :footer="null"
    width="400px"
  >
    <div class="call-container">
      <div id="call-container-main">
				<div v-if="!callState.callStatus">
					<p>正在初始化通道...</p>
				</div>
        <!-- 呼叫状态 -->
        <div v-if="callState.callStatus === 'calling'" class="calling-status">
          <a-avatar :size="64" :src="targetUser.avatar" />
          <p>正在呼叫 {{ targetUser.name }}...</p>
          <a-space>
            <a-button type="primary" danger @click="onEndCall">取消</a-button>
          </a-space>
        </div>
        
        <!-- 来电状态 -->
        <div v-if="callState.callStatus === 'incoming'" class="incoming-status">
          <a-avatar :size="64" :src="callerAvatar" />
          <p>来自 {{ callerName }} 的{{ callState.callType === 'voice' ? '语音' : '视频' }}通话</p>
          <a-space>
            <a-button type="primary" @click="onAcceptCall">接听</a-button>
            <a-button type="primary" danger @click="onRejectCall">拒绝</a-button>
          </a-space>
        </div>

        <!-- 通话中状态 -->
        <div v-if="callState.callStatus === 'connected'" class="connected-status">
          <!-- 群组视频网格 -->
          <div class="group-video-grid" v-if="isGroupCall">
            <div v-for="[userId, stream] in callState.groupCallStreams" :key="userId" class="video-item">
              <video :srcObject="stream" autoplay class="remote-video"></video>
              <div class="user-name">{{ getUserName(userId) }}</div>
            </div>
            <div class="video-item">
              <video :srcObject="callState.localStream" autoplay muted class="local-video"></video>
              <div class="user-name">我</div>
            </div>
          </div>
          
          <!-- 一对一视频通话界面 -->
          <div v-if="callState.callType === 'video' && !isGroupCall" class="call-content">
            <!-- 主窗口 -->
            <video
              v-if="!isLocalMain"
              id="remoteVideo"
              class="main-video"
              autoplay
              playsinline
              @click="switchVideo('remote')"
              ref="mainVideoRef"
            ></video>
            <video
              v-else
              :srcObject="callState.localStream"
              class="main-video"
              autoplay
              playsinline
              muted
              @click="switchVideo('local')"
              ref="mainVideoRef"
            ></video>
            <!-- 小窗口 -->
            <video
              v-if="isLocalMain"
              id="remoteVideoSmall"
              class="small-video"
              autoplay
              playsinline
              @click="switchVideo('remote')"
              :style="smallVideoStyle"
              ref="smallVideoRef"
            ></video>
            <video
              v-else
              :srcObject="callState.localStream"
              class="small-video"
              autoplay
              playsinline
              muted
              @click="switchVideo('local')"
              :style="smallVideoStyle"
              ref="smallVideoRef"
            ></video>
          </div>
          
          <!-- 语音通话界面 -->
          <audio v-if="callState.callType === 'voice'" id="remoteAudio" ref="remoteAudioRef" autoplay playsinline style="width:100%; display:block; margin:10px 0;"></audio>
          
          <p>通话时长: {{ callState.callDuration }}</p>
          <a-button type="primary" danger @click="onEndCall">结束通话</a-button>
        </div>
      </div>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { defineProps, defineEmits, ref, watch, computed, onMounted, onUnmounted, nextTick } from 'vue';
import { WebRTCState } from './types';

const props = defineProps({
  callState: {
    type: Object,
    required: true
  },
  currentUser: {
    type: Object,
    required: true
  },
  targetUser: {
    type: Object,
    required: true
  },
  usersMap: {
    type: Object,
    required: true
  },
  isGroupCall: {
    type: Boolean,
    default: false
  },
  sendMessageFunc: {
    type: Function,
    required: true
  }
});

const emit = defineEmits(['accept-call', 'reject-call', 'end-call']);

// 通话开始时间
const callStartTime = ref<number>(0);
// 通话超时定时器
const callTimeoutTimer = ref(null);

// 语音通话 audio 元素模板 ref（供父组件/服务层绑定使用）
const remoteAudioRef = ref<HTMLAudioElement | null>(null);

const callMessageSent = ref(false);

// 发送通话消息
const sendCallMessage = (status: string, duration?: number) => {
  try {
    if (callMessageSent.value) return;
    if (props.callState?.callRecordSent) return;
    // 只让主叫方发送通话记录，避免方向反转/重复记录
    if (!props.callState?.isCaller) return;

    console.log("准备发送通话消息:", { 
      targetUser: props.targetUser, 
      currentUser: props.currentUser,
      callStatus: props.callState.callStatus,
      callType: props.callState.callType,
      incomingCall: props.callState.incomingCall
    });
    
    const peerUserId = props.callState?.incomingCall?.fromUserId || props.targetUser?.id;
    const fromUserId = props.currentUser?.id;
    const toUserId = peerUserId;
    if (!fromUserId || !toUserId) return;

    const messageType = props.callState.callType === 'voice' ? '5' : '6';
    const message = {
			// 与普通聊天消息保持一致：type/content 均为字符串
      type: messageType,
      fromUserId,
      toUserId,
      chatType: props.isGroupCall ? '2' : '1',
      toUserType: props.callState?.peerUserType || '1',
			fromUserType: '1',
			content: JSON.stringify({
				status,
				duration: duration || 0,
				startTime: callStartTime.value,
				endTime: Date.now()
			})
    };
		props.sendMessageFunc(message);
		callMessageSent.value = true;
		console.log("发送消息成功",message)
  } catch (error) {
    console.error('发送通话消息失败:', error);
  }
};

// 开始通话计时
const startCallTimer = (startAt?: number) => {
  callStartTime.value = startAt || Date.now();
};

// 处理通话超时
const handleCallTimeout = () => {
  if (props.callState.callStatus === 'calling') {
    sendCallMessage('未接通');
    emit('end-call', true);
  }
};

// 监听通话状态变化
watch(() => props.callState.callStatus, (newStatus, oldStatus) => {
  if (newStatus === 'calling') {
    // 开始30秒超时计时
    callTimeoutTimer.value = setTimeout(handleCallTimeout, 30000);
  } else if (newStatus === 'connected') {
    // 清除超时计时器
    if (callTimeoutTimer.value) {
      clearTimeout(callTimeoutTimer.value);
      callTimeoutTimer.value = null;
    }
    // 使用通话服务中记录的起始时间，确保双方时间一致
    startCallTimer(props.callState.callStartTime || undefined);
  }
}, { immediate: true });

// 计算属性：获取来电者姓名
const callerName = computed(() => {
  if (!props.callState.incomingCall.fromUserId) return '未知';
  const caller = props.usersMap[props.callState.incomingCall.fromUserId];
  return caller ? caller.name : '未知';
});

// 计算属性：获取来电者头像
const callerAvatar = computed(() => {
  if (!props.callState.incomingCall.fromUserId) return '';
  const caller = props.usersMap[props.callState.incomingCall.fromUserId];
  return caller ? caller.avatar : '';
});

// 根据用户ID获取用户名
const getUserName = (userId: string) => {
  return props.usersMap[userId]?.name || '未知用户';
};

// 接听通话
const onAcceptCall = () => {
  emit('accept-call');
};

// 拒绝通话
const onRejectCall = () => {
  if (props.callState.callStatus === 'incoming') {
    sendCallMessage('对方已拒绝');
  }
  emit('reject-call');
};

// 结束通话
const onEndCall = () => {
  const duration = callStartTime.value ? Math.floor((Date.now() - callStartTime.value) / 1000) : 0;
  let status = '通话结束';
  
  // 根据不同的通话状态设置不同的状态信息
  if (props.callState.callStatus === 'calling') {
    // 如果是呼叫方主动取消
    status = '已取消';
  } else if (props.callState.callStatus === 'incoming') {
    // 如果是被呼叫方拒绝
    status = '对方已拒绝';
  }
  
	console.log("结束通话",status,duration)
  sendCallMessage(status, duration);
  emit('end-call', true);
};

// 视频窗口状态
const isLocalMain = ref(false);
const mainVideoRef = ref(null);
const smallVideoRef = ref(null);
const smallVideoPos = ref({ left: 20, top: 20 });
let isDragging = false;
let startX = 0;
let startY = 0;
let initialLeft = 0;
let initialTop = 0;

const smallVideoStyle = computed(() => ({
  left: smallVideoPos.value.left + 'px',
  top: smallVideoPos.value.top + 'px',
}));

const bindRemoteVideos = async () => {
  if (props.isGroupCall) return;
  if (props.callState.callType !== 'video') return;
  if (props.callState.callStatus !== 'connected') return;

  await nextTick();
  const stream = props.callState.remoteStream as MediaStream | null;
  if (!stream) return;

  const mainRemote = document.getElementById('remoteVideo') as HTMLVideoElement;
  const smallRemote = document.getElementById('remoteVideoSmall') as HTMLVideoElement;

  if (mainRemote) {
    mainRemote.srcObject = stream;
    // 音频交给独立 audio 元素播放（由 service 动态创建/绑定）
    mainRemote.muted = true;
    mainRemote.play().catch((e) => console.error('主窗口远端视频播放失败:', e));
  }
  if (smallRemote) {
    smallRemote.srcObject = stream;
    smallRemote.muted = true;
    smallRemote.play().catch((e) => console.error('小窗口远端视频播放失败:', e));
  }
};

// 监听远程视频流变化
watch(
  () => props.callState.remoteStream,
  () => {
    bindRemoteVideos();
  },
  { immediate: true }
);

// 远端视频元素会随 isLocalMain 切换而重建，需在切换/连通时重绑一次
watch(
  [isLocalMain, () => props.callState.callStatus, () => props.callState.callType],
  () => {
    bindRemoteVideos();
  }
);

const switchVideo = (type: 'local' | 'remote') => {
  isLocalMain.value = type === 'local';
  bindRemoteVideos();
};

// 同步服务端记录的通话开始时间，确保双方显示一致
watch(() => props.callState.callStartTime, (startAt) => {
  if (startAt) {
    callStartTime.value = startAt;
  }
});

const handleMouseDown = (e: MouseEvent) => {
  isDragging = true;
  startX = e.clientX;
  startY = e.clientY;
  initialLeft = smallVideoPos.value.left;
  initialTop = smallVideoPos.value.top;
  document.addEventListener('mousemove', handleMouseMove);
  document.addEventListener('mouseup', handleMouseUp);
};
const handleMouseMove = (e: MouseEvent) => {
  if (!isDragging) return;
  const deltaX = e.clientX - startX;
  const deltaY = e.clientY - startY;
  let newLeft = initialLeft + deltaX;
  let newTop = initialTop + deltaY;
  // 限制在父容器内
  const parent = document.querySelector('.call-content');
  const video = document.querySelector('.small-video') as HTMLElement;
  if (parent && video) {
    const parentRect = parent.getBoundingClientRect();
    const videoRect = video.getBoundingClientRect();
    newLeft = Math.max(0, Math.min(newLeft, parentRect.width - videoRect.width));
    newTop = Math.max(0, Math.min(newTop, parentRect.height - videoRect.height));
  }
  smallVideoPos.value = { left: newLeft, top: newTop };
};
const handleMouseUp = () => {
  isDragging = false;
  document.removeEventListener('mousemove', handleMouseMove);
  document.removeEventListener('mouseup', handleMouseUp);
};

onMounted(() => {
  // 监听小窗口拖动
  setTimeout(() => {
    const video = document.querySelector('.small-video');
    if (video) {
      video.addEventListener('mousedown', handleMouseDown);
    }
  }, 100);
});
onUnmounted(() => {
  const video = document.querySelector('.small-video');
  if (video) {
    video.removeEventListener('mousedown', handleMouseDown);
  }
  document.removeEventListener('mousemove', handleMouseMove);
  document.removeEventListener('mouseup', handleMouseUp);
  if (callTimeoutTimer.value) {
    clearTimeout(callTimeoutTimer.value);
  }
  
  // 兜底：仅主叫方、且未发送过时，离开组件再补发一次
  if (!callMessageSent.value) {
    const duration = callStartTime.value ? Math.floor((Date.now() - callStartTime.value) / 1000) : 0;
    if (props.callState.callStatus === 'connected') {
      sendCallMessage('通话结束', duration);
    } else if (props.callState.callStatus === 'calling') {
      sendCallMessage('已取消', 0);
    }
  }
});

// 提供Ref给父组件
defineExpose({
  remoteAudioRef
});
</script>

<style lang="less" scoped>
.call-container {
  text-align: center;
  padding: 20px;
  
  .calling-status,
  .incoming-status,
  .connected-status {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 20px;
  }
  
  .call-content {
    position: relative;
    width: 100%;
    height: 400px;
    background: #000;
    margin-bottom: 20px;
    overflow: hidden;
  }
  
  .local-video {
    width: 160px;
    height: 120px;
    position: absolute;
    right: 20px;
    bottom: 20px;
    border-radius: 8px;
    object-fit: cover;
    cursor: move;
    z-index: 2;
    transition: all 0.3s ease;
    
    &.main-video {
      width: 100%;
      height: 100%;
      right: 0;
      bottom: 0;
      z-index: 1;
    }
  }
  
  .remote-video {
    width: 100%;
    height: 100%;
    border-radius: 8px;
    object-fit: cover;
    transition: all 0.3s ease;
    cursor: pointer;
    
    &.main-video {
      width: 160px;
      height: 120px;
      position: absolute;
      right: 20px;
      bottom: 20px;
      z-index: 2;
    }
  }
  
  .group-video-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 10px;
    width: 100%;
    padding: 10px;
  }
  
  .video-item {
    position: relative;
    aspect-ratio: 16/9;
    background: #000;
    border-radius: 8px;
    overflow: hidden;
    
    .user-name {
      position: absolute;
      bottom: 10px;
      left: 10px;
      color: white;
      background: rgba(0, 0, 0, 0.5);
      padding: 2px 8px;
      border-radius: 4px;
    }
  }
  
  .main-video {
    width: 100%;
    height: 100%;
    border-radius: 8px;
    object-fit: cover;
    transition: all 0.3s ease;
    cursor: pointer;
    z-index: 1;
    position: absolute;
    left: 0;
    top: 0;
  }
  .small-video {
    width: 160px;
    height: 120px;
    border-radius: 8px;
    object-fit: cover;
    position: absolute;
    z-index: 2;
    cursor: move;
    box-shadow: 0 2px 8px rgba(0,0,0,0.3);
    transition: box-shadow 0.2s;
  }
}
</style>