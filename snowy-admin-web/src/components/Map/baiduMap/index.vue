<template>
	<div class="baiduMap" :style="{ height: `${height}px` }">
		<div :id="`container-${mid}`" class="xn-wh">地图资源加载中...</div>
	</div>
</template>
<!--BMapGL官网：https://lbsyun.baidu.com/index.php?title=jspopularGL-->
<script setup name="baiduMap">
	import { onMounted, onUnmounted, shallowRef } from 'vue'

	const props = defineProps({
		mid: {
			type: Number,
			default: new Date().getTime()
		},
		height: {
			type: Number,
			default: 800
		},
		apiKey: {
			type: String,
			required: true
		},
		center: {
			type: Array
		},
		plugins: {
			type: Array,
			// eslint-disable-next-line vue/require-valid-default-prop
			default: ['BMap.ScaleControl', 'BMap.ZoomControl', 'BMap.LocationControl', 'BMap.NavigationControl3D']
		},
		viewMode: {
			type: String,
			default: '3D',
			validator(value) {
				return ['2D', '3D'].includes(value)
			}
		},
		rotationAngle: {
			type: Number,
			default: 60
		},
		tiltAngle: {
			type: Number,
			default: 70
		},
		zoom: {
			type: Number,
			default: 12
		},
		mapStyle: {
			type: String
		}
	})

	const emits = defineEmits(['complete', 'markerClick'])
	const baiduMap = shallowRef(null)
	const baiduMapPointArr = ref([])
	const baiduMapInfoWindowObj = ref({})

	const init = () => {
		// 创建script脚本 引入api
		const script = document.createElement('script')
		script.src = `https://api.map.baidu.com/api?v=1.0&&type=webgl&ak=${props.apiKey}&callback=initMap`
		// 加入head 加载api
		const head = document.getElementsByTagName('head')[0]
		head.appendChild(script)
	}

	// 初始化 地图
	window.initMap = () => {
		baiduMap.value = new BMapGL.Map(`container-${props.mid}`)
		// 滚轮放大缩小
		baiduMap.value.enableScrollWheelZoom(true)
		// 地图样式：个性化地图
		if (props.mapStyle) {
			baiduMap.value.setMapStyleV2(props.mapStyle)
		}
		// 控件
		props.plugins.length > 0 && initControlPlugin()
		// 中心点
		if (props.center) {
			setFitView(new BMapGL.Point(props.center[0], props.center[1]))

			// 地图初始化完成
			emits('complete')
		} else {
			// 浏览器定位
			new BMapGL.Geolocation().getCurrentPosition((r) => {
				if (r) {
					setFitView(new BMapGL.Point(r.longitude, r.latitude))
				} else {
					setFitView()
				}
				// 地图初始化完成
				emits('complete')
			})
		}
	}

	// 初始化 控制控件
	const initControlPlugin = () => {
		// 比例尺
		props.plugins.includes('BMap.ScaleControl') && baiduMap.value.addControl(new BMapGL.ScaleControl())
		// 缩放
		props.plugins.includes('BMap.ZoomControl') && baiduMap.value.addControl(new BMapGL.ZoomControl())
		// 定位
		props.plugins.includes('BMap.LocationControl') && baiduMap.value.addControl(new BMapGL.LocationControl())
		// 3D控件
		props.plugins.includes('BMap.NavigationControl3D') && baiduMap.value.addControl(new BMapGL.NavigationControl3D())
	}

	// 渲染 点标记
	const renderMarker = (dataArr) => {
		dataArr.forEach((d) => {
			const point = new BMapGL.Point(d.position[0], d.position[1])
			const marker = new BMapGL.Marker(
				// 坐标
				point,
				{
					// 鼠标滑过点标记时的文字提示
					title: d.title
				}
			)
			marker.addEventListener('click', () => {
				emits('markerClick', d.position)
			})
			baiduMap.value.addOverlay(marker)

			baiduMapPointArr.value.push(point)
		})

		setFitView()
	}

	// 渲染 图标标记
	const renderIconMarker = (dataArr) => {
		dataArr.forEach((d) => {
			const point = new BMapGL.Point(d.position[0], d.position[1])
			const marker = new BMapGL.Marker(
				// 坐标
				point,
				{
					// 鼠标滑过点标记时的文字提示
					title: d.title,
					// 图标对象
					icon: new BMapGL.Icon(d.img, new BMapGL.Size(d.imgWidth ? d.imgWidth : 40, d.imgHeight ? d.imgHeight : 40))
				}
			)
			marker.addEventListener('click', () => {
				emits('markerClick', d.position)
			})
			baiduMap.value.addOverlay(marker)

			baiduMapPointArr.value.push(point)
		})

		setFitView()
	}

	// 渲染 3D圆点标记
	const render3DCircleMarker = (dataArr) => {
		dataArr.forEach((d) => {
			const point = new BMapGL.Point(d.position[0], d.position[1])
			const marker = new BMapGL.Marker3D(
				// 坐标
				point,
				// 高度，默认8000
				d.height ? d.height : 8000,
				// 自定义样式
				{
					// 大小，默认50
					size: d.size ? d.size : 50,
					// 形状，默认圆形
					shape: 'BMAP_SHAPE_CIRCLE',
					// 填充颜色
					fillColor: d.fillColor ? d.fillColor : '#006600',
					// 填充透明度
					fillOpacity: 0.5
				}
			)
			marker.addEventListener('click', () => {
				emits('markerClick', d.position)
			})
			baiduMap.value.addOverlay(marker)

			baiduMapPointArr.value.push(point)
		})

		setFitView()
	}

	// 渲染 3D图标标记
	const render3DIconMarker = (dataArr) => {
		dataArr.forEach((d) => {
			const point = new BMapGL.Point(d.position[0], d.position[1])
			const marker = new BMapGL.Marker3D(
				// 坐标
				point,
				// 高度，默认8000
				d.height ? d.height : 8000,
				// 自定义样式
				{
					// 大小，默认50
					size: d.size ? d.size : 50,
					// 图标对象
					icon: new BMapGL.Icon(d.img, new BMapGL.Size(d.imgWidth ? d.imgWidth : 40, d.imgHeight ? d.imgHeight : 40))
				}
			)
			marker.addEventListener('click', () => {
				emits('markerClick', d.position)
			})
			baiduMap.value.addOverlay(marker)

			baiduMapPointArr.value.push(point)
		})

		setFitView()
	}

	// 渲染 线
	const renderPolyline = (dataArr, option = {}) => {
		dataArr.forEach((d) => {
			baiduMapPointArr.value.push(new BMapGL.Point(d.position[0], d.position[1]))
		})

		const polyline = new BMapGL.Polyline(baiduMapPointArr.value, {
			stokeStyle: 'solid',
			strokeColor: option.strokeColor || 'blue',
			strokeWeight: option.strokeWeight || 2,
			strokeOpacity: option.strokeOpacity || 0.5
		})
		baiduMap.value.addOverlay(polyline)

		setFitView()
	}

	// 渲染 圆
	const renderCircle = (position, radius, option = {}) => {
		const point = new BMapGL.Point(position[0], position[1])
		baiduMapPointArr.value.push(point)

		const circle = new BMapGL.Circle(point, radius ? radius : 500, {
			strokeColor: option.strokeColor || 'blue',
			strokeWeight: option.strokeWeight || 2,
			strokeOpacity: option.strokeOpacity || 0.5
		})
		baiduMap.value.addOverlay(circle)

		setFitView()
	}

	// 渲染 面
	const renderPolygon = (dataArr, option = {}) => {
		dataArr.forEach((d) => {
			baiduMapPointArr.value.push(new BMapGL.Point(d.position[0], d.position[1]))
		})

		const polygon = new BMapGL.Polygon(baiduMapPointArr.value, {
			stokeStyle: 'solid',
			strokeColor: option.strokeColor || 'blue',
			strokeWeight: option.strokeWeight || 2,
			strokeOpacity: option.strokeOpacity || 0.5,
			fillColor: option.fillColor || 'blue',
			fllOpacity: option.fllOpacity || 0.5
		})
		baiduMap.value.addOverlay(polygon)

		setFitView()
	}

	// 设置 视图级别
	const setFitView = (point) => {
		if (!point) {
			const viewPort = baiduMap.value.getViewport(baiduMapPointArr.value)
			point = new BMapGL.Point(viewPort.center.lng, viewPort.center.lat)
		}
		baiduMap.value.centerAndZoom(point, props.zoom)

		// 3D视图
		if (props.viewMode === '3D') {
			// 地图旋转角度
			baiduMap.value.setHeading(props.rotationAngle)
			// 地图倾斜角度
			baiduMap.value.setTilt(props.tiltAngle)
		}
	}

	// 渲染 信息窗体
	const renderInfoWindow = (dataArr) => {
		dataArr.forEach((d) => {
			baiduMapInfoWindowObj.value[d.position] = new BMapGL.InfoWindow(d.content.join('<br>'), {
				title: d.title,
				width: d.width ? d.width : 250,
				height: d.height ? d.height : 100
			})
		})
	}

	// 打开 信息窗体
	const openInfoWindow = (position) => {
		const infoWindow = baiduMapInfoWindowObj.value[position]
		if (infoWindow) {
			baiduMap.value.openInfoWindow(infoWindow, new BMapGL.Point(position[0], position[1]))
		}
	}

	// 清理 覆盖物
	const clearOverlay = () => {
		baiduMap.value.clearOverlays()
	}

	onMounted(() => {
		init()
	})

	onUnmounted(() => {
		baiduMap.value && baiduMap.value.baseLayerAdded && baiduMap.value.destroy()
	})

	defineExpose({
		renderMarker,
		renderIconMarker,
		render3DCircleMarker,
		render3DIconMarker,
		renderPolyline,
		renderCircle,
		renderPolygon,
		renderInfoWindow,
		openInfoWindow,
		clearOverlay
	})
</script>

<style lang="less">
	.xn-wh {
		width: 100%;
		height: 100%
	}
	.baiduMap {
		padding: 0;
		margin: 0;
		width: 100%;
	}
</style>
