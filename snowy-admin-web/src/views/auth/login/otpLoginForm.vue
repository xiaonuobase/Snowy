<template>
	<a-form ref="otpLoginFormRef" :model="ruleForm" :rules="formRules">
		<a-form-item name="accountForOtp">
			<a-input v-model:value="ruleForm.accountForOtp" :placeholder="$t('login.accountPlaceholder')" size="large">
				<template #prefix>
					<user-outlined class="text-black text-opacity-25" />
				</template>
			</a-input>
		</a-form-item>
		<a-form-item name="otpCode">
			<a-input v-model:value="ruleForm.otpCode" :placeholder="$t('login.otpCodePlaceholder')" size="large">
				<template #prefix>
					<mail-outlined class="text-black text-opacity-25" />
				</template>
			</a-input>
		</a-form-item>
		<a-form-item name="validCodeForOtp" v-if="captchaOpen === 'true'">
			<a-row :gutter="8">
				<a-col :span="17">
					<a-input
						v-model:value="ruleForm.validCodeForOtp"
						:placeholder="$t('login.validPlaceholder')"
						size="large"
					>
						<template #prefix>
							<verified-outlined class="login-icon-gray" />
						</template>
					</a-input>
				</a-col>
				<a-col :span="7">
					<img :src="validCodeBase64" class="login-validCode-img" @click="loginCaptcha" />
				</a-col>
			</a-row>
		</a-form-item>
		<a-form-item>
			<a-button type="primary" class="xn-wd" :loading="loading" round size="large" @click="submitLogin">
				{{ $t('login.signIn') }}
			</a-button>
		</a-form-item>
	</a-form>
</template>

<script setup name="otpLoginForm">
	import loginApi from '@/api/auth/loginApi'
	import { afterLogin } from './util'
	import {required} from "@/utils/formRules";
	const { proxy } = getCurrentInstance()
	const props = defineProps({
		captchaOpen: {
			type: String,
			default: () => {}
		}
	})

	const otpLoginFormRef = ref()
	const validCodeBase64 = ref('')
	const loading = ref(false)

	const ruleForm = reactive({
		accountForOtp: '',
		otpCode: '',
		validCodeForOtp: '',
		validCodeReqNo: ''
	})

	const formRules = reactive({
		accountForOtp: [required(proxy.$t('login.accountError'), 'blur')],
		otpCode: [required(proxy.$t('login.otpCodePlaceholder'), 'blur')]
	})

	// 通过开关加载内容
	const refreshSwitch = () => {
		// 判断是否开启验证码
		if (props.captchaOpen === 'true') {
			// 加载验证码
			loginCaptcha()
			// 加入校验
			formRules.validCodeForOtp = [required(proxy.$t('login.validError'), 'blur')]
		}
	}

	// 获取验证码
	const loginCaptcha = () => {
		loginApi.getPicCaptcha().then((data) => {
			validCodeBase64.value = data.validCodeBase64
			ruleForm.validCodeReqNo = data.validCodeReqNo
		})
	}

	// 通过开关加载内容
	refreshSwitch()

	// 点击登录按钮
	const submitLogin = async () => {
		const validate = await otpLoginFormRef.value.validate().catch(() => {})
		if (!validate) return false
		const loginData = {
			account: ruleForm.accountForOtp,
			otpCode: ruleForm.otpCode,
			validCode: ruleForm.validCodeForOtp,
			validCodeReqNo: ruleForm.validCodeReqNo
		}
		loading.value = true
		loginApi
			.loginByOtp(loginData)
			.then(async (loginToken) => {
				await afterLogin(loginToken)
			}).catch(() => {
				loading.value = false
				if (props.captchaOpen === 'true') {
					loginCaptcha()
				}
			})
	}
</script>
