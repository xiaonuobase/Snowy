<template>
	<xn-form-container title="批量复制组织" :width="550" :visible="visible" :destroy-on-close="true" @close="onClose">
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-form-item label="目标上级组织：" name="targetParentId">
				<xn-tree-select
					ref="orgTreeSelectRef"
					v-model:value="formData.targetParentId"
					:tree-api="orgApi.orgTreeSelector"
					:top-node="{ id: '0', parentId: '-1', name: '顶级' }"
					placeholder="请选择目标上级组织"
				/>
				<a-alert
					class="mt-3"
					message="温馨提示：批量复制将自动跳过同名组织；复制内容包含排序号、组织名称、分类；部门主管需重新指定。"
					type="warning"
					show-icon
				/>
			</a-form-item>
		</a-form>
		<template #footer>
			<a-button class="xn-mr8" @click="onClose">关闭</a-button>
			<a-button type="primary" :loading="submitLoading" @click="onSubmit">保存</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="orgCopyForm">
	import { required } from '@/utils/formRules'
	import orgApi from '@/api/sys/orgApi'
	import { message } from 'ant-design-vue'

	const emit = defineEmits({ successful: null })
	const visible = ref(false)
	const formRef = ref()
	const formData = ref({})
	const submitLoading = ref(false)
	const orgTreeSelectRef = ref()
	const ids = ref([])

	const onOpen = (idParam) => {
		visible.value = true
		formData.value = {}
		if (idParam) {
			ids.value = idParam.map((item) => item.id)
		}
		nextTick(() => {
			orgTreeSelectRef.value.init()
		})
	}

	const onClose = () => {
		visible.value = false
	}

	const formRules = {
		targetParentId: [required('请选择目标上级组织')]
	}

	const onSubmit = () => {
		formRef.value
			.validate()
			.then(() => {
				submitLoading.value = true
				orgApi
					.orgCopy({ ids: ids.value, targetParentId: formData.value.targetParentId })
					.then(() => {
						visible.value = false
						message.success('复制成功')
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
