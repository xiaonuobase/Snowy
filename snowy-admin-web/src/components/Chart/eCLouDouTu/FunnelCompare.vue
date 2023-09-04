<template>
	<div id="FunnelCompare"></div>
</template>
<script setup name="FunnelCompare">
	import { onMounted } from 'vue'
	import * as echarts from 'echarts'

	const option = {
		title: {
			text: '',
			subtext: '',
			left: 'left',
			top: 'bottom'
		},
		tooltip: {
			trigger: 'item',
			formatter: '{a} <br/>{b} : {c}%'
		},
		toolbox: {
			show: true,
			orient: 'vertical',
			top: 'center',
			feature: {
				dataView: { readOnly: false },
				restore: {},
				saveAsImage: {}
			}
		},
		legend: {
			orient: 'vertical',
			left: 'left',
			data: ['Prod A', 'Prod B', 'Prod C', 'Prod D', 'Prod E']
		},
		series: [
			{
				name: 'Funnel',
				type: 'funnel',
				width: '40%',
				height: '45%',
				left: '5%',
				top: '50%',
				funnelAlign: 'right',
				data: [
					{ value: 60, name: 'Prod C' },
					{ value: 30, name: 'Prod D' },
					{ value: 10, name: 'Prod E' },
					{ value: 80, name: 'Prod B' },
					{ value: 100, name: 'Prod A' }
				]
			},
			{
				name: 'Pyramid',
				type: 'funnel',
				width: '40%',
				height: '45%',
				left: '5%',
				top: '5%',
				sort: 'ascending',
				funnelAlign: 'right',
				data: [
					{ value: 60, name: 'Prod C' },
					{ value: 30, name: 'Prod D' },
					{ value: 10, name: 'Prod E' },
					{ value: 80, name: 'Prod B' },
					{ value: 100, name: 'Prod A' }
				]
			},
			{
				name: 'Funnel',
				type: 'funnel',
				width: '40%',
				height: '45%',
				left: '55%',
				top: '5%',
				funnelAlign: 'left',
				data: [
					{ value: 60, name: 'Prod C' },
					{ value: 30, name: 'Prod D' },
					{ value: 10, name: 'Prod E' },
					{ value: 80, name: 'Prod B' },
					{ value: 100, name: 'Prod A' }
				]
			},
			{
				name: 'Pyramid',
				type: 'funnel',
				width: '40%',
				height: '45%',
				left: '55%',
				top: '50%',
				sort: 'ascending',
				funnelAlign: 'left',
				data: [
					{ value: 60, name: 'Prod C' },
					{ value: 30, name: 'Prod D' },
					{ value: 10, name: 'Prod E' },
					{ value: 80, name: 'Prod B' },
					{ value: 100, name: 'Prod A' }
				]
			}
		]
	}

	onMounted(() => {
		let Echarts = echarts.init(document.getElementById('FunnelCompare'))

		// 绘制图表
		Echarts.setOption(option)
		// 自适应大小
		window.onresize = () => {
			Echarts.resize()
		}
	})
</script>
