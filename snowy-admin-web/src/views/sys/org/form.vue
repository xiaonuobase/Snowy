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
				<xn-tree-select
					ref="orgTreeSelectRef"
					v-model:value="formData.parentId"
					:tree-api="orgApi.orgTreeSelector"
					:ancestor-api="orgApi.orgGetAncestorNodes"
					:top-node="{ id: '0', parentId: '-1', name: '顶级' }"
					placeholder="请选择上级组织"
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

	const emit = defineEmits({ successful: null })
	const visible = ref(false)
	const formRef = ref()
	const formData = ref({})
	const submitLoading = ref(false)
	const orgTreeSelectRef = ref()

	const onOpen = (record, parentId) => {
		visible.value = true
		formData.value = { sortCode: 99 }
		if (parentId) {
			formData.value.parentId = parentId
		}
		nextTick(() => {
			if (record) {
				orgApi.orgDetail({ id: record.id }).then((data) => {
					formData.value = Object.assign({}, data)
					const pid = data.parentId
					if (pid && pid !== '0') {
						orgTreeSelectRef.value.echo([pid])
					} else {
						orgTreeSelectRef.value.init()
					}
				})
			} else if (parentId && parentId !== '0') {
				orgTreeSelectRef.value.echo([parentId])
			} else {
				orgTreeSelectRef.value.init()
			}
		})
	}

	const onClose = () => {
		visible.value = false
	}

	const formRules = {
		parentId: [required('请选择上级组织')],
		name: [required('请输入组织名称')],
		category: [required('请选择组织分类')],
		sortCode: [required('请选择排序')]
	}

	const orgCategoryOptions = tool.dictList('ORG_CATEGORY')

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

	const selectorApiFunction = {
		orgTreeApi: (param) => orgApi.orgTreeSelector(param),
		userPageApi: (param) => orgApi.orgUserSelector(param)
	}

	defineExpose({ onOpen })
</script>
