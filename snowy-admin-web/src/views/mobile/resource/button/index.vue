<template>
	<xn-form-container
		title="按钮权限"
		:width="700"
		:visible="visible"
		:destroy-on-close="true"
		:body-style="{ paddingBottom: '80px' }"
		@close="onClose"
	>
		<s-table
			ref="tableRef"
			:columns="columns"
			:data="loadData"
			:alert="false"
			:row-key="(record) => record.id"
			:tool-config="toolConfig"
		>
			<template #operator class="table-operator">
				<a-button type="primary" @click="buttonForm.onOpen(recordData)">
					<template #icon>
						<plus-outlined />
					</template>
					<span>新增按钮</span>
				</a-button>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.key === 'action'">
					<a @click="buttonForm.onOpen(recordData, record)">编辑</a>
					<a-divider type="vertical" />
					<a-popconfirm title="确定要删除此按钮吗？" @confirm="removeButton(record)">
						<a-button type="link" danger size="small">删除</a-button>
					</a-popconfirm>
				</template>
			</template>
		</s-table>
	</xn-form-container>
	<Form ref="buttonForm" @successful="tableRef.refresh(true)" />
</template>

<script setup>
	import buttonApi from '@/api/mobile/resource/buttonApi'
	import Form from './form.vue'
	const columns = [
		{
			title: '名称',
			dataIndex: 'title'
		},
		{
			title: '编码',
			dataIndex: 'code'
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
	const buttonForm = ref()
	const recordData = ref()
	const tableRef = ref()

	// 打开抽屉
	const onOpen = (record) => {
		recordData.value = record
		searchFormState.value = {
			parentId: record.id,
			category: 'BUTTON'
		}
		visible.value = true
	}
	// 关闭抽屉
	const onClose = () => {
		visible.value = false
	}
	// 加载按钮数据
	const loadData = (parameter) => {
		return buttonApi.mobileButtonPage(Object.assign(parameter, searchFormState.value)).then((res) => {
			return res
		})
	}
	// 删除
	const removeButton = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		buttonApi.mobileButtonDelete(params).then((res) => {
			tableRef.value.refresh(true)
		})
	}
	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>
