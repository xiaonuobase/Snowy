/**
 *  Copyright [2022] [https://www.xiaonuo.vip]
 *	Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：
 *	1.请不要删除和修改根目录下的LICENSE文件。
 *	2.请不要删除和修改Snowy源码头部的版权声明。
 *	3.本项目代码可免费商业使用，商业使用请保留源码和相关描述文件的项目出处，作者声明等。
 *	4.分发源码时候，请注明软件出处 https://www.xiaonuo.vip
 *	5.不可二次分发开源参与同类竞品，如有想法可联系团队xiaonuobase@qq.com商议合作。
 *	6.若您的项目无法满足以上几点，需要更多功能代码，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
import { changeColor, getLocalSetting } from '@/utils/themeUtil'
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
export default {
	state: {
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
		topHanderThemeColorSpread:
			getCacheConfig('SNOWY_TOP_HANDER_THEME_COLOR_SPREAD'),
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
	},
	mutations: {
		SET_ismobile(state, key) {
			state.ismobile = key
		},
		SET_layout(state, key) {
			state.layout = key
		},
		SET_theme(state, key) {
			state.theme = key
			const closeMessage = message.loading(`加载中...`)
			changeColor(state.themeColor, key).then(closeMessage)
		},
		SET_themeColor(state, key) {
			state.themeColor = key
			const closeMessage = message.loading(`加载中...`)
			changeColor(key, state.theme).then(closeMessage)
		},
		initTheme(state) {
			const closeMessage = message.loading(`加载中...`)
			changeColor(state.themeColor, state.theme).then(closeMessage)
		},
		TOGGLE_menuIsCollapse(state) {
			state.menuIsCollapse = !state.menuIsCollapse
		},
		TOGGLE_sideUniqueOpen(state) {
			state.sideUniqueOpen = !state.sideUniqueOpen
		},
		TOGGLE_layoutTagsOpen(state) {
			state.layoutTagsOpen = !state.layoutTagsOpen
		},
		TOGGLE_breadcrumbOpen(state) {
			state.breadcrumbOpen = !state.breadcrumbOpen
		},
		TOGGLE_topHanderThemeColorOpen(state) {
			state.topHanderThemeColorOpen = !state.topHanderThemeColorOpen
		},
		TOGGLE_topHanderThemeColorSpread(state) {
			state.topHanderThemeColorSpread = !state.topHanderThemeColorSpread
		},
		TOGGLE_moduleUnfoldOpen(state) {
			state.moduleUnfoldOpen = !state.moduleUnfoldOpen
		},
		SET_userInfo(state, key) {
			state.userInfo = key
		},
		SET_sysBaseConfig(state, key) {
			state.sysBaseConfig = key
		}
	}
}
