<template>
	<a-card :bordered="false" :body-style="{ 'padding-bottom': '0px' }" class="mb-2">
		<a-form ref="formRef" name="advanced_search" :model="searchFormState" class="ant-advanced-search-form">
			<a-row :gutter="24">
				<a-col :span="8">
					<a-form-item label="名称关键词" name="searchKey">
						<a-input v-model:value="searchFormState.searchKey" placeholder="请输入模块名称关键词"></a-input>
					</a-form-item>
				</a-col>
				<a-col :span="8">
					<a-button type="primary" @click="table.refresh(true)">查询</a-button>
					<a-button style="margin: 0 8px" @click="reset">重置</a-button>
				</a-col>
			</a-row>
		</a-form>
	</a-card>
	<a-card :bordered="false">
		<s-table
			ref="table"
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
					<a-button type="primary" @click="form.onOpen()">
						<template #icon><plus-outlined /></template>
						新增模块
					</a-button>
					<xn-batch-delete :selectedRowKeys="selectedRowKeys" @batchDelete="deleteBatchModule" />
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'icon'">
					<a-tag :color="record.color">
						<component :is="record.icon" />
					</a-tag>
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a @click="form.onOpen(record)">编辑</a>
						<a-divider type="vertical" />
						<a-popconfirm title="确定要删除此模块吗？" @confirm="deleteModule(record)">
							<a-button type="link" danger size="small">删除</a-button>
						</a-popconfirm>
					</a-space>
				</template>
			</template>
		</s-table>
	</a-card>
	<Form ref="form" @successful="table.refresh(true)" />
</template>

<script setup name="mobileModule">
	import Form from './form.vue'
	import moduleApi from '@/api/mobile/resource/moduleApi'
	let searchFormState = reactive({})
	const formRef = ref()
	const table = ref()
	let form = ref()
	const toolConfig = { refresh: true, height: true, columnSetting: false, striped: false }
	const columns = [
		{
			title: '显示名称',
			dataIndex: 'title'
		},
		{
			title: '图标',
			dataIndex: 'icon'
		},
		{
			title: '排序',
			dataIndex: 'sortCode',
			sorter: true
		},
		{
			title: '创建时间',
			dataIndex: 'createTime',
			sorter: true
		},
		{
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: '200px'
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
		return moduleApi.modulePage(Object.assign(parameter, searchFormState)).then((res) => {
			return res
		})
	}
	// 重置
	const reset = () => {
		formRef.value.resetFields()
		table.value.refresh(true)
	}
	// 删除
	const deleteModule = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		moduleApi.moduleDelete(params).then(() => {
			table.value.refresh(true)
		})
	}
	// 批量删除
	const deleteBatchModule = (params) => {
		moduleApi.moduleDelete(params).then(() => {
			table.value.clearRefreshSelected()
		})
	}
</script>
