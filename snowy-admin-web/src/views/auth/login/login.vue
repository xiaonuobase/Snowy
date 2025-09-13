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
					<img :alt="systemName" :src="sysBaseConfig.SNOWY_SYS_LOGO" />
					<label>{{ systemName }}</label>
				</a>
			</div>
			<div class="version">
				<p>{{ sysBaseConfig.SNOWY_SYS_DEFAULT_DESCRRIPTION }}</p>
				<p>{{ sysBaseConfig.SNOWY_SYS_COPYRIGHT }} {{ sysBaseConfig.SNOWY_SYS_VERSION }}</p>
			</div>
		</div>
		<div class="login_main">
			<div class="login_config">
				<a-dropdown>
					<global-outlined />
					<template #overlay>
						<a-menu>
							<a-menu-item
								v-for="item in lang"
								:key="item.value"
								:command="item"
								:class="{ selected: config.lang === item.value }"
								@click="configLang(item.value)"
							>
								{{ item.name }}
							</a-menu-item>
						</a-menu>
					</template>
				</a-dropdown>
			</div>
			<div class="login-form">
				<a-card>
					<div class="login-header">
						<h2>{{ $t('login.signInTitle') }}</h2>
					</div>
					<a-tabs v-model:activeKey="activeKey">
						<a-tab-pane key="userAccount" :tab="$t('login.accountPassword')">
							<a-form ref="loginForm" :model="ruleForm" :rules="rules">
								<a-form-item name="account">
									<a-input
										v-model:value="ruleForm.account"
										:placeholder="$t('login.accountPlaceholder')"
										size="large"
										@keyup.enter="login"
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
										@keyup.enter="login"
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
												@keyup.enter="login"
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
									<div style="display: flex; justify-content: space-between">
										<a href="/findpwd" class="xn-color-0d84ff">{{ $t('login.forgetPassword') }}？</a>
										<a href="/register" class="xn-color-0d84ff" v-if="registerOpen === 'true'">
											{{ $t('login.notAccountPleaseRegister') }}
										</a>
									</div>
								</a-form-item>
								<a-form-item>
									<a-button type="primary" class="w-full" :loading="loading" round size="large" @click="login"
										>{{ $t('login.signIn') }}
									</a-button>
								</a-form-item>
							</a-form>
						</a-tab-pane>
						<a-tab-pane key="userSms" :tab="$t('login.phoneLogin')" force-render v-if="loginTypes.phoneLogin === 'true'">
							<phone-login-form />
						</a-tab-pane>
						<a-tab-pane key="userEmail" :tab="$t('login.emailLogin')" force-render v-if="loginTypes.emailLogin === 'true'">
							<email-login-form />
						</a-tab-pane>
						<a-tab-pane key="userOtp" :tab="$t('login.otpLogin')" force-render v-if="loginTypes.otpLogin === 'true'">
							<otp-login-form :captchaOpen="captchaOpen" />
						</a-tab-pane>
					</a-tabs>
					<div v-if="configData.FRONT_BACK_LOGIN_URL_SHOW">
						<a href="/front/client/index" class="xn-color-0d84ff">{{ $t('login.frontLogin') }}</a>
					</div>
					<three-login v-if="configData.THREE_LOGIN_SHOW && !appId" />
					<three-login-for-app ref="threeLoginForAppRef"
										 v-if="configData.THREE_LOGIN_SHOW && appId"
										 :appId="appId"
										 :loginTypes="loginTypes"
										 @updateLoginTypes="updateLoginTypes"
										 @updateSystemName="updateSystemName"/>
				</a-card>
			</div>
		</div>
	</div>
