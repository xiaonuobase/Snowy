<template>
	<a-form ref="emailLoginFormRef" :model="emailFormData" :rules="formRules" layout="vertical">
		<a-form-item name="email" label="邮箱号">
			<a-input v-model:value="emailFormData.email" placeholder="请输入邮箱号" size="large">
				<template #prefix>
					<MailOutlined class="field-icon" />
				</template>
			</a-input>
		</a-form-item>
		<a-form-item name="emailValidCode" label="邮箱验证码">
			<a-row :gutter="12">
				<a-col :span="15">
					<a-input v-model:value="emailFormData.emailValidCode" placeholder="请输入验证码" size="large">
						<template #prefix>
							<MailOutlined class="field-icon" />
						</template>
					</a-input>
				</a-col>
				<a-col :span="9">
					<a-button size="large" block @click="getEmailValidCode" :disabled="state.smsSendBtn">
						{{ (!state.smsSendBtn && '获取验证码') || state.time + ' s' }}
					</a-button>
				</a-col>
			</a-row>
		</a-form-item>
		<a-form-item class="submit-item">
			<a-button type="primary" class="login-button" :loading="loading" size="large" @click="submitLogin" block>
				立即登录
			</a-button>
		</a-form-item>
	</a-form>
	<a-modal v-model:open="visible" :width="400" title="机器验证" @cancel="handleCancel" @ok="handleOk" centered>
		<a-form ref="emailLoginFormModalRef" :model="emailFormModalData" :rules="formModalRules" layout="vertical">
			<a-form-item name="validCode" label="请输入图中验证码">
				<a-row :gutter="12">
					<a-col :span="15">
						<a-input v-model:value="emailFormModalData.validCode" placeholder="验证码" size="large">
							<template #prefix>
								<SafetyOutlined class="field-icon" />
							</template>
						</a-input>
					</a-col>
					<a-col :span="9">
						<div class="captcha-img-wrapper" style="height: 40px">
							<img :src="validCodeBase64" class="login-validCode-img" @click="getEmailPicCaptcha" style="width: 100%; height: 100%; object-fit: cover; cursor: pointer" />
						</div>
					</a-col>
				</a-row>
			</a-form-item>
		</a-form>
	</a-modal>
</template>

<script setup name="emailLoginForm">
	import { message } from 'ant-design-vue'
	import { required, rules } from '@/utils/formRules'
	import clientLoginApi from '@/api/auth/client/clientLoginApi'
	import { afterLogin } from './util'

	const emailLoginFormRef = ref()
	const emailFormData = ref({})
	const loading = ref(false)
	let state = ref({
		time: 60,
		smsSendBtn: false
	})
	let formRules = ref({})
	const emailValidCodeReqNo = ref('')

	// 点击获取邮箱验证码
	const getEmailValidCode = () => {
		formRules.value.email = [required('请输入正确的邮箱号'), rules.email]
		delete formRules.value.emailValidCode
		emailLoginFormRef.value.validate().then(() => {
			// 显示弹框
			visible.value = true
			// 获取内部图片验证码
			getEmailPicCaptcha()
		})
	}

	// 点击登录按钮
	const submitLogin = async () => {
		formRules.value.email = [required('请输入正确的邮箱号'), rules.email]
		formRules.value.emailValidCode = [required('请输入邮箱验证码'), rules.number]

		const validate = await emailLoginFormRef.value.validate().catch(() => {})
		if (!validate) return false

		emailFormData.value.validCode = emailFormData.value.emailValidCode
		// delete emailFormData.value.emailValidCode
		emailFormData.value.validCodeReqNo = emailValidCodeReqNo.value
		loading.value = true
		clientLoginApi
			.clientLoginByEmail(emailFormData.value)
			.then((token) => {
				afterLogin(token)
			})
			.finally(() => {
				loading.value = false
			})
	}

	// 弹框的
	const visible = ref(false)
	const emailLoginFormModalRef = ref()
	const emailFormModalData = ref({})
	const validCodeBase64 = ref('')
	const validCodeReqNo = ref('')
	const formModalRules = {
		validCode: [required('请输入图形验证码'), rules.lettersNum]
	}
	const getEmailPicCaptcha = () => {
		clientLoginApi.clientGetPicCaptcha().then((data) => {
			validCodeBase64.value = data.validCodeBase64
			emailFormModalData.value.validCodeReqNo = data.validCodeReqNo
		})
	}
	const handleCancel = () => {
		visible.value = false
	}
	const handleOk = () => {
		// 获取到里面的验证码，并发送邮箱
		emailLoginFormModalRef.value.validate().then(() => {
			visible.value = false
			// 发送邮箱，首先拿到刚刚输入的邮箱号
			emailFormModalData.value.email = emailFormData.value.email
			// 禁用发送按钮，并设置为倒计时
			state.value.smsSendBtn = true
			const interval = window.setInterval(() => {
				if (state.value.time-- <= 0) {
					state.value.time = 60
					state.value.smsSendBtn = false
					window.clearInterval(interval)
				}
			}, 1000)
			const hide = message.loading('验证码发送中..', 0)

			clientLoginApi
				.clientGetEmailValidCode(emailFormModalData.value)
				.then((data) => {
					emailValidCodeReqNo.value = data
					visible.value = false
					setTimeout(hide, 500)
					emailFormModalData.value.validCode = ''
				})
				.catch(() => {
					setTimeout(hide, 100)
					clearInterval(interval)
					state.value.smsSendBtn = false
				})
		})
	}
</script>
