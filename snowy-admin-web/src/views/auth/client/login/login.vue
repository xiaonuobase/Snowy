<template>
	<div class="login-wrapper">
		<div class="login_main">
			<div class="login-form">
				<a-card>
					<div class="login-header">
						<h2>登录</h2>
					</div>
					<a-tabs v-model:activeKey="activeKey">
						<a-tab-pane key="userAccount" tab="账号密码">
							<a-form ref="loginForm" :model="ruleForm" :rules="rules">
								<div v-if="tenSelectShow">
									<a-form-item name="tenCode" v-if="tenOptions.length > 1 || !ruleForm.tenCode">
										<a-select
											v-model:value="ruleForm.tenCode"
											size="large"
											placeholder="请选择租户"
											:options="tenOptions"
											@change="tenCodeChange"
										/>
									</a-form-item>
								</div>
								<a-form-item name="account">
									<a-input v-model:value="ruleForm.account" placeholder="请输入账号" size="large" @keyup.enter="login">
										<template #prefix>
											<UserOutlined class="login-icon-gray" />
										</template>
									</a-input>
								</a-form-item>
								<a-form-item name="password">
									<a-input-password
										v-model:value="ruleForm.password"
										placeholder="请输入密码"
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
												placeholder="请输入验证码"
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
										<a href="/front/client/findPwd">忘记密码？</a>
										<a href="/front/client/register" v-if="registerOpen === 'true'"> 没有账号？前往注册 </a>
									</div>
								</a-form-item>
								<a-form-item>
									<a-button
										type="primary"
										class="w-full"
										:loading="loading"
										round
										size="large"
										@click="login"
										:disabled="loginButtonDisable"
									>
										登录
									</a-button>
								</a-form-item>
							</a-form>
						</a-tab-pane>
						<a-tab-pane key="userSms" tab="手机号登录" force-render v-if="phoneLogin === 'true'">
							<phone-login-form />
						</a-tab-pane>
						<a-tab-pane key="userEmail" tab="邮箱号登录" force-render v-if="emailLogin === 'true'">
							<email-login-form />
						</a-tab-pane>
					</a-tabs>
					<div v-if="configData.FRONT_BACK_LOGIN_URL_SHOW">
						<a href="/login">后台登录</a>
					</div>
				</a-card>
			</div>
		</div>
	</div>
</template>
<script setup>
	import clientLoginApi from '@/api/auth/client/clientLoginApi'
	import smCrypto from '@/utils/smCrypto'
	import { required } from '@/utils/formRules'
	import { afterLogin } from './util'
	import configData from '@/config'
	import configApi from '@/api/dev/configApi'
	import tool from '@/utils/tool'
	import { globalStore } from '@/store'
	import { useRoute } from 'vue-router'
	import { isEmpty } from 'lodash-es'
	const PhoneLoginForm = defineAsyncComponent(() => import('./phoneLoginForm.vue'))
	const EmailLoginForm = defineAsyncComponent(() => import('./emailLoginForm.vue'))
	const route = useRoute()
	const activeKey = ref('userAccount')
	const captchaOpen = ref(configData.SYS_BASE_CONFIG.SNOWY_SYS_DEFAULT_CAPTCHA_OPEN_FLAG_FOR_C)
	const registerOpen = ref('false')
	const phoneLogin = ref('false')
	const emailLogin = ref('false')
	const validCodeBase64 = ref('')
	const loading = ref(false)
	const tenSelectShow = ref(false)
	const tenOptions = ref([])
	const loginButtonDisable = ref(false)

	const ruleForm = reactive({
		validCode: '',
		validCodeReqNo: '',
		autologin: false,
		tenCode: ''
	})
	// 如果是开发环境，填充用户名密码
	if (process.env.NODE_ENV === 'development') {
		ruleForm.account = ''
		ruleForm.password = ''
	}

	const rules = reactive({
		account: [required('请输入账号', 'blur')],
		password: [required('请输入密码', 'blur')],
		tenCode: [required('请选择租户', 'blur')]
	})
	const config = ref({
		theme: tool.data.get('APP_THEME') || 'default'
	})

	const store = globalStore()

	const setSysBaseConfig = store.setSysBaseConfig

	const sysBaseConfig = computed(() => {
		return store.sysBaseConfig
	})

	onMounted(() => {
		getSysConfig()
	})
	// 查询这个租户的配置，租户code已经被放domain里了
	const getSysConfig = () => {
		let formData = ref(configData.SYS_BASE_CONFIG)
		configApi
			.configSysBaseList()
			.then((data) => {
				loginButtonDisable.value = false
				if (data) {
					data.forEach((item) => {
						formData.value[item.configKey] = item.configValue
					})
					captchaOpen.value = formData.value.SNOWY_SYS_DEFAULT_CAPTCHA_OPEN_FLAG_FOR_C
					registerOpen.value = formData.value.SNOWY_SYS_DEFAULT_ALLOW_REGISTER_FLAG_FOR_C
					phoneLogin.value = formData.value.SNOWY_SYS_DEFAULT_ALLOW_PHONE_LOGIN_FLAG_FOR_C
					emailLogin.value = formData.value.SNOWY_SYS_DEFAULT_ALLOW_EMAIL_LOGIN_FLAG_FOR_C
					tool.data.set('SNOWY_SYS_BASE_CONFIG', formData.value)
					setSysBaseConfig(formData.value)
					refreshSwitch()
				}
			})
			.catch(() => {})
	}
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
			rules.validCode = [required('请输入验证码', 'blur')]
		}
	}
	// 获取验证码
	const loginCaptcha = () => {
		clientLoginApi.clientGetPicCaptcha().then((data) => {
			validCodeBase64.value = data.validCodeBase64
			ruleForm.validCodeReqNo = data.validCodeReqNo
			// 如果有输入的将其清空
			ruleForm.validCode = undefined
		})
	}
	//登陆
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
				// 获取token
				try {
					const loginToken = await clientLoginApi.clientLogin(loginData)
					await afterLogin(loginToken)
				} catch (err) {
					if (captchaOpen.value === 'true') {
						loginCaptcha()
					}
				}
			})
			.finally(() => {
				loading.value = false
			})
	}
	// 租户选择事件
	const tenCodeChange = (code) => {
		// 设置新的
		tool.data.set('SNOWY_TEN_CODE', code)
		ruleForm.tenCode = code
		getSysConfig()
	}
</script>
<style lang="less" scoped>
	@import 'login';
</style>
