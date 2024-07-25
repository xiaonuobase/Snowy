<template>
	<div class="im panel-item" @click="handleOpen">
		<MessageOutlined />
		<a-modal v-model:open="open" title="聊天"
			:style="{ width: 'auto', height: 'auto', maxWidth: '60vw', maxHeight: '90vh' }"
			:body-style="{ padding: 0, height: 'calc(100vh - 300px)', overflow: 'hidden' }">
			<div class="chat-container">
				<div class="user-list">
					<a-input placeholder="搜索">
						<template #prefix>
							<SearchOutlined />
						</template>
					</a-input>
					<a-tabs v-model:activeKey="activeKey" centered @change="changeTabs">
						<a-tab-pane key="1" tab="聊天">
							<a-list class="webkit-scrollbar" :data-source="ImMessageUserVoList" :item-layout="'horizontal'"
								@scroll="scrolling">
								<template #renderItem="{ item }">
									<a-list-item @click="selectMessageUser(usersMap[item.userId + ''])" class="listItem">
										<a-list-item-meta>
											<template #title>
												<span>{{ usersMap[item.userId + ''].name }}</span> <a-tag color="blue">{{ (userClient ===
													'1') ? 'B' : 'C' }}</a-tag>
											</template>
											<template #description>
												<div class="text-long">
													<span>{{ item.content }}</span>
												</div>
											</template>
											<template #avatar>
												<a-badge :count="item.unreadCount">
												<!-- <img :src="usersMap[item.userId + ''].avatar" class="avatar" /> -->
												<a-avatar :src="usersMap[item.userId + ''].avatar"  shape="shape" />
											</a-badge>
											</template>
										</a-list-item-meta>
									</a-list-item>
								</template>
							</a-list>
						</a-tab-pane>
						<a-tab-pane key="2" tab="用户">
							<div v-if="true" class="space-around" style="padding-bottom: 10px;">
								<a-button :type="userClient == '1' ? 'primary' : 'dashed'" @click="switchClient('1')">B 端用户</a-button>
								<a-button :type="userClient == '2' ? 'primary' : 'dashed'" @click="switchClient('2')" disabled>C
									端用户</a-button>
							</div>
							<a-list :data-source="users[userClient]" :item-layout="'horizontal'" class="webkit-scrollbar-2"
								@scroll="scrolling">
								<template #renderItem="{ item }">
									<a-list-item @click="selectMessageUser(item)" class="listItem">
										<a-list-item-meta>
											<template #title>
												<span>{{ item.name }}</span> <a-tag color="blue">{{ (userClient === '1') ? 'B' : 'C' }}</a-tag>
											</template>
											<template #description>
												<span>{{ item.account }}</span>
											</template>
											<template #avatar>
												<img :src="item.avatar" class="avatar" />
											</template>
										</a-list-item-meta>
									</a-list-item>
								</template>
							</a-list>
						</a-tab-pane>
						<a-tab-pane key="3" tab="群组" class="webkit-scrollbar">

						</a-tab-pane>
					</a-tabs>
				</div>
				<div class="chat-content">
					<div class="current-user">
						<div v-if="chatUser.id" class="current-user-info">
							<img :src="chatUser.avatar" class="avatar" />
							<span class="user-name">{{ chatUser.name }}</span>
						</div>
					</div>
					<div class="messages" ref="messageContainer" @scroll="messagesScrolling">
						<div v-if="messageListMap[chatUser.id]&&messageListMap[chatUser.id].length!=0">
							<div  v-for="message in messageListMap[chatUser.id]" :key="message.id"
								:class="{ 'my-message': message.fromUserId === currentUser.id, 'other-message': message.fromUserId !== currentUser.id }"
								class="message-item">
								<img :src="usersMap[message.fromUserId]?.avatar || currentUser.avatar" class="avatar message" />
								<div class="message-box-column">
									<div class="message-sender" :class="message.fromUserId === currentUser.id ? 'text-r' : 'text-l'">
										{{ usersMap[message.fromUserId]?.name || currentUser.name }}
										<span style="font-weight: 100;">
											&nbsp;{{ message.createTime }}</span>
									</div>
									<div class="message-content">
										<p class="message-text">{{ message.content }}</p>
									</div>
								</div>
							</div>
						</div>
						<div v-else class="text-align-center">
							{{ '暂无消息' }}
						</div>
					</div>
					<div class="message-input">
						<a-textarea v-model:value="newMessage" @keypress.enter="sendMessage" placeholder="输入消息..."
							:auto-size="{ minRows: 3, maxRows: 6 }" />
						<a-button style="float: right;margin-top: 5px;" @click="sendMessage" type="primary"
							:disabled="!chatUser.id">发送</a-button>
					</div>
				</div>
			</div>
			<template #footer />
		</a-modal>
	</div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { MessageOutlined, SearchOutlined } from '@ant-design/icons-vue';
