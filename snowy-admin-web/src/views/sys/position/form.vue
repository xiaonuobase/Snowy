<template>
	<xn-form-container
		:title="formData.id ? '编辑职位' : '增加职位'"
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
			<a-form-item label="职位名称：" name="name">
				<a-input v-model:value="formData.name" placeholder="请输入职位名称" allow-clear />
			</a-form-item>
			<a-form-item label="职位分类：" name="category">
				<a-select
					v-model:value="formData.category"
					:options="positionCategoryOptions"
					class="xn-wd"
					placeholder="请选择职位分类"
				/>
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

<script setup name="positionForm">
	import { required } from '@/utils/formRules'
	import positionApi from '@/api/sys/positionApi'
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
		positionApi.positionOrgTreeSelector().then((res) => {
			treeData.value = res
		})
	}
	// 关闭抽屉
	const onClose = () => {
		visible.value = false
	}
	// 默认要校验的
	const formRules = {
		orgId: [required('请选择所属组织')],
		name: [required('请输入职位名称')],
		category: [required('请选择职位分类')],
		sortCode: [required('请选择排序')]
	}
	const positionCategoryOptions = tool.dictList('POSITION_CATEGORY')
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value
			.validate()
			.then(() => {
				submitLoading.value = true
				positionApi.submitForm(formData.value, formData.value.id).then(() => {
					visible.value = false
					submitLoading.value = false
					emit('successful')
				})
			})
			.catch(() => {})
	}
	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>
