<template>
	<div class="login_background">
		<div class="login_background_front"></div>
		<div class="login_main">
			<div class="login-form">
				<a-card>
					<div class="login-header">
						<h2>三方登录</h2>
					</div>
					<a-spin tip="正在登录中...">
						<div style="height: 300px">
							<a-skeleton />
						</div>
					</a-spin>
				</a-card>
			</div>
		</div>
	</div>
</template>

<script setup name="loginCallback">
	import { message } from 'ant-design-vue'
	import { nextTick } from 'vue'
	import tool from '@/utils/tool'
	import router from '@/router'
	import thirdApi from '@/api/auth/thirdApi'
	import loginApi from '@/api/auth/loginApi'
	import userCenterApi from '@/api/sys/userCenterApi'
	import dictApi from '@/api/dev/dictApi'
	import { onMounted } from 'vue'

	onMounted(() => {
		// 获取当前url
		const url = window.location.href
		let parameter = url.split('?')[1]
		if (!parameter) {
			// 这个界面无参数属于非法访问，跳转到登录界面让他登录去
			window.location.href = '/login'
		}
		const parameterArray = parameter.split('&')
		// 当然了，不可能只有一个参数
		if (!parameterArray) {
			window.location.href = '/login'
		}
		const parameterObject = {}
		// 遍历数组，拿到json对象
		for (let i = 0; i < parameterArray.length; i++) {
			parameterObject[parameterArray[i].split('=')[0]] = parameterArray[i].split('=')[1]
		}
		thirdApi
			.thirdCallback(parameterObject)
			.then((data) => {
				tool.data.set('TOKEN', data)
				// 获取登录的用户信息
				loginApi.getLoginUser().then((loginUser) => {
					tool.data.set('USER_INFO', loginUser)
				})
				userCenterApi.userLoginMenu().then((menu) => {
					const indexMenu = menu[0].children[0].path
					tool.data.set('MENU', menu)
					// 重置系统默认应用
					tool.data.set('SNOWY_MENU_MODULE_ID', menu[0].id)
					router.replace({
						path: indexMenu
					})
					message.success('登录成功')
					nextTick(() => {
						dictApi.dictTree().then((dictData) => {
							// 设置字典到store中
							tool.data.set('DICT_TYPE_TREE_DATA', dictData)
						})
					})
				})
			})
			.catch(() => {
				window.location.href = '/login'
			})
	})
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
