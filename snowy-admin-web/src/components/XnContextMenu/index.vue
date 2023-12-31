<template>
	<div :style="style" v-show="show" @mousedown.stop @contextmenu.prevent>
		<slot></slot>
	</div>
</template>
<script setup>
	const props = defineProps({
		target: null,
		show: Boolean
	})

	const x = ref(null)
	const y = ref(null)
	const style = ref({})
	const binded = ref(false)
	const emit = defineEmits(['update:show', 'get-context-menu'])

	// 监听show的变化
	watch(
		() => props.show,
		(newValue) => {
			newValue ? bindHideEvents() : unbindHideEvents()
		}
	)
	watch(
		() => props.target,
		(newValue) => {
			bindEvents()
		}
	)

	// 初始化事件
	const bindEvents = (e) => {
		nextTick(() => {
			if (!props.target || binded.value) return
			props.target.addEventListener('contextmenu', contextMenuHandler)
			binded.value = true
		})
	}

	// 绑定隐藏菜单事件
	const bindHideEvents = () => {
		document.addEventListener('mousedown', clickDocumentHandler)
		document.addEventListener('mousewheel', clickDocumentHandler)
	}

	// 取消绑定隐藏菜单事件
	const unbindHideEvents = () => {
		document.removeEventListener('mousedown', clickDocumentHandler)
		document.removeEventListener('mousewheel', clickDocumentHandler)
	}

	// 鼠标按压事件处理器
	const clickDocumentHandler = () => {
		emit('update:show', false)
	}

	// 右键事件事件处理
	const contextMenuHandler = (e) => {
		x.value = e.clientX
		y.value = e.clientY
		layout()
		emit('update:show', true)
		emit('get-context-menu', e)
		e.preventDefault()
	}

	// 布局
	const layout = () => {
		style.value = {
			left: x.value + 'px',
			top: y.value + 'px',
			display: 'block'
		}
	}

	// 取消绑定事件
	const unbindEvents = () => {
		if (!props.target) return
		props.target.removeEventListener('contextmenu', contextMenuHandler)
	}
</script>
