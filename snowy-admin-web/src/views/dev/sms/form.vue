<template>
	<xn-form-container
		title="发送短信"
		:width="550"
		:visible="visible"
		:destroy-on-close="true"
		:bodyStyle="{ 'padding-top': '0px' }"
		@close="onClose"
	>
		<a-tabs v-model:activeKey="activeKey">
			<a-tab-pane key="XiaonuoSmsSend" tab="小诺短信">
				<xiaouo-sms-send ref="xiaonuoSmsSendRef" @loadingStart="loadingStart" @loadingEnd="loadingEnd" />
			</a-tab-pane>
			<a-tab-pane key="AliyunSmsSend" tab="阿里云短信">
				<aliyun-sms-send ref="aliyunSmsSendRef" @loadingStart="loadingStart" @loadingEnd="loadingEnd" />
			</a-tab-pane>
			<a-tab-pane key="TencentSmsSend" tab="腾讯云短信">
				<tencent-sms-send ref="tencentSmsSendRef" @loadingStart="loadingStart" @loadingEnd="loadingEnd" />
			</a-tab-pane>
		</a-tabs>
		<template #footer>
			<a-button class="xn-mr8" @click="onClose">关闭</a-button>
			<a-button type="primary" @click="onSubmit" :loading="sendLoading">发送</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="smsForm">
	import XiaouoSmsSend from './send/xiaonuoSmsSend.vue'
	import AliyunSmsSend from './send/aliyunSmsSend.vue'
	import TencentSmsSend from './send/tencentSmsSend.vue'

	const xiaonuoSmsSendRef = ref()
	const aliyunSmsSendRef = ref()
	const tencentSmsSendRef = ref()

	// 默认是关闭状态
	const visible = ref(false)
	const activeKey = ref('XiaonuoSmsSend')
	const sendLoading = ref(false)
	// 打开抽屉
	const onOpen = () => {
		visible.value = true
	}
	// 关闭抽屉
	const onClose = () => {
		visible.value = false
	}
	// 验证并提交数据
	const onSubmit = () => {
		const tabActiveKey = activeKey.value
		if (tabActiveKey === 'AliyunSmsSend') {
			aliyunSmsSendRef.value.send()
		} else if (tabActiveKey === 'TencentSmsSend') {
			tencentSmsSendRef.value.send()
		} else if (tabActiveKey === 'XiaonuoSmsSend') {
			xiaonuoSmsSendRef.value.send()
		}
	}
	// 请求loading开始
	const loadingStart = () => {
		sendLoading.value = true
	}
	// 请求loading结束
	const loadingEnd = () => {
		sendLoading.value = false
	}
	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>
