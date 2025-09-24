<template>
	<a-card :bordered="false" class="xn-mb10">
		<a-form ref="searchFormRef" :model="searchFormState">
			<a-row :gutter="10">
				<a-col :xs="24" :sm="8" :md="8" :lg="8" :xl="8">
					<a-form-item name="searchKey" label="关键词">
						<a-input v-model:value="searchFormState.searchKey" placeholder="请输入站内信主题关键词" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="16" :md="16" :lg="16" :xl="16">
					<a-form-item>
						<a-space>
							<a-button type="primary" @click="tableRef.refresh(true)">
								<template #icon>
									<SearchOutlined/>
								</template>
								查询
							</a-button>
							<a-button @click="reset">
								<template #icon>
									<redo-outlined/>
								</template>
								重置
							</a-button>
						</a-space>
					</a-form-item>
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
			:scroll="{ x: 'max-content' }"
		>
			<template #operator class="table-operator">
				<a-space>
					<a-button type="primary" @click="formRef.onOpen()"> 发送站内信 </a-button>
					<xn-batch-button
						buttonName="批量删除"
						icon="DeleteOutlined"
						buttonDanger
						:selectedRowKeys="selectedRowKeys"
						@batchCallBack="deleteBatchEmail"
					/>
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
	<Form ref="formRef" @successful="tableRef.refresh()" />
	<detail ref="detailRef" />
</template>

<script setup name="devMessage">
	import messageApi from '@/api/dev/messageApi'
	import Form from './form.vue'
	import Detail from './detail.vue'

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
			sorter: true
		},
		{
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			fixed: 'right'
		}
	]
	const selectedRowKeys = ref([])
	// 列表选择配置
	const options = {
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
	// 定义tableDOM
	const tableRef = ref(null)
	const formRef = ref()
	const searchFormRef = ref()
	const searchFormState = ref({})
	const detailRef = ref()
	// 表格查询 返回 Promise 对象
	const loadData = (parameter) => {
		return messageApi.messagePage(Object.assign(parameter, searchFormState.value)).then((data) => {
			return data
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
	// 删除
	const deleteMessage = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		messageApi.messageDelete(params).then(() => {
			tableRef.value.refresh(true)
		})
	}
	// 批量删除
	const deleteBatchEmail = (params) => {
		messageApi.messageDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
</script>

<style lang="less" scoped>

</style>
