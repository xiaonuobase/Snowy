<template>
	<div class="im panel-item" @click="handleOpen">
		<MessageOutlined />
		<a-modal v-model:open="open" title="聊天"
			:style="{ width: 'auto', height: 'auto', maxWidth: '60vw', maxHeight: '90vh' }"
			:body-style="{ padding: 0, height: 'calc(100vh - 300px)', overflow: 'hidden' }">
			<div class="chat-container">
				<div class="user-list">
					<div class="flex">
						<a-input placeholder="搜索">
							<template #prefix>
								<SearchOutlined />
							</template>
						</a-input>
						<PlusOutlined class="create-group" @click="createGroup" />
					</div>
					<a-tabs v-model:activeKey="activeKey" centered>
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
													<span>{{ groupRecall(item.content) }}</span>
												</div>
											</template>
											<template #avatar>
												<a-badge :count="item.unreadCount">
													<!-- <img :src="usersMap[item.userId + ''].avatar" class="avatar" /> -->
													<a-avatar :src="usersMap[item.userId + ''].avatar" shape="shape" />
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
							<a-list :data-source="groupList" :item-layout="'horizontal'" class="webkit-scrollbar-2"
								@scroll="scrolling">
								<template #renderItem="{ item }">
									<a-list-item @click="selectMessageUser(item)" class="listItem">
										<a-list-item-meta>
											<template #title>
												<span>{{ item.name }}</span> <a-tag color="blue">群组</a-tag>
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
					</a-tabs>
				</div>
				<div class="chat-content">
					<div class="current-user">
						<div v-if="chatUser.id" class="current-user-info">
							<img :src="chatUser.avatar" class="avatar" />
							<span class="user-name">{{ chatUser.name }}</span>
						</div>
						<div v-if="chatUser.chatType=='2'">
							<a-button @click="updateGroup(chatUser.id)">编辑</a-button>
						</div>
					</div>
					<div class="messages" ref="messageContainer" @scroll="messagesScrolling">
						<div v-if="messageListMap[chatUser.id] && messageListMap[chatUser.id].length != 0">
							<div v-for="message in messageListMap[chatUser.id]" :key="message.id">
								<div
									:class="{ 'my-message': message.fromUserId === currentUser.id, 'other-message': message.fromUserId !== currentUser.id }"
									class="message-item" v-if="message.isRecall == '2'">
									<img :src="usersMap[message.fromUserId]?.avatar || currentUser.avatar" class="avatar message" />
									<div class="message-box-column">
										<div class="message-sender" :class="message.fromUserId === currentUser.id ? 'text-r' : 'text-l'">
											{{ usersMap[message.fromUserId]?.name || currentUser.name }}
											<span style="font-weight: 100;">
												&nbsp;{{ message.createTime }}</span>
										</div>
										<div class="message-content" v-if="message.type == '1'">
											<div class="box2" @contextmenu="onContextMenu($event, message)">
												<p class="message-text">{{ message.content }}</p>
											</div>
										</div>
										<div class="message-content" v-else @contextmenu="onContextMenu($event, message)"
											v-for="key in [JSON.parse(message.content)]">
											<div class="box2" @click="onPreview(key)"
												:style="{ minWidth: imageSuffix.indexOf(key.suffix) == -1 ? '200px' : 'auto' }">
												<a-image :width="80" :src="key.downloadPath"
													fallback="data:image/png;base64,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"
													v-if="imageSuffix.indexOf(key.suffix) > -1" />
												<img src="/src/assets/images/fileImg/docx.png" class="record-img"
													:title="previewDisplay(key.suffix)"
													v-else-if="key.suffix === 'doc' || key.suffix === 'docx'" />
												<img src="/src/assets/images/fileImg/xlsx.png" class="record-img"
													:title="previewDisplay(key.suffix)"
													v-else-if="key.suffix === 'xls' || key.suffix === 'xlsx'" />
												<img src="/src/assets/images/fileImg/zip.png" :title="previewDisplay(key.suffix)"
													class="record-img" v-else-if="key.suffix === 'zip'" />
												<img src="/src/assets/images/fileImg/rar.png" :title="previewDisplay(key.suffix)"
													class="record-img" v-else-if="key.suffix === 'rar'" />
												<img src="/src/assets/images/fileImg/ppt.png" class="record-img"
													:title="previewDisplay(key.suffix)"
													v-else-if="key.suffix === 'ppt' || key.suffix === 'pptx'" />
												<img src="/src/assets/images/fileImg/pdf.png" :title="previewDisplay(key.suffix)"
													class="record-img" v-else-if="key.suffix === 'pdf'" />
												<img src="/src/assets/images/fileImg/txt.png" :title="previewDisplay(key.suffix)"
													class="record-img" v-else-if="key.suffix === 'txt'" />
												<img src="/src/assets/images/fileImg/html.png" :title="previewDisplay(key.suffix)"
													class="record-img" v-else-if="key.suffix === 'html'" />
												<img src="/src/assets/images/fileImg/file.png" :title="previewDisplay(key.suffix)"
													class="record-img" v-else />
											</div>
											<p v-if="imageSuffix.indexOf(key.suffix) == -1">{{ key.name }}</p>
										</div>
									</div>
								</div>
								<div v-else style="color:gray;font-size:x-small; display: flex; justify-content: center;">
									<p>{{groupRecall(message.content)}}</p>
								</div>
							</div>
						</div>
						<div v-else class="text-align-center">
							{{ '暂无消息' }}
						</div>
					</div>
					<div class="message-input">
						<FileImageOutlined class="large" @click="uploadImage('图片')" />
						<FolderOutlined class="large" @click="uploadImage('文件')" />
						<a-textarea v-model:value="newMessage" @keypress.enter="sendMessage" placeholder="输入消息..."
							:auto-size="{ minRows: 3, maxRows: 6 }" :disabled="false"/>
						<a-button style="float: right;margin-top: 5px;" @click="sendMessage" type="primary"
							:disabled="!chatUser.id">发送</a-button>
					</div>
				</div>
			</div>
			<template #footer />
		</a-modal>
	</div>
	<a-modal v-model:open="uploadShow" :title="'发送'+uploadTitle" @ok="handleOk">
		<xn-upload v-if="uploadShow" uploadMode="drag" ref="uploadImageRef"></xn-upload>
	</a-modal>
	<a-modal v-model:open="previewShow" title="预览文件" :width="1000">
		<xn-file-preview v-show="previewShow" :src="previewSrc" :file-type="previewFileType" @goBack="previewBack" />
		<template #footer />
	</a-modal>
	<a-modal v-model:open="createGroupShow" title="创建群组" @ok="createGroupOk">
		<CreateGroup @updateGroupInfo="updateGroupInfoData" :createGroupType="createGroupType" :id= "updateGroupId" v-if="createGroupShow" ref="createGroupRef" />
	</a-modal>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import dayjs from 'dayjs'
