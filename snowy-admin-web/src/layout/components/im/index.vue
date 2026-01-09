<template>
	<div>
		<div @click="handleOpen">
			<a-badge
				v-if="props.disPlayUi == 'badge'"
				:dot="ImMessageUserVoList.map((res) => res.unreadCount).reduce((a, b) => a + b, 0) > 0"
			>
				<slot name="custom">
					<MessageOutlined />
				</slot>
			</a-badge>
			<a-float-button
				id="float-button"
				:type="props.floatBottonType"
				v-if="props.disPlayUi == 'float' && props.badge == 'dot'"
				shape="circle"
				:badge="{ dot: ImMessageUserVoList.map((res) => res.unreadCount).reduce((a, b) => a + b, 0) > 0 }"
				:style="props.floatStyle"
			>
				<template #icon>
					<slot name="icon">
						<MessageOutlined />
					</slot>
				</template>
			</a-float-button>
			<a-float-button
				id="float-button"
				:type="props.floatBottonType"
				v-if="props.disPlayUi == 'float' && props.badge == 'count'"
				shape="circle"
				:badge="{ count: ImMessageUserVoList.map((res) => res.unreadCount).reduce((a, b) => a + b, 0) }"
				:style="props.floatStyle"
			>
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
								<a-avatar shape="square" size="large" :src="usersMap[item.userId + ''].avatar" />
							</a-badge>
							<a-badge status="success" v-if="onlineFunc(item.userId)" style="padding-top: 32px; margin-left: -8px" />
							<a-badge status="default" v-else style="padding-top: 32px; margin-left: -8px" />
							<div class="catalog-content-li-user">
								<div>
									<a-typography-text
										:style="{ width: lastMessageDate(item.createTime).length >= 6 ? '60%' : '75%' }"
										:ellipsis="{ tooltip: usersMap[item.userId + ''].name }"
										:content="usersMap[item.userId + ''].name"
									/>
									<span
										class="catalog-content-li-user-time"
										:style="{ width: lastMessageDate(item.createTime).length >= 6 ? '40%' : '25%' }"
										>{{ lastMessageDate(item.createTime) }}</span
									>
								</div>
								<span class="catalog-content-li-user-last-msg" :style="{ width: '90%' }">{{
									groupRecall(item.content)
								}}</span>
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
								<a-button
									type="primary"
									shape="circle"
									size="large"
									@click="createGroup"
									style="position: absolute; bottom: 20px; left: 230px"
								>
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
											<div
												class="message-content"
												v-if="message.type == '1' || message.type == '5' || message.type == '6'"
											>
												<div v-if="message.type == '1'" class="box2" @contextmenu="onContextMenu($event, message)">
													<p class="message-text">{{ message.content }}</p>
												</div>
												<div v-if="message.type == '5' || message.type == '6'">
													<div v-if="JSON.parse(message.content).status == '通话结束'">
														<PhoneOutlined v-if="isTrue(message.type, '5')" />
														<VideoCameraOutlined v-if="isTrue(message.type, '6')" />
														通话时长：{{ durationFormat(JSON.parse(message.content).duration) }}
													</div>
													<div v-if="JSON.parse(message.content).status != '通话结束'">
														<PhoneOutlined v-if="isTrue(message.type, '5')" />
														<VideoCameraOutlined v-if="isTrue(message.type, '6')" />
														{{ JSON.parse(message.content).status }}
													</div>
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
														:fallback="downIcon"
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
							<FileImageOutlined class="large message-input-icon message-input-icon--image" @click="uploadImage('图片', 'image')" />
							<FolderOutlined class="large message-input-icon message-input-icon--folder" @click="uploadImage('文件', 'drag')" />
							<AudioOutlined class="large message-input-icon message-input-icon--audio" @click="startVoiceCall()" />
							<VideoCameraOutlined class="large message-input-icon message-input-icon--video" @click="startVideoCall()" />
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
									>发送
								</a-button>
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
			<xn-im-upload
				v-if="uploadShow"
				uploadResultType="id"
				:uploadMode="uploadMode"
				ref="uploadImageRef"
				:uri="config.API_URL"
			/>
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
				:baseRequest="props.baseRequest"
			/>
			<template #footer />
		</a-modal>
		<xn-im-web-socket @setWebSocket="setWebSocket" :uri="config.API_URL" />
		<XnImCallModal
			:callState="callService.state"
			:currentUser="currentUser"
			:targetUser="computeTargetUser()"
			:usersMap="usersMap"
			:isGroupCall="chatUser.chatType === '2'"
			:sendMessageFunc="(msg) => sendMessageToWebSocket(msg)"
			@accept-call="onAcceptCall"
			@reject-call="onRejectCall"
			@end-call="onEndCall"
			ref="callModalRef"
		/>
	</div>
