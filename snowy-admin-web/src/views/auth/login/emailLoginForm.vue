<template>
	<a-form ref="emailLoginFormRef" :model="emailFormData" :rules="formRules">
		<a-form-item name="email">
			<a-input v-model:value="emailFormData.email" :placeholder="$t('login.emailPlaceholder')" size="large">
				<template #prefix>
					<mail-outlined class="text-black text-opacity-25" />
				</template>
			</a-input>
		</a-form-item>
		<a-form-item name="emailValidCode">
			<a-row :gutter="8">
				<a-col :span="16">
					<a-input v-model:value="emailFormData.emailValidCode" :placeholder="$t('login.validError')" size="large">
						<template #prefix>
							<mail-outlined class="text-black text-opacity-25" />
						</template>
					</a-input>
				</a-col>
				<a-col :span="8">
					<a-button size="large" class="xn-wd" @click="getEmailValidCode" :disabled="state.smsSendBtn">
						{{ (!state.smsSendBtn && $t('login.getEmailCode')) || state.time + ' s' }}
					</a-button>
				</a-col>
			</a-row>
		</a-form-item>
		<a-form-item>
			<a-button type="primary" class="xn-wd" :loading="loading" round size="large" @click="submitLogin">
				{{ $t('login.signIn') }}
			</a-button>
		</a-form-item>
	</a-form>
	<a-modal
		v-model:open="visible"
		:width="400"
		:title="$t('login.machineValidation')"
		@cancel="handleCancel"
		@ok="handleOk"
	>
		<a-form ref="emailLoginFormModalRef" :model="emailFormModalData" :rules="formModalRules">
			<a-form-item name="validCode">
				<a-row :gutter="8">
					<a-col :span="17">
						<a-input v-model:value="emailFormModalData.validCode" :placeholder="$t('login.validError')" size="large">
							<template #prefix>
								<verified-outlined class="text-black text-opacity-25" />
							</template>
						</a-input>
					</a-col>
					<a-col :span="7">
						<img :src="validCodeBase64" class="xn-findform-line" @click="getEmailPicCaptcha" />
					</a-col>
				</a-row>
			</a-form-item>
		</a-form>
	</a-modal>
</template>

<script setup name="emailLoginForm">
	import { message } from 'ant-design-vue'
	import { required, rules } from '@/utils/formRules'
	import loginApi from '@/api/auth/loginApi'
	import { afterLogin } from './util'
	const { proxy } = getCurrentInstance()

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
		formRules.value.email = [required(proxy.$t('login.emailValidPlaceholder')), rules.email]
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
		formRules.value.email = [required(proxy.$t('login.emailValidPlaceholder')), rules.email]
		formRules.value.emailValidCode = [required(proxy.$t('login.emailCodePlaceholder')), rules.number]

		const validate = await emailLoginFormRef.value.validate().catch(() => {})
		if (!validate) return false

		emailFormData.value.validCode = emailFormData.value.emailValidCode
		// delete emailFormData.value.emailValidCode
		emailFormData.value.validCodeReqNo = emailValidCodeReqNo.value
		loading.value = true
		loginApi
			.loginByEmail(emailFormData.value)
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
		validCode: [required(proxy.$t('login.validError')), rules.lettersNum]
	}
	const getEmailPicCaptcha = () => {
		loginApi.getPicCaptcha().then((data) => {
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

			loginApi
				.getEmailValidCode(emailFormModalData.value)
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
