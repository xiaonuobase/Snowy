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
			<a-form-item label="连续登录失败持续时间：" name="SNOWY_SYS_DEFAULT_CONTINUOUS_LOGIN_FAIL_DURATION_FOR_C">
				<a-input-number
					v-model:value="formData.SNOWY_SYS_DEFAULT_CONTINUOUS_LOGIN_FAIL_DURATION_FOR_C"
					placeholder="分钟"
					style="width: 50%"
				>
					<template #addonAfter> 分钟 </template>
				</a-input-number>
			</a-form-item>
			<a-form-item label="连续登录失败次数：" name="SNOWY_SYS_DEFAULT_CONTINUOUS_LOGIN_FAIL_TIMES_FOR_C">
				<a-input-number
					v-model:value="formData.SNOWY_SYS_DEFAULT_CONTINUOUS_LOGIN_FAIL_TIMES_FOR_C"
					placeholder="分钟"
					style="width: 50%"
				>
					<template #addonAfter> 次 </template>
				</a-input-number>
			</a-form-item>
			<a-form-item label="连续登录失败锁定时间：" name="SNOWY_SYS_DEFAULT_CONTINUOUS_LOGIN_FAIL_LOCK_DURATION_FOR_C">
				<a-input-number
					v-model:value="formData.SNOWY_SYS_DEFAULT_CONTINUOUS_LOGIN_FAIL_LOCK_DURATION_FOR_C"
					placeholder="分钟"
					style="width: 50%"
				>
					<template #addonAfter> 分钟 </template>
				</a-input-number>
			</a-form-item>
			<a-form-item label="是否允许手机号登录：" name="SNOWY_SYS_DEFAULT_ALLOW_PHONE_LOGIN_FLAG_FOR_C">
				<a-switch
					v-model:checked="formData.SNOWY_SYS_DEFAULT_ALLOW_PHONE_LOGIN_FLAG_FOR_C"
					checked-children="是"
					un-checked-children="否"
					placeholder="请选择是否允许手机号登录"
				/>
			</a-form-item>
			<a-form-item
				v-if="formData.SNOWY_SYS_DEFAULT_ALLOW_PHONE_LOGIN_FLAG_FOR_C"
				name="SNOWY_SYS_DEFAULT_STRATEGY_WHEN_NO_USER_WITH_PHONE_FOR_C"
			>
				<template #label>
					<a-tooltip>
						<template #title>是否能配置自动创建用户，取决于注册策略是否开启注册</template>
						<QuestionCircleOutlined /> 手机号无对应用户时策略：
					</a-tooltip>
				</template>
				<a-radio-group
					v-model:value="formData.SNOWY_SYS_DEFAULT_STRATEGY_WHEN_NO_USER_WITH_PHONE_FOR_C"
					:options="strategyWhenNoUserOptions"
					:disabled="loginNoUserPhoneDisabled"
					placeholder="请选择手机号无对应用户时策略"
				/>
			</a-form-item>
			<a-form-item label="是否允许邮箱登录：" name="SNOWY_SYS_DEFAULT_ALLOW_EMAIL_LOGIN_FLAG_FOR_C">
				<a-switch
					v-model:checked="formData.SNOWY_SYS_DEFAULT_ALLOW_EMAIL_LOGIN_FLAG_FOR_C"
					checked-children="是"
					un-checked-children="否"
					placeholder="请选择是否允许邮箱登录"
				/>
			</a-form-item>
			<a-form-item
				v-if="formData.SNOWY_SYS_DEFAULT_ALLOW_EMAIL_LOGIN_FLAG_FOR_C"
				name="SNOWY_SYS_DEFAULT_STRATEGY_WHEN_NO_USER_WITH_EMAIL_FOR_C"
			>
				<template #label>
					<a-tooltip>
						<template #title>是否能配置自动创建用户，取决于注册策略是否开启注册</template>
						<QuestionCircleOutlined /> 邮箱无对应用户时策略：
					</a-tooltip>
				</template>
				<a-radio-group
					v-model:value="formData.SNOWY_SYS_DEFAULT_STRATEGY_WHEN_NO_USER_WITH_EMAIL_FOR_C"
					:options="strategyWhenNoUserOptions"
					:disabled="loginNoUserEmailDisabled"
					placeholder="请选择邮箱无对应用户时策略"
				/>
			</a-form-item>
			<a-form-item>
				<a-button type="primary" :loading="submitLoading" @click="onSubmit()">保存</a-button>
				<a-button class="xn-ml10" @click="() => formRef.resetFields()">重置</a-button>
			</a-form-item>
		</a-form>
	</a-spin>
