<template>
	<div>
		<a-form ref="formRef" :model="formData" layout="vertical">
			<a-form-item label="名称：" name="name">
				<a-input
					v-model:value="formData.name"
					placeholder="请输入名称"
					allow-clear
					show-count
					:maxlength="10"
					:disabled="props.createGroupType == 'details' || (currentUser.role && currentUser.role != '1')"
				/>
			</a-form-item>
			<a-row>
				<a-col :span="6">
					<div class="account-center-avatarHolder">
						<div class="avatar">
							<a-spin size="small" :spinning="avatarLoading">
								<img :src="formData.avatar ? formData.avatar : '/img/logo.png'" />
							</a-spin>
							<a @click="uploadLogo">
								<div v-if="formData.avatar" :class="formData.avatar ? 'mask' : 'mask-notImg'"><upload-outlined /></div>
							</a>
						</div>
					</div>
				</a-col>
				<a-col :span="18">
					<a-form-item label="群聊人：" name="receiverIdList">
						<xn-im-user-selector
							:org-tree-api="selectorApiFunction.orgTreeApi"
							:user-page-api="selectorApiFunction.userPageApi"
							:user-list-by-id-list-api="selectorApiFunction.checkedUserListApi"
							data-type="object"
							v-model:value="formData.receiverIdList"
							:userShow="props.createGroupType == 'add'"
							:updateShow="props.createGroupType == 'details' || currentUser.role != '1'"
						/>
					</a-form-item>
				</a-col>
			</a-row>
			<div class="webkit-scrollbar" v-if="props.createGroupType != 'add'">
				<a-list :data-source="userList">
					<template #renderItem="{ item }">
						<a-list-item v-for="key in [translateUser(item)]" :key="key">
							<template #actions>
								<a
									key="list-loadmore-edit"
									v-if="key.role == '3' && userId != key.userId && currentUser.role == '1'"
									@click="updateGroupMember(1, key.userId)"
									>设为管理员</a
								>
								<a
									key="list-loadmore-more"
									v-if="key.role == '2' && userId != key.userId && currentUser.role == '1'"
									@click="updateGroupMember(2, key.userId)"
									>取消管理员</a
								>
								<a
									key="list-loadmore-more"
									v-if="key.role != '1' && userId != key.userId && currentUser.role == '1'"
									@click="updateGroupMember(3, key.userId)"
									>转让群主</a
								>
								<a
									key="list-loadmore-more"
									v-if="key.role != '1' && userId != key.userId && isShow(key.role)"
									@click="updateGroupMember(4, key.userId)"
									>移除</a
								>
								<a
									key="list-loadmore-more"
									v-if="
										key.role != '1' && userId != key.userId && isShow(key.role) && !cancelSilenceTime(key.silenceTime)
									"
									@click="mutedShowOpen(key.userId)"
									>禁言</a
								>
								<a
									key="list-loadmore-more"
									v-if="
										key.role != '1' && userId != key.userId && isShow(key.role) && cancelSilenceTime(key.silenceTime)
									"
									@click="cancelSilence(key.userId)"
									>取消禁言</a
								>
							</template>
							<a-skeleton avatar :title="false" :loading="!!item.loading" active>
								<a-list-item-meta :description="key.role == '1' ? '群主' : key.role == '2' ? '管理员' : '普通成员'">
									<template #title>
										<span style="font-size: smaller">{{ item.name || '-' }}</span>
									</template>
									<template #avatar>
										<a-avatar shape="square" :src="item.avatar || avatar" size="large" />
										<div class="online">
											<a-badge status="success" v-if="onlineFunc(key.userId)" />
											<a-badge status="default" v-else />
										</div>
									</template>
								</a-list-item-meta>
							</a-skeleton>
						</a-list-item>
					</template>
				</a-list>
			</div>
			<a-form-item label="备注：" name="remark">
				<a-input
					v-model:value="formData.remark"
					placeholder="请输入备注"
					allow-clear
					:disabled="props.createGroupType == 'details' || (currentUser.role && currentUser.role != '1')"
				/>
			</a-form-item>
		</a-form>
		<div class="footer">
			<a-button class="ml" type="primary" danger @click="deleteGroup" v-if="currentUser.role == '1'">解散群组</a-button>
			<a-button class="ml" type="primary" @click="add" v-if="currentUser.role == '1' || props.createGroupType == 'add'"
				>保存</a-button
			>
			<a-button class="ml" type="primary" @click="add" v-if="userList.length != imGroupMembers.length"
				>确认邀请</a-button
			>
			<a-button @click="cancel">关闭</a-button>
		</div>
	</div>
	<a-modal v-model:open="mutedShow" title="禁言" @ok="mutedShowOpen">
		<a-form-item label="禁言时间：" name="time">
			<a-input-number v-model:value="mutedValue" min="0" max="9999">
				<template #addonAfter>分钟</template>
			</a-input-number>
		</a-form-item>
	</a-modal>
	<xn-im-crop-upload
		ref="cropUploadRef"
		:img-src="formData ? formData.avatar : undefined"
		@successful="cropUploadSuccess"
	/>
