<template>
	<div id="ZheXianTu02"></div>
</template>

<script setup>
	import { onMounted } from 'vue'
	import { Line } from '@antv/g2plot'

	onMounted(() => {
		fetch('https://gw.alipayobjects.com/os/bmw-prod/55424a73-7cb8-4f79-b60d-3ab627ac5698.json')
			.then((res) => res.json())
			.then((data) => {
				const line = new Line('ZheXianTu02', {
					data,
					xField: 'year',
					yField: 'value',
					seriesField: 'category',
					xAxis: {
						type: 'time'
					},
					yAxis: {
						label: {
							// 数值格式化为千分位
							formatter: (v) => `${v}`.replace(/\d{1,3}(?=(\d{3})+$)/g, (s) => `${s},`)
						}
					}
				})

				line.render()
			})
	})
</script>
