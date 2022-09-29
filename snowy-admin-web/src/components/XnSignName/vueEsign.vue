<!--
本插件来源于：https://github.com/JaimeCheng/vue-esign#readme
因为集成进来跟我的Vue版本不一致，打包出问题，所以集成源码方式，感谢作者的源码
-->
<template>
	<canvas
		ref="canvas"
		@mousedown="mouseDown"
		@mousemove="mouseMove"
		@mouseup="mouseUp"
		@touchstart="touchStart"
		@touchmove="touchMove"
		@touchend="touchEnd"
	></canvas>
</template>

<script>
	export default {
		props: {
			width: {
				type: Number,
				default: 800
			},
			height: {
				type: Number,
				default: 300
			},
			lineWidth: {
				type: Number,
				default: 4
			},
			lineColor: {
				type: String,
				default: '#000000'
			},
			bgColor: {
				type: String,
				default: ''
			},
			isCrop: {
				type: Boolean,
				default: false
			},
			isClearBgColor: {
				type: Boolean,
				default: true
			},
			format: {
				type: String,
				default: 'image/png'
			},
			quality: {
				type: Number,
				default: 1
			}
		},
		data() {
			return {
				hasDrew: false,
				resultImg: '',
				points: [],
				canvasTxt: null,
				startX: 0,
				startY: 0,
				isDrawing: false,
				sratio: 1
			}
		},
		computed: {
			ratio() {
				return this.height / this.width
			},
			stageInfo() {
				return this.$refs.canvas.getBoundingClientRect()
			},
			myBg() {
				return this.bgColor ? this.bgColor : 'rgba(255, 255, 255, 0)'
			}
		},
		watch: {
			myBg: function (newVal) {
				this.$refs.canvas.style.background = newVal
			}
		},
		beforeMount() {
			window.addEventListener('resize', this.$_resizeHandler)
		},
		// eslint-disable-next-line vue/no-deprecated-destroyed-lifecycle
		beforeDestroy() {
			window.removeEventListener('resize', this.$_resizeHandler)
		},
		mounted() {
			const canvas = this.$refs.canvas
			canvas.height = this.height
			canvas.width = this.width
			canvas.style.background = this.myBg
			this.$_resizeHandler()
			// 在画板以外松开鼠标后冻结画笔
			document.onmouseup = () => {
				this.isDrawing = false
			}
		},
		methods: {
			$_resizeHandler() {
				const canvas = this.$refs.canvas
				canvas.style.width = this.width + 'px'
				const realw = parseFloat(window.getComputedStyle(canvas).width)
				canvas.style.height = this.ratio * realw + 'px'
				this.canvasTxt = canvas.getContext('2d')
				this.canvasTxt.scale(Number(this.sratio), Number(this.sratio))
				this.sratio = realw / this.width
				this.canvasTxt.scale(1 / this.sratio, 1 / this.sratio)
			},
			// pc
			mouseDown(e) {
				e = e || event
				e.preventDefault()
				this.isDrawing = true
				this.hasDrew = true
				let obj = {
					x: e.offsetX,
					y: e.offsetY
				}
				this.drawStart(obj)
			},
			mouseMove(e) {
				e = e || event
				e.preventDefault()
				if (this.isDrawing) {
					let obj = {
						x: e.offsetX,
						y: e.offsetY
					}
					this.drawMove(obj)
				}
			},
			mouseUp(e) {
				e = e || event
				e.preventDefault()
				let obj = {
					x: e.offsetX,
					y: e.offsetY
				}
				this.drawEnd(obj)
				this.isDrawing = false
			},
			// mobile
			touchStart(e) {
				e = e || event
				e.preventDefault()
				this.hasDrew = true
				if (e.touches.length === 1) {
					let obj = {
						x: e.targetTouches[0].clientX - this.$refs.canvas.getBoundingClientRect().left,
						y: e.targetTouches[0].clientY - this.$refs.canvas.getBoundingClientRect().top
					}
					this.drawStart(obj)
				}
			},
			touchMove(e) {
				e = e || event
				e.preventDefault()
				if (e.touches.length === 1) {
					let obj = {
						x: e.targetTouches[0].clientX - this.$refs.canvas.getBoundingClientRect().left,
						y: e.targetTouches[0].clientY - this.$refs.canvas.getBoundingClientRect().top
					}
					this.drawMove(obj)
				}
			},
			touchEnd(e) {
				e = e || event
				e.preventDefault()
				if (e.touches.length === 1) {
					let obj = {
						x: e.targetTouches[0].clientX - this.$refs.canvas.getBoundingClientRect().left,
						y: e.targetTouches[0].clientY - this.$refs.canvas.getBoundingClientRect().top
					}
					this.drawEnd(obj)
				}
			},
			// 绘制
			drawStart(obj) {
				this.startX = obj.x
				this.startY = obj.y
				this.canvasTxt.beginPath()
				this.canvasTxt.moveTo(this.startX, this.startY)
				this.canvasTxt.lineTo(obj.x, obj.y)
				this.canvasTxt.lineCap = 'round'
				this.canvasTxt.lineJoin = 'round'
				this.canvasTxt.lineWidth = this.lineWidth * this.sratio
				this.canvasTxt.stroke()
				this.canvasTxt.closePath()
				this.points.push(obj)
			},
			drawMove(obj) {
				this.canvasTxt.beginPath()
				this.canvasTxt.moveTo(this.startX, this.startY)
				this.canvasTxt.lineTo(obj.x, obj.y)
				this.canvasTxt.strokeStyle = this.lineColor
				this.canvasTxt.lineWidth = this.lineWidth * this.sratio
				this.canvasTxt.lineCap = 'round'
				this.canvasTxt.lineJoin = 'round'
				this.canvasTxt.stroke()
				this.canvasTxt.closePath()
				this.startY = obj.y
				this.startX = obj.x
				this.points.push(obj)
			},
			drawEnd(obj) {
				this.canvasTxt.beginPath()
				this.canvasTxt.moveTo(this.startX, this.startY)
				this.canvasTxt.lineTo(obj.x, obj.y)
				this.canvasTxt.lineCap = 'round'
				this.canvasTxt.lineJoin = 'round'
				this.canvasTxt.stroke()
				this.canvasTxt.closePath()
				this.points.push(obj)
				this.points.push({ x: -1, y: -1 })
			},
			// 操作
			generate(options) {
				let imgFormat = options && options.format ? options.format : this.format
				let imgQuality = options && options.quality ? options.quality : this.quality
				const pm = new Promise((resolve, reject) => {
					if (!this.hasDrew) {
						reject(`Warning: Not Signned!`)
						return
					}
					var resImgData = this.canvasTxt.getImageData(0, 0, this.$refs.canvas.width, this.$refs.canvas.height)
					this.canvasTxt.globalCompositeOperation = 'destination-over'
					this.canvasTxt.fillStyle = this.myBg
					this.canvasTxt.fillRect(0, 0, this.$refs.canvas.width, this.$refs.canvas.height)
					this.resultImg = this.$refs.canvas.toDataURL(imgFormat, imgQuality)
					var resultImg = this.resultImg
					this.canvasTxt.clearRect(0, 0, this.$refs.canvas.width, this.$refs.canvas.height)
					this.canvasTxt.putImageData(resImgData, 0, 0)
					this.canvasTxt.globalCompositeOperation = 'source-over'
					if (this.isCrop) {
						const crop_area = this.getCropArea(resImgData.data)
						var crop_canvas = document.createElement('canvas')
						const crop_ctx = crop_canvas.getContext('2d')
						crop_canvas.width = crop_area[2] - crop_area[0]
						crop_canvas.height = crop_area[3] - crop_area[1]
						const crop_imgData = this.canvasTxt.getImageData(...crop_area)
						crop_ctx.globalCompositeOperation = 'destination-over'
						crop_ctx.putImageData(crop_imgData, 0, 0)
						crop_ctx.fillStyle = this.myBg
						crop_ctx.fillRect(0, 0, crop_canvas.width, crop_canvas.height)
						resultImg = crop_canvas.toDataURL(imgFormat, imgQuality)
						crop_canvas = null
					}
					resolve(resultImg)
				})
				return pm
			},
			reset() {
				this.canvasTxt.clearRect(0, 0, this.$refs.canvas.width, this.$refs.canvas.height)
				if (this.isClearBgColor) {
					this.$emit('update:bgColor', '')
					this.$refs.canvas.style.background = 'rgba(255, 255, 255, 0)'
				}
				this.points = []
				this.hasDrew = false
				this.resultImg = ''
			},
			getCropArea(imgData) {
				var topX = this.$refs.canvas.width
				var btmX = 0
				var topY = this.$refs.canvas.height
				var btnY = 0
				for (var i = 0; i < this.$refs.canvas.width; i++) {
					for (var j = 0; j < this.$refs.canvas.height; j++) {
						var pos = (i + this.$refs.canvas.width * j) * 4
						if (imgData[pos] > 0 || imgData[pos + 1] > 0 || imgData[pos + 2] || imgData[pos + 3] > 0) {
							btnY = Math.max(j, btnY)
							btmX = Math.max(i, btmX)
							topY = Math.min(j, topY)
							topX = Math.min(i, topX)
						}
					}
				}
				topX++
				btmX++
				topY++
				btnY++
				const data = [topX, topY, btmX, btnY]
				return data
			}
		}
	}
</script>

<style scoped>
	canvas {
		max-width: 100%;
		display: block;
	}
</style>
