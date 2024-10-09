<template>
	<a-card :bordered="false">
		<a-form ref="searchFormRef" name="advanced_search" :model="searchFormState" class="ant-advanced-search-form">
			<a-row :gutter="24">
				<a-col :span="6">
					<a-form-item label="标题" name="title">
						<a-input v-model:value="searchFormState.title" placeholder="请输入标题" />
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-form-item label="类型" name="type">
						<a-select v-model:value="searchFormState.type" placeholder="请选择类型" :options="typeOptions" />
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-form-item label="发布位置" name="place">
						<a-select v-model:value="searchFormState.place" placeholder="请选择发布位置" :options="placeOptions" />
					</a-form-item>
				</a-col>
				<a-col :span="6" v-show="advanced">
					<a-form-item label="状态" name="status">
						<a-select v-model:value="searchFormState.status" placeholder="请选择状态" :options="statusOptions" />
					</a-form-item>
				</a-col>
				<a-col :span="6" v-show="advanced">
					<a-form-item label="创建时间" name="createTime">
						<a-range-picker v-model:value="searchFormState.createTime" show-time />
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-button type="primary" @click="tableRef.refresh(true)">查询</a-button>
					<a-button style="margin: 0 8px" @click="reset">重置</a-button>
					<a @click="toggleAdvanced" style="margin-left: 8px">
						{{ advanced ? '收起' : '展开' }}
						<component :is="advanced ? 'up-outlined' : 'down-outlined'" />
					</a>
				</a-col>
			</a-row>
		</a-form>
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
					<a-button type="primary" @click="formRef.onOpen()" v-if="hasPerm('bizNoticeAdd')">
						<template #icon><plus-outlined /></template>
						新增
					</a-button>
					<xn-batch-button
						v-if="hasPerm('bizNoticeBatchDelete')"
						buttonName="批量删除"
						icon="DeleteOutlined"
						buttonDanger
						:selectedRowKeys="selectedRowKeys"
						@batchCallBack="deleteBatchBizNotice"
					/>
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'image'">
					<div v-if="record.image">
						<a-image :src="record.image" style="width: 30px; height: 30px" />
					</div>
					<span v-else>未上传</span>
				</template>
				<template v-if="column.dataIndex === 'type'">
					<a-tag :bordered="false" color="success" v-if="record.type === 'NOTICE'">
						{{ $TOOL.dictTypeData('BIZ_NOTICE_TYPE', record.type) }}
					</a-tag>
					<a-tag :bordered="false" color="processing" v-else-if="record.type === 'ANNOUNCEMENT'">
						{{ $TOOL.dictTypeData('BIZ_NOTICE_TYPE', record.type) }}
					</a-tag>
					<a-tag :bordered="false" color="warning" v-else-if="record.type === 'WARNING'">
						{{ $TOOL.dictTypeData('BIZ_NOTICE_TYPE', record.type) }}
					</a-tag>
					<span v-else>无</span>
				</template>
				<template v-if="column.dataIndex === 'place'">
					<a-tag v-for="textValue in JSON.parse(record.place)" :key="textValue" color="processing">
						{{ $TOOL.dictTypeData('BIZ_NOTICE_PLACE', textValue) }}
					</a-tag>
				</template>
				<template v-if="column.dataIndex === 'status'">
					<a-switch
						:loading="loading"
						:checked="record.status === 'ENABLE'"
						@change="editStatus(record)"
						v-if="hasPerm('bizNoticerUpdateStatus')"
					/>
					<span v-else>{{ $TOOL.dictTypeData('BIZ_NOTICE_STATUS', record.status) }}</span>
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a @click="detailRef.onOpen(record.id)" v-if="hasPerm('bizNoticeDetail')">详情</a>
						<a-divider type="vertical" v-if="hasPerm(['bizNoticeEdit', 'bizNoticeDetail'], 'and')" />
						<a @click="formRef.onOpen(record)" v-if="hasPerm('bizNoticeEdit')">编辑</a>
						<a-divider type="vertical" v-if="hasPerm(['bizNoticeEdit', 'bizNoticeDelete'], 'and')" />
						<a-popconfirm title="确定要删除吗？" @confirm="deleteBizNotice(record)">
							<a-button type="link" danger size="small" v-if="hasPerm('bizNoticeDelete')">删除</a-button>
						</a-popconfirm>
					</a-space>
				</template>
			</template>
		</s-table>
	</a-card>
	<Form ref="formRef" @successful="tableRef.refresh()" />
	<detail ref="detailRef" />
</template>

<script setup name="notice">
	import tool from '@/utils/tool'
	import { cloneDeep } from 'lodash-es'
	import Form from './form.vue'
	import Detail from './detail.vue'
	import bizNoticeApi from '@/api/biz/bizNoticeApi'
	const searchFormState = ref({})
	const searchFormRef = ref()
	const tableRef = ref()
	const formRef = ref()
	const detailRef = ref()
	const toolConfig = { refresh: true, height: true, columnSetting: true, striped: false }
	const loading = ref(false)
	// 查询区域显示更多控制
	const advanced = ref(false)
	const toggleAdvanced = () => {
		advanced.value = !advanced.value
	}
	const columns = [
		{
			title: '标题',
			dataIndex: 'title'
		},
		{
			title: '封面图',
			dataIndex: 'image',
			width: '100px'
		},
		{
			title: '类型',
			dataIndex: 'type'
		},
		{
			title: '发布位置',
			dataIndex: 'place'
		},
		{
			title: '排序',
			dataIndex: 'sortCode',
			sorter: true
		},
		{
			title: '状态',
			dataIndex: 'status'
		},
		{
			title: '创建时间',
			dataIndex: 'createTime',
			width: '150px'
		}
	]
	// 操作栏通过权限判断是否显示
	if (hasPerm(['bizNoticeEdit', 'bizNoticeDelete'])) {
		columns.push({
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: '200px'
		})
	}
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
		const searchFormParam = cloneDeep(searchFormState.value)
		// createTime范围查询条件重载
		if (searchFormParam.createTime) {
			searchFormParam.startCreateTime = searchFormParam.createTime[0]
			searchFormParam.endCreateTime = searchFormParam.createTime[1]
			delete searchFormParam.createTime
		}
		return bizNoticeApi.bizNoticePage(Object.assign(parameter, searchFormParam)).then((data) => {
			return data
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
	// 删除
	const deleteBizNotice = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		bizNoticeApi.bizNoticeDelete(params).then(() => {
			tableRef.value.refresh(true)
		})
	}
	// 批量删除
	const deleteBatchBizNotice = (params) => {
		bizNoticeApi.bizNoticeDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
	// 修改状态
	const editStatus = (record) => {
		loading.value = true
		if (record.status === 'ENABLE') {
			bizNoticeApi
				.bizNoticeDisableStatus(record)
				.then(() => {
					tableRef.value.refresh()
				})
				.finally(() => {
					loading.value = false
				})
		} else {
			bizNoticeApi
				.bizNoticeEnableStatus(record)
				.then(() => {
					tableRef.value.refresh()
				})
				.finally(() => {
					loading.value = false
				})
		}
	}
	const typeOptions = tool.dictList('BIZ_NOTICE_TYPE')
	const placeOptions = tool.dictList('BIZ_NOTICE_PLACE')
	const statusOptions = tool.dictList('BIZ_NOTICE_STATUS')
</script>
