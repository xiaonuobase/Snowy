<template>
	<div id="NightingaleChart"></div>
</template>
<!--ec官网：https://echarts.apache.org/zh/index.html-->
<script setup name="NightingaleChart">
	import { onMounted } from 'vue'
	import * as echarts from 'echarts'

	onMounted(() => {
		let Echarts = echarts.init(document.getElementById('NightingaleChart'))
		const option = {
			legend: {
				top: 'bottom'
			},
			toolbox: {
				show: true,
				feature: {
					mark: { show: true },
					dataView: { show: true, readOnly: false },
					restore: { show: true },
					saveAsImage: { show: true }
				}
			},
			series: [
				{
					name: 'Nightingale Chart',
					type: 'pie',
					radius: [20, 120],
					center: ['50%', '50%'],
					roseType: 'area',
					itemStyle: {
						borderRadius: 8
					},
					data: [
						{ value: 40, name: 'rose 1' },
						{ value: 38, name: 'rose 2' },
						{ value: 32, name: 'rose 3' },
						{ value: 30, name: 'rose 4' },
						{ value: 28, name: 'rose 5' },
						{ value: 26, name: 'rose 6' },
						{ value: 22, name: 'rose 7' },
						{ value: 18, name: 'rose 8' }
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
