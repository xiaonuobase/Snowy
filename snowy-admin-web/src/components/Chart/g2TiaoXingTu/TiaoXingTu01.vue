<template>
	<div id="TiaoXingTu01"></div>
</template>

<script setup>
	import { onMounted } from 'vue'
	import { Bar } from '@antv/g2plot'

	const data = [
		{ year: '1951 年', value: 38 },
		{ year: '1952 年', value: 52 },
		{ year: '1956 年', value: 61 },
		{ year: '1957 年', value: 145 },
		{ year: '1958 年', value: 48 }
	]

	onMounted(() => {
		const bar = new Bar('TiaoXingTu01', {
			data,
			xField: 'value',
			yField: 'year',
			seriesField: 'year',
			legend: {
				position: 'top-left'
			}
		})

		bar.render()
	})
</script>
