<template>
	<div id="BasicLineChart"></div>
</template>
<script setup name="BasicLineChart">
	import { onMounted } from 'vue'
	import * as echarts from 'echarts'

	const option = {
		xAxis: {
			type: 'category',
			data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
		},
		yAxis: {
			type: 'value'
		},
		series: [
			{
				data: [150, 230, 224, 218, 135, 147, 260],
				type: 'line'
			}
		]
	}

	onMounted(() => {
		let Echarts = echarts.init(document.getElementById('BasicLineChart'))

		// 绘制图表
		Echarts.setOption(option)
		// 自适应大小
		window.onresize = () => {
			Echarts.resize()
		}
	})
</script>
