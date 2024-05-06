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
import tool from '../utils/tool'
import config from '../config'
import { themeEnum } from '@/layout/enum/themeEnum'

const changeColor = (newPrimaryColor, theme, darkClass = 'snowy-theme-dark') => {
	return new Promise((resolve) => {
		const themeEleId = 'snowy-theme-var'
		const themeEle = document.querySelector(`#${themeEleId}`)
		if (themeEle && themeEle.parentNode) {
			themeEle.parentNode.removeChild(themeEle)
		}
		const isRealDark = theme === themeEnum.REAL_DARK
		if (newPrimaryColor) {
			const colors = generate(newPrimaryColor, isRealDark ? { theme: 'dark' } : {})
			const rootClass = isRealDark ? `.${darkClass}` : ':root'
			const styleElement = document.createElement('style')
			styleElement.id = themeEleId
			styleElement.setAttribute('type', 'text/css')
			styleElement.innerHTML = `${rootClass}{${colors
				.map((c, i) => {
					return `--primary-${i + 1}:${c};`
				})
				.concat([`--primary-color:${newPrimaryColor};`])
				.join('')}}`
			document.head.appendChild(styleElement)
		} else {
			document.body.removeAttribute('snowy-theme')
		}
		if (isRealDark) {
			document.body.classList.add(darkClass)
		} else {
			document.body.classList.remove(darkClass)
		}
		resolve()
	})
}

const loadLocalTheme = (localSetting) => {
	if (localSetting) {
		let { theme, themeColor } = localSetting
		themeColor = themeColor || config.COLOR
		theme = theme || config.THEME
		changeColor(themeColor, theme)
	}
}

/**
 * 获取本地保存的配置
 * @param loadTheme {boolean} 是否加载配置中的主题
 * @returns {Object}
 */
const getLocalSetting = (loadTheme) => {
	let localSetting = {}
	try {
		const theme = tool.data.get('SNOWY_THEME')
		const themeColor = tool.data.get('SNOWY_THEME_COLOR')
		localSetting = {
			theme,
			themeColor
		}
	} catch (e) {
		console.error(e)
	}
	if (loadTheme) {
		loadLocalTheme(localSetting)
	}
	return localSetting
}

export { loadLocalTheme, getLocalSetting, changeColor }
