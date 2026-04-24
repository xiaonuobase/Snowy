<template>
	<a-spin :spinning="loading" size="small">
		<a-tree-select
			v-model:value="modelValue"
			class="xn-wd"
			:dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
			:placeholder="props.placeholder"
			:allow-clear="props.allowClear"
			:disabled="props.disabled"
			:multiple="props.multiple"
			tree-line
			:tree-data="treeData"
			v-model:treeExpandedKeys="expandedKeys"
			:field-names="fieldNames"
			:load-data="onLoadData"
			v-bind="$attrs"
			@change="handleChange"
		/>
	</a-spin>
</template>

<script setup name="xnTreeSelect">
	/**
	 * 通用懒加载树选择器
	 *
	 * 功能：
	 * 1. 懒加载树节点（首次加载根节点，展开时加载子节点）
	 * 2. 编辑回显时自动加载祖先路径 + 同级兄弟节点，只展开直链路径
	 * 3. 未展开的兄弟节点支持点击懒加载
	 *
	 * 使用方式：
	 * <xn-tree-select
	 *   v-model:value="formData.orgId"
	 *   :tree-api="userApi.userOrgTreeSelector"
	 *   :ancestor-api="userApi.userGetAncestorNodes"
	 *   placeholder="请选择组织"
	 *   @change="onOrgChange"
	 * />
	 *
	 * // 编辑时调用回显：
	 * treeSelectRef.value.echo(['orgId1', 'orgId2'])
	 *
	 * // 新增时初始化（无需回显）：
	 * treeSelectRef.value.init()
	 *
	 * // 新增但需要定位到某节点：
	 * treeSelectRef.value.echo(['orgId1'])
	 */

	const emit = defineEmits({ change: null, 'update:value': null })
	const props = defineProps({
		/** 选中值（v-model:value） */
		value: { type: [String, Array], default: undefined },
		/** 树数据加载接口，参数为 { parentId? }，返回扁平节点数组 */
		treeApi: { type: Function, required: true },
		/** 祖先节点加载接口，参数为 id 数组，返回扁平节点数组（含兄弟） */
		ancestorApi: { type: Function, default: undefined },
		/** 占位文本 */
		placeholder: { type: String, default: '请选择' },
		/** 允许清除 */
		allowClear: { type: Boolean, default: true },
		/** 禁用 */
		disabled: { type: Boolean, default: false },
		/** 多选 */
		multiple: { type: Boolean, default: false },
		/** 顶部虚拟节点，如 { id: '0', name: '顶级' }，加载的根节点会作为它的 children */
		topNode: { type: Object, default: undefined },
		/** 字段映射 */
		fieldNames: {
			type: Object,
			default: () => ({ children: 'children', label: 'name', key: 'id', value: 'id' })
		},
		/** 父ID字段名（用于构建树和回溯路径） */
		parentIdKey: { type: String, default: 'parentId' },
		/** 根节点的父ID值 */
		rootParentId: { type: String, default: '0' }
	})

	const modelValue = ref(props.value)
	const treeData = ref([])
	const expandedKeys = ref([])
	const loading = ref(false)

	// 同步外部 value 变化
	watch(
		() => props.value,
		(val) => {
			modelValue.value = val
		}
	)

	// ======================== 核心工具方法 ========================

	/** 标准化节点（确保 isLeaf 有值） */
	const normalizeNode = (node) => ({
		...node,
		isLeaf: node.isLeaf === undefined ? false : node.isLeaf
	})

	/** 将祖先扁平节点（含兄弟）与根节点合并构建树 */
	const buildTreeWithAncestors = (rootNodes, ancestorNodes) => {
		const allNodes = [...rootNodes]
		const existingIds = new Set(allNodes.map((n) => n[props.fieldNames.key || 'id']))
		const idKey = props.fieldNames.key || 'id'
		const pidKey = props.parentIdKey

		ancestorNodes.forEach((node) => {
			if (!existingIds.has(node[idKey])) {
				allNodes.push(node)
				existingIds.add(node[idKey])
			}
		})

		// 按 parentId 分组
		const parentChildMap = new Map()
		allNodes.forEach((node) => {
			const pid = node[pidKey]
			if (!parentChildMap.has(pid)) {
				parentChildMap.set(pid, [])
			}
			const siblings = parentChildMap.get(pid)
			if (!siblings.find((n) => n[idKey] === node[idKey])) {
				siblings.push(node)
			}
		})

		// 只有祖先节点才递归展开子节点，兄弟节点保留懒加载能力
		const ancestorIdSet = new Set(ancestorNodes.map((n) => n[idKey]))
		const buildBranch = (parentId) => {
			const children = parentChildMap.get(parentId)
			if (!children) return undefined
			return children.map((child) => {
				const node = normalizeNode(child)
				if (ancestorIdSet.has(child[idKey]) && parentChildMap.has(child[idKey])) {
					node.children = buildBranch(child[idKey])
				}
				return node
			})
		}
		return buildBranch(props.rootParentId) || []
	}

	/** 从选中节点沿 parentId 向上回溯，只收集直链祖先作为展开 key */
	const collectExpandKeys = (ancestorNodes, selectedIds) => {
		const idKey = props.fieldNames.key || 'id'
		const pidKey = props.parentIdKey
		const nodeMap = new Map(ancestorNodes.map((n) => [n[idKey], n]))
		const keys = new Set()
		const selectedSet = new Set(selectedIds)

		selectedIds.forEach((id) => {
			let current = nodeMap.get(id)
			while (current) {
				keys.add(current[idKey])
				current = nodeMap.get(current[pidKey])
			}
		})

		// 移除叶子选中节点自身
		return [...keys].filter((id) => {
			const node = nodeMap.get(id)
			return !selectedSet.has(id) || (node && !node.isLeaf)
		})
	}

	/** 将节点列表包裹在 topNode 下（如果配置了的话） */
	const wrapWithTopNode = (nodes) => {
		if (!props.topNode) return nodes
		const idKey = props.fieldNames.key || 'id'
		return [{ ...props.topNode, children: nodes, isLeaf: false, [idKey]: props.topNode[idKey] || props.topNode.id }]
	}

	// ======================== 对外暴露方法 ========================

	/**
	 * 初始化树（新增模式，无需回显）
	 * 加载根节点，单根时自动展开
	 */
	const init = () => {
		loading.value = true
		expandedKeys.value = []
		return props
			.treeApi()
			.then((res) => {
				const nodes = (res || []).map(normalizeNode)
				treeData.value = wrapWithTopNode(nodes)
				// topNode 模式自动展开顶级，否则单根时展开
				const idKey = props.fieldNames.key || 'id'
				if (props.topNode) {
					expandedKeys.value = [props.topNode[idKey] || props.topNode.id]
				} else if (nodes.length === 1) {
					expandedKeys.value = [nodes[0][idKey]]
				}
			})
			.finally(() => {
				loading.value = false
			})
	}

	/**
	 * 回显模式：加载根节点 + 祖先路径（含兄弟），展开到选中节点
	 * @param {Array<string>} selectedIds - 需要回显的节点ID数组
	 */
	const echo = (selectedIds) => {
		if (!selectedIds || selectedIds.length === 0 || !props.ancestorApi) {
			return init()
		}
		loading.value = true
		expandedKeys.value = []
		const rootPromise = props.treeApi()
		const ancestorPromise = props.ancestorApi(selectedIds)
		return Promise.all([rootPromise, ancestorPromise])
			.then(([rootNodes, ancestorNodes]) => {
				const roots = (rootNodes || []).map(normalizeNode)
				const mergedTree = buildTreeWithAncestors(roots, ancestorNodes || [])
				treeData.value = wrapWithTopNode(mergedTree)
				const echoKeys = collectExpandKeys(ancestorNodes || [], selectedIds)
				if (props.topNode) {
					const idKey = props.fieldNames.key || 'id'
					expandedKeys.value = [props.topNode[idKey] || props.topNode.id, ...echoKeys]
				} else {
					expandedKeys.value = echoKeys
				}
			})
			.finally(() => {
				loading.value = false
			})
	}

	/**
	 * 重置组件状态
	 */
	const reset = () => {
		treeData.value = []
		expandedKeys.value = []
		modelValue.value = undefined
	}

	// ======================== 内部事件处理 ========================

	/** 懒加载子节点 */
	const onLoadData = (treeNode) => {
		return new Promise((resolve) => {
			if (treeNode.dataRef.children) {
				resolve()
				return
			}
			const idKey = props.fieldNames.key || 'id'
			props
				.treeApi({ parentId: treeNode.dataRef[idKey] })
				.then((res) => {
					treeNode.dataRef.children = (res || []).map(normalizeNode)
					treeData.value = [...treeData.value]
					resolve()
				})
		})
	}

	/** 选中变化 */
	const handleChange = (value, label, extra) => {
		modelValue.value = value
		emit('update:value', value)
		emit('change', value, label, extra)
	}

	defineExpose({
		init,
		echo,
		reset,
		/** 直接访问树数据（供特殊场景使用） */
		treeData,
		expandedKeys
	})
</script>
