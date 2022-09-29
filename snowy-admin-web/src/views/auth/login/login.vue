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
									<a-input v-model:value="ruleForm.account" :placeholder="$t('login.accountPlaceholder')" size="large">
										<template #prefix>
											<UserOutlined style="color: rgba(0, 0, 0, 0.25)" />
										</template>
									</a-input>
								</a-form-item>
								<a-form-item name="password">
									<a-input-password
										v-model:value="ruleForm.password"
										:placeholder="$t('login.PWPlaceholder')"
										size="large"
										autocomplete="off"
									>
										<template #prefix>
											<LockOutlined style="color: rgba(0, 0, 0, 0.25)" />
										</template>
									</a-input-password>
								</a-form-item>
								<a-form-item name="validCode" v-if="sysBaseConfig.SNOWY_SYS_DEFAULT_CAPTCHA_OPEN === 'true'">
									<a-row :gutter="8">
										<a-col :span="17">
											<a-input
												v-model:value="ruleForm.validCode"
												:placeholder="$t('login.validLaceholder')"
												size="large"
											>
												<template #prefix>
													<verified-outlined style="color: rgba(0, 0, 0, 0.25)" />
												</template>
											</a-input>
										</a-col>
										<a-col :span="7">
											<img
												:src="validCodeBase64"
												style="border: 1px solid var(--border-color-split); cursor: pointer; width: 100%; height: 40px"
												@click="loginCaptcha"
											/>
										</a-col>
									</a-row>
								</a-form-item>

								<a-form-item>
									<a href="/findpwd" style="color: #0d84ff">{{ $t('login.forgetPassword') }}？</a>
								</a-form-item>
								<a-form-item>
									<a-button type="primary" style="width: 100%" :loading="islogin" round size="large" @click="login"
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
	import userCenterApi from '@/api/sys/userCenterApi'
	import dictApi from '@/api/dev/dictApi'
	import phoneLoginForm from './phoneLoginForm.vue'
	import threeLogin from './threeLogin.vue'
	import smCrypto from '@/utils/smCrypto'

	export default {
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
					account: [{ required: true, message: this.$t('login.accountError'), trigger: 'blur' }],
					password: [{ required: true, message: this.$t('login.PWError'), trigger: 'blur' }]
				},
				islogin: false,
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
			sysBaseConfigWatch() {
				return this.$store.state.global.sysBaseConfig
			}
		},
		watch: {
			'config.theme': function (val) {
				document.body.setAttribute('data-theme', val)
			},
			'config.lang': function (val) {
				this.$i18n.locale = val
				this.$TOOL.data.set('APP_LANG', val)
			},
			sysBaseConfigWatch(val) {
				this.sysBaseConfig = val
				this.refreshSwitch()
			}
		},
		created() {
			this.$store.commit('clearViewTags')
			this.$store.commit('clearKeepLive')
			this.$store.commit('clearIframeList')
		},
		mounted() {
			this.refreshSwitch()
			// 获取回车键事件
			document.onkeydown = (e) => {
				if (e.defaultPrevented) {
					return;
				}
				const body = document.getElementsByTagName('body')[0];
				// match(浏览器中的地址，不需要包括https、http或者www)，是为了防止其他页面触发
				if (e.keyCode === 13 && e.target.baseURI.match("/login") && e.target === body) {
					this.login()
				}
			}
		},
		methods: {
			// 通过开关加载内容
			refreshSwitch() {
				// 判断是否开启验证码
				if (this.sysBaseConfig.SNOWY_SYS_DEFAULT_CAPTCHA_OPEN === 'true') {
					// 加载验证码
					this.loginCaptcha()
					// 加入校验
					this.rules.validCode = [{ required: true, message: this.$t('login.validError'), trigger: 'blur' }]
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
				const validate = await this.$refs.loginForm.validate().catch(() => {})
				if (!validate) return false

				this.islogin = true
				const loginData = {
					account: this.ruleForm.account,
					// 密码进行SM2加密，传输过程中看到的只有密文，后端存储使用hash
					password: smCrypto.doSm2Encrypt(this.ruleForm.password),
					validCode: this.ruleForm.validCode,
					validCodeReqNo: this.ruleForm.validCodeReqNo
				}
				// 获取token
				const login = await loginApi.login(loginData).finally(() => {
					this.islogin = false
				})
				this.$TOOL.data.set('TOKEN', login)
				// 获取登录的用户信息
				const loginUser = await loginApi.getLoginUser()
				this.$TOOL.data.set('USER_INFO', loginUser)

				// 获取用户的菜单
				const menu = await userCenterApi.userLoginMenu().catch(() => {
					this.islogin = false
					return
				})
				this.islogin = false
				const indexMenu = menu[0].children[0].path
				this.$TOOL.data.set('MENU', menu)
				// 重置系统默认应用
				this.$TOOL.data.set('SNOWY_MENU_MODULE_ID', menu[0].id)
				this.$router.replace({
					path: indexMenu
				})
				this.$message.success('登录成功')
				this.$nextTick(() => {
					dictApi.dictTree().then((data) => {
						// 设置字典到store中
						this.$TOOL.data.set('DICT_TYPE_TREE_DATA', data)
					})
				})
			},
			configLang(key) {
				this.config.lang = key
			}
		}
	}
