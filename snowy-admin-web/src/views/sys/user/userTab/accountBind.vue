<template>
	<a-list item-layout="horizontal" :data-source="bindInfoList">
		<template #renderItem="{ item }">
			<a-list-item>
				<a-list-item-meta class="list-item-meta">
					<template #title>{{ item.title }}</template>
					<template #description>
						<span class="security-list-description">{{ item.description }}</span>
						<span v-if="item.value"> : </span>
						<span class="security-list-value">{{ item.value }}</span>
					</template>
					<template #avatar>
						<wechat-outlined v-if="item.type === 'weChat'" class="bind-icon" :style="{ color: '#1AAD19' }" />
						<mail-outlined v-if="item.type === 'email'" class="bind-icon" :style="{ color: '#fcab43' }" />
						<mobile-outlined v-if="item.type === 'phone'" class="bind-icon" :style="{ color: '#43a0fc' }" />
						<verified-outlined v-if="item.type === 'password'" class="bind-icon" :style="{ color: '#a059e8' }" />
						<usb-outlined v-if="item.type === 'otp'" class="bind-icon" :style="{ color: '#1AAD19' }" />
						<GiteeIcon v-if="item.type === 'Gitee'" class="bind-icon xn-wd40" />
					</template>
				</a-list-item-meta>
				<template #actions>
					<a @click="bindCommon(item)">{{ item.value ? (item.type === 'otp'?'解绑' : '修改') : '去绑定' }}</a>
				</template>
			</a-list-item>
		</template>
	</a-list>
	<updatePassword ref="updatePasswordRef" />
	<bind-phone ref="bindPhoneRef" />
	<bind-email ref="bindEmailRef" />
	<bind-otp ref="bindOtpRef" @successful="getOtpInfoBindStatus()"/>
</template>

<script setup>
	import { message } from 'ant-design-vue'
	import UpdatePassword from './bindForm/updatePassword.vue'
	import BindPhone from '@/views/sys/user/userTab/bindForm/bindPhone.vue'
	import BindEmail from '@/views/sys/user/userTab/bindForm/bindEmail.vue'
	import BindOtp from '@/views/sys/user/userTab/bindForm/bindOtp.vue'
	// 按需导入图标组件
	import WechatOutlined from '@ant-design/icons-vue/WechatOutlined'
	import MailOutlined from '@ant-design/icons-vue/MailOutlined'
	import MobileOutlined from '@ant-design/icons-vue/MobileOutlined'
	import VerifiedOutlined from '@ant-design/icons-vue/VerifiedOutlined'
	import UsbOutlined from '@ant-design/icons-vue/UsbOutlined'
	import { globalStore } from '@/store'
	import userCenterApi from "@/api/sys/userCenterApi";

	const updatePasswordRef = ref()
	const bindPhoneRef = ref()
	const bindEmailRef = ref()
	const bindOtpRef = ref()
	const store = globalStore()
	const userInfo = computed(() => {
		if (store.userInfo) {
			return store.userInfo
		} else {
			return {
				phone: '',
				name: '',
				email: ''
			}
		}
	})
	// 获取绑定的情况
	const bindInfoList = ref([
		{ title: '密码强度', description: '当前密码强度', value: '弱', type: 'password', bindStatus: 0 },
		{
			title: '邮箱',
			description: userInfo && userInfo.value.email ? '已绑定邮箱' : '未绑定邮箱',
			value: userInfo && userInfo.value.email ? userInfo.value.email : '',
			type: 'email',
			bindStatus: userInfo && userInfo.value.email
		},
		{
			title: '手机号',
			description: userInfo && userInfo.value.phone ? '已绑定手机' : '未绑定手机',
			value: userInfo && userInfo.value.phone ? userInfo.value.phone : '',
			type: 'phone',
			bindStatus: userInfo && userInfo.value.phone
		},
		{ title: '绑定微信', description: '未绑定', value: '', type: 'weChat', bindStatus: 0 },
		{ title: '绑定Gitee', description: '未绑定', value: '', type: 'Gitee', bindStatus: 0 }
	])
	const bindCommon = (item) => {
		let key = item.type
		if (key === 'password') {
			updatePasswordRef.value.onOpen()
		} else if (key === 'phone') {
			bindPhoneRef.value.open()
		} else if (key === 'email') {
			bindEmailRef.value.open()
		} else if (key === 'otp') {
			if(item.value) {
				bindOtpRef.value.onOpen('unbind')
			} else {
				bindOtpRef.value.onOpen('bind')
			}
		} else {
			message.info('请在登录页使用三方登录后输入账号信息' + item.title)
		}
	}
	// 获取动态口令绑定状态
	const getOtpInfoBindStatus = async () => {
		userCenterApi.userCenterGetOtpInfoBindStatus().then((data) => {
			bindInfoList.value[3] = {
				title: '动态口令',
				description: data ? '已绑定动态口令' : '未绑定动态口令',
				value: data ? '******' : '',
				type: 'otp',
				bindStatus: data
			}
		})
	}
	onMounted(async () => {
		// 获取动态口令绑定状态
		await getOtpInfoBindStatus()
	})
</script>

<style scoped>
	.list-item-meta {
		align-items: center;
	}
	.bind-icon {
		padding-left: 10px;
		font-size: 30px;
	}
	.xn-wd40 {
		width: 40px;
	}
</style>