import websocket from '@/utils/websocketTool';
import tool from '@/utils/tool'
import imSysUserApi from '@/api/im/imSysUserApi'
import imMessageApi from '@/api/im/imMessageApi'
import { User, Message, ImMessageUserVo, ImMessageBo } from './type.js'
import { notification } from 'ant-design-vue'

// 聊天记录参数
const queryChatRecordWithUserParams = reactive({
	current: 1,
	size: 10,
	userId: '',
});

//聊天界面用户列表参数
const queryChatRecordParams = reactive({
	current: 1,
	size: 4,
	total: -1,
});

const messageContainer = ref(null)

// B端用户1 C端用户2
const userClient = ref('1');
const open = ref(false);
const activeKey = ref('2')
// 当前聊天用户
const chatUser = reactive<User>({ id: '', name: '', avatar: '' });
// 当前用户
const currentUser = reactive<User>({ id: '', name: '', avatar: '' });
// 聊天页面用户列表
const ImMessageUserVoList = reactive<ImMessageUserVo[]>([]);
// 通过客户端类型区分用户列表
const users = reactive<Record<string, User[]>>({
	'1': [],
	'2': []
});
// 全量用户列表
const usersMap = reactive<Record<string, User>>({});
// 消息列表
const messageListMap = reactive<Record<string, Message[]>>({
});
// 发送的消息
const newMessage = ref<string>('');

const messgaeScrollHeight = ref(0);

onMounted(() => {
	initUserInfo();
	initMessageList();
	websocket.InitWebSocket();
	if (!websocket.onMessageCallback) {
		websocket.setMessageCallback(onMessage);
	}
});

// 监听ref 滚动到底部
const scrolling = (e) => {
	const clientHeight = e.target.clientHeight
	const scrollHeight = e.target.scrollHeight
	const scrollTop = e.target.scrollTop
	if (scrollTop + clientHeight >= scrollHeight) {
		// 判断是哪个tab
		if (activeKey.value == '1') {
			// 判断是否还有数据 -1时候为初始化
			if (queryChatRecordParams.total != -1 && queryChatRecordParams.current * queryChatRecordParams.size >= queryChatRecordParams.total) {
				return;
			}
			queryChatRecordWithUserParams.current += 1;
			initMessageList()
		}
	} else if (activeKey.value == '2') {
	
	} else {

	}
	// console.log(`到底了!${activeKey.value == '1' ? '聊天' : activeKey.value == '2' ? '用户' : '群组'}`);
}

//监听消息列表
const messagesScrolling = (e) => {
	const scrollTop = e.target.scrollTop
	// 加载以前的消息
	if (scrollTop == 0) {
		if (queryChatRecordWithUserParams.current * queryChatRecordWithUserParams.size >= usersMap[chatUser.id].total || usersMap[chatUser.id].total == 0) {
			return;
		}
		queryChatRecordWithUserParams.current += 1;
		usersMap[chatUser.id].current?queryChatRecordWithUserParams.current:-1;
		selectMessageList();
		console.log(usersMap[chatUser.id].current);
		
	}
}


const onMessage = (data)=> {
  let json = JSON.parse(data);
  if (!json.fromUserId) return;

  // 格式化时间
  if (typeof json.createTime === 'string'||typeof json.createTime === 'number') {
    json.createTime = new Date(json.createTime).toLocaleString();
  }

  // 更新或添加消息到ImMessageUserVoList
  updateOrCreateUserVoList(json);

  // 更新消息列表
  updateMessageListMap(json);

  // 如果是当前聊天对象，设置消息为已读并滚动到底部
  if (json.toUserId == currentUser.id && json.fromUserId == chatUser.id) {
    setMessagesAsRead(json);
    scrollToBottomOnNextTick();
  } else if (json.toUserId == currentUser.id) {
    // 不是当前聊天对象，增加未读计数
    incrementUnreadCount(json);
  }
}

const  updateOrCreateUserVoList = (json)=> {
  const userId = json.toUserId == currentUser.id ? json.fromUserId : json.fromUserId == currentUser.id ? json.toUserId : null;
  if (!userId) return;

  const index = ImMessageUserVoList.findIndex(item => item.userId == userId);
  if (index === -1) {
	const message = {
		userId,
		content: json.content,
		createTime: json.createTime,
		unreadCount: 0
	}
	ImMessageUserVoList.push(message);
  } else {
	ImMessageUserVoList[index].content = json.content;
	ImMessageUserVoList[index].createTime = json.createTime;
  }
	

	//排序
	ImMessageUserVoList.sort((a, b) => {
		return new Date(b.createTime).getTime() - new Date(a.createTime).getTime();
	});
}

