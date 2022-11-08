<template>
	<a-config-provider :locale="locale">
		<router-view></router-view>
	</a-config-provider>
</template>

<script setup name="App">
	import i18n from '@/locales'
	import store from '@/store'
	import config from '@/config'
	import configApi from '@/api/dev/configApi'
	import tool from '@/utils/tool'

	store.commit('initTheme')
	const locale = i18n.global.messages[i18n.global.locale].lang
	if (!tool.data.get('SNOWY_SYS_BASE_CONFIG')) {
		let formData = ref(config.SYS_BASE_CONFIG)
		configApi.configSysBaseList().then((data) => {
			if (data) {
				data.forEach((item) => {
					formData.value[item.configKey] = item.configValue
				})
				tool.data.set('SNOWY_SYS_BASE_CONFIG', formData.value)
				store.commit('SET_sysBaseConfig', formData.value)
			}
		})
	}
</script>