</template>
<script setup>
	import loginApi from '@/api/auth/loginApi'
	const PhoneLoginForm = defineAsyncComponent(() => import('./phoneLoginForm.vue'))
	const EmailLoginForm = defineAsyncComponent(() => import('./emailLoginForm.vue'))
	const OtpLoginForm = defineAsyncComponent(() => import('./otpLoginForm.vue'))
	import ThreeLogin from './threeLogin.vue'
	import ThreeLoginForApp from './threeLoginForApp.vue'
	import smCrypto from '@/utils/smCrypto'
	import { required } from '@/utils/formRules'
	import { afterLogin } from './util'
	import configData from '@/config'
	import configApi from '@/api/dev/configApi'
	import tool from '@/utils/tool'
	import { globalStore, iframeStore, keepAliveStore, viewTagsStore } from '@/store'
	import router from '@/router'
	const route = router.currentRoute.value
	const appId = computed(() => {
		return route.query.appId
	})
	const threeLoginForAppRef = ref(null)
	const { proxy } = getCurrentInstance()
	const activeKey = ref('userAccount')

	const captchaOpen = ref(configData.SYS_BASE_CONFIG.SNOWY_SYS_DEFAULT_CAPTCHA_OPEN_FLAG_FOR_B)
	const registerOpen = ref('false')
	const loginTypes = reactive({
		phoneLogin: 'false',
		emailLogin: 'false',
		otpLogin: 'false'
	})
	const updateLoginTypes = (newTypes) => {
		Object.assign(loginTypes, newTypes)
	}
	const updateSystemName = (newSystemName) => {
		systemName.value = newSystemName
	}
	const validCodeBase64 = ref('')
	const loading = ref(false)

	const ruleForm = reactive({
		validCode: '',
		validCodeReqNo: '',
		autologin: false
	})
	// 如果是开发环境，填充用户名密码
	if (process.env.NODE_ENV === 'development') {
		ruleForm.account = 'superAdmin'
		ruleForm.password = '123456'
	}

	const rules = reactive({
		account: [required(proxy.$t('login.accountError'), 'blur')],
		password: [required(proxy.$t('login.PWError'), 'blur')]
	})
	const lang = ref([
		{
			name: '简体中文',
			value: 'zh-cn'
		},
		{
			name: 'English',
			value: 'en'
		}
	])
	const config = ref({
		lang: tool.data.get('APP_LANG') || proxy.$CONFIG.LANG,
		theme: tool.data.get('APP_THEME') || 'default'
	})

	const store = globalStore()
	const kStore = keepAliveStore()
	const iStore = iframeStore()
	const vStore = viewTagsStore()

	const setSysBaseConfig = store.setSysBaseConfig
	const clearKeepLive = kStore.clearKeepLive
	const clearIframeList = iStore.clearIframeList
	const clearViewTags = vStore.clearViewTags

	const sysBaseConfig = computed(() => {
		return store.sysBaseConfig
	})
	const systemName = ref(sysBaseConfig.value.SNOWY_SYS_NAME)
	onMounted(() => {
		let formData = ref(configData.SYS_BASE_CONFIG)
		configApi
			.configSysBaseList()
			.then((data) => {
				if (data) {
					data.forEach((item) => {
						formData.value[item.configKey] = item.configValue
					})
					captchaOpen.value = formData.value.SNOWY_SYS_DEFAULT_CAPTCHA_OPEN_FLAG_FOR_B
					registerOpen.value = formData.value.SNOWY_SYS_DEFAULT_ALLOW_REGISTER_FLAG_FOR_B
					loginTypes.phoneLogin = formData.value.SNOWY_SYS_DEFAULT_ALLOW_PHONE_LOGIN_FLAG_FOR_B
					loginTypes.emailLogin = formData.value.SNOWY_SYS_DEFAULT_ALLOW_EMAIL_LOGIN_FLAG_FOR_B
					loginTypes.otpLogin = formData.value.SNOWY_SYS_DEFAULT_ALLOW_OTP_LOGIN_FLAG_FOR_B
					tool.data.set('SNOWY_SYS_BASE_CONFIG', formData.value)
					setSysBaseConfig(formData.value)
					refreshSwitch()
					if (threeLoginForAppRef.value) {
						threeLoginForAppRef.value.init(appId)
					}
				}
			})
			.catch(() => {})
	})

	onBeforeMount(() => {
		clearViewTags()
		clearKeepLive()
		clearIframeList()
	})

	// 监听语言
	watch(
		() => config.value.lang,
		(newValue) => {
			proxy.$i18n.locale = newValue
			tool.data.set('APP_LANG', newValue)
		}
	)
	// 主题
	watch(
		() => config.value.theme,
		(newValue) => {
			document.body.setAttribute('data-theme', newValue)
		}
	)
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
	// 登录
	const loginForm = ref()
	const login = async () => {
		loginForm.value
			.validate()
			.then(async () => {
				loading.value = true
				const loginData = {
					account: ruleForm.account,
					// 密码进行SM2加密，传输过程中看到的只有密文，后端存储使用hash
					password: smCrypto.doSm2Encrypt(ruleForm.password),
					validCode: ruleForm.validCode,
					validCodeReqNo: ruleForm.validCodeReqNo
				}
				const loginToken = await loginApi.login(loginData)
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
	const configLang = (key) => {
		config.value.lang = key
	}
	// logo链接
	const handleLink = (e) => {
		if (!sysBaseConfig.value.SNOWY_SYS_COPYRIGHT_URL) {
			e?.stopPropagation()
			e?.preventDefault()
		}
	}
</script>
<style lang="less">
	@import 'login';
	.xn-color-0d84ff {
		color: #0d84ff;
	}
</style>