const updateMessageListMap = (json) => {
  const targetUserId = json.fromUserId == currentUser.id ? json.toUserId : json.fromUserId;
  if (!targetUserId) return;

  if (!messageListMap[targetUserId]) {
    messageListMap[targetUserId] = [];
  }
  messageListMap[targetUserId].push(json);

  scrollToBottomOnNextTick();
}

const setMessagesAsRead = (json) => {
  setRead([{ id: json.id }]);
  messageListMap[json.fromUserId].forEach(item => {
    if (item.toUserId == currentUser.id && item.isRead == '2') {
      item.isRead = '1';
    }
  });
}

const incrementUnreadCount = (json) => {
  ImMessageUserVoList.forEach(item => {
    if (item.userId == json.fromUserId && json.isRead == 2) {
      item.unreadCount += 1;
    }
  });
}
// 滚动到底部
const scrollToBottomOnNextTick = () => {
  nextTick(() => {
		let scrollElem = messageContainer.value;
		scrollElem.scrollTo({ top: scrollElem.scrollHeight, behavior: 'smooth' });
  });
}


const switchClient = (client: string) => {
	userClient.value = client;
}

const changeTabs = (value)=> {
}

const handleOpen = () =>{
	open.value = true;
}

// 初始化聊天列表
const initMessageList=() => {
	// 查询当前用户的所有聊天人员列表和最后一条消息
	imMessageApi.queryChatRecord(queryChatRecordParams.value).then(res => {
		ImMessageUserVoList.push(...res.records);
		queryChatRecordParams.total = res.total;
	});
}

// 通过用户id查询和当前用户的聊天记录
const selectMessageUser = (user: User) => {
  // 将未读消息数置零
  resetUnreadCount(user.id);

  // 如果是当前对话用户，无需进一步操作
  if (user.id === chatUser.id) return;

  // 需要对话的用户和当前对话用户不一致且对话用户没有聊天记录
  if (usersMap[user.id].current === -1) {
    initChatUserAndQueryRecords(user);
  } else {
    // 用户已有聊天记录，但不是当前聊天对象
    scrollToBottomAndInitChatUser(user);
  }

  // 置零前端的未读消息
  markMessagesAsRead(user.id);
}

const resetUnreadCount = (userId) => {
  ImMessageUserVoList.forEach(item => {
    if (item.userId === userId) {
      item.unreadCount = 0;
    }
  });
}

const initChatUserAndQueryRecords = (user)=> {
  initChatUser(user);
  queryChatRecordWithUserParams.current = 1;
  adjustQuerySizeBasedOnUnread(user.id);
  selectMessageList();
}

const adjustQuerySizeBasedOnUnread = (userId)=> {
  const user = ImMessageUserVoList.find(item => item.userId === userId);
  if (user && user.unreadCount > 0) {
    const paramSize = queryChatRecordParams.size;
    const size = Math.ceil(user.unreadCount / paramSize);
    queryChatRecordParams.size = size * paramSize;
  }
}

const scrollToBottomAndInitChatUser = (user)=> {
  nextTick(() => {
    setTimeout(() => {
      messageContainer.value.scrollTop = messageContainer.value.scrollHeight;
    }, 100);
  });
  initChatUser(user);
  queryChatRecordWithUserParams.current = usersMap[user.id].current;
}

const markMessagesAsRead = (userId) =>{
  if (messageListMap[userId] && messageListMap[userId].length > 0) {
    messageListMap[userId].forEach(item => {
      if (item.toUserId === currentUser.id && item.isRead === '2') {
        item.isRead = '1';
        setRead([{ id: item.id }]);
      }
    });
  }
}
// 给当前聊天用户赋值
const initChatUser = (user: User) => {
	chatUser.id = user.id;
	chatUser.name = user.name;
	chatUser.avatar = user.avatar;
	queryChatRecordWithUserParams.userId = user.id;
}

