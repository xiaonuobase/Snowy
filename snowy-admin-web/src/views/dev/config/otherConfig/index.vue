<template>
	<s-table
		ref="tableRef"
		:columns="columns"
		:data="loadData"
		:alert="false"
		bordered
		:row-key="(record) => record.id"
		:tool-config="toolConfig"
	>
		<template #operator class="table-operator">
			<a-space>
				<a-button type="primary" @click="formRef.onOpen()">
					<template #icon>
						<plus-outlined />
					</template>
					<span>新增配置</span>
				</a-button>
				<a-input-search
					v-model:value="searchFormState.searchKey"
					placeholder="请输入关键词"
					enter-button
					allowClear
					@search="tableRef.refresh(true)"
				/>
			</a-space>
		</template>
		<template #bodyCell="{ column, record }">
			<template v-if="column.key === 'action'">
				<a-space>
					<a @click="formRef.onOpen(record)">编辑</a>
					<a-divider type="vertical" />
					<a-popconfirm title="确定要删除此配置吗？" @confirm="deleteConfig(record)">
						<a-button type="link" danger size="small">删除</a-button>
					</a-popconfirm>
				</a-space>
			</template>
		</template>
	</s-table>
	<Form ref="formRef" @successful="tableRef.refresh(true)" />
</template>

<script setup name="sysModule">
	import Form from './form.vue'
	import configApi from '@/api/dev/configApi'
	const searchFormState = ref({})
	const formRef = ref()
	const tableRef = ref()
	const toolConfig = { refresh: true, height: true, columnSetting: false, striped: false }
	const columns = [
		{
			title: '配置键',
			dataIndex: 'configKey',
			ellipsis: true
		},
		{
			title: '配置值',
			dataIndex: 'configValue',
			ellipsis: true
		},
		{
			title: '备注',
			dataIndex: 'remark'
		},
		{
			title: '排序',
			dataIndex: 'sortCode',
			width: 100,
			sorter: true
		},
		{
			title: '操作',
			key: 'action',
			width: '200px',
			scopedSlots: { customRender: 'action' }
		}
	]
	const loadData = (parameter) => {
		return configApi.configPage(Object.assign(parameter, searchFormState.value)).then((res) => {
			return res
		})
	}
	// 删除
	const deleteConfig = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		configApi.configDelete(params).then(() => {
			tableRef.value.refresh(true)
		})
	}
</script>
