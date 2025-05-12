<template>
	<xn-form-container
		title="推送消息"
		:width="1000"
		:visible="visible"
		:destroy-on-close="true"
		:bodyStyle="{ 'padding-top': '0px' }"
		@close="onClose"
	>
		<a-tabs v-model:activeKey="activeKey">
			<a-tab-pane key="feishuPushSend" tab="飞书消息">
				<feishu-push-send ref="feishuPushSendRef" @loadingStart="loadingStart" @loadingEnd="loadingEnd" />
			</a-tab-pane>
			<a-tab-pane key="dingPushSend" tab="钉钉消息">
				<ding-push-send ref="dingPushSendRef" @loadingStart="loadingStart" @loadingEnd="loadingEnd" />
			</a-tab-pane>
			<a-tab-pane key="workWechatPushSend" tab="企业微信消息">
				<work-wechat-push-send ref="workWechatPushSendRef" @loadingStart="loadingStart" @loadingEnd="loadingEnd" />
			</a-tab-pane>
		</a-tabs>
		<template #footer>
			<a-button class="xn-mr8" @click="onClose">关闭</a-button>
			<a-button type="primary" @click="onSubmit" :loading="sendLoading">发送</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="pushForm">
	import FeishuPushSend from './send/feishuPushSend.vue'
	import DingPushSend from './send/dingPushSend.vue'
	import WorkWechatPushSend from './send/workWechatPushSend.vue'

	const feishuPushSendRef = ref()
	const dingPushSendRef = ref()
	const workWechatPushSendRef = ref()

	// 默认是关闭状态
	const visible = ref(false)
	const activeKey = ref('feishuPushSend')
	const sendLoading = ref(false)
	const emit = defineEmits({ successful: null })
	// 打开抽屉
	const onOpen = () => {
		visible.value = true
	}
	// 关闭抽屉
	const onClose = () => {
		visible.value = false
		emit('successful')
	}
	// 验证并提交数据
	const onSubmit = () => {
		const tabActiveKey = activeKey.value
		if (tabActiveKey === 'feishuPushSend') {
			feishuPushSendRef.value.send()
		} else if (tabActiveKey === 'dingPushSend') {
			dingPushSendRef.value.send()
		} else if (tabActiveKey === 'workWechatPushSend') {
			workWechatPushSendRef.value.send()
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
