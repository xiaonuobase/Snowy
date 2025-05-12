<template>
	<a-spin :spinning="loadSpinning">
		<a-form
			ref="formRef"
			:model="formData"
			:rules="formRules"
			layout="vertical"
			:label-col="{ ...layout.labelCol, offset: 0 }"
			:wrapper-col="{ ...layout.wrapperCol, offset: 0 }"
			style="width: 60%"
		>
			<a-row :gutter="8">
				<a-col :span="12">
					<a-form-item label="默认用户密码：" name="SNOWY_SYS_DEFAULT_PASSWORD_FOR_B">
						<a-input
							v-model:value="formData.SNOWY_SYS_DEFAULT_PASSWORD_FOR_B"
							placeholder="请输入默认用户密码"
							style="width: 100%"
						/>
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="密码修改验证方式：" name="SNOWY_SYS_DEFAULT_PASSWORD_UPDATE_VALID_TYPE_FOR_B">
						<a-radio-group
							v-model:value="formData.SNOWY_SYS_DEFAULT_PASSWORD_UPDATE_VALID_TYPE_FOR_B"
							:options="updatePasswordValidTypeOptions"
							placeholder="请选择密码修改验证方式"
						/>
					</a-form-item>
				</a-col>
			</a-row>

			<a-row :gutter="8">
				<a-col :span="12">
					<a-form-item label="密码最小长度：" name="SNOWY_SYS_DEFAULT_PASSWORD_MIN_LENGTH_FOR_B">
						<a-input-number
							v-model:value="formData.SNOWY_SYS_DEFAULT_PASSWORD_MIN_LENGTH_FOR_B"
							placeholder="请输入密码最小长度"
							style="width: 100%"
						>
							<template #addonAfter> 位 </template>
						</a-input-number>
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="密码最大长度：" name="SNOWY_SYS_DEFAULT_PASSWORD_MAX_LENGTH_FOR_B">
						<a-input-number
							v-model:value="formData.SNOWY_SYS_DEFAULT_PASSWORD_MAX_LENGTH_FOR_B"
							placeholder="请输入密码最大长度"
							style="width: 100%"
						>
							<template #addonAfter> 位 </template>
						</a-input-number>
					</a-form-item>
				</a-col>
			</a-row>

			<a-row :gutter="8">
				<a-col :span="12">
					<a-form-item label="密码复杂度：" name="SNOWY_SYS_DEFAULT_PASSWORD_COMPLEXITY_FOR_B">
						<a-radio-group
							v-model:value="formData.SNOWY_SYS_DEFAULT_PASSWORD_COMPLEXITY_FOR_B"
							:options="passwordComplexityTypeOptions"
							direction="vertical"
							class="vertical-radio-group"
							placeholder="请选择密码复杂度"
						/>
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item
						label="密码不能连续存在相同字符个数："
						name="SNOWY_SYS_DEFAULT_PASSWORD_NOT_ALLOW_CONTINUOUS_SAME_CHARACTER_LENGTH_FOR_B"
					>
						<a-input-number
							v-model:value="formData.SNOWY_SYS_DEFAULT_PASSWORD_NOT_ALLOW_CONTINUOUS_SAME_CHARACTER_LENGTH_FOR_B"
							placeholder="请输入密码不能连续存在相同字符个数"
							style="width: 100%"
						>
							<template #addonAfter> 个 </template>
						</a-input-number>
					</a-form-item>
					<a-form-item
						label="密码不能包含用户信息："
						name="SNOWY_SYS_DEFAULT_PASSWORD_NOT_ALLOW_CONTAINS_USER_INFO_FLAG_FOR_B"
					>
						<a-switch
							v-model:checked="formData.SNOWY_SYS_DEFAULT_PASSWORD_NOT_ALLOW_CONTAINS_USER_INFO_FLAG_FOR_B"
							checked-children="不能"
							un-checked-children="包含"
							placeholder="请选择密码不能包含用户信息"
						/>
					</a-form-item>
					<a-form-item
						label="密码不能使用历史密码："
						name="SNOWY_SYS_DEFAULT_PASSWORD_NOT_ALLOW_USE_HISTORY_FLAG_FOR_B"
					>
						<a-switch
							v-model:checked="formData.SNOWY_SYS_DEFAULT_PASSWORD_NOT_ALLOW_USE_HISTORY_FLAG_FOR_B"
							checked-children="不能"
							un-checked-children="允许"
							placeholder="请选择密码不能使用历史密码"
						/>
					</a-form-item>
				</a-col>
			</a-row>
			<a-row :gutter="8">
				<a-col :span="12">
					<a-form-item
						label="密码不能使用历史密码范围个数："
						name="SNOWY_SYS_DEFAULT_PASSWORD_NOT_ALLOW_USE_HISTORY_COUNT_FOR_B"
					>
						<a-input-number
							v-model:value="formData.SNOWY_SYS_DEFAULT_PASSWORD_NOT_ALLOW_USE_HISTORY_COUNT_FOR_B"
							placeholder="请输入密码不能使用历史密码范围个数"
							style="width: 100%"
						>
							<template #addonAfter> 个 </template>
						</a-input-number>
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item
						label="密码不能使用弱密码库中密码："
						name="SNOWY_SYS_DEFAULT_PASSWORD_NOT_ALLOW_USE_WEAK_FLAG_FOR_B"
					>
						<a-switch
							v-model:checked="formData.SNOWY_SYS_DEFAULT_PASSWORD_NOT_ALLOW_USE_WEAK_FLAG_FOR_B"
							checked-children="不能"
							un-checked-children="允许"
							placeholder="请选择密码不能使用弱密码库中密码"
						/>
					</a-form-item>
				</a-col>
			</a-row>
			<a-row :gutter="8">
				<a-col :span="12">
					<a-form-item label="密码有效期天数：" name="SNOWY_SYS_DEFAULT_PASSWORD_EXPIRED_DAYS_FOR_B">
						<a-input-number
							v-model:value="formData.SNOWY_SYS_DEFAULT_PASSWORD_EXPIRED_DAYS_FOR_B"
							placeholder="天"
							style="width: 100%"
						>
							<template #addonAfter> 天 </template>
						</a-input-number>
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="密码过期提前提醒天数：" name="SNOWY_SYS_DEFAULT_PASSWORD_EXPIRED_NOTICE_DAYS_FOR_B">
						<a-input-number
							v-model:value="formData.SNOWY_SYS_DEFAULT_PASSWORD_EXPIRED_NOTICE_DAYS_FOR_B"
							placeholder="天"
							style="width: 100%"
						>
							<template #addonAfter> 天 </template>
						</a-input-number>
					</a-form-item>
				</a-col>
			</a-row>
			<a-col :span="12">
				<a-form-item label="自定义额外弱密码库：" name="SNOWY_SYS_DEFAULT_PASSWORD_DEFINE_WEAK_DATABASE_FOR_B">
					<a-input
						v-model:value="formData.SNOWY_SYS_DEFAULT_PASSWORD_DEFINE_WEAK_DATABASE_FOR_B"
						placeholder="请输入自定义额外弱密码库"
						style="width: 100%"
					/>
				</a-form-item>
			</a-col>

			<a-form-item>
				<a-button type="primary" :loading="submitLoading" @click="onSubmit()">保存</a-button>
				<a-button class="xn-ml10" @click="() => formRef.resetFields()">重置</a-button>
			</a-form-item>
		</a-form>
	</a-spin>
