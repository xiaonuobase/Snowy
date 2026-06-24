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
					<a-form-item label="是否允许CAS登录：" name="SNOWY_THIRD_CAS_ALLOW_LOGIN_FLAG">
						<a-switch
							v-model:checked="formData.SNOWY_THIRD_CAS_ALLOW_LOGIN_FLAG"
							checked-children="是"
							un-checked-children="否"
							placeholder="请选择是否允许CAS登录"
						/>
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item
						label="CAS认证服务登录地址："
						name="SNOWY_THIRD_CAS_SERVER_LOGIN_URL"
						v-if="formData.SNOWY_THIRD_CAS_ALLOW_LOGIN_FLAG"
					>
						<a-input
							v-model:value="formData.SNOWY_THIRD_CAS_SERVER_LOGIN_URL"
							placeholder="请输入CAS认证服务登录地址"
						/>
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item
						label="CAS认证服务验证地址："
						name="SNOWY_THIRD_CAS_SERVER_VALIDATE_URL"
						v-if="formData.SNOWY_THIRD_CAS_ALLOW_LOGIN_FLAG"
					>
						<a-input
							v-model:value="formData.SNOWY_THIRD_CAS_SERVER_VALIDATE_URL"
							placeholder="请输入CAS认证服务验证地址"
						/>
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item name="SNOWY_THIRD_CAS_SERVER_PROTOCOL_VERSION" v-if="formData.SNOWY_THIRD_CAS_ALLOW_LOGIN_FLAG">
						<template #label>
							<a-tooltip>
								<template #title> 请填写V1或V2或V3 </template>
								<question-circle-outlined />
							</a-tooltip>
							CAS认证服务协议版本：
						</template>
						<a-input
							v-model:value="formData.SNOWY_THIRD_CAS_SERVER_PROTOCOL_VERSION"
							placeholder="请输入CAS认证服务协议版本"
						/>
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item name="SNOWY_THIRD_CAS_SERVICE_URL" v-if="formData.SNOWY_THIRD_CAS_ALLOW_LOGIN_FLAG">
						<template #label>
							<a-tooltip>
								<template #title> 请填写{前端地址}/callback/CAS，如http://localhost:81/callback/CAS </template>
								<question-circle-outlined />
							</a-tooltip>
							Service地址：
						</template>
						<a-input v-model:value="formData.SNOWY_THIRD_CAS_SERVICE_URL" placeholder="请输入Service地址" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item name="SNOWY_THIRD_CAS_SOURCE_PROPERTY" v-if="formData.SNOWY_THIRD_CAS_ALLOW_LOGIN_FLAG">
						<template #label>
							<a-tooltip>
								<template #title> 请填写cas:authenticationFailure标签内的属性，如cas:user </template>
								<question-circle-outlined />
							</a-tooltip>
							关联源属性：
						</template>
						<a-input v-model:value="formData.SNOWY_THIRD_CAS_SOURCE_PROPERTY" placeholder="请输入关联源属性" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item name="SNOWY_THIRD_CAS_TARGET_PROPERTY" v-if="formData.SNOWY_THIRD_CAS_ALLOW_LOGIN_FLAG">
						<template #label>
							<a-tooltip>
								<template #title> 请填写对应本系统字段 </template>
								<question-circle-outlined />
							</a-tooltip>
							关联目标属性：
						</template>
						<a-input v-model:value="formData.SNOWY_THIRD_CAS_TARGET_PROPERTY" placeholder="请输入关联目标属性" />
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

<script setup name="casThirdForm">
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
		category: 'THIRD_CAS'
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
		SNOWY_THIRD_CAS_ALLOW_LOGIN_FLAG: [required('请选择是否允许CAS登录')],
		SNOWY_THIRD_CAS_SERVER_LOGIN_URL: [required('请输入CAS认证服务登录地址')],
		SNOWY_THIRD_CAS_SERVER_VALIDATE_URL: [required('请输入CAS认证服务验证地址')],
		SNOWY_THIRD_CAS_SERVER_PROTOCOL_VERSION: [required('请输入CAS认证服务协议版本')],
		SNOWY_THIRD_CAS_SERVICE_URL: [required('请输入Service地址')],
		SNOWY_THIRD_CAS_SOURCE_PROPERTY: [required('请输入CAS关联源属性')],
		SNOWY_THIRD_CAS_TARGET_PROPERTY: [required('请输入CAS关联目标属性')]
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
