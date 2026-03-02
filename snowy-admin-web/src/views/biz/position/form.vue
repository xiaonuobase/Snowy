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
				<a-tree-select
					v-model:value="formData.orgId"
					class="xn-wd"
					:dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
					placeholder="请选择组织"
					allow-clear
					:tree-data="treeData"
					:field-names="treeFieldNames"
					tree-line
					:load-data="onLoadData"
				></a-tree-select>
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
	const treeFieldNames = { children: 'children', label: 'name', value: 'id' }
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
		if (!orgId || findNodeInTree(treeData.value, orgId)) return
		userCenterApi.userCenterGetOrgListByIdList({ idList: [orgId] }).then((data) => {
			if (data && data.length > 0) {
				treeData.value.push({ ...data[0], isLeaf: true })
				treeData.value = [...treeData.value]
			}
		})
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
		// 获取机构树
		bizPositionApi.positionOrgTreeLazySelector().then((res) => {
			treeData.value = res.map((item) => {
				return {
					...item,
					isLeaf: item.isLeaf === undefined ? false : item.isLeaf
				}
			})
			// 编辑时确保选中的机构可回显
			if (record && formData.value.orgId) {
				ensureOrgInTree(formData.value.orgId)
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
				.positionOrgTreeLazySelector({
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
