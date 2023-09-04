<template>
	<div id="SanDianTu02"></div>
</template>

<script setup>
	import { onMounted } from 'vue'
	import { Scatter } from '@antv/g2plot'

	onMounted(() => {
		fetch('https://gw.alipayobjects.com/os/antfincdn/t81X1wXdoj/scatter-data.json')
			.then((res) => res.json())
			.then((data) => {
				const scatterPlot = new Scatter('SanDianTu02', {
					appendPadding: 30,
					data,
					xField: 'x',
					yField: 'y',
					colorField: 'genre',
					color: [
						'r(0.4, 0.3, 0.7) 0:rgba(255,255,255,0.5) 1:#5B8FF9',
						'r(0.4, 0.4, 0.7) 0:rgba(255,255,255,0.5) 1:#61DDAA'
					],
					sizeField: 'size',
					size: [5, 20],
					shape: 'circle',
					yAxis: {
						nice: true,
						line: {
							style: {
								stroke: '#eee'
							}
						}
					},
					xAxis: {
						grid: {
							line: {
								style: {
									stroke: '#eee'
								}
							}
						},
						line: {
							style: {
								stroke: '#eee'
							}
						}
					}
				})
				scatterPlot.render()
			})
	})
</script>
