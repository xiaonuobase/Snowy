import { notification } from 'ant-design-vue'
import tool from '@/utils/tool'

let url = import.meta.env.VITE_API_BASEURL.replace('http', 'ws') + '/ws/im?token=' + tool.data.get('TOKEN')
// WebSocket实例
let websocketInstance = null

// 重连定时器实例
let reconnectTimer = null

// WebSocket重连开关
let isReconnecting = false

/**
 * WebSocket对象
 */
const websocket = {
	// 初始化ws连接
	InitWebSocket() {
		// 判断浏览器是否支持WebSocket
		if (!('WebSocket' in window)) {
			notification.error({
				message: '错误',
				description: errorName
			})
			return
		}

		// 创建WebSocket实例
		websocketInstance = new WebSocket(url)

		// 监听WebSocket连接
		websocketInstance.onopen = () => {
			notification.success({
				message: '初始化im成功'
			})
		}

		// 监听WebSocket连接错误信息
		websocketInstance.onerror = (e) => {
			notification.error({
				message: '错误',
				description: 'WebSocket连接错误信息'
			})
			// 打开重连
			reconnect()
		}

		// 监听WebSocket接收消息
		websocketInstance.onmessage = (e) => {
			// 心跳消息不做处理
			if (e.data === 'ok') {
				return
			}
			// 调用回调函数处理接收到的消息
			if (websocket.onMessageCallback) {
				websocket.onMessageCallback(e.data)
			}
		}
		websocketInstance.onclose = (e) => {
			notification.error({
				message: '错误',
				description: 'WebSocket连接关闭'
			})
			// 打开重连
			reconnect()
		}
	},

	// WebSocket连接关闭方法
	Close() {
		// 关闭断开重连机制
		isReconnecting = true
		websocketInstance.close()
	},

	// WebSocket发送信息方法
	Send(data) {
		// 处理发送数据JSON字符串
		const msg = JSON.stringify(data)
		// 发送消息给后端
		websocketInstance.send(msg)
	},

	// 暴露WebSocket实例，其他地方调用就调用这个
	getWebSocket() {
		return websocketInstance
	},

	// 新增回调函数用于处理收到的消息
	onMessageCallback: null,

	// 设置消息处理回调函数
	setMessageCallback(callback) {
		this.onMessageCallback = callback
	}
}

// 监听窗口关闭事件，当窗口关闭时-每一个页面关闭都会触发-扩张需求业务
window.onbeforeunload = function () {
	// 在窗口关闭时关闭 WebSocket 连接
	websocket.Close()
}

// 重连方法
const reconnect = () => {
	// 判断是否主动关闭连接
	if (isReconnecting) {
		return
	}
	// 重连定时器-每次WebSocket错误方法onerror触发它都会触发
	reconnectTimer && clearTimeout(reconnectTimer)
	reconnectTimer = setTimeout(function () {
		websocket.InitWebSocket()
		isReconnecting = false
	}, 4000)
}

// 暴露对象
export default websocket
