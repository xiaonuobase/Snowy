<template>
	<div id="SanDianTu01"></div>
</template>

<script setup>
	import { onMounted } from 'vue'
	import { Scatter } from '@antv/g2plot'

	onMounted(() => {
		fetch('https://gw.alipayobjects.com/os/antfincdn/aao6XnO5pW/IMDB.json')
			.then((res) => res.json())
			.then((data) => {
				const scatterPlot = new Scatter('SanDianTu01', {
					appendPadding: 10,
					data,
					xField: 'Revenue (Millions)',
					yField: 'Rating',
					shape: 'circle',
					colorField: 'Genre',
					size: 4,
					yAxis: {
						nice: true,
						line: {
							style: {
								stroke: '#aaa'
							}
						}
					},
					xAxis: {
						min: -100,
						grid: {
							line: {
								style: {
									stroke: '#eee'
								}
							}
						},
						line: {
							style: {
								stroke: '#aaa'
							}
						}
					}
				})
				scatterPlot.render()
			})
	})
</script>
