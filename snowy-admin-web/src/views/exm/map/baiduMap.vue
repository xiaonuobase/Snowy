<template>
	<baidu-map
		ref="map"
		api-key="NtTydKuftIVXAy526uWXZoHS86lg0KeW"
		@complete="handleComplete"
		@marker-click="handleMarkerClick"
	/>
</template>

<script setup name="exmBaiduMap">
	import BaiduMap from '@/components/Map/baiduMap/index.vue'
	// 使用该组件请再百度地图官网注册，申请应用获得key
	const map = ref(null)

	const handleComplete = () => {
		// 点标记
		// map.value.renderMarker(
		// 	[
		// 		{
		// 			position: [116.39, 39.9],
		// 			title: 'BI'
		// 		},
		// 		{
		// 			position: [116.33, 39.5],
		// 			title: 'BI-2'
		// 		}
		// 	]
		// )
		// 图标点标记
		// map.value.renderIconMarker(
		// 	[
		// 		{
		// 			position: [116.39, 39.9],
		// 			title: 'BI',
		// 			img: 'https://webmap0.bdimg.com/image/api/bg.png',
		// 			imgWidth: 40,
		// 			imgHeight: 40
		// 		},
		// 		{
		// 			position: [116.33, 39.5],
		// 			title: 'BI-2',
		// 			img: 'https://webmap0.bdimg.com/image/api/bg.png'
		// 		}
		// 	]
		// )
		// 3D圆点标记
		// map.value.render3DCircleMarker(
		// 	[
		// 		{
		// 			position: [116.39, 39.9],
		// 			height: 7000,
		// 			size: 40
		// 		},
		// 		{
		// 			position: [116.33, 39.5],
		// 			height: 7000,
		// 			size: 40
		// 		}
		// 	]
		// )
		// 3D图标标记
		// map.value.render3DIconMarker(
		// 	[
		// 		{
		// 			position: [116.39, 39.9],
		// 			height: 7000,
		// 			size: 40,
		// 			img: 'https://webmap0.bdimg.com/image/api/bg.png',
		// 			imgWidth: 40,
		// 			imgHeight: 40
		// 		},
		// 		{
		// 			position: [116.33, 39.5],
		// 			height: 7000,
		// 			img: 'https://webmap0.bdimg.com/image/api/bg.png'
		// 		}
		// 	]
		// )
		// 面
		// map.value.renderPolygon(
		// 	[
		// 		{
		// 			position: [116.39, 39.9]
		// 		},
		// 		{
		// 			position: [116.47, 39.8]
		// 		},
		// 		{
		// 			position: [116.46, 39.7]
		// 		},
		// 		{
		// 			position: [116.35, 39.6]
		// 		}
		// 	]
		// )
		// 信息窗体
		map.value.renderInfoWindow([
			{
				position: [116.39, 39.9],
				title: 'Snowy-小诺开源技术',
				content: [
					"<div style='padding:0'>",
					'网站 : https://www.xiaonuo.vip',
					'Snowy是一款国内首例国产密码算法加密框架，采用Vue3.0+AntDesignVue3.0+SpringBoot2.8前后分离技术打造，技术框架与密码的结合，让前后分离‘密’不可分！</div>'
				]
			},
			{
				position: [116.33, 39.5],
				title: 'Snowy-小诺开源技术',
				content: [
					"<div style='padding:0'>",
					'网站 : https://www.xiaonuo.vip',
					'Snowy是一款国内首例国产密码算法加密框架，采用Vue3.0+AntDesignVue3.0+SpringBoot2.8前后分离技术打造，技术框架与密码的结合，让前后分离‘密’不可分！</div>'
				],
				width: 300,
				height: 200
			}
		])
	}

	const handleMarkerClick = (position) => {
		map.value.openInfoWindow(position)
	}
</script>