import { MessageOutlined, SearchOutlined } from '@ant-design/icons-vue';
import websocket from '@/utils/websocketTool';
import tool from '@/utils/tool'
import imSysUserApi from '@/api/im/imSysUserApi'
import imMessageApi from '@/api/im/imMessageApi'
import imGroupApi from '@/api/im/imGroupApi.js'
import { User, Message, ImMessageUserVo, ImMessageBo, ImGroupVo } from './type.js'
import { notification } from 'ant-design-vue'
import ContextMenu from "@imengyu/vue3-context-menu";
import XnUpload from '@/components/XnUpload/index.vue'
import CreateGroup from './createGroup.vue'

const uploadTitle = ref('')
const previewSrc = ref()
const previewFileType = ref()

const imageSuffix = ['png', 'jpg', 'jpeg', 'ico', 'bmp', 'gif'];
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
// 群组列表
const groupList = reactive<ImGroupVo[]>([]);
// 消息列表
const messageListMap = reactive<Record<string, Message[]>>({
});
// 发送的消息
const newMessage = ref<string>('');
const messgaeScrollHeight = ref(0);
const menuData = reactive({
	theme: 'mac',
	items: [
	],
	iconFontClass: 'iconfont',
	customClass: "class-a",
	zIndex: 9999,
	minWidth: 100,
	x: null,
	y: null
})
const uploadShow = ref(false)
const uploadImageRef = ref(null)
const previewShow = ref(false)
const createGroupShow = ref(false)
const createGroupRef = ref(null)
const createGroupType = ref('add')
const updateGroupId = ref(null)


