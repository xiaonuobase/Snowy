<template>
	<a-modal
		v-bind="$attrs"
		:open="isOpen"
		:width="modalWidth"
		:wrap-class-name="`${wrapClassName} ${fullscreenClass}`"
		:bodyStyle="calcBodyStyle"
		@cancel="handleCancel"
	>
		<template #title>
			<div class="flex justify-between items-center">
				<div
					ref="modalTitleRef"
					:style="{
						flex: 1,
						overflow: 'hidden',
						whiteSpace: 'nowrap',
						textOverflow: 'ellipsis',
						cursor: isFullscreen ? 'default' : draggable ? 'move' : 'default',
						userSelect: 'none'
					}"
				>
					<span class="cursor-default select-text" @mousedown.stop>
						<slot name="title">{{ title }}</slot>
					</span>
				</div>
				<div v-if="draggable && isDragged && !isFullscreen" class="ant-modal-action" @click="toggleResetDrag">
					<a-tooltip title="还原拖拽" placement="bottom" :getPopupContainer="getTooltipContainer">
						<component :is="AimOutlined" class="p-0.5" />
					</a-tooltip>
				</div>
				<div v-if="fullscreen" class="ant-modal-action" @click="toggleFullscreen">
					<a-tooltip
						:key="isFullscreen ? '' : 'fullscreen'"
						:title="isFullscreen ? '退出全屏' : '全屏'"
						placement="bottom"
						:getPopupContainer="getTooltipContainer"
					>
						<component :is="isFullscreen ? FullscreenExitOutlined : FullscreenOutlined" class="p-0.5" />
					</a-tooltip>
				</div>
			</div>
		</template>

		<template #closeIcon>
			<slot name="closeIcon">
				<a-tooltip title="关闭" placement="bottom" :getPopupContainer="getTooltipContainer">
					<component :is="CloseOutlined" class="p-0.5" />
				</a-tooltip>
			</slot>
		</template>

		<template #modalRender="{ originVNode }">
			<slot name="modalRender" :originVNode="originVNode">
				<div :style="transformStyle" class="relative">
					<div ref="modalContentRef">
						<component :is="originVNode" />
					</div>
					<div
						v-if="resizable && !isFullscreen"
						class="absolute right-0.5 bottom-0.5 w-2 h-2 resize overflow-auto pointer-events-auto"
						@mousedown="handleResize"
					/>
				</div>
			</slot>
		</template>

		<slot></slot>

		<template #footer>
			<slot name="insertFooter" />
			<slot name="footer">
				<a-button @click="handleCancel">
					<slot name="cancelText">{{ cancelText || '取消' }}</slot>
				</a-button>
				<slot name="centerFooter"></slot>
				<a-button type="primary" @click="handleOk" :loading="confirmLoading">
					<slot name="okText">{{ okText || '确定' }}</slot>
				</a-button>
			</slot>
			<slot name="appendFooter" />
		</template>
	</a-modal>
