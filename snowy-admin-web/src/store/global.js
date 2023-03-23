import { defineStore } from 'pinia'
import { changeColor } from '@/utils/themeUtil'
import config from '@/config'
import { message } from 'ant-design-vue'
import tool from '@/utils/tool'

const toolDataGet = (key) => {
	return tool.data.get(key)
}

// 获取缓存中的，如果取不到那就用配置的
const getCacheConfig = (value) => {
	const data = toolDataGet(value)
	if (data === null) {
		return config[value]
	}
	return data
}

export const globalStore = defineStore({
	id: 'global',
	state: () => ({
		// 移动端布局
		ismobile: false,
		// 布局
		layout: getCacheConfig('SNOWY_LAYOUT'),
		// 菜单是否折叠 toggle
		menuIsCollapse: getCacheConfig('SNOWY_MENU_COLLAPSE'),
		// 侧边菜单是否排他展开
		sideUniqueOpen: getCacheConfig('SNOWY_SIDE_UNIQUE_OPEN'),
		// 多标签栏
		layoutTagsOpen: getCacheConfig('SNOWY_LAYOUT_TAGS_OPEN'),
		// 是否展示面包屑
		breadcrumbOpen: getCacheConfig('SNOWY_BREADCRUMD_OPEN'),
		// 顶栏是否应用主题色
		topHanderThemeColorOpen: getCacheConfig('SNOWY_TOP_HANDER_THEME_COLOR_OPEN'),
		// 顶栏主题色通栏
		topHanderThemeColorSpread: getCacheConfig('SNOWY_TOP_HANDER_THEME_COLOR_SPREAD'),
		// 模块坞
		moduleUnfoldOpen: getCacheConfig('SNOWY_MODULE_UNFOLD_OPEN'),
		// 主题
		theme: getCacheConfig('SNOWY_THEME'),
		// 主题颜色
		themeColor: toolDataGet('SNOWY_THEME_COLOR') || config.COLOR,
		// 用户信息
		userInfo: toolDataGet('USER_INFO') || {},
		// 系统配置
		sysBaseConfig: toolDataGet('SNOWY_SYS_BASE_CONFIG') || config.SYS_BASE_CONFIG
	}),
	getters: {},
	actions: {
		setIsmobile(key) {
			this.ismobile = key
		},
		setLayout(key) {
			this.layout = key
		},
		setTheme(key) {
			this.theme = key
			const closeMessage = message.loading(`加载中...`)
			changeColor(this.themeColor, key).then(closeMessage)
		},
		setThemeColor(key) {
			this.themeColor = key
			const closeMessage = message.loading(`加载中...`)
			changeColor(key, this.theme).then(closeMessage)
		},
		initTheme() {
			const closeMessage = message.loading(`加载中...`)
			changeColor(this.themeColor, this.theme).then(closeMessage)
		},
		toggleConfig(key) {
			this[key] = !this[key]
		},
		toggle_SideUniqueOpen() {
			this.sideUniqueOpen = !this.sideUniqueOpen
		},
		toggle_LayoutTagsOpen() {
			this.layoutTagsOpen = !this.layoutTagsOpen
		},
		toggle_BreadcrumbOpen() {
			this.breadcrumbOpen = !this.breadcrumbOpen
		},
		toggle_TopHanderThemeColorOpen() {
			this.topHanderThemeColorOpen = !this.topHanderThemeColorOpen
		},
		toggle_TopHanderThemeColorSpread() {
			this.topHanderThemeColorSpread = !this.topHanderThemeColorSpread
		},
		toggle_ModuleUnfoldOpen() {
			this.moduleUnfoldOpen = !this.moduleUnfoldOpen
		},
		setUserInfo(key) {
			this.userInfo = key
		},
		setSysBaseConfig(key) {
			this.sysBaseConfig = key
		}
	}
})
