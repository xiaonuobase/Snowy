<template>
	<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
		<a-form-item label="发送方式：" name="sendType">
			<a-radio-group v-model:value="sendType">
				<a-radio value="TXT">纯文本</a-radio>
				<a-radio value="HTML">HTML</a-radio>
				<a-radio value="TMP">模板邮件</a-radio>
			</a-radio-group>
		</a-form-item>
		<a-form-item label="发件人邮箱：" name="sendAccount">
			<a-input v-model:value="formData.sendAccount" placeholder="请输入管理控制台中配置的发信地址" />
		</a-form-item>
		<a-form-item label="接收人邮箱：" name="receiveAccounts">
			<a-textarea
				v-model:value="formData.receiveAccounts"
				:placeholder="sendType !== 'TMP' ? '请输入接收人邮箱，多个逗号拼接' : '预先创建且上传了收件人的收件人列表名称'"
				:auto-size="{ minRows: 2, maxRows: 5 }"
			/>
		</a-form-item>
		<a-form-item label="邮件主题：" name="subject" v-if="sendType !== 'TMP'">
			<a-input v-model:value="formData.subject" placeholder="请输入邮件主题" allow-clear />
		</a-form-item>
		<a-form-item label="邮件正文" name="content" v-if="sendType === 'TXT'">
			<a-textarea
				v-model:value="formData.content"
				placeholder="请输入邮件正文"
				:auto-size="{ minRows: 6, maxRows: 6 }"
			/>
		</a-form-item>
		<a-form-item label="邮件正文" name="content" v-if="sendType === 'HTML'">
			<xn-editor
				v-model="formData.content"
				placeholder="请输入邮件正文"
				:height="200"
				:file-upload-function="apiFunction.fileUploadApi"
			/>
		</a-form-item>
		<a-form-item label="模板名：" name="templateName" v-if="sendType === 'TMP'">
			<a-input v-model:value="formData.templateName" placeholder="请输入预先创建且通过审核的模板名称" allow-clear />
		</a-form-item>
		<a-form-item label="标签名：" name="tagName" v-if="sendType === 'TMP'">
			<a-input v-model:value="formData.tagName" placeholder="请输入标签名" allow-clear />
		</a-form-item>
		<a-form-item label="发件人昵称：" name="sendUser" v-if="sendType !== 'TMP'">
			<a-input v-model:value="formData.sendUser" placeholder="设置发件人昵称" allow-clear />
		</a-form-item>
	</a-form>
</template>

<script setup name="aliyunEmailSend">
	import { message } from 'ant-design-vue'
	import XnEditor from '@/components/Editor/index.vue'
	import { required } from '@/utils/formRules'
	import emailApi from '@/api/dev/emailApi'
	import fileApi from '@/api/dev/fileApi'

	// 发送文本方式
	const sendType = ref('TXT')
	// 定义emit事件
	const emit = defineEmits({ loadingStart: null, loadingEnd: null })
	const formRef = ref()
	// 表单数据
	const formData = ref({})

	// 默认要校验的
	const formRules = {
		sendAccount: [required('请输入发件人邮箱，即管理控制台中配置的发信地址')],
		receiveAccounts: [required('请输入接收人邮箱，多个逗号拼接')],
		subject: [required('请输入邮件主题')],
		templateName: [required('请输入预先创建且通过审核的模板名称')],
		content: [required('请输入邮件正文')]
	}

	// 发送
	const send = () => {
		formRef.value
			.validate()
			.then(() => {
				emit('loadingStart')
				if (sendType.value === 'TXT') {
					emailApi
						.emailSendAliyunTxt(formData.value)
						.then(() => {
							message.success('发送成功')
						})
						.finally(() => {
							emit('loadingEnd')
						})
				}
				if (sendType.value === 'HTML') {
					emailApi
						.emailSendAliyunHtml(formData.value)
						.then(() => {
							message.success('发送成功')
						})
						.finally(() => {
							emit('loadingEnd')
						})
				}
				if (sendType.value === 'TMP') {
					emailApi
						.emailSendAliyunTmp(formData.value)
						.then(() => {
							message.success('发送成功')
						})
						.finally(() => {
							emit('loadingEnd')
						})
				}
			})
			.catch(() => {})
	}
	// 传递文件上传需要的API
	const apiFunction = {
		fileUploadApi: (param) => {
			return fileApi.fileUploadDynamicReturnUrl(param).then((data) => {
				return Promise.resolve(data)
			})
		}
	}
	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		send
	})
</script>
