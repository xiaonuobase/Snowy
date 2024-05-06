<template>
	<a-row :gutter="10">
		<a-col :xs="24" :sm="24" :md="7" :lg="7" :xl="7" class="mb-3">
			<a-card :bordered="false">
				<div class="account-center-avatarHolder">
					<div class="avatar">
						<a-spin size="small" :spinning="avatarLoading">
							<img :src="userInfo.avatar" />
						</a-spin>
						<a @click="uploadLogo">
							<div v-if="userInfo" :class="userInfo.avatar ? 'mask' : 'mask-notImg'"><upload-outlined /></div>
						</a>
					</div>
					<div class="username">{{ userInfo.name }}</div>
					<div class="bio">{{ userInfo.nickname }}</div>
				</div>
				<div class="account-center-detail">
					<p><i class="title"></i>{{ userInfo.positionName }}</p>
					<p><i class="group"></i>{{ userInfo.orgName }}</p>
					<p>
						<i class="address"></i>
						<span>{{ userInfo.homeAddress ? userInfo.homeAddress : '暂无地址' }}</span>
					</p>
				</div>
				<a-divider />
				<div class="account-center-team">
					<div class="mb-2 xn-wd" v-if="userInfo.signature">
						<a-image :src="userInfo.signature" width="100%" class="xn-bdr236 xn-ht120" />
					</div>
					<a-button @click="xnSignNameRef.show()">打开签名板</a-button>
					<XnSignName ref="xnSignNameRef" :image="userInfo.signature" @successful="signSuccess" />
				</div>
			</a-card>
		</a-col>
		<a-col :xs="24" :sm="24" :md="17" :lg="17" :xl="17">
			<a-card
				:bordered="false"
				class="xn-wd"
				:tab-list="tabList"
				:active-tab-key="noTitleKey"
				@tabChange="(key) => onTabChange(key, 'key')"
			>
				<p v-if="noTitleKey === 'accountBasic'">
					<accountBasic />
				</p>
				<p v-if="noTitleKey === 'organizationChart'">
					<organizationChart />
				</p>
				<p v-if="noTitleKey === 'accountBind'">
					<accountBind />
				</p>
				<p v-if="noTitleKey === 'shortcutSetting'">
					<shortcutSetting />
				</p>
				<p v-if="noTitleKey === 'userMessage'">
					<userMessage />
				</p>
			</a-card>
		</a-col>
	</a-row>
	<CropUpload ref="cropUploadRef" :img-src="userInfo ? userInfo.avatar : undefined" @successful="cropUploadSuccess" />
</template>

<script setup name="userCenter">
	import { onMounted } from 'vue'
	import { useRoute } from 'vue-router'
	import tool from '@/utils/tool'
	import { globalStore } from '@/store'
	import userCenterApi from '@/api/sys/userCenterApi'
	import accountBasic from './userTab/accountBasic.vue'
	import CropUpload from '@/components/CropUpload/index.vue'
	import organizationChart from './userTab/organizationChart.vue'
	import shortcutSetting from './userTab/shortcutSetting.vue'
	import accountBind from './userTab/accountBind.vue'
	import userMessage from './userTab/userMessage.vue'

	const store = globalStore()
	const userInfo = computed(() => {
		if (store.userInfo) {
			return store.userInfo
		} else {
			return {
				avatar: '',
				name: '',
				nickname: '',
				signature: '',
				orgName: '',
				positionName: ''
			}
		}
	})
	const cropUploadRef = ref()
	const avatarLoading = ref(false)
	const uploadLogo = () => {
		cropUploadRef.value.show()
	}
	const xnSignNameRef = ref()
	const tabList = [
		{
			key: 'accountBasic',
			tab: '基本信息'
		},
		{
			key: 'organizationChart',
			tab: '组织架构'
		},
		{
			key: 'accountBind',
			tab: '账号相关'
		},
		{
			key: 'shortcutSetting',
			tab: '快捷方式'
		},
		{
			key: 'userMessage',
			tab: '我的消息'
		}
	]
	const noTitleKey = ref('accountBasic')
	const onTabChange = (key) => {
		noTitleKey.value = key
	}
	const Route = useRoute()
	onMounted(() => {
		if (Route.query.tab) {
			noTitleKey.value = Route.query.tab
		}
	})
	// 头像裁剪图片回调
	const cropUploadSuccess = (data) => {
		// 转换为file类型
		const result = new File([data.blobData], data.fileName, { type: 'image/jpeg', lastModified: Date.now() })
		const fileData = new FormData()
		fileData.append('file', result)
		avatarLoading.value = true
		userCenterApi.userUpdateAvatar(fileData).then((data) => {
			avatarLoading.value = false
			userInfo.value.avatar = data
			// 更新缓存
			tool.data.set('USER_INFO', userInfo.value)
			store.setUserInfo(userInfo.value)
		})
	}
	// 签名板组件回调
	const signSuccess = (value) => {
		const param = {
			signature: value
		}
		userCenterApi.userUpdateSignature(param).then(() => {
			userInfo.value.signature = value
			// 更新缓存
			tool.data.set('USER_INFO', userInfo.value)
			store.setUserInfo(userInfo.value)
		})
	}
</script>

<style lang="less" scoped>
	.xn-ht120 {
		height: 120px;
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
			margin-top: -24px;
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

	.account-center-detail {
		p {
			margin-bottom: 8px;
			padding-left: 26px;
			position: relative;
		}
		i {
			position: absolute;
			height: 14px;
			width: 14px;
			left: 0;
			top: 4px;
			background: url(https://gw.alipayobjects.com/zos/rmsportal/pBjWzVAHnOOtAUvZmZfy.svg);
		}
		.title {
			background-position: 0 0;
		}
		.group {
			background-position: 0 -22px;
		}
		.address {
			background-position: 0 -44px;
		}
	}
	.teamTitle {
		font-weight: 500;
		margin-bottom: 12px;
	}
	.account-center-team {
		.members {
			a {
				display: block;
				margin: 12px 0;
				line-height: 24px;
				height: 24px;
				.member {
					font-size: 14px;
					line-height: 24px;
					max-width: 100px;
					vertical-align: top;
					margin-left: 12px;
					transition: all 0.3s;
					display: inline-block;
				}
			}
		}
	}
</style>
