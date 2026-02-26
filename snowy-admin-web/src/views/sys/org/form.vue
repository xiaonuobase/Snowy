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
				<a-tree-select
					v-model:value="formData.parentId"
					class="xn-wd"
					:dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
					placeholder="请选择上级组织"
					allow-clear
					:tree-data="treeData"
					:field-names="{
						children: 'children',
						label: 'name',
						value: 'id'
					}"
					tree-line
					:load-data="onLoadData"
				/>
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
					:org-tree-lazy-api="selectorApiFunction.orgTreeLazyApi"
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
	import userCenterApi from '@/api/sys/userCenterApi'
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

	// 在树中递归查找节点
	const findNodeInTree = (nodes, id) => {
		if (!nodes) return false
		for (const node of nodes) {
			if (node.id === id) return true
			if (node.children && findNodeInTree(node.children, id)) return true
		}
		return false
	}
	// 确保选中的机构节点在树中可回显名称
	const ensureOrgInTree = (orgId) => {
		if (!orgId || orgId === '0' || findNodeInTree(treeData.value, orgId)) return
		userCenterApi.userCenterGetOrgListByIdList({ idList: [orgId] }).then((data) => {
			if (data && data.length > 0 && treeData.value.length > 0) {
				const topNode = treeData.value[0]
				if (topNode.children) {
					topNode.children.push({ ...data[0], isLeaf: true })
				} else {
					topNode.children = [{ ...data[0], isLeaf: true }]
				}
				treeData.value = [...treeData.value]
			}
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
		// 获取机构树并加入顶级
		const treePromise = orgApi.orgOrgTreeLazySelector().then((res) => {
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
		})
		if (record) {
			const detailPromise = orgApi.orgDetail({ id: record.id }).then((data) => {
				formData.value = Object.assign({}, data)
				return data
			})
			// 等待树和详情都加载完成后，确保父节点可回显
			Promise.all([treePromise, detailPromise]).then(([_, detail]) => {
				if (detail.parentId) {
					ensureOrgInTree(detail.parentId)
				}
			})
		}
	}
	// 懒加载子节点
	const onLoadData = (treeNode) => {
		return new Promise((resolve) => {
			if (treeNode.dataRef.children) {
				resolve()
				return
			}
			orgApi
				.orgOrgTreeLazySelector({
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
		orgTreeLazyApi: (param) => {
			return orgApi.orgOrgTreeLazySelector(param).then((data) => {
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
