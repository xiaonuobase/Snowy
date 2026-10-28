<template>
	<div ref="wrapRef" class="xn-watermark-wrap">
		<slot />
	</div>
</template>

<script setup name="XnWatermark">
	// 水印层的标记属性，水印被删、被改样式后按它找回并重建
	const MARK_ATTR = 'data-xn-watermark'
	// 定时自检间隔（毫秒），兜底处理 MutationObserver 观察不到的隐藏手段（如追加样式表）
	const CHECK_INTERVAL = 2000

	const props = defineProps({
		// 水印文字，数组按行显示
		content: {
			type: [String, Array],
			default: ''
		},
		// 字号
		fontSize: {
			type: Number,
			default: 15
		},
		// 文字颜色
		color: {
			type: String,
			default: 'rgba(0, 0, 0, 0.16)'
		},
		// 旋转角度
		rotate: {
			type: Number,
			default: -22
		},
		// 横向间距
		gapX: {
			type: Number,
			default: 120
		},
		// 纵向间距
		gapY: {
			type: Number,
			default: 100
		}
	})

	const wrapRef = ref()
	let markElement = null
	let backgroundUrl = ''
	let observer = null
	let checkTimer = null
	let rebuilding = false

	// 水印行
	const lines = computed(() => {
		const list = Array.isArray(props.content) ? props.content : [props.content]
		return list
			.filter((item) => item !== null && item !== undefined && String(item).trim() !== '')
			.map((item) => String(item))
	})

	// 用画布生成平铺用的水印图（两倍像素，高分屏不发虚）
	const renderBackground = () => {
		const ratio = Math.max(window.devicePixelRatio || 1, 2)
		const canvas = document.createElement('canvas')
		const context = canvas.getContext('2d')
		if (!context || lines.value.length === 0) {
			return ''
		}
		const font = `${props.fontSize * ratio}px "PingFang SC", "Microsoft YaHei", sans-serif`
		context.font = font
		const textWidth = Math.max(...lines.value.map((line) => context.measureText(line).width))
		const lineHeight = props.fontSize * ratio * 1.5
		const blockWidth = textWidth + props.gapX * ratio
		const blockHeight = lineHeight * lines.value.length + props.gapY * ratio
		canvas.width = blockWidth
		canvas.height = blockHeight
		context.font = font
		context.fillStyle = props.color
		context.textAlign = 'center'
		context.textBaseline = 'middle'
		context.translate(blockWidth / 2, blockHeight / 2)
		context.rotate((props.rotate * Math.PI) / 180)
		lines.value.forEach((line, index) => {
			context.fillText(line, 0, (index - (lines.value.length - 1) / 2) * lineHeight)
		})
		backgroundUrl = canvas.toDataURL()
		return `${blockWidth / ratio}px ${blockHeight / ratio}px`
	}

	// 水印层的内联样式，全部加 !important，页面或追加的样式表都盖不过
	const markStyle = (size) =>
		[
			'position:absolute !important',
			'inset:0 !important',
			'z-index:2147483000 !important',
			'pointer-events:none !important',
			'display:block !important',
			'visibility:visible !important',
			'opacity:1 !important',
			'transform:none !important',
			'clip-path:none !important',
			'filter:none !important',
			'width:auto !important',
			'height:auto !important',
			`background-image:url("${backgroundUrl}") !important`,
			'background-repeat:repeat !important',
			`background-size:${size} !important`
		].join(';')

	// 建（或重建）水印层，挂到容器最后
	const buildMark = () => {
		const wrap = wrapRef.value
		if (!wrap) return
		rebuilding = true
		if (markElement && markElement.parentNode) {
			markElement.parentNode.removeChild(markElement)
		}
		markElement = null
		if (lines.value.length > 0) {
			const size = renderBackground()
			if (backgroundUrl) {
				const element = document.createElement('div')
				element.setAttribute(MARK_ATTR, '1')
				element.setAttribute('style', markStyle(size))
				wrap.appendChild(element)
				markElement = element
			}
		}
		// 自己的改动不触发观察回调里的重建
		Promise.resolve().then(() => {
			rebuilding = false
		})
	}

	// 判断水印层是否还完好：在容器里、样式没被改、计算后仍可见
	const isMarkHealthy = () => {
		const wrap = wrapRef.value
		if (!wrap || lines.value.length === 0) return true
		if (!markElement || markElement.parentNode !== wrap || !markElement.hasAttribute(MARK_ATTR)) return false
		if (markElement.hidden || !String(markElement.getAttribute('style') || '').includes('background-image'))
			return false
		const style = window.getComputedStyle(markElement)
		if (style.display === 'none' || style.visibility !== 'visible' || Number(style.opacity) < 1) return false
		if (style.backgroundImage === 'none' || style.position !== 'absolute') return false
		// 容器本身被设成定位不对时水印会跑偏，顺带纠正
		if (window.getComputedStyle(wrap).position === 'static') {
			wrap.style.setProperty('position', 'relative', 'important')
		}
		return true
	}

	// 观察到删改水印层或改容器样式时立即重建
	const onMutate = (mutations) => {
		if (rebuilding) return
		const touched = mutations.some((mutation) => {
			if (mutation.type === 'childList') {
				return Array.from(mutation.removedNodes).some((node) => node === markElement)
			}
			return mutation.target === markElement || mutation.target === wrapRef.value
		})
		if (touched || !isMarkHealthy()) {
			buildMark()
		}
	}

	onMounted(() => {
		buildMark()
		observer = new MutationObserver(onMutate)
		observer.observe(wrapRef.value, { childList: true, subtree: true, attributes: true })
		checkTimer = setInterval(() => {
			if (!isMarkHealthy()) {
				buildMark()
			}
		}, CHECK_INTERVAL)
	})

	watch(
		() => [lines.value.join('\n'), props.fontSize, props.color, props.rotate, props.gapX, props.gapY],
		() => buildMark()
	)

	onBeforeUnmount(() => {
		if (observer) {
			observer.disconnect()
			observer = null
		}
		if (checkTimer) {
			clearInterval(checkTimer)
			checkTimer = null
		}
	})
</script>

<style lang="less" scoped>
	.xn-watermark-wrap {
		position: relative;
	}
</style>
