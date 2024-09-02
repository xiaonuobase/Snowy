<template>
	<div>
		<!-- 这个组件不需要任何模板内容，因为它只处理WebSocket逻辑 -->
	</div>
</template>

<script setup lang="ts">
	import { ref, onMounted, onBeforeUnmount, defineProps, defineEmits, defineExpose } from 'vue'
	import { notification } from 'ant-design-vue'
	import tool from '@/utils/tool'

	const websocketInstance = ref(null)
	const isReconnecting = ref(false)
	const reconnectTimer = ref(null)
	const onMessageCallback = ref(null)
	const isReconnectingNum = ref(0)
	const isReconnectingRun = ref(false)
	const emit = defineEmits(['setWebSocket'])

	const props = defineProps({
		uri: {
			type: String,
			default: ''
		}
	})

	const url = props.uri.replace('http', 'ws') + '/ws/im?token=' + tool.data.get('TOKEN')

	//监听websocket状态
	// 重连方法
	const reconnect = () => {
		// 判断是否主动关闭连接
		if (isReconnecting.value) {
			return
		}
		// 重连定时器-每次WebSocket错误方法onerror触发它都会触发
		reconnectTimer.value && clearTimeout(reconnectTimer.value)
		isReconnectingRun.value = true
		reconnectTimer.value = setTimeout(function () {
			isReconnectingNum.value++
			websocketInstance.value = null
			initWebSocket()
			isReconnecting.value = false
			isReconnectingRun.value = false
		}, 4000)
	}
	const initWebSocket = () => {
		if (!('WebSocket' in window)) {
			notification.error({
				message: '错误',
				description: '浏览器不支持WebSocket'
			})
			return
		}
		if (websocketInstance.value) {
			return
		}
		websocketInstance.value = new WebSocket(url)
		websocketInstance.value.onopen = () => {
			notification.success({
				message: '初始化IM成功'
			})
			// 回调给父组件
			isReconnectingNum.value = 0
			isReconnecting.value = false
			isReconnectingRun.value = false
			reconnectTimer.value && clearTimeout(reconnectTimer.value)
			emit('setWebSocket', websocketInstance.value)
		}
		websocketInstance.value.onerror = (e) => {
			if (isReconnectingNum.value <= 5) {
				notification.error({
					message: '错误',
					description: `IM连接异常,${isReconnectingNum.value > 0 ? '连接失败重连中...' : '准备重新连接'}`
				})
			}
			emit('setWebSocket', null)
			isReconnectingRun.value && reconnect()
		}
		websocketInstance.value.onmessage = (e) => {
			if (e.data === 'ok') return
			if (onMessageCallback.value) {
				onMessageCallback.value(e.data)
			}
		}
		websocketInstance.value.onclose = (e) => {
			if (e.code === 1000) {
				notification.success({
					message: 'IM连接关闭'
				})
			} else {
				!isReconnectingRun.value && reconnect()
			}
		}
		websocketInstance.value.setMessageCallback = (callback) => {
			onMessageCallback.value = callback
		}
		websocketInstance.value.sendWebSocketMessage = (data) => {
			const msg = JSON.stringify(data)
			try {
				websocketInstance.value.send(msg)
			} catch (e) {
				notification.error({
					message: 'IM发送消息失败'
				})
				reconnect()
			}
		}
	}
	const closeWebSocket = () => {
		//主动关闭连接
		isReconnecting.value = true
		websocketInstance.value.close()
	}
	onMounted(() => {
		initWebSocket()
	})
	onBeforeUnmount(() => {
		closeWebSocket()
	})
	window.onbeforeunload = () => {
		closeWebSocket()
	}
	defineExpose({
		initWebSocket,
		closeWebSocket,
		websocketInstance
	})
</script>
