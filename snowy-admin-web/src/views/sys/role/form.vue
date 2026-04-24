<template>
	<xn-form-container
		:title="formData.id ? '编辑角色' : '增加角色'"
		:width="550"
		:visible="visible"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-form-item label="角色名称：" name="name">
				<a-input v-model:value="formData.name" placeholder="请输入角色名称" allow-clear />
			</a-form-item>
			<a-form-item label="角色编码：" name="code">
				<a-input v-model:value="formData.code" placeholder="请输入角色编码" allow-clear />
			</a-form-item>
			<a-form-item label="角色分类：" name="category">
				<a-select
					v-model:value="formData.category"
					:options="categoryOptions"
					class="xn-wd"
					placeholder="请选择角色分类"
				/>
			</a-form-item>
			<a-form-item v-if="formData.category === 'ORG'" label="所属机构：" name="orgId">
				<xn-tree-select
					ref="orgTreeSelectRef"
					v-model:value="formData.orgId"
					:tree-api="roleApi.roleOrgTreeSelector"
					:ancestor-api="roleApi.roleGetAncestorNodes"
					placeholder="请选择组织"
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

<script setup name="roleForm">
	import { required } from '@/utils/formRules'
	import tool from '@/utils/tool'
	import roleApi from '@/api/sys/roleApi'

	const emit = defineEmits({ successful: null })
	const visible = ref(false)
	const formRef = ref()
	const formData = ref({})
	const submitLoading = ref(false)
	const orgTreeSelectRef = ref()

	const onOpen = (record, category, orgId) => {
		visible.value = true
		formData.value = {}
		if (category) formData.value.category = category
		if (orgId) {
			formData.value.category = 'ORG'
			formData.value.orgId = orgId
		}
		if (record) {
			formData.value = Object.assign({}, record)
		} else {
			formData.value.sortCode = 99
			formData.value.code = tool.generateString(10)
		}
		nextTick(() => {
			const echoOrgId = (record && record.orgId) || orgId
			if (echoOrgId) {
				// 等组件渲染后再调用 echo（v-if 可能延迟挂载）
				nextTick(() => {
					orgTreeSelectRef.value && orgTreeSelectRef.value.echo([echoOrgId])
				})
			} else if (orgTreeSelectRef.value) {
				orgTreeSelectRef.value.init()
			}
		})
	}

	const onClose = () => {
		visible.value = false
		formData.value = {}
	}

	const formRules = {
		orgId: [required('请选择所属组织')],
		name: [required('请输入角色名称')],
		code: [required('请输入角色编码')],
		category: [required('请选择角色分类')],
		sortCode: [required('请选择排序')]
	}

	let categoryOptions = tool.dictList('ROLE_CATEGORY')

	const onSubmit = () => {
		formRef.value
			.validate()
			.then(() => {
				submitLoading.value = true
				roleApi
					.submitForm(formData.value, formData.value.id)
					.then(() => {
						onClose()
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
