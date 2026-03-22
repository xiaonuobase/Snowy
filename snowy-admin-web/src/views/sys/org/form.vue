<template>
	<xn-form-container
		:title="formData.id ? '编辑组织' : '增加组织'"
		:width="550"
		:visible="visible"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-form-item label="上级组织：" name="parentId">
				<a-spin :spinning="treeLoading">
					<a-tree-select
						v-model:value="formData.parentId"
						class="xn-wd"
						:dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
						placeholder="请选择上级组织"
						allow-clear
						:tree-data="treeData"
						v-model:treeExpandedKeys="treeDefaultExpandedKeys"
						:field-names="treeFieldNames"
						tree-line
						:load-data="onLoadData"
					/>
				</a-spin>
			</a-form-item>
			<a-form-item label="组织名称：" name="name">
				<a-input v-model:value="formData.name" placeholder="请输入组织名称" allow-clear />
			</a-form-item>
			<a-form-item label="组织分类：" name="category">
				<a-select
					v-model:value="formData.category"
					:options="orgCategoryOptions"
					class="xn-wd"
					placeholder="请选择组织分类"
				/>
			</a-form-item>
			<a-form-item label="排序：" name="sortCode">
				<a-input-number class="xn-wd" v-model:value="formData.sortCode" :max="100" />
			</a-form-item>
			<a-form-item label="指定主管：" name="directorId">
				<xn-user-selector
					:org-tree-api="selectorApiFunction.orgTreeApi"
					:user-page-api="selectorApiFunction.userPageApi"
					:radio-model="true"
					v-model:value="formData.directorId"
				/>
			</a-form-item>
		</a-form>
		<template #footer>
			<a-button class="xn-mr8" @click="onClose">关闭</a-button>
			<a-button type="primary" :loading="submitLoading" @click="onSubmit">保存</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="orgForm">
	import { required } from '@/utils/formRules'
	import orgApi from '@/api/sys/orgApi'
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
	// 将祖先扁平节点合并到懒加载根节点中，构建包含祖先链路的部分树
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
	// 从祖先扁平节点中提取需要展开的key
	const collectAncestorKeysFromFlat = (ancestorNodes, selectedIds) => {
		const selectedSet = new Set(selectedIds)
		return ancestorNodes.filter((n) => !selectedSet.has(n.id) || !n.isLeaf).map((n) => n.id)
	}
	// 加载懒加载树（无需展开到指定节点时使用）
	const loadLazyTree = () => {
		return orgApi.orgTreeSelector().then((res) => {
			treeData.value = [
				{
					id: '0',
					parentId: '-1',
					name: '顶级',
					children: res.map((item) => {
						return {
							...item,
							isLeaf: item.isLeaf === undefined ? false : item.isLeaf
						}
					}),
					isLeaf: false
				}
			]
			treeDefaultExpandedKeys.value.push('0')
		})
	}
	// 打开抽屉
	const onOpen = (record, parentId) => {
		visible.value = true
		formData.value = {
			sortCode: 99
		}
		if (parentId) {
			formData.value.parentId = parentId
		}
		nextTick(() => {
			if (record) {
				// 编辑模式：懒加载根节点 + 祖先路径并行请求
				treeLoading.value = true
				const detailPromise = orgApi.orgDetail({ id: record.id }).then((data) => {
					formData.value = Object.assign({}, data)
					return data
				})
				detailPromise.then((data) => {
					const selectedParentId = data.parentId
					if (selectedParentId && selectedParentId !== '0') {
						const rootPromise = orgApi.orgTreeSelector()
						const ancestorPromise = orgApi.orgGetAncestorNodes([selectedParentId])
						Promise.all([rootPromise, ancestorPromise])
							.then(([rootNodes, ancestorNodes]) => {
								const roots = (rootNodes || []).map((item) => ({
									...item,
									isLeaf: item.isLeaf === undefined ? false : item.isLeaf
								}))
								const mergedChildren = buildTreeWithAncestors(roots, ancestorNodes || [])
								treeData.value = [{ id: '0', parentId: '-1', name: '顶级', children: mergedChildren, isLeaf: false }]
								treeDefaultExpandedKeys.value = [
									'0',
									...collectAncestorKeysFromFlat(ancestorNodes || [], [selectedParentId])
								]
							})
							.finally(() => {
								treeLoading.value = false
							})
					} else {
						loadLazyTree().finally(() => {
							treeLoading.value = false
						})
					}
				})
			} else if (parentId) {
				// 新增模式且有parentId：懒加载根节点 + 祖先路径
				treeLoading.value = true
				if (parentId !== '0') {
					const rootPromise = orgApi.orgTreeSelector()
					const ancestorPromise = orgApi.orgGetAncestorNodes([parentId])
					Promise.all([rootPromise, ancestorPromise])
						.then(([rootNodes, ancestorNodes]) => {
							const roots = (rootNodes || []).map((item) => ({
								...item,
								isLeaf: item.isLeaf === undefined ? false : item.isLeaf
							}))
							const mergedChildren = buildTreeWithAncestors(roots, ancestorNodes || [])
							treeData.value = [{ id: '0', parentId: '-1', name: '顶级', children: mergedChildren, isLeaf: false }]
							treeDefaultExpandedKeys.value = ['0', ...collectAncestorKeysFromFlat(ancestorNodes || [], [parentId])]
						})
						.finally(() => {
							treeLoading.value = false
						})
				} else {
					loadLazyTree().finally(() => {
						treeLoading.value = false
					})
				}
			} else {
				// 新增模式无parentId：懒加载树
				loadLazyTree()
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
			orgApi
				.orgTreeSelector({
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
		parentId: [required('请选择上级组织')],
		name: [required('请输入组织名称')],
		category: [required('请选择组织分类')],
		sortCode: [required('请选择排序')]
	}
	// 机构分类字典
	const orgCategoryOptions = tool.dictList('ORG_CATEGORY')
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value
			.validate()
			.then(() => {
				submitLoading.value = true
				orgApi
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
	// 传递设计器需要的API
	const selectorApiFunction = {
		orgTreeApi: (param) => {
			return orgApi.orgTreeSelector(param).then((data) => {
				return Promise.resolve(data)
			})
		},
		userPageApi: (param) => {
			return orgApi.orgUserSelector(param).then((data) => {
				return Promise.resolve(data)
			})
		}
	}
	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>
