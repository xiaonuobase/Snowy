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
						<h2>{{ $t('login.forgetPassword') }}</h2>
					</div>
					<a-tabs v-model:activeKey="activeKey">
						<a-tab-pane key="userPhone" :tab="$t('login.restPhoneType')">
							<phone-find-form />
						</a-tab-pane>
						<a-tab-pane key="userEmail" :tab="$t('login.restEmailType')" force-render>
							<email-find-form />
						</a-tab-pane>
					</a-tabs>
				</a-card>
			</div>
		</div>
	</div>
</template>

<script setup>
	import PhoneFindForm from './phoneFindForm.vue'
	import EmailFindForm from './emailFindForm.vue'
	import { globalStore } from '@/store'

	const store = globalStore()
	const activeKey = ref('userPhone')
	const sysBaseConfig = computed(() => {
		return store.sysBaseConfig
	})
	// logo链接
	const handleLink = (e) => {
		if (!sysBaseConfig.value.SNOWY_SYS_COPYRIGHT_URL) {
			e?.stopPropagation()
			e?.preventDefault()
		}
	}
</script>

<style lang="less" scoped>
	.login-wrapper {
		width: 100vw;
		height: 100vh;
		overflow: hidden;
		background-color: #fff;
		display: flex;
	}
	.login_background {
		width: 50%;
		height: 100%;
		overflow: hidden;
		background-size: cover;
		background-position: center;
		background-image: url(/img/login_background.png);
		position: relative;
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
		width: 50%;
		height: 100%;
		display: flex;
		justify-content: center;
	}
	.login-form {
		width: 450px;
		margin-top: 110px;
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
		margin-top: 10px;
	}
	.login-oauth {
		display: flex;
		justify-content: space-around;
	}
	.login_config {
		position: absolute;
		top: 20px;
		right: 20px;
	}
	.logo_background {
		position: absolute;
		left: 0;
		top: 50px;
		height: 60px;
		padding-left: 56px;
		width: 100%;
		background: -webkit-gradient(
			linear,
			right top,
			left top,
			from(rgba(67, 147, 250, 0.5)),
			to(rgba(133, 182, 252, 0.5))
		);
		background: linear-gradient(120deg, rgb(255 255 255 / 90%), rgba(255, 255, 255, 0));
		display: flex;
		align-items: center;
	}
	.logo_background img {
		height: 40px;
		margin-right: 10px;
	}
	.logo_background label {
		font-size: 24px;
		color: #fff;
	}
	.logo_background a {
		text-decoration: none;
		cursor: pointer;
		display: flex;
		align-items: center;
	}
	.logo_background a.no-link,
	.logo_background a.no-link label {
		cursor: default;
	}
	.logo_background a label {
		font-size: 24px;
		color: #fff;
		cursor: pointer;
	}
	.login_background .version {
		width: 100%;
		font-size: 14px;
		color: #fff;
		font-weight: 300;
		padding: 0 56px;
		box-sizing: border-box;
		position: absolute;
		bottom: 12px;
	}
	.login_background .version p {
		line-height: 22px;
		text-align: center;
		margin-bottom: 6px;
	}
	@media (max-width: 1200px) {
		.login-form {
			width: 340px;
		}
	}
	@media (max-width: 1000px) {
		.login_main {
			width: 100%;
			position: absolute;
			left: 0;
			right: 0;
		}
		.logo_background {
			padding-left: 40px;
		}
		.login-form {
			width: 100%;
			padding: 20px 40px;
			top: 15%;
		}
		.login_background .version {
			padding: 0 20px;
		}
		.login_background .version p:first-child {
			display: none;
		}
	}
</style>
