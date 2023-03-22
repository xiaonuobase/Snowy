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
			<a-tab-pane key="AliyunSmsSend" tab="阿里云短信">
				<AliyunSmsSend ref="aliyunSmsSend" @loadingStart="loadingStart" @loadingEnd="loadingEnd" />
			</a-tab-pane>
			<a-tab-pane key="TencentSmsSend" tab="腾讯云短信">
				<TencentSmsSend ref="tencentSmsSend" @loadingStart="loadingStart" @loadingEnd="loadingEnd" />
			</a-tab-pane>
		</a-tabs>
		<template #footer>
			<a-button style="margin-right: 8px" @click="onClose">关闭</a-button>
			<a-button type="primary" @click="onSubmit" :loading="sendLoading">发送</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="smsForm">
	import AliyunSmsSend from './send/AliyunSmsSend.vue'
	import TencentSmsSend from './send/TencentSmsSend.vue'

	const aliyunSmsSend = ref()
	const tencentSmsSend = ref()

	// 默认是关闭状态
	let visible = $ref(false)
	const activeKey = ref('AliyunSmsSend')
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
		if (tabActiveKey === 'AliyunSmsSend') {
			aliyunSmsSend.value.send()
		} else if (tabActiveKey === 'TencentSmsSend') {
			tencentSmsSend.value.send()
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
