<template>
	<div id="ZheXianTu04"></div>
</template>

<script setup>
	import { onMounted } from 'vue'
	import { Line } from '@antv/g2plot'

	const seriesKey = 'series'
	const valueKey = 'value'
	const processData = (data, yFields, meta) => {
		const result = []
		data.forEach((d) => {
			yFields.forEach((yField) => {
				const name = meta?.[yField]?.alias || yField
				result.push({ ...d, [seriesKey]: name, [valueKey]: d[yField] })
			})
		})
		return result
	}
	onMounted(() => {
		fetch('https://gw.alipayobjects.com/os/antfincdn/nHVKXA8ClI/multiple-measures-line-data.json')
			.then((data) => data.json())
			.then((data) => {
				const meta = {
					date: {
						alias: '销售日期'
					},
					price: {
						alias: '单价'
					},
					discount_price: {
						alias: '折扣单价'
					}
				}
				const line = new Line('ZheXianTu04', {
					data: processData(data, ['price', 'discount_price'], meta),
					padding: 'auto',
					xField: 'date',
					yField: valueKey,
					seriesField: seriesKey,
					appendPadding: [0, 8, 0, 0]
				})

				line.render()
			})
	})
</script>