</template>
<script setup>
	import { ref, computed, nextTick, watch, watchEffect, useAttrs, onUnmounted } from 'vue'
	import { AimOutlined, CloseOutlined, FullscreenOutlined, FullscreenExitOutlined } from '@ant-design/icons-vue'
	import { useDraggable } from '@vueuse/core'

	const attrs = useAttrs()
	const props = defineProps({
		// 对话框是否可见
		visible: {
			type: Boolean,
			default: false
		},
		// 标题
		title: {
			type: String,
			default: ''
		},
		// 宽度
		width: {
			type: [Number, String],
			default: '50%'
		},
		// Modal body 样式
		bodyStyle: {
			type: Object,
			default: undefined
		},
		// 对话框外层容器的类名
		wrapClassName: {
			type: String,
			default: ''
		},
		// 底部内容
		footer: {
			type: [String, Object, Function],
			default: undefined
		},
		// 确认按钮文字
		okText: {
			type: String,
			default: '确定'
		},
		// 取消按钮文字
		cancelText: {
			type: String,
			default: '取消'
		},
		// 确定按钮 loading
		confirmLoading: {
			type: Boolean,
			default: false
		},
		// 自定义关闭图标
		closeIcon: {
			type: [Object, Function],
			default: undefined
		},
		// 可全屏
		fullscreen: {
			type: Boolean,
			default: true
		},
		// 可拖拽
		draggable: {
			type: Boolean,
			default: true
		},
		// 可伸缩
		resizable: {
			type: Boolean,
			default: true
		}
	})

	const isOpen = computed(() => {
		return 'open' in attrs ? attrs.open : props.visible
	})

	const emit = defineEmits(['ok', 'close', 'update:open', 'update:visible', 'fullscreen'])

	const handleOk = (e) => emit('ok', e)
	const handleCancel = (e) => {
		emit('close', e)
		if ('open' in attrs) {
			emit('update:open', false)
		} else {
			emit('update:visible', false)
		}
	}

	// Tooltip 挂载 body, 防止拖拽后定位错位
	const getTooltipContainer = () => document.body

	// 响应式对话框
	const modalContentRef = ref()

	// 响应式对话框宽度
	const modalWidth = ref(props.width)

	// 响应式对话框高度
	const modalHeight = ref('auto')

	// 对话框计算属性 bodyStyle
	const calcBodyStyle = computed(() => {
		// 优先使用用户传入的 paddingTop，如果没有则使用默认 16px
		const defaultPaddingTop = props.bodyStyle?.paddingTop ?? props.bodyStyle?.['padding-top'] ?? '16px'
		const style = { paddingTop: defaultPaddingTop, ...(props.bodyStyle || {}) }
		if (isFullscreen.value) {
			style.height = 'calc(100vh - 116px)'
			style.overflowY = 'auto'
			style.overflowX = 'hidden'
		} else if (modalHeight.value && modalHeight.value !== 'auto') {
			style.height = `calc(${modalHeight.value}px - 116px)`
			style.overflowY = 'auto'
			style.overflowX = 'hidden'
		} else {
			// 非全屏模式：自适应高度，但最大不超过视口
			style.maxHeight = 'calc(100vh - 200px)'
			style.overflowY = 'auto'
			style.overflowX = 'hidden'
		}
		return style
	})

	// 对话框全屏/退出全屏
	const isFullscreen = ref(false)
	const fullscreenClass = ref()
	const prevModalWidth = ref(props.width)
	const prevModalHeight = ref('auto')
	const toggleFullscreen = () => {
		if (!isFullscreen.value) {
			toggleResetDrag()
			prevModalWidth.value = modalWidth.value
			prevModalHeight.value = modalHeight.value
		}
		isFullscreen.value = !isFullscreen.value
		modalWidth.value = isFullscreen.value ? '100vw' : prevModalWidth.value
		modalHeight.value = isFullscreen.value ? '100vh' : prevModalHeight.value
		fullscreenClass.value = isFullscreen.value ? 'full-modal' : ''
		emit('fullscreen', isFullscreen.value)
	}

	// 基于 vueuse 实现对话框拖拽
	const modalTitleRef = ref()
	const { x, y, isDragging } = useDraggable(modalTitleRef, {
		disabled: computed(() => !props.draggable || isFullscreen.value)
	})
	const startedDrag = ref(false)
	const startX = ref(0)
	const startY = ref(0)
	const preTransformX = ref(0)
	const preTransformY = ref(0)
	const transformX = ref(0)
	const transformY = ref(0)
	const dragRect = ref({ left: 0, right: 0, top: 0, bottom: 0 })
	watch([x, y], () => {
		if (!startedDrag.value) {
			startX.value = x.value
			startY.value = y.value
			const bodyRect = document.body.getBoundingClientRect()
			const titleRect = modalTitleRef.value.getBoundingClientRect()
			dragRect.value.right = bodyRect.width - titleRect.width
			dragRect.value.bottom = bodyRect.height - titleRect.height
			preTransformX.value = transformX.value
			preTransformY.value = transformY.value
		}
		startedDrag.value = true
	})
	watch(isDragging, () => {
		if (!isDragging.value) {
			startedDrag.value = false
		}
	})
	watchEffect(() => {
		if (startedDrag.value) {
			transformX.value =
				preTransformX.value + Math.min(Math.max(dragRect.value.left, x.value), dragRect.value.right) - startX.value
			transformY.value =
				preTransformY.value + Math.min(Math.max(dragRect.value.top, y.value), dragRect.value.bottom) - startY.value
		}
	})
	const transformStyle = computed(() => {
		return {
			transform: `translate(${transformX.value}px, ${transformY.value}px)`,
			willChange: 'transform'
		}
	})

	// 对话框是否被拖拽
	const isDragged = computed(() => x.value !== 0 || y.value !== 0)

	// 对话框还原拖拽
	const toggleResetDrag = () => {
		x.value = 0
		y.value = 0
		startX.value = 0
		startY.value = 0
		transformX.value = 0
		transformY.value = 0
		preTransformX.value = 0
		preTransformY.value = 0
		nextTick(() => {
			startedDrag.value = false
		})
	}

	// 监听对话框关闭时还原拖拽
	watch(
		() => isOpen.value,
		(newVal) => {
			if (!newVal) toggleResetDrag()
		}
	)

	// 对话框缩放
	let modalAbortCtrl = null
	const handleResize = (e) => {
		e.preventDefault()

		if (!props.resizable || isFullscreen.value) return

		const content = modalContentRef.value
		if (!content) return

		if (modalAbortCtrl) modalAbortCtrl.abort()
		modalAbortCtrl = new AbortController()
		const signal = modalAbortCtrl.signal

		const startX = e.clientX
		const startY = e.clientY
		const startWidth = parseFloat(window.getComputedStyle(content).width) || content.offsetWidth
		const startHeight = parseFloat(window.getComputedStyle(content).height) || content.offsetHeight

		const minWidth = 200
		const minHeight = 200

		const onMousemove = (e) => {
			requestAnimationFrame(() => {
				const diffX = e.clientX - startX
				const diffY = e.clientY - startY
				const newWidth = startWidth + diffX
				const newHeight = startHeight + diffY
				modalWidth.value = Math.min(Math.max(newWidth, minWidth), window.innerWidth)
				modalHeight.value = Math.min(Math.max(newHeight, minHeight), window.innerHeight)
			})
		}

		const onMouseup = () => {
			modalAbortCtrl?.abort()
			modalAbortCtrl = null
		}

		window.addEventListener('mousemove', onMousemove, { signal })
		window.addEventListener('mouseup', onMouseup, { signal })
	}

	onUnmounted(() => {
		modalAbortCtrl?.abort()
	})
</script>

<script>
	// 声明额外的选项
	export default {
		inheritAttrs: false
	}
</script>
