<template>
	<a-spin :spinning="loadSpinning">
		<a-form
			ref="formRef"
			:model="formData"
			:rules="formRules"
			layout="vertical"
			:label-col="{ ...layout.labelCol, offset: 0 }"
			:wrapper-col="{ ...layout.wrapperCol, offset: 0 }"
		>
			<a-form-item label="GITEE客户端ID：" name="SNOWY_THIRD_GITEE_CLIENT_ID">
				<a-input v-model:value="formData.SNOWY_THIRD_GITEE_CLIENT_ID" placeholder="请输入GITEE客户端ID" />
			</a-form-item>
			<a-form-item label="GITEE客户端SECRET：" name="SNOWY_THIRD_GITEE_CLIENT_SECRET">
				<a-input v-model:value="formData.SNOWY_THIRD_GITEE_CLIENT_SECRET" placeholder="请输入GITEE客户端SECRET" />
			</a-form-item>
			<a-form-item label="重定向URL：" name="SNOWY_THIRD_GITEE_REDIRECT_URL">
				<a-input v-model:value="formData.SNOWY_THIRD_GITEE_REDIRECT_URL" placeholder="请输入重定向URL" />
			</a-form-item>
			<a-form-item>
				<a-button type="primary" :loading="submitLoading" @click="onSubmit()">保存</a-button>
				<a-button class="xn-ml10" @click="() => formRef.resetFields()">重置</a-button>
			</a-form-item>
		</a-form>
	</a-spin>
</template>

<script setup name="giteeThirdForm">
	import { cloneDeep } from 'lodash-es'
	import { required } from '@/utils/formRules'
	import { message } from 'ant-design-vue'
	import configApi from '@/api/dev/configApi'

	const formRef = ref()
	const formData = ref({})
	const submitLoading = ref(false)
	const loadSpinning = ref(true)

	// 查询此界面的配置项,并转为表单
	const param = {
		category: 'THIRD_GITEE'
	}
	configApi.configList(param).then((data) => {
		loadSpinning.value = false
		if (data) {
			data.forEach((item) => {
				formData.value[item.configKey] = item.configValue
			})
		} else {
			message.warning('表单项不存在，请初始化数据库')
		}
	})

	// 默认要校验的
	const formRules = {
		SNOWY_THIRD_GITEE_CLIENT_ID: [required('请输入GITEE客户端ID')],
		SNOWY_THIRD_GITEE_CLIENT_SECRET: [required('请输入GITEE客户端SECRET')],
		SNOWY_THIRD_GITEE_REDIRECT_URL: [required('请输入重定向URL')]
	}
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value
			.validate()
			.then(() => {
				submitLoading.value = true
				let submitParam = cloneDeep(formData.value)
				const param = Object.entries(submitParam).map((item) => {
					return {
						configKey: item[0],
						configValue: item[1]
					}
				})
				configApi
					.configEditForm(param)
					.then(() => {})
					.finally(() => {
						submitLoading.value = false
					})
			})
			.catch(() => {})
	}
	const layout = {
		labelCol: {
			span: 4
		},
		wrapperCol: {
			span: 12
		}
	}
</script>
