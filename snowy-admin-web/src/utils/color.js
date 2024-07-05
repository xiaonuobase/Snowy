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
/* eslint-disable eqeqeq */
export default {
	// 加深
	darken(color, level) {
		const rgbc = this.hexToRgb(color)
		for (let i = 0; i < 3; i++) rgbc[i] = Math.floor(rgbc[i] * (1 - level))
		return this.rgbToHex(rgbc[0], rgbc[1], rgbc[2])
	},
	// 变淡
	lighten(color, level) {
		const rgbc = this.hexToRgb(color)
		for (let i = 0; i < 3; i++) rgbc[i] = Math.floor((255 - rgbc[i]) * level + rgbc[i])
		return this.rgbToHex(rgbc[0], rgbc[1], rgbc[2])
	},
	// rgb颜色转hex颜色
	rgbToHex(rgb) {
		const bg = rgb.match(/^rgb\(\s*(\d+)\s*,\s*(\d+)\s*,\s*(\d+)\s*\)$/)
		// 返回空字符串
		if (!bg) {
			return ''
		}
		return '#' + toHex(bg[1]) + toHex(bg[2]) + toHex(bg[3])
	},
	// hex颜色转rgb颜色
	hexToRgb(hex) {
		// 去除开头 #
		if (hex.startsWith('#')) {
			hex = hex.substring(1)
		}
		// 如果当前传入的是 3 位小数值，直接转换为 6 位进行处理
		if (hex.length === 3) {
			hex = [hex[0], hex[0], hex[1], hex[1], hex[2], hex[2]].join('')
		}
		if (hex.length !== 6) {
			throw new Error('invalid hex:' + hex)
		}
		const r = parseInt(hex.slice(0, 2), 16)
		const g = parseInt(hex.slice(2, 4), 16)
		const b = parseInt(hex.slice(4, 6), 16)
		if ([r, g, b].some((x) => Number.isNaN(x))) {
			throw new Error('invalid hex:' + hex)
		}
		return [r, g, b]
	},
	// 透明度
	alpha(color, alpha = 1) {
		let hex = color.length > 7 ? color.rgbToHex(color) : color
		const { r, g, b } = color.hexToRgb(hex)
		return `rgba(${r}, ${g}, ${b}, ${alpha})`
	}
}

// 转Hex
const toHex = (x) => ('0' + parseInt(x).toString(16)).slice(-2)
