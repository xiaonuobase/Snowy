<template>
	<div class="login_background">
		<div class="login_background_front"></div>
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
						<div class="logo">
							<img :alt="sysBaseConfig.SNOWY_SYS_NAME" :src="sysBaseConfig.SNOWY_SYS_LOGO" />
							<label>{{ sysBaseConfig.SNOWY_SYS_NAME }}</label>
						</div>
						<!--<h2>{{ $t('login.signInTitle') }}</h2>-->
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
								<a-form-item name="validCode" v-if="captchaOpen">
									<a-row :gutter="8">
										<a-col :span="17">
											<a-input
												v-model:value="ruleForm.validCode"
												:placeholder="$t('login.validLaceholder')"
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
									<a href="/findpwd" style="color: #0d84ff">{{ $t('login.forgetPassword') }}？</a>
								</a-form-item>
								<a-form-item>
									<a-button type="primary" class="w-full" :loading="loading" round size="large" @click="login"
										>{{ $t('login.signIn') }}
									</a-button>
								</a-form-item>
							</a-form>
						</a-tab-pane>
						<a-tab-pane key="userSms" :tab="$t('login.phoneSms')" force-render>
							<phone-login-form />
						</a-tab-pane>
					</a-tabs>
					<three-login />
				</a-card>
			</div>
		</div>
	</div>
</template>

<script>
	import loginApi from '@/api/auth/loginApi'
	import phoneLoginForm from './phoneLoginForm.vue'
	import threeLogin from './threeLogin.vue'
	import smCrypto from '@/utils/smCrypto'
	import { required } from '@/utils/formRules'
	import { afterLogin } from './util'

	export default {
		name: 'Login',
		components: {
			phoneLoginForm,
			threeLogin
		},

		data() {
			return {
				activeKey: 'userAccount',
				sysBaseConfig: this.$TOOL.data.get('SNOWY_SYS_BASE_CONFIG') || this.$store.state.global.sysBaseConfig,
				validCodeBase64: '',
				ruleForm: {
					account: 'superAdmin',
					password: '123456',
					validCode: '',
					validCodeReqNo: '',
					autologin: false
				},
				rules: {
					account: [required(this.$t('login.accountError'), 'blur')],
					password: [required(this.$t('login.PWError'), 'blur')]
				},
				loading: false,
				config: {
					lang: this.$TOOL.data.get('APP_LANG') || this.$CONFIG.LANG,
					theme: this.$TOOL.data.get('APP_THEME') || 'default'
				},
				lang: [
					{
						name: '简体中文',
						value: 'zh-cn'
					},
					{
						name: 'English',
						value: 'en'
					}
				]
			}
		},
		computed: {
			captchaOpen() {
				return this.sysBaseConfig.SNOWY_SYS_DEFAULT_CAPTCHA_OPEN === 'true'
			}
		},
		watch: {
			'config.theme': function (val) {
				document.body.setAttribute('data-theme', val)
			},
			'config.lang': function (val) {
				this.$i18n.locale = val
				this.$TOOL.data.set('APP_LANG', val)
			}
		},
		created() {
			this.$store.commit('clearViewTags')
			this.$store.commit('clearKeepLive')
			this.$store.commit('clearIframeList')
		},
		mounted() {
			this.refreshSwitch()
		},
		methods: {
			// 通过开关加载内容
			refreshSwitch() {
				// 判断是否开启验证码
				if (this.captchaOpen) {
					// 加载验证码
					this.loginCaptcha()
					// 加入校验
					this.rules.validCode = [required(this.$t('login.validError'), 'blur')]
				}
			},
			// 获取验证码
			loginCaptcha() {
				loginApi.getPicCaptcha().then((data) => {
					this.validCodeBase64 = data.validCodeBase64
					this.ruleForm.validCodeReqNo = data.validCodeReqNo
				})
			},
			// 用户名密码登录
			async login() {
				this.$refs.loginForm.validate().then(async () => {
					this.loading = true
					const loginData = {
						account: this.ruleForm.account,
						// 密码进行SM2加密，传输过程中看到的只有密文，后端存储使用hash
						password: smCrypto.doSm2Encrypt(this.ruleForm.password),
						validCode: this.ruleForm.validCode,
						validCodeReqNo: this.ruleForm.validCodeReqNo
					}
					// 获取token
					try {
						const loginToken = await loginApi.login(loginData)
						afterLogin(loginToken)
					} catch (err) {
						this.loading = false
					}
				})
			},
			configLang(key) {
				this.config.lang = key
			}
		}
	}
</script>

<style lang="less">
	@import 'login';
</style>
