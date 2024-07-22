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
						<h2>三方登录</h2>
					</div>
					<a-spin tip="正在登录中...">
						<div class="h-[300px]">
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
	import tool from '@/utils/tool'
	import router from '@/router'
	import thirdApi from '@/api/auth/thirdApi'
	import loginApi from '@/api/auth/loginApi'
	import userCenterApi from '@/api/sys/userCenterApi'
	import dictApi from '@/api/dev/dictApi'
	import { globalStore } from '@/store'

	const store = globalStore()
	const sysBaseConfig = computed(() => {
		return store.sysBaseConfig
	})

	onMounted(() => {
		// 获取当前url
		const url = new URL(window.location.href)
		let argLength = 0
		const params = {}
		url.searchParams.forEach((value, key) => {
			argLength += 1
			params[key] = value
		})
		// 当然了，不可能只有一个参数
		if (argLength < 2) {
			window.location.href = '/login'
			return
		}

		thirdApi
			.thirdCallback(params)
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
					dictApi.dictTree().then((dictData) => {
						// 设置字典到store中
						tool.data.set('DICT_TYPE_TREE_DATA', dictData)
					})
				})
			})
			.catch(() => {
				window.location.href = '/login'
			})
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
	@import 'login';
</style>
