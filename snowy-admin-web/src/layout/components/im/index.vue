<template>
	<div @click="handleOpen">
		<a-badge v-if="props.disPlayUi=='badge'" :dot="ImMessageUserVoList.map(res=> res.unreadCount).reduce((a, b) => a + b, 0) > 0" >
			<slot name="custom">
				<MessageOutlined />
			</slot>
		</a-badge>
		<a-float-button id="float-button" :type="props.floatBottonType" v-if="props.disPlayUi=='float'&&props.badge=='dot'" shape="circle" :badge="{ dot: ImMessageUserVoList.map(res=> res.unreadCount).reduce((a, b) => a + b, 0) > 0}" :style="props.floatStyle">
			<template #icon>
				<slot name="icon">
					<MessageOutlined />
				</slot>
			</template>
		</a-float-button>
		<a-float-button id="float-button" :type="props.floatBottonType" v-if="props.disPlayUi=='float'&&props.badge=='count'" shape="circle" :badge="{ count: ImMessageUserVoList.map(res=> res.unreadCount).reduce((a, b) => a + b, 0)}" :style="props.floatStyle">
			<template #icon>
				<slot name="icon">
					<MessageOutlined />
				</slot>
			</template>
		</a-float-button>
	</div>
	<a-modal
		v-model:open="open"
		:closable="false"
		:mask-closable="false"
		:style="{ width: 'auto', height: 'auto', maxWidth: '60vw', maxHeight: '90vh' }"
		:footer="null"
	>
		<template #title></template>
		<div class="xn-im-total-container" :style="{ padding: 0, height: 'calc(100vh - 200px)', overflow: 'hidden' }">
			<!-- 左侧 -->
			<div class="container-side">
				<a-popover trigger="click">
					<template #content>
						<div style="display: flex">
							<a-avatar shape="square" :src="currentUser.avatar" :size="64" />
							<a-descriptions bordered size="small" :column="1" style="margin-left: 10px">
								<a-descriptions-item label="姓名：">{{ currentUser.name }}</a-descriptions-item>
								<a-descriptions-item label="机构：">{{ currentUser.orgName }}</a-descriptions-item>
								<a-descriptions-item label="职位：">{{ currentUser.positionName }}</a-descriptions-item>
							</a-descriptions>
						</div>
					</template>
					<a-avatar shape="square" size="large" :src="currentUser.avatar" class="container-side-avatar" />
				</a-popover>
				<a-menu
					style="width: 65px"
					v-model:selectedKeys="leftSelectedKeys"
					mode="inline"
					theme="dark"
					:inline-collapsed="true"
					:items="leftMenuItems"
					@click="handleMenuClick"
				/>
			</div>
			<!-- 目录区 -->
			<div class="container-catalog">
				<div class="catalog-search">
					<a-select
						v-model:value="searchValue"
						show-search
						placeholder="搜索"
						:default-active-first-option="false"
						:show-arrow="false"
						:filter-option="false"
						:not-found-content="null"
						:options="searchData"
						@search="onSearch"
						@change="onHandleChangeSearch"
					></a-select>
				</div>
				<div class="catalog-content webkit-scrollbar" v-if="leftSelectedKeys == '1'" @scroll="scrolling">
					<div
						:class="['catalog-content-li', chatUser.id === item.id ? 'li-checked' : '']"
						:key="item.id"
						v-for="item in ImMessageUserVoList"
						@click="selectMessageUser(usersMap[item.userId + ''])"
					>
						<a-badge :count="item.unreadCount" class="catalog-content-li-avatar">
							<a-avatar
								shape="square"
								size="large"
								:src="usersMap[item.userId + ''].avatar"
							/>
						</a-badge>
						<a-badge status="success" v-if="onlineFunc(item.userId)" style="padding-top: 32px; margin-left: -8px" />
						<a-badge status="default" v-else style="padding-top: 32px; margin-left: -8px" />
						<div class="catalog-content-li-user">
							<div>
								<a-typography-text
									:style="{ width: lastMessageDate(item.createTime).length >= 6 ? '60%' : '80%' }"
									:ellipsis="{ tooltip: usersMap[item.userId + ''].name }"
									:content="usersMap[item.userId + ''].name"
								/>
								<span class="catalog-content-li-user-time" :style="{ width:lastMessageDate(item.createTime).length >= 6 ? '40%' : '20%' }">{{ lastMessageDate(item.createTime) }}</span>
							</div>
							<span class="catalog-content-li-user-last-msg" :style="{ width:'90%' }">{{ groupRecall(item.content) }}</span>
						</div>
					</div>
				</div>
				<div v-else>
					<a-tabs v-model:activeKey="activeKey" centered style="position: relative">
						<a-tab-pane key="1" tab="好友"></a-tab-pane>
						<a-tab-pane key="2" tab="群组"></a-tab-pane>
					</a-tabs>
					<div class="tab-person webkit-scrollbar" v-if="activeKey == '1'">
						<div
							:class="['catalog-content-li', chatUser.id === item.id ? 'li-checked' : '']"
							:key="item.id"
							v-for="item in users[userClient]"
							@click="selectMessageUser(item)"
						>
							<a-avatar shape="square" size="large" :src="item.avatar" class="catalog-content-li-avatar" />
							<a-badge status="success" v-if="onlineFunc(item.id)" style="padding-top: 32px; margin-left: -8px" />
							<a-badge status="default" v-else style="padding-top: 32px; margin-left: -8px" />
							<div class="catalog-content-li-user">
								<div>
									<span>{{ item.name }}</span>
								</div>
								<span class="catalog-content-li-user-last-msg">{{ item.account }}</span>
							</div>
						</div>
					</div>
					<div v-if="activeKey == '2'">
						<a-tooltip title="创建群组">
							<a-button type="primary" shape="circle" size="large" @click="createGroup" style="position: absolute; bottom: 20px; left: 230px;">
								<template #icon>
									<PlusOutlined />
								</template>
							</a-button>
						</a-tooltip>
						<div class="tab-person webkit-scrollbar-2">
							<div
								:class="['catalog-content-li', chatUser.id === item.id ? 'li-checked' : '']"
								:key="item.id"
								v-for="item in groupList"
								@click="selectMessageUser(item)"
							>
								<a-avatar shape="square" size="large" :src="item.avatar" class="catalog-content-li-avatar" />
								<div class="catalog-content-li-user">
									<div>
										<span>{{ item.name }}</span>
									</div>
									<span class="catalog-content-li-user-last-msg">{{ item.account }}</span>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<!-- 聊天区 -->
			<div class="container-content">
				<div class="content-header">
					<a-flex>
						<div class="header-title">
							<span class="header-title-font" v-if="chatUser.id">{{ chatUser.name }}</span>
						</div>
						<div class="header-close">
							<div v-if="chatUser.chatType === 2 || chatUser.chatType === '2'">
								<div class="header-close-icon">
									<SettingOutlined @click="updateGroup(chatUser.id)" />
								</div>
							</div>
							<div class="header-close-icon">
								<CloseOutlined @click="handleClose" />
							</div>
						</div>
					</a-flex>
				</div>
				<!-- 聊天内容区域 -->
				<div class="content-center" v-if="chatUser.id" style="display: flex; flex-direction: column">
					<div class="messages" ref="messageContainer" @scroll="messagesScrolling">
						<div v-if="messageListMap[chatUser.id] && messageListMap[chatUser.id].length != 0">
							<div v-for="message in messageListMap[chatUser.id]" :key="message.id">
								<div
									:class="{
										'my-message': message.fromUserId === currentUser.id,
										'other-message': message.fromUserId !== currentUser.id
									}"
									class="message-item"
									v-if="message.isRecall == '2'"
								>
									<a-avatar
										:src="usersMap[message.fromUserId]?.avatar || currentUser.avatar"
										shape="square"
										class="message"
									/>
									<div class="message-box-column">
										<div class="message-sender" :class="message.fromUserId === currentUser.id ? 'text-r' : 'text-l'">
											{{ usersMap[message.fromUserId]?.name || currentUser.name }}
											<span style="font-weight: 100"> &nbsp;{{ message.createTime }}</span>
										</div>
										<div class="message-content" v-if="message.type == '1'">
											<div class="box2" @contextmenu="onContextMenu($event, message)">
												<p class="message-text">{{ message.content }}</p>
											</div>
										</div>
										<div
											class="message-content"
											v-else
											@contextmenu="onContextMenu($event, message)"
											v-for="key in [JSON.parse(message.content)]"
										>
											<div
												class="box2"
												@click="onPreview(key)"
												:style="{ minWidth: imageSuffix.indexOf(key.suffix) == -1 ? '200px' : 'auto' }"
											>
												<a-image
													:width="80"
													:src="key.downloadPath"
													fallback="data:image/png;base64,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"
													v-if="imageSuffix.indexOf(key.suffix) > -1"
												/>
												<img
													src="@/assets/images/fileImg/docx.png"
													class="record-img"
													:title="previewDisplay(key.suffix)"
													v-else-if="key.suffix === 'doc' || key.suffix === 'docx'"
												/>
												<img
													src="@/assets/images/fileImg/xlsx.png"
													class="record-img"
													:title="previewDisplay(key.suffix)"
													v-else-if="key.suffix === 'xls' || key.suffix === 'xlsx'"
												/>
												<img
													src="@/assets/images/fileImg/zip.png"
													:title="previewDisplay(key.suffix)"
													class="record-img"
													v-else-if="key.suffix === 'zip'"
												/>
												<img
													src="@/assets/images/fileImg/rar.png"
													:title="previewDisplay(key.suffix)"
													class="record-img"
													v-else-if="key.suffix === 'rar'"
												/>
												<img
													src="@/assets/images/fileImg/ppt.png"
													class="record-img"
													:title="previewDisplay(key.suffix)"
													v-else-if="key.suffix === 'ppt' || key.suffix === 'pptx'"
												/>
												<img
													src="@/assets/images/fileImg/pdf.png"
													:title="previewDisplay(key.suffix)"
													class="record-img"
													v-else-if="key.suffix === 'pdf'"
												/>
												<img
													src="@/assets/images/fileImg/txt.png"
													:title="previewDisplay(key.suffix)"
													class="record-img"
													v-else-if="key.suffix === 'txt'"
												/>
												<img
													src="@/assets/images/fileImg/html.png"
													:title="previewDisplay(key.suffix)"
													class="record-img"
													v-else-if="key.suffix === 'html'"
												/>
												<img
													src="@/assets/images/fileImg/file.png"
													:title="previewDisplay(key.suffix)"
													class="record-img"
													v-else
												/>
											</div>
											<p v-if="imageSuffix.indexOf(key.suffix) == -1">{{ key.name }}</p>
										</div>
									</div>
								</div>
								<div v-else style="color: gray; font-size: x-small; display: flex; justify-content: center">
									<p>{{ groupRecall(message.content) }}</p>
								</div>
							</div>
						</div>
					</div>
					<div class="message-input">
						<FileImageOutlined class="large" @click="uploadImage('图片', 'image')" />
						<FolderOutlined class="large" @click="uploadImage('文件', 'drag')" />
						<AudioOutlined class="large" @click="startVoiceCall()" />
						<VideoCameraOutlined class="large" @click="startVideoCall()" />
						<a-textarea
							v-model:value="newMessage"
							@keydown.enter="handleEnterKey"
							:bordered="false"
							:placeholder="
								cancelSilenceTime(groupMutedList[chatUser.id])
									? '输入消息...'
									: '您已被禁言，剩余时间' + cancelSilenceDateTime + '分钟解除禁言'
							"
							:auto-size="{ minRows: 4, maxRows: 8 }"
							:disabled="!cancelSilenceTime(groupMutedList[chatUser.id])"
						/>
						<div class="send-msg-bto">
							<a-button
								@click="sendMessage"
								type="primary"
								:disabled="!chatUser.id || !cancelSilenceTime(groupMutedList[chatUser.id])"
								>发送</a-button
							>
						</div>
					</div>
				</div>

				<!-- 未命中任何聊天 -->
				<div class="content-center-miss" v-else>
					<img src="./image/content.png" class="content-center-miss-image" />
					<span style="font-weight: 600">打开系统的另一扇窗</span>
					<span style="color: #bbbbbb">主动一点，工作会更轻松！</span>
				</div>
			</div>
		</div>
	</a-modal>

	<a-modal v-model:open="uploadShow" :title="'发送' + uploadTitle" @ok="handleOk">
		<xn-im-upload v-if="uploadShow" uploadResultType="id" :uploadMode="uploadMode" ref="uploadImageRef" :uri="config.API_URL"/>
	</a-modal>
	<a-modal v-model:open="previewShow" title="预览文件" :width="1200" style="top: 10px">
		<xn-im-file-preview v-if="previewShow" :src="previewSrc" :file-type="previewFileType" @goBack="previewBack" />
		<template #footer />
	</a-modal>
	<a-modal
		v-model:open="createGroupShow"
		style="top: 50px"
		:title="createGroupType == 'add' ? '创建群组' : '操作群组'"
		:width="600"
		:mask="false"
	>
		<xn-im-edit-group
			@updateGroupInfo="updateGroupInfoData"
			:createGroupType="createGroupType"
			:id="updateGroupId"
			@closeGroupShow="closeGroupShow"
			@restChatUser="restChatUser"
			v-if="createGroupShow"
			ref="createGroupRef"
			:onlineUserList="onlineUserList"
			:baseRequest = "props.baseRequest"
		/>
		<template #footer />
	</a-modal>
	<xn-im-web-socket @setWebSocket="setWebSocket" :uri="config.API_URL"/>
	<a-modal
    v-model:open="callModalVisible"
    :title="callType === 'voice' ? '语音通话' : '视频通话'"
    :closable="false"
    :mask-closable="false"
    :footer="null"
    width="400px"
  >
    <div class="call-container">
			<div id="call-container-main">
				<div v-if="callStatus === 'calling'" class="calling-status">
					<a-avatar :size="64" :src="chatUser.avatar" />
					<p>正在呼叫 {{ chatUser.name }}...</p>
					<a-space>
						<a-button type="primary" danger @click="endCall">取消</a-button>
					</a-space>
				</div>
				
				<div v-if="callStatus === 'incoming'" class="incoming-status">
					<a-avatar :size="64" :src="usersMap[incomingCall.fromUserId]?.avatar" />
					<p>来自 {{ usersMap[incomingCall.fromUserId]?.name }} 的{{ callType === 'voice' ? '语音' : '视频' }}通话</p>
					<a-space>
						<a-button type="primary" @click="acceptCall">接听</a-button>
						<a-button type="primary" danger @click="rejectCall">拒绝</a-button>
					</a-space>
				</div>

				<div v-if="callStatus === 'connected'" class="connected-status">
					<div class="group-video-grid" v-if="chatUser.chatType === '2'">
						<div v-for="[userId, stream] in groupCallStreams" :key="userId" class="video-item">
							<video :srcObject="stream" autoplay class="remote-video"></video>
							<div class="user-name">{{ usersMap[userId]?.name }}</div>
						</div>
						<div class="video-item">
							<video :srcObject="localStream" autoplay muted class="local-video"></video>
							<div class="user-name">我</div>
						</div>
					</div>
					<div v-if="callType === 'video'" class="call-content">
						<video id="remoteVideo" class="remote-video" autoplay playsinline muted="false"></video>
						<video v-if="localStream" :srcObject="localStream" class="local-video" autoplay playsinline muted></video>
					</div>
					<!-- <video v-if="callType === 'video'" id="localVideo" ref="localVideo" autoplay playsinline muted class="local-video"></video> -->
					<!-- <video v-if="callType === 'video'" id="remoteVideo" ref="remoteVideo" autoplay playsinline class="remote-video"></video> -->
					<audio id="remoteAudio" ref="remoteAudioRef" autoplay playsinline controls style="width:100%; display:block; margin:10px 0;"></audio>
					<p>通话时长: {{ callDuration }}</p>
					<a-button type="primary" danger @click="endCall">结束通话</a-button>
				</div>
			</div>
		</div>
  </a-modal>
