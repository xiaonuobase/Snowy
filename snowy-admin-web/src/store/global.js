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

// deprecated 请使用 useGlobalStore
export const globalStore = defineStore('global', () => {
	// 移动端布局
	const isMobile = ref(false)
	// 布局
	const layout = ref(getCacheConfig('SNOWY_LAYOUT'))
	// 菜单是否折叠 toggle
	const menuIsCollapse = ref(getCacheConfig('SNOWY_MENU_COLLAPSE'))
	// 侧边菜单是否排他展开
	const sideUniqueOpen = ref(getCacheConfig('SNOWY_SIDE_UNIQUE_OPEN'))
	// 多标签栏
	const layoutTagsOpen = ref(getCacheConfig('SNOWY_LAYOUT_TAGS_OPEN'))
	// 是否展示面包屑
	const breadcrumbOpen = ref(getCacheConfig('SNOWY_BREADCRUMB_OPEN'))
	// 是否开启固定宽度（顶栏菜单）
	const fixedWidth = ref(getCacheConfig('SNOWY_FIXEDWIDTH_OPEN'))
	// 顶栏是否应用主题色
	const topHeaderThemeColorOpen = ref(getCacheConfig('SNOWY_TOP_HEADER_THEME_COLOR_OPEN'))
	// 顶栏主题色通栏
	const topHeaderThemeColorSpread = ref(getCacheConfig('SNOWY_TOP_HEADER_THEME_COLOR_SPREAD'))
	// 登录用户水印
	const loginUserWatermarkOpen = ref(getCacheConfig('SNOWY_LOGIN_USER_WATERMARK_OPEN'))
	// 页脚版权信息
	const footerCopyrightOpen = ref(getCacheConfig('SNOWY_FOOTER_COPYRIGHT_OPEN'))
	// 模块坞
	const moduleUnfoldOpen = ref(getCacheConfig('SNOWY_MODULE_UNFOLD_OPEN'))
	// 主题
	const theme = ref(getCacheConfig('SNOWY_THEME'))
	// 主题颜色
	const themeColor = ref(toolDataGet('SNOWY_THEME_COLOR') || config.COLOR)
	// 圆角分格
	const roundedCornerStyleOpen = ref(getCacheConfig('SNOWY_ROUNDED_CORNER_STYLE_OPEN'))
	// 整体表单风格
	const formStyle = ref(getCacheConfig('SNOWY_FORM_STYLE'))
	// 用户信息
	const userInfo = ref(toolDataGet('USER_INFO') || {})
	// 系统配置
	const sysBaseConfig = ref(toolDataGet('SNOWY_SYS_BASE_CONFIG') || config.SYS_BASE_CONFIG)
	// 默认应用
	const module = ref(getCacheConfig('SNOWY_MENU_MODULE_ID'))

	// 定义action
	const setIsMobile = (key) => {
		isMobile.value = key
	}
	const setLayout = (key) => {
		layout.value = key
	}
	const setTheme = (key) => {
		theme.value = key
		changeColor(themeColor.value, key).then()
	}
	const setThemeColor = (key) => {
		themeColor.value = key
		changeColor(key, theme.value).then()
	}
	const initTheme = () => {
		changeColor(themeColor.value, theme.value).then()
	}
	const toggleConfig = (key) => {
		switch (key) {
			case 'menuIsCollapse':
				menuIsCollapse.value = !menuIsCollapse.value
				break
			case 'topHeaderThemeColorSpread':
				topHeaderThemeColorSpread.value = !topHeaderThemeColorSpread.value
				break
			case 'sideUniqueOpen':
				sideUniqueOpen.value = !sideUniqueOpen.value
				break
			case 'layoutTagsOpen':
				layoutTagsOpen.value = !layoutTagsOpen.value
				break
			case 'breadcrumbOpen':
				breadcrumbOpen.value = !breadcrumbOpen.value
				break
			case 'fixedWidth':
				fixedWidth.value = !fixedWidth.value
				break
			case 'topHeaderThemeColorOpen':
				topHeaderThemeColorOpen.value = !topHeaderThemeColorOpen.value
				topHeaderThemeColorSpread.value = topHeaderThemeColorOpen.value
					? topHeaderThemeColorSpread.value
					: topHeaderThemeColorOpen.value
				break
			case 'loginUserWatermarkOpen':
				loginUserWatermarkOpen.value = !loginUserWatermarkOpen.value
				break
			case 'footerCopyrightOpen':
				footerCopyrightOpen.value = !footerCopyrightOpen.value
				break
			case 'roundedCornerStyleOpen':
				roundedCornerStyleOpen.value = !roundedCornerStyleOpen.value
				break
			case 'moduleUnfoldOpen':
				moduleUnfoldOpen.value = !moduleUnfoldOpen.value
				break
		}
	}
	const setFormStyle = (key) => {
		formStyle.value = key
	}
	const setUserInfo = (key) => {
		userInfo.value = key
	}
	const setSysBaseConfig = (key) => {
		sysBaseConfig.value = key
	}
	const setModule = (key) => {
		module.value = key
	}
	return {
		isMobile,
		layout,
		menuIsCollapse,
		sideUniqueOpen,
		layoutTagsOpen,
		breadcrumbOpen,
		fixedWidth,
		topHeaderThemeColorOpen,
		topHeaderThemeColorSpread,
		loginUserWatermarkOpen,
		footerCopyrightOpen,
		moduleUnfoldOpen,
		theme,
		themeColor,
		roundedCornerStyleOpen,
		formStyle,
		userInfo,
		sysBaseConfig,
		module,
		setIsMobile,
		setLayout,
		setTheme,
		setThemeColor,
		initTheme,
		toggleConfig,
		setFormStyle,
		setUserInfo,
		setSysBaseConfig,
		setModule
	}
})

export const useGlobalStore = globalStore
