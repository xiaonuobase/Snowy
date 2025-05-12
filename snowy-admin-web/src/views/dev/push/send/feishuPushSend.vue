<template>
	<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
		<a-form-item label="通知类型：" name="noticeType">
			<a-radio-group v-model:value="formData.noticeType">
				<a-radio value="NONE">无</a-radio>
				<a-radio value="ALL">通知所有人</a-radio>
			</a-radio-group>
		</a-form-item>
		<a-form-item label="推送内容：" name="content">
			<a-textarea
				v-model:value="formData.content"
				placeholder="请输入推送内容"
				:auto-size="{ minRows: 5, maxRows: 10 }"
			/>
		</a-form-item>
	</a-form>
</template>

<script setup name="FeishuPushSend">
	import { message } from 'ant-design-vue'
	import { required } from '@/utils/formRules'
	import pushApi from '@/api/dev/pushApi'
	// 定义emit事件
	const emit = defineEmits({ loadingStart: null, loadingEnd: null })
	const formRef = ref()
	// 表单数据，也就是默认给一些数据
	const formData = ref({
		noticeType: 'NONE'
	})

	// 默认要校验的
	const formRules = {
		content: [required('请输入推送内容')],
		noticeType: [required('请选择通知类型')]
	}
	// 发送短信
	const send = () => {
		formRef.value
			.validate()
			.then(() => {
				emit('loadingStart')
				pushApi
					.pushFeiShuText(formData.value)
					.then(() => {
						message.success('推送成功')
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
