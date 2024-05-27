<template>
	<div class="im panel-item" @click="handleOpen">
		<MessageOutlined/>
		<a-modal
			v-model:open="open"
			title="聊天"
			:style="{ width: 'auto', height: 'auto', maxWidth: '60vw', maxHeight: '90vh' }"
			:body-style="{ padding: 0, height: 'calc(100vh - 300px)', overflow: 'hidden' }"
		>
			<div class="chat-container">
				<div class="user-list">
					<a-input placeholder="搜索">
						<template #prefix>
							<SearchOutlined/>
						</template>
					</a-input>
					<a-tabs v-model:activeKey="activeKey" centered>
						<a-tab-pane key="1" tab="聊天">
							<a-list
								:data-source="users"
								:item-layout="'horizontal'"
							>
								<template #renderItem="{ item }">
									<a-list-item @click="selectUser(item)" class="listItem">
										<a-list-item-meta>
											<template #title>
												<span>{{ item.name }}</span> <a-tag color="blue">B</a-tag>
											</template>
											<template #description>
												<span>We supply a series of ...</span>
											</template>
											<template #avatar>
												<img :src="item.avatar" class="avatar"/>
											</template>
										</a-list-item-meta>
									</a-list-item>
								</template>
							</a-list>
						</a-tab-pane>
						<a-tab-pane key="2" tab="用户">

						</a-tab-pane>
						<a-tab-pane key="3" tab="群组">

						</a-tab-pane>
					</a-tabs>
				</div>
				<div class="chat-content">
					<div class="current-user">
						<div v-if="currentUser.id" class="current-user-info">
							<img :src="currentUser.avatar" class="avatar" />
							<span class="user-name">{{ currentUser.name }}</span>
						</div>
					</div>
					<div class="messages">
						<div
							v-for="message in messages"
							:key="message.id"
							:class="{'my-message': message.sender === currentUser.id, 'other-message': message.sender !== currentUser.id}"
							class="message-item"
						>
							<img :src="message.avatar" class="avatar message"/>
							<div class="message-content">
								<span class="message-sender">{{ message.name }}</span>
								<p class="message-text">{{ message.text }}</p>
							</div>
						</div>
					</div>
					<div class="message-input">
						<a-textarea
							v-model="newMessage"
							@keypress.enter="sendMessage"
							placeholder="输入消息..."
							:auto-size="{ minRows: 3, maxRows: 6 }"
						/>
					</div>
				</div>
			</div>
			<template #footer/>
		</a-modal>
	</div>
</template>

<script setup lang="ts">
import {ref, reactive} from 'vue';
import {MessageOutlined, SearchOutlined} from '@ant-design/icons-vue';

interface User {
	id: number;
	name: string;
	avatar: string;
}

interface Message {
	id: number;
	text: string;
	sender: number;
	name: string;
	avatar: string;
}

const open = ref(false);
const activeKey = ref('1')
const currentUser = reactive({});
const users = reactive<User[]>([
	{id: 1, name: 'User 1', avatar: 'https://xiaoming728.com/upload/logo.jpg'},
	{id: 2, name: 'User 2', avatar: 'https://xiaoming728.com/upload/logo.jpg'},
	{id: 3, name: 'User 3', avatar: 'https://xiaoming728.com/upload/logo.jpg'}
]);
const messages = reactive<Message[]>([
	{id: 1, text: 'Hello!', sender: 1, name: 'User 1', avatar: 'https://xiaoming728.com/upload/logo.jpg' },
	{id: 2, text: 'Hi!', sender: 2, name: 'User 2', avatar: 'https://xiaoming728.com/upload/logo.jpg'}
]);
const newMessage = ref('');

function handleOpen() {
	open.value = true;
}

function selectUser(user: User) {
	currentUser.id = user.id;
	currentUser.name = user.name;
	currentUser.avatar = user.avatar;
}

function sendMessage() {
	if (newMessage.value.trim()) {
		messages.push({
			id: Date.now(),
			text: newMessage.value,
			sender: currentUser.id,
			name: currentUser.name,
			avatar: currentUser.avatar
		});
		newMessage.value = '';
	}
}
</script>

<style scoped>
.chat-container {
	display: flex;
	height: 100%;
}

.user-list {
	padding-right: 20px;
	width: 30%;
	border-right: 1px solid #f0f0f0;
	overflow-y: auto;
}

.avatar {
	width: 32px;
	height: 32px;
	border-radius: 50%;

	&.message {
		margin-right: 10px;
		margin-left: 10px;
	}
}


.chat-content {
	flex: 1;
	display: flex;
	flex-direction: column;
	height: 100%;
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
}

.message-item {
	display: flex;
	align-items: flex-start;
	margin-bottom: 16px;
}

.my-message {
	flex-direction: row-reverse;
	text-align: right;
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
	max-width: 70%;
	padding: 10px;
	border-radius: 8px;
}

.message-sender {
	font-weight: bold;
	margin-bottom: 4px;
	display: block;
}

.message-text {
	margin: 0;
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
	align-items: center; /* 垂直居中 */
	border-bottom: 1px solid #f0f0f0;
	background-color: #fafafa;
}

.current-user-info {
	display: flex;
	align-items: center; /* 垂直居中 */
}

.user-name {
	margin-left: 10px; /* 为用户名称添加左边距 */
}


</style>
