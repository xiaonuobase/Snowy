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
			<a-form-item label="是否允许注册：" name="SNOWY_SYS_DEFAULT_ALLOW_REGISTER_FLAG_FOR_C">
				<a-switch
					v-model:checked="formData.SNOWY_SYS_DEFAULT_ALLOW_REGISTER_FLAG_FOR_C"
					checked-children="是"
					un-checked-children="否"
					placeholder="请选择是否允许注册"
				/>
			</a-form-item>
			<a-form-item
				v-if="formData.SNOWY_SYS_DEFAULT_ALLOW_REGISTER_FLAG_FOR_C"
				label="注册后是否需要绑定手机号："
				name="SNOWY_SYS_DEFAULT_REGISTER_NEED_BIND_PHONE_FOR_C"
			>
				<a-switch
					v-model:checked="formData.SNOWY_SYS_DEFAULT_REGISTER_NEED_BIND_PHONE_FOR_C"
					checked-children="是"
					un-checked-children="否"
					placeholder="请选择注册后是否需要绑定手机号"
				/>
			</a-form-item>
			<a-form-item
				v-if="formData.SNOWY_SYS_DEFAULT_ALLOW_REGISTER_FLAG_FOR_C"
				label="注册后是否需要绑定邮箱："
				name="SNOWY_SYS_DEFAULT_REGISTER_NEED_BIND_EMAIL_FOR_C"
			>
				<a-switch
					v-model:checked="formData.SNOWY_SYS_DEFAULT_REGISTER_NEED_BIND_EMAIL_FOR_C"
					checked-children="是"
					un-checked-children="否"
					placeholder="请选择注册后是否需要绑定邮箱"
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

	const formRef = ref()
	const formData = ref({})
	const submitLoading = ref(false)
	const loadSpinning = ref(true)
	// 查询此界面的配置项,并转为表单
	const param = {
		category: 'REGISTER_STRATEGY_FOR_C'
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
		SNOWY_SYS_DEFAULT_ALLOW_REGISTER_FLAG_FOR_C: [required('请选择是否需要注册')],
		SNOWY_SYS_DEFAULT_REGISTER_NEED_BIND_PHONE_FOR_C: [required('请选择注册时是否需要绑定手机号')],
		SNOWY_SYS_DEFAULT_REGISTER_NEED_BIND_EMAIL_FOR_C: [required('请选择注册时是否需要绑定邮箱')]
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
