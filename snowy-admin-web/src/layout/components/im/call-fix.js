// 这是修复后的通话函数
// 将这些代码复制到 index.vue 中替换现有函数

// 发起语音通话
const startVoiceCall = () => {
  callService.startVoiceCall(currentUser.id, chatUser.id);
};

// 发起视频通话
const startVideoCall = () => {
  callService.startVideoCall(currentUser.id, chatUser.id);
}; 