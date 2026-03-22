<template>
	<a-modal
		v-model:open="visible"
		title="选择组织"
		:width="500"
		:mask-closable="false"
		:destroy-on-close="true"
		@ok="handleOk"
		@cancel="onClose"
	>
		<a-spin :spinning="treeLoading">
			<div class="scopeDefineOrgActions">
				<a-space size="small">
					<a-tooltip title="仅对已展开加载的节点生效">
						<a-button size="small" @click="checkAll">全选</a-button>
					</a-tooltip>
					<a-tooltip title="仅对已展开加载的节点生效">
						<a-button size="small" @click="invertCheck">反选</a-button>
					</a-tooltip>
				</a-space>
			</div>
			<div class="scopeDefineOrgTreeDiv">
				<a-tree
					v-model:expandedKeys="defaultExpandedKeys"
					v-model:checkedKeys="checkedKeys"
					:tree-data="treeData"
					:field-names="treeFieldNames"
					checkable
					check-strictly
					:selectable="false"
					:load-data="onLoadData"
					:height="400"
					@check="treeCheck"
				>
				</a-tree>
			</div>
		</a-spin>
	</a-modal>
</template>

<script setup="props, context" name="scopeDefineOrg">
	import roleApi from '@/api/sys/roleApi'
	const visible = ref(false)
	let defaultExpandedKeys = ref([])
	let checkedKeys = ref([])
	const treeData = ref([])
	const treeLoading = ref(false)

	const getAllIds = (nodes) => {
		const ids = []
		const stack = [...nodes]
		while (stack.length) {
			const n = stack.pop()
			if (n && n.id) ids.push(n.id)
			if (n && n.children && n.children.length) {
				for (let i = 0; i < n.children.length; i++) {
					stack.push(n.children[i])
				}
			}
		}
		return ids
	}

	const resultDataModel = {
		dataScopeId: '',
		defineOrgIdData: {
			scopeCategory: 'SCOPE_ORG_DEFINE',
			scopeDefineOrgIdList: []
		}
	}

	// 懒加载子节点回调
	const onLoadData = (treeNode) => {
		return new Promise((resolve) => {
			if (treeNode.dataRef.children) {
				resolve()
				return
			}
			roleApi.roleOrgTreeSelector({ parentId: treeNode.dataRef.id }).then((res) => {
				treeNode.dataRef.children = res.map((item) => ({
					...item,
					isLeaf: item.isLeaf === undefined ? false : item.isLeaf
				}))
				treeData.value = [...treeData.value]
				resolve()
			})
		})
	}

	// 将祖先扁平节点列表与根节点列表合并，构建出包含祖先链路的部分树
	const buildTreeWithAncestors = (rootNodes, ancestorNodes) => {
		const allNodes = [...rootNodes]
		const existingIds = new Set(allNodes.map((n) => n.id))
		// 将祖先节点中不在根节点列表中的加入
		ancestorNodes.forEach((node) => {
			if (!existingIds.has(node.id)) {
				allNodes.push(node)
				existingIds.add(node.id)
			}
		})
		// 按 parentId 分组
		const parentChildMap = new Map()
		allNodes.forEach((node) => {
			const pid = node.parentId
			if (!parentChildMap.has(pid)) {
				parentChildMap.set(pid, [])
			}
			const siblings = parentChildMap.get(pid)
			if (!siblings.find((n) => n.id === node.id)) {
				siblings.push(node)
			}
		})
		// 祖先节点id集合
		const ancestorIdSet = new Set(ancestorNodes.map((n) => n.id))
		// 从根开始递归构建，只展开有祖先路径的分支
		const buildBranch = (parentId) => {
			const children = parentChildMap.get(parentId)
			if (!children) return undefined
			return children.map((child) => {
				const node = { ...child, isLeaf: child.isLeaf === undefined ? false : child.isLeaf }
				if (ancestorIdSet.has(child.id) && parentChildMap.has(child.id)) {
					node.children = buildBranch(child.id)
				}
				return node
			})
		}
		return buildBranch('0') || []
	}

	// 收集所有祖先节点的id用于展开
	const collectAncestorKeys = (ancestorNodes, selectedIds) => {
		const selectedSet = new Set(selectedIds)
		// 祖先节点中排除叶子选中节点本身，只展开其父级路径
		return ancestorNodes.filter((n) => !selectedSet.has(n.id) || !n.isLeaf).map((n) => n.id)
	}

	// 打开此界面需要具体某条菜单的id跟选中的
	const onOpen = (id, checkKeys) => {
		visible.value = true
		resultDataModel.dataScopeId = id
		defaultExpandedKeys.value = []
		checkedKeys.value = []
		treeData.value = []

		// 解析已选中的key
		let selectedIds = []
		if (checkKeys && checkKeys.length > 0) {
			selectedIds = checkKeys
				.toString()
				.split(',')
				.filter((k) => k)
		}

		if (selectedIds.length > 0) {
			// 有已选中节点：并行请求根节点 + 祖先路径
			treeLoading.value = true
			const rootPromise = roleApi.roleOrgTreeSelector({})
			const ancestorPromise = roleApi.roleGetAncestorNodes(selectedIds)
			Promise.all([rootPromise, ancestorPromise])
				.then(([rootNodes, ancestorNodes]) => {
					// 为根节点设置isLeaf
					const roots = (rootNodes || []).map((item) => ({
						...item,
						isLeaf: item.isLeaf === undefined ? false : item.isLeaf
					}))
					// 合并构建树
					treeData.value = buildTreeWithAncestors(roots, ancestorNodes || [])
					// 回显选中项
					echoOrgSelectKeys(checkKeys)
					// 展开祖先路径
					defaultExpandedKeys.value = collectAncestorKeys(ancestorNodes || [], selectedIds)
				})
				.finally(() => {
					treeLoading.value = false
				})
		} else {
			// 无已选中节点：仅懒加载根节点
			treeLoading.value = true
			roleApi
				.roleOrgTreeSelector({})
				.then((res) => {
					if (res !== null) {
						treeData.value = res.map((item) => ({
							...item,
							isLeaf: item.isLeaf === undefined ? false : item.isLeaf
						}))
						// 默认展开1级
						treeData.value.forEach((item) => {
							if (item.parentId === '0') {
								defaultExpandedKeys.value.push(item.id)
							}
						})
					}
				})
				.finally(() => {
					treeLoading.value = false
				})
		}
	}
	const onClose = () => {
		visible.value = false
	}
	// 回显机构的选中项
	const echoOrgSelectKeys = (checkKeys) => {
		checkedKeys.value = []
		if (checkKeys && checkKeys.length > 0) {
			checkKeys
				.toString()
				.split(',')
				.forEach((key) => {
					checkedKeys.value.push(key)
				})
		}
		resultDataModel.defineOrgIdData.scopeDefineOrgIdList = checkedKeys.value
	}
	// 替换treeNode 中 title,key,children
	const treeFieldNames = { children: 'children', title: 'name', key: 'id' }

	const treeCheck = (keysObj) => {
		checkedKeys.value = keysObj.checked
		resultDataModel.defineOrgIdData.scopeDefineOrgIdList = keysObj.checked
	}

	const getCurrentCheckedIds = () => {
		if (Array.isArray(checkedKeys.value)) return checkedKeys.value
		if (checkedKeys.value && Array.isArray(checkedKeys.value.checked)) return checkedKeys.value.checked
		return []
	}

	const checkAll = () => {
		const all = getAllIds(treeData.value)
		checkedKeys.value = all
		resultDataModel.defineOrgIdData.scopeDefineOrgIdList = all
	}

	const invertCheck = () => {
		const all = getAllIds(treeData.value)
		const current = getCurrentCheckedIds()
		const set = new Set(current)
		const next = all.filter((id) => !set.has(id))
		checkedKeys.value = next
		resultDataModel.defineOrgIdData.scopeDefineOrgIdList = next
	}
	// 定义emit事件
	const emit = defineEmits({
		click: null
	})
	const handleOk = () => {
		emit('click', resultDataModel)
		visible.value = false
	}

	defineExpose({
		onOpen
	})
</script>

<style lang="less">
	// 穿梭框宽度重写
	.ant-transfer-list {
		width: 220px !important;
	}
	.scopeDefineOrgTreeDiv {
		max-height: 450px;
		overflow: auto;
	}
	.scopeDefineOrgActions {
		margin-bottom: 8px;
	}
</style>
