<template>
	<a-modal
		v-if="isModal"
		v-bind="attrs"
		:open="isOpen"
		:width="modalWidth"
		:footer="slots.footer ? undefined : null"
		:wrap-class-name="`${wrapClassName} ${fullscreenClass}`"
		:bodyStyle="calcBodyStyle"
		@cancel="cancel"
	>
		<template #title v-if="slots.title || title">
			<div class="flex justify-between items-center">
				<div
					ref="modalTitleRef"
					:style="{
						flex: 1,
						overflow: 'hidden',
						whiteSpace: 'nowrap',
						textOverflow: 'ellipsis',
						cursor: modalFullscreen ? 'default' : 'move',
						userSelect: 'none'
					}"
				>
					<span class="cursor-default select-text" @mousedown.stop>
						<slot name="title">{{ title }}</slot>
					</span>
				</div>
				<div v-if="isDragged && !modalFullscreen" class="ant-modal-action" @click="toggleResetDrag">
					<a-tooltip title="还原拖拽" placement="bottom" :getPopupContainer="getTooltipContainer">
						<component :is="AimOutlined" class="p-0.5" />
					</a-tooltip>
				</div>
				<div class="ant-modal-action" @click="toggleFullScreenModal">
					<a-tooltip
						:key="modalFullscreen ? '' : 'modalFullscreen'"
						:title="modalFullscreen ? '退出全屏' : '全屏'"
						placement="bottom"
						:getPopupContainer="getTooltipContainer"
					>
						<component :is="modalFullscreen ? FullscreenExitOutlined : FullscreenOutlined" class="p-0.5" />
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
						v-if="!modalFullscreen"
						class="absolute right-0.5 bottom-0.5 w-2 h-2 resize overflow-auto pointer-events-auto"
						@mousedown="toggleResizerModal"
					/>
				</div>
			</slot>
		</template>

		<template #footer>
			<slot name="insertFooter"></slot>
			<slot name="footer">
				<a-button @click="cancel">
					<slot name="cancelText">{{ cancelText || '取消' }}</slot>
				</a-button>
				<slot name="centerFooter"></slot>
				<a-button type="primary" @click="ok" :loading="confirmLoading">
					<slot name="okText">{{ okText || '确定' }}</slot>
				</a-button>
			</slot>
			<slot name="appendFooter"></slot>
		</template>

		<template v-for="slotKey in modalSlotKeys" :key="slotKey" #[slotKey]>
			<slot :name="slotKey" />
		</template>
	</a-modal>

	<a-drawer
		v-else
		v-bind="attrs"
		:open="isOpen"
		:width="drawerWidth"
		:height="drawerHeight"
		:footer-style="{ textAlign: 'right' }"
		@close="cancel"
	>
		<template #title>
			<div class="flex justify-start items-center gap-1">
				<div v-if="isResized && !drawerFullscreen" class="ant-modal-action" @click="toggleResetResize">
					<a-tooltip title="还原尺寸" placement="bottom" :getPopupContainer="getTooltipContainer">
						<component :is="AimOutlined" class="p-0.5" />
					</a-tooltip>
				</div>
				<div v-if="!isSmallScreen" class="ant-drawer-action" @click="toggleFullscreenDrawer">
					<a-tooltip
						:key="drawerFullscreen ? '' : 'drawerFullscreen'"
						:title="drawerFullscreen ? '退出全屏' : '全屏'"
						placement="bottom"
						:getPopupContainer="getTooltipContainer"
					>
						<component :is="drawerFullscreen ? FullscreenExitOutlined : FullscreenOutlined" class="p-0.5" />
					</a-tooltip>
				</div>
				<slot name="title">{{ title }}</slot>
			</div>
		</template>

		<template #closeIcon>
			<slot name="closeIcon">
				<a-tooltip title="关闭" placement="bottom" :getPopupContainer="getTooltipContainer">
					<component :is="CloseOutlined" class="p-0.5" />
				</a-tooltip>
			</slot>
		</template>

		<template #default>
			<div
				v-if="!isSmallScreen && !drawerFullscreen"
				class="resizer-handle"
				:class="`placement-${placement}`"
				@mousedown="toggleResizerDrawer"
			/>
			<slot />
		</template>

		<template v-for="slotKey in drawerSlotKeys" :key="slotKey" #[slotKey]>
			<slot :name="slotKey" />
		</template>
	</a-drawer>
</template>

