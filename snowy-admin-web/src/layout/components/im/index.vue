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
							<a-list class="webkit-scrollbar" :data-source="ImMessageUserVoList" :item-layout="'horizontal'" @scroll="scrolling">
								<template #renderItem="{ item }" >
									<a-list-item @click="selectMessageUser(usersMap.value[item.userId])" class="listItem">
										<a-list-item-meta>
											<template #title>
												<span>{{ usersMap.value[item.userId].name }}</span> <a-tag color="blue">{{ (userClient === '1')?'B':'C' }}</a-tag>
											</template>
											<template #description>
												<div class="text-long">
													<span>{{ item.content }}</span>
												</div>
											</template>
											<template #avatar>
												<img :src="usersMap.value[item.userId].avatar" class="avatar" />
											</template>
										</a-list-item-meta>
									</a-list-item>
								</template>
							</a-list>
						</a-tab-pane>
						<a-tab-pane key="2" tab="用户">
							<div v-if="true" class="space-around" style="padding-bottom: 10px;">
								<a-button :type="userClient == '1' ? 'primary' : 'dashed'" @click="switchClient('1')">B 端用户</a-button>
								<a-button :type="userClient == '2' ? 'primary' : 'dashed'" @click="switchClient('2')" disabled>C 端用户</a-button>
							</div>
							<a-list :data-source="users[userClient]" :item-layout="'horizontal'" class="webkit-scrollbar-2" @scroll="scrolling">
								<template #renderItem="{ item }">
									<a-list-item @click="selectMessageUser(item)" class="listItem">
										<a-list-item-meta>
											<template #title>
												<span>{{ item.name }}</span> <a-tag color="blue">{{ (userClient === '1')?'B':'C' }}</a-tag>
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
					<div class="messages">
						<div v-if="messageListMap[chatUser.id]" v-for="message in messageListMap[chatUser.id]" :key="message.id"
							:class="{ 'my-message': message.fromUserId === chatUser.id, 'other-message': message.fromUserId !== chatUser.id }"
							class="message-item">
							<img :src="usersMap.value?.[message.toUserId]?.avatar ||  currentUser.avatar" class="avatar message" />
							<div class="message-box-column">
								<div class="message-sender" :class="message.fromUserId === chatUser.id ? 'text-r' : 'text-l'">{{ usersMap.value?.[message.toUserId]?.name ||  currentUser.name}}<span style="font-weight: 100;"> &nbsp;{{ message.createTime }}</span></div>
								<div class="message-content">
									<p class="message-text">{{ message.content}}</p>
								</div>
							</div>
						</div>
						<div v-else class="text-align-center">
								{{ '暂无消息' }}
						</div>
					</div>
					<div class="message-input">
						<a-textarea v-model="newMessage" @keypress.enter="sendMessage" placeholder="输入消息..." :auto-size="{ minRows: 3, maxRows: 6 }" />
						<p style="float: right;">by:lkx</p>
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
import { InitWebSocket, websocket } from '@/utils/websocketTool';
import tool from '@/utils/tool'
import imSysUserApi from '@/api/im/imSysUserApi'
import imMessageApi from '@/api/im/imMessageApi'
import {User,Message,ImMessageUserVo } from './type.js'

const queryChatRecordWithUserParams = reactive({
	current: 1,
	size: 4,
	userId: ''
});

const queryChatRecordParams = reactive({
	current: 1,
	size: 4,
});

// B端用户1 C端用户2
const userClient = ref('1');
const open = ref(true);
const activeKey = ref('1')
const chatUser = reactive<User>({ id: '', name: '', avatar: '' });
const currentUser = reactive<User>({ id: '', name: '', avatar: '' });

const ImMessageUserVoList = reactive<ImMessageUserVo[]>([]);
// 通过客户端类型区分用户列表
const users = reactive<Record<string,User[]>>({
	'1': [],
	'2': []
});
// 全量用户列表
const usersMap = reactive<Record<string, User>>({});

const messageListMap = reactive<Record<string, Message[]>>({});

const newMessage = ref('');

onMounted(() => {
	initUserInfo();
	getUserList({});
	initMessageList();
	InitWebSocket('ws://127.0.0.1:82/ws/im?token=' + tool.data.get('TOKEN'), onMessage);
});


// 监听ref 滚动到底部
const scrolling = (e) => {
    const clientHeight = e.target.clientHeight
    const scrollHeight = e.target.scrollHeight
    const scrollTop = e.target.scrollTop
     
    if (scrollTop + clientHeight >= scrollHeight) {
      console.log(`到底了!${activeKey.value=='1'?'聊天':activeKey.value=='2'?'用户':'群组'}`);
    }
  }
function onMessage(data) {
	console.log('onMessage', data.data);
}

function switchClient(client: string) {
	userClient.value = client;
}

function changeTabs(value ){
	console.log('changeTabs',value);
}

function handleOpen() {
	open.value = true;
}

// 初始化聊天列表
function initMessageList() {
	// 查询当前用户的所有聊天人员列表和最后一条消息
	imMessageApi.queryChatRecord(queryChatRecordParams.value).then(res => {
		ImMessageUserVoList.push(...res.records);
	});
}

// 通过用户id查询和当前用户的聊天记录
function selectMessageUser(user: User) {
	chatUser.id = user.id;
	chatUser.name = user.name;
	chatUser.avatar = user.avatar;
	queryChatRecordWithUserParams.userId = user.id;
	imMessageApi.queryChatRecordWithUser(queryChatRecordWithUserParams).then(res => {
		messageListMap[user.id] = res.records;
	});
}

function sendMessage() {
	if (newMessage.value.trim()) {
		// messages.push({
		// 	id: Date.now(),
		// 	text: newMessage.value,
		// 	sender: chatUser.id,
		// 	name: chatUser.name,
		// 	avatar: chatUser.avatar
		// });
		newMessage.value = '';
	}
}
// 初始化当前用户的好友列表
function getUserList(userParams) {
	imSysUserApi.imUserPage({ current: '1', size: '4' }).then(res => {
		users[userClient.value].push(...res.records);
		// 缓存头像 节省前端内存
		usersMap.value = res.records.reduce((acc, cur) => {
			acc[cur.id] = cur;
			return acc;
		}, {});
	})
}

// 获取当前帐号信息
function initUserInfo() {
	let user = tool.data.get('USER_INFO');
	console.log(user.id);
	currentUser.id = user.id;
	currentUser.name = user.name;
	currentUser.avatar = user.avatar;
}

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
}

.other-message {
	text-align: left;
}

.other-message .message-content {
	background-color: #f6f6f6;
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

.text-long{
	overflow:hidden;
   text-overflow:ellipsis;
   white-space:nowrap;
	 width: 100%;
}
.webkit-scrollbar{
	overflow-y: auto;
	overflow-x: hidden;
	-ms-overflow-style: none;
	scrollbar-width: none;
	height: calc(100vh - 400px);
}

.webkit-scrollbar-2{
	overflow-y: auto;
	overflow-x: hidden;
	-ms-overflow-style: none;
	scrollbar-width: none;
	height: calc(100vh - 445px);
}
</style>
