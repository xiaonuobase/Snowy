<template>
	<a-card :bordered="false" :body-style="{ 'padding-bottom': '0px' }" class="mb-2">
		<a-form ref="searchFormRef" name="advanced_search" :model="searchFormState" class="ant-advanced-search-form">
			<a-row :gutter="24">
				<a-col :span="6">
					<a-form-item label="关键词" name="searchKey">
						<a-input v-model:value="searchFormState.searchKey" placeholder="请输入关键词" />
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-form-item label="分类" name="category">
						<a-select
							v-model:value="searchFormState.category"
							placeholder="请选择分类"
							:options="categoryOptions"
							:getPopupContainer="(trigger) => trigger.parentNode"
						/>
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-form-item label="状态" name="jobStatus">
						<a-select
							v-model:value="searchFormState.jobStatus"
							placeholder="请选择状态"
							:options="jobStatusOptions"
							:getPopupContainer="(trigger) => trigger.parentNode"
						/>
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-button type="primary" @click="tableRef.refresh(true)">查询</a-button>
					<a-button class="xn-mg08" @click="reset">重置</a-button>
				</a-col>
			</a-row>
		</a-form>
	</a-card>
	<a-card :bordered="false">
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
					<a-button type="primary" @click="formRef.onOpen()">
						<template #icon><plus-outlined /></template>
						新增
					</a-button>
					<xn-batch-button
						buttonName="批量删除"
						icon="DeleteOutlined"
						buttonDanger
						:selectedRowKeys="selectedRowKeys"
						@batchCallBack="deleteBatchJob"
					/>
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'category'">
					{{ $TOOL.dictTypeData('JOB_CATEGORY', record.category) }}
				</template>
				<template v-if="column.dataIndex === 'jobStatus'">
					<a-badge status="processing" v-if="record.jobStatus === 'RUNNING'" />
					<a-badge status="default" v-else />
					{{ $TOOL.dictTypeData('JOB_STATUS', record.jobStatus) }}
				</template>
				<template v-if="column.dataIndex === 'jobStatusUpdate'">
					<a-switch
						:loading="statusLoading"
						:checked="record.jobStatus === 'RUNNING'"
						@change="editJobStatus(record)"
					/>
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a @click="immediatelyRun(record)">立即运行</a>
						<a-divider type="vertical" />
						<a @click="formRef.onOpen(record)">编辑</a>
						<a-divider type="vertical" />
						<a-popconfirm title="确定要删除此定时任务吗？" @confirm="deleteJob(record)">
							<a-button type="link" danger size="small">删除</a-button>
						</a-popconfirm>
					</a-space>
				</template>
			</template>
		</s-table>
	</a-card>
	<Form ref="formRef" @successful="tableRef.refresh(true)" />
</template>

<script setup name="devJob">
	import tool from '@/utils/tool'
	import Form from './form.vue'
	import jobApi from '@/api/dev/jobApi'
	const searchFormState = ref({})
	const searchFormRef = ref()
	const tableRef = ref()
	const formRef = ref()
	const statusLoading = ref(false)
	const toolConfig = { refresh: true, height: true, columnSetting: false, striped: false }
	const columns = [
		{
			title: '名称',
			dataIndex: 'name'
		},
		{
			title: '任务类名',
			dataIndex: 'actionClass',
			ellipsis: true
		},
		{
			title: '表达式',
			dataIndex: 'cronExpression',
			ellipsis: true
		},
		{
			title: '状态',
			dataIndex: 'jobStatus',
			width: '100px'
		},
		{
			title: '启停',
			dataIndex: 'jobStatusUpdate',
			width: '120px'
		},
		{
			title: '排序',
			dataIndex: 'sortCode',
			sorter: true,
			width: '80px'
		},
		{
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: '220px'
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
	const loadData = (parameter) => {
		return jobApi.jobPage(Object.assign(parameter, searchFormState.value)).then((res) => {
			return res
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
	// 启停
	const editJobStatus = (record) => {
		statusLoading.value = true
		if (record.jobStatus === 'RUNNING') {
			jobApi
				.jobStopJob(record)
				.then(() => {
					tableRef.value.refresh()
				})
				.finally(() => {
					statusLoading.value = false
				})
		} else {
			jobApi
				.jobRunJob(record)
				.then(() => {
					tableRef.value.refresh()
				})
				.finally(() => {
					statusLoading.value = false
				})
		}
	}
	// 立即运行
	const immediatelyRun = (record) => {
		const params = {
			id: record.id
		}
		jobApi.jobRunJobNow(params).then(() => {
			tableRef.value.refresh(true)
		})
	}
	// 删除
	const deleteJob = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		jobApi.jobDelete(params).then(() => {
			tableRef.value.refresh(true)
		})
	}
	// 批量删除
	const deleteBatchJob = (params) => {
		jobApi.jobDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
	// 分类
	const categoryOptions = tool.dictList('JOB_CATEGORY')
	// 状态
	const jobStatusOptions = tool.dictList('JOB_STATUS')
</script>