</script>

<style lang="less" scoped>
	.login_background {
		width: 100%;
		height: 100%;
		overflow: hidden;
		background-size: cover;
		background-position: center;
		background-image: url(/img/login_background.png);
	}
	.login_background_front {
		width: 450px;
		height: 450px;
		margin-left: 100px;
		margin-top: 15%;
		overflow: hidden;
		/*position: relative;*/
		background-size: cover;
		background-position: center;
		background-image: url(/img/login_background_front.png);
		animation-name: myfirst;
		animation-duration: 5s;
		animation-timing-function: linear;
		animation-delay: 1s;
		animation-iteration-count: infinite;
		animation-direction: alternate;
		animation-play-state: running;
		/* Safari and Chrome: */
		-webkit-animation-name: myfirst;
		-webkit-animation-duration: 5s;
		-webkit-animation-timing-function: linear;
		-webkit-animation-delay: 1s;
		-webkit-animation-iteration-count: infinite;
		-webkit-animation-direction: alternate;
		-webkit-animation-play-state: running;
	}
	@keyframes myfirst {
		0% {
			left: 0px;
			top: 0px;
		}
		50% {
			left: 50px;
			top: 0px;
		}
		100% {
			left: 0px;
			top: 0px;
		}
	}
	@-webkit-keyframes myfirst /* Safari and Chrome */ {
		0% {
			left: 0px;
			top: 0px;
		}
		50% {
			left: 50px;
			top: 0px;
		}
		100% {
			left: 0px;
			top: 0px;
		}
	}
	.login_adv__title h2 {
		font-size: 40px;
	}
	.login_adv__title h4 {
		font-size: 18px;
		margin-top: 10px;
		font-weight: normal;
	}
	.login_adv__title p {
		font-size: 14px;
		margin-top: 10px;
		line-height: 1.8;
		color: rgba(255, 255, 255, 0.6);
	}
	.login_adv__title div {
		margin-top: 10px;
		display: flex;
		align-items: center;
	}
	.login_adv__title div span {
		margin-right: 15px;
	}
	.login_adv__title div i {
		font-size: 40px;
	}
	.login_adv__title div i.add {
		font-size: 20px;
		color: rgba(255, 255, 255, 0.6);
	}
	/*background-image:linear-gradient(transparent, #000);*/
	.login_main {
		flex: 1;
		overflow: auto;
		display: flex;
	}
	.login-form {
		top: 15%;
		right: 15%;
		position: absolute;
		width: 450px;
		margin-left: 10%;
		margin-top: 20px;
		padding: 10px 0;
	}
	.login-header {
		margin-bottom: 20px;
	}
	.login-header .logo {
		display: flex;
		align-items: center;
	}
	.login-header .logo img {
		width: 35px;
		height: 35px;
		vertical-align: bottom;
		margin-right: 10px;
	}
	.login-header .logo label {
		font-size: 24px;
	}
	.login-header h2 {
		font-size: 24px;
		font-weight: bold;
		margin-top: 40px;
	}
	.login_config {
		position: absolute;
		top: 20px;
		right: 20px;
	}
	@media (max-width: 1200px) {
		.login-form {
			width: 340px;
		}
	}
	@media (max-width: 1000px) {
		.login_main {
			display: block;
		}
		.login_background_front {
			display: none;
		}
		.login-form {
			width: 100%;
			padding: 20px 40px;
			right: 0 !important;
			top: 0 !important;
		}
	}
</style>
