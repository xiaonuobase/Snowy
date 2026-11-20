// 暗黑模式配色：antd 令牌在这里配置，同名色值需与 src/style/realdark.less 顶部「表面色」保持一致
import { generate } from '@ant-design/colors'

// #rrggbb / #rgb 转成带透明度的 rgba
const hexToRgba = (hex, alpha) => {
	const full = hex.replace('#', '').replace(/^(.)(.)(.)$/, '$1$1$2$2$3$3')
	const [r, g, b] = [0, 2, 4].map((i) => parseInt(full.slice(i, i + 2), 16))
	return `rgba(${r}, ${g}, ${b}, ${alpha})`
}

export const darkPalettes = {
	// 柔和中性灰：层次靠明度区分，不带色偏
	soft: {
		layout: '#17181b',
		container: '#1e2023',
		elevated: '#26282c',
		border: '#3b3e44',
		borderSecondary: '#2c2f33',
		text: '#e5e7eb'
	},
	// 蓝灰：略带冷色调，对比更低
	slate: {
		layout: '#1c2128',
		container: '#22272e',
		elevated: '#2d333b',
		border: '#444c56',
		borderSecondary: '#373e47',
		text: '#cdd9e5'
	},
	// 深邃黑：接近原版纯黑，只统一框架色、降低文字刺眼度
	deep: {
		layout: '#0f0f10',
		container: '#18181a',
		elevated: '#222225',
		border: '#3a3a3e',
		borderSecondary: '#2a2a2e',
		text: '#ececec'
	}
}

export const darkPaletteName = 'soft'

// 生成暗黑模式下 a-config-provider 的 token 与组件令牌：侧栏、顶栏、菜单不再使用 antd 默认的深海蓝，菜单选中改为主色淡底
export const getDarkThemeConfig = (primaryColor, palette = darkPalettes[darkPaletteName]) => {
	const primaryText = generate(primaryColor, { theme: 'dark' })[7]
	const primaryBg = hexToRgba(primaryColor, 0.2)
	return {
		token: {
			colorBgBase: palette.layout,
			colorBgLayout: palette.layout,
			colorBgContainer: palette.container,
			colorBgElevated: palette.elevated,
			colorBorder: palette.border,
			colorBorderSecondary: palette.borderSecondary,
			colorTextBase: palette.text
		},
		components: {
			Layout: {
				colorBgHeader: palette.container,
				colorBgBody: palette.layout,
				colorBgTrigger: palette.elevated
			},
			Menu: {
				colorItemBg: palette.container,
				colorSubItemBg: palette.container,
				menuSubMenuBg: palette.elevated,
				colorItemBgSelected: primaryBg,
				colorItemTextSelected: primaryText,
				colorItemBgSelectedHorizontal: 'transparent',
				colorItemTextSelectedHorizontal: primaryText
			}
		}
	}
}
