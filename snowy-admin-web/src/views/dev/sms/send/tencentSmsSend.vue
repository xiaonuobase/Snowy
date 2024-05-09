<template>
	<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
		<a-form-item label="手机号：" name="phoneNumbers">
			<a-textarea
				v-model:value="formData.phoneNumbers"
				placeholder="请输入手机号，多个逗号拼接"
				:auto-size="{ minRows: 2, maxRows: 5 }"
			/>
		</a-form-item>
		<a-form-item label="模板编码：" name="templateCode">
			<a-input
				v-model:value="formData.templateCode"
				placeholder="请输入短信服务控制台配置且审核通过的模板编码"
				allow-clear
			/>
		</a-form-item>
		<a-form-item label="发送参数：" name="templateParam">
			<a-textarea
				v-model:value="formData.templateParam"
				placeholder="请输入短信模板变量对应的顺序。支持传入多个参数，逗号拼接"
				:auto-size="{ minRows: 3, maxRows: 5 }"
			/>
		</a-form-item>
		<a-form-item label="sdkAppId：" name="sdkAppId">
			<a-input
				v-model:value="formData.sdkAppId"
				placeholder="请输入在短信控制台添加应用后生成的实际SdkAppId"
				allow-clear
			/>
		</a-form-item>
		<a-form-item label="短信签名：" name="signName">
			<a-input v-model:value="formData.signName" placeholder="请输入短信签名" allow-clear />
		</a-form-item>
	</a-form>
</template>

<script setup name="TencentSmsSend">
	import { required } from '@/utils/formRules'
	import { message } from 'ant-design-vue'
	import smsApi from '@/api/dev/smsApi'
	// 定义emit事件
	const emit = defineEmits({ loadingStart: null, loadingEnd: null })
	// 默认是关闭状态
	const visible = ref(false)
	const formRef = ref()
	// 表单数据，也就是默认给一些数据
	const formData = ref({})

	// 默认要校验的
	const formRules = {
		phoneNumbers: [required('请输入手机号，多个逗号拼接')],
		templateCode: [required('请输入短信服务控制台配置且审核通过的模板编码')]
	}
	const send = () => {
		formRef.value
			.validate()
			.then(() => {
				emit('loadingStart')
				smsApi
					.smsSendTencent(formData.value)
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
