<template>
	<div class="msg panel-item" @click="showMsg">
		<a-badge :count="unreadMessageNum" class="badge">
			<bell-outlined class="msg-icon-trigger" />
		</a-badge>
		<a-drawer
			v-model:open="msgVisible"
			title="站内消息"
			placement="right"
			:width="500"
			:body-style="{ padding: '0' }"
			:footer-style="{ textAlign: 'right', borderTop: '1px solid #f0f0f0' }"
		>
			<div class="msg-list-container">
				<a-list :data-source="messageList" :loading="miniMessageLoading" item-layout="horizontal">
					<template #renderItem="{ item }">
						<a-list-item class="msg-item" :class="{ unread: !item.read }" @click="messageDetail(item)">
							<a-list-item-meta>
								<template #avatar>
									<a-avatar
										shape="square"
										class="msg-avatar"
										:style="{ backgroundColor: item.read ? '#d9d9d9' : '#1890ff' }"
									>
										<template #icon><BellOutlined /></template>
									</a-avatar>
								</template>
								<template #title>
									<div class="msg-title-row">
										<span class="msg-title text-ellipsis">{{ item.subject }}</span>
										<span class="msg-time">{{ item.createTime }}</span>
									</div>
								</template>
								<template #description>
									<div class="msg-desc text-ellipsis">{{ item.content || '暂无摘要' }}</div>
								</template>
							</a-list-item-meta>
						</a-list-item>
					</template>
					<template #empty>
						<a-empty description="暂无新消息" style="margin-top: 50px" />
					</template>
				</a-list>
			</div>

			<template #footer>
				<a-space>
					<a-button v-if="unreadMessageNum > 0" @click="markRead">全部已读</a-button>
					<a-button type="primary" @click="leaveFor('/usercenter')">消息中心</a-button>
				</a-space>
			</template>
		</a-drawer>

		<xn-form-container title="消息详情" :width="700" :open="visible" :destroy-on-close="true" @close="onClose">
			<a-descriptions bordered :column="1" size="middle" class="mb-4">
				<a-descriptions-item label="主题">{{ formData.subject }}</a-descriptions-item>
				<a-descriptions-item label="发送时间">{{ formData.createTime }}</a-descriptions-item>
				<a-descriptions-item label="内容">
					<div class="msg-content-detail">{{ formData.content }}</div>
				</a-descriptions-item>
			</a-descriptions>

			<div class="receive-info-title">查收情况</div>
			<s-table
				ref="tableRef"
				:columns="columns"
				:data="loadData"
				:alert="false"
				:showPagination="false"
				bordered
				:row-key="(record) => record.id"
				compSize="small"
			>
				<template #bodyCell="{ column, record }">
					<template v-if="column.dataIndex === 'read'">
						<a-tag :color="record.read ? 'green' : 'red'">
							{{ record.read ? '已读' : '未读' }}
						</a-tag>
					</template>
				</template>
			</s-table>
		</xn-form-container>
	</div>
</template>

<script setup name="devUserMessage">
	import indexApi from '@/api/sys/indexApi'
	import { message } from 'ant-design-vue'
	import { BellOutlined } from '@ant-design/icons-vue'
	import router from '@/router'
	import { onMounted } from 'vue'
	import sysConfig from '@/config'
	import { convertUrl } from '@/utils/apiAdaptive'
	import tool from '@/utils/tool'

	const miniMessageLoading = ref(false)
	const msgVisible = ref(false)
	const messageList = ref([])
	const unreadMessageNum = ref(0)

	onMounted(() => {
		createWebSocketConnect()
	})

	// 创建ws连接
	const createWebSocketConnect = () => {
		if ('WebSocket' in window) {
			let url = sysConfig.API_URL + convertUrl('/dev/message/ws') + '?token=' + tool.data.get('TOKEN')
			url = url.replace('https://', 'wss://').replace('http://', 'ws://')
			const socket = new WebSocket(url)
			// 监听打开事件
			socket.onopen = () => {
				// console.log('WebSocket连接已打开')
			}
			// 监听消息事件
			socket.onmessage = (event) => {
				const result = JSON.parse(event.data)
				const code = result.code
				const data = result.data
				if (code === 200) {
					unreadMessageNum.value = data
				}
			}
			// 监听错误事件
			socket.onerror = () => {
				console.error('WebSocket连接发生错误')
			}
			// 监听关闭事件
			socket.onclose = () => {
				// console.log('WebSocket连接已关闭')
			}
		} else {
			message.warning('该浏览器不支持WebSocket消息功能')
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
			dataIndex: 'read'
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

<style scoped lang="less">
	.msg {
		height: 100%;
		display: flex;
		align-items: center;
		justify-content: center;
		padding: 0 10px;
		cursor: pointer;
		transition: all 0.3s;
	}
	.msg:hover {
		background: rgba(0, 0, 0, 0.025);
	}
	.msg-icon-trigger {
		font-size: 16px;
	}

	.msg-list-container {
		max-height: calc(100vh - 150px);
		overflow-y: auto;
	}

	.msg-item {
		cursor: pointer;
		padding: 12px 16px;
		transition: background-color 0.3s;
		border-bottom: 1px solid #f0f0f0;

		&:hover {
			background-color: #fafafa;
		}

		&.unread {
			background-color: #f0f9ff;
			&:hover {
				background-color: #e6f7ff;
			}
			.msg-title {
				font-weight: 600;
				color: #1890ff;
			}
		}
	}

	.msg-avatar {
		margin-top: 4px;
	}

	.msg-title-row {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 4px;
	}

	.msg-title {
		flex: 1;
		color: #333;
		font-size: 14px;
		margin-right: 8px;
	}

	.msg-time {
		color: #999;
		font-size: 12px;
		white-space: nowrap;
	}

	.msg-desc {
		color: #666;
		font-size: 13px;
	}

	.text-ellipsis {
		overflow: hidden;
		white-space: nowrap;
		text-overflow: ellipsis;
	}

	.msg-content-detail {
		white-space: pre-wrap;
		line-height: 1.6;
		max-height: 400px;
		overflow-y: auto;
	}

	.receive-info-title {
		font-size: 15px;
		font-weight: 600;
		margin: 20px 0 10px;
		padding-left: 10px;
		border-left: 3px solid #1890ff;
		line-height: 1;
	}

	:deep(.ant-badge-count) {
		padding: 0px;
		min-width: 15px;
		height: 15px;
		line-height: 15px;
	}
</style>