onMounted(() => {
	initMessageList();
	websocket.InitWebSocket();
	if (!websocket.onMessageCallback) {
		websocket.setMessageCallback(onMessage);
	}
});
// 更新群组信息 如果修改的话
const updateGroupInfoData = (e) => {
	nextTick(()=>{
		usersMap[e.id].name = e.name;
		usersMap[e.id].avatar = e.avatar;
		groupList.forEach(element => {
		if (element.id == e.id) {
			element.name = e.name;
			element.avatar = e.avatar;
		}
		});
	})
}

// 群组撤回翻译
const groupRecall = (msg: string) => {
	if(msg.indexOf('%s')!=-1){
		let msgValue = msg.split(',')
		let user = usersMap[msgValue[1]];
		user = user?user:{name:'未知'}
		return msgValue[0].replace('%s',user.name)
	}else{
		return msg
	}
}

// 初始化群组
const initGroupList = () => {
	imGroupApi.imGroupListByUser({}).then(res => {
		res.forEach(element => {
			element.useType = '2';
			groupList.push(element);
			element.current = -1;
			usersMap[element.id] = element;
		});
	})
}

const updateGroup = (id: string) => {
	createGroupShow.value = true;
	createGroupType.value = 'update';
	updateGroupId.value = id;
}

const createGroup = () => {
	createGroupType.value = 'add';
	createGroupShow.value = true;
}
const createGroupOk = () => {
	// 调用子组件方法
	createGroupRef.value.add();
	createGroupShow.value = false;
}

const previewBack = () => {
	previewShow.value = false;
}

const onPreview = (obj: Object) => {
	if (imageSuffix.indexOf(obj['suffix']) > -1) {
		return;
	}
	previewShow.value = true;
	nextTick(() => {
		previewSrc.value = obj['downloadPath']
		previewFileType.value = obj['suffix']
	})
}

// 判断是否显示预览
const previewDisplay = (fileSuffix: string) => {
	if (!fileSuffix) {
		return '点击下载'
	}
	const suffix = fileSuffix.toLowerCase()
	if (
		suffix === 'doc' ||
		suffix === 'docx' ||
		suffix === 'xls' ||
		suffix === 'xlsx' ||
		suffix === 'pdf' ||
		suffix === 'jpg' ||
		suffix === 'png' ||
		suffix === 'gif' ||
		suffix === 'svg' ||
		suffix === 'ico' ||
		suffix === 'tmp' ||
		suffix === 'jpeg'
	) {
		return '点击预览'
	}
}

const uploadImage = (title: string) => {
	uploadShow.value = true;
	uploadTitle.value = title
}

const handleOk = () => {
	uploadShow.value = false;
	let obj = uploadImageRef.value.uploadFileList()[0];
	if (obj['url']) {
		sendMessageByFile(obj);
	}
}


const onContextMenu = (e: MouseEvent, msg: Message) => {
	e.preventDefault();
	e.stopPropagation();
	menuData.x = e.x;
	menuData.y = e.y;
	ContextMenu.showContextMenu(menuData);
	menuData.items = [{
		label: '复制',
		onClick: () => {
			if (msg.type != '1') {
				notification.warning({
					message: '暂不支持复制文件'
				});
				return;
			} else {
				tool.copyToClipboard(msg.content);
				notification.success({
					message: '复制成功'
				})
			}
		},
	}];
	reCallMeun(msg);
}

