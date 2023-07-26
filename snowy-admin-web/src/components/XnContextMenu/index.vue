<template>
	<div :style="style" v-show="show" @mousedown.stop @contextmenu.prevent>
		<slot></slot>
	</div>
</template>

<script>
	export default {
		name: 'XnContextMenu',
		props: {
			target: null,
			show: Boolean
		},
		data() {
			return {
				triggerShowFn: () => {},
				triggerHideFn: () => {},
				x: null,
				y: null,
				style: {},
				binded: false
			}
		},
		watch: {
			show(show) {
				if (show) {
					this.bindHideEvents()
				} else {
					this.unbindHideEvents()
				}
			},
			target(target) {
				this.bindEvents()
			}
		},
		mounted() {
			this.bindEvents()
		},
		methods: {
			// 初始化事件
			bindEvents() {
				this.$nextTick(() => {
					if (!this.target || this.binded) return
					this.triggerShowFn = this.contextMenuHandler.bind(this)
					this.target.addEventListener('contextmenu', this.triggerShowFn)
					this.binded = true
				})
			},
			// 取消绑定事件
			unbindEvents() {
				if (!this.target) return
				this.target.removeEventListener('contextmenu', this.triggerShowFn)
			},
			// 绑定隐藏菜单事件
			bindHideEvents() {
				this.triggerHideFn = this.clickDocumentHandler.bind(this)
				document.addEventListener('mousedown', this.triggerHideFn)
				document.addEventListener('mousewheel', this.triggerHideFn)
			},
			// 取消绑定隐藏菜单事件
			unbindHideEvents() {
				document.removeEventListener('mousedown', this.triggerHideFn)
				document.removeEventListener('mousewheel', this.triggerHideFn)
			},
			// 鼠标按压事件处理器
			clickDocumentHandler(e) {
				this.$emit('update:show', false)
			},
			// 右键事件事件处理
			contextMenuHandler(e) {
				//兼容ie写法
				let obj = e.srcElement ? e.srcElement : e.target
				let cs = obj.attributes['class']
				cs = cs ? cs.nodeValue : cs
				if(cs&&cs!=='ant-tabs-tab-remove'){
					this.x = e.clientX
					this.y = e.clientY
					this.layout()
					this.$emit('update:show', true)
					this.$emit('get-context-menu', e)
				}
				e.preventDefault()
			},
			// 布局
			layout() {
				this.style = {
					left: this.x + 'px',
					top: this.y + 'px',
					display: 'block'
				}
			}
		}
	}
</script>
