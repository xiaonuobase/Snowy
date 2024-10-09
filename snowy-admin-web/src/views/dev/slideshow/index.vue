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
					<a-form-item label="展示位置" name="place">
						<a-select v-model:value="searchFormState.place" placeholder="请选择展示位置" :options="placeOptions" />
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-form-item label="状态" name="status">
						<a-select v-model:value="searchFormState.status" placeholder="请选择状态" :options="statusOptions" />
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-button type="primary" @click="tableRef.refresh(true)">查询</a-button>
					<a-button style="margin: 0 8px" @click="reset">重置</a-button>
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
			<template #expandColumnTitle>
				<span>更多</span>
			</template>
			<template #expandedRowRender="{ record }">
				<a-table
					size="middle"
					:dataSource="JSON.parse(record.pathDetails)"
					:columns="detailsColumns"
					:pagination="false"
					bordered
				>
					<template #bodyCell="{ column, record }">
						<template v-if="column.dataIndex === 'whetherToClick'">
							<a-tag color="blue" v-if="record.whetherToClick === 'ENABLE'">
								{{ $TOOL.dictTypeData('WHETHER_TO_CLICK', record.whetherToClick) }}
							</a-tag>
							<a-tag color="red" v-if="record.whetherToClick === 'DISABLE'">
								{{ $TOOL.dictTypeData('WHETHER_TO_CLICK', record.whetherToClick) }}
							</a-tag>
						</template>
						<template v-if="column.dataIndex === 'skipMode'">
							{{ $TOOL.dictTypeData('SKIP_MODE', record.skipMode) }}
						</template>
					</template>
				</a-table>
			</template>
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
						@batchCallBack="deleteBatchDevSlideshow"
					/>
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'place'">
					<a-tag v-for="textValue in JSON.parse(record.place)" :key="textValue" color="green">{{
						$TOOL.dictTypeData('DEV_SLIDESHOW_PLACE', textValue)
					}}</a-tag>
				</template>
				<template v-if="column.dataIndex === 'image'">
					<a-image :src="record.image" style="width: 50px; height: 30px" />
				</template>
				<template v-if="column.dataIndex === 'status'">
					<a-switch
						:loading="loading"
						:checked="record.status === 'ENABLE'"
						@change="editStatus(record)"
						v-if="hasPerm('bizNoticerUpdateStatus')"
					/>
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a @click="formRef.onOpen(record)">编辑</a>
						<a-divider type="vertical" />
						<a-popconfirm title="确定要删除吗？" @confirm="deleteDevSlideshow(record)">
							<a-button type="link" danger size="small">删除</a-button>
						</a-popconfirm>
					</a-space>
				</template>
			</template>
		</s-table>
	</a-card>
	<Form ref="formRef" @successful="tableRef.refresh()" />
</template>

<script setup name="slideshow">
	import tool from '@/utils/tool'
	import { cloneDeep } from 'lodash-es'
	import Form from './form.vue'
	import slideshowApi from '@/api/dev/slideshowApi'
	const searchFormState = ref({})
	const searchFormRef = ref()
	const tableRef = ref()
	const formRef = ref()
	const loading = ref(false)
	const toolConfig = { refresh: true, height: true, columnSetting: true, striped: false }
	const columns = [
		{
			title: '标题',
			dataIndex: 'title'
		},
		{
			title: '图片',
			dataIndex: 'image'
		},
		{
			title: '展示位置',
			dataIndex: 'place'
		},
		{
			title: '状态',
			dataIndex: 'status'
		},
		{
			title: '排序',
			dataIndex: 'sortCode',
			sorter: true
		},
		{
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: '150px'
		}
	]
	const detailsColumns = [
		{
			title: '位置',
			dataIndex: 'label',
			width: '200px'
		},
		{
			title: '点击事件',
			dataIndex: 'whetherToClick'
		},
		{
			title: '跳转方式',
			dataIndex: 'skipMode'
		},
		{
			title: 'URL',
			dataIndex: 'url'
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
		const searchFormParam = cloneDeep(searchFormState.value)
		return slideshowApi.devSlideshowPage(Object.assign(parameter, searchFormParam)).then((data) => {
			return data
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
	// 删除
	const deleteDevSlideshow = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		slideshowApi.devSlideshowDelete(params).then(() => {
			tableRef.value.refresh()
		})
	}
	// 批量删除
	const deleteBatchDevSlideshow = (params) => {
		slideshowApi.devSlideshowDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
	// 修改状态
	const editStatus = (record) => {
		loading.value = true
		if (record.status === 'ENABLE') {
			slideshowApi
				.devSlideshowDisableStatus(record)
				.then(() => {
					tableRef.value.refresh()
				})
				.finally(() => {
					loading.value = false
				})
		} else {
			slideshowApi
				.devSlideshowEnableStatus(record)
				.then(() => {
					tableRef.value.refresh()
				})
				.finally(() => {
					loading.value = false
				})
		}
	}
	const placeOptions = tool.dictList('DEV_SLIDESHOW_PLACE')
	const statusOptions = tool.dictList('DEV_SLIDESHOW_STATUS')
</script>
