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
			<a-form-item label="阿里云密钥ID：" name="SNOWY_EMAIL_ALIYUN_ACCESS_KEY_ID">
				<a-input v-model:value="formData.SNOWY_EMAIL_ALIYUN_ACCESS_KEY_ID" placeholder="请输入阿里云密钥ID" />
			</a-form-item>
			<a-form-item label="阿里云密钥SECRET：" name="SNOWY_EMAIL_ALIYUN_ACCESS_KEY_SECRET">
				<a-input v-model:value="formData.SNOWY_EMAIL_ALIYUN_ACCESS_KEY_SECRET" placeholder="请输入阿里云密钥SECRET" />
			</a-form-item>
			<a-form-item label="阿里云区域ID：" name="SNOWY_EMAIL_ALIYUN_REGION_ID">
				<a-input v-model:value="formData.SNOWY_EMAIL_ALIYUN_REGION_ID" placeholder="请输入阿里云区域ID" />
			</a-form-item>
			<a-form-item>
				<a-button type="primary" :loading="submitLoading" @click="onSubmit()">保存</a-button>
				<a-button class="xn-ml10" @click="() => formRef.resetFields()">重置</a-button>
			</a-form-item>
		</a-form>
	</a-spin>
</template>

<script setup name="aliyunEmailForm">
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
		category: 'EMAIL_ALIYUN'
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
		SNOWY_EMAIL_ALIYUN_ACCESS_KEY_ID: [required('请输入阿里云密钥ID')],
		SNOWY_EMAIL_ALIYUN_ACCESS_KEY_SECRET: [required('请输入阿里云密钥SECRET')],
		SNOWY_EMAIL_ALIYUN_REGION_ID: [required('请输入阿里云区域ID')]
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
