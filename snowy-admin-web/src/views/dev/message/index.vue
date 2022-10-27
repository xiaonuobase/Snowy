<template>
	<a-card :bordered="false" style="margin-bottom: 10px">
		<a-form ref="searchFormRef" name="advanced_search" class="ant-advanced-search-form" :model="searchFormState">
			<a-row :gutter="24">
				<a-col :span="8">
					<a-form-item name="engine" label="主题关键词">
						<a-input v-model:value="searchFormState.searchKey" placeholder="请输入站内信主题关键词"></a-input>
					</a-form-item>
				</a-col>
				<a-col :span="8">
					<a-button type="primary" @click="table.refresh(true)">
						<template #icon><SearchOutlined /></template>
						查询
					</a-button>
					<a-button class="snowy-buttom-left" @click="() => searchFormRef.resetFields()">
						<template #icon><redo-outlined /></template>
						重置
					</a-button>
				</a-col>
			</a-row>
		</a-form>
	</a-card>
	<a-card :bordered="false">
		<s-table
			ref="table"
			:columns="columns"
			:data="loadData"
			:expand-row-by-click="true"
			:alert="options.alert.show"
			bordered
			:row-key="(record) => record.id"
			:row-selection="options.rowSelection"
		>
			<template #operator class="table-operator">
				<a-space>
					<a-button type="primary" @click="form.onOpen()"> 发送站内信 </a-button>
					<a-button danger @click="deleteBatchEmail()">删除</a-button>
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'action'">
					<a @click="detailRef.onOpen(record)">详情</a>
					<a-divider type="vertical" />
					<a-popconfirm title="删除此站内信？" @confirm="deleteMessage(record)">
						<a-button type="link" danger size="small">删除</a-button>
					</a-popconfirm>
				</template>
			</template>
		</s-table>
	</a-card>
	<Form ref="form" @successful="table.refresh(true)" />
	<detail ref="detailRef" />
</template>

<script setup name="devMessage">
	import { message } from 'ant-design-vue'
	import messageApi from '@/api/dev/messageApi'
	import Form from './form.vue'
	import detail from './detail.vue'

	const columns = [
		{
			title: '主题',
			dataIndex: 'subject',
			ellipsis: true
		},
		{
			title: '正文',
			dataIndex: 'content',
			ellipsis: true
		},
		{
			title: '发送时间',
			dataIndex: 'createTime',
			ellipsis: true,
			sorter: true,
			width: '150px'
		},
		{
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: '150px'
		}
	]
	let selectedRowKeys = ref([])
	// 列表选择配置
	const options = {
		alert: {
			show: false,
			clear: () => {
				selectedRowKeys = ref([])
			}
		},
		rowSelection: {
			onChange: (selectedRowKey, selectedRows) => {
				selectedRowKeys.value = selectedRowKey
			}
		}
	}
	// 定义tableDOM
	const table = ref(null)
	const form = ref()
	const searchFormRef = ref()
	let searchFormState = reactive({})
	const detailRef = ref()
	// 表格查询 返回 Promise 对象
	const loadData = (parameter) => {
		return messageApi.messagePage(Object.assign(parameter, searchFormState)).then((data) => {
			return data
		})
	}
	// 删除
	const deleteMessage = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		messageApi.messageDelete(params).then(() => {
			table.refresh(true)
		})
	}
	// 批量删除
	const deleteBatchEmail = () => {
		if (selectedRowKeys.value.length < 1) {
			message.warning('请选择一条或多条数据')
			return false
		}
		const params = selectedRowKeys.value.map((m) => {
			return {
				id: m
			}
		})
		messageApi.messageDelete(params).then(() => {
			table.value.clearRefreshSelected()
		})
	}
</script>

<style scoped>
	.ant-form-item {
		margin-bottom: 0 !important;
	}
	.snowy-buttom-left {
		margin-left: 8px;
	}
</style>
