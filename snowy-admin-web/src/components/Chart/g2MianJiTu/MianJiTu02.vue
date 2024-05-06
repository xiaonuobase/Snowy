<template>
	<div id="MianJiTu02"></div>
</template>

<script setup>
	import { onMounted } from 'vue'
	import { Area } from '@antv/g2plot'

	onMounted(() => {
		fetch('https://gw.alipayobjects.com/os/bmw-prod/1d565782-dde4-4bb6-8946-ea6a38ccf184.json')
			.then((res) => res.json())
			.then((data) => {
				const area = new Area('MianJiTu02', {
					data,
					xField: 'Date',
					yField: 'scales',
					xAxis: {
						range: [0, 1],
						tickCount: 5
					},
					areaStyle: () => {
						return {
							fill: 'l(270) 0:#ffffff 0.5:#7ec2f3 1:#1677FF'
						}
					}
				})
				area.render()
			})
	})
</script>