// 撤回消息方法
const reCallMeun = (msg: Message) => {
	// 数组中是否存在撤回 且是当前用户发送的消息 且两分钟之内的数据
	if (menuData.items.findIndex(item => item.label == '撤回') == -1 && msg.fromUserId == currentUser.id && new Date().getTime() - new Date(msg.createTime).getTime() < 120000) {
		let call = {
			label: '撤回',
			onClick: () => {
				//调用撤回接口
				imMessageApi.recallMessage({ id: msg.id }).then(res => {
					notification.success({
						message: '撤回成功'
					})
				})
			},
		}
		menuData.items.push(call);
	}
}
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
		usersMap[chatUser.id].current = queryChatRecordWithUserParams.current;
		selectMessageList();
	}
}


const onMessage = (data) => {
	let json = JSON.parse(data);
	if (!json.fromUserId) return;
	// 格式化时间
	if (typeof json.createTime === 'string' || typeof json.createTime === 'number') {
		json.createTime = dayjs(json.createTime).format('YYYY-MM-DD HH:mm:ss');
	}

	// 更新或添加消息到ImMessageUserVoList
	updateOrCreateUserVoList(json);

	// 更新消息列表
	updateMessageListMap(json);

	// 如果是当前聊天对象，设置消息为已读并滚动到底部
	if (json.toUserId === currentUser.id && json.fromUserId === chatUser.id) {
		setMessagesAsRead(json);
	} else if (json.toUserId == currentUser.id) {
		// 不是当前聊天对象，增加未读计数
		incrementUnreadCount(json);
	}else if(json.toUserId === chatUser.id&&json.chatType==='2'){
		// 如果是群聊且是当前聊天对象 则设置消息为已读并滚动到底部
		setMessagesAsRead(json);
	}else if(json.chatType=='2' && json.fromUserId !== currentUser.id && json.toUserId !== currentUser.id && json.toUserId !== chatUser.id){
		// 如果是群聊且不是当前聊天对象 则增加未读计数
		incrementUnreadCount(json);
	}
}
// 修改或创建用户消息列表
const updateOrCreateUserVoList = (json) => {
	var userId = json.toUserId == currentUser.id ? json.fromUserId : json.fromUserId == currentUser.id ? json.toUserId : null;
	if(json.chatType==='2'){
		// 群聊
		userId = json.toUserId;
	}
	if (!userId) return;
	// //判断此条消息是否是撤回消息 
	if (json.isRecall == '1') {
		// 判断是否是最后一条消息 如果是则更新消息列表
		if (messageListMap[userId] && messageListMap[userId].length > 0 && messageListMap[userId][messageListMap[userId].length - 1].id == json.id) {
			ImMessageUserVoList.forEach(item => {
				if (item.userId == userId) {
					item.content = json.content;
				}
			});
		}
		return;
	}
	const index = ImMessageUserVoList.findIndex(item => item.userId == userId);
	// 是什么类型的信息
	let content = null;
	if (json.type != '1') {
		let itemJson = JSON.parse(json.content);
		content = imageSuffix.indexOf(itemJson.suffix) > -1 ? '[图片]' : '[文件1]' + itemJson.name;
	} else {
		content = json.content;
	}
	if (index === -1) {
		const message = {
			userId,
			content,
			createTime: json.createTime,
			unreadCount: 0
		}
		ImMessageUserVoList.push(message);
	} else {
		ImMessageUserVoList[index].content = content;
		ImMessageUserVoList[index].createTime = json.createTime;
	}


	//排序
	ImMessageUserVoList.sort((a, b) => {
		return new Date(b.createTime).getTime() - new Date(a.createTime).getTime();
	});
}

