<template>
	<div id="BasicCandlestick"></div>
</template>
<!--ec官网：https://echarts.apache.org/zh/index.html-->
<script setup name="BasicCandlestick">
	import { onMounted } from 'vue'
	import * as echarts from 'echarts'

	onMounted(() => {
		let Echarts = echarts.init(document.getElementById('BasicCandlestick'))
		const option = {
			xAxis: {
				data: ['2017-10-24', '2017-10-25', '2017-10-26', '2017-10-27']
			},
			yAxis: {},
			series: [
				{
					type: 'candlestick',
					data: [
						[20, 34, 10, 38],
						[40, 35, 30, 50],
						[31, 38, 33, 44],
						[38, 15, 5, 42]
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
	})
</script>
