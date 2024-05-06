<template>
	<div id="columnChart" class="xn-ht200"></div>
</template>

<script setup>
	import logApi from '@/api/dev/logApi'
	import { Column } from '@antv/g2plot'
	import { onMounted } from 'vue'

	onMounted(() => {
		logApi.logOpBarChartData().then((data) => {
			const stackedColumnPlot = new Column('columnChart', {
				data,
				isGroup: true,
				xField: 'date',
				yField: 'count',
				seriesField: 'name',
				/** 设置颜色 */
				color: ['#1677FF', '#F5222D'],
				/** 设置间距 */
				// marginRatio: 0.1,
				label: {
					// 可手动配置 label 数据标签位置
					position: 'middle', // 'top', 'middle', 'bottom'
					// 可配置附加的布局方法
					layout: [
						// 柱形图数据标签位置自动调整
						{ type: 'interval-adjust-position' },
						// 数据标签防遮挡
						{ type: 'interval-hide-overlap' },
						// 数据标签文颜色自动调整
						{ type: 'adjust-color' }
					]
				}
			})
			stackedColumnPlot.render()
		})
	})
</script>
<style scoped>
	.xn-ht200 {
		height: 200px;
	}
</style>
