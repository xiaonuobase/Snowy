<template>
	<div class="im panel-item" @click="handleOpen">
		<MessageOutlined />
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
								<span>{{ usersMap[item.userId + ''].name }}</span>
								<span class="catalog-content-li-user-time">{{ lastMessageDate(item.createTime) }}</span>
							</div>
							<span class="catalog-content-li-user-last-msg">{{ groupRecall(item.content) }}</span>
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
													src="/src/assets/images/fileImg/docx.png"
													class="record-img"
													:title="previewDisplay(key.suffix)"
													v-else-if="key.suffix === 'doc' || key.suffix === 'docx'"
												/>
												<img
													src="/src/assets/images/fileImg/xlsx.png"
													class="record-img"
													:title="previewDisplay(key.suffix)"
													v-else-if="key.suffix === 'xls' || key.suffix === 'xlsx'"
												/>
												<img
													src="/src/assets/images/fileImg/zip.png"
													:title="previewDisplay(key.suffix)"
													class="record-img"
													v-else-if="key.suffix === 'zip'"
												/>
												<img
													src="/src/assets/images/fileImg/rar.png"
													:title="previewDisplay(key.suffix)"
													class="record-img"
													v-else-if="key.suffix === 'rar'"
												/>
												<img
													src="/src/assets/images/fileImg/ppt.png"
													class="record-img"
													:title="previewDisplay(key.suffix)"
													v-else-if="key.suffix === 'ppt' || key.suffix === 'pptx'"
												/>
												<img
													src="/src/assets/images/fileImg/pdf.png"
													:title="previewDisplay(key.suffix)"
													class="record-img"
													v-else-if="key.suffix === 'pdf'"
												/>
												<img
													src="/src/assets/images/fileImg/txt.png"
													:title="previewDisplay(key.suffix)"
													class="record-img"
													v-else-if="key.suffix === 'txt'"
												/>
												<img
													src="/src/assets/images/fileImg/html.png"
													:title="previewDisplay(key.suffix)"
													class="record-img"
													v-else-if="key.suffix === 'html'"
												/>
												<img
													src="/src/assets/images/fileImg/file.png"
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
						<a-textarea
							v-model:value="newMessage"
							@keypress.enter="sendMessage"
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
		<xn-upload v-if="uploadShow" :uploadMode="uploadMode" ref="uploadImageRef"></xn-upload>
	</a-modal>
	<a-modal v-model:open="previewShow" title="预览文件" :width="1200" style="top: 10px">
		<xn-file-preview v-show="previewShow" :src="previewSrc" :file-type="previewFileType" @goBack="previewBack" />
		<template #footer />
	</a-modal>
	<a-modal
		v-model:open="createGroupShow"
		style="top: 50px"
		:title="createGroupType == 'add' ? '创建群组' : '操作群组'"
		:width="600"
		:mask="false"
	>
		<XnEditGroupComponent
			@updateGroupInfo="updateGroupInfoData"
			:createGroupType="createGroupType"
			:id="updateGroupId"
			@closeGroupShow="closeGroupShow"
			@restChatUser="restChatUser"
			v-if="createGroupShow"
			ref="createGroupRef"
			:onlineUserList="onlineUserList"
		/>
		<template #footer />
	</a-modal>
	<WebSocketComponent @setWebSocket="setWebSocket" />
</template>

<script setup lang="ts">
	import { ref, reactive } from 'vue'
	import dayjs from 'dayjs'
	import { MessageOutlined, TeamOutlined, SettingOutlined } from '@ant-design/icons-vue'
	import tool from '@/utils/tool'
	import imSysUserApi from '@/layout/components/im/api/imSysUserApi'
	import imMessageApi from '@/layout/components/im/api/imMessageApi'
	import imGroupMemberApi from '@/layout/components/im/api/imGroupMemberApi'
	import imGroupApi from '@/layout/components/im/api/imGroupApi'
	import { User, Message, ImMessageUserVo, ImMessageBo, ImGroupVo } from '@/layout/components/im/type/type'
	import { notification } from 'ant-design-vue'
	import ContextMenu from '@imengyu/vue3-context-menu'
	import XnUpload from '@/components/XnUpload/index.vue'
	import WebSocketComponent from '@/layout/components/im/WebSocketComponent.vue'
	import XnEditGroupComponent from '@/layout/components/im/XnEditGroupComponent.vue'

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
		size: 10,
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
		items: [],
		iconFontClass: 'iconfont',
		customClass: 'class-a',
		zIndex: 9999,
		minWidth: 100,
		x: null,
		y: null
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

	onMounted(() => {
		initMessageList()
		initGroupMemberMuted()
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
		ws.setMessageCallback(onMessage)
	}

	// 判断是否可以发送消息
	const cancelSilenceTime = (time) => {
		return !time || new Date().getTime() > new Date(time).getTime()
	}

	//初始化当前用户所在的群聊是否被禁言列表
	const initGroupMemberMuted = () => {
		imGroupMemberApi.imGroupMemberMuteList({}).then((res) => {
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
		imGroupApi.imGroupListByUser({}).then((res) => {
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
		menuData.items = [
			{
				label: '复制',
				onClick: () => {
					if (msg.type != '1') {
						notification.warning({
							message: '暂不支持复制文件'
						})
						return
					} else {
						tool.copyToClipboard(msg.content)
						notification.success({
							message: '复制成功'
						})
					}
				}
			}
		]
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
					imMessageApi.recallMessage({ id: msg.id }).then((res) => {
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
		const clientHeight = e.target.clientHeight
		const scrollHeight = e.target.scrollHeight
		const scrollTop = e.target.scrollTop
		if (Math.ceil(scrollTop) + clientHeight >= scrollHeight) {
			// 判断是哪个tab
			// if (activeKey.value == '1') {
			// 判断是否还有数据 -1时候为初始化
			if (
				queryChatRecordParams.total != -1 &&
				queryChatRecordParams.current * queryChatRecordParams.size >= queryChatRecordParams.total
			) {
				return
			}
			queryChatRecordParams.current += 1
			initMessageList()
		}
		// }
		// console.log(`到底了!${activeKey.value == '1' ? '聊天' : activeKey.value == '2' ? '用户' : '群组'}`);
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
			content = imageSuffix.indexOf(itemJson.suffix) > -1 ? '[图片]' : '[文件1]' + itemJson.name
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
		imMessageApi.queryChatRecord(queryChatRecordParams).then((res) => {
			ImMessageUserVoList.push(...res.records)
			queryChatRecordParams.total = res.total
		})
	}

	const checkGroupRole = (user: User) => {
		imGroupMemberApi.imGroupMemberPage({ groupId: user.id, userId: currentUser.id }).then((res) => {
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
			imMessageApi.queryChatRecordWithUser(queryChatRecordWithUserParams).then((res) => {
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
		// http://localhost:82/dev/file/download?id=1816742994645123073 取出id
		const content = obj['url'].split('=')[1]

		// 拼接消息
		const msg: ImMessageBo = {
			fromUserId: currentUser.id,
			toUserId: chatUser.id,
			content,
			chatType: chatUser.chatType ? chatUser.chatType : '1',
			type,
			toUserType: userClient.value,
			fromUserType: '1'
		}
		// 发送消息
		sendMessageToWebSocket(msg)
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
		websocket.value.sendWebSocketMessage(msg)
	}

	// 初始化当前用户的好友列表
	const getUserList = () => {
		imSysUserApi.imUserList().then((res) => {
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
		imMessageApi.setMessageRead(ids)
	}

	getUserList()
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
		word-break: normal;
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
		width: 230px;
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
</style>