</template>

<script setup lang="ts">
	import { ref, reactive, onMounted, watch, nextTick, defineProps, h } from 'vue'
	import { notification } from 'ant-design-vue'
	import { MessageOutlined, TeamOutlined, SettingOutlined,AudioOutlined, VideoCameraOutlined } from '@ant-design/icons-vue'
	import '@imengyu/vue3-context-menu/lib/vue3-context-menu.css'
	import ContextMenu from '@imengyu/vue3-context-menu'
	import dayjs from 'dayjs'
	import tool from '@/utils/tool'
	import imSysUserApi from './api/imSysUserApi'
	import imMessageApi from './api/imMessageApi'
	import imGroupMemberApi from './api/imGroupMemberApi'
	import imGroupApi from './api/imGroupApi'
	import { User, Message, ImMessageUserVo, ImMessageBo, ImGroupVo } from './type/type'
	import XnImUpload from './components/XnImUpload/index.vue'
	import XnImWebSocket from './components/XnImWebSocket/index.vue'
	import XnImFilePreview from './components/XnImFilePreview/index.vue'
	import XnImEditGroup from './components/XnImEditGroup/index.vue'

	const props = defineProps({
		baseRequest: {
			type: Function,
			default: () => undefined
		},
		disPlayUi:{
			type: String,
			default:'badge' //float
		},
		floatStyle:{
			type: Object,
			default:()=>({ right: '124px',bottom:'100px'}) //displayUi为float时生效
		},
		badge:{
			type: String,
			default: 'dot' //count
		},
		config:{
			type: Object,
			default:()=>({})
		},
		floatBottonType:{
			type: String,
			default: '' //primary or ''
		}
	})

	const websocket = ref(null)
	const leftSelectedKeys = ref(['1'])
	const leftMenuItems = ref([
		{
			key: '1',
			icon: () => h(MessageOutlined),
			label: '聊天',
			title: '聊天'
		},
		{
			key: '2',
			icon: () => h(TeamOutlined),
			label: '通讯录',
			title: '通讯录'
		}
	])
	// 聊天记录参数
	const queryChatRecordWithUserParams = reactive({
		current: 1,
		size: 10,
		userId: ''
	})
	//聊天界面用户列表参数
	const queryChatRecordParams = reactive({
		current: 1,
		size: 30,
		total: -1
	})

	const messageContainer = ref(null)
	// B端用户1 C端用户2
	const userClient = ref('1')
	const open = ref(false)
	const activeKey = ref('1')
	// 当前聊天用户
	const chatUser = reactive<User>({ id: '', name: '', avatar: '' })
	// 当前用户
	const currentUser = reactive<User>({ id: '', name: '', avatar: '' })
	// 聊天页面用户列表
	const ImMessageUserVoList = reactive<ImMessageUserVo[]>([])
	// 通过客户端类型区分用户列表
	const users = reactive<Record<string, User[]>>({
		'1': [],
		'2': []
	})
	// 全量用户列表
	const usersMap = reactive<Record<string, User>>({})
	// 群组列表
	const groupList = reactive<ImGroupVo[]>([])
	// 消息列表
	const messageListMap = reactive<Record<string, Message[]>>({})
	// 在线用户列表
	const onlineUserList = reactive<string[]>([])
	// 发送的消息
	const newMessage = ref<string>('')
	const messgaeScrollHeight = ref(0)

	// 右键菜单
	const menuData = reactive({
		theme: 'mac',
		items: [{}],
		iconFontClass: 'iconfont',
		customClass: 'class-a',
		zIndex: 9999,
		minWidth: 100,
		x: 0,
		y: 0
	})

	// 发送文件预览文件变量
	const uploadMode = ref('image')
	const uploadTitle = ref('')
	const imageSuffix = ['png', 'jpg', 'jpeg', 'ico', 'bmp', 'gif']

	const previewSrc = ref()
	const previewShow = ref(false)
	const previewFileType = ref()

	const uploadShow = ref(false)
	const uploadImageRef = ref(null)

	// 处理群组弹窗
	const createGroupShow = ref(false)
	const createGroupRef = ref(null)
	const createGroupType = ref('add')
	const updateGroupId = ref(null)

	// 禁言列表
	const groupMutedList = reactive<Record<string, Date>>({})
	const cancelSilenceDateTime = ref(0)
	// 禁言定时器
	const timer = reactive<Record<string, any>>({})
	// 搜索
	const searchValue = ref()
	const searchData = ref<any[]>([])
	// 通话所使用的变量信息
	const callModalVisible = ref(false)
	const callType = ref<'voice' | 'video'>('voice')
	const callStatus = ref<'calling' | 'incoming' | 'connected' | null>(null)
	const localStream = ref<MediaStream | null>(null)
	const remoteStream = ref<MediaStream | null>(null)
	const peerConnection = ref<RTCPeerConnection | null>(null)
	const callDuration = ref('00:00')
	const callTimer = ref<NodeJS.Timer | null>(null)
	const incomingCall = reactive({
		fromUserId: '',
		type: ''
	})
	const iceServers = [
		{ urls: 'stun:stun.l.google.com:19302' },
		{ urls: 'stun:stun1.l.google.com:19302' },
		{ urls: 'stun:stun2.l.google.com:19302' },
		{ urls: 'stun:stun3.l.google.com:19302' },
		{ urls: 'stun:stun4.l.google.com:19302' }
	]
	// 在现有变量声明后添加
	const groupCallPeers = ref<Map<string, RTCPeerConnection>>(new Map())
	const groupCallStreams = ref<Map<string, MediaStream>>(new Map())
	const groupCallParticipants = ref<string[]>([])
	const remoteAudioRef = ref<HTMLAudioElement | null>(null)

	onMounted(() => {
		initMessageList()
		initGroupMemberMuted()
		if(props.floatStyle.backgroundColor != '' && props.floatStyle.backgroundColor != undefined){
			var floatButton = document.getElementById("float-button")
			var boxes = floatButton.getElementsByClassName('ant-float-btn-body');
			for (var i = 0; i < boxes.length; i++) {
				boxes[0].style.backgroundColor = props.floatStyle.backgroundColor;
			}
		}
	})

	watch(
		() => [newMessage.value, chatUser.id],
		([nNewMessage, chatUserId], [oldNewMessage, oldChatUserId]) => {
			if (nNewMessage == '\n') {
				newMessage.value = ''
			}
			if (chatUserId) {
				if (chatUser.chatType == '2') {
					timer[chatUserId] = setInterval(() => {
						if (groupMutedList[chatUserId]) {
							cancelSilenceDateTime.value = parseInt(
								(new Date(groupMutedList[chatUserId]).getTime() - new Date().getTime()) / 60000
							)
						}
					}, 1000)
				}
			}
			if (oldChatUserId) {
				clearInterval(timer[oldChatUserId])
			}
		}
	)

	const lastMessageDate = (date) => {
		// 格式化时间 如果是今天的只显示时间 如果是昨天的显示昨天 如果是昨天之前的显示日期
		if (dayjs().format('YYYY-MM-DD') == dayjs(date).format('YYYY-MM-DD')) {
			return dayjs(date).format('HH:mm')
		} else if (dayjs().subtract(1, 'day').format('YYYY-MM-DD') == dayjs(date).format('YYYY-MM-DD')) {
			return '昨天'
		} else {
			return dayjs(date).format('YYYY-MM-DD')
		}
	}
	const handleMenuClick = (e) => {
		leftSelectedKeys.value = e.key
	}
	const handleClose = () => {
		open.value = false
	}

	const onlineFunc = (data) => {
		return onlineUserList.includes(data) > 0
	}

	const onSearch = (val: string, callback: any) => {
		let data = []
		Object.keys(usersMap).forEach((item, index) => {
			if (usersMap[item].name.indexOf(val) != -1) {
				data.push({ value: item, label: usersMap[item].name })
			}
		})
		setSearchData(data)
	}

	const setSearchData = (val: any[]) => {
		searchData.value = val
		if (val.length == 0) {
			searchValue.value = null
		}
	}

	const onHandleChangeSearch = (val: string) => {
		searchValue.value = val
		// 点击调用selectMessageUsers方法
		selectMessageUser(usersMap[val], usersMap[val].userType && usersMap[val].userType != 1)
		setSearchData([])
	}
	const setWebSocket = (ws) => {
		websocket.value = ws
		if (ws) {
			ws.setMessageCallback(onMessage)
		}
	}

	// 判断是否可以发送消息
	const cancelSilenceTime = (time) => {
		return !time || new Date().getTime() > new Date(time).getTime()
	}

	//初始化当前用户所在的群聊是否被禁言列表
	const initGroupMemberMuted = () => {
		imGroupMemberApi.imGroupMemberMuteList(props.baseRequest,{}).then((res) => {
			res.forEach((item) => {
				groupMutedList[item.groupId] = item.silenceTime
			})
		})
	}

	const restChatUser = () => {
		// 删除列表中的群组 并且删除用户列表中的群组
		delete usersMap[updateGroupId.value]
		groupList.forEach((item, index) => {
			if (item.id == updateGroupId.value) {
				groupList.splice(index, 1)
			}
		})
		delete messageListMap[updateGroupId.value]
		chatUser.id = ''
		chatUser.name = ''
		chatUser.avatar = ''
		chatUser.chatType = ''
		// 关闭弹窗
		closeGroupShow()
	}

	// 更新群组信息 如果修改的话
	const updateGroupInfoData = (e) => {
		closeGroupShow()
		nextTick(() => {
			if (e.type == 'add') {
				initGroupList()
				return
			}
			usersMap[e.id].name = e.name
			usersMap[e.id].avatar = e.avatar
			groupList.forEach((element) => {
				if (element.id == e.id) {
					element.name = e.name
					element.avatar = e.avatar
				}
			})
			chatUser.name = e.name
			chatUser.avatar = e.avatar
		})
	}

	// 群组撤回翻译
	const groupRecall = (msg: string) => {
		if (msg.indexOf('%s') != -1) {
			let msgValue = msg.split(',')
			let user = usersMap[msgValue[1]]
			user = user ? user : { name: '未知' }
			return msgValue[0].replace('%s', user.name)
		} else {
			return msg
		}
	}

	// 初始化群组
	const initGroupList = () => {
		groupList.splice(0, groupList.length)
		imGroupApi.imGroupListByUser(props.baseRequest,{}).then((res) => {
			res.forEach((element) => {
				element.userType = 2
				groupList.push(element)
				element.current = -1
				usersMap[element.id] = element
			})
		})
	}

	const updateGroup = (id: string) => {
		createGroupShow.value = true
		createGroupType.value = 'update'
		updateGroupId.value = id
	}

	const createGroup = () => {
		createGroupType.value = 'add'
		createGroupShow.value = true
	}

	const closeGroupShow = () => {
		createGroupShow.value = false
	}

	const previewBack = () => {
		previewShow.value = false
	}

	const onPreview = (obj: Object) => {
		if (imageSuffix.indexOf(obj['suffix']) > -1) {
			return
		}
		previewShow.value = true
		nextTick(() => {
			previewSrc.value = obj['downloadPath']
			previewFileType.value = obj['suffix']
		})
	}

	// 复制文本
	const copyToClipboard = (textToCopy) => {
	if (navigator.clipboard && window.isSecureContext) {
		return navigator.clipboard.writeText(textToCopy);
	} else {
		let input = document.createElement('input')
		input.style.position = 'fixed'
		input.style.top = '-10000px'
		input.style.zIndex = '-999'
		document.body.appendChild(input)
		input.value = textToCopy
		input.focus()
		input.select()
		let result = document.execCommand('copy')
		document.body.removeChild(input)
		if (!result || result === 'unsuccessful') {
			notification.error({
				message: '复制失败'
			})
		} else {
			notification.success({
				message: '复制成功'
			})
		}
	}
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

	const uploadImage = (title: string, mode: string) => {
		uploadMode.value = mode
		uploadShow.value = true
		uploadTitle.value = title
	}

	const handleOk = () => {
		uploadShow.value = false
		let obj = uploadImageRef.value.uploadFileList()[0]
		if (obj['url']) {
			sendMessageByFile(obj)
		}
	}

	const onContextMenu = (e: MouseEvent, msg: Message) => {
		e.preventDefault()
		e.stopPropagation()
		menuData.x = e.x
		menuData.y = e.y
		ContextMenu.showContextMenu(menuData)
		menuData.items = []
		let copy = {
				label: '复制',
				onClick: () => {
					if (msg.type != '1') {
						notification.warning({
							message: '暂不支持复制文件'
						})
						return
					} else {
						copyToClipboard(msg.content)
					}
				}
			}
		menuData.items.push(copy)
		reCallMeun(msg)
	}

	// 撤回消息方法
	const reCallMeun = (msg: Message) => {
		// 数组中是否存在撤回 且是当前用户发送的消息 且两分钟之内的数据
		if (
			menuData.items.findIndex((item) => item.label == '撤回') == -1 &&
			msg.fromUserId == currentUser.id &&
			new Date().getTime() - new Date(msg.createTime).getTime() < 120000
		) {
			let call = {
				label: '撤回',
				onClick: () => {
					//调用撤回接口
					imMessageApi.recallMessage(props.baseRequest,{ id: msg.id }).then((res) => {
						notification.success({
							message: '撤回成功'
						})
					})
				}
			}
			menuData.items.push(call)
		}
	}

	// 监听ref 滚动到底部
	const scrolling = (e) => {
		const {clientHeight, scrollHeight, scrollTop} = e.target
		if (Math.ceil(scrollTop) + clientHeight >= scrollHeight) {
			if (
				queryChatRecordParams.total != -1 &&
				queryChatRecordParams.current * queryChatRecordParams.size >= queryChatRecordParams.total
			) {
				return
			}
			queryChatRecordParams.current += 1
			initMessageList()
		}
	}

	//监听消息列表
	const messagesScrolling = (e) => {
		const scrollTop = e.target.scrollTop
		// 加载以前的消息 处理数据错乱问题
		if (scrollTop == 0 && usersMap[chatUser.id].total > 0) {
			if (
				queryChatRecordWithUserParams.current * queryChatRecordWithUserParams.size >= usersMap[chatUser.id].total ||
				usersMap[chatUser.id].total == 0
			) {
				return
			}
			queryChatRecordWithUserParams.current += 1
			usersMap[chatUser.id].current = queryChatRecordWithUserParams.current
			selectMessageList()
		}
	}

	const onMessage = (data) => {
		let json = JSON.parse(data)
		// 处理通话相关消息
		if (json.type && json.type.startsWith('call_')) {
			handleCallMessage(json)
			return
		}
		//messageType 3 在线用户列表  4用户上线通知 5用户离线通知
		if (json.messageType && (json.messageType == '3' || json.messageType == '4' || json.messageType == '5')) {
			if (json.messageType == '3') {
				onlineUserList.splice(0, onlineUserList.length)
			}
			if (json.messageType == '5') {
				onlineUserList.splice(
					onlineUserList.findIndex((item) => item == json.unOnlineUser),
					1
				)
			}
			if (json.onlineUserList && json.onlineUserList.length > 0) {
				onlineUserList.push(...json.onlineUserList)
			}
			return
		}

		// messageType 0 message消息  1 群组禁言(因为不在messgae中所以单独处理) 2解除禁言
		if (json.messageType && json.messageType == '1') {
			// 群组禁言 当前人的那个群 被禁言了多长时间
			groupMutedList[json.groupId] = new Date(json.silenceTime)
			return
		} else if (json.messageType && json.messageType == '2') {
			// 解除禁言
			delete groupMutedList[json.groupId]
			return
		}
		if (!json.fromUserId) return
		// 格式化时间
		if (typeof json.createTime === 'string' || typeof json.createTime === 'number') {
			json.createTime = dayjs(json.createTime).format('YYYY-MM-DD HH:mm:ss')
		}

		if (json.authStatus && json.authStatus == -1) {
			notification.error({
				message: 'IM模块未授权！！！'
			})
		}

		// 更新或添加消息到ImMessageUserVoList
		updateOrCreateUserVoList(json)

		// 更新消息列表
		updateMessageListMap(json)

		// 如果是当前聊天对象，设置消息为已读并滚动到底部
		if (json.toUserId === currentUser.id && json.fromUserId === chatUser.id) {
			setMessagesAsRead(json)
		} else if (json.toUserId == currentUser.id) {
			// 不是当前聊天对象，增加未读计数
			incrementUnreadCount(json)
		} else if (json.toUserId === chatUser.id && json.chatType === '2') {
			// 如果是群聊且是当前聊天对象 则设置消息为已读并滚动到底部
			setMessagesAsRead(json)
		} else if (
			json.chatType == '2' &&
			json.fromUserId !== currentUser.id &&
			json.toUserId !== currentUser.id &&
			json.toUserId !== chatUser.id
		) {
			// 如果是群聊且不是当前聊天对象 则增加未读计数
			incrementUnreadCount(json)
		}
	}

	// 添加通话消息处理函数
	const handleCallMessage = async (message) => {
	  console.log('收到通话消息:', message);
		switch (message.type) {
			case 'call_offer':
				// 收到呼叫请求
				// callType.value = message.callType
				incomingCall.fromUserId = message.fromUserId
				incomingCall.sdp = message.sdp
				callModalVisible.value = true
				callStatus.value = 'incoming'
				break
				
			case 'call_answer':
				console.log('收到通话应答');
				if (peerConnection.value && peerConnection.value.signalingState !== 'closed') {
					peerConnection.value.setRemoteDescription(new RTCSessionDescription(message.sdp))
						.then(() => {
							console.log('成功设置远程描述');
							callStatus.value = 'connected';
						})
						.catch(error => {
							console.error('设置远程描述失败:', error);
							notification.error({
								message: '连接失败',
								description: '无法建立媒体连接'
							});
							endCall();
						});
				}
				break
				
			case 'call_ice_candidate':
				// 处理 ICE 候选
				console.log('收到ICE候选');
				if (peerConnection.value && peerConnection.value.signalingState !== 'closed') {
					try {
						peerConnection.value.addIceCandidate(new RTCIceCandidate(message.candidate))
							.then(() => console.log('成功添加ICE候选'))
							.catch(e => console.error('添加ICE候选失败:', e));
					} catch (e) {
						console.error('处理ICE候选异常:', e);
					}
				}
				break
				
			case 'call_reject':
				// 对方拒绝通话
				notification.warning({
					message: `${usersMap[message.fromUserId]?.name || '对方'}拒绝了通话`
				})
				endCall()
				break
				
			case 'call_end':
				// 对方结束通话
				if (callStatus.value === 'connected') {
					notification.info({
						message: '通话已结束'
					})
				}
				endCall()
				break

			case 'call_group_invite':
				// 收到群组通话邀请
				// callType.value = message.callType
				incomingCall.fromUserId = message.fromUserId
				incomingCall.groupId = message.groupId
				callModalVisible.value = true
				callStatus.value = 'incoming'
				break
			case 'call_group_accept':
				// 群组成员接受通话
				if (message.fromUserId !== currentUser.id) {
					const peerConnection = groupCallPeers.value.get(message.fromUserId)
					if (peerConnection) {
						const offer = await peerConnection.createOffer()
						await peerConnection.setLocalDescription(offer)
						sendMessageToWebSocket({
							type: 'call_group_offer',
							fromUserId: currentUser.id,
							toUserId: message.fromUserId,
							groupId: message.groupId,
							sdp: offer
						})
					}
				}
				break
				
			case 'call_group_offer':
				// 处理群组通话提议
				if (message.toUserId === currentUser.id) {
					const peerConnection = new RTCPeerConnection({ iceServers })
					groupCallPeers.value.set(message.fromUserId, peerConnection)
					
					await peerConnection.setRemoteDescription(new RTCSessionDescription(message.sdp))
					const answer = await peerConnection.createAnswer()
					await peerConnection.setLocalDescription(answer)
					
					sendMessageToWebSocket({
						type: 'call_group_answer',
						fromUserId: currentUser.id,
						toUserId: message.fromUserId,
						groupId: message.groupId,
						sdp: answer
					})
				}
				break
		}
	}
	// 修改或创建用户消息列表
	const updateOrCreateUserVoList = (json) => {
		var userId =
			json.toUserId == currentUser.id ? json.fromUserId : json.fromUserId == currentUser.id ? json.toUserId : null
		if (json.chatType === '2') {
			// 群聊
			userId = json.toUserId
		}
		if (!userId) return
		// //判断此条消息是否是撤回消息
		if (json.isRecall == '1') {
			// 判断是否是最后一条消息 如果是则更新消息列表
			if (
				messageListMap[userId] &&
				messageListMap[userId].length > 0 &&
				messageListMap[userId][messageListMap[userId].length - 1].id == json.id
			) {
				ImMessageUserVoList.forEach((item) => {
					if (item.userId == userId) {
						item.content = json.content
					}
				})
			}
			return
		}
		const index = ImMessageUserVoList.findIndex((item) => item.userId == userId)
		// 是什么类型的信息
		let content = null
		if (json.type != '1') {
			let itemJson = JSON.parse(json.content)
			content = imageSuffix.indexOf(itemJson.suffix) > -1 ? '【图片】' : '【文件】' + itemJson.name
		} else {
			content = json.content
		}
		if (index === -1) {
			const message = {
				userId,
				content,
				createTime: json.createTime,
				unreadCount: 0
			}
			ImMessageUserVoList.push(message)
		} else {
			ImMessageUserVoList[index].content = content
			ImMessageUserVoList[index].createTime = json.createTime
		}

		//排序
		ImMessageUserVoList.sort((a, b) => {
			return new Date(b.createTime).getTime() - new Date(a.createTime).getTime()
		})
	}

	const updateMessageListMap = (json) => {
		var targetUserId = json.fromUserId == currentUser.id ? json.toUserId : json.fromUserId
		if (json.chatType === '2') {
			targetUserId = json.toUserId
		}
		if (!targetUserId) return

		// 判断是否是撤回消息
		if (json.isRecall == '1') {
			messageListMap[targetUserId == chatUser.id ? chatUser.id : currentUser.id].forEach((item) => {
				if (item.id == json.id) {
					item.content = json.content
					item.isRecall = json.isRecall
				}
			})
			return
		}
		if (!messageListMap[targetUserId]) {
			messageListMap[targetUserId] = []
		}
		messageListMap[targetUserId].push(json)
		scrollToBottomOnNextTick()
	}

	const setMessagesAsRead = (json) => {
		if (json.isRecall == '1') {
			return
		}
		setRead([{ id: json.id }])
		if (messageListMap[json.fromUserId]) {
			messageListMap[json.fromUserId].forEach((item) => {
				if (item.toUserId == currentUser.id && item.isRead == '2') {
					item.isRead = '1'
				}
			})
			scrollToBottomOnNextTick()
		}
	}

	const incrementUnreadCount = (json) => {
		if (json.isRecall == '1') {
			return
		}
		ImMessageUserVoList.forEach((item) => {
			if (item.userId == json.fromUserId && json.isRead == 2 && json.chatType == '1') {
				item.unreadCount += 1
			} else if (item.userId == json.toUserId && json.isRead == 2 && json.chatType == '2') {
				item.unreadCount += 1
			}
		})
	}
	// 滚动到底部
	const scrollToBottomOnNextTick = () => {
		nextTick(() => {
			let scrollElem = messageContainer.value
			if (scrollElem) {
				scrollElem.scrollTo({ top: scrollElem.scrollHeight, behavior: 'smooth' })
			}
		})
	}

	const handleOpen = () => {
		open.value = true
	}

	// 初始化聊天列表
	const initMessageList = () => {
		// 查询当前用户的所有聊天人员列表和最后一条消息
		imMessageApi.queryChatRecord(props.baseRequest,queryChatRecordParams).then((res) => {
			ImMessageUserVoList.push(...res.records)
			queryChatRecordParams.total = res.total
		})
	}

	const checkGroupRole = (user: User) => {
		imGroupMemberApi.imGroupMemberPage(props.baseRequest,{ groupId: user.id, userId: currentUser.id }).then((res) => {
			if (res.records.length == 0) {
				notification.warning({
					message: '您不是该群组成员'
				})
				return
			} else {
				if (res.records[0].role == '1') {
					createGroupType.value = 'update'
				} else {
					createGroupType.value = 'details'
				}
			}
		})
	}
	// 通过用户id查询和当前用户的聊天记录
	const selectMessageUser = (user: User, isGroup = false) => {
		// 判断当前是否是群聊
		if (isGroup) {
			checkGroupRole(user)
		}
		if (user.userType) {
			queryChatRecordWithUserParams.chatType = user.userType + ''
		} else {
			queryChatRecordWithUserParams.chatType = '1'
		}
		// 将未读消息数置零
		resetUnreadCount(user.id)
		// 如果是当前对话用户，无需进一步操作
		if (user.id === chatUser.id) return
		// 给当前聊天用户赋值
		initChatUser(user)
		// 需要对话的用户和当前对话用户不一致且对话用户没有聊天记录
		if (usersMap[user.id].current === -1) {
			initChatUserAndQueryRecords(user)
		} else {
			// 用户已有聊天记录，但不是当前聊天对象
			scrollToBottomAndInitChatUser(user)
		}
		// 置零前端的未读消息
		// markMessagesAsRead(user.id);
	}

	const resetUnreadCount = (userId) => {
		ImMessageUserVoList.forEach((item) => {
			if (item.userId === userId) {
				item.unreadCount = 0
			}
		})
	}

	const initChatUserAndQueryRecords = (user) => {
		queryChatRecordWithUserParams.current = 1
		adjustQuerySizeBasedOnUnread(user.id)
		selectMessageList().then((res) => {
			markMessagesAsRead(user.id)
		})
	}

	const adjustQuerySizeBasedOnUnread = (userId) => {
		const user = ImMessageUserVoList.find((item) => item.userId === userId)
		if (user && user.unreadCount > 0) {
			const paramSize = queryChatRecordParams.size
			const size = Math.ceil(user.unreadCount / paramSize)
			queryChatRecordParams.size = size * paramSize
		}
	}

	const scrollToBottomAndInitChatUser = (user) => {
		nextTick(() => {
			messageContainer.value.scrollTop = messageContainer.value.scrollHeight
		})
		queryChatRecordWithUserParams.current = usersMap[user.id].current
		markMessagesAsRead(user.id)
	}

	const markMessagesAsRead = (userId) => {
		if (messageListMap[userId] && messageListMap[userId].length > 0) {
			messageListMap[userId].forEach((item) => {
				if (
					(item.toUserId === currentUser.id && item.isRead === '2') ||
					(item.chatType == '2' && item.isRead === '2')
				) {
					item.isRead = '1'
					setRead([{ id: item.id }])
				}
			})
		}
	}
	// 给当前聊天用户赋值
	const initChatUser = (user: User) => {
		chatUser.id = user.id
		chatUser.name = user.name
		chatUser.avatar = user.avatar
		chatUser.chatType = user.userType
		queryChatRecordWithUserParams.userId = user.id
	}

	// 查询消息记录
	const selectMessageList = async () => {
		return new Promise((resolve, reject) => {
			if (!chatUser.id || queryChatRecordWithUserParams.userId == '') {
				notification.warning({
					message: '请选择聊天对象'
				})
			}
			if (messageContainer.value) {
				messgaeScrollHeight.value = messageContainer.value.scrollHeight
			} else {
				messgaeScrollHeight.value = 0
			}
			imMessageApi.queryChatRecordWithUser(props.baseRequest,queryChatRecordWithUserParams).then((res) => {
				if (!messageListMap[chatUser.id] || usersMap[chatUser.id].current == -1) {
					messageListMap[chatUser.id] = []
				}
				messageListMap[chatUser.id].unshift(...res.records)
				usersMap[chatUser.id].total = res.total
				if (queryChatRecordWithUserParams.current == 1) {
					scrollToBottomOnNextTick()
				} else {
					nextTick(() => {
						let scrollHeight = messageContainer.value.scrollHeight
						messageContainer.value.scrollTo({ top: scrollHeight - messgaeScrollHeight.value, behavior: 'auto' })
					})
				}
				usersMap[chatUser.id].current = queryChatRecordWithUserParams.current
				resolve()
			})
		})
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
		const name = obj['name']
		const suffix = name.substring(name.lastIndexOf('.') + 1)
		let type = '4'
		if (
			suffix == 'jpg' ||
			suffix == 'png' ||
			suffix == 'gif' ||
			suffix == 'jpeg' ||
			suffix == 'bmp' ||
			suffix == 'ico'
		) {
			type = '2'
		} else if (suffix == 'mp4' || suffix == 'avi' || suffix == 'mov' || suffix == 'rmvb') {
			type = '3'
		}
		// 拼接消息
		const msg: ImMessageBo = {
			fromUserId: currentUser.id,
			toUserId: chatUser.id,
			content: obj['url'],
			chatType: chatUser.chatType ? chatUser.chatType : '1',
			type,
			toUserType: userClient.value,
			fromUserType: '1'
		}
		// 发送消息
		sendMessageToWebSocket(msg)
	}

	const handleEnterKey = (e) => {
		if (e.ctrlKey && e.keyCode == 13) {
			newMessage.value += '\n'
		} else {
			sendMessage()
		}
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
			}
			sendMessageToWebSocket(msg)
			newMessage.value = ''.trim()
		}
	}

	const sendMessageToWebSocket = (msg) => {
		// 发送消息
		if(websocket.value){
			websocket.value.sendWebSocketMessage(msg)
		}else{
			notification.error({
				message: 'IM模块断开，请刷新页面检查'
			})
		}
	}

	// 初始化当前用户的好友列表
	const getUserList = () => {
		imSysUserApi.imUserList(props.baseRequest).then((res) => {
			users[userClient.value].push(...res)
			// 缓存头像 节省前端内存
			res.forEach((item) => {
				item.current = -1
				usersMap[item.id] = item
			})
		})
		initUserInfo()
	}

	// 获取当前帐号信息
	const initUserInfo = () => {
		let user = tool.data.get('USER_INFO')
		currentUser.id = user.id
		currentUser.name = user.name
		currentUser.avatar = user.avatar
		currentUser.orgName = user.orgName
		currentUser.positionName = user.positionName
		usersMap[user.id] = currentUser
	}

	// 将消息设置为已读
	const setRead = (ids: []) => {
		imMessageApi.setMessageRead(props.baseRequest,ids)
	}


	// 在开始通话前检查网络
	const startVoiceCall = () => {
		checkNetworkConnection();
		callType.value = 'voice';
		initializeCall();
	};

	const startVideoCall = () => {
		checkNetworkConnection();
		callType.value = 'video';
		initializeCall();
	};


	const checkNetworkConnection = () => {
		const connection = navigator.connection || navigator.mozConnection || navigator.webkitConnection;
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
			
			// 检查WebSocket连接
			if (websocket) {
				console.log('WebSocket连接正常');
			} else {
				console.error('WebSocket连接异常');
				notification.error({
					message: 'WebSocket连接异常',
					description: '信令服务器连接异常，可能影响通话建立'
				});
			}
	};

	const initializeCall = async () => {
		try {
			// 清理现有连接
			if (localStream.value) {
				localStream.value.getTracks().forEach(track => track.stop());
				localStream.value = null;
			}
			if (peerConnection.value) {
				peerConnection.value.close();
				peerConnection.value = null;
			}
			
			// 使用简单的媒体约束
			const constraints = {
				audio: true,
				video: callType.value === 'video'
			};
			
			console.log('请求媒体设备权限...');
			try {
				localStream.value = await navigator.mediaDevices.getUserMedia(constraints);
				console.log('成功获取本地媒体流:', 
					localStream.value.getTracks().map(t => `${t.kind}:${t.label}`));
			} catch (mediaError) {
				console.error('获取媒体设备失败:', mediaError);
				notification.error({
					message: '无法访问麦克风或摄像头',
					description: mediaError.message
				});
				return;
			}
			
			// 创建RTCPeerConnection，添加详细配置
			peerConnection.value = new RTCPeerConnection({
				iceServers: iceServers,
				iceTransportPolicy: 'all',
				bundlePolicy: 'max-bundle',
				rtcpMuxPolicy: 'require',
				sdpSemantics: 'unified-plan'
			});
			
			// 添加连接状态监听
			peerConnection.value.onconnectionstatechange = () => {
				console.log('连接状态变化:', peerConnection.value.connectionState);
				if (peerConnection.value.connectionState === 'connected') {
					console.log('连接成功建立!');
					callStatus.value = 'connected';
				} else if (peerConnection.value.connectionState === 'failed') {
					console.error('连接失败');
					notification.error({
						message: '连接失败',
						description: '无法建立媒体连接'
					});
					endCall();
				} else if (peerConnection.value.connectionState === 'disconnected') {
					console.warn('连接断开');
					notification.warning({
						message: '连接已断开',
						description: '尝试重新连接中...'
					});
				}
			};
			
			// 添加ICE连接状态监听
			peerConnection.value.oniceconnectionstatechange = () => {
				console.log('ICE连接状态:', peerConnection.value.iceConnectionState);
				if (peerConnection.value.iceConnectionState === 'failed') {
					console.error('ICE连接失败');
					// 尝试ICE重启
					if (peerConnection.value.restartIce) {
						console.log('尝试ICE重启');
						peerConnection.value.restartIce();
					}
				}
			};
			
			// 添加ICE收集状态监听
			peerConnection.value.onicegatheringstatechange = () => {
				console.log('ICE收集状态:', peerConnection.value.iceGatheringState);
			};
			
			// 添加信令状态监听
			peerConnection.value.onsignalingstatechange = () => {
				console.log('信令状态:', peerConnection.value.signalingState);
			};
			
			// 监听ICE候选
			peerConnection.value.onicecandidate = (event) => {
				if (event.candidate) {
					console.log('发送ICE候选:', event.candidate.candidate.substr(0, 50) + '...');
					// 发送ICE候选到对方
					sendMessageToWebSocket({
						type: 'call_ice_candidate',
						fromUserId: currentUser.id,
						toUserId: chatUser.id,
						candidate: event.candidate
					});
				} else {
					console.log('ICE候选收集完成');
				}
			};
			
			// 处理远程流
			peerConnection.value.ontrack = (event) => {
				console.log(`收到远程${event.track.kind}轨道`);
				handleRemoteStream(event.streams[0]);
			};
			
			// 添加本地轨道
			localStream.value.getTracks().forEach(track => {
				console.log(`添加本地${track.kind}轨道`);
				peerConnection.value.addTrack(track, localStream.value);
			});
			
			// 创建offer
			try {
				console.log('创建offer...');
				const offer = await peerConnection.value.createOffer({
					offerToReceiveAudio: true,
					offerToReceiveVideo: callType.value === 'video'
				});
				
				console.log('设置本地描述...');
				await peerConnection.value.setLocalDescription(offer);
				
				// 等待ICE收集完成或超时
				await new Promise((resolve) => {
					const checkState = () => {
						if (peerConnection.value.iceGatheringState === 'complete') {
							console.log('ICE收集完成，发送offer');
							resolve();
						} else {
							setTimeout(checkState, 500);
						}
					};
					
					// 设置超时
					const timeout = setTimeout(() => {
						console.log('ICE收集超时，发送当前offer');
						resolve();
					}, 5000);
					
					// 检查状态
					checkState();
				});
				
				// 发送offer
				console.log('发送offer到对方');
				sendMessageToWebSocket({
					type: 'call_offer',
					fromUserId: currentUser.id,
					toUserId: chatUser.id,
					callType: callType.value,
					sdp: peerConnection.value.localDescription
				});
				
				// 更新UI
				callModalVisible.value = true;
				callStatus.value = 'calling';
				startCallTimer();
				
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
				description: error.message
			});
			endCall();
		}
	};

	// 3. 修改处理远程流的代码，使用ref引用
	const handleRemoteStream = (stream) => {
		console.log('处理远程流，轨道:', stream.getTracks().map(t => `${t.kind}:${t.enabled}`));
		remoteStream.value = stream;
		
		// 使用setTimeout确保DOM已完全渲染
		setTimeout(() => {
			console.log('延迟处理远程流');
			
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
				const audioElement = document.getElementById('remoteAudio');
				
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
			
			// 如果是视频通话，也处理视频元素
			if (callType.value === 'video') {
				const remoteVideo = document.getElementById('remoteVideo');
				if (remoteVideo) {
					console.log('设置远程视频流');
					remoteVideo.srcObject = stream;
					remoteVideo.muted = false;
					remoteVideo.play().catch(e => console.error('视频播放失败:', e));
				}
			}
		}, 500); // 延迟500ms确保DOM已渲染
	};
	// 修改接听通话逻辑
	const acceptCall = async () => {
		try {
			// 1. 确保结束任何现有通话
			if (localStream.value) {
				localStream.value.getTracks().forEach(track => track.stop());
				localStream.value = null;
			}
			if (peerConnection.value) {
				peerConnection.value.close();
				peerConnection.value = null;
			}
			
			// 2. 获取本地媒体流
			const constraints = {
				audio: true,
				video: callType.value === 'video'
			};
			
			console.log('接听通话: 请求媒体权限...');
			localStream.value = await navigator.mediaDevices.getUserMedia(constraints);
			console.log('接听通话: 已获取本地媒体流');
			
			// 3. 创建RTCPeerConnection
			peerConnection.value = new RTCPeerConnection({
				iceServers: [
					{ urls: 'stun:stun.l.google.com:19302' }
				]
			});
			
			// 4. 设置远程描述（必须在添加本地轨道之前）
			console.log('接听通话: 设置远程描述');
			await peerConnection.value.setRemoteDescription(new RTCSessionDescription(incomingCall.sdp));
			
			// 5. 添加本地轨道
			localStream.value.getTracks().forEach(track => {
				console.log(`接听通话: 添加${track.kind}轨道`);
				peerConnection.value.addTrack(track, localStream.value);
			});
			
			// 6. 设置事件处理器
			
			// 处理ICE候选
			peerConnection.value.onicecandidate = (event) => {
				if (event.candidate) {
					console.log('接听通话: 发送ICE候选');
					sendMessageToWebSocket({
						type: 'call_ice_candidate',
						fromUserId: currentUser.id,
						toUserId: incomingCall.fromUserId,
						candidate: event.candidate
					});
				}
			};
			
			// 监听ICE连接状态
			peerConnection.value.oniceconnectionstatechange = () => {
				console.log('接听通话: ICE连接状态:', peerConnection.value.iceConnectionState);
			};
			
			// 监听连接状态
			peerConnection.value.onconnectionstatechange = () => {
				console.log('接听通话: 连接状态:', peerConnection.value.connectionState);
				if (peerConnection.value.connectionState === 'connected') {
					callStatus.value = 'connected';
				}
			};
			
			// 处理远程流
			peerConnection.value.ontrack = (event) => {
				console.log(`接听通话: 收到远程${event.track.kind}轨道`);
				handleRemoteStream(event.streams[0]);
			};
			
			// 7. 创建并发送应答
			console.log('接听通话: 创建应答');
			const answer = await peerConnection.value.createAnswer();
			await peerConnection.value.setLocalDescription(answer);
			
			console.log('接听通话: 发送应答');
			sendMessageToWebSocket({
				type: 'call_answer',
				fromUserId: currentUser.id,
				toUserId: incomingCall.fromUserId,
				sdp: peerConnection.value.localDescription
			});
			
			// 8. 更新UI状态
			callStatus.value = 'connected';
			startCallTimer();
			
		} catch (error) {
			console.error('接听通话失败:', error);
			notification.error({
				message: '接听失败',
				description: error.message
			});
			endCall();
		}
	};


	const rejectCall = () => {
		sendMessageToWebSocket({
			type: 'call_reject',
			fromUserId: currentUser.id,
			toUserId: incomingCall.fromUserId
		})
		endCall()
	}

	const endCall = () => {
		// 防止重复调用
		if (!callStatus.value) return
		
		if (localStream.value) {
			localStream.value.getTracks().forEach(track => track.stop())
			localStream.value = null
		}

		if (remoteStream.value) {
			// 清理远程流引用
			remoteStream.value = null;
		}
  
		if (peerConnection.value) {
			peerConnection.value.close()
			peerConnection.value = null;
		}
		if (callTimer.value) {
			clearInterval(callTimer.value)
			callTimer.value = null;
		}
		
		// 清理音频元素
		if (remoteAudioRef.value) {
			remoteAudioRef.value.srcObject = null;
		}
		// 只在通话状态时发送结束信号
		if (callStatus.value === 'connected' || callStatus.value === 'calling') {
			sendMessageToWebSocket({
				type: 'call_end',
				fromUserId: currentUser.id,
				toUserId: chatUser.id
			})
		}
		
		// 最后再清空状态
		callStatus.value = null
		callModalVisible.value = false
		callDuration.value = '00:00'
	}

	const startCallTimer = () => {
		let seconds = 0
		callTimer.value = setInterval(() => {
			seconds++
			const minutes = Math.floor(seconds / 60)
			const remainingSeconds = seconds % 60
			callDuration.value = `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`
		}, 1000)
	}

	getUserList();
	initGroupList()
