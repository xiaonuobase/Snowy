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
					<a-form-item label="是否允许SAML登录：" name="SNOWY_THIRD_SAML_ALLOW_LOGIN_FLAG">
						<a-switch
							v-model:checked="formData.SNOWY_THIRD_SAML_ALLOW_LOGIN_FLAG"
							checked-children="是"
							un-checked-children="否"
							placeholder="请选择是否允许SAML登录"
						/>
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item name="SNOWY_THIRD_SAML_SP_META_DATA" v-if="formData.SNOWY_THIRD_SAML_ALLOW_LOGIN_FLAG">
						<template #label>
							<a-tooltip>
								<template #title> AuthSamlClient生成SP元数据后填入 </template>
								<question-circle-outlined />
							</a-tooltip>
							SP元数据：
						</template>
						<a-input v-model:value="formData.SNOWY_THIRD_SAML_SP_META_DATA" placeholder="请输入SP元数据" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item ame="SNOWY_THIRD_SAML_CERTIFICATE" v-if="formData.SNOWY_THIRD_SAML_ALLOW_LOGIN_FLAG">
						<template #label>
							<a-tooltip>
								<template #title> AuthSamlClient生成自签证书后填入 </template>
								<question-circle-outlined />
							</a-tooltip>
							自签证书：
						</template>
						<a-input v-model:value="formData.SNOWY_THIRD_SAML_CERTIFICATE" placeholder="请输入自签证书" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item name="SNOWY_THIRD_SAML_PRIVATE_KEY" v-if="formData.SNOWY_THIRD_SAML_ALLOW_LOGIN_FLAG">
						<template #label>
							<a-tooltip>
								<template #title> AuthSamlClient生成签名私钥后填入 </template>
								<question-circle-outlined />
							</a-tooltip>
							签名私钥：
						</template>
						<a-input v-model:value="formData.SNOWY_THIRD_SAML_PRIVATE_KEY" placeholder="请输入签名私钥" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item name="SNOWY_THIRD_SAML_PUBLIC_KEY" v-if="formData.SNOWY_THIRD_SAML_ALLOW_LOGIN_FLAG">
						<template #label>
							<a-tooltip>
								<template #title> AuthSamlClient生成验证公钥后填入 </template>
								<question-circle-outlined />
							</a-tooltip>
							验证公钥：
						</template>
						<a-input v-model:value="formData.SNOWY_THIRD_SAML_PUBLIC_KEY" placeholder="请输入验证公钥" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item name="SNOWY_THIRD_SAML_SP_ENTITY_ID" v-if="formData.SNOWY_THIRD_SAML_ALLOW_LOGIN_FLAG">
						<template #label>
							<a-tooltip>
								<template #title> AuthSamlClient生成SP元数据后取spEntityId填入 </template>
								<question-circle-outlined />
							</a-tooltip>
							SP的entityID：
						</template>
						<a-input v-model:value="formData.SNOWY_THIRD_SAML_SP_ENTITY_ID" placeholder="请输入SP的entityID" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item name="SNOWY_THIRD_SAML_SP_ACL_URL" v-if="formData.SNOWY_THIRD_SAML_ALLOW_LOGIN_FLAG">
						<template #label>
							<a-tooltip>
								<template #title> AuthSamlClient生成SP元数据后取spAclUrl填入 </template>
								<question-circle-outlined />
							</a-tooltip>
							SP的AclUrl：
						</template>
						<a-input v-model:value="formData.SNOWY_THIRD_SAML_SP_ACL_URL" placeholder="请输入SP的AclUrl" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item name="SNOWY_THIRD_SAML_IDP_META_DATA" v-if="formData.SNOWY_THIRD_SAML_ALLOW_LOGIN_FLAG">
						<template #label>
							<a-tooltip>
								<template #title> 从IDP平台下载后填入 </template>
								<question-circle-outlined />
							</a-tooltip>
							IdP元数据：
						</template>
						<a-input v-model:value="formData.SNOWY_THIRD_SAML_IDP_META_DATA" placeholder="请输入IdP元数据" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item name="SNOWY_THIRD_SAML_BINDING_TYPE" v-if="formData.SNOWY_THIRD_SAML_ALLOW_LOGIN_FLAG">
						<template #label>
							<a-tooltip>
								<template #title> 请填写POST或Redirect </template>
								<question-circle-outlined />
							</a-tooltip>
							绑定类型：
						</template>
						<a-input v-model:value="formData.SNOWY_THIRD_SAML_BINDING_TYPE" placeholder="请输入绑定类型" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item name="SNOWY_THIRD_SAML_SOURCE_PROPERTY" v-if="formData.SNOWY_THIRD_SAML_ALLOW_LOGIN_FLAG">
						<template #label>
							<a-tooltip>
								<template #title> 请固定填写nameID </template>
								<question-circle-outlined />
							</a-tooltip>
							关联源属性：
						</template>
						<a-input v-model:value="formData.SNOWY_THIRD_SAML_SOURCE_PROPERTY" placeholder="请输入关联源属性" />
					</a-form-item>
				</a-col>
<!--				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item name="SNOWY_THIRD_SAML_TARGET_PROPERTY" v-if="formData.SNOWY_THIRD_SAML_ALLOW_LOGIN_FLAG">
						<template #label>
							<a-tooltip>
								<template #title> 请填写对应本系统字段 </template>
								<question-circle-outlined />
							</a-tooltip>
							关联目标属性：
						</template>
						<a-input v-model:value="formData.SNOWY_THIRD_SAML_TARGET_PROPERTY" placeholder="请输入关联目标属性" />
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

<script setup name="samlThirdForm">
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
		category: 'THIRD_SAML'
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
		SNOWY_THIRD_SAML_ALLOW_LOGIN_FLAG: [required('请选择是否允许SAML登录')],
		SNOWY_THIRD_SAML_SP_META_DATA: [required('请输入SP元数据')],
		SNOWY_THIRD_SAML_CERTIFICATE: [required('请输入自签证书')],
		SNOWY_THIRD_SAML_PRIVATE_KEY: [required('请输入签名私钥')],
		SNOWY_THIRD_SAML_PUBLIC_KEY: [required('请输入验证公钥')],
		SNOWY_THIRD_SAML_SP_ENTITY_ID: [required('请输入SP的entityID')],
		SNOWY_THIRD_SAML_SP_ACL_URL: [required('请输入SP的AclUrl')],
		SNOWY_THIRD_SAML_IDP_META_DATA: [required('请输入IdP元数据')],
		SNOWY_THIRD_SAML_BINDING_TYPE: [required('请输入绑定类型')],
		SNOWY_THIRD_SAML_SOURCE_PROPERTY: [required('请输入SAML关联源属性')],
		SNOWY_THIRD_SAML_TARGET_PROPERTY: [required('请输入SAML关联目标属性')]
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
