<template>
	<div id="YiBiaoTu01"></div>
</template>

<script setup>
	import { onMounted } from 'vue'
	import { Gauge } from '@antv/g2plot'

	onMounted(() => {
		const gauge = new Gauge('YiBiaoTu01', {
			percent: 0.75,
			range: {
				ticks: [0, 1 / 3, 2 / 3, 1],
				color: ['#F4664A', '#FAAD14', '#30BF78']
			},
			indicator: {
				pointer: {
					style: {
						stroke: '#D0D0D0'
					}
				},
				pin: {
					style: {
						stroke: '#D0D0D0'
					}
				}
			},
			statistic: {
				content: {
					style: {
						fontSize: '36px',
						lineHeight: '36px'
					}
				}
			}
		})

		gauge.render()
	})
</script>
