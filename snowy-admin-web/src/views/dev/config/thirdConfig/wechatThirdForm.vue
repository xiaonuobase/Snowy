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
			<a-row :gutter="8">
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item label="是否允许微信登录：" name="SNOWY_THIRD_WECHAT_ALLOW_LOGIN_FLAG">
						<a-switch
							v-model:checked="formData.SNOWY_THIRD_WECHAT_ALLOW_LOGIN_FLAG"
							checked-children="是"
							un-checked-children="否"
							placeholder="请选择是否允许微信登录"
						/>
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item label="clientId：" name="SNOWY_THIRD_WECHAT_CLIENT_ID" v-if="formData.SNOWY_THIRD_WECHAT_ALLOW_LOGIN_FLAG">
						<a-input v-model:value="formData.SNOWY_THIRD_WECHAT_CLIENT_ID" placeholder="请输入clientId" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item label="clientSecret：" name="SNOWY_THIRD_WECHAT_CLIENT_SECRET" v-if="formData.SNOWY_THIRD_WECHAT_ALLOW_LOGIN_FLAG">
						<a-input v-model:value="formData.SNOWY_THIRD_WECHAT_CLIENT_SECRET" placeholder="请输入clientSecret" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item label="redirectUrl：" name="SNOWY_THIRD_WECHAT_REDIRECT_URL" v-if="formData.SNOWY_THIRD_WECHAT_ALLOW_LOGIN_FLAG">
						<a-input v-model:value="formData.SNOWY_THIRD_WECHAT_REDIRECT_URL" placeholder="请输入redirectUrl" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item>
						<a-space>
							<a-button type="primary" :loading="submitLoading" @click="onSubmit()">保存</a-button>
							<a-button @click="() => formRef.resetFields()">重置</a-button>
						</a-space>
					</a-form-item>
				</a-col>
			</a-row>
		</a-form>
	</a-spin>
</template>

<script setup name="wechatThirdForm">
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
		category: 'THIRD_WECHAT'
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
		SNOWY_THIRD_WECHAT_ALLOW_LOGIN_FLAG: [required('请选择是否允许微信登录')],
		SNOWY_THIRD_WECHAT_CLIENT_ID: [required('请输入clientId')],
		SNOWY_THIRD_WECHAT_CLIENT_SECRET: [required('请输入clientSecret')],
		SNOWY_THIRD_WECHAT_REDIRECT_URL: [required('请输入redirectUrl')]
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
			span: 24
		},
		wrapperCol: {
			span: 12
		}
	}
</script>
