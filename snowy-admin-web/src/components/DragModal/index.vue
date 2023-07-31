<template>
	<a-modal
		:class="['my-modal', modalClass, simpleClass]"
		:visible="visible"
		v-bind="$props"
		:width="modalWidth"
		:footer="null"
		:bodyStyle="{ padding: 0 }"
		@ok="handleOk"
		@cancel="handleCancel"
	>
		<div class="my-modal-body ant-modal-body" :style="bodyStyle">
			<slot></slot>
		</div>
		<div class="ant-modal-footer relative" v-if="footer === true">
			<slot name="footer">
				<a-button @click="handleCancel">取消</a-button>
				<a-button type="primary" @click="handleOk">确定</a-button>
			</slot>
		</div>
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
			// 拖拽
			drag: {
				type: Boolean,
				default: true
			},
			// 缩放
			resize: {
				type: Boolean,
				default: false
			},
			visible: {
				type: Boolean,
				default: false
			},
			title: {
				type: String,
				default: undefined
			},
			width: {
				type: [Number, String],
				default: '70%'
			},
			footer: {
				type: Boolean,
				default: true
			}
		},
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
				startY: 0
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
			handleOk(e) {
				this.resetNum()
				this.$emit('ok', e)
			},
			handleCancel(e) {
				this.resetNum()
				this.$emit('close', e)
			},
			resetNum() {
				this.mouseDownX = 0
				this.mouseDownY = 0
				this.deltaX = 0
				this.deltaY = 0
				this.sumX = 0
				this.sumY = 0

				this.prevModalWidth = 0
				this.prevModalHeight = 0
				this.prevBodyWidth = 0
				this.prevBodyHeight = 0
				this.startX = 0
				this.startY = 0
			},
			initialEvent(visible) {
				if (visible) {
					this.resetNum()
					// 获取控件
					document.removeEventListener('mouseup', this.removeUp, false)
					this.contain = document.getElementsByClassName(this.simpleClass)[0]
					this.changeWidth(this.$props.width)
					if (this.$props.drag === true) {
						this.header = this.contain.getElementsByClassName('ant-modal-header')[0]
						this.modalContent = this.contain.getElementsByClassName('ant-modal-content')[0]
						this.header.style.cursor = 'all-scroll'
						this.modalContent.style.left = 0
						this.modalContent.style.transform = 'translate(0px,0px)'
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
						this.modalBody = this.contain.getElementsByClassName('ant-modal-body')[0]
						this.myBody = this.contain.getElementsByClassName('my-modal-body')[0]
						this.modalBody.style.overflow = 'hidden'
						this.modalBody.style.resize = 'both'
						this.myBody.style.overflow = 'auto'
						this.myBody.style.height = 'auto'
						// 缩放事件监听
						this.modalBody.onmousedown = (event) => {
							event.preventDefault()
							const rect = this.modalBody.getBoundingClientRect()
							const rightBorder = rect.x + rect.width - 17
							const bottomBorder = rect.y + rect.height - 17
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
				const delta1X = event.pageX - this.mouseDownX
				const delta1Y = event.pageY - this.mouseDownY
				this.deltaX = delta1X
				this.deltaY = delta1Y
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
				}
				this.removeMove()
			},
			handleResize(event) {
				const diffX = event.clientX - this.startX
				const diffY = event.clientY - this.startY
				const minWidth = 180
				const minHeight = 0
				if (this.prevBodyWidth + diffX > minWidth) {
					this.changeWidth(this.prevModalWidth + diffX + 'px')
				}
				if (this.prevBodyHeight + diffY > minHeight) {
					this.myBody.style.height = this.prevBodyHeight + diffY + 'px'
				}
			},
			removeResize() {
				document.removeEventListener('mousemove', this.handleResize)
			}
		}
	}
</script>
