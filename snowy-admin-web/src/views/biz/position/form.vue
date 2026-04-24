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
				<xn-tree-select
					ref="orgTreeSelectRef"
					v-model:value="formData.orgId"
					:tree-api="bizPositionApi.positionOrgTreeSelector"
					:ancestor-api="bizPositionApi.positionGetAncestorNodes"
					placeholder="请选择组织"
				/>
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

<script setup name="bizPositionForm">
	import { required } from '@/utils/formRules'
	import bizPositionApi from '@/api/biz/bizPositionApi'
	import tool from '@/utils/tool'

	const emit = defineEmits({ successful: null })
	const visible = ref(false)
	const formRef = ref()
	const formData = ref({})
	const submitLoading = ref(false)
	const orgTreeSelectRef = ref()

	const onOpen = (record, orgId) => {
		visible.value = true
		formData.value = { sortCode: 99 }
		if (orgId) formData.value.orgId = orgId
		if (record) formData.value = Object.assign({}, record)
		nextTick(() => {
			const echoOrgId = (record && record.orgId) || orgId
			if (echoOrgId) {
				orgTreeSelectRef.value.echo([echoOrgId])
			} else {
				orgTreeSelectRef.value.init()
			}
		})
	}

	const onClose = () => {
		visible.value = false
	}

	const formRules = {
		orgId: [required('请选择所属组织')],
		name: [required('请输入岗位名称')],
		category: [required('请选择岗位分类')],
		sortCode: [required('请选择排序')]
	}

	const positionCategoryOptions = tool.dictList('POSITION_CATEGORY')

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

	defineExpose({ onOpen })
</script>
