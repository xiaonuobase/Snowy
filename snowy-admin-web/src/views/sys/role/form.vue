<template>
	<a-drawer
		:title="formData.id ? '编辑角色' : '增加角色'"
		:width="500"
		:visible="visible"
		:destroy-on-close="true"
		:body-style="{ paddingBottom: '80px' }"
		:footer-style="{ textAlign: 'right' }"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-form-item label="角色名称：" name="name">
				<a-input v-model:value="formData.name" placeholder="请输入角色名称" allow-clear />
			</a-form-item>
			<a-form-item label="角色分类：" name="category">
				<a-select
					v-model:value="formData.category"
					:options="categoryOptions"
					style="width: 100%"
					placeholder="请选择角色分类"
				>
				</a-select>
			</a-form-item>
			<a-form-item v-if="formData.category === 'ORG'" label="所属机构：" name="orgId">
				<a-tree-select
					v-model:value="formData.orgId"
					style="width: 100%"
					:dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
					placeholder="请选择组织"
					allow-clear
					tree-default-expand-all
					:tree-data="treeData"
					:field-names="{
						children: 'children',
						label: 'name',
						value: 'id'
					}"
					selectable="false"
					tree-line
				></a-tree-select>
			</a-form-item>
			<a-form-item label="排序：" name="sortCode">
				<a-slider v-model:value="formData.sortCode" :max="100" />
			</a-form-item>
		</a-form>
		<template #footer>
			<a-button style="margin-right: 8px" @click="onClose">关闭</a-button>
			<a-button type="primary" :loading="submitLoading" @click="onSubmit">保存</a-button>
		</template>
	</a-drawer>
</template>

<script setup name="roleForm">
	import { required } from '@/utils/formRules'
	import { getCurrentInstance } from 'vue'
	import orgApi from '@/api/sys/orgApi'
	import roleApi from '@/api/sys/roleApi'

	// 定义emit事件
	const emit = defineEmits({ successful: null })
	const { proxy } = getCurrentInstance()
	// 默认是关闭状态
	let visible = $ref(false)
	const formRef = ref()
	// 表单数据，也就是默认给一些数据
	const formData = ref({})
	// 定义机构元素
	const treeData = ref([])
	const submitLoading = ref(false)

	// 打开抽屉
	const onOpen = (record) => {
		visible = true
		formData.value = {
			sortCode: 99
		}
		if (record) {
			formData.value = Object.assign({}, record)
		}
		// 获取机构树并加入顶级
		orgApi.orgTree().then((res) => {
			treeData.value = res
		})
	}
	// 关闭抽屉
	const onClose = () => {
		visible = false
	}
	// 默认要校验的
	const formRules = {
		orgId: [required('请选择所属组织')],
		name: [required('请输入角色名称')],
		category: [required('请选择角色分类')],
		sortCode: [required('请选择排序')]
	}
	// 机构分类字典
	let categoryOptions = proxy.$TOOL.dictTypeList('ROLE_CATEGORY').map((item) => {
		return {
			value: item['dictValue'],
			label: item['name']
		}
	})
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value
			.validate()
			.then(() => {
				submitLoading.value = true
				roleApi
					.submitForm(formData.value, !formData.value.id)
					.then(() => {
						visible = false
						emit('successful')
					})
					.finally(() => {
						submitLoading.value = false
					})
			})
	}
	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>
