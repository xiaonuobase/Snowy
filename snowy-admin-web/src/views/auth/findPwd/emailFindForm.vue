<template>
	<a-form ref="emailResetFormRef" :model="emailFormData" :rules="formRules">
		<a-form-item name="email">
			<a-input v-model:value="emailFormData.email" :placeholder="$t('login.emailPlaceholder')" size="large">
				<template #prefix>
					<mail-outlined class="xn-color-00025" />
				</template>
			</a-input>
		</a-form-item>
		<a-form-item name="emailValidCode">
			<a-row :gutter="8">
				<a-col :span="17">
					<a-input
						v-model:value="emailFormData.emailValidCode"
						:placeholder="$t('login.emailCodePlaceholder')"
						size="large"
					>
						<template #prefix>
							<mail-outlined class="xn-color-00025" />
						</template>
					</a-input>
				</a-col>
				<a-col :span="7">
					<a-button size="large" class="xn-wd" @click="getEmailValidCode" :disabled="state.smsSendBtn">{{
						(!state.smsSendBtn && $t('login.getSmsCode')) || state.time + ' s'
					}}</a-button>
				</a-col>
			</a-row>
		</a-form-item>

		<a-form-item name="newPassword">
			<a-input-password
				v-model:value="emailFormData.newPassword"
				:placeholder="$t('login.newPwdPlaceholder')"
				size="large"
			>
				<template #prefix>
					<LockOutlined class="xn-color-00025" />
				</template>
			</a-input-password>
		</a-form-item>

		<a-form-item>
			<a-row :gutter="8">
				<a-col :span="7">
					<a-button class="xn-wd" round size="large" href="/login">{{ $t('login.backLogin') }}</a-button>
				</a-col>
				<a-col :span="17">
					<a-button type="primary" class="xn-wd" :loading="islogin" round size="large" @click="submitReset">{{
						$t('login.restPassword')
					}}</a-button>
				</a-col>
			</a-row>
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
						<a-input
							v-model:value="emailFormModalData.validCode"
							:placeholder="$t('login.validLaceholder')"
							size="large"
						>
							<template #prefix>
								<verified-outlined class="xn-color-00025" />
							</template>
						</a-input>
					</a-col>
					<a-col :span="7">
						<img :src="validCodeBase64" class="xn-findform-line" @click="getPhonePicCaptcha" />
					</a-col>
				</a-row>
			</a-form-item>
		</a-form>
	</a-modal>
</template>

<script setup name="emailFindForm">
	import { message } from 'ant-design-vue'
	import router from '@/router'
	import { required, rules } from '@/utils/formRules'
	import userCenterApi from '@/api/sys/userCenterApi'
	import smCrypto from '@/utils/smCrypto'
	const emailResetFormRef = ref()
	const emailFormData = ref({})
	const islogin = ref(false)
	let state = ref({
		time: 60,
		smsSendBtn: false
	})
	let formRules = ref({})
	const emailValidCodeReqNo = ref('')

	// 点击获取邮箱验证码
	const getEmailValidCode = () => {
		formRules.value.email = [required(), rules.email]
		delete formRules.value.emailValidCode
		delete formRules.value.newPassword
		emailResetFormRef.value.validate().then(() => {
			// 显示弹框
			visible.value = true
			// 获取内部图片验证码
			getPhonePicCaptcha()
		})
	}
	// 点击找回按钮
	const submitReset = () => {
		formRules.value.email = [required(), rules.email]
		formRules.value.emailValidCode = [required(), rules.number]
		formRules.value.newPassword = [required()]

		emailResetFormRef.value
			.validate()
			.then(() => {
				emailFormData.value.validCode = emailFormData.value.emailValidCode
				emailFormData.value.validCodeReqNo = emailValidCodeReqNo.value
				emailFormData.value.newPassword = smCrypto.doSm2Encrypt(emailFormData.value.newPassword)
				islogin.value = true
				userCenterApi
					.userFindPasswordByEmail(emailFormData.value)
					.then(() => {
						router.replace({
							path: '/'
						})
						message.success('找回成功')
					})
					.finally(() => {
						islogin.value = false
					})
			})
			.catch(() => {})
	}

	// 弹框的
	const visible = ref(false)
	const emailLoginFormModalRef = ref()
	const emailFormModalData = ref({})
	const validCodeBase64 = ref('')
	const formModalRules = {
		validCode: [required(), rules.lettersNum]
	}
	const getPhonePicCaptcha = () => {
		userCenterApi.userGetPicCaptcha().then((data) => {
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
			// 发送邮箱，首先拿到刚刚输入的邮箱
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

			userCenterApi
				.userFindPasswordGetEmailValidCode(emailFormModalData.value)
				.then((data) => {
					emailValidCodeReqNo.value = data
					visible.value = false
					setTimeout(hide, 500)
				})
				.catch(() => {
					setTimeout(hide, 100)
					clearInterval(interval)
					state.value.smsSendBtn = false
				})
				.finally(() => {
					emailFormModalData.value.validCode = ''
				})
		})
	}
</script>