const updateMessageListMap = (json) => {
	var targetUserId = json.fromUserId == currentUser.id ? json.toUserId : json.fromUserId;
	if(json.chatType==='2'){
		targetUserId = json.toUserId;
	}
	if (!targetUserId) return;

	// 判断是否是撤回消息
	if (json.isRecall == '1') {
		messageListMap[targetUserId == chatUser.id ? chatUser.id : currentUser.id].forEach(item => {
			if (item.id == json.id) {
				item.content = json.content;
				item.isRecall = json.isRecall;
			}
		});
		return;
	}
	if (!messageListMap[targetUserId]) {
		messageListMap[targetUserId] = [];
	}
	messageListMap[targetUserId].push(json);
	scrollToBottomOnNextTick();
}

const setMessagesAsRead = (json) => {
	if (json.isRecall == '1') {
		return;
	}
	setRead([{ id: json.id }]);
	if(messageListMap[json.fromUserId]){
		messageListMap[json.fromUserId].forEach(item => {
		if (item.toUserId == currentUser.id && item.isRead == '2') {
			item.isRead = '1';
		}
	});
	scrollToBottomOnNextTick();
	}
}

const incrementUnreadCount = (json) => {
	if (json.isRecall == '1') {
		return;
	}
	ImMessageUserVoList.forEach(item => {
		if (item.userId == json.fromUserId && json.isRead == 2&&json.chatType=='1') {
			item.unreadCount += 1;
		}else if(item.userId == json.toUserId && json.isRead == 2&&json.chatType=='2'){
			item.unreadCount += 1;
		}
	});
}
// 滚动到底部
const scrollToBottomOnNextTick = () => {
	nextTick(() => {
		let scrollElem = messageContainer.value;
		if (scrollElem) {
			scrollElem.scrollTo({ top: scrollElem.scrollHeight, behavior: 'smooth' });
		}
	});
}

const switchClient = (client: string) => {
	userClient.value = client;
}

const handleOpen = () => {
	open.value = true;
}

// 初始化聊天列表
const initMessageList = () => {
	// 查询当前用户的所有聊天人员列表和最后一条消息
	imMessageApi.queryChatRecord(queryChatRecordParams.value).then(res => {
		ImMessageUserVoList.push(...res.records);
		queryChatRecordParams.total = res.total;
	});
}

// 通过用户id查询和当前用户的聊天记录
const selectMessageUser = (user: User) => {
	if (user.useType) {
		queryChatRecordWithUserParams.chatType = user.useType
	} else {
		queryChatRecordWithUserParams.chatType = '1'
	}
	// 将未读消息数置零
	resetUnreadCount(user.id);
	// 如果是当前对话用户，无需进一步操作
	if (user.id === chatUser.id) return;
	// 给当前聊天用户赋值
	initChatUser(user);
	// 需要对话的用户和当前对话用户不一致且对话用户没有聊天记录
	if (usersMap[user.id].current === -1) {
		initChatUserAndQueryRecords(user);
	} else {
		// 用户已有聊天记录，但不是当前聊天对象
		scrollToBottomAndInitChatUser(user);
	}
		// 置零前端的未读消息
		// markMessagesAsRead(user.id);
}

const resetUnreadCount = (userId) => {
	ImMessageUserVoList.forEach(item => {
		if (item.userId === userId) {
			item.unreadCount = 0;
		}
	});
}

const initChatUserAndQueryRecords = (user) => {
	queryChatRecordWithUserParams.current = 1;
	adjustQuerySizeBasedOnUnread(user.id);
	selectMessageList().then(res=>{
		markMessagesAsRead(user.id);
	})
}

const adjustQuerySizeBasedOnUnread = (userId) => {
	const user = ImMessageUserVoList.find(item => item.userId === userId);
	if (user && user.unreadCount > 0) {
		const paramSize = queryChatRecordParams.size;
		const size = Math.ceil(user.unreadCount / paramSize);
		queryChatRecordParams.size = size * paramSize;
	}
}

