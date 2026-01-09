<template>
	<div>
		<!-- 这个组件不需要任何模板内容，因为它只处理WebSocket逻辑 -->
	</div>
</template>

<script setup lang="ts">
	import { ref, onMounted, onUnmounted } from 'vue'
	import { notification } from 'ant-design-vue'
	import tool from '@/utils/tool'
	import { convertUrl } from '@/utils/apiAdaptive'
	const websocketInstance = ref<WebSocket | null>(null)
	const isReconnecting = ref(false)
	const reconnectTimer = ref<NodeJS.Timeout | null>(null)
	const onMessageCallback = ref<((data: string) => void) | null>(null)
	const isReconnectingNum = ref(0)
	const isReconnectingRun = ref(false)
	const closeWebSocketAsync = ref(false)
	const emit = defineEmits(['setWebSocket'])

	const props = defineProps({
		uri: {
			type: String,
			default: ''
		}
	})

	declare global {
		interface WebSocket {
			sendWebSocketMessage: (data: any) => void
			setMessageCallback: (callback: (data: string) => void) => void
		}
	}

	// Keep connection warm to reduce call init latency
	const heartbeatTimer = ref<NodeJS.Timeout | null>(null)

	const startHeartbeat = () => {
		if (heartbeatTimer.value) return
		heartbeatTimer.value = setInterval(() => {
			if (websocketInstance.value && websocketInstance.value.readyState === WebSocket.OPEN) {
				try {
					websocketInstance.value.send('ping')
				} catch (e) {
					stopHeartbeat()
					reconnect()
				}
			}
		}, 25000)
	}
	const stopHeartbeat = () => {
		if (heartbeatTimer.value) {
			clearInterval(heartbeatTimer.value)
			heartbeatTimer.value = null
		}
	}

	//监听websocket状态
	// 重连方法
	const reconnect = () => {
		// 判断是否主动关闭连接
		if (isReconnecting.value) {
			return
		}
		isReconnecting.value = true
		// 重连定时器-每次WebSocket错误方法onerror触发它都会触发
		reconnectTimer.value && clearTimeout(reconnectTimer.value)
		isReconnectingRun.value = true
		reconnectTimer.value = setTimeout(function () {
			isReconnectingNum.value++
			websocketInstance.value = null
			initWebSocket()
			isReconnecting.value = false
			isReconnectingRun.value = false
		}, 1500)
	}
	const initWebSocket = () => {
		let url = props.uri.replace('https', 'wss').replace('http', 'ws') + convertUrl('/ws/im') + '?token=' + tool.data.get('TOKEN')
		if (!('WebSocket' in window)) {
			notification.error({
				message: '错误',
				description: '浏览器不支持WebSocket'
			})
			return
		}
		if (websocketInstance.value && websocketInstance.value.readyState === WebSocket.OPEN) {
			return
		}
		if (websocketInstance.value && websocketInstance.value.readyState === WebSocket.CONNECTING) {
			return
		}
		websocketInstance.value = new WebSocket(url)
		websocketInstance.value.onopen = () => {
			closeWebSocketAsync.value = false
			notification.success({
				message: '初始化IM成功'
			})
			// 回调给父组件
			isReconnectingNum.value = 0
			isReconnecting.value = false
			isReconnectingRun.value = false
			reconnectTimer.value && clearTimeout(reconnectTimer.value)
			startHeartbeat()
			emit('setWebSocket', websocketInstance.value)
		}
		websocketInstance.value.onerror = (e) => {
			if (closeWebSocketAsync.value) {
				return
			}
			if (isReconnectingNum.value <= 5) {
				notification.error({
					message: '错误',
					description: `IM连接异常,${isReconnectingNum.value > 0 ? '连接失败重连中...' : '准备重新连接'}`
				})
			}
			emit('setWebSocket', null)
			stopHeartbeat()
			reconnect()
		}
		websocketInstance.value.onmessage = (e) => {
			if (e.data === 'ok' || e.data === 'pong') return
			if (onMessageCallback.value) {
				onMessageCallback.value(e.data)
			}
		}
		websocketInstance.value.onclose = (e) => {
			stopHeartbeat()
			if (e.code === 1000) {
				notification.success({
					message: 'IM连接关闭'
				})
			} else {
				!isReconnectingRun.value && reconnect()
			}
		}
		if (websocketInstance.value) {
			websocketInstance.value.setMessageCallback = (callback) => {
				onMessageCallback.value = callback
			}
		}
		websocketInstance.value.sendWebSocketMessage = (data) => {
			const msg = JSON.stringify(data)
			try {
				if (websocketInstance.value) {
					websocketInstance.value.send(msg)
				}
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
		stopHeartbeat()
		if (websocketInstance.value) {
			websocketInstance.value.close()
		}
	}
	onMounted(() => {
		initWebSocket()
	})
	window.onbeforeunload = (e) => {
		closeWebSocket()
	}
	onUnmounted(() => {
		if (closeWebSocketAsync.value) {
			return
		}
		closeWebSocketAsync.value = true
		stopHeartbeat()
		closeWebSocket()
	})

	defineExpose({
		initWebSocket,
		closeWebSocket,
		websocketInstance
	})
</script>
