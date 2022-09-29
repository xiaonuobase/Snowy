<template>
	<div id="pieChart" style="height: 180px"></div>
</template>

<script setup>
	import logApi from '@/api/dev/logApi'
	import { onMounted } from 'vue'
	import { Pie } from '@antv/g2plot'

	onMounted(() => {
		logApi.logOpPieChartData().then((data) => {
			const piePlot = new Pie('pieChart', {
				appendPadding: 10,
				data,
				angleField: 'value',
				colorField: 'type',
				radius: 0.9,
				color: ['#1890ff', '#F5222D'],
				label: {
					type: 'inner',
					offset: '-30%',
					content: ({ percent }) => `${(percent * 100).toFixed(0)}%`,
					style: {
						fontSize: 14,
						textAlign: 'center'
					}
				},
				interactions: [{ type: 'element-active' }]
			})
			piePlot.render()
		})
	})
</script>
