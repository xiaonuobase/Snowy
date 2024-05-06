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
		<a-watermark
			:content="loginUserWatermarkOpen && userInfo ? [userInfo.name, userInfo.account] : undefined"
			class="admin-ui-main"
		>
			<router-view />
		</a-watermark>
	</a-config-provider>
</template>

<script setup name="App">
	import i18n from '@/locales'
	import { globalStore } from '@/store'
	import { theme } from 'ant-design-vue'
	const store = globalStore()
	store.initTheme()
	const locale = i18n.global.messages.value[i18n.global.locale.value].lang
	// 获取用户信息
	const userInfo = computed(() => {
		return store.userInfo
	})
	// 水印开关
	const loginUserWatermarkOpen = computed(() => {
		return store.loginUserWatermarkOpen
	})
	// 圆角风格
	const roundedCornerStyleOpen = computed(() => {
		return store.roundedCornerStyleOpen
	})
</script>
