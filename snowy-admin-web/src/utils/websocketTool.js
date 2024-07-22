//需求：在JavaScript中实现WebSocket连接失败后3分钟内尝试重连3次的功能，你可以设置一个重连策略，
//		 包括重连的间隔时间、尝试次数以及总时间限制。

/**
 * @param {string} url	Url to connect
 * @param {number} maxReconnectAttempts Maximum number of times
 * @param {number} reconnect Timeout
 * @param {number} reconnectTimeout Timeout
 *
 */
var websocket = null
var reconnectCount = null
var reconnectTimeout = null

var maxReconnectAttempts = 3
var reconnectInterval = 3000

const InitWebSocket = (url, messageCallBack) => {
	console.log('connecting...')
	websocket = new WebSocket(url)

	//连接成功建立的回调方法
	websocket.onopen = () => {
		console.log('WebSocket Connection Opened!')
		reconnectCount = 0
	}
	//连接关闭的回调方法
	websocket.onclose = (event) => {
		console.log('WebSocket Connection Closed:', event)
		reconnect()
	}
	//连接发生错误的回调方法
	websocket.onerror = (error) => {
		console.error('WebSocket Connection Error:', error)
		reconnect() //重连
	}
	//接收到消息的回调方法
	websocket.onmessage = messageCallBack
}

// 重连方法
function reconnect() {
	console.log('WebSocket重连开关', isReconnecting)
	// 判断是否主动关闭连接
	if (isReconnecting) {
		return
	}
	// 重连定时器-每次WebSocket错误方法onerror触发它都会触发
	reconnectTimer && clearTimeout(reconnectTimer)
	reconnectTimer = setTimeout(function () {
		console.log('WebSocket执行断线重连...')
		// 获取username（假设为测试username写死，现在是动态获取）
		const username = useUserStore().user.username
		websocket.Init(username)
		isReconnecting = false
	}, 4000)
}

//关闭连接
// WebSocket连接关闭方法
function Close() {
	// 关闭断开重连机制
	isReconnecting = true
	websocket.close()
	// ElMessage.error('WebSocket断开连接')
}

//发送消息
function send(data) {
	if (this.websocket && this.websocket.readyState === WebSocket.OPEN) {
		this.websocket.send(data)
	} else {
		console.error('WebSocket Connection is not open. Cannot send message.')
	}
}

//监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
window.onbeforeunload = function () {
	Close()
}
//关闭连接
function closeWebSocket() {
	Close()
}

export { InitWebSocket, websocket, closeWebSocket, send }
