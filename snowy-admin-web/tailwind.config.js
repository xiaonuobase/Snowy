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
const generatePrimaryColors = () => {
	const result = {
		primary: `var(--primary-color)`
	}
	for (let i = 0; i < 10; i++) {
		result[`primary-${i}`] = `var(--primary-${i})`
	}
	return result
}

const generateFontSize = () => {
	const result = {}
	for (let i = 10; i < 32; i++) {
		result[i] = `${i}px`
	}
	return result
}

const colors = require('tailwindcss/colors')

const filterWarnColors = (colors) => {
	const result = {}
	for (const key in colors) {
		if (['lightBlue', 'warmGray', 'trueGray', 'coolGray', 'blueGray'].indexOf(key) === -1) {
			result[key] = colors[key]
		}
	}
	return result
}

module.exports = {
	content: ['./src/**/*.vue', './src/**/*.js'],
	darkMode: 'class', // or 'media' or 'class'
	corePlugins: {
		preflight: false
	},
	theme: {
		extend: {},
		colors: {
			transparent: 'transparent',
			current: 'currentColor',
			...filterWarnColors(colors),
			...generatePrimaryColors()
		},
		fontWeight: {
			1: 100,
			2: 200,
			3: 300,
			4: 400,
			5: 500,
			6: 600,
			7: 700,
			8: 800,
			9: 900
		},
		fontSize: {
			...generateFontSize()
		}
	},
	variants: {},
	plugins: []
}
