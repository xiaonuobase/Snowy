<template>
	<div class="resizable-panel" :style="{ display: 'flex', flexDirection: direction }">
		<div
			class="panel-left"
			:style="{
				[sizeProperty]: leftSize + 'px',
				minWidth: direction === 'row' ? minSize + 'px' : 'auto',
				minHeight: direction === 'column' ? minSize + 'px' : 'auto'
			}"
		>
			<slot name="left"></slot>
		</div>
		<div
			class="resizer"
			:class="{ 'resizer-horizontal': direction === 'row', 'resizer-vertical': direction === 'column' }"
			@mousedown="startResize"
		></div>
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
		}
	})

	const emit = defineEmits(['resize'])

	const leftSize = ref(props.initialSize)
	const isResizing = ref(false)

	// 根据方向确定使用的CSS属性
	const sizeProperty = computed(() => {
		return props.direction === 'row' ? 'width' : 'height'
	})

	const startResize = (e) => {
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

	onUnmounted(() => {
		document.removeEventListener('mousemove', handleResize)
		document.removeEventListener('mouseup', stopResize)
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
		overflow: auto;
		background: var(--component-background);
		/* 隐藏滚动条但保持滚动功能 */
		scrollbar-width: none; /* Firefox */
		-ms-overflow-style: none; /* IE 和 Edge */
	}

	.panel-left::-webkit-scrollbar {
		display: none; /* Chrome, Safari 和 Opera */
	}

	.panel-right {
		overflow: auto;
		background: var(--component-background);
		/* 隐藏滚动条但保持滚动功能 */
		scrollbar-width: none; /* Firefox */
		-ms-overflow-style: none; /* IE 和 Edge */
	}

	.panel-right::-webkit-scrollbar {
		display: none; /* Chrome, Safari 和 Opera */
	}

	.resizer {
		background: var(--border-color-base);
		cursor: col-resize;
		user-select: none;
		transition: background-color 0.2s;
	}

	.resizer:hover {
		background: var(--primary-color);
	}

	.resizer-horizontal {
		width: 4px;
		cursor: col-resize;
	}

	.resizer-vertical {
		height: 4px;
		cursor: row-resize;
	}

	.resizer:active {
		background: var(--primary-color);
	}
</style>
