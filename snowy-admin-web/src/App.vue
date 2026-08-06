<template>
	<a-config-provider
		:locale="locale"
		:theme="{
			algorithm: store.theme === 'realDark' ? theme.darkAlgorithm : theme.defaultAlgorithm,
			token: {
				colorPrimary: `${store.themeColor}`,
				borderRadius: roundedCornerStyleOpen ? 6 : 2
			}
		}"
	>
		<div class="app-wrapper">
			<a-watermark
				:content="loginUserWatermarkOpen && userInfo ? [userInfo.name, userInfo.account] : undefined"
				class="admin-ui-main"
			>
				<router-view />
			</a-watermark>
		</div>
		<xn-lock-screen />
	</a-config-provider>
</template>

<script setup name="App">
	import i18n from '@/locales'
	import { globalStore } from '@/store'
	import { theme } from 'ant-design-vue'
	import { useAutoLock } from '@/hooks/useAutoLock'
	import { useGrayMode } from '@/hooks/useGrayMode'

	const store = globalStore()
	store.initTheme()
	const locale = i18n.global.messages.value[i18n.global.locale.value].lang
	// 获取用户信息
	const userInfo = computed(() => store.userInfo)
	// 水印开关
	const loginUserWatermarkOpen = computed(() => store.loginUserWatermarkOpen)
	// 圆角风格
	const roundedCornerStyleOpen = computed(() => store.roundedCornerStyleOpen)

	// 无操作自动锁屏，事件监听与计时逻辑都收在 hook 内
	useAutoLock()
	// 灰色模式，类名同步与清理都收在 hook 内
	useGrayMode()
</script>

<style lang="less" scoped>
	.app-wrapper {
		width: 100%;
		height: 100%;
	}
</style>
