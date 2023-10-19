<template>
	<a-modal
		:class="['my-modal', modalClass, simpleClass]"
		:visible="visible"
		v-bind="$props"
		:width="modalWidth"
		:wrap-class-name="wrapClassName + fullscreenClass"
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
				<close-outlined @click="handleCancel" />
			</a-tooltip>
		</template>

		<slot></slot>

		<template #footer>
			<slot name="insertFooter"></slot>
			<slot name="footer">
				<a-button @click="handleCancel">
					{{ $props.cancelText || '取消' }}
				</a-button>
				<slot name="centerFooter"></slot>
				<a-button type="primary" @click="handleOk" :loading="loading">
					{{ $props.okText || '确定' }}
				</a-button>
			</slot>
			<slot name="appendFooter"></slot>
		</template>
	</a-modal>
</template>
<script>
	import props from './props.js'

	export default {
		name: 'DragModal',
		mixins: [props],
		props: {
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
		},
		emits: ['ok', 'close', 'fullscreen'],
		data() {
			return {
				modalWidth: '',
				contain: null,
				// 拖拽
				header: null,
				modalContent: null,
				mouseDownX: 0,
				mouseDownY: 0,
				deltaX: 0,
				deltaY: 0,
				sumX: 0,
				sumY: 0,
				onmousedown: false,
				// 缩放
				modalBody: null,
				myBody: null,
				prevModalWidth: 0,
				prevModalHeight: 0,
				prevBodyWidth: 0,
				prevBodyHeight: 0,
				startX: 0,
				startY: 0,
				// 全屏
				fullscreenClass: '',
				fullscreenStatus: false
			}
		},
		computed: {
			slotKeys() {
				return Object.keys(this.$slots)
			},
			simpleClass() {
				return Math.random().toString(36).substring(2)
			}
		},
		watch: {
			visible() {
				this.$nextTick(() => {
					this.initialEvent(this.visible)
				})
			},
			fullscreenStatus() {
				this.fullscreenClass = this.fullscreenStatus ? ' full-modal' : ''
			}
		},
		mounted() {
			this.$nextTick(() => {
				this.initialEvent(this.visible)
			})
		},
		created() {},
		beforeUnmount() {
			this.removeMove()
			document.removeEventListener('mouseup', this.removeUp, false)
			this.removeResize()
			document.removeEventListener('mouseup', this.removeResize)
		},
		methods: {
			changeWidth(width) {
				this.modalWidth = width
			},
			getPopupContainer(trigger) {
				return trigger?.parentElement ?? document.body
			},
			handleFullScreen(e) {
				e?.stopPropagation()
				e?.preventDefault()

				this.fullscreenStatus = !this.fullscreenStatus
				this.$emit('fullscreen', e)
			},
			handleOk(e) {
				this.reset()
				this.$emit('ok', e)
			},
			handleCancel(e) {
				const classList = e.target?.classList
				// 过滤自定义关闭按钮的空白区域
				if (classList.contains('ant-modal-close-x') || classList.contains('ant-space-item')) {
					return
				}
				this.reset()
				this.$emit('close', e)
			},
			reset() {
				// 拖拽
				this.mouseDownX = 0
				this.mouseDownY = 0
				this.deltaX = 0
				this.deltaY = 0
				this.sumX = 0
				this.sumY = 0
				// 缩放
				this.prevModalWidth = 0
				this.prevModalHeight = 0
				this.prevBodyWidth = 0
				this.prevBodyHeight = 0
				this.startX = 0
				this.startY = 0
				// 全屏
				this.fullscreenStatus = false
			},
			initialEvent(visible) {
				// console.log('--------- 初始化')
				// console.log('simpleClass===>', this.simpleClass)
				// console.log('document===>', document)
				if (visible) {
					this.reset()
					// 获取控件
					document.removeEventListener('mouseup', this.removeUp, false)
					this.contain = document.getElementsByClassName(this.simpleClass)[0]
					// console.log('初始化-contain:', this.contain)
					this.changeWidth(this.$props.width)
					if (this.$props.drag === true) {
						this.header = this.contain.getElementsByClassName('ant-modal-header')[0]
						this.modalContent = this.contain.getElementsByClassName('ant-modal-content')[0]
						this.header.style.cursor = 'all-scroll'
						this.modalContent.style.left = 0
						this.modalContent.style.transform = 'translate(0px,0px)'
						// console.log('初始化-header:', this.header)
						// console.log('初始化-modalContent:', this.modalContent)
						// 拖拽事件监听
						// this.contain.onmousedown = (event) => {
						this.header.onmousedown = (event) => {
							this.onmousedown = true
							this.mouseDownX = event.pageX
							this.mouseDownY = event.pageY
							document.body.onselectstart = () => false
							document.addEventListener('mousemove', this.handleMove, false)
						}
						document.addEventListener('mouseup', this.removeUp, false)
					}

					if (this.$props.resize === true) {
						this.modalBody = this.contain.getElementsByClassName('ant-modal-content')[0]
						this.myBody = this.contain.getElementsByClassName('ant-modal-body')[0]
						this.modalBody.style.overflow = 'hidden'
						this.modalBody.style.resize = 'both'
						this.myBody.style.overflow = 'auto'
						this.myBody.style.height = 'auto'
						// console.log('初始化-modalBody:', this.modalBody)
						// console.log('初始化-myBody:', this.myBody)
						// 缩放事件监听
						this.modalBody.onmousedown = (event) => {
							event.preventDefault()
							const rect = this.modalBody.getBoundingClientRect()
							const rightBorder = rect.x + rect.width - 17
							const bottomBorder = rect.y + rect.height - 17
							// console.log('rightBorder:' + rightBorder, 'clientX:' + event.clientX)
							// console.log('bottomBorder:' + bottomBorder, 'clientY:' + event.clientY)
							if (event.clientX >= rightBorder && event.clientY >= bottomBorder) {
								this.prevModalWidth = this.modalBody.offsetWidth
								this.prevModalHeight = this.modalBody.offsetHeight
								this.prevBodyWidth = this.myBody.offsetWidth
								this.prevBodyHeight = this.myBody.offsetHeight
								this.startX = event.clientX
								this.startY = event.clientY

								document.addEventListener('mousemove', this.handleResize)
							}
							document.addEventListener('mouseup', this.removeResize)
						}
					}
				}
			},
			handleMove(event) {
				if (this.fullscreenStatus) {
					return
				}
				const delta1X = event.pageX - this.mouseDownX
				const delta1Y = event.pageY - this.mouseDownY
				this.deltaX = delta1X
				this.deltaY = delta1Y
				// console.log('delta1X:' + delta1X, 'sumX:' + this.sumX, 'delta1Y:' + delta1Y, 'sumY:' + this.sumY)
				this.modalContent.style.transform = `translate(${delta1X + this.sumX}px, ${delta1Y + this.sumY}px)`
			},
			removeMove() {
				document.removeEventListener('mousemove', this.handleMove, false)
			},
			removeUp(event) {
				document.body.onselectstart = () => true
				if (this.onmousedown && !(event.pageX === this.mouseDownX && event.pageY === this.mouseDownY)) {
					this.onmousedown = false
					this.sumX = this.sumX + this.deltaX
					this.sumY = this.sumY + this.deltaY
					// console.log('sumX:' + this.sumX, 'sumY:' + this.sumY)
				}
				this.removeMove()
				// this.checkMove()
			},
			handleResize(event) {
				if (this.fullscreenStatus) {
					return
				}
				const diffX = event.clientX - this.startX
				const diffY = event.clientY - this.startY
				const minWidth = 180
				const minHeight = 0

				if (this.prevBodyWidth + diffX > minWidth) {
					this.changeWidth(this.prevModalWidth + diffX + 'px')
					// this.myBody.style.width = this.prevBodyWidth + diffX + 'px'
				}
				if (this.prevBodyHeight + diffY > minHeight) {
					// this.modalBody.style.height = this.prevModalHeight + diffY + 'px'
					this.myBody.style.height = this.prevBodyHeight + diffY + 'px'
				}
			},
			removeResize() {
				document.removeEventListener('mousemove', this.handleResize)
			}
		}
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
