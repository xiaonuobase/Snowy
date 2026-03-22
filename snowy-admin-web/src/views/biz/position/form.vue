<template>
	<xn-form-container
		:title="formData.id ? '编辑岗位' : '增加岗位'"
		:width="550"
		:visible="visible"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-form-item label="所属组织：" name="orgId">
				<a-spin :spinning="treeLoading">
					<a-tree-select
						v-model:value="formData.orgId"
						class="xn-wd"
						:dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
						placeholder="请选择组织"
						allow-clear
						:tree-data="treeData"
						v-model:treeExpandedKeys="treeDefaultExpandedKeys"
						:field-names="treeFieldNames"
						tree-line
						:load-data="onLoadData"
					/>
				</a-spin>
			</a-form-item>
			<a-form-item label="岗位名称：" name="name">
				<a-input v-model:value="formData.name" placeholder="请输入岗位名称" allow-clear />
			</a-form-item>
			<a-form-item label="岗位分类：" name="category">
				<a-select
					v-model:value="formData.category"
					:options="positionCategoryOptions"
					class="xn-wd"
					placeholder="请选择岗位分类"
				>
				</a-select>
			</a-form-item>
			<a-form-item label="排序：" name="sortCode">
				<a-input-number class="xn-wd" v-model:value="formData.sortCode" :max="100" />
			</a-form-item>
		</a-form>
		<template #footer>
			<a-button class="xn-mr8" @click="onClose">关闭</a-button>
			<a-button type="primary" :loading="submitLoading" @click="onSubmit">保存</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="bizPositionForm">
	import { required } from '@/utils/formRules'
	import bizPositionApi from '@/api/biz/bizPositionApi'
	import tool from '@/utils/tool'

	// 定义emit事件
	const emit = defineEmits({ successful: null })
	// 默认是关闭状态
	const visible = ref(false)
	const formRef = ref()
	// 表单数据，也就是默认给一些数据
	const formData = ref({})
	// 定义机构元素
	const treeData = ref([])
	const submitLoading = ref(false)
	const treeLoading = ref(false)
	const treeDefaultExpandedKeys = ref([])
	const treeFieldNames = { children: 'children', label: 'name', value: 'id' }
	// 将祖先扁平节点合并到懒加载根节点中
	const buildTreeWithAncestors = (rootNodes, ancestorNodes) => {
		const allNodes = [...rootNodes]
		const existingIds = new Set(allNodes.map((n) => n.id))
		ancestorNodes.forEach((node) => {
			if (!existingIds.has(node.id)) {
				allNodes.push(node)
				existingIds.add(node.id)
			}
		})
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
		const ancestorIdSet = new Set(ancestorNodes.map((n) => n.id))
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
	const collectAncestorKeysFromFlat = (ancestorNodes, selectedIds) => {
		const selectedSet = new Set(selectedIds)
		return ancestorNodes.filter((n) => !selectedSet.has(n.id) || !n.isLeaf).map((n) => n.id)
	}
	// 打开抽屉
	const onOpen = (record, orgId) => {
		visible.value = true
		formData.value = {
			sortCode: 99
		}
		if (orgId) {
			formData.value.orgId = orgId
		}
		if (record) {
			formData.value = Object.assign({}, record)
		}
		nextTick(() => {
			if (record && record.orgId) {
				// 编辑模式：懒加载根节点 + 祖先路径
				treeLoading.value = true
				const rootPromise = bizPositionApi.positionOrgTreeSelector()
				const ancestorPromise = bizPositionApi.positionGetAncestorNodes([record.orgId])
				Promise.all([rootPromise, ancestorPromise])
					.then(([rootNodes, ancestorNodes]) => {
						const roots = (rootNodes || []).map((item) => ({
							...item,
							isLeaf: item.isLeaf === undefined ? false : item.isLeaf
						}))
						treeData.value = buildTreeWithAncestors(roots, ancestorNodes || [])
						treeDefaultExpandedKeys.value = collectAncestorKeysFromFlat(ancestorNodes || [], [record.orgId])
					})
					.finally(() => {
						treeLoading.value = false
					})
			} else {
				// 新增模式：懒加载树
				bizPositionApi.positionOrgTreeSelector().then((res) => {
					treeData.value = res.map((item) => {
						return {
							...item,
							isLeaf: item.isLeaf === undefined ? false : item.isLeaf
						}
					})
					// 只有一个根节点时才自动展开
					if (treeData.value.length === 1) {
						treeDefaultExpandedKeys.value.push(treeData.value[0].id)
					}
				})
			}
		})
	}
	// 懒加载子节点
	const onLoadData = (treeNode) => {
		return new Promise((resolve) => {
			if (treeNode.dataRef.children) {
				resolve()
				return
			}
			bizPositionApi
				.positionOrgTreeSelector({
					parentId: treeNode.dataRef.id
				})
				.then((res) => {
					treeNode.dataRef.children = res.map((item) => {
						return {
							...item,
							isLeaf: item.isLeaf === undefined ? false : item.isLeaf
						}
					})
					treeData.value = [...treeData.value]
					resolve()
				})
		})
	}
	// 关闭抽屉
	const onClose = () => {
		treeData.value = []
		treeDefaultExpandedKeys.value = []
		visible.value = false
	}
	// 默认要校验的
	const formRules = {
		orgId: [required('请选择所属组织')],
		name: [required('请输入岗位名称')],
		category: [required('请选择岗位分类')],
		sortCode: [required('请选择排序')]
	}
	const positionCategoryOptions = tool.dictList('POSITION_CATEGORY')
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value
			.validate()
			.then(() => {
				submitLoading.value = true
				bizPositionApi
					.submitForm(formData.value, formData.value.id)
					.then(() => {
						visible.value = false
						emit('successful')
					})
					.finally(() => {
						submitLoading.value = false
					})
			})
			.catch(() => {})
	}
	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>
