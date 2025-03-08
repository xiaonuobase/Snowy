<template>
	<div class="ant-dropdown-menu s-tool-column ant-dropdown-content">
		<div class="s-tool-column-header s-tool-column-item">
			<a-checkbox :indeterminate="indeterminate" :checked="checkAll" @change="onCheckAllChange"> 列展示 </a-checkbox>
			<a @click="reset">重置</a>
		</div>
		<a-divider />
		<div class="ant-checkbox-group">
			<div>
				<draggable :list="columnsSetting" item-key="dataIndex" animation="300" @end="emitColumnChange">
					<template #item="{ element }">
						<div class="s-tool-column-item">
							<div class="s-tool-column-handle layout-items-center">
								<more-outlined />
								<more-outlined />
							</div>
							<a-checkbox v-model:checked="element.checked" @change="onChange">{{ element.title }}</a-checkbox>
						</div>
					</template>
				</draggable>
			</div>
		</div>
	</div>
</template>
<script setup>
	import Draggable from 'vuedraggable-es'
	import { ref, watch } from 'vue'

	const emit = defineEmits(['columnChange'])
	const props = defineProps({
		columns: {
			type: Array,
			default: () => []
		}
	})

	const indeterminate = ref(false)
	const checkAll = ref(true)
	const columnsSetting = ref([])
	const originColumns = ref([])

	const emitColumnChange = () => {
		// 确保发送的数据包含所有必要的列信息，并保持响应性
		const updatedColumns = columnsSetting.value.map((col) => ({
			...col,
			hidden: !col.checked,
			checked: col.checked,
			show: col.checked // 添加show属性以确保列的显示状态正确同步
		}))
		// 触发列变化事件，确保父组件能够接收到完整的列信息
		emit('columnChange', updatedColumns)
	}

	// 初始化列设置
	const initializeColumns = (columns) => {
		columnsSetting.value = columns.map((value) => ({
			...value,
			checked: value.checked !== undefined ? value.checked : !value.hidden
		}))

		// 深拷贝保存原始列设置
		originColumns.value = columnsSetting.value.map((value) => ({ ...value }))

		// 处理全选状态
		const notCheckedList = columnsSetting.value.filter((value) => !value.checked)
		checkAll.value = !notCheckedList.length
		indeterminate.value = notCheckedList.length > 0 && notCheckedList.length < columnsSetting.value.length

		// 触发列变化事件
		emitColumnChange()
	}

	// 监听props.columns的变化
	watch(
		() => props.columns,
		(newColumns) => {
			initializeColumns(newColumns)
		},
		{ immediate: true }
	)

	const reset = () => {
		columnsSetting.value = originColumns.value.map((value) => ({ ...value }))
		indeterminate.value = false
		const checkedList = columnsSetting.value.filter((value) => value.checked)
		checkAll.value = checkedList.length === columnsSetting.value.length
		emitColumnChange()
	}

	const onChange = () => {
		const checkedList = columnsSetting.value.filter((value) => value.checked)
		indeterminate.value = Boolean(checkedList.length) && checkedList.length < columnsSetting.value.length
		checkAll.value = checkedList.length === columnsSetting.value.length
		emitColumnChange()
	}

	// 全选
	const onCheckAllChange = (e) => {
		e.preventDefault()
		const val = e.target.checked

		indeterminate.value = false
		checkAll.value = val
		columnsSetting.value = props.columns.map((value) => ({
			...value,
			checked: val
		}))
		emitColumnChange()
	}
</script>
<style lang="less" scoped>
	.s-tool-column-item {
		display: flex;
		align-items: center;
		padding: 4px 16px 4px 4px;
		.ant-checkbox-wrapper {
			flex: 1;
		}
		.s-tool-column-handle {
			opacity: 0.8;
			cursor: move;
			.anticon-more {
				font-size: 12px;
				& + .anticon-more {
					margin: 0px 4px 0 -8px;
				}
			}
		}
	}
	.s-tool-column-header {
		padding: 5px 16px 10px 24px;
		min-width: 180px;
	}
	.s-tool-column {
		.ant-divider {
			margin: 0;
		}
		.ant-checkbox-group {
			padding: 4px 0;
			display: block;
		}
	}
</style>
