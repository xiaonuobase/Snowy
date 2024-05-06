<template>
	<xn-form-container
		title="发送邮件"
		:width="1000"
		:visible="visible"
		:destroy-on-close="true"
		:bodyStyle="{ 'padding-top': '0px' }"
		@close="onClose"
	>
		<a-tabs v-model:activeKey="activeKey">
			<a-tab-pane key="localEmailSend" tab="本地邮件">
				<localEmailSend ref="localEmailSendRef" @loadingStart="loadingStart" @loadingEnd="loadingEnd" />
			</a-tab-pane>
			<a-tab-pane key="aliyunEmailSend" tab="阿里云邮件">
				<aliyunEmailSend ref="aliyunEmailSendRef" @loadingStart="loadingStart" @loadingEnd="loadingEnd" />
			</a-tab-pane>
			<a-tab-pane key="tencentEmailSend" tab="腾讯云邮件">
				<tencentEmailSend ref="tencentEmailSendRef" @loadingStart="loadingStart" @loadingEnd="loadingEnd" />
			</a-tab-pane>
		</a-tabs>
		<template #footer>
			<a-button class="xn-mr8" @click="onClose">关闭</a-button>
			<a-button type="primary" @click="onSubmit" :loading="sendLoading">发送</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="emailForm">
	import LocalEmailSend from './send/localEmailSend.vue'
	import AliyunEmailSend from './send/aliyunEmailSend.vue'
	import TencentEmailSend from './send/tencentEmailSend.vue'

	const localEmailSendRef = ref()
	const aliyunEmailSendRef = ref()
	const tencentEmailSendRef = ref()

	// 默认是关闭状态
	const visible = ref(false)
	const activeKey = ref('localEmailSend')
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
		if (tabActiveKey === 'localEmailSend') {
			localEmailSendRef.value.send()
		} else if (tabActiveKey === 'aliyunEmailSend') {
			aliyunEmailSendRef.value.send()
		} else if (tabActiveKey === 'tencentEmailSend') {
			tencentEmailSendRef.value.send()
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
