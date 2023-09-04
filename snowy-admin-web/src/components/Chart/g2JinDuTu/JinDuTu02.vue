<template>
	<div id="YiBiaoTu02"></div>
</template>

<script setup>
	import { onMounted } from 'vue'
	import { Liquid } from '@antv/g2plot'

	onMounted(() => {
		const liquidPlot = new Liquid('YiBiaoTu02', {
			percent: 0.25,
			outline: {
				border: 4,
				distance: 8
			},
			wave: {
				length: 128
			}
		})
		liquidPlot.render()
	})
</script>
