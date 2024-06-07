<template>
	<div class="snowy-color-picker" :id="id" @click="forceResize">
		<color-picker
			v-bind="$attrs"
			:format="props.format"
			:shape="props.shape"
			:disable-alpha="props.disableAlpha"
			:pureColor="props.value"
			@update:pureColor="update"
		/>
		<div class="remark"></div>
	</div>
</template>

<script setup>
	import { ColorPicker } from 'vue3-colorpicker'
	import 'vue3-colorpicker/style.css'
	import tool from '@/utils/tool'
	import color from '@/utils/color'
	const emit = defineEmits(['update:value'])
	const props = defineProps({
		value: {
			type: String,
			default: '#1677FF'
		},
		valuePosition: {
			type: String,
			default: 'inline',
			validator: (val) => {
				return ['inline', 'bottom'].includes(val)
			}
		},
		format: {
			type: String,
			default: 'hex',
			validator: (val) => {
				return ['hex', 'rgb'].includes(val)
			}
		},
		shape: {
			type: String,
			default: 'square',
			validator: (val) => {
				return ['circle', 'square'].includes(val)
			}
		},
		disableAlpha: {
			type: Boolean,
			default: true
		}
	})
	const id = tool.snowyUuid()
	const forceResize = () => {
		window.dispatchEvent(new Event('resize'))
	}
	const update = (val) => {
		showTxt(val)
		emit('update:value', val)
	}
	onMounted(() => {
		showTxt(props.value)
	})
	const showTxt = (val) => {
		// 内联：赋值
		if (props.valuePosition === 'inline') {
			const currentColor = document.querySelector('#' + id + ' > .vc-color-wrap > .current-color')
			if (currentColor) {
				currentColor.textContent = val
				// 根据背景色 适配 文字颜色
				let rgb
				const backgroundColor = currentColor.style.backgroundColor
				if (backgroundColor.startsWith('rgba')) {
					rgb = [0, 0, 0]
				} else if (backgroundColor.startsWith('rgb')) {
					rgb = color.hexToRgb(color.rgbToHex(backgroundColor))
				} else {
					rgb = color.hexToRgb(backgroundColor)
				}
				currentColor.style.color = rgb[0] * 0.299 + rgb[1] * 0.587 + rgb[2] * 0.114 > 186 ? '#000' : '#fff'
			}
		}
		// 底部：赋值
		else {
			const currentColor = document.querySelector('#' + id + ' > .remark')
			if (currentColor) {
				currentColor.textContent = val
			}
		}
		// 改变文字颜色
		currentColor.style.color = changeTextColor(currentColor.style.backgroundColor)
	}
	// 改变文字颜色 backgroundColor背景色
	const changeTextColor = (backgroundColor) => {
		const backgroundHexColor = backgroundColor.length > 7 ? convertRGBToHex(backgroundColor) : backgroundColor
		let hex = backgroundHexColor
		// 如果当前传入的参数以 # 开头,去除当前的
		if (hex.startsWith('#')) {
			hex = hex.substring(1)
		}
		// 如果当前传入的是 3 位小数值，直接转换为 6 位进行处理
		if (hex.length === 3) {
			hex = [hex[0], hex[0], hex[1], hex[1], hex[2], hex[2]].join('')
		}
		if (hex.length !== 6) {
			throw new Error('Invalid background color.' + backgroundHexColor)
		}
		const r = parseInt(hex.slice(0, 2), 16)
		const g = parseInt(hex.slice(2, 4), 16)
		const b = parseInt(hex.slice(4, 6), 16)
		if ([r, g, b].some((x) => Number.isNaN(x))) {
			throw new Error('Invalid background color.' + backgroundHexColor)
		}
		return r * 0.299 + g * 0.587 + b * 0.114 > 186 ? '#000' : '#fff'
	}
	const toHex = (x) => ('0' + parseInt(x).toString(16)).slice(-2)
	// RGB 转换为 HEX
	const convertRGBToHex = (rgb) => {
		const bg = rgb.match(/^rgb\(\s*(\d+)\s*,\s*(\d+)\s*,\s*(\d+)\s*\)$/)
		// 返回空字符串，在后面判断长度为 6 时候会报错。不在此处进行操作
		if (!bg) {
			return ''
		}
		return '#' + toHex(bg[1]) + toHex(bg[2]) + toHex(bg[3])
	}
</script>

<style lang="less">
	.snowy-color-picker {
		width: 100%;
		.vc-color-wrap {
			width: 100%;
		}
		.current-color {
			padding: 0 10px;
		}
		.remark {
			font-size: 12px;
			color: #6c6c6c;
			padding: 0 2px;
		}
	}
</style>
