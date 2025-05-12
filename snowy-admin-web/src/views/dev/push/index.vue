<template>
	<a-card :bordered="false" class="xn-mb10">
		<a-form ref="searchFormRef" name="advanced_search" class="ant-advanced-search-form" :model="searchFormState">
			<a-row :gutter="24">
				<a-col :span="8">
					<a-form-item name="engine" label="消息引擎">
						<a-select
							v-model:value="searchFormState.engine"
							:options="engineOptions"
							placeholder="请选择消息引擎"
							:getPopupContainer="(trigger) => trigger.parentNode"
							allow-clear
						></a-select>
					</a-form-item>
				</a-col>
				<a-col :span="8">
					<a-form-item name="searchKey" label="消息标题关键词">
						<a-input v-model:value="searchFormState.searchKey" placeholder="请输入消息标题关键词"></a-input>
					</a-form-item>
				</a-col>
				<a-col :span="8">
					<a-button type="primary" @click="tableRef.refresh(true)">
						<template #icon><SearchOutlined /></template>
						查询
					</a-button>
					<a-button class="snowy-button-left" @click="reset">
						<template #icon><redo-outlined /></template>
						重置
					</a-button>
				</a-col>
			</a-row>
		</a-form>
	</a-card>
	<a-card :bordered="false">
		<s-table
			ref="tableRef"
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
					<a-button type="primary" @click="formRef.onOpen()">
						<template #icon><plus-outlined /></template>
						推送消息
					</a-button>
					<xn-batch-button
						buttonName="批量删除"
						icon="DeleteOutlined"
						buttonDanger
						:selectedRowKeys="selectedRowKeys"
						@batchCallBack="deleteBatchPush"
					/>
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'engine'">
					{{ $TOOL.dictTypeData('PUSH_ENGINE', record.engine) }}
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a @click="detailRef.onOpen(record)">详情</a>
					<a-divider type="vertical" />
					<a-popconfirm title="删除此消息？" @confirm="deletePush(record)">
						<a-button type="link" danger size="small">删除</a-button>
					</a-popconfirm>
				</template>
			</template>
		</s-table>
	</a-card>
	<Form ref="formRef" @successful="tableRef.refresh(true)" />
	<detail ref="detailRef" />
</template>

<script setup name="devPushIndex">
	import pushApi from '@/api/dev/pushApi'
	import Form from './form.vue'
	import Detail from './detail.vue'
	import tool from '@/utils/tool'

	const tableRef = ref(null)
	const formRef = ref()
	const searchFormRef = ref()
	const searchFormState = ref({})
	const detailRef = ref()

	const columns = [
		{
			title: '消息标题',
			dataIndex: 'title'
		},
		{
			title: '消息引擎',
			dataIndex: 'engine',
			ellipsis: true
		},
		{
			title: '消息类别',
			dataIndex: 'type',
			ellipsis: true
		},
		{
			title: '消息内容',
			dataIndex: 'content',
			ellipsis: true
		},
		{
			title: '回执信息',
			dataIndex: 'receiptInfo',
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
		return pushApi.pushPage(Object.assign(parameter, searchFormState.value)).then((data) => {
			return data
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
	const engineOptions = tool.dictList('PUSH_ENGINE')
	// 删除
	const deletePush = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		pushApi.pushDelete(params).then(() => {
			tableRef.value.refresh(true)
		})
	}
	// 批量删除
	const deleteBatchPush = (params) => {
		pushApi.pushDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
</script>

<style lang="less" scoped>
	.ant-form-item {
		margin-bottom: 0 !important;
	}
	.snowy-button-left {
		margin-left: 8px;
	}
</style>