</script>
<style lang="less" scoped>
	.record-img {
		width: 40px;
		height: 40px;
		float: left;
	}
	.large {
		font-size: large;
		margin-left: 8px;
		padding-top: 2px;
		padding-bottom: 2px;
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
		width: 26%;
		border-right: 1px solid #f0f0f0;
		overflow-y: auto;
		min-width: 200px;
	}
	.message {
		margin-right: 10px;
		margin-left: 10px;
	}
	.chat-content {
		flex: 1;
		display: flex;
		flex-direction: column;
	}
	.call-container {
		text-align: center;
		padding: 20px;
	}
	.call-header {
		margin-bottom: 20px;
	}
	.call-status {
		color: #666;
		margin: 10px 0;
	}
	.call-duration {
		font-size: 24px;
		margin: 10px 0;
	}
	.call-content {
		position: relative;
		width: 100%;
		height: 400px;
		background: #000;
		margin-bottom: 20px;
	}
	.local-video {
		position: absolute;
		right: 20px;
		bottom: 20px;
		width: 160px;
		height: 120px;
		border: 2px solid #fff;
		z-index: 1;
	}
	.remote-video {
		width: 100%;
		height: 100%;
		object-fit: cover;
	}
	.call-controls {
		display: flex;
		justify-content: center;
		gap: 10px;
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
		padding: 8px;
		border-radius: 6px;
		border-bottom: 1px solid rgb(0 0 0 / 10%);
		flex: 1;
	}
	.message-sender {
		font-weight: bold;
		margin-bottom: 4px;
	}
	.message-text {
		margin: 0;
		word-wrap: break-word;
    word-break:break-all;
	}
	.message-box-column {
		display: flex;
		flex-direction: column;
		width: auto;
		max-width: 90%;
	}
	.message-input {
		padding: 5px 10px 10px;
		display: flex;
		flex-wrap: wrap;
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
		display: flex;
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
		height: calc(100vh - 323px);
	}
	.webkit-scrollbar-2 {
		overflow-y: auto;
		overflow-x: hidden;
		-ms-overflow-style: none;
		scrollbar-width: none;
		height: calc(100vh - 343px);
	}
	.flex {
		display: flex;
	}
	.create-group {
		margin-left: 10px;
	}
	.online {
		position: relative;
		bottom: 15px;
		left: 32px;
	}
	.online-chat-content {
		position: relative;
		bottom: -20px;
	}
	.xn-im-total-container {
		margin: -28px -24px -20px;

		display: flex;
		display: flex;
		height: 100%;
	}
	.container-side {
		width: 65px;
		background-color: #001529;
		display: flex;
		flex-direction: column;
		align-items: center;
		flex-grow: 0;
	}
	.container-side-avatar {
		margin-top: 20px;
		margin-bottom: 20px;
		cursor: pointer;
	}
	.container-catalog {
		flex-grow: 0;
		min-width: 23%;
		max-width: 25%;
		border-right: 1px solid rgb(0 0 0 / 10%);
		height: 100%;
	}
	.container-content {
		flex-grow: 1;
	}
	/*  目录区 -start */
	.catalog-search {
		width: 100%;
		height: 60px;
		padding-left: 10px;
		padding-right: 10px;
		text-align: center;
		display: flex;
		justify-content: center;
		flex-direction: column-reverse;
		border-bottom: 1px solid rgb(0 0 0 / 10%);
	}
	.catalog-content {
		height: calc(100vh - 263px);
		overflow-y: auto;
		overflow-x: hidden;
		-ms-overflow-style: none;
		scrollbar-width: none;
	}
	.catalog-content-else {
		overflow-x: hidden;
		-ms-overflow-style: none;
		scrollbar-width: none;
	}
	.catalog-content-li {
		background-color: #ffffff;
		border-bottom: 1px solid rgb(0 0 0 / 0.06);
		display: flex;
		cursor: pointer;
		height: 60px;
	}
	.li-checked {
		background-color: #e7e7e7;
	}
	.catalog-content-li-avatar {
		margin-left: 10px;
		margin-top: 10px;
		margin-bottom: 10px;
		flex-grow: 0;
	}
	.catalog-content-li-user {
		margin-left: 5px;
		margin-top: 10px;
		margin-right: 10px;
		display: flex;
		flex-direction: column;
		flex-grow: 1;
		width: 200px;
	}
	.catalog-content-li-user-time {
		float: right;
		color: #b3b3b3;
	}
	.catalog-content-li-user-last-msg {
		font-size: 12px;
		color: #b3b3b3;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
		width: 120px;
	}
	/* 目录区 -end */
	.content-header {
		width: auto;
		height: 60px;
		border-bottom: 1px solid rgb(0 0 0 / 10%);
	}
	.header-title {
		min-width: 260px;
		margin-left: 20px;
		height: 60px;
		display: flex;
		align-items: center;
	}
	.header-title-font {
		font-weight: 600;
	}
	.header-close {
		width: 100%;
		display: flex;
		justify-content: flex-end;
	}
	.header-close-icon {
		padding: 5px 10px 5px 5px;
	}
	.content-center-miss {
		display: flex;
		flex-direction: column;
		align-items: center;
		padding-top: 100px;
		flex: 1;
		height: 100%;
		width: 80%;
	}
	.content-center-miss-image {
		width: 180px;
	}
	.content-center {
		height: 90%;
	}
	.tab-person {
		overflow-y: auto;
	}
	.send-msg-bto {
		justify-content: flex-end;
		display: flex;
		flex: 1;
	}
	:deep(.ant-tabs-nav) {
		margin: 0 !important;
	}
	// 添加通话相关样式
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
  
  .local-video {
    width: 160px;
    height: 120px;
    position: absolute;
    right: 20px;
    bottom: 20px;
    border-radius: 8px;
    object-fit: cover;
  }
  
  .remote-video {
    width: 100%;
    height: 400px;
    border-radius: 8px;
    object-fit: cover;
  }
}
.group-video-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 10px;
  width: 100%;
  height: 100%;
  padding: 10px;
}

.video-item {
  position: relative;
  aspect-ratio: 16/9;
  background: #000;
  border-radius: 8px;
  overflow: hidden;
}

.user-name {
  position: absolute;
  bottom: 10px;
  left: 10px;
  color: white;
  background: rgba(0, 0, 0, 0.5);
  padding: 2px 8px;
  border-radius: 4px;
}
</style>
