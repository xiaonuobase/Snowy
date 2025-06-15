<template>
	<div>
		<s-table
			ref="tableRef"
			:columns="columns"
			:data="loadData"
			:alert="options.alert.show"
			bordered
			:row-key="(record) => record.id"
			:tool-config="toolConfig"
			:row-selection="options.rowSelection"
		>
			<template #operator class="table-operator">
				<a-space>
					<a-button type="primary" size="small" @click="formRef.onOpen()">
						<template #icon><plus-outlined /></template>
						新增
					</a-button>
					<xn-batch-button
						buttonName="批量删除"
						size="small"
						icon="DeleteOutlined"
						buttonDanger
						:selectedRowKeys="selectedRowKeys"
						@batchCallBack="deleteBatchDevWeakPassword"
					/>
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a @click="formRef.onOpen(record)">编辑</a>
						<a-divider type="vertical" />
						<a-popconfirm title="确定要删除吗？" @confirm="deleteDevWeakPassword(record)">
							<a-button type="link" danger size="small">删除</a-button>
						</a-popconfirm>
					</a-space>
				</template>
			</template>
		</s-table>
		<Form ref="formRef" @successful="tableRef.refresh(true)" />
	</div>
</template>

<script setup name="weakpassword">
	import { cloneDeep } from 'lodash-es'
	import { ref } from 'vue'
	import Form from './form.vue'
	import weakPasswordApi from '@/api/dev/weakPasswordApi'
	const tableRef = ref()
	const formRef = ref()
	const toolConfig = { refresh: true, height: true, columnSetting: true, striped: false }
	const columns = [
		{
			title: '弱密码',
			dataIndex: 'password'
		},
		{
			title: '创建时间',
			dataIndex: 'createTime'
		},
		{
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: '150px'
		}
	]
	const selectedRowKeys = ref([])
	// 列表选择配置
	const options = {
		// columns数字类型字段加入 needTotal: true 可以勾选自动算账
		alert: {
			show: false,
			clear: () => {
				selectedRowKeys.value = ref([])
			}
		},
		rowSelection: {
			onChange: (selectedRowKey, selectedRows) => {
				selectedRowKeys.value = selectedRowKey
			}
		}
	}
	const loadData = (parameter) => {
		return weakPasswordApi.weakPasswordPage(parameter).then((data) => {
			return data
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
	// 删除
	const deleteDevWeakPassword = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		weakPasswordApi.weakPasswordDelete(params).then(() => {
			tableRef.value.refresh(true)
		})
	}
	// 批量删除
	const deleteBatchDevWeakPassword = (params) => {
		weakPasswordApi.weakPasswordDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
</script>
