<template>
	<a-modal
		:class="['my-modal', modalClass, simpleClass]"
		:open="visible"
		v-bind="props"
		:width="modalWidth"
		:wrap-class-name="wrapClassName + fullscreenClass"
		@cancel="handleCancel"
	>
		<template #closeIcon>
			<template v-if="fullscreen">
				<a-tooltip title="还原" placement="bottom" v-if="fullscreenStatus">
					<fullscreen-exit-outlined @click="handleFullScreen" />
				</a-tooltip>
				<a-tooltip title="最大化" placement="bottom" v-else>
					<fullscreen-outlined @click="handleFullScreen" />
				</a-tooltip>
			</template>
			<a-tooltip title="关闭" placement="bottom">
				<close-outlined />
			</a-tooltip>
		</template>

		<slot></slot>

		<template #footer>
			<slot name="insertFooter"></slot>
			<slot name="footer">
				<a-button @click="handleCancel">
					{{ props.cancelText || '取消' }}
				</a-button>
				<slot name="centerFooter"></slot>
				<a-button type="primary" @click="handleOk" :loading="loading">
					{{ props.okText || '确定' }}
				</a-button>
			</slot>
			<slot name="appendFooter"></slot>
		</template>
	</a-modal>
</template>
<script setup>
	import mixinProps from './props.js'
	import { useSlots } from 'vue'
	const slots = useSlots()
	const props = defineProps({
		...mixinProps,
		// 容器的类名
		modalClass: {
			type: String,
			default: 'modal-box'
		},
		// 对话框外层容器的类名
		wrapClassName: {
			type: String,
			default: ''
		},
		helpMessage: {
			type: String
		},
		// 可全屏
		fullscreen: {
			type: Boolean,
			default: true
		},
		// 可拖拽
		drag: {
			type: Boolean,
			default: true
		},
		// 可拉伸
		resize: {
			type: Boolean,
			default: false
		},
		// 是否显示
		visible: {
			type: Boolean,
			default: false
		},
		// 标题
		title: {
			type: String,
			default: undefined
		},
		// 宽度
		width: {
			type: [Number, String],
			default: '70%'
		},
		loading: {
			type: Boolean,
			default: undefined
		}
	})
	const emit = defineEmits(['ok', 'close', 'fullscreen'])
	const modalWidth = ref('')
	const contain = ref(null)
	// 拖拽
	const header = ref(null)
	const modalContent = ref(null)
	const mouseDownX = ref(0)
	const mouseDownY = ref(0)
	const deltaX = ref(0)
	const deltaY = ref(0)
	const sumX = ref(0)
	const sumY = ref(0)
	const onmousedown = ref(false)
	// 缩放
	const modalBody = ref(null)
	const myBody = ref(null)
	const prevModalWidth = ref(0)
	const prevModalHeight = ref(0)
	const prevBodyWidth = ref(0)
	const prevBodyHeight = ref(0)
	const startX = ref(0)
	const startY = ref(0)
	// 全屏
	const fullscreenClass = ref('')
	const fullscreenStatus = ref(false)
	const slotKeys = computed(() => {
		return Object.keys(slots)
	})
	const simpleClass = computed(() => {
		return Math.random().toString(36).substring(2)
	})
	onMounted(() => {
		nextTick(() => {
			initialEvent(props.visible)
		})
	})
	watch(
		() => props.visible,
		(newValue) => {
			nextTick(() => {
				initialEvent(props.visible)
			})
		}
	)
	watch(
		() => fullscreenStatus.value,
		(newValue) => {
			fullscreenClass.value = fullscreenStatus.value ? ' full-modal' : ''
		}
	)
	onBeforeUnmount(() => {
		removeMove()
		document.removeEventListener('mouseup', removeUp, false)
		removeResize()
		document.removeEventListener('mouseup', removeResize)
	})
	const changeWidth = (width) => {
		modalWidth.value = width
	}
	const handleFullScreen = (e) => {
		e?.stopPropagation()
		e?.preventDefault()

		fullscreenStatus.value = !fullscreenStatus.value
		emit('fullscreen', e)
	}
	const handleOk = (e) => {
		reset()
		emit('ok', e)
	}
	const handleCancel = (e) => {
		const classList = e.target?.classList
		// 过滤自定义关闭按钮的空白区域
		if (classList.contains('ant-modal-close-x') || classList.contains('ant-space-item')) {
			return
		}
		reset()
		emit('close', e)
	}
	const reset = () => {
		// 拖拽
		mouseDownX.value = 0
		mouseDownY.value = 0
		deltaX.value = 0
		deltaY.value = 0
		sumX.value = 0
		sumY.value = 0
		// 缩放
		prevModalWidth.value = 0
		prevModalHeight.value = 0
		prevBodyWidth.value = 0
		prevBodyHeight.value = 0
		startX.value = 0
		startY.value = 0
		// 全屏
		fullscreenStatus.value = false
	}
	const initialEvent = (visible) => {
		if (visible) {
			reset()
			// 获取控件
			document.removeEventListener('mouseup', removeUp, false)
			contain.value = document.getElementsByClassName(simpleClass.value)[0]
			changeWidth(props.width)
			if (props.drag === true) {
				header.value = contain.value.getElementsByClassName('ant-modal-header')[0]
				modalContent.value = contain.value.getElementsByClassName('ant-modal-content')[0]
				header.value.style.cursor = 'all-scroll'
				modalContent.value.style.left = 0
				modalContent.value.style.transform = 'translate(0px,0px)'
				// 拖拽事件监听
				header.value.onmousedown = (event) => {
					onmousedown.value = true
					mouseDownX.value = event.pageX
					mouseDownY.value = event.pageY
					document.body.onselectstart = () => false
					document.addEventListener('mousemove', handleMove, false)
				}
				document.addEventListener('mouseup', removeUp, false)
			}

			if (props.resize === true) {
				modalBody.value = contain.value.getElementsByClassName('ant-modal-content')[0]
				myBody.value = contain.value.getElementsByClassName('ant-modal-body')[0]
				modalBody.value.style.overflow = 'hidden'
				modalBody.value.style.resize = 'both'
				myBody.value.style.overflow = 'auto'
				myBody.value.style.height = 'auto'
				// 缩放事件监听
				modalBody.value.onmousedown = (event) => {
					event.preventDefault()
					const rect = modalBody.value.getBoundingClientRect()
					const rightBorder = rect.x + rect.width - 17
					const bottomBorder = rect.y + rect.height - 17
					if (event.clientX >= rightBorder && event.clientY >= bottomBorder) {
						prevModalWidth.value = modalBody.value.offsetWidth
						prevModalHeight.value = modalBody.value.offsetHeight
						prevBodyWidth.value = myBody.value.offsetWidth
						prevBodyHeight.value = myBody.value.offsetHeight
						startX.value = event.clientX
						startY.value = event.clientY
						document.addEventListener('mousemove', handleResize)
					}
					document.addEventListener('mouseup', removeResize)
				}
			}
		}
	}
	const handleMove = (event) => {
		if (fullscreenStatus.value) {
			return
		}
		const delta1X = event.pageX - mouseDownX.value
		const delta1Y = event.pageY - mouseDownY.value
		deltaX.value = delta1X
		deltaY.value = delta1Y
		modalContent.value.style.transform = `translate(${delta1X + sumX.value}px, ${delta1Y + sumY.value}px)`
	}
	const removeMove = () => {
		document.removeEventListener('mousemove', handleMove, false)
	}
	const removeUp = (event) => {
		document.body.onselectstart = () => true
		if (onmousedown.value && !(event.pageX === mouseDownX.value && event.pageY === mouseDownY.value)) {
			onmousedown.value = false
			sumX.value = sumX.value + deltaX.value
			sumY.value = sumY.value + deltaY.value
		}
		removeMove()
	}
	const handleResize = (event) => {
		if (fullscreenStatus.value) {
			return
		}
		const diffX = event.clientX - startX.value
		const diffY = event.clientY - startY.value
		const minWidth = 180
		const minHeight = 0
		if (prevBodyWidth.value + diffX > minWidth) {
			changeWidth(prevModalWidth.value + diffX + 'px')
		}
		if (prevBodyHeight.value + diffY > minHeight) {
			myBody.value.style.height = prevBodyHeight.value + diffY + 'px'
		}
	}
	const removeResize = () => {
		document.removeEventListener('mousemove', handleResize)
	}
</script>
<style lang="less">
	.ant-modal-close-x {
		margin-right: 10px;
		width: auto;

		.anticon {
			padding: 20px 10px;
		}
	}
	.full-modal {
		.ant-modal {
			top: 0 !important;
			right: 0 !important;
			bottom: 0 !important;
			left: 0 !important;
			width: 100% !important;
			height: 100% !important;
			max-width: 100% !important;
			max-height: 100% !important;
		}
		.ant-modal-content {
			display: flex;
			flex-direction: column;
			height: calc(100vh) !important;
			transform: translate(0px, 0px) !important;
			resize: none !important;
		}
		.ant-modal-header {
			cursor: default !important;
		}
		.ant-modal-body {
			flex: 1;
		}
	}
</style>
