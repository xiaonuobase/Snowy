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
					<a-form-item label="是否允许JWT登录：" name="SNOWY_THIRD_JWT_ALLOW_LOGIN_FLAG">
						<a-switch
							v-model:checked="formData.SNOWY_THIRD_JWT_ALLOW_LOGIN_FLAG"
							checked-children="是"
							un-checked-children="否"
							placeholder="请选择是否允许JWT登录"
						/>
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item
						label="JWT认证地址："
						name="SNOWY_THIRD_JWT_AUTHORIZE_URL"
						v-if="formData.SNOWY_THIRD_JWT_ALLOW_LOGIN_FLAG"
					>
						<a-input v-model:value="formData.SNOWY_THIRD_JWT_AUTHORIZE_URL" placeholder="请输入JWT认证地址" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item name="SNOWY_THIRD_JWT_PUBLIC_KEY" v-if="formData.SNOWY_THIRD_JWT_ALLOW_LOGIN_FLAG">
						<template #label>
							<a-tooltip>
								<template #title> 请填写公钥base64字符串或者jwk字符串或jwk地址 </template>
								<question-circle-outlined />
							</a-tooltip>
							JWT公钥：
						</template>
						<a-input v-model:value="formData.SNOWY_THIRD_JWT_PUBLIC_KEY" placeholder="请输入JWT公钥" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item name="SNOWY_THIRD_JWT_ALGORITHM" v-if="formData.SNOWY_THIRD_JWT_ALLOW_LOGIN_FLAG">
						<template #label>
							<a-tooltip>
								<template #title> 请填写RS256或HS256 </template>
								<question-circle-outlined />
							</a-tooltip>
							JWT签名算法：
						</template>
						<a-input v-model:value="formData.SNOWY_THIRD_JWT_ALGORITHM" placeholder="请输入JWT签名算法" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item
						label="JWT客户端ID："
						name="SNOWY_THIRD_JWT_CLIENT_ID"
						v-if="formData.SNOWY_THIRD_JWT_ALLOW_LOGIN_FLAG"
					>
						<a-input v-model:value="formData.SNOWY_THIRD_JWT_CLIENT_ID" placeholder="请输入JWT客户端ID" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item
						label="JWT客户端SECRET："
						name="SNOWY_THIRD_JWT_CLIENT_SECRET"
						v-if="formData.SNOWY_THIRD_JWT_ALLOW_LOGIN_FLAG"
					>
						<a-input v-model:value="formData.SNOWY_THIRD_JWT_CLIENT_SECRET" placeholder="请输入JWT客户端SECRET" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item name="SNOWY_THIRD_JWT_REDIRECT_URL" v-if="formData.SNOWY_THIRD_JWT_ALLOW_LOGIN_FLAG">
						<template #label>
							<a-tooltip>
								<template #title> 请填写{前端地址}/callback/JWT，如http://localhost:81/callback/JWT </template>
								<question-circle-outlined />
							</a-tooltip>
							重定向地址：
						</template>
						<a-input v-model:value="formData.SNOWY_THIRD_JWT_REDIRECT_URL" placeholder="请输入重定向URL" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item name="SNOWY_THIRD_JWT_SOURCE_PROPERTY" v-if="formData.SNOWY_THIRD_JWT_ALLOW_LOGIN_FLAG">
						<template #label>
							<a-tooltip>
								<template #title> 请填写源系统中用户信息唯一字段 </template>
								<question-circle-outlined />
							</a-tooltip>
							关联源属性：
						</template>
						<a-input v-model:value="formData.SNOWY_THIRD_JWT_SOURCE_PROPERTY" placeholder="请输入关联源属性" />
					</a-form-item>
				</a-col>
<!--				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item name="SNOWY_THIRD_JWT_TARGET_PROPERTY" v-if="formData.SNOWY_THIRD_JWT_ALLOW_LOGIN_FLAG">
						<template #label>
							<a-tooltip>
								<template #title> 请填写对应本系统字段 </template>
								<question-circle-outlined />
							</a-tooltip>
							关联目标属性：
						</template>
						<a-input v-model:value="formData.SNOWY_THIRD_JWT_TARGET_PROPERTY" placeholder="请输入关联目标属性" />
					</a-form-item>
				</a-col>-->
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

<script setup name="jwtThirdForm">
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
		category: 'THIRD_JWT'
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
		SNOWY_THIRD_JWT_ALLOW_LOGIN_FLAG: [required('请选择是否允许JWT登录')],
		SNOWY_THIRD_JWT_AUTHORIZE_URL: [required('请输入JWT认证地址')],
		SNOWY_THIRD_JWT_ACCESS_TOKEN_URL: [required('请输入JWT获取token地址')],
		SNOWY_THIRD_JWT_USER_INFO_URL: [required('请输入JWT获取用户信息地址')],
		SNOWY_THIRD_JWT_SCOPE: [required('请输入JWT权限范围')],
		SNOWY_THIRD_JWT_PUBLIC_KEY: [required('请输入JWT公钥')],
		SNOWY_THIRD_JWT_ALGORITHM: [required('请输入JWT签名算法')],
		SNOWY_THIRD_JWT_CLIENT_ID: [required('请输入JWT客户端ID')],
		SNOWY_THIRD_JWT_CLIENT_SECRET: [required('请输入JWT客户端SECRET')],
		SNOWY_THIRD_JWT_REDIRECT_URL: [required('请输入重定向URL')],
		SNOWY_THIRD_JWT_SOURCE_PROPERTY: [required('请输入JWT关联源属性')],
		SNOWY_THIRD_JWT_TARGET_PROPERTY: [required('请输入JWT关联目标属性')]
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
