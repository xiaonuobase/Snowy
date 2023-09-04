<template>
	<div id="ZheXianTu01"></div>
</template>

<script setup>
	import { onMounted } from 'vue'
	import { Line } from '@antv/g2plot'

	onMounted(() => {
		fetch('https://gw.alipayobjects.com/os/bmw-prod/1d565782-dde4-4bb6-8946-ea6a38ccf184.json')
			.then((res) => res.json())
			.then((data) => {
				const line = new Line('ZheXianTu01', {
					data,
					padding: 'auto',
					xField: 'Date',
					yField: 'scales',
					xAxis: {
						// type: 'timeCat',
						tickCount: 5
					}
				})

				line.render()
			})
	})
</script>
