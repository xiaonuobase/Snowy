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
			<a-form-item label="发送邮箱号：" name="SNOWY_EMAIL_LOCAL_FROM">
				<a-input v-model:value="formData.SNOWY_EMAIL_LOCAL_FROM" placeholder="请输入发送邮箱号" />
			</a-form-item>
			<a-form-item label="邮箱密钥：" name="SNOWY_EMAIL_LOCAL_PASSWORD">
				<a-input v-model:value="formData.SNOWY_EMAIL_LOCAL_PASSWORD" placeholder="请输入邮箱密钥" />
			</a-form-item>
			<a-form-item label="SMTP服务器域名：" name="SNOWY_EMAIL_LOCAL_SMTP_HOST">
				<a-input v-model:value="formData.SNOWY_EMAIL_LOCAL_SMTP_HOST" placeholder="请输入SMTP服务器域名" />
			</a-form-item>
			<a-form-item label="SMTP服务端口：" name="SNOWY_EMAIL_LOCAL_SMTP_PORT">
				<a-input-number
					v-model:value="formData.SNOWY_EMAIL_LOCAL_SMTP_PORT"
					placeholder="请输入SMTP服务端口"
					style="width: 100%"
				/>
			</a-form-item>
			<a-form-item label="是否需要用户名密码验证：" name="SNOWY_EMAIL_LOCAL_AUTH">
				<a-switch v-model:checked="formData.SNOWY_EMAIL_LOCAL_AUTH" placeholder="请选择是否需要用户名密码验证" />
			</a-form-item>
			<a-form-item label="是否使用SSL安全连接：" name="SNOWY_EMAIL_LOCAL_SSL_ENABLE">
				<a-switch v-model:checked="formData.SNOWY_EMAIL_LOCAL_SSL_ENABLE" placeholder="请选择是否使用SSL安全连接" />
			</a-form-item>
			<a-form-item label="是否使用STARTTLS安全连接：" name="SNOWY_EMAIL_LOCAL_STARTTLS_ENABLE">
				<a-switch
					v-model:checked="formData.SNOWY_EMAIL_LOCAL_STARTTLS_ENABLE"
					placeholder="请选择是否使用STARTTLS安全连接"
				/>
			</a-form-item>
			<a-form-item>
				<a-button type="primary" :loading="submitLoading" @click="onSubmit()">保存</a-button>
				<a-button class="xn-ml10" @click="() => formRef.resetFields()">重置</a-button>
			</a-form-item>
		</a-form>
	</a-spin>
</template>

<script setup name="localEmailForm">
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
		category: 'EMAIL_LOCAL'
	}
	configApi.configList(param).then((data) => {
		loadSpinning.value = false
		if (data) {
			data.forEach((item) => {
				formData.value[item.configKey] = transferBooleanInValue(item.configValue)
			})
		} else {
			message.warning('表单项不存在，请初始化数据库')
		}
	})
	// 转换值
	const transferBooleanInValue = (value) => {
		if (value === 'true' || value === 'false') {
			return value === 'true'
		} else {
			return value
		}
	}
	// 默认要校验的
	const formRules = {
		SNOWY_EMAIL_LOCAL_FROM: [required('请输入发送邮箱号')],
		SNOWY_EMAIL_LOCAL_PASSWORD: [required('请输入邮箱密钥')]
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
