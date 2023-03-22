<template>
	<a-card :bordered="false" style="margin-bottom: 10px">
		<a-form ref="searchFormRef" name="advanced_search" class="ant-advanced-search-form" :model="searchFormState">
			<a-row :gutter="24">
				<a-col :span="8">
					<a-form-item name="engine" label="邮件引擎">
						<a-select
							v-model:value="searchFormState.engine"
							:options="engineOptions"
							placeholder="请选择邮件引擎"
							allow-clear
						></a-select>
					</a-form-item>
				</a-col>
				<a-col :span="8">
					<a-form-item name="searchKey" label="主题关键词">
						<a-input v-model:value="searchFormState.searchKey" placeholder="请输入邮件主题关键词"></a-input>
					</a-form-item>
				</a-col>
				<a-col :span="8">
					<a-button type="primary" @click="table.refresh(true)">
						<template #icon><SearchOutlined /></template>
						查询
					</a-button>
					<a-button class="snowy-buttom-left" @click="reset">
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
					<a-button type="primary" @click="form.onOpen()">
						<template #icon><plus-outlined /></template>
						发送邮件
					</a-button>
					<a-button danger @click="deleteBatchEmail()">删除</a-button>
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'engine'">
					{{ $TOOL.dictTypeData('EMAIL_ENGINE', record.engine) }}
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a @click="detailRef.onOpen(record)">详情</a>
					<a-divider type="vertical" />
					<a-popconfirm title="删除此邮件？" @confirm="deleteEmail(record)">
						<a-button type="link" danger size="small">删除</a-button>
					</a-popconfirm>
				</template>
			</template>
		</s-table>
	</a-card>
	<Form ref="form" @successful="table.refresh(true)" />
	<detail ref="detailRef" />
</template>

<script setup name="devEmail">
	import { message } from 'ant-design-vue'
	import tool from '@/utils/tool'
	import emailApi from '@/api/dev/emailApi'
	import Form from './form.vue'
	import detail from './detail.vue'
	const table = ref(null)
	const form = ref()
	const searchFormRef = ref()
	let searchFormState = reactive({})
	const detailRef = ref()

	const columns = [
		{
			title: '邮件引擎',
			dataIndex: 'engine',
			ellipsis: true
		},
		{
			title: '发件人邮箱',
			dataIndex: 'sendAccount'
		},
		{
			title: '发件人昵称',
			dataIndex: 'sendUser'
		},
		{
			title: '接收人',
			dataIndex: 'receiveAccounts',
			ellipsis: true
		},
		{
			title: '邮件主题',
			dataIndex: 'subject',
			ellipsis: true
		},
		{
			title: '标签名',
			dataIndex: 'tagName',
			ellipsis: true
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
	// 表格查询 返回 Promise 对象
	const loadData = (parameter) => {
		return emailApi.emailPage(Object.assign(parameter, searchFormState)).then((data) => {
			return data
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		table.value.refresh(true)
	}
	const engineOptions = tool.dictList('EMAIL_ENGINE')
	// 删除
	const deleteEmail = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		emailApi.emailDelete(params).then(() => {
			table.value.refresh(true)
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
		emailApi.emailDelete(params).then(() => {
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
