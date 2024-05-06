<template>
	<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
		<a-form-item label="手机号：" name="phoneNumbers">
			<a-textarea
				v-model:value="formData.phoneNumbers"
				placeholder="请输入手机号，多个逗号拼接"
				:auto-size="{ minRows: 2, maxRows: 5 }"
			/>
		</a-form-item>
		<a-form-item label="短信内容：" name="message">
			<a-textarea
				v-model:value="formData.message"
				placeholder="请输入短信内容"
				:auto-size="{ minRows: 3, maxRows: 5 }"
			/>
		</a-form-item>
		<a-form-item label="短信签名：" name="signName">
			<a-input v-model:value="formData.signName" placeholder="请输入审核通过的短信签名" allow-clear />
		</a-form-item>
	</a-form>
</template>

<script setup name="XiaonuoSmsSend">
	import { message } from 'ant-design-vue'
	import { required } from '@/utils/formRules'
	import smsApi from '@/api/dev/smsApi'
	// 定义emit事件
	const emit = defineEmits({ loadingStart: null, loadingEnd: null })
	const formRef = ref()
	// 表单数据，也就是默认给一些数据
	const formData = ref({})

	// 默认要校验的
	const formRules = {
		phoneNumbers: [required('请输入手机号，多个逗号拼接')]
	}
	// 发送短信
	const send = () => {
		formRef.value
			.validate()
			.then(() => {
				emit('loadingStart')
				smsApi
					.smsSendXiaonuo(formData.value)
					.then(() => {
						message.success('发送成功')
					})
					.catch(() => {})
					.finally(() => {
						emit('loadingEnd')
					})
			})
			.catch(() => {})
	}
	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		send
	})
</script>
