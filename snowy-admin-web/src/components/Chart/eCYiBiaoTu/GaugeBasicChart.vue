<template>
	<div id="GaugeBasicChart"></div>
</template>
<script setup name="GaugeBasicChart">
	import { onMounted } from 'vue'
	import * as echarts from 'echarts'

	onMounted(() => {
		let Echarts = echarts.init(document.getElementById('GaugeBasicChart'))
		const option = {
			tooltip: {
				formatter: '{a} <br/>{b} : {c}%'
			},
			series: [
				{
					name: 'Pressure',
					type: 'gauge',
					detail: {
						formatter: '{value}'
					},
					data: [
						{
							value: 50,
							name: 'SCORE'
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
	})
</script>
