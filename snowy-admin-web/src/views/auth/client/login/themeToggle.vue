<template>
	<a-button type="text" shape="circle" :aria-label="label" :title="label" @click="toggleTheme">
		<template #icon>
			<AntIcon :view-box="store.isRealDark ? '1 1 22 22' : '2 2 20 20'" aria-hidden="true">
				<g fill="none" stroke="currentColor" stroke-width="1.75" stroke-linecap="round" stroke-linejoin="round">
					<template v-if="store.isRealDark">
						<circle cx="12" cy="12" r="4" />
						<path d="M12 2v2m0 16v2M2 12h2m16 0h2M5 5l1.5 1.5m11 11L19 19M19 5l-1.5 1.5m-11 11L5 19" />
					</template>
					<path v-else d="M21 12a9 9 0 1 1-9-9a7 7 0 0 0 9 9Z" />
				</g>
			</AntIcon>
		</template>
	</a-button>
</template>

<script setup name="themeToggle">
	import AntIcon from '@ant-design/icons-vue'
	import { useI18n } from 'vue-i18n'
	import { themeEnum } from '@/layout/enum/themeEnum'
	import { globalStore } from '@/store'
	import tool from '@/utils/tool'
	import { switchThemeWithTransition } from '@/utils/themeUtil'

	const store = globalStore()
	const { t } = useI18n()
	const label = computed(() => t(store.isRealDark ? 'login.switchToLight' : 'login.switchToDark'))

	const toggleTheme = (event) => {
		const targetTheme = store.isRealDark ? themeEnum.LIGHT : themeEnum.REAL_DARK
		return switchThemeWithTransition(store.theme, targetTheme, event, () => {
			store.setTheme(targetTheme)
			tool.data.set('SNOWY_THEME', targetTheme)
		})
	}
</script>
