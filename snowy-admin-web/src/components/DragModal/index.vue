<template>
	<a-modal
		v-bind="$attrs"
		:open="visible"
		:width="modalWidth"
		:wrap-class-name="wrapClassName + fullscreenClass"
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
					<span class="cursor-default select-text">{{ title }}</span>
				</div>
				<div v-if="draggable && isDragged" class="ant-modal-action" @click="toggleResetDrag">
					<a-tooltip title="还原拖拽" placement="bottom" :getPopupContainer="(trigger) => trigger">
						<component :is="AimOutlined" class="p-0.5" />
					</a-tooltip>
				</div>
				<div v-if="fullscreen" class="ant-modal-action" @click="toggleFullScreen">
					<a-tooltip
						:key="isFullscreen ? '' : 'fullscreen'"
						:title="isFullscreen ? '退出全屏' : '全屏'"
						placement="bottom"
						:getPopupContainer="(trigger) => trigger"
					>
						<component :is="isFullscreen ? FullscreenExitOutlined : FullscreenOutlined" class="p-0.5" />
					</a-tooltip>
				</div>
			</div>
		</template>

		<template #modalRender="{ originVNode }">
			<div :style="transformStyle" class="relative">
				<component ref="modalContentRef" :is="originVNode" />
				<div
					v-if="props.resizable && !isFullscreen"
					class="absolute right-0.5 bottom-0.5 w-2 h-2 resize overflow-auto pointer-events-auto"
					@mousedown="handleResize"
				/>
			</div>
		</template>

		<template #closeIcon>
			<a-tooltip title="关闭" placement="bottom" :getPopupContainer="(trigger) => trigger">
				<component :is="CloseOutlined" class="p-0.5" />
			</a-tooltip>
		</template>

		<slot></slot>

		<template #footer>
			<slot name="insertFooter"></slot>
			<slot name="footer">
				<a-button @click="handleCancel">
					{{ cancelText || '取消' }}
				</a-button>
				<slot name="centerFooter"></slot>
				<a-button type="primary" @click="handleOk" :loading="confirmLoading">
					{{ okText || '确定' }}
				</a-button>
			</slot>
			<slot name="appendFooter"></slot>
		</template>
	</a-modal>
</template>
<script setup>
	import { ref, computed, watch, watchEffect } from 'vue'
	import { AimOutlined, CloseOutlined, FullscreenOutlined, FullscreenExitOutlined } from '@ant-design/icons-vue'
	import { useDraggable } from '@vueuse/core'

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

	const emit = defineEmits(['ok', 'close', 'fullscreen'])

	const handleOk = (e) => emit('ok', e)
	const handleCancel = (e) => emit('close', e)

	// 响应式对话框
	const modalContentRef = ref()

	// 宽度
	const modalWidth = ref(props.width)

	// 高度
	const modalHeight = ref(undefined)

	// 计算属性 bodyStyle
	const calcBodyStyle = computed(() => {
		const style = { overflow: 'auto', ...props.bodyStyle }
		if (isFullscreen.value) return style
		if (modalHeight.value) {
			style.height = `calc(${modalHeight.value}px - 116px)`
		} else {
			style.height = props.bodyStyle?.height || 'auto'
		}
		return style
	})

	// 全屏
	const isFullscreen = ref(false)
	const fullscreenClass = ref()
	const prevModalWidth = ref(props.width)
	const prevModalHeight = ref()
	const toggleFullScreen = () => {
		if (!isFullscreen.value) {
			toggleResetDrag()
			prevModalWidth.value = modalWidth.value
			prevModalHeight.value = modalHeight.value
		}
		isFullscreen.value = !isFullscreen.value
		modalWidth.value = isFullscreen.value ? '100vw' : prevModalWidth.value
		modalHeight.value = isFullscreen.value ? '100vh' : prevModalHeight.value
		fullscreenClass.value = isFullscreen.value ? ' full-modal' : ''
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
			transform: `translate(${transformX.value}px, ${transformY.value}px)`
		}
	})

	// 是否被拖拽
	const isDragged = computed(() => x.value !== 0 || y.value !== 0)

	// 还原拖拽
	const toggleResetDrag = () => {
		x.value = 0
		y.value = 0
		startX.value = 0
		startY.value = 0
		transformX.value = 0
		transformY.value = 0
		preTransformX.value = 0
		preTransformY.value = 0
		startedDrag.value = false
	}

	watch(
		() => props.visible,
		() => {
			if (!props.visible) toggleResetDrag()
		}
	)

	// 对话框缩放
	const handleResize = (e) => {
		e.preventDefault()

		if (!props.resizable || isFullscreen.value) return

		const content = modalContentRef.value
		if (!content) return

		const startX = e.clientX
		const startY = e.clientY
		const startWidth = parseFloat(window.getComputedStyle(content).width) || content.offsetWidth
		const startHeight = parseFloat(window.getComputedStyle(content).height) || content.offsetHeight

		const onMouseMove = (e) => {
			const diffX = e.clientX - startX
			const diffY = e.clientY - startY
			const newWidth = startWidth + diffX
			const newHeight = startHeight + diffY
			if (newWidth > 200) modalWidth.value = newWidth
			if (newHeight > 200) modalHeight.value = newHeight
		}

		const onMouseUp = () => {
			window.removeEventListener('mousemove', onMouseMove)
			window.removeEventListener('mouseup', onMouseUp)
		}

		window.addEventListener('mousemove', onMouseMove)
		window.addEventListener('mouseup', onMouseUp)
	}
</script>
