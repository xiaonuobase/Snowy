<template>
	<div id="ZiDanTu03"></div>
</template>

<script setup>
	import { onMounted } from 'vue'
	import { Area } from '@antv/g2plot'

	onMounted(() => {
		fetch('https://gw.alipayobjects.com/os/bmw-prod/b21e7336-0b3e-486c-9070-612ede49284e.json')
			.then((res) => res.json())
			.then((data) => {
				const area = new Area('ZiDanTu03', {
					data,
					xField: 'date',
					yField: 'value',
					seriesField: 'country'
				})
				area.render()
			})
	})
</script>
