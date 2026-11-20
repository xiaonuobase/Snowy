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
import { generate } from '@ant-design/colors'
import config from '../config'
import { themeEnum } from '@/layout/enum/themeEnum'

const DARK_THEME_CLASS = 'snowy-theme-dark'
const THEME_STYLE_ID = 'snowy-theme-var'

const getPrimaryVariables = (primaryColor, options) =>
	generate(primaryColor, options)
		.map((color, index) => `--primary-${index + 1}:${color};`)
		.join('') + `--primary-color:${primaryColor};`

// 主色改变时同时生成亮色和暗黑色板，切换风格只需更新 body 类名
const changeColor = (primaryColor) => {
	let styleElement = document.getElementById(THEME_STYLE_ID)
	if (!primaryColor) {
		styleElement?.remove()
		document.body.removeAttribute('snowy-theme')
		return
	}

	const light = getPrimaryVariables(primaryColor)
	const dark = getPrimaryVariables(primaryColor, { theme: 'dark' })
	const cssText = `:root{${light}}.${DARK_THEME_CLASS}{${dark}}`
	if (!styleElement) {
		styleElement = document.createElement('style')
		styleElement.id = THEME_STYLE_ID
	}
	if (styleElement.textContent !== cssText) {
		styleElement.textContent = cssText
	}
	if (!styleElement.isConnected) {
		document.head.appendChild(styleElement)
	}
}

const setDarkTheme = (isRealDark) => {
	document.body.classList.toggle(DARK_THEME_CLASS, isRealDark)
}

let activeThemeTransition
const clearThemeTransition = (root, transition) => {
	if (activeThemeTransition?.transition === transition) {
		activeThemeTransition = undefined
		root.removeAttribute('data-snowy-theme-transition')
		root.style.removeProperty('--snowy-theme-transition-origin')
		root.style.removeProperty('--snowy-theme-transition-radius')
	}
}

const switchThemeWithTransition = (currentTheme, targetTheme, event, apply) => {
	if (activeThemeTransition?.targetTheme === targetTheme) {
		return
	}

	const root = document.documentElement
	if (activeThemeTransition) {
		// 连续点选时立即应用最后一次选择，避免连续截取整页
		const { transition } = activeThemeTransition
		transition.skipTransition()
		clearThemeTransition(root, transition)
		return currentTheme === targetTheme ? undefined : apply()
	}
	if (currentTheme === targetTheme) {
		return
	}
	if (!document.startViewTransition || window.matchMedia('(prefers-reduced-motion: reduce)').matches) {
		return apply()
	}

	const { innerWidth: width, innerHeight: height } = window
	const x = event?.detail ? event.clientX : width / 2
	const y = event?.detail ? event.clientY : height / 2
	const radius = Math.ceil(Math.hypot(Math.max(x, width - x), Math.max(y, height - y)))
	const reverse =
		currentTheme === themeEnum.REAL_DARK || (currentTheme === themeEnum.DARK && targetTheme === themeEnum.LIGHT)

	root.style.setProperty('--snowy-theme-transition-origin', `${x}px ${y}px`)
	root.style.setProperty('--snowy-theme-transition-radius', `${radius}px`)
	root.setAttribute('data-snowy-theme-transition', reverse ? 'collapse' : 'expand')

	const transition = document.startViewTransition(() => {
		if (activeThemeTransition?.transition === transition) {
			apply()
			return nextTick()
		}
	})
	activeThemeTransition = { transition, targetTheme }
	// 浏览器跳过过渡时 ready 会拒绝，主题仍由更新回调提交
	transition.ready.catch(() => {})
	const cleanup = () => clearThemeTransition(root, transition)
	transition.finished.then(cleanup, cleanup)
	return transition.updateCallbackDone
}

const loadLocalTheme = (localSetting) => {
	if (localSetting) {
		const { theme, themeColor } = localSetting
		changeColor(themeColor || config.COLOR)
		setDarkTheme((theme || config.SNOWY_THEME) === themeEnum.REAL_DARK)
	}
}

export { loadLocalTheme, changeColor, setDarkTheme, switchThemeWithTransition }
