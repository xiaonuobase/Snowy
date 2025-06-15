<template>
	<a-list item-layout="horizontal" :data-source="data">
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
						<qq-outlined v-if="item.type === 'qq'" class="bind-icon" :style="{ color: '#1677FF' }" />
						<wechat-outlined v-if="item.type === 'weChat'" class="bind-icon" :style="{ color: '#1AAD19' }" />
						<alipay-circle-outlined v-if="item.type === 'AliPay'" class="bind-icon" :style="{ color: '#178bf5' }" />
						<mail-outlined v-if="item.type === 'email'" class="bind-icon" :style="{ color: '#fcab43' }" />
						<mobile-outlined v-if="item.type === 'phone'" class="bind-icon" :style="{ color: '#43a0fc' }" />
						<verified-outlined v-if="item.type === 'password'" class="bind-icon" :style="{ color: '#a059e8' }" />
						<GiteeIcon v-if="item.type === 'Gitee'" class="bind-icon xn-wd40" />
					</template>
				</a-list-item-meta>
				<template #actions>
					<a @click="bindCommon(item.type)">{{ item.value ? '修改' : '去绑定' }}</a>
				</template>
			</a-list-item>
		</template>
	</a-list>
	<updatePassword ref="updatePasswordRef" />
	<bind-phone ref="bindPhoneRef" />
	<bind-email ref="bindEmailRef" />
</template>

<script setup>
	import { message } from 'ant-design-vue'
	import UpdatePassword from './bindForm/updatePassword.vue'
	import BindPhone from '@/views/sys/user/userTab/bindForm/bindPhone.vue'
	import BindEmail from '@/views/sys/user/userTab/bindForm/bindEmail.vue'
	// 按需导入图标组件
	import QqOutlined from '@ant-design/icons-vue/QqOutlined'
	import WechatOutlined from '@ant-design/icons-vue/WechatOutlined'
	import AlipayCircleOutlined from '@ant-design/icons-vue/AlipayCircleOutlined'
	import MailOutlined from '@ant-design/icons-vue/MailOutlined'
	import MobileOutlined from '@ant-design/icons-vue/MobileOutlined'
	import VerifiedOutlined from '@ant-design/icons-vue/VerifiedOutlined'
	import { globalStore } from '@/store'

	const updatePasswordRef = ref()
	const bindPhoneRef = ref()
	const bindEmailRef = ref()
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
	const data = [
		{ title: '密码强度', description: '当前密码强度', value: '弱', type: 'password', bindStatus: 0 },
		{
			title: '邮箱',
			description: userInfo && userInfo.value.email ? '已绑定邮箱' : '未绑定邮箱',
			value: userInfo && userInfo.value.email ? userInfo.value.email : '',
			type: 'email',
			bindStatus: 0
		},
		{
			title: '手机号',
			description: userInfo && userInfo.value.phone ? '已绑定手机' : '未绑定手机',
			value: userInfo && userInfo.value.phone ? userInfo.value.phone : '',
			type: 'phone',
			bindStatus: 1
		},
		{ title: '绑定QQ', description: '未绑定', value: '', type: 'qq', bindStatus: 0 },
		{ title: '绑定微信', description: '未绑定', value: '', type: 'weChat', bindStatus: 0 },
		{ title: '绑定支付宝', description: '未绑定', value: '', type: 'AliPay', bindStatus: 0 },
		{ title: '绑定Gitee', description: '未绑定', value: '', type: 'Gitee', bindStatus: 0 }
	]
	const bindCommon = (key) => {
		if (key === 'password') {
			updatePasswordRef.value.onOpen()
		} else if (key === 'phone') {
			bindPhoneRef.value.open(userInfo.value.phone)
		} else if (key === 'email') {
			bindEmailRef.value.open(userInfo.value.email)
		} else {
			message.info('开发中')
		}
	}
	onMounted(() => {
		// 获取绑定情况
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