<script setup>
	import {
		computed,
		nextTick,
		onMounted,
		onUnmounted,
		ref,
		useAttrs,
		useSlots,
		watch,
		watchEffect,
		watchPostEffect
	} from 'vue'
	import { AimOutlined, CloseOutlined, FullscreenExitOutlined, FullscreenOutlined } from '@ant-design/icons-vue'
	import { useDraggable } from '@vueuse/core'
	import { globalStore } from '@/store'

	const store = globalStore()
	const attrs = useAttrs()
	const slots = useSlots()
	const props = defineProps({
		// 对话框是否可见
		visible: {
			type: Boolean,
			default: false,
			required: false
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
		// body 样式
		bodyStyle: {
			type: Object,
			default: undefined
		}
	})

	const FormContainerTypeEnum = {
		DRAWER: 'drawer',
		MODAL: 'modal'
	}
	const formStyle = computed(() => {
		return store.formStyle
	})
	const isModal = computed(() => {
		return FormContainerTypeEnum.MODAL === formStyle.value
	})

	const isOpen = computed(() => {
		return 'open' in attrs ? attrs.open : props.visible
	})

	// 对话框：外层容器的类名
	const wrapClassName = computed(() => attrs.wrapClassName || '')
	// 对话框：确认按钮文字, 默认"确定"
	const okText = computed(() => attrs.okText || '确定')
	// 对话框：取消按钮文字, 默认"取消"
	const cancelText = computed(() => attrs.cancelText || '取消')
	// 对话框：确定按钮 loading
	const confirmLoading = computed(() => attrs.confirmLoading || false)
	// 抽屉：抽屉的方向 'top' | 'right' | 'bottom' | 'left', 默认 'right'
	const placement = computed(() => attrs.placement || 'right')

	// watchPostEffect实时监听插槽
	const modalSlotKeys = ref([])
	const drawerSlotKeys = ref([])
	watchPostEffect(() => {
		const excludeModal = ['title', 'closeIcon', 'modalRender', 'footer']
		modalSlotKeys.value = Object.keys(slots).filter((key) => !excludeModal.includes(key))

		const excludeDrawer = ['title', 'closeIcon', 'default']
		drawerSlotKeys.value = Object.keys(slots).filter((key) => !excludeDrawer.includes(key))
	})

	const emit = defineEmits(['ok', 'close', 'update:open', 'update:visible', 'modalFullscreen', 'drawerFullscreen'])

	const ok = (e) => emit('ok', e)

	const cancel = (e) => {
		emit('close', e)
		if ('open' in attrs) {
			emit('update:open', false)
		} else {
			emit('update:visible', false)
		}
	}

	// Tooltip 挂载 body, 防止拖拽后定位错位
	const getTooltipContainer = () => document.body

	/************************* 对话框 *************************/
		// 响应式对话框
	const modalContentRef = ref()

	// 响应式对话框宽度
	const modalWidth = ref(props.width)

	// 响应式对话框高度
	const modalHeight = ref('auto')

	// 对话框计算属性 bodyStyle
	const calcBodyStyle = computed(() => {
		const style = { overflow: 'auto', ...(props.bodyStyle || {}) }
		if (modalFullscreen.value) return style
		if (modalHeight.value) {
			style.height = `calc(${modalHeight.value}px - 116px)`
		} else {
			style.height = props.bodyStyle?.height || 'auto'
		}
		return style
	})

	// 对话框全屏/退出全屏
	const modalFullscreen = ref(false)
	const fullscreenClass = ref()
	const prevModalWidth = ref(props.width)
	const prevModalHeight = ref('auto')
	const toggleFullScreenModal = () => {
		if (!modalFullscreen.value) {
			toggleResetDrag()
			prevModalWidth.value = modalWidth.value
			prevModalHeight.value = modalHeight.value
		}
		modalFullscreen.value = !modalFullscreen.value
		modalWidth.value = modalFullscreen.value ? '100vw' : prevModalWidth.value
		modalHeight.value = modalFullscreen.value ? '100vh' : prevModalHeight.value
		fullscreenClass.value = modalFullscreen.value ? 'full-modal' : ''
		emit('modalFullscreen', modalFullscreen.value)
	}

	// 基于 vueuse 实现对话框拖拽
	const modalTitleRef = ref()
	const { x, y, isDragging } = useDraggable(modalTitleRef, {
		disabled: computed(() => modalFullscreen.value)
	})
	const startedDrag = ref(false)
	const startX = ref(0)
	const startY = ref(0)
	const transformX = ref(0)
	const transformY = ref(0)
	const preTransformX = ref(0)
	const preTransformY = ref(0)
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

	// 对话框缩放
	let modalAbortCtrl = null
	const toggleResizerModal = (e) => {
		e.preventDefault()

		if (modalFullscreen.value) return

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

		const onMousemoveModal = (e) => {
			requestAnimationFrame(() => {
				const diffX = e.clientX - startX
				const diffY = e.clientY - startY
				const newWidth = startWidth + diffX
				const newHeight = startHeight + diffY
				modalWidth.value = Math.min(Math.max(newWidth, minWidth), window.innerWidth)
				modalHeight.value = Math.min(Math.max(newHeight, minHeight), window.innerHeight)
			})
		}

		const onMouseupModal = () => {
			modalAbortCtrl?.abort()
			modalAbortCtrl = null
		}

		window.addEventListener('mousemove', onMousemoveModal, { signal })
		window.addEventListener('mouseup', onMouseupModal, { signal })
	}

	/************************* 抽屉 *************************/
	let initDrawerWidth = props.width || 378

	let initDrawerHeight = attrs.height ?? 'auto'

	watch(
		() => props.width,
		(val) => {
			initDrawerWidth = val || 378
		}
	)
	watch(
		() => attrs.height,
		(val) => {
			initDrawerHeight = val ?? 'auto'
		}
	)

	// 响应式抽屉宽度
	const baseDrawerWidth = ref(initDrawerWidth)

	// 响应式抽屉高度
	const baseDrawerHeight = ref(initDrawerHeight)

	// 是否小屏
	const isSmallScreen = ref(window.innerWidth <= 768)

	// 是否全屏
	const drawerFullscreen = ref(false)

	// 抽屉宽度
	const drawerWidth = computed(() => {
		if (isSmallScreen.value || drawerFullscreen.value) return '100%'
		return baseDrawerWidth.value
	})

	// 抽屉高度
	const drawerHeight = computed(() => {
		if (isSmallScreen.value || drawerFullscreen.value) return '100%'
		return baseDrawerHeight.value
	})

	// 抽屉全屏/退出全屏
	const toggleFullscreenDrawer = () => {
		drawerFullscreen.value = !drawerFullscreen.value
		emit('drawerFullscreen', drawerFullscreen.value)
	}

	// 抽屉缩放
	let drawerAbortCtrl = null
	const toggleResizerDrawer = (e) => {
		e.preventDefault()

		if (drawerFullscreen.value || isSmallScreen.value) return

		if (drawerAbortCtrl) drawerAbortCtrl.abort()
		drawerAbortCtrl = new AbortController()
		const signal = drawerAbortCtrl.signal

		const drawerPlacement = placement.value
		const startX = e.clientX
		const startY = e.clientY
		const startWidth = baseDrawerWidth.value
		const startHeight = baseDrawerHeight.value

		const minWidth = 378
		const maxWidth = window.innerWidth
		const minHeight = 378
		const maxHeight = window.innerHeight

		const onMousemoveDrawer = (e) => {
			requestAnimationFrame(() => {
				const diffX = e.clientX - startX
				const diffY = e.clientY - startY
				switch (drawerPlacement) {
					case 'right': {
						let newWidth = startWidth - diffX
						baseDrawerWidth.value = Math.min(Math.max(newWidth, minWidth), maxWidth)
						break
					}
					case 'left': {
						let newWidth = startWidth + diffX
						baseDrawerWidth.value = Math.min(Math.max(newWidth, minWidth), maxWidth)
						break
					}
					case 'bottom': {
						let newHeight = startHeight - diffY
						baseDrawerHeight.value = Math.min(Math.max(newHeight, minHeight), maxHeight)
						break
					}
					case 'top': {
						let newHeight = startHeight + diffY
						baseDrawerHeight.value = Math.min(Math.max(newHeight, minHeight), maxHeight)
						break
					}
				}
			})
		}

		const onMouseupDrawer = () => {
			drawerAbortCtrl?.abort()
			drawerAbortCtrl = null
		}

		document.addEventListener('mousemove', onMousemoveDrawer, { signal })
		document.addEventListener('mouseup', onMouseupDrawer, { signal })
	}

	const isResized = computed(
		() =>
			baseDrawerWidth.value !== initDrawerWidth ||
			(initDrawerHeight !== 'auto' && baseDrawerHeight.value !== initDrawerHeight)
	)

	const toggleResetResize = () => {
		baseDrawerWidth.value = initDrawerWidth
		baseDrawerHeight.value = initDrawerHeight
	}

	// 监听关闭时重置对话框和抽屉状态
	watch(
		() => isOpen.value,
		(newVal) => {
			if (!newVal) {
				// 重置对话框
				toggleResetDrag()
				modalFullscreen.value = false
				// 重置抽屉
				toggleResetResize()
				drawerFullscreen.value = false
			}
		}
	)

	// 监听窗口大小变化
	const handleResize = () => {
		isSmallScreen.value = window.innerWidth <= 768
	}

	onMounted(() => {
		window.addEventListener('resize', handleResize)
	})

	onUnmounted(() => {
		window.removeEventListener('resize', handleResize)
		modalAbortCtrl?.abort()
		drawerAbortCtrl?.abort()
	})
</script>

<script>
	// 声明额外的选项
	export default {
		inheritAttrs: false
	}
</script>

<style lang="less" scoped>
/* 确保小屏幕下抽屉不会有额外的边距或滚动条 */
@media (max-width: 576px) {
	:deep(.ant-drawer-content-wrapper) {
		width: 100% !important;
		max-width: 100% !important;
	}
}
.resizer-handle {
	position: absolute;
	user-select: none;

	&.placement-right {
		left: 0;
		top: 0;
		width: 4px;
		height: 100%;
		cursor: ew-resize;
	}

	&.placement-left {
		right: 0;
		top: 0;
		width: 4px;
		height: 100%;
		cursor: ew-resize;
	}

	&.placement-top {
		bottom: 0;
		left: 0;
		height: 4px;
		width: 100%;
		cursor: ns-resize;
	}

	&.placement-bottom {
		top: 0;
		left: 0;
		height: 4px;
		width: 100%;
		cursor: ns-resize;
	}
}
</style>
