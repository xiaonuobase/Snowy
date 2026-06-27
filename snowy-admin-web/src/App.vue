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
		<div :class="['app-wrapper', { 'gray-mode': grayModeOpen }]">
			<a-watermark
				:content="loginUserWatermarkOpen && userInfo ? [userInfo.name, userInfo.account] : undefined"
				class="admin-ui-main"
			>
				<router-view />
			</a-watermark>
		</div>
	</a-config-provider>
</template>

<script setup name="App">
	import { onUnmounted } from 'vue'
	import i18n from '@/locales'
	import { globalStore } from '@/store'
	import { theme } from 'ant-design-vue'
	const store = globalStore()
	store.initTheme()
	const locale = i18n.global.messages.value[i18n.global.locale.value].lang
	// 获取用户信息
	const userInfo = computed(() => store.userInfo)
	// 水印开关
	const loginUserWatermarkOpen = computed(() => store.loginUserWatermarkOpen)
	// 圆角风格
	const roundedCornerStyleOpen = computed(() => store.roundedCornerStyleOpen)
	// 灰色模式
	const grayModeOpen = computed(() => store.grayModeOpen)
	// 灰色模式类名
	const GRAY_MODE_CLASS = 'gray-mode'
	// 监听灰色模式，同步到 html 元素
	watch(
		grayModeOpen,
		(isEnabled) => {
			document.documentElement.classList.toggle(GRAY_MODE_CLASS, isEnabled)
		},
		{ immediate: true }
	)
	// 组件卸载时清理
	onUnmounted(() => {
		document.documentElement.classList.remove(GRAY_MODE_CLASS)
	})
</script>

<style lang="less" scoped>
	.app-wrapper {
		width: 100%;
		height: 100%;
	}
</style>
