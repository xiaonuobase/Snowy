<template>
	<a-drawer
		title="发送邮件"
		:width="1000"
		:visible="visible"
		:destroy-on-close="true"
		:footer-style="{ textAlign: 'right' }"
		:bodyStyle="{ 'padding-top': '0px' }"
		@close="onClose"
	>
		<a-tabs v-model:activeKey="activeKey">
			<a-tab-pane key="LocalEmailSend" tab="本地邮件">
				<localEmailSend ref="LocalEmailSend" @loadingStart="loadingStart" @loadingEnd="loadingEnd" />
			</a-tab-pane>
			<a-tab-pane key="AliyunEmailSend" tab="阿里云邮件">
				<aliyunEmailSend ref="AliyunEmailSend" @loadingStart="loadingStart" @loadingEnd="loadingEnd" />
			</a-tab-pane>
			<a-tab-pane key="TencentEmailSend" tab="腾讯云邮件">
				<tencentEmailSend ref="TencentEmailSend" @loadingStart="loadingStart" @loadingEnd="loadingEnd" />
			</a-tab-pane>
		</a-tabs>
		<template #footer>
			<a-button style="margin-right: 8px" @click="onClose">关闭</a-button>
			<a-button type="primary" @click="onSubmit" :loading="sendLoading">发送</a-button>
		</template>
	</a-drawer>
</template>

<script setup name="emailForm">
	import localEmailSend from './send/localEmailSend.vue'
	import aliyunEmailSend from './send/aliyunEmailSend.vue'
	import tencentEmailSend from './send/tencentEmailSend.vue'

	const LocalEmailSend = ref()
	const AliyunEmailSend = ref()
	const TencentEmailSend = ref()

	// 默认是关闭状态
	let visible = $ref(false)
	const activeKey = ref('LocalEmailSend')
	const sendLoading = ref(false)
	// 打开抽屉
	const onOpen = () => {
		visible = true
	}
	// 关闭抽屉
	const onClose = () => {
		visible = false
	}
	// 验证并提交数据
	const onSubmit = () => {
		const tabActiveKey = activeKey.value
		if (tabActiveKey === 'LocalEmailSend') {
			LocalEmailSend.value.send()
		} else if (tabActiveKey === 'AliyunEmailSend') {
			AliyunEmailSend.value.send()
		} else if (tabActiveKey === 'TencentEmailSend') {
			TencentEmailSend.value.send()
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
