<template>
	<xn-form-container
		:title="formData.id ? '编辑配置' : '增加配置'"
		:width="550"
		:visible="visible"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-form-item label="配置键：" name="configKey">
				<a-input v-model:value="formData.configKey" placeholder="请输入配置键" allow-clear />
			</a-form-item>
			<a-form-item label="配置值：" name="configValue">
				<a-textarea
					v-model:value="formData.configValue"
					placeholder="请输入配置值"
					:auto-size="{ minRows: 2, maxRows: 5 }"
				/>
			</a-form-item>
			<a-form-item label="备注：" name="remark">
				<a-input v-model:value="formData.remark" placeholder="请输入备注" allow-clear />
			</a-form-item>
			<a-form-item label="排序:" name="sortCode">
				<a-input-number class="xn-wd" v-model:value="formData.sortCode" :max="1000" />
			</a-form-item>
		</a-form>
		<template #footer>
			<a-button class="xn-mr8" @click="onClose">关闭</a-button>
			<a-button type="primary" @click="onSubmit" :loading="submitLoading">保存</a-button>
		</template>
	</xn-form-container>
</template>

<script setup>
	import { required } from '@/utils/formRules'
	import configApi from '@/api/dev/configApi'
	// 默认是关闭状态
	const visible = ref(false)
	const emit = defineEmits({ successful: null })
	const formRef = ref()
	// 表单数据
	const formData = ref({})
	const submitLoading = ref()

	// 打开抽屉
	const onOpen = (record) => {
		visible.value = true
		formData.value = {
			sortCode: 99
		}
		if (record) {
			formData.value = Object.assign({}, record)
		}
	}
	// 关闭抽屉
	const onClose = () => {
		formRef.value.resetFields()
		visible.value = false
	}
	// 默认要校验的
	const formRules = {
		configKey: [required('请输入配置键')],
		configValue: [required('请输入配置值')],
		sortCode: [required('请滑动排序')]
	}

	// 验证并提交数据
	const onSubmit = () => {
		formRef.value
			.validate()
			.then(() => {
				submitLoading.value = true
				configApi
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

	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>
