<template>
	<xn-form-container title="修改密码" :width="550" :visible="visible" :destroy-on-close="true" @close="onClose">
		<a-form ref="formRef" :model="formState" :rules="rules" layout="vertical">
			<a-form-item label="旧密码：" name="password" has-feedback>
				<a-input
					v-model:value="formState.password"
					placeholder="请输入原密码"
					type="password"
					allow-clear
					autocomplete="off"
				/>
			</a-form-item>
			<a-form-item label="新密码：" name="newPassword" has-feedback>
				<a-input
					v-model:value="formState.newPassword"
					placeholder="请输入新密码"
					type="password"
					allow-clear
					autocomplete="off"
				/>
			</a-form-item>
		</a-form>
		<template #footer>
			<a-button class="xn-mr8" @click="onClose">关闭</a-button>
			<a-button type="primary" :loading="submitLoading" @click="onSubmit">保存</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="updatePassword">
	import { required } from '@/utils/formRules'
	import userCenterApi from '@/api/sys/userCenterApi'

	// 定义emit事件
	const emit = defineEmits({ successful: null })
	// 默认是关闭状态
	const visible = ref(false)
	const formRef = ref()
	// 表单数据
	const formState = ref({})
	const submitLoading = ref(false)

	// 打开抽屉
	const onOpen = () => {
		visible.value = true
	}
	// 关闭抽屉
	const onClose = () => {
		visible.value = false
	}
	// 默认要校验的
	const rules = {
		password: [required('请输入原密码')],
		checkPassword: [required('请再次输入原密码')],
		newPassword: [required('请输入新密码')]
	}

	// 提交数据
	const onSubmit = async () => {
		formRef.value
			.validate()
			.then((values) => {
				submitLoading.value = true
				userCenterApi
					.userUpdatePassword(values)
					.then(() => {
						formRef.value.resetFields()
						visible.value = false
						emit('successful')
					})
					.finally(() => {
						submitLoading.value = false
					})
			})
			.catch(() => {})
	}
	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>
