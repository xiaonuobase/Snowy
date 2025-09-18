<template>
	<div class="login-wrapper">
		<div class="login_background">
			<div class="logo_background">
				<a
					:class="{ 'no-link': !sysBaseConfig.SNOWY_SYS_COPYRIGHT_URL }"
					:href="sysBaseConfig.SNOWY_SYS_COPYRIGHT_URL"
					target="_blank"
					@click="handleLink"
				>
					<img :alt="sysBaseConfig.SNOWY_SYS_NAME" :src="sysBaseConfig.SNOWY_SYS_LOGO" />
					<label>{{ sysBaseConfig.SNOWY_SYS_NAME }}</label>
				</a>
			</div>
			<div class="version">
				<p>{{ sysBaseConfig.SNOWY_SYS_DEFAULT_DESCRRIPTION }}</p>
				<p>{{ sysBaseConfig.SNOWY_SYS_COPYRIGHT }} {{ sysBaseConfig.SNOWY_SYS_VERSION }}</p>
			</div>
		</div>
		<div class="login_main">
			<div class="login-form">
				<a-card>
					<div class="login-header">
						<h2>{{tipText}}</h2>
					</div>
					<a-spin :tip="tipText" v-if="showLoading">
						<div class="h-[300px]">
							<a-skeleton />
						</div>
					</a-spin>
					<a-empty :description="tipText" v-if="!showLoading && !showBind"></a-empty>
					<a-form ref="loginForm" :model="ruleForm" :rules="rules" v-if="showBind">
						<a-form-item name="account">
							<a-input
								v-model:value="ruleForm.account"
								:placeholder="$t('login.accountPlaceholder')"
								size="large"
								@keyup.enter="bindAccount"
							>
								<template #prefix>
									<UserOutlined class="login-icon-gray" />
								</template>
							</a-input>
						</a-form-item>
						<a-form-item name="password">
							<a-input-password
								v-model:value="ruleForm.password"
								:placeholder="$t('login.PWPlaceholder')"
								size="large"
								autocomplete="off"
								@keyup.enter="bindAccount"
							>
								<template #prefix>
									<LockOutlined class="login-icon-gray" />
								</template>
							</a-input-password>
						</a-form-item>
						<a-form-item name="validCode" v-if="captchaOpen === 'true'">
							<a-row :gutter="8">
								<a-col :span="17">
									<a-input
										v-model:value="ruleForm.validCode"
										:placeholder="$t('login.validPlaceholder')"
										size="large"
										@keyup.enter="bindAccount"
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
							<a href="/login" class="xn-color-0d84ff">{{ $t('login.signIn') }}</a>
						</a-form-item>
						<a-form-item>
							<a-button type="primary" class="w-full" :loading="loading" round size="large" @click="bindAccount"
							>{{ $t('login.bindAccount') }}
							</a-button>
						</a-form-item>
					</a-form>
				</a-card>
			</div>
		</div>
	</div>
</template>

<script setup name="loginCallback">
	import { message } from 'ant-design-vue'
	import thirdApi from '@/api/auth/thirdApi'
	import { globalStore } from '@/store'
	import {afterLogin} from "@/views/auth/login/util";
	import router from '@/router'
	import {required} from "@/utils/formRules";
	import tool from "@/utils/tool";
	import loginApi from "@/api/auth/loginApi";
	import smCrypto from '@/utils/smCrypto'
	const { proxy } = getCurrentInstance()
	const route = router.currentRoute.value
	const showLoading = ref(true)
	const showBind = ref(false)
	const tipText = ref(proxy.$t('login.thirdLogin'))
	const thirdId = ref(null)
	const store = globalStore()
	const sysBaseConfig = computed(() => {
		return tool.data.get('SNOWY_SYS_BASE_CONFIG') || store.sysBaseConfig
	})
	const captchaOpen = ref(sysBaseConfig.value.SNOWY_SYS_DEFAULT_CAPTCHA_OPEN_FLAG_FOR_B)

	const loading = ref(false)
	const validCodeBase64 = ref('')

	const ruleForm = reactive({
		validCode: '',
		validCodeReqNo: '',
		autologin: false
	})

	const rules = reactive({
		account: [required(proxy.$t('login.accountError'), 'blur')],
		password: [required(proxy.$t('login.PWError'), 'blur')]
	})

	const showError = (msg, alert) => {
		if(alert) {
			message.error(msg)
		}
		tipText.value = msg
		showLoading.value = false
	}

	onMounted(() => {
		if(!route.params.platform) {
			showError(proxy.$t('login.paramError'), true)
			return
		}
		// 获取当前url
		const url = new URL(window.location.href)
		let argLength = 0
		const params = {}
		url.searchParams.forEach((value, key) => {
			argLength += 1
			params[key] = value
		})
		// 参数不能为空
		if (argLength === 0) {
			showError(proxy.$t('login.paramError'), true)
			return
		}
		// 平台
		params.platform = route.params.platform
		thirdApi
			.thirdCallback(params)
			.then(async (data) => {
				if (data.startsWith('needBind')) {
					showError(proxy.$t('login.bindAccount'), true)
					thirdId.value = data.split(":")[1];
					showBind.value = true
					refreshSwitch()
				} else {
					await afterLogin(data)
				}
			})
			.catch((err) => {
				showError(proxy.$t('login.thirdLoginError'), false)
				console.log(err)
			})
	})
	// 通过开关加载内容
	const refreshSwitch = () => {
		// 判断是否开启验证码
		if (captchaOpen.value === 'true') {
			// 加载验证码
			loginCaptcha()
			// 加入校验
			rules.validCode = [required(proxy.$t('login.validError'), 'blur')]
		}
	}
	// 获取验证码
	const loginCaptcha = () => {
		loginApi.getPicCaptcha().then((data) => {
			validCodeBase64.value = data.validCodeBase64
			ruleForm.validCodeReqNo = data.validCodeReqNo
		})
	}
	// 绑定账号
	const loginForm = ref()
	const bindAccount = async () => {
		loginForm.value
			.validate()
			.then(async () => {
				loading.value = true
				const loginData = {
					thirdId: thirdId.value,
					account: ruleForm.account,
					// 密码进行SM2加密，传输过程中看到的只有密文，后端存储使用hash
					password: smCrypto.doSm2Encrypt(ruleForm.password),
					validCode: ruleForm.validCode,
					validCodeReqNo: ruleForm.validCodeReqNo
				}
				const loginToken = await thirdApi.thirdBindAccount(loginData)
				await afterLogin(loginToken)
			})
			.catch((err) => {
				console.log(err)
				loading.value = false
				if (captchaOpen.value === 'true') {
					loginCaptcha()
				}}
			)
	}
	// logo链接
	const handleLink = (e) => {
		if (!sysBaseConfig.value.SNOWY_SYS_COPYRIGHT_URL) {
			e?.stopPropagation()
			e?.preventDefault()
		}
	}
</script>

<style lang="less" scoped>
	@import 'login';
</style>
