<template>
	<a-popconfirm
		title="删除此信息？"
		:open="deleteVisible"
		@openChange="deleteVisibleChange"
		@confirm="deleteBatch"
	>
		<a-button danger>
			<template #icon><delete-outlined /></template>
			{{ props.buttonName }}
		</a-button>
	</a-popconfirm>
</template>

<script setup name="commonBatchDelete">
	import { message } from 'ant-design-vue'
	const deleteVisible = ref(false)
	const emit = defineEmits({ batchDelete: null })
	const props = defineProps({
		buttonName: {
			type: String,
			default: () => '批量删除'
		},
		selectedRowKeys: {
			type: Array,
			default: () => []
		}
	})
	// 参数校验
	const deleteVisibleChange = () => {
		if (deleteVisible.value) {
			deleteVisible.value = false
			return false
		}
		if (props.selectedRowKeys.length < 1) {
			message.warning('请选择一条或多条数据')
			deleteVisible.value = false
			return false
		} else {
			deleteVisible.value = true
		}
	}
	// 批量删除
	const deleteBatch = () => {
		const params = props.selectedRowKeys.map((m) => {
			return {
				id: m
			}
		})
		// 发起方法调用，谁的谁来实现
		emit('batchDelete', params)
	}
</script>
