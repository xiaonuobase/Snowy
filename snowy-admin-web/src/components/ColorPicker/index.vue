<template>
	<div class="snowy-color-picker" :id="id" @click="forceResize">
		<color-picker
			v-bind="$attrs"
			:format="props.format"
			:shape="props.shape"
			:disable-alpha="props.disableAlpha"
			:useType="props.useType"
			:pureColor="props.value"
			@update:pureColor="update"
			@update:gradientColor="updateGradient"
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
		useType: {
			type: String,
			default: 'pure',
			validator: (val) => {
				return ['pure', 'gradient', 'both'].includes(val)
			}
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

	const updateGradient = (val) => {
		if (props.useType === 'both' || props.useType === 'gradient') {
			showTxt(val)
			emit('update:value', val)
		}
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
				// 检查是否为渐变色
				if (val && val.includes('gradient')) {
					// 渐变色使用白色文字
					rgb = [255, 255, 255]
					currentColor.style.color = '#fff'
					return
				}
				if (backgroundColor && backgroundColor !== 'initial') {
					if (backgroundColor.startsWith('rgba')) {
						rgb = [0, 0, 0]
					} else if (backgroundColor.startsWith('rgb')) {
						rgb = color.hexToRgb(color.rgbToHex(backgroundColor))
					} else if (backgroundColor.startsWith('#')) {
						rgb = color.hexToRgb(backgroundColor)
					} else {
						// 默认使用黑色文字
						rgb = [0, 0, 0]
					}
					currentColor.style.color = rgb[0] * 0.299 + rgb[1] * 0.587 + rgb[2] * 0.114 > 186 ? '#000' : '#fff'
				} else {
					// 默认使用黑色文字
					currentColor.style.color = '#000'
				}
			}
		}
		// 底部：赋值
		else {
			const currentColor = document.querySelector('#' + id + ' > .remark')
			if (currentColor) {
				currentColor.textContent = val
			}
		}
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
