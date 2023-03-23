<template>
	<a-form
		ref="formRef"
		:model="formData"
		:rules="formRules"
		v-bind="layout"
		:label-col="{ ...layout.labelCol, offset: 0 }"
		:wrapper-col="{ ...layout.wrapperCol, offset: 0 }"
	>
		<a-form-item label="账号：" name="account">
			<span>{{ formData.account }}</span>
		</a-form-item>
		<a-form-item label="姓名：" name="name">
			<a-input v-model:value="formData.name" placeholder="请输入姓名" allow-clear />
		</a-form-item>
		<a-form-item label="手机：" name="phone">
			<a-input v-model:value="formData.phone" placeholder="请输入手机" allow-clear />
		</a-form-item>
		<a-form-item label="昵称：" name="nickname">
			<a-input v-model:value="formData.nickname" placeholder="请输入昵称" allow-clear />
		</a-form-item>
		<a-form-item label="性别：" name="sex">
			<a-radio-group v-model:value="formData.gender" :options="genderOptions" />
		</a-form-item>
		<a-form-item label="生日：" name="birthday">
			<a-date-picker v-model:value="formData.birthday" value-format="YYYY-MM-DD" style="width: 100%" />
		</a-form-item>
		<a-form-item label="邮箱：" name="email">
			<a-input v-model:value="formData.email" placeholder="请输入邮箱" allow-clear />
		</a-form-item>

		<a-form-item :wrapper-col="{ ...layout.wrapperCol, offset: 4 }">
			<a-button type="primary" :loading="submitLoading" @click="onSubmit">保存基本信息</a-button>
		</a-form-item>
	</a-form>
</template>

<script setup name="AccountBasic">
	import { required } from '@/utils/formRules'
	import userCenterApi from '@/api/sys/userCenterApi'
	import tool from '@/utils/tool'
	import { globalStore } from '@/store'

	const store = globalStore()

	const formRef = ref()
	// 获取用户信息
	const userInfo = tool.data.get('USER_INFO')
	let formData = ref({})
	formData.value = userInfo
	const submitLoading = ref(false)
	// 默认要校验的
	const formRules = {
		name: [required('请输入姓名')],
		gender: [required('请选择性别')]
	}
	const genderOptions = tool.dictList('GENDER')
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value
			.validate()
			.then(() => {
				submitLoading.value = true
				userCenterApi.userUpdateUserInfo(formData.value).then(() => {
					submitLoading.value = false
					// 更新前端缓存
					store.setUserInfo(formData.value)
					tool.data.set('USER_INFO', formData.value)
				})
			})
			.catch(() => {
				submitLoading.value = false
			})
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

<style scoped></style>
