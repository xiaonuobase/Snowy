<template>
	<div class="msg panel-item" @click="showMsg">
		<a-badge :count="unreadMessageNum" class="badge">
			<comment-outlined />
		</a-badge>
		<a-drawer v-model:open="msgVisible" title="新消息" placement="right" :width="500">
			<a-list :data-source="messageList" size="small" class="mb-3" :loading="miniMessageLoading">
				<template #renderItem="{ item }">
					<a-list-item>
						<a-list-item-meta :description="item.createTime">
							<template #title>
								<a @click="messageDetail(item)">{{ item.subject }}</a>
							</template>
						</a-list-item-meta>
					</a-list-item>
				</template>
			</a-list>
			<a-space class="xn-fdr">
				<a-button v-if="unreadMessageNum > 0" @click="markRead">全部设为已读</a-button>
				<a-button type="primary" @click="leaveFor('/usercenter')">消息中心</a-button>
			</a-space>
		</a-drawer>
		<xn-form-container title="详情" :width="700" :open="visible" :destroy-on-close="true" @close="onClose">
			<a-form ref="formRef" :model="formData" layout="vertical">
				<a-form-item label="主题：" name="subject">
					<span>{{ formData.subject }}</span>
				</a-form-item>
				<a-form-item label="发送时间：" name="createTime">
					<span>{{ formData.createTime }}</span>
				</a-form-item>
				<a-form-item label="内容：" name="content">
					<span>{{ formData.content }}</span>
				</a-form-item>
				<a-form-item label="查收情况：" name="receiveInfoList">
					<s-table
						ref="tableRef"
						:columns="columns"
						:data="loadData"
						:alert="false"
						:showPagination="false"
						bordered
						:row-key="(record) => record.id"
					>
						<template #bodyCell="{ column, record }">
							<template v-if="column.dataIndex === 'read'">
								<span v-if="record.read" class="xn-color-d9d9d9">已读</span>
								<span v-else class="xn-color-ff4d4f">未读</span>
							</template>
						</template>
					</s-table>
				</a-form-item>
			</a-form>
		</xn-form-container>
	</div>
</template>

<script setup name="devUserMessage">
	import indexApi from '@/api/sys/indexApi'
	import { message } from 'ant-design-vue'
	import router from '@/router'
	import { onMounted } from 'vue'
	import sysConfig from '@/config'
	import { convertUrl } from '@/utils/apiAdaptive'
	import { EventSourcePolyfill } from 'event-source-polyfill'
	import tool from '@/utils/tool'

	const miniMessageLoading = ref(false)
	const msgVisible = ref(false)
	const messageList = ref([])
	const unreadMessageNum = ref(0)

	onMounted(() => {
		createSseConnect()
	})

	// 创建sse连接
	const createSseConnect = () => {
		if (window.EventSource) {
			let clientId = tool.data.get('CLIENTID') ? tool.data.get('CLIENTID') : ''
			let url = sysConfig.API_URL + convertUrl('/dev/message/createSseConnect?clientId=') + clientId
			// heartbeatTimeout:心跳超时监测 30s
			let source = new EventSourcePolyfill(url, {
				headers: { token: tool.data.get('TOKEN') },
				heartbeatTimeout: 300000
			})
			// 监听打开事件
			source.addEventListener('open', (e) => {})
			// 监听消息事件
			source.addEventListener('message', (e) => {
				const result = JSON.parse(e.data)
				const code = result.code
				const data = result.data
				if (code === 200) {
					unreadMessageNum.value = data
				} else if (code === 0) {
					// 初次建立连接，客户端id储存本地
					tool.data.set('CLIENTID', data)
				}
			})
			// 监听错误事件
			source.addEventListener('error', (e) => {
				console.error('发生错误，消息实时获取已断开与服务器的连接')
				source.close()
			})
		} else {
			message.warning('该浏览器不支持消息功能')
		}
	}

	// 获取站内信列表
	const getMessageList = () => {
		miniMessageLoading.value = true
		indexApi
			.indexMessageList()
			.then((data) => {
				messageList.value = data
			})
			.finally(() => {
				miniMessageLoading.value = false
			})
	}
	// 显示站内信
	const showMsg = () => {
		msgVisible.value = true
		getMessageList()
	}
	// 标记已读
	const markRead = () => {
		messageList.value = []
		unreadMessageNum.value = 0
		indexApi.indexMessageAllMarkRead().then((data) => {
			message.success('操作成功')
		})
	}
	// 跳转，用于点击消息中心
	const leaveFor = (url = '/') => {
		msgVisible.value = false
		router.replace({ path: url, query: { tab: 'userMessage' } })
	}
	// 点击详情
	const messageDetail = (message) => {
		visible.value = true
		const param = {
			id: message.id
		}
		indexApi.indexMessageDetail(param).then((data) => {
			Object.assign(message, data)
			formData.value = message
			receiveInfoList.value = data.receiveInfoList
			tableRef.value.refresh(true)
		})
		unreadMessageNum.value = Math.max(unreadMessageNum.value - 1, 0)
	}

	const loadData = () => {
		return new Promise((resolve) => {
			resolve(receiveInfoList.value)
		})
	}
	// 以下部分是抽屉的
	const visible = ref(false)
	const formRef = ref()
	const receiveInfoList = ref([])
	const formData = ref({})
	const tableRef = ref()
	const columns = [
		{
			title: '姓名',
			dataIndex: 'receiveUserName'
		},
		{
			title: '是否已读',
			dataIndex: 'read',
			width: 120
		}
	]
	// 关闭抽屉
	const onClose = () => {
		visible.value = false
		formData.value = {}
		receiveInfoList.value = []
		getMessageList()
	}
</script>

<style scoped>
	:deep(.ant-badge-count) {
		padding: 0px;
		min-width: 15px;
		height: 15px;
		line-height: 15px;
	}
</style>
