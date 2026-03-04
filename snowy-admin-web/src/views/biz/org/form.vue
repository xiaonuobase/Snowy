<template>
	<xn-form-container
		:title="formData.id ? '编辑机构' : '增加机构'"
		:width="550"
		:visible="visible"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-form-item label="上级机构：" name="parentId">
				<a-spin :spinning="treeLoading">
					<a-tree-select
						v-model:value="formData.parentId"
						class="xn-wd"
						:dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
						placeholder="请选择上级机构"
						allow-clear
						:tree-data="treeData"
						v-model:treeExpandedKeys="treeDefaultExpandedKeys"
						:field-names="treeFieldNames"
						tree-line
						:load-data="isFullTree ? undefined : onLoadData"
					/>
				</a-spin>
			</a-form-item>
			<a-form-item label="机构名称：" name="name">
				<a-input v-model:value="formData.name" placeholder="请输入机构名称" allow-clear />
			</a-form-item>
			<a-form-item label="机构分类：" name="category">
				<a-select
					v-model:value="formData.category"
					:options="orgCategoryOptions"
					class="xn-wd"
					placeholder="请选择机构分类"
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

<script setup name="bizOrgForm">
	import { required } from '@/utils/formRules'
	import bizOrgApi from '@/api/biz/bizOrgApi'
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
	// 是否加载了全量树（有parentId时加载全量树以便展开到选中节点，否则懒加载）
	const isFullTree = ref(false)
	// 在全量树中查找目标节点的所有祖先ID（用于展开树到选中节点）
	const collectAncestorKeys = (nodes, targetId, path = []) => {
		if (!nodes) return null
		for (const node of nodes) {
			if (node.id === targetId) return path
			if (node.children) {
				const found = collectAncestorKeys(node.children, targetId, [...path, node.id])
				if (found) return found
			}
		}
		return null
	}
	// 展开树到选中的机构节点
	const expandToSelectedOrgs = () => {
		if (formData.value.parentId) {
			const ancestors = collectAncestorKeys(treeData.value, formData.value.parentId)
			if (ancestors) {
				ancestors.forEach((id) => {
					if (!treeDefaultExpandedKeys.value.includes(id)) {
						treeDefaultExpandedKeys.value.push(id)
					}
				})
			}
		}
	}
	// 加载全量树（用于需要展开到指定节点的场景）
	const loadFullTree = () => {
		return bizOrgApi.orgTreeSelector({ searchKey: '' }).then((res) => {
			if (res !== null) {
				treeData.value = [
					{
						id: '0',
						parentId: '-1',
						name: '顶级',
						children: res,
						isLeaf: false
					}
				]
				treeDefaultExpandedKeys.value.push('0')
			}
		})
	}
	// 加载懒加载树（无需展开到指定节点时使用）
	const loadLazyTree = () => {
		return bizOrgApi.orgTreeSelector().then((res) => {
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
				// 编辑模式：加载全量树 + 详情，展开到选中节点
				isFullTree.value = true
				treeLoading.value = true
				const treePromise = loadFullTree()
				const detailPromise = bizOrgApi.orgDetail({ id: record.id }).then((data) => {
					formData.value = Object.assign({}, data)
					return data
				})
				Promise.all([treePromise, detailPromise])
					.then(() => {
						expandToSelectedOrgs()
					})
					.finally(() => {
						treeLoading.value = false
					})
			} else if (parentId) {
				// 新增模式且有parentId：加载全量树，展开到parentId节点
				isFullTree.value = true
				treeLoading.value = true
				loadFullTree()
					.then(() => {
						expandToSelectedOrgs()
					})
					.finally(() => {
						treeLoading.value = false
					})
			} else {
				// 新增模式无parentId：懒加载树
				isFullTree.value = false
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
			bizOrgApi
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
		parentId: [required('请选择上级机构')],
		name: [required('请输入机构名称')],
		category: [required('请选择机构分类')],
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
				bizOrgApi
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
			return bizOrgApi.orgTreeSelector(param).then((data) => {
				return Promise.resolve(data)
			})
		},
		userPageApi: (param) => {
			return bizOrgApi.orgUserSelector(param).then((data) => {
				return Promise.resolve(data)
			})
		}
	}
	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>
