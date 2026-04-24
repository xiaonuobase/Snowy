<template>
	<div class="resizable-panel" :style="wrapperStyle" ref="rootRef">
		<div
			class="panel-left"
			:class="{ 'panel-left-animating': animating }"
			:style="leftPanelStyle"
			v-if="!shouldHideLeft"
		>
			<div class="panel-left-inner" :style="{ padding: toPx(leftPadding) }" v-show="!collapsed">
				<slot name="left"></slot>
			</div>
		</div>
		<div
			class="resizer"
			:class="{
				'resizer-horizontal': direction === 'row',
				'resizer-vertical': direction === 'column',
				'resizer-collapsed': collapsed
			}"
			v-if="!shouldHideLeft && !collapsed"
			@mousedown="onResizerMouseDown"
		>
			<div class="resizer-handle">
				<!-- 默认：三点拖拽图标 -->
				<div class="handle-dots"></div>
				<!-- hover：折叠箭头 -->
				<div class="handle-arrow" v-if="collapsible" @click.stop="toggleCollapse" :title="'折叠面板'">
					<LeftOutlined v-if="direction === 'row'" />
					<UpOutlined v-else />
				</div>
			</div>
		</div>
		<div class="panel-right" :style="{ flex: 1, padding: toPx(rightPadding), position: 'relative' }">
			<!-- 折叠后：展开按钮定位在右侧面板左边缘 -->
			<div
				v-if="collapsed && collapsible && !shouldHideLeft"
				class="resizer-expand"
				:class="{ 'resizer-expand-vertical': direction === 'column' }"
				@click="toggleCollapse"
				:title="'展开面板'"
			>
				<RightOutlined v-if="direction === 'row'" />
				<DownOutlined v-else />
			</div>
			<slot name="right"></slot>
		</div>
	</div>
</template>

<script setup>
	import { ref, computed, onMounted, onUnmounted } from 'vue'
	import { LeftOutlined, RightOutlined, UpOutlined, DownOutlined } from '@ant-design/icons-vue'
	import { globalStore } from '@/store'

	const props = defineProps({
		initialSize: { type: Number, default: 200 },
		minSize: { type: Number, default: 100 },
		maxSize: { type: Number, default: 500 },
		direction: {
			type: String,
			default: 'row',
			validator: (value) => ['row', 'column'].includes(value)
		},
		md: { type: Number, default: null },
		bottomGap: { type: [Number, String], default: 10 },
		shadow: { type: Boolean, default: true },
		radius: { type: [Number, String], default: 'system' },
		hideLeft: { type: Boolean, default: false },
		leftPadding: { type: [Number, String], default: 24 },
		rightPadding: { type: [Number, String], default: 24 },
		collapsible: { type: Boolean, default: true }
	})

	const emit = defineEmits(['resize', 'collapse'])

	const leftSize = ref(props.initialSize)
	const isResizing = ref(false)
	const collapsed = ref(false)
	const animating = ref(false)
	const rootRef = ref(null)
	let activeContainer = null

	const windowWidth = ref(typeof window !== 'undefined' ? window.innerWidth : 1024)

	const shouldHideLeft = computed(() => {
		return props.hideLeft || (props.md === 0 && windowWidth.value < 768)
	})

	const sizeProperty = computed(() => {
		return props.direction === 'row' ? 'width' : 'height'
	})

	const leftPanelStyle = computed(() => {
		const size = collapsed.value ? '0px' : leftSize.value + 'px'
		return {
			[sizeProperty.value]: size,
			minWidth: collapsed.value ? '0px' : (props.direction === 'row' ? props.minSize + 'px' : 'auto'),
			minHeight: collapsed.value ? '0px' : (props.direction === 'column' ? props.minSize + 'px' : 'auto'),
			overflow: 'hidden'
		}
	})

	const toggleCollapse = () => {
		if (!props.collapsible) return
		animating.value = true
		collapsed.value = !collapsed.value
		emit('collapse', collapsed.value)
		setTimeout(() => { animating.value = false }, 300)
	}

	const onResizerMouseDown = (e) => {
		if (shouldHideLeft.value || collapsed.value) return
		if (e.target.closest('.handle-arrow')) return
		isResizing.value = true
		activeContainer = rootRef.value || e.target.closest('.resizable-panel')
		document.addEventListener('mousemove', handleResize)
		document.addEventListener('mouseup', stopResize)
		e.preventDefault()
	}

	const handleResize = (e) => {
		if (!isResizing.value) return
		const container = activeContainer
		if (!container) return
		const rect = container.getBoundingClientRect()
		let newSize
		if (props.direction === 'row') {
			newSize = e.clientX - rect.left
		} else {
			newSize = e.clientY - rect.top
		}
		newSize = Math.max(props.minSize, Math.min(props.maxSize, newSize))
		leftSize.value = newSize
		emit('resize', newSize)
	}

	const stopResize = () => {
		isResizing.value = false
		activeContainer = null
		document.removeEventListener('mousemove', handleResize)
		document.removeEventListener('mouseup', stopResize)
	}

	let resizeHandler = null
	onMounted(() => {
		resizeHandler = () => { windowWidth.value = window.innerWidth }
		window.addEventListener('resize', resizeHandler)
		resizeHandler()
	})

	onUnmounted(() => {
		document.removeEventListener('mousemove', handleResize)
		document.removeEventListener('mouseup', stopResize)
		if (resizeHandler) window.removeEventListener('resize', resizeHandler)
	})

	const store = globalStore()
	const systemRadius = computed(() => (store.roundedCornerStyleOpen ? 8 : 2))
	const toPx = (val) => (typeof val === 'number' ? `${val}px` : val)
	const wrapperStyle = computed(() => {
		const gap = toPx(props.bottomGap)
		return {
			display: 'flex',
			flexDirection: props.direction,
			borderRadius: toPx(props.radius === 'system' ? systemRadius.value : props.radius),
			boxShadow: props.shadow ? 'var(--card-shadow-soft, 0 1px 6px rgba(0, 0, 0, 0.06))' : 'none',
			marginBottom: gap,
			height: `calc(100% - ${gap})`
		}
	})

	defineExpose({
		setSize: (size) => {
			leftSize.value = Math.max(props.minSize, Math.min(props.maxSize, size))
		},
		getSize: () => leftSize.value,
		toggleCollapse,
		isCollapsed: () => collapsed.value
	})