</template>

<script setup name="bForm">
	import { cloneDeep } from 'lodash-es'
	import { required } from '@/utils/formRules'
	import { message } from 'ant-design-vue'
	import configApi from '@/api/dev/configApi'
	import tool from '@/utils/tool'

	const formRef = ref()
	const formData = ref({})
	const submitLoading = ref(false)
	const loadSpinning = ref(true)
	const updatePasswordValidTypeOptions = tool.dictList('UPDATE_PASSWORD_VALID_TYPE')
	const passwordComplexityTypeOptions = tool.dictList('PASSWORD_COMPLEXITY_TYPE')
	// 查询此界面的配置项,并转为表单
	const param = {
		category: 'PASSWORD_STRATEGY_FOR_B'
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
		SNOWY_SYS_DEFAULT_PASSWORD_FOR_B: [required('请输入默认用户密码')],
		SNOWY_SYS_DEFAULT_PASSWORD_UPDATE_VALID_TYPE_FOR_B: [required('请选择密码修改验证方式')],
		SNOWY_SYS_DEFAULT_PASSWORD_MIN_LENGTH_FOR_B: [required('请输入密码最小长度')],
		SNOWY_SYS_DEFAULT_PASSWORD_MAX_LENGTH_FOR_B: [required('请输入密码最大长度')],
		SNOWY_SYS_DEFAULT_PASSWORD_COMPLEXITY_FOR_B: [required('请选择密码复杂度')],
		SNOWY_SYS_DEFAULT_PASSWORD_NOT_ALLOW_CONTINUOUS_SAME_CHARACTER_LENGTH_FOR_B: [
			required('请输入密码不能连续存在相同字符个数')
		],
		SNOWY_SYS_DEFAULT_PASSWORD_NOT_ALLOW_CONTAINS_USER_INFO_FLAG_FOR_B: [required('请选择密码不能包含用户信息')],
		SNOWY_SYS_DEFAULT_PASSWORD_NOT_ALLOW_USE_HISTORY_FLAG_FOR_B: [required('请选择密码不能使用历史密码')],
		SNOWY_SYS_DEFAULT_PASSWORD_NOT_ALLOW_USE_HISTORY_COUNT_FOR_B: [required('密码不能使用历史密码范围个数')],
		SNOWY_SYS_DEFAULT_PASSWORD_NOT_ALLOW_USE_WEAK_FLAG_FOR_B: [required('请选择密码不能使用弱密码库中密码')],
		SNOWY_SYS_DEFAULT_PASSWORD_DEFINE_WEAK_DATABASE_FOR_B: [required('请输入自定义额外弱密码库')],
		SNOWY_SYS_DEFAULT_PASSWORD_EXPIRED_DAYS_FOR_B: [required('请输入密码有效期天数')],
		SNOWY_SYS_DEFAULT_PASSWORD_EXPIRED_NOTICE_DAYS_FOR_B: [required('请输入密码过期天数')]
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
			span: 16
		},
		wrapperCol: {
			span: 22
		}
	}
</script>

<style scoped>
	.vertical-radio-group :deep(.ant-radio-wrapper) {
		display: flex;
		align-items: center;
		margin-bottom: 8px;
	}
</style>
