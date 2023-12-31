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
	const originColumns = ref()

	onMounted(() => {
		columnsSetting.value = props.columns.map((value) => {
			if (value.checked === undefined) {
				return {
					...value,
					checked: true
				}
			} else return value
		})

		// 这里要用深的拷贝，否则，勾选了字段时会修改了originColumns里的内容
		originColumns.value = columnsSetting.value.map((value) => ({ ...value }))

		// 处理全选组件
		const notCheckedList = columnsSetting.value.filter((value) => !value.checked)
		if (notCheckedList.length) checkAll.value = false
	})

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

	const emitColumnChange = () => {
		// eslint-disable-next-line vue/require-explicit-emits
		emit('columnChange', columnsSetting.value)
	}
</script>
