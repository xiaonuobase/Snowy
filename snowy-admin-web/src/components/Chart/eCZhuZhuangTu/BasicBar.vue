<template>
	<div id="BasicBar"></div>
</template>
<script setup name="BasicBar">
	import { onMounted } from 'vue'
	import * as echarts from 'echarts'

	onMounted(() => {
		let Echarts = echarts.init(document.getElementById('BasicBar'))
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
					data: [120, 200, 150, 80, 70, 110, 130],
					type: 'bar'
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