// 查询消息记录
const selectMessageList = () => {
	if (!chatUser.id || queryChatRecordWithUserParams.userId == '') {
		notification.warning({
			message: '请选择聊天对象'
		})
	}
	messgaeScrollHeight.value = messageContainer.value.scrollHeight;
	imMessageApi.queryChatRecordWithUser(queryChatRecordWithUserParams).then(res => {
		if (!messageListMap[chatUser.id]||usersMap[chatUser.id].current == -1) {
			messageListMap[chatUser.id] = [];
		}
		messageListMap[chatUser.id].unshift(...res.records);
		usersMap[chatUser.id].total = res.total;
		if (queryChatRecordWithUserParams.current == 1) {
				scrollToBottomOnNextTick();
		} else {
			nextTick(() => {
				let scrollHeight = messageContainer.value.scrollHeight;
				messageContainer.value.scrollTo({ top: scrollHeight - messgaeScrollHeight.value, behavior: 'auto' });
			});
		}
		usersMap[chatUser.id].current?queryChatRecordWithUserParams.current:-1;
	});
}

const sendMessage = () => {
	if (chatUser.id == '') {
		notification.warning({
			message: '请选择聊天对象'
		})
	}
	if (newMessage.value.trim()) {
		// 拼接消息
		const msg: ImMessageBo = {
			fromUserId: currentUser.id,
			toUserId: chatUser.id,
			content: newMessage.value,
			chatType: '1',
			type: '1',
			toUserType: userClient.value,
			fromUserType: '1'
		};
		// 发送消息
		websocket.Send(msg);
		newMessage.value = '';
	}
}
// 初始化当前用户的好友列表
const getUserList =() => {
	imSysUserApi.imUserPage().then(res => {
		users[userClient.value].push(...res.records);
		// 缓存头像 节省前端内存
		res.records.forEach(item => {
			item.current = -1;
			usersMap[item.id] = item;
		});
	})
	initUserInfo();
}

// 获取当前帐号信息
const initUserInfo = () => {
	let user = tool.data.get('USER_INFO');
	currentUser.id = user.id;
	currentUser.name = user.name;
	currentUser.avatar = user.avatar;
	usersMap[user.id] = currentUser;
}

// 将消息设置为已读
const setRead = (ids: []) => {
	imMessageApi.setMessageRead(ids);
}

getUserList();
</script>

<style scoped>
.text-align-center {
	text-align: center;
}

.chat-container {
	display: flex;
	height: 100%;
}

.user-list {
	padding-right: 20px;
	width: 30%;
	border-right: 1px solid #f0f0f0;
	overflow-y: auto;
	min-width: 200px;
}

.avatar {
	width: 32px;
	height: 32px;
	border-radius: 50%;
	flex-shrink: 0;
}

.message {
	margin-right: 10px;
	margin-left: 10px;
}

.chat-content {
	flex: 1;
	display: flex;
	flex-direction: column;
	height: 100%;
	width: 80%;
}

.current-user {
	flex: 0 0 auto;
	padding: 16px;
	border-bottom: 1px solid #f0f0f0;
	background-color: #fafafa;
}

.messages {
	flex: 1;
	padding: 16px;
	overflow-y: auto;
	display: flex;
	flex-direction: column;
	width: 100%;
	overflow-x: auto;
}

.message-item {
	display: flex;
	align-items: flex-start;
	margin-bottom: 16px;
}

.my-message {
	flex-direction: row-reverse;
}

.my-message .message-content {
	background-color: #e6f7ff;
	margin-left: auto;
}

.other-message {
	text-align: left;
}

.other-message .message-content {
	background-color: #f6f6f6;
	margin-right: auto;
}

.message-content {
	padding: 10px;
	border-radius: 8px;
}

.message-sender {
	font-weight: bold;
	margin-bottom: 4px;
}

.message-text {
	margin: 0;
	word-wrap: break-word;
	display: inline-block;
}

.message-box-column {
	display: flex;
	flex-direction: column;
	width: auto;
	max-width: 80%;
}

.message-input {
	padding: 16px;
	border-top: 1px solid #f0f0f0;
}

.listItem {
	padding: 10px 0px;
}

.current-user {
	flex: 0 0 auto;
	padding: 16px;
	display: flex;
	align-items: center;
	/* 垂直居中 */
	border-bottom: 1px solid #f0f0f0;
	background-color: #fafafa;
}

.current-user-info {
	display: flex;
	align-items: center;
	/* 垂直居中 */
}

.user-name {
	margin-left: 10px;
	/* 为用户名称添加左边距 */
}

.space-around {
	display: flex;
	justify-content: space-around;
}

.text-r {
	text-align: right !important;
}


.text-l {
	text-align: left !important;
}

.text-long {
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
	width: 100%;
}

.webkit-scrollbar {
	overflow-y: auto;
	overflow-x: hidden;
	-ms-overflow-style: none;
	scrollbar-width: none;
	height: calc(100vh - 400px);
}

.webkit-scrollbar-2 {
	overflow-y: auto;
	overflow-x: hidden;
	-ms-overflow-style: none;
	scrollbar-width: none;
	height: calc(100vh - 445px);
}

</style>
