<template>
	<a-tree-select
		v-model:value="defaultSelectKeys"
		show-search
		class="xn-wd"
		:dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
		placeholder="请选择菜单"
		:field-names="treeFieldNames"
		allow-clear
		multiple
		tree-default-expand-all
		:tree-data="menu"
	>
	</a-tree-select>
</template>

<script setup name="menuTreeSelect">
	import tool from '@/utils/tool'
	import { onMounted } from 'vue'
	const defaultSelectKeys = ref([])
	const menu = ref([])

	const props = defineProps({
		defaultSelectKeys: {
			type: Array,
			default: () => [],
			required: false
		},
		defaultSelectData: {
			type: Array,
			default: () => [],
			required: false
		},
		resultData: {
			type: Boolean,
			default: () => false,
			required: false
		}
	})

	const treeFieldNames = { children: 'children', title: 'title', key: 'id', value: 'id' }
	onMounted(() => {
		// 设置选中项目，调用的地方可传纯id数组，或者是对象型数组
		if (!props.defaultSelectKeys) {
			defaultSelectKeys.value = props.defaultSelectData.map((m) => {
				return m.id
			})
		} else {
			defaultSelectKeys.value = props.defaultSelectKeys
		}
		menu.value = traverseChildren(tool.data.get('MENU'))
	})
	// 遍历增加属性
	const traverseChildren = (data = []) => {
		// 递归遍历控件树
		const traverse = (array) => {
			array.forEach((element) => {
				if (element.children) {
					// 设置支节点不可选择
					element.selectable = false
					traverse(element.children)
				}
			})
		}
		traverse(data)
		return data
	}
	// 遍历增加属性
	const traverseResultDataList = (menu, data = []) => {
		const result = []
		// 递归遍历控件树
		const traverse = (array) => {
			array.forEach((element) => {
				if (data) {
					data.forEach((item) => {
						if (item === element.id) {
							const obj = {
								id: element.id,
								title: element.title,
								icon: element.icon,
								path: element.path
							}
							result.push(obj)
						}
					})
				}
				if (element.children) {
					traverse(element.children)
				}
			})
		}
		traverse(menu)
		return result
	}

	// 获取选择的数据
	const getSelectData = () => {
		if (props.resultData) {
			return traverseResultDataList(menu.value, defaultSelectKeys.value)
		}
		return defaultSelectKeys.value
	}
	// 设置回显的数据
	const setSelectData = (data) => {
		defaultSelectKeys.value = data.map((m) => {
			return m.id
		})
	}
	defineExpose({
		getSelectData,
		setSelectData
	})
</script>
