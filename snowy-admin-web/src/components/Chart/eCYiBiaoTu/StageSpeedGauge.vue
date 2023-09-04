<template>
	<div id="StageSpeedGauge"></div>
</template>
<script setup name="StageSpeedGauge">
	import { onMounted } from 'vue'
	import * as echarts from 'echarts'

	onMounted(() => {
		let Echarts = echarts.init(document.getElementById('StageSpeedGauge'))
		const option = {
			series: [
				{
					type: 'gauge',
					axisLine: {
						lineStyle: {
							width: 30,
							color: [
								[0.3, '#67e0e3'],
								[0.7, '#37a2da'],
								[1, '#fd666d']
							]
						}
					},
					pointer: {
						itemStyle: {
							color: 'auto'
						}
					},
					axisTick: {
						distance: -30,
						length: 8,
						lineStyle: {
							color: '#fff',
							width: 2
						}
					},
					splitLine: {
						distance: -30,
						length: 30,
						lineStyle: {
							color: '#fff',
							width: 4
						}
					},
					axisLabel: {
						color: 'auto',
						distance: 40,
						fontSize: 20
					},
					detail: {
						valueAnimation: true,
						formatter: '{value} km/h',
						color: 'auto'
					},
					data: [
						{
							value: 70
						}
					]
				}
			]
		}
		// 绘制图表
		Echarts.setOption(option)
		// 自适应大小
		window.onresize = () => {
			Echarts.resize()
		}
		setInterval(() => {
			Echarts.setOption({
				series: [
					{
						data: [
							{
								value: Number((Math.random() * 100).toFixed(2))
							}
						]
					}
				]
			})
		}, 2000)
	})
</script>