const scrollToBottomAndInitChatUser = (user) => {
	nextTick(() => {
		setTimeout(() => {
			messageContainer.value.scrollTop = messageContainer.value.scrollHeight;
		}, 100);
	});
	queryChatRecordWithUserParams.current = usersMap[user.id].current;
	markMessagesAsRead(user.id);
}

const markMessagesAsRead = (userId) => {
	if (messageListMap[userId] && messageListMap[userId].length > 0) {
		messageListMap[userId].forEach(item => {
			if ((item.toUserId === currentUser.id && item.isRead === '2') || (item.chatType == '2' && item.isRead === '2')) {
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
	chatUser.chatType = user.useType;
	queryChatRecordWithUserParams.userId = user.id;
}

// 查询消息记录
const selectMessageList = async () => {
	return new Promise((resolve, reject) => {
		if (!chatUser.id || queryChatRecordWithUserParams.userId == '') {
			notification.warning({
				message: '请选择聊天对象'
			})
		}
		messgaeScrollHeight.value = messageContainer.value.scrollHeight;
		imMessageApi.queryChatRecordWithUser(queryChatRecordWithUserParams).then(res => {
			if (!messageListMap[chatUser.id] || usersMap[chatUser.id].current == -1) {
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
			usersMap[chatUser.id].current = queryChatRecordWithUserParams.current;
			resolve();
		});
	});

}

const sendMessageByFile = (obj: Object) => {
	if (chatUser.id == '') {
		notification.warning({
			message: '请选择聊天对象'
		})
	}
	if (!obj) {
		notification.warning({
			message: '请选择文件'
		})
	}
	// 通过name 获取文件名 消息类型：1-文本，2-图片，3-视频，4-文件
	// name: "数据仓库工具箱  维度建模权威指南（第3版).pdf"
	const name = obj['name'];
	const suffix = name.substring(name.lastIndexOf('.') + 1);
	let type = '4';
	if (suffix == 'jpg' || suffix == 'png' || suffix == 'gif' || suffix == 'jpeg' || suffix == 'bmp' || suffix == 'ico') {
		type = '2';
	} else if (suffix == 'mp4' || suffix == 'avi' || suffix == 'mov' || suffix == 'rmvb') {
		type = '3';
	}
	// http://localhost:82/dev/file/download?id=1816742994645123073 取出id
	const content = obj['url'].split('=')[1];

	// 拼接消息
	const msg: ImMessageBo = {
		fromUserId: currentUser.id,
		toUserId: chatUser.id,
		content,
		chatType: '1',
		type,
		toUserType: userClient.value,
		fromUserType: '1'
	};
	// 发送消息
	websocket.Send(msg);
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
			chatType: chatUser.chatType ? chatUser.chatType : '1',
			type: '1',
			toUserType: userClient.value,
			fromUserType: '1'
		};
		// 发送消息
		websocket.Send(msg);
		newMessage.value = ''.trim();
	}
}
watch (
	() => newMessage.value,
	() => {
		if (newMessage.value=='\n') {
				newMessage.value = '';
		}
	}

)
// 初始化当前用户的好友列表
const getUserList = () => {
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
initGroupList();
</script>

<style scoped>
.record-img {
	width: 40px;
	height: 40px;
	float: left;
}

.large {
	font-size: large;
	margin-right: 7px;
}

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
	display: flex;
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
	word-break: normal;

}

.message-box-column {
	display: flex;
	flex-direction: column;
	width: auto;
	max-width: 90%;
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
	/* 垂直居中 */
	border-bottom: 1px solid #f0f0f0;
	background-color: #fafafa;
	justify-content: space-between;
	align-items: center;
}

.current-user-info {
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

.flex {
	display: flex;
}

.create-group {
	margin-left: 10px;
}
</style>
