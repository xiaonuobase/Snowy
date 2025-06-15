<script setup name="bindEmail">
	import { ref, reactive } from 'vue'
	import userCenterApi from '@/api/sys/userCenterApi'
	import { message, Modal, Form, Input, Button } from 'ant-design-vue'
	import { required, rules } from '@/utils/formRules'

	const visible = ref(false)
	const loading = ref(false)
	const captchaLoading = ref(false)
	const captchaImage = ref('')
	const captchaReqNo = ref('')
	const messageCodeReqNo = ref('')
	const bindEmail = ref('')
	let state = ref({
		time: 60,
		sendBtn: false
	})
	const formRef = ref()
	const formState = reactive({
		email: '',
		validCode: '',
		emailValidCode: '',
		validCodeReqNo: ''
	})
	// 打开绑定
	const open = (email) => {
		if (email) {
			formState.email = email
			bindEmail.value = email
		}
		visible.value = true
		getCaptcha()
	}
	// 获取图片验证码
	const getCaptcha = () => {
		captchaLoading.value = true
		try {
			userCenterApi.userGetPicCaptcha().then((data) => {
				captchaImage.value = data.validCodeBase64
				captchaReqNo.value = data.validCodeReqNo
			})
		} finally {
			captchaLoading.value = false
		}
	}

	// 获取邮箱验证码
	const getEmailValidCode = async () => {
		try {
			if (!formState.email) {
				message.error('请输入邮箱号')
				return
			}
			if (!formState.validCode) {
				message.error('请输入图片验证码')
				return
			}
			const hide = message.loading('验证码发送中..', 0)
			userCenterApi
				.userBindEmailGetEmailValidCode(
					{
						email: formState.email,
						validCode: formState.validCode,
						validCodeReqNo: captchaReqNo.value
					},
					bindEmail.value
				)
				.then((data) => {
					// 禁用发送按钮，并设置为倒计时
					state.value.sendBtn = true
					const interval = window.setInterval(() => {
						if (state.value.time-- <= 0) {
							state.value.time = 60
							state.value.sendBtn = false
							window.clearInterval(interval)
						}
					}, 1000)
					messageCodeReqNo.value = data
					message.success('验证码已发送到邮箱')
				})
				.catch(() => {
					// 清理掉已经输入的
					formState.validCode = ''
					formState.validCodeReqNo = ''
					messageCodeReqNo.value = ''
					getCaptcha()
				})
				.finally(() => {
					setTimeout(hide, 100)
				})
		} catch (error) {
			getCaptcha()
		}
	}

	// 提交表单
	const handleSubmit = async () => {
		try {
			await formRef.value.validate()
			loading.value = true
			userCenterApi
				.userBindEmail({
					email: formState.email,
					validCode: formState.emailValidCode,
					validCodeReqNo: messageCodeReqNo.value
				})
				.then(() => {
					message.success('绑定成功')
					visible.value = false
				})
				.catch(() => {
					formState.emailValidCode = ''
					messageCodeReqNo.value = ''
				})
		} catch (error) {
			console.error(error)
		} finally {
			loading.value = false
		}
	}

	// 表单验证规则
	const formRules = {
		email: [required('请输入邮箱号'), rules.email],
		validCode: required('请输入图片验证码'),
		emailValidCode: [{ required: true, message: '请输入邮箱验证码', trigger: 'blur' }]
	}
	const onClose = () => {
		bindEmail.value = ''
		visible.value = false
		formRef.value.resetFields()
	}
	defineExpose({
		open
	})
</script>

<template>
	<a-modal
		v-model:open="visible"
		:title="bindEmail ? '修改邮箱' : '绑定邮箱'"
		:width="400"
		@ok="handleSubmit"
		:destroy-on-close="true"
		@cancel="onClose"
	>
		<Form ref="formRef" :model="formState" :rules="formRules" layout="vertical">
			<Form.Item name="email">
				<Input v-model:value="formState.email" placeholder="请输入邮箱号" />
			</Form.Item>
			<Form.Item name="validCode">
				<a-row :gutter="8">
					<a-col :span="16">
						<Input v-model:value="formState.validCode" placeholder="请输入图片验证码" />
					</a-col>
					<a-col :span="8">
						<img v-if="captchaImage" :src="captchaImage" class="captcha-image" @click="getCaptcha" />
					</a-col>
				</a-row>
			</Form.Item>
			<Form.Item name="emailValidCode">
				<a-row :gutter="8">
					<a-col :span="16">
						<Input v-model:value="formState.emailValidCode" placeholder="请输入邮箱验证码" />
					</a-col>
					<a-col :span="8">
						<Button :loading="captchaLoading" @click="getEmailValidCode" style="width: 100%" :disabled="state.sendBtn">
							{{ (!state.sendBtn && '获取验证码') || state.time + ' s' }}
						</Button>
					</a-col>
				</a-row>
			</Form.Item>
		</Form>
	</a-modal>
</template>

<style scoped lang="less">
	.captcha-wrapper {
		display: flex;
		align-items: center;
		gap: 8px;

		.captcha-image {
			height: 32px;
			cursor: pointer;
		}
	}
</style>
