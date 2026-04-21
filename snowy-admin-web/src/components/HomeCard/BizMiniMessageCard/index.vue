<template>
	<a-card :title="title" :bordered="false" :loading="miniMessageLoading" class="mini-message-card">
		<template #extra>
			<a-button type="link" size="small" @click="leaveFor('/usercenter')">更多</a-button>
		</template>
		<div class="index-message-list">
			<a-list :data-source="messageList" size="small" :loading="miniMessageLoading">
				<template #renderItem="{ item }">
					<a-list-item class="message-item">
						<a-list-item-meta>
							<template #title>
								<div class="message-header">
									<a class="message-subject" @click="messageDetail(item)">{{ item.subject }}</a>
									<span class="message-time">{{ item.createTime }}</span>
								</div>
							</template>
						</a-list-item-meta>
					</a-list-item>
				</template>
			</a-list>
		</div>
		<xn-form-container title="详情" :width="700" :visible="visible" :destroy-on-close="true" @close="onClose">
			<a-form ref="formRef" :model="formData" layout="vertical">
				<a-form-item label="主题：" name="subject">
					<span class="detail-text">{{ formData.subject }}</span>
				</a-form-item>
				<a-form-item label="发送时间：" name="createTime">
					<span class="detail-text">{{ formData.createTime }}</span>
				</a-form-item>
				<a-form-item label="内容：" name="content">
					<span class="detail-text">{{ formData.content }}</span>
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
								<a-tag :color="record.read ? 'default' : 'error'">
									{{ record.read ? '已读' : '未读' }}
								</a-tag>
							</template>
						</template>
					</s-table>
				</a-form-item>
			</a-form>
		</xn-form-container>
	</a-card>
</template>

<script setup name="miniMessage">
	import indexApi from '@/api/sys/indexApi'
	import { onMounted, ref } from 'vue'
	import router from '@/router'
	const miniMessageLoading = ref(false)
	const messageList = ref([])
	const title = ref('站内信')
	onMounted(() => {
		// 进来后执行查询
		getMessageList()
	})
	// 获取站内信列表
	const getMessageList = () => {
		miniMessageLoading.value = true
		indexApi
			.indexMessageList()
			.then((data) => {
				messageList.value = data
			})
			.catch(() => {})
			.finally(() => {
				miniMessageLoading.value = false
			})
	}
	// 跳转，用于点击更多按钮
	const leaveFor = (url = '/') => {
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
	}

	const loadData = () => {
		return new Promise((resolve) => {
			resolve(receiveInfoList.value)
		})
	}
	// 以下部分是抽屉的
	const visible = ref(false)
	const formRef = ref()
	const formData = ref({})
	const receiveInfoList = ref([])
	const tableRef = ref()
	const columns = [
		{
			title: '姓名',
			dataIndex: 'receiveUserName'
		},
		{
			title: '是否查收',
			dataIndex: 'read'
		}
	]
	const onClose = () => {
		visible.value = false
	}
</script>

<style scoped lang="less">
	:deep(.ant-card-body) {
		padding-top: 0 !important;
	}
	.index-message-list {
		height: 300px;
		overflow-y: auto;
		padding: 0 5px;
		/* 自定义滚动条 */
		&::-webkit-scrollbar {
			width: 4px;
		}
		&::-webkit-scrollbar-thumb {
			background: #e8e8e8;
			border-radius: 2px;
		}
	}
	.message-item {
		padding: 12px 0 !important;
		border-bottom: 1px dashed #f0f0f0 !important;
		&:last-child {
			border-bottom: none !important;
		}
	}
	.message-header {
		display: flex;
		justify-content: space-between;
		align-items: flex-start;
	}
	.message-subject {
		font-size: 14px;
		color: #262626;
		font-weight: 500;
		flex: 1;
		margin-right: 12px;
		&:hover {
			color: #1890ff;
		}
	}
	:deep(.snowy-theme-dark) & {
		.message-subject {
			color: rgba(255, 255, 255, 0.85);
		}
		.message-item {
			border-bottom-color: #303030 !important;
		}
		.index-message-list {
			&::-webkit-scrollbar-thumb {
				background: #333;
			}
		}
	}
	.message-time {
		font-size: 12px;
		color: #8c8c8c;
		white-space: nowrap;
	}
	.detail-text {
		color: #595959;
		line-height: 1.6;
	}
</style>
