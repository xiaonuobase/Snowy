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
</template>

<script setup>
	import { message } from 'ant-design-vue'
	import UpdatePassword from './bindForm/updatePassword.vue'

	const updatePasswordRef = ref()
	// 获取绑定的情况
	const data = [
		{ title: '密码强度', description: '当前密码强度', value: '弱', type: 'password', bindStatus: 0 },
		/*{ title: '密保手机', description: '已绑定手机', value: '138****8293', type: 'phone', bindStatus: 1 },
		{ title: '密保邮箱', description: '未绑定邮箱', value: '', type: 'email', bindStatus: 0 },
		{ title: '实名状态', description: '未实名', value: '', type: 'userReal', bindStatus: 0 },*/
		{ title: '绑定QQ', description: '未绑定', value: '', type: 'qq', bindStatus: 0 },
		{ title: '绑定微信', description: '未绑定', value: '', type: 'weChat', bindStatus: 0 },
		{ title: '绑定支付宝', description: '未绑定', value: '', type: 'AliPay', bindStatus: 0 },
		{ title: '绑定Gitee', description: '未绑定', value: '', type: 'Gitee', bindStatus: 0 }
	]
	const bindCommon = (key) => {
		if (key === 'password') {
			updatePasswordRef.value.onOpen()
		} else {
			message.info('开发中')
		}
	}
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
