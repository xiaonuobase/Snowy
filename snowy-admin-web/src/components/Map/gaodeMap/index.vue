<template>
	<div class="gaodeMap" :style="{ height: `${height}px` }">
		<div :id="`container-${mid}`" class="xn-wh">地图资源加载中...</div>
	</div>
</template>
<!--AMap官网：https://lbs.amap.com/api/javascript-api-v2/summary-->
<script setup name="GaodeMap">
	import { onMounted, onUnmounted, shallowRef } from 'vue'
	import AMapLoader from '@amap/amap-jsapi-loader'

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
			default: ['AMap.ToolBar', 'AMap.Scale', 'AMap.HawkEye', 'AMap.MapType', 'AMap.Geolocation', 'AMap.MarkerCluster']
		},
		viewMode: {
			type: String,
			default: '3D',
			validator(value) {
				return ['2D', '3D'].includes(value)
			}
		},
		zoom: {
			type: Number,
			default: 12
		},
		pitch: {
			type: Number,
			default: 50
		},
		mapStyle: {
			type: String,
			default: 'normal',
			validator(value) {
				return ['normal', 'macaron', 'dark', 'fresh', 'grey'].includes(value)
			}
		},
		markerCluster: {
			type: Boolean,
			default: true
		}
	})

	const emits = defineEmits(['complete', 'markerClick'])

	const gaodeMap = shallowRef(null)
	const gaodeMapMarkerArr = ref([])
	const gaodeMapInfoWindowObj = ref({})

	const init = () => {
		AMapLoader.load({
			key: props.apiKey,
			version: '2.0',
			plugins: props.plugins,
			AMapUI: {
				version: '1.1',
				plugins: ['overlay/SimpleMarker', 'overlay/AwesomeMarker']
			}
		})
			.then(() => {
				initMap()
			})
			.catch((e) => {
				console.error(e)
			})
	}

	// 初始化 地图
	const initMap = () => {
		gaodeMap.value = new AMap.Map(`container-${props.mid}`, {
			viewMode: props.viewMode,
			zoom: props.zoom,
			// 地图俯仰角度
			pitch: props.pitch,
			mapStyle: `amap://styles/${props.mapStyle}`
		})

		// 中心点
		if (props.center) {
			gaodeMap.value.setCenter(props.center)
		}

		// 控件
		props.plugins.length > 0 && initControlPlugin()

		// 地图初始化完成
		gaodeMap.value.on('complete', () => {
			emits('complete')
		})
	}

	// 初始化 控制控件
	const initControlPlugin = () => {
		// 工具条，控制地图的缩放、平移等
		props.plugins.includes('AMap.ToolBar') && gaodeMap.value.addControl(new AMap.ToolBar({}))
		// 比例尺
		props.plugins.includes('AMap.Scale') && gaodeMap.value.addControl(new AMap.Scale())
		// 鹰眼，显示缩略图
		props.plugins.includes('AMap.HawkEye') && gaodeMap.value.addControl(new AMap.HawkEye({ isOpen: true }))
		// 图层切换
		props.plugins.includes('AMap.MapType') && gaodeMap.value.addControl(new AMap.MapType({}))
		// 定位
		props.plugins.includes('AMap.Geolocation') && gaodeMap.value.addControl(new AMap.Geolocation({}))
	}

	// 渲染 点标记
	const renderMarker = (dataArr) => {
		dataArr.forEach((d) => {
			const marker = new AMap.Marker({
				map: gaodeMap.value,
				position: d.position,
				// 鼠标滑过点标记时的文字提示
				title: d.title,
				// 显示内容：content有效时，icon属性将被覆盖
				content: d.content,
				// 图标
				icon: d.icon ? d.icon : null,
				// 文本标注
				label: d.label
			})
			marker.on('click', () => {
				emits('markerClick', d.position)
			})
			gaodeMapMarkerArr.value.push(marker)
		})

		setFitView()
	}

	// 渲染 圆点标记
	const renderCircleMarker = (dataArr) => {
		dataArr.forEach((d) => {
			const marker = new AMap.CircleMarker({
				map: gaodeMap.value,
				// 圆心位置
				center: d.position,
				// 圆点半径
				radius: d.radius ? d.radius : 20,
				// 线条颜色
				strokeColor: d.strokeColor ? d.strokeColor : '#006600',
				// 轮廓线透明度
				strokeOpacity: 0.5,
				// 轮廓线宽度
				strokeWeight: 2,
				// 填充颜色
				fillColor: d.fillColor ? d.fillColor : '#006600',
				// 填充透明度
				fillOpacity: 0.5,
				cursor: 'pointer'
			})
			marker.on('click', () => {
				emits('markerClick', d.position)
			})
			gaodeMapMarkerArr.value.push(marker)
		})

		setFitView()
	}

	// 渲染 简单点标记
	const renderSimpleMarker = (dataArr, theme = 'default') => {
		dataArr.forEach((d) => {
			const marker = new AMapUI.SimpleMarker({
				map: gaodeMap.value,
				position: d.position,
				// 前景文字
				iconLabel: {
					// 文本
					innerHTML: d.label,
					// 字体的样式，比如颜色，大小等
					style: d.labelStyle
						? d.labelStyle
						: {
								color: '#333',
								fontSize: '12px'
						  }
				},
				// 图标主题：default，fresh，numv1，numv2
				iconTheme: theme,
				// 背景图标样式
				iconStyle: d.style
			})
			marker.on('click', () => {
				emits('markerClick', d.position)
			})
			gaodeMapMarkerArr.value.push(marker)
		})

		setFitView()
	}

	// 渲染 字体点标记
	const renderAwesomeMarker = (dataArr) => {
		dataArr.forEach((d) => {
			const marker = new AMapUI.AwesomeMarker({
				map: gaodeMap.value,
				position: d.position,
				// 图标，参见：http://fontawesome.io/icons/
				awesomeIcon: d.awesomeIcon,
				// 字体的样式，比如颜色，大小等
				iconLabel: {
					style: d.labelStyle
						? d.labelStyle
						: {
								color: '#333',
								fontSize: '12px'
						  }
				},
				// 背景图标的样式
				iconStyle: d.style
			})
			marker.on('click', () => {
				emits('markerClick', d.position)
			})
			gaodeMapMarkerArr.value.push(marker)
		})

		setFitView()
	}

	// 设置 视图级别
	const setFitView = () => {
		// 点聚合
		props.markerCluster && new AMap.MarkerCluster(gaodeMap.value, gaodeMapMarkerArr.value)

		// 根据地图上添加的覆盖物分布情况，自动缩放地图到合适的视野级别
		gaodeMap.value.setFitView(gaodeMapMarkerArr.value)
	}

	// 渲染 线
	const renderPolyline = (dataArr, option = {}) => {
		const path = []
		dataArr.forEach((d) => {
			path.push(new AMap.LngLat(d.position[0], d.position[1]))
		})

		const polyline = new AMap.Polyline({
			path: path,
			strokeColor: option.strokeColor || 'blue',
			strokeWeight: option.strokeWeight || 2,
			strokeOpacity: option.strokeOpacity || 0.5,
			isOutline: option.isOutline || false,
			borderWeight: option.borderWeight || 1,
			// 折线拐点连接处样式
			lineJoin: 'round'
		})
		gaodeMap.value.add(polyline)

		gaodeMap.value.setFitView([polyline])
	}

	// 渲染 圆
	const renderCircle = (position, radius, option) => {
		const circle = new AMap.Circle({
			center: new AMap.LngLat(position[0], position[1]),
			radius: radius,
			strokeColor: option.strokeColor || 'blue',
			strokeWeight: option.strokeWeight || 2,
			strokeOpacity: option.strokeOpacity || 0.5,
			fillColor: option.fillColor || 'blue',
			fillOpacity: option.fillOpacity || 0.5,
			strokeStyle: 'solid'
		})
		gaodeMap.value.add(circle)

		gaodeMap.value.setFitView([circle])
	}

	// 渲染 面
	const renderPolygon = (dataArr, option = {}) => {
		const path = []
		dataArr.forEach((d) => {
			path.push(new AMap.LngLat(d.position[0], d.position[1]))
		})

		const polygon = new AMap.Polygon({
			path: path,
			strokeColor: option.strokeColor || 'blue',
			strokeWeight: option.strokeWeight || 2,
			strokeOpacity: option.strokeOpacity || 0.5,
			fillColor: option.fillColor || 'blue',
			fillOpacity: option.fillOpacity || 0.5,
			strokeStyle: 'solid'
		})
		gaodeMap.value.add(polygon)

		gaodeMap.value.setFitView([polygon])
	}

	// 渲染 信息窗体
	const renderInfoWindow = (dataArr) => {
		dataArr.forEach((d) => {
			gaodeMapInfoWindowObj.value[d.position] = new AMap.InfoWindow({
				// 显示内容
				content: d.content.join('<br>'),
				// 位置偏移量
				offset: new AMap.Pixel(0, -20),
				// 点击地图后关闭信息窗体
				closeWhenClickMap: true
			})
		})
	}

	// 打开 信息窗体
	const openInfoWindow = (position) => {
		const infoWindow = gaodeMapInfoWindowObj.value[position]
		if (infoWindow) {
			infoWindow.open(gaodeMap.value, position)
		}
	}

	// 清理 覆盖物
	const clearOverlay = () => {
		gaodeMap.value.clearMap()
	}

	onMounted(() => {
		init()
	})

	onUnmounted(() => {
		gaodeMap.value && gaodeMap.value.destroy()
	})

	defineExpose({
		renderMarker,
		renderCircleMarker,
		renderSimpleMarker,
		renderAwesomeMarker,
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
		height: 100%;
	}
	.gaodeMap {
		padding: 0;
		margin: 0;
		width: 100%;

		input[type='radio'] {
			-webkit-appearance: radio;
		}

		input[type='checkbox'] {
			-webkit-appearance: checkbox;
		}
	}
</style>
