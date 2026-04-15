<template>
	<a-modal
		v-if="isModal"
		v-bind="$attrs"
		:open="visible"
		:width="modalWidth"
		:footer="slotKeys.includes('footer') ? undefined : null"
		:wrap-class-name="wrapClassName + fullscreenClass"
		@cancel="cancel"
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
						cursor: isFullscreen ? 'default' : 'move',
						userSelect: 'none'
					}"
				>
					<span class="cursor-default select-text">{{ title }}</span>
				</div>
				<div v-if="isDragged" class="ant-modal-action" @click="toggleResetDrag">
					<a-tooltip title="还原拖拽" placement="bottom" :getPopupContainer="(trigger) => trigger">
						<component :is="AimOutlined" class="p-0.5" />
					</a-tooltip>
				</div>
				<div class="ant-modal-action" @click="toggleFullScreen">
					<a-tooltip
						:key="isFullscreen ? '' : 'isFullscreen'"
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
			<div :style="transformStyle">
				<component ref="modalContentRef" :is="originVNode" />
			</div>
		</template>

		<template #closeIcon>
			<a-tooltip title="关闭" placement="bottom" :getPopupContainer="(trigger) => trigger">
				<component :is="CloseOutlined" class="p-0.5" />
			</a-tooltip>
		</template>

		<template v-for="slotKey in slotKeys" #[slotKey]>
			<slot :name="slotKey" />
		</template>
	</a-modal>
	<a-drawer
		v-else
		v-bind="$attrs"
		:open="visible"
		:width="drawerWidth"
		:footer-style="{ textAlign: 'right' }"
		@close="cancel"
	>
		<template v-for="slotKey in slotKeys" #[slotKey]>
			<slot :name="slotKey" />
		</template>
	</a-drawer>
</template>

<script setup>
	import { computed, useSlots, onMounted, onUnmounted } from 'vue'
	import { AimOutlined, CloseOutlined, FullscreenExitOutlined, FullscreenOutlined } from '@ant-design/icons-vue'
	import { useDraggable } from '@vueuse/core'
	import { globalStore } from '@/store'
	const slots = useSlots()
	const store = globalStore()
	const props = defineProps({
		visible: {
			type: Boolean,
			default: false,
			required: false
		},
		title: {
			type: String,
			default: ''
		},
		width: {
			type: [Number, String],
			default: '50%'
		},
		wrapClassName: {
			type: String,
			default: ''
		}
	})
	const FormContainerTypeEnum = {
		DRAWER: 'drawer',
		MODAL: 'modal'
	}
	const formStyle = computed(() => {
		return store.formStyle
	})
	const slotKeys = computed(() => {
		return Object.keys(slots)
	})
	const isModal = computed(() => {
		return FormContainerTypeEnum.MODAL === formStyle.value
	})

	const emit = defineEmits(['close', 'fullscreen'])

	const cancel = () => {
		emit('close')
	}

	// 响应式抽屉宽度
	const isSmallScreen = ref(window.innerWidth <= 768)
	const drawerWidth = computed(() => {
		return isSmallScreen.value ? '100%' : props.width // 小屏幕100%宽度，其他情况使用默认值
	})

	// 响应式对话框
	const modalContentRef = ref()

	// 响应式对话框宽度
	const modalWidth = ref(props.width)

	// 全屏
	const isFullscreen = ref(false)
	const fullscreenClass = ref()
	const toggleFullScreen = () => {
		if (!isFullscreen.value) toggleResetDrag()
		isFullscreen.value = !isFullscreen.value
		modalWidth.value = isFullscreen.value ? '100vw' : props.width
		modalContentRef.value.style.height = isFullscreen.value ? '100vh' : ''
		fullscreenClass.value = isFullscreen.value ? ' full-modal' : ''
		emit('fullscreen', isFullscreen.value)
	}

	// 基于 vueuse 实现对话框拖拽
	const modalTitleRef = ref()
	const { x, y, isDragging } = useDraggable(modalTitleRef, {
		disabled: computed(() => isFullscreen.value)
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

	// 监听窗口大小变化
	const handleResize = () => {
		isSmallScreen.value = window.innerWidth <= 768
	}

	onMounted(() => {
		window.addEventListener('resize', handleResize)
	})

	onUnmounted(() => {
		window.removeEventListener('resize', handleResize)
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
</style>