</template>

<script setup lang="ts">
	import { ref, reactive, onMounted, watch, nextTick, h } from 'vue'
	import { notification } from 'ant-design-vue'
	import {
		MessageOutlined,
		TeamOutlined,
		SettingOutlined,
		AudioOutlined,
		PhoneOutlined,
		VideoCameraOutlined
	} from '@ant-design/icons-vue'
	import '@imengyu/vue3-context-menu/lib/vue3-context-menu.css'
	import ContextMenu from '@imengyu/vue3-context-menu'
	import dayjs from 'dayjs'
	import downIcon from './image/baseImages'
	import { durationFormat, isTrue } from './utils/common'
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
	import { XnImCallModal, useCallService } from './components/XnImCall'

	const props = defineProps({
		baseRequest: {
			type: Function,
			default: () => undefined
		},
		disPlayUi: {
			type: String,
			default: 'badge' //float
		},
		floatStyle: {
			type: Object,
			default: () => ({ right: '124px', bottom: '100px' }) //displayUi为float时生效
		},
		badge: {
			type: String,
			default: 'dot' //count
		},
		config: {
			type: Object,
			default: () => ({})
		},
		floatBottonType: {
			type: String,
			default: '' //primary or ''
		}
	})
	const callService = useCallService((msg) => websocket.value?.sendWebSocketMessage(msg))
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

	// 保存通话前的聊天对象信息
	const priorChatUser = reactive<User>({ id: '', name: '', avatar: '' })
	onMounted(() => {
		initMessageList()
		initGroupMemberMuted()
		if (props.floatStyle.backgroundColor != '' && props.floatStyle.backgroundColor != undefined) {
			var floatButton = document.getElementById('float-button')
			var boxes = floatButton.getElementsByClassName('ant-float-btn-body')
			for (var i = 0; i < boxes.length; i++) {
				boxes[0].style.backgroundColor = props.floatStyle.backgroundColor
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

	// 接听通话
	const onAcceptCall = () => {
		callService.acceptCall(currentUser.id)
	}

	// 拒绝通话
	const onRejectCall = () => {
		callService.rejectCall(currentUser.id)
	}

	// 结束通话
	const onEndCall = () => {
		callService.endCall()
		// 如果当前聊天对象为空或已更改，则恢复到通话前的聊天对象
		if (!chatUser.id && priorChatUser.id) {
			initChatUser(priorChatUser)
		}
	}

	const lastMessageDate = (date) => {
		// 格式化时间 如果是今天的只显示时间 如果是昨天的显示昨天 如果是昨天之前的显示月日
		if (dayjs().format('YYYY-MM-DD') == dayjs(date).format('YYYY-MM-DD')) {
			return dayjs(date).format('HH:mm')
		} else if (dayjs().subtract(1, 'day').format('YYYY-MM-DD') == dayjs(date).format('YYYY-MM-DD')) {
			return '昨天'
		} else {
			return dayjs(date).format('MM-DD')
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
		imGroupMemberApi.imGroupMemberMuteList(props.baseRequest, {}).then((res) => {
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
		imGroupApi.imGroupListByUser(props.baseRequest, {}).then((res) => {
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
			return navigator.clipboard.writeText(textToCopy)
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
					imMessageApi.recallMessage(props.baseRequest, { id: msg.id }).then((res) => {
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
		const { clientHeight, scrollHeight, scrollTop } = e.target
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
			callService.handleWebSocketMessage(json, currentUser.id)
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
		if (json.toUserId === currentUser.id && json.fromUserId === chatUser.id && open.value) {
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
			if (json.type == '5' || json.type == '6') {
				content =
					(json.type == '5' ? '【语音】' : '【视频】') +
					(JSON.parse(json.content).status == '通话结束'
						? `通话时长：${durationFormat(JSON.parse(json.content).duration)}`
						: JSON.parse(json.content).status)
			} else {
				let itemJson = JSON.parse(json.content)
				content = imageSuffix.indexOf(itemJson.suffix) > -1 ? '【图片】' : '【文件】' + itemJson.name
			}
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
		imMessageApi.queryChatRecord(props.baseRequest, queryChatRecordParams).then((res) => {
			ImMessageUserVoList.push(...res.records)
			queryChatRecordParams.total = res.total
		})
	}

	const checkGroupRole = (user: User) => {
		imGroupMemberApi.imGroupMemberPage(props.baseRequest, { groupId: user.id, userId: currentUser.id }).then((res) => {
			if (res.records.length == 0) {
				notification.warning({
					message: '您不是该群组成员'
				})
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
			imMessageApi.queryChatRecordWithUser(props.baseRequest, queryChatRecordWithUserParams).then((res) => {
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
		if (websocket.value) {
			websocket.value.sendWebSocketMessage(msg)
		} else {
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
		imMessageApi.setMessageRead(props.baseRequest, ids)
	}

	// 开始视频通话
	const startVideoCall = () => {
		notification.success({
			message: '正在初始化视频通道'
		})
		// 保存当前聊天对象信息，以便通话结束后恢复
		savePriorChatUser()
		callService.startVideoCall(currentUser.id, chatUser.id, userClient.value)
	}

	// 开始语音通话
	const startVoiceCall = () => {
		notification.success({
			message: '正在初始化语音通道'
		})
		// 保存当前聊天对象信息，以便通话结束后恢复
		savePriorChatUser()
		callService.startVoiceCall(currentUser.id, chatUser.id, userClient.value)
	}

	// 保存通话前的聊天对象信息
	const savePriorChatUser = () => {
		priorChatUser.id = chatUser.id
		priorChatUser.name = chatUser.name
		priorChatUser.avatar = chatUser.avatar
		priorChatUser.chatType = chatUser.chatType
	}

	getUserList()
	initGroupList()

	const computeTargetUser = () => {
		console.log('computeTargetUser被调用, callStatus:', callService.state.callStatus)
		console.log('incomingCall:', callService.state.incomingCall)

		if (callService.state.callStatus === 'incoming') {
			// 被呼叫方，处理传入的呼叫
			const fromUserId = callService.state.incomingCall.fromUserId
			if (fromUserId) {
				// 首先尝试从usersMap中查找用户信息
				if (usersMap[fromUserId]) {
					console.log('找到呼叫方信息:', usersMap[fromUserId])
					return usersMap[fromUserId]
				} else {
					// 如果找不到用户信息，返回一个基本对象
					console.log('未找到呼叫方信息,创建临时对象:', fromUserId)
					return {
						id: fromUserId,
						name: '未知用户',
						avatar: ''
					}
				}
			}
		} else if (callService.state.callStatus === 'calling' || callService.state.callStatus === 'connected') {
			// 呼叫方，使用当前选择的聊天用户
			if (chatUser.id) {
				console.log('使用当前聊天用户:', chatUser)
				return chatUser
			}
		}

		// 默认返回一个空对象，避免出错
		if (callService.state.incomingCall && callService.state.incomingCall.fromUserId) {
			return {
				id: callService.state.incomingCall.fromUserId,
				name: '未知用户',
				avatar: ''
			}
		}

		return { id: '', name: '未知', avatar: '' }
	}
</script>
<style lang="less" scoped>
	@import './styles/im.less';
</style>