</script>

<style scoped>
	.resizable-panel {
		height: 100%;
		width: 100%;
	}

	/* ---- 左侧面板 ---- */
	.panel-left {
		overflow: hidden;
		background: var(--snowy-background-color);
	}

	.panel-left-animating {
		transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1),
					height 0.3s cubic-bezier(0.4, 0, 0.2, 1),
					min-width 0.3s cubic-bezier(0.4, 0, 0.2, 1),
					min-height 0.3s cubic-bezier(0.4, 0, 0.2, 1);
	}

	.panel-left-inner {
		height: 100%;
		overflow: auto;
		scrollbar-width: none;
		-ms-overflow-style: none;
	}

	.panel-left-inner::-webkit-scrollbar {
		display: none;
	}

	/* ---- 右侧面板 ---- */
	.panel-right {
		padding: 24px;
		overflow: auto;
		background: var(--snowy-background-color);
		scrollbar-width: none;
		-ms-overflow-style: none;
	}

	.panel-right::-webkit-scrollbar {
		display: none;
	}

	/* ---- 分隔条 ---- */
	.resizer {
		position: relative;
		background: transparent;
		user-select: none;
		display: flex;
		align-items: center;
		justify-content: center;
		flex-shrink: 0;
	}

	.resizer-horizontal {
		width: 8px;
		cursor: col-resize;
	}

	.resizer-vertical {
		height: 8px;
		cursor: row-resize;
	}

	/* ---- 拖拽手柄（胶囊） ---- */
	.resizer-handle {
		position: absolute;
		top: 50%;
		left: 50%;
		transform: translate(-50%, -50%);
		background: var(--component-background, #fff);
		border-radius: 999px;
		box-shadow:
			0 2px 10px rgba(0, 0, 0, 0.06),
			inset 0 0 0 1px var(--border-color-base, #d9d9d9);
		display: flex;
		align-items: center;
		justify-content: center;
		transition: all 0.25s ease;
		z-index: 5;
	}

	.resizer-horizontal .resizer-handle {
		width: 12px;
		height: 32px;
	}

	.resizer-vertical .resizer-handle {
		width: 32px;
		height: 12px;
	}

	/* ---- 三点图标 ---- */
	.handle-dots {
		position: absolute;
		top: 50%;
		left: 50%;
		transform: translate(-50%, -50%);
		width: 2px;
		height: 2px;
		background: var(--border-color-base, #d9d9d9);
		border-radius: 50%;
		transition: opacity 0.2s ease;
	}

	.resizer-horizontal .handle-dots {
		box-shadow:
			0 6px 0 0 var(--border-color-base, #d9d9d9),
			0 -6px 0 0 var(--border-color-base, #d9d9d9);
	}

	.resizer-vertical .handle-dots {
		box-shadow:
			6px 0 0 0 var(--border-color-base, #d9d9d9),
			-6px 0 0 0 var(--border-color-base, #d9d9d9);
	}

	/* ---- 折叠箭头（hover 时替换三点） ---- */
	.handle-arrow {
		position: absolute;
		top: 0;
		left: 0;
		width: 100%;
		height: 100%;
		display: flex;
		align-items: center;
		justify-content: center;
		opacity: 0;
		transition: opacity 0.2s ease;
		color: var(--primary-color, #1890ff);
		font-size: 10px;
		cursor: pointer;
	}

	.resizer-handle:hover .handle-dots {
		opacity: 0;
	}

	.resizer-handle:hover .handle-arrow {
		opacity: 1;
	}

	.resizer-handle:hover {
		background: var(--primary-1, #e6f7ff);
		box-shadow:
			0 2px 10px rgba(24, 144, 255, 0.12),
			inset 0 0 0 1px var(--primary-color, #1890ff);
	}

	/* ---- 折叠后的展开按钮（定位在右侧面板左边缘垂直居中） ---- */
	.resizer-expand {
		position: absolute;
		left: 0;
		top: 50%;
		transform: translateY(-50%);
		z-index: 10;
		display: flex;
		align-items: center;
		justify-content: center;
		width: 12px;
		height: 32px;
		background: var(--component-background, #fff);
		border: 1px solid var(--border-color-base, #d9d9d9);
		border-left: none;
		border-radius: 0 4px 4px 0;
		cursor: pointer;
		color: var(--text-color-secondary, #999);
		font-size: 10px;
		transition: all 0.2s ease;
	}

	.resizer-expand-vertical {
		left: 50%;
		top: 0;
		transform: translateX(-50%);
		width: 32px;
		height: 12px;
		border: 1px solid var(--border-color-base, #d9d9d9);
		border-top: none;
		border-radius: 0 0 4px 4px;
	}

	.resizer-expand:hover {
		color: var(--primary-color, #1890ff);
		border-color: var(--primary-color, #1890ff);
		background: var(--primary-1, #e6f7ff);
	}

	.resizer-expand:active {
		transform: translateY(-50%) scale(0.95);
	}
</style>
