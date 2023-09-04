<template>
	<div id="BarChartWithNegativeValue"></div>
</template>
<script setup name="BarChartWithNegativeValue">
	import { onMounted } from 'vue'
	import * as echarts from 'echarts'

	const labelRight = {
		position: 'right'
	}

	const option = {
		title: {
			text: ''
		},
		tooltip: {
			trigger: 'axis',
			axisPointer: {
				type: 'shadow'
			}
		},
		grid: {
			top: 80,
			bottom: 30
		},
		xAxis: {
			type: 'value',
			position: 'top',
			splitLine: {
				lineStyle: {
					type: 'dashed'
				}
			}
		},
		yAxis: {
			type: 'category',
			axisLine: { show: false },
			axisLabel: { show: false },
			axisTick: { show: false },
			splitLine: { show: false },
			data: ['ten', 'nine', 'eight', 'seven', 'six', 'five', 'four', 'three', 'two', 'one']
		},
		series: [
			{
				name: 'Cost',
				type: 'bar',
				stack: 'Total',
				label: {
					show: true,
					formatter: '{b}'
				},
				data: [
					{ value: -0.07, label: labelRight },
					{ value: -0.09, label: labelRight },
					0.2,
					0.44,
					{ value: -0.23, label: labelRight },
					0.08,
					{ value: -0.17, label: labelRight },
					0.47,
					{ value: -0.36, label: labelRight },
					0.18
				]
			}
		]
	}

	onMounted(() => {
		let Echarts = echarts.init(document.getElementById('BarChartWithNegativeValue'))
		// 绘制图表
		Echarts.setOption(option)
		// 自适应大小
		window.onresize = () => {
			Echarts.resize()
		}
	})
</script>