</template>

<script setup lang="ts">
	import { defineEmits, ref, createVNode, onMounted, nextTick, reactive, defineProps, defineExpose } from 'vue'
	import { message, Modal } from 'ant-design-vue'
	import { ExclamationCircleOutlined } from '@ant-design/icons-vue'
	import tool from '@/utils/tool'
	import userApi from '../../api/userApi'
	import imGroupApi from '../../api/imGroupApi'
	import imGroupMemberApi from '../../api/imGroupMemberApi'
	import userCenterApi from '../../api/userCenterApi'

	import XnImCropUpload from '../XnImCropUpload/index.vue'
	import XnImUserSelector from '../XnImUserSelector/index.vue'

	const mutedShow = ref(false)
	const mutedValue = ref(0)
	const formData = reactive({
		name: '',
		receiverIdList: [],
		remark: '',
		avatar: ''
	})
	const imGroupMembers = ref([])
	const userList = ref([])
	const avatar = ref(
		'data:image/png;base64,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'
	)
	const avatarLoading = ref(false)
	const formRef = ref()
	const cropUploadRef = ref()
	const userId = ref('')
	const currentUser = ref({})
	const uploadLogo = () => {
		if (props.createGroupType == 'details' || (currentUser.value.role && currentUser.value.role != '1')) {
			message.error({
				message: '无权限修改用户头像'
			})
			return
		}
		// 上传头像
		cropUploadRef.value.show()
	}
	const props = defineProps({
		createGroupType: {
			type: String,
			default: 'add'
		},
		id: {
			type: String,
			default: ''
		},
		onlineUserList: {
			type: Array,
			default: () => []
		},
		baseRequest: {
			type: Function,
			default: () => undefined
		}
	})

	onMounted(() => {
		let user_info = tool.data.get('USER_INFO')
		userId.value = user_info.id
		if (props.createGroupType === 'update') {
			update(props.id)
		} else {
			rest()
		}
	})

	const onlineFunc = (data) => {
		return props.onlineUserList.includes(data) > 0
	}

	// 判断是否可以取消禁言
	const cancelSilenceTime = (time) => {
		if (time) {
			let currentTime = undefined
			if (typeof time == 'string') {
				currentTime = new Date(time).getTime()
			} else {
				currentTime = time.getTime()
			}
			return new Date().getTime() < currentTime
		} else {
			return false
		}
	}

	const cancelSilence = (userId) => {
		Modal.confirm({
			title: '是否取消禁言',
			icon: createVNode(ExclamationCircleOutlined),
			onOk() {
				let id = imGroupMembers.value.find((item) => item.userId == userId).id
				imGroupMemberApi
					.imGroupMemberUnMute(props.baseRequest, {
						id,
						userId
					})
					.then(() => {
						imGroupMembers.value.forEach((item) => {
							if (item.userId == userId) {
								item.silenceTime = ''
							}
						})
					})
			}
		})
	}

	const mutedShowUserId = ref('')

	const mutedShowOpen = (userId) => {
		if (typeof userId == 'string') {
			mutedShowUserId.value = ''
			mutedShowUserId.value = userId
			mutedShow.value = true
			mutedValue.value = 0
		} else {
			// member信息id
			let id = imGroupMembers.value.find((item) => item.userId == mutedShowUserId.value).id
			// 当前时间加上参数muteTime的时间(单位分钟)
			let time = new Date().getTime() + 60000 * mutedValue.value
			imGroupMemberApi
				.imGroupMemberMute(props.baseRequest, {
					id,
					silenceTime: time,
					userId: mutedShowUserId.value
				})
				.then(() => {
					imGroupMembers.value.forEach((item) => {
						if (item.userId == mutedShowUserId.value) {
							item.silenceTime = new Date(time)
						}
					})
					// 同时给父组件传递禁言时间
					mutedShow.value = false
				})
		}
	}

	const isShow = (role) => {
		if (!role) {
			return false
		}
		role = role.toString()
		return (
			(currentUser.value.role == '2' && role == '3') || (currentUser.value.role == '1' && ['2', '3'].indexOf(role) > -1)
		)
	}
	const emit = defineEmits(['updateGroupInfo', 'closeGroupShow', 'restChatUser'])

	const rest = () => {
		formData.name = ''
		formData.remark = ''
		formData.avatar = avatar.value
		formData.receiverIdList = []
	}

	const cancel = () => {
		emit('closeGroupShow')
	}

	const deleteGroup = () => {
		if (props.createGroupType == 'update') {
			Modal.confirm({
				title: '请确认是否解散群组?',
				icon: createVNode(ExclamationCircleOutlined),
				content: createVNode('div', { style: 'color:red;' }, '如果确认解散群组，将无法恢复，请谨慎操作'),
				onOk() {
					imGroupApi
						.imGroupDelete(props.baseRequest, [{ id: props.id }])
						.then(() => {
							emit('restChatUser')
						})
						.catch(() => {})
				}
			})
		}
	}

	//翻译当前遍历的用户信息
	const translateUser = (item) => {
		let member = imGroupMembers.value.find((member) => member.userId == item.id)
		return member ? member : {}
	}
	// 头像裁剪图片回调
	const cropUploadSuccess = (data) => {
		// 转换为file类型
		const result = new File([data.blobData], data.fileName, { type: 'image/jpeg', lastModified: Date.now() })
		const fileData = new FormData()
		fileData.append('file', result)
		avatarLoading.value = true
		imGroupApi.imGroupUploadAvatar(props.baseRequest, fileData).then((data) => {
			avatarLoading.value = false
			formData.avatar = data
		})
	}

	const updateGroupMember = (type, toUserId) => {
		let id = imGroupMembers.value.find((item) => item.userId == toUserId).id
		//设为管理员
		if (type == 1) {
			imGroupMemberApi
				.imGroupMemberSubmitForm(
					props.baseRequest,
					{
						id,
						userId: toUserId,
						role: 2
					},
					true
				)
				.then(() => {
					imGroupMembers.value.forEach((item) => {
						if (item.userId == toUserId) {
							item.role = 2
						}
					})
				})
				.catch(() => {})
			//移除管理员
		} else if (type == 2) {
			imGroupMemberApi
				.imGroupMemberSubmitForm(
					props.baseRequest,
					{
						id,
						userId: toUserId,
						role: 3
					},
					true
				)
				.then(() => {
					imGroupMembers.value.forEach((item) => {
						if (item.userId == toUserId) {
							item.role = 3
						}
					})
				})
				.catch(() => {})
			//转让群主
		} else if (type == 3) {
			imGroupMemberApi
				.imGroupMemberSubmitForm(
					props.baseRequest,
					{
						id,
						userId: toUserId,
						role: 1
					},
					true
				)
				.then(() => {
					let id = imGroupMembers.value.find((item) => item.userId == userId.value).id
					imGroupMemberApi
						.imGroupMemberSubmitForm(
							props.baseRequest,
							{
								id,
								userId: userId.value,
								role: 3
							},
							true
						)
						.then(() => {
							imGroupMembers.value.forEach((item) => {
								if (item.userId == toUserId) {
									item.role = 1
								}
								if (item.userId == userId.value) {
									item.role = 3
								}
							})
						})
						.catch(() => {})
				})
				.catch(() => {})
			//移除当前人
		} else {
			Modal.confirm({
				title: '请确认是否移除此用户?',
				icon: createVNode(ExclamationCircleOutlined),
				content: createVNode('div', { style: 'color:red;' }, '如果确认移除此用户，将无法恢复，请谨慎操作'),
				onOk() {
					imGroupMemberApi
						.imGroupMemberDelete(props.baseRequest, [{ id }])
						.then(() => {
							imGroupMembers.value = imGroupMembers.value.filter((item) => item.userId != toUserId)
							userList.value = userList.value.filter((item) => item.id != toUserId)
						})
						.catch(() => {})
				}
			})
		}
	}
	const add = () => {
		const params = {
			name: formData.name,
			receiverIdList: formData.receiverIdList,
			remark: formData.remark,
			avatar: formData.avatar
		}
		// 判断是否有当前用户  如果没有加进去 如果又不用管
		if (!params.receiverIdList.includes(userId.value)) {
			params.receiverIdList.push(userId.value)
		}
		if (props.createGroupType == 'update') {
			params.id = props.id
		}
		// 创建群聊
		imGroupApi
			.imGroupSubmitForm(
				props.baseRequest,
				params,
				props.createGroupType == 'update' || props.createGroupType == 'details'
			)
			.then(() => {
				let aa = {
					avatar: formData.avatar,
					name: formData.name,
					id: props.id,
					type: props.createGroupType
				}
				emit('updateGroupInfo', aa)
			})
	}

	const update = (id) => {
		// 查询原有的数据进行回滚
		imGroupApi.imGroupDetail(props.baseRequest, { id: id }).then((data) => {
			formData.name = data.name
			formData.remark = data.remark
			formData.avatar = data.avatar
			let aa = data.imGroupMembers.map((item) => item.userId)
			nextTick(() => {
				imGroupMembers.value = data.imGroupMembers
				currentUser.value = data.imGroupMembers.find((item) => item.userId == userId.value)
				formData.receiverIdList = aa
			})
		})
	}
	//	传递设计器需要的API
	const selectorApiFunction = {
		orgTreeApi: (param) => {
			return userApi.userOrgTreeSelector(props.baseRequest, param).then((data) => {
				return Promise.resolve(data)
			})
		},
		userPageApi: (param) => {
			return userApi.userSelector(props.baseRequest, param).then((data) => {
				return Promise.resolve(data)
			})
		},
		checkedUserListApi: (param) => {
			return userCenterApi.userCenterGetUserListByIdList(props.baseRequest, param).then((data) => {
				userList.value = data
				return Promise.resolve(data)
			})
		}
	}

	defineExpose({ add, update })