</template>

<script setup name="cForm">
	import { cloneDeep } from 'lodash-es'
	import { required } from '@/utils/formRules'
	import { message } from 'ant-design-vue'
	import configApi from '@/api/dev/configApi'
	import tool from '@/utils/tool'
	import { QuestionCircleOutlined } from '@ant-design/icons-vue'

	const formRef = ref()
	const formData = ref({})
	const submitLoading = ref(false)
	const loadSpinning = ref(true)
	const strategyWhenNoUserOptions = tool.dictList('NO_USER_STRATEGY')
	const loginNoUserPhoneDisabled = ref(false)
	const loginNoUserEmailDisabled = ref(false)
	// 查询此界面的配置项,并转为表单
	const param = {
		category: 'LOGIN_STRATEGY_FOR_C'
	}
	configApi.configList(param).then((data) => {
		loadSpinning.value = false
		if (data) {
			data.forEach((item) => {
				formData.value[item.configKey] = transferBooleanInValue(item.configValue)
			})
			registerConfig()
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
	// 判断注册策略变更登录配置
	const registerConfig = () => {
		// 查询注册策略是否开启
		const regParam = {
			category: 'REGISTER_STRATEGY_FOR_C'
		}
		configApi.configList(regParam).then((regData) => {
			const regFlagResult = regData.find((f) => {
				if (f.configKey === 'SNOWY_SYS_DEFAULT_ALLOW_REGISTER_FLAG_FOR_C') {
					return f
				}
			})
			if (!regFlagResult) {
				return
			}
			const regFlag = regFlagResult.configValue
			if (regFlag !== 'true') {
				// 禁用
				loginNoUserPhoneDisabled.value = true
				loginNoUserEmailDisabled.value = true
				// 并且将其设为-不允许登录
				formData.value.SNOWY_SYS_DEFAULT_STRATEGY_WHEN_NO_USER_WITH_PHONE_FOR_C = 'NOT_ALLOW_LOGIN'
				formData.value.SNOWY_SYS_DEFAULT_STRATEGY_WHEN_NO_USER_WITH_EMAIL_FOR_C = 'NOT_ALLOW_LOGIN'
			}
		})
	}
	// 默认要校验的
	const formRules = {
		SNOWY_SYS_DEFAULT_CONTINUOUS_LOGIN_FAIL_DURATION_FOR_C: [required('请输入连续登录失败持续时间')],
		SNOWY_SYS_DEFAULT_CONTINUOUS_LOGIN_FAIL_TIMES_FOR_C: [required('请输入连续登录失败次数')],
		SNOWY_SYS_DEFAULT_CONTINUOUS_LOGIN_FAIL_LOCK_DURATION_FOR_C: [required('请输入连续登录失败锁定时间')],
		SNOWY_SYS_DEFAULT_ALLOW_PHONE_LOGIN_FLAG_FOR_C: [required('请选择是否允许手机号登录')],
		SNOWY_SYS_DEFAULT_STRATEGY_WHEN_NO_USER_WITH_PHONE_FOR_C: [required('请选择手机号无对应用户时策略')],
		SNOWY_SYS_DEFAULT_ALLOW_EMAIL_LOGIN_FLAG_FOR_C: [required('请选择是否允许邮箱登录')],
		SNOWY_SYS_DEFAULT_STRATEGY_WHEN_NO_USER_WITH_EMAIL_FOR_C: [required('请选择邮箱无对应用户时策略')]
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
