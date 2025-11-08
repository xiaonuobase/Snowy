<template>
	<div class="resizable-panel" :style="{ display: 'flex', flexDirection: direction }">
		<div
			class="panel-left"
			:style="{
				[sizeProperty]: leftSize + 'px',
				minWidth: direction === 'row' ? minSize + 'px' : 'auto',
				minHeight: direction === 'column' ? minSize + 'px' : 'auto'
			}"
			v-if="!shouldHideLeft"
		>
			<slot name="left"></slot>
		</div>
		<div
			class="resizer"
			:class="{ 'resizer-horizontal': direction === 'row', 'resizer-vertical': direction === 'column' }"
			@mousedown="startResize"
			v-if="!shouldHideLeft"
		>
			<div class="resizer-handle"></div>
		</div>
		<div class="panel-right" :style="{ flex: 1 }">
			<slot name="right"></slot>
		</div>
	</div>
</template>

<script setup>
	import { ref, computed, onMounted, onUnmounted } from 'vue'

	const props = defineProps({
		// 初始左侧面板大小
		initialSize: {
			type: Number,
			default: 200
		},
		// 最小大小
		minSize: {
			type: Number,
			default: 100
		},
		// 最大大小
		maxSize: {
			type: Number,
			default: 500
		},
		// 方向：'row' 水平分割，'column' 垂直分割
		direction: {
			type: String,
			default: 'row',
			validator: (value) => ['row', 'column'].includes(value)
		},
		// 合并后的响应式开关：当 md 为 0 时，在 <768px 隐藏左侧与拖拽条
		md: {
			type: Number,
			default: null
		}
	})

	const emit = defineEmits(['resize'])

	const leftSize = ref(props.initialSize)
	const isResizing = ref(false)

	// 监听窗口宽度，用于判断是否在小屏隐藏
	const windowWidth = ref(typeof window !== 'undefined' ? window.innerWidth : 1024)

	const shouldHideLeft = computed(() => {
		// 当 md 为 0 时，在 <768 的视口隐藏左侧与拖拽条
		return props.md === 0 && windowWidth.value < 768
	})

	// 根据方向确定使用的CSS属性
	const sizeProperty = computed(() => {
		return props.direction === 'row' ? 'width' : 'height'
	})

	const startResize = (e) => {
		if (shouldHideLeft.value) return
		isResizing.value = true
		document.addEventListener('mousemove', handleResize)
		document.addEventListener('mouseup', stopResize)
		e.preventDefault()
	}

	const handleResize = (e) => {
		if (!isResizing.value) return

		const container = e.currentTarget?.closest?.('.resizable-panel') || document.querySelector('.resizable-panel')
		if (!container) return

		const rect = container.getBoundingClientRect()
		let newSize

		if (props.direction === 'row') {
			newSize = e.clientX - rect.left
		} else {
			newSize = e.clientY - rect.top
		}

		// 限制在最小值和最大值之间
		newSize = Math.max(props.minSize, Math.min(props.maxSize, newSize))

		leftSize.value = newSize
		emit('resize', newSize)
	}

	const stopResize = () => {
		isResizing.value = false
		document.removeEventListener('mousemove', handleResize)
		document.removeEventListener('mouseup', stopResize)
	}

	let resizeHandler = null
	onMounted(() => {
		resizeHandler = () => {
			windowWidth.value = window.innerWidth
		}
		window.addEventListener('resize', resizeHandler)
		// 立刻同步一次
		resizeHandler()
	})

	onUnmounted(() => {
		document.removeEventListener('mousemove', handleResize)
		document.removeEventListener('mouseup', stopResize)
		if (resizeHandler) window.removeEventListener('resize', resizeHandler)
	})

	// 暴露方法供外部调用
	defineExpose({
		setSize: (size) => {
			leftSize.value = Math.max(props.minSize, Math.min(props.maxSize, size))
		},
		getSize: () => leftSize.value
	})
</script>

<style scoped>
	.resizable-panel {
		height: 100%;
		width: 100%;
	}

	.panel-left {
		padding: 24px;
		overflow: auto;
		background: var(--snowy-background-color);
		/* 隐藏滚动条但保持滚动功能 */
		scrollbar-width: none; /* Firefox */
		-ms-overflow-style: none; /* IE 和 Edge */
	}

	.panel-left::-webkit-scrollbar {
		display: none; /* Chrome, Safari 和 Opera */
	}

	.panel-right {
		padding: 24px;
		overflow: auto;
		background: var(--snowy-background-color);
		/* 隐藏滚动条但保持滚动功能 */
		scrollbar-width: none; /* Firefox */
		-ms-overflow-style: none; /* IE 和 Edge */
	}

	.panel-right::-webkit-scrollbar {
		display: none; /* Chrome, Safari 和 Opera */
	}

	.resizer {
		position: relative;
		background: transparent;
		user-select: none;
		transition:
			background-color 0.2s,
			box-shadow 0.2s;
	}

	/* 增大可点击区域，居中握把，便于拖拽 */
	.resizer-horizontal {
		width: 8px;
		cursor: col-resize;
	}

	.resizer-vertical {
		height: 8px;
		cursor: row-resize;
		box-shadow:
			inset 0 -1px 0 var(--border-color-base),
			inset 0 1px 0 var(--border-color-base);
	}

	/* 中间握把（胶囊形），提升可拖拽直觉性 */
	.resizer-handle {
		position: absolute;
		top: 50%;
		left: 50%;
		transform: translate(-50%, -50%);
		background: var(--component-background);
		border-radius: 999px;
		box-shadow:
			0 2px 10px rgba(0, 0, 0, 0.06),
			inset 0 0 0 1px var(--border-color-base);
	}

	/* 横向握把：竖排 3 个点 */
	.resizer-horizontal .resizer-handle {
		width: 6px;
		height: 26px;
	}

	.resizer-horizontal .resizer-handle::before {
		content: '';
		display: block;
		position: absolute;
		top: 50%;
		left: 50%;
		transform: translate(-50%, -50%);
		width: 2px;
		height: 2px;
		background: var(--border-color-base);
		border-radius: 50%;
		box-shadow:
			0 7px 0 0 var(--border-color-base),
			0 -7px 0 0 var(--border-color-base);
	}

	/* 纵向握把：横排 3 个点 */
	.resizer-vertical .resizer-handle {
		width: 26px;
		height: 6px;
	}

	.resizer-vertical .resizer-handle::before {
		content: '';
		display: block;
		position: absolute;
		top: 50%;
		left: 50%;
		transform: translate(-50%, -50%);
		width: 2px;
		height: 2px;
		background: var(--border-color-base);
		border-radius: 50%;
		box-shadow:
			7px 0 0 0 var(--border-color-base),
			-7px 0 0 0 var(--border-color-base);
	}

	/* 悬停与按下反馈 */
	.resizer:hover .resizer-handle {
		box-shadow:
			0 4px 14px rgba(0, 0, 0, 0.1),
			inset 0 0 0 1px var(--primary-color);
	}

	.resizer:active .resizer-handle {
		transform: translate(-50%, -50%) scale(0.98);
	}
</style>
