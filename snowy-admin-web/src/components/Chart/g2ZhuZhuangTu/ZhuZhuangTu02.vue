<template>
	<div id="ZhuZhuangTu02"></div>
</template>

<script setup>
	import { onMounted } from 'vue'
	import { Column } from '@antv/g2plot'

	onMounted(() => {
		fetch('https://gw.alipayobjects.com/os/antfincdn/8elHX%26irfq/stack-column-data.json')
			.then((data) => data.json())
			.then((data) => {
				const stackedColumnPlot = new Column('ZhuZhuangTu02', {
					data,
					isStack: true,
					xField: 'year',
					yField: 'value',
					seriesField: 'type',
					label: {
						// 可手动配置 label 数据标签位置
						position: 'middle', // 'top', 'bottom', 'middle'
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
