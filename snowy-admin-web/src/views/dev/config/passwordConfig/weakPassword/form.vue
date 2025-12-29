<template>
	<a-modal
		:title="formData.id ? '编辑弱密码' : '增加弱密码'"
		:width="500"
		v-model:open="open"
		:destroy-on-close="true"
		@cancel="onClose"
		@ok="onSubmit"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-form-item label="弱密码：" name="password">
				<a-input v-model:value="formData.password" placeholder="请输入弱密码" allow-clear />
			</a-form-item>
		</a-form>
	</a-modal>
</template>

<script setup name="devWeakPasswordForm">
	import { cloneDeep } from 'lodash-es'
	import { required } from '@/utils/formRules'
	import weakPasswordApi from '@/api/dev/weakPasswordApi'
	// 抽屉状态
	const open = ref(false)
	const emit = defineEmits({ successful: null })
	const formRef = ref()
	// 表单数据
	const formData = ref({})
	const submitLoading = ref(false)

	// 打开抽屉
	const onOpen = (record) => {
		open.value = true
		if (record) {
			let recordData = cloneDeep(record)
			formData.value = Object.assign({}, recordData)
		}
	}
	// 关闭抽屉
	const onClose = () => {
		formRef.value.resetFields()
		formData.value = {}
		open.value = false
	}
	// 默认要校验的
	const formRules = {
		password: [required('请输入弱密码')]
	}
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value.validate().then(() => {
			submitLoading.value = true
			const formDataParam = cloneDeep(formData.value)
			weakPasswordApi
				.weakPasswordSubmitForm(formDataParam, formDataParam.id)
				.then(() => {
					onClose()
					emit('successful')
				})
				.finally(() => {
					submitLoading.value = false
				})
		})
	}
	// 抛出函数
	defineExpose({
		onOpen
	})
</script>