</script>
<style lang="less" scoped>
	.footer {
		display: flex;
		justify-content: flex-end;

		& > .ml {
			margin-right: 10px;
		}
	}

	.mask {
		border-radius: 50%;
		position: absolute;
		margin-top: -104px;
		width: 104px;
		height: 104px;
		background: rgba(101, 101, 101, 0.6);
		color: #ffffff;
		opacity: 0;
		font-size: 25px;
		display: flex;
		justify-content: center;
		align-items: center;
	}

	.mask-notImg {
		border-radius: 50%;
		position: absolute;
		margin-top: 104px;
		width: 104px;
		height: 104px;
		background: rgba(101, 101, 101, 0.6);
		color: #ffffff;
		opacity: 0;
		font-size: 25px;
		display: flex;
		justify-content: center;
		align-items: center;
	}

	.account-center-avatarHolder {
		text-align: center;
		margin-bottom: 24px;

		& > .avatar {
			margin: 0 auto;
			width: 104px;
			height: 104px;
			margin-bottom: 20px;
			border-radius: 50%;
			overflow: hidden;

			img {
				height: 100%;
				width: 100%;
			}
		}

		.mask {
			border-radius: 50%;
			position: absolute;
			margin-top: -105px;
			width: 104px;
			height: 104px;
			background: rgba(101, 101, 101, 0.6);
			color: #ffffff;
			opacity: 0;
			font-size: 25px;
			display: flex;
			justify-content: center;
			align-items: center;
		}

		.mask-notImg {
			border-radius: 50%;
			position: absolute;
			margin-top: -105px;
			width: 104px;
			height: 104px;
			background: rgba(101, 101, 101, 0.6);
			color: #ffffff;
			opacity: 0;
			font-size: 25px;
			display: flex;
			justify-content: center;
			align-items: center;
		}

		.avatar a:hover .mask {
			opacity: 1;
		}

		.avatar a:hover .mask-notImg {
			opacity: 1;
		}

		.username {
			font-size: 20px;
			line-height: 28px;
			font-weight: 500;
			margin-bottom: 4px;
		}
	}

	.webkit-scrollbar {
		overflow-y: auto;
		overflow-x: hidden;
		-ms-overflow-style: none;
		scrollbar-width: none;
		height: calc(30vh);
		border: 1px solid #d9d9d9;
		margin-bottom: 10px;
	}
	.online {
		position: relative;
		left: 32px;
		bottom: 17px;
	}
	:deep(.ant-list-item) {
		padding-bottom: 5px !important;
	}
	:deep(.ant-list-item-meta-avatar) {
		height: 40px;
	}
</style>
