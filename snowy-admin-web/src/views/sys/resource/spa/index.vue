<template>
	<a-alert style="margin-bottom: 10px" message="温馨提示：排序第一条为首页页面！" show-icon type="warning" closable />
	<a-card :bordered="false" :body-style="{ 'padding-bottom': '0px' }" class="mb-2">
		<a-form ref="formRef" name="advanced_search" :model="searchFormState" class="ant-advanced-search-form">
			<a-row :gutter="24">
				<a-col :span="8">
					<a-form-item label="名称关键词" name="searchKey">
						<a-input v-model:value="searchFormState.searchKey" placeholder="请输入单页名称关键词" allow-clear></a-input>
					</a-form-item>
				</a-col>
				<a-col :span="8">
					<a-form-item label="类型" name="menuType">
						<a-select
							v-model:value="searchFormState.menuType"
							:options="categoryOptions"
							placeholder="请选择类型"
							allow-clear
						></a-select>
					</a-form-item>
				</a-col>
				<a-col :span="8">
					<a-button type="primary" html-type="submit" @click="table.refresh()">查询</a-button>
					<a-button style="margin: 0 8px" @click="() => formRef.resetFields()">重置</a-button>
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
						新增单页
					</a-button>
					<a-button danger @click="deleteBatchSpa()">删除</a-button>
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'icon'">
					<component :is="record.icon" />
				</template>
				<template v-if="column.dataIndex === 'menuType'">
					<a-tag v-if="record.menuType === 'MENU'" color="blue">
						{{ $TOOL.dictTypeData('MENU_TYPE', record.menuType) }}页
					</a-tag>
					<a-tag v-if="record.menuType === 'IFRAME'" color="purple">
						{{ $TOOL.dictTypeData('MENU_TYPE', record.menuType) }}页
					</a-tag>
					<a-tag v-if="record.menuType === 'LINK'" color="orange">
						{{ $TOOL.dictTypeData('MENU_TYPE', record.menuType) }}页
					</a-tag>
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a @click="form.onOpen(record)">编辑</a>
						<a-divider type="vertical" />
						<a-popconfirm title="确定要删除此单页吗？" @confirm="removeSpa(record)">
							<a-button type="link" danger size="small">删除</a-button>
						</a-popconfirm>
					</a-space>
				</template>
			</template>
		</s-table>
	</a-card>
	<Form ref="form" @successful="table.refresh(true)" />
</template>

<script setup name="sysSpa">
	import { message } from 'ant-design-vue'
	import spaApi from '@/api/sys/resource/spaApi'
	import tool from '@/utils/tool'
	import Form from './form.vue'
	let searchFormState = reactive({})
	const formRef = ref()
	const table = ref(null)
	let form = ref()
	const toolConfig = { refresh: true, height: true, columnSetting: false, striped: false }
	const columns = [
		{
			title: '单页名称',
			dataIndex: 'title',
			width: 260
		},
		{
			title: '图标',
			dataIndex: 'icon'
		},
		{
			title: '类型',
			dataIndex: 'menuType'
		},
		{
			title: '路由地址',
			dataIndex: 'path',
			ellipsis: true,
			width: 150
		},
		{
			title: '组件',
			dataIndex: 'component',
			ellipsis: true,
			width: 150
		},
		{
			title: '排序',
			dataIndex: 'sortCode',
			sorter: true
		},
		{
			title: '创建时间',
			dataIndex: 'createTime',
			ellipsis: true,
			sorter: true
		},
		{
			title: '操作',
			dataIndex: 'action',
			width: '180px',
			align: 'center',
			scopedSlots: { customRender: 'action' }
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
	let categoryOptions = tool
		.dictTypeList('MENU_TYPE')
		.filter((item) => {
			// 排除
			if (item.dictValue !== 'CATALOG') {
				return item
			}
		})
		.map((item) => {
			return {
				value: item['dictValue'],
				label: item['name'] + '页'
			}
		})
	// 列表数据
	const loadData = (parameter) => {
		return spaApi.spaPage(Object.assign(parameter, searchFormState)).then((res) => {
			return res
		})
	}
	// 删除
	const removeSpa = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		spaApi.spaDelete(params).then(() => {
			table.value.refresh(true)
		})
	}
	// 批量删除
	const deleteBatchSpa = () => {
		if (selectedRowKeys.value.length < 1) {
			message.warning('请选择一条或多条数据')
			return false
		}
		const params = selectedRowKeys.value.map((m) => {
			return {
				id: m
			}
		})
		spaApi.spaDelete(params).then(() => {
			table.value.clearRefreshSelected()
		})
	}
</script>

<style scoped></style>
