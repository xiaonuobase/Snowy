<template>
	<a-drawer title="字段权限" :width="650" :open="visible" :destroy-on-close="true" @close="onClose">
		<s-table
			ref="tableRef"
			:columns="columns"
			:data="loadData"
			:alert="false"
			:row-key="(record) => record.id"
			:tool-config="toolConfig"
		>
			<template #operator class="table-operator">
				<a-button type="primary" @click="fieldForm.onOpen(recordData)">
					<template #icon>
						<plus-outlined />
					</template>
					<span>新增字段</span>
				</a-button>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.key === 'action'">
					<a @click="fieldForm.onOpen(recordData, record)">编辑</a>
					<a-divider type="vertical" />
					<a-popconfirm title="确定要删除此字段吗？" @confirm="removeField(record)">
						<a-button type="link" danger size="small">删除</a-button>
					</a-popconfirm>
				</template>
			</template>
		</s-table>
	</a-drawer>
	<Form ref="fieldForm" @successful="tableRef.refresh(true)" />
</template>

<script setup name="sysResourceField">
	import fieldApi from '@/api/sys/resource/fieldApi'
	import Form from './form.vue'
	const columns = [
		{
			title: '名称',
			dataIndex: 'title'
		},
		{
			title: '字段名',
			dataIndex: 'code',
			sorter: true
		},
		{
			title: '排序',
			dataIndex: 'sortCode',
			sorter: true
		},
		{
			title: '操作',
			key: 'action',
			align: 'center',
			scopedSlots: { customRender: 'action' }
		}
	]
	const toolConfig = { refresh: true, height: false, columnSetting: false, striped: false }
	// 默认是关闭状态
	const visible = ref(false)
	const searchFormState = ref()
	const fieldForm = ref()
	const recordData = ref()
	const tableRef = ref()
	// 打开抽屉
	const onOpen = (record) => {
		recordData.value = record
		searchFormState.value = {
			parentId: record.id,
			category: 'FIELD'
		}
		visible.value = true
	}
	// 关闭抽屉
	const onClose = () => {
		visible.value = false
	}
	// 加载字段数据
	const loadData = (parameter) => {
		return fieldApi.fieldPage(Object.assign(parameter, searchFormState.value)).then((res) => {
			return res
		})
	}
	// 删除
	const removeField = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		fieldApi.fieldDelete(params).then(() => {
			tableRef.value.refresh(true)
		})
	}
	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>
