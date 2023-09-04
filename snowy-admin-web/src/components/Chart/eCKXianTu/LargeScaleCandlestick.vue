<template>
	<div id="LargeScaleCandlestick"></div>
</template>
<!--ec官网：https://echarts.apache.org/zh/index.html-->
<script setup name="LargeScaleCandlestick">
	import { onMounted } from 'vue'
	import * as echarts from 'echarts'

	const upColor = '#ec0000'
	const upBorderColor = '#8A0000'
	const downColor = '#00da3c'
	const downBorderColor = '#008F28'
	const dataCount = 2e5

	const getSign = (data, dataIndex, openVal, closeVal, closeDimIdx) => {
		let sign
		if (openVal > closeVal) {
			sign = -1
		} else if (openVal < closeVal) {
			sign = 1
		} else {
			// eslint-disable-next-line no-extra-parens
			sign = dataIndex > 0 ? (data[dataIndex - 1][closeDimIdx] <= closeVal ? 1 : -1) : 1
		}
		return sign
	}

	const generateOHLC = (count) => {
		let data = []
		let xValue = Number(new Date(2011, 0, 1))
		let minute = 60 * 1000
		let baseValue = Math.random() * 12000
		let boxVals = new Array(4)
		let dayRange = 12
		for (let i = 0; i < count; i++) {
			baseValue = baseValue + Math.random() * 20 - 10
			for (let j = 0; j < 4; j++) {
				boxVals[j] = (Math.random() - 0.5) * dayRange + baseValue
			}
			boxVals.sort()
			let openIdx = Math.round(Math.random() * 3)
			let closeIdx = Math.round(Math.random() * 2)
			if (closeIdx === openIdx) {
				closeIdx++
			}
			let volumn = boxVals[3] * (1000 + Math.random() * 500)
			// ['open', 'close', 'lowest', 'highest', 'volumn']
			// [1, 4, 3, 2]
			data[i] = [
				// eslint-disable-next-line no-extra-parens
				echarts.format.formatTime('yyyy-MM-dd\nhh:mm:ss', (xValue += minute)),
				Number(boxVals[openIdx].toFixed(2)),
				Number(boxVals[3].toFixed(2)),
				Number(boxVals[0].toFixed(2)),
				Number(boxVals[closeIdx].toFixed(2)),
				Number(volumn.toFixed(0)),
				getSign(data, i, Number(boxVals[openIdx]), Number(boxVals[closeIdx]), 4) // sign
			]
		}
		return data
	}

	const data = generateOHLC(dataCount)

	onMounted(() => {
		let Echarts = echarts.init(document.getElementById('LargeScaleCandlestick'))
		const option = {
			dataset: {
				source: data
			},
			title: {
				text: 'Data Amount: ' + echarts.format.addCommas(dataCount)
			},
			tooltip: {
				trigger: 'axis',
				axisPointer: {
					type: 'line'
				}
			},
			toolbox: {
				feature: {
					dataZoom: {
						yAxisIndex: false
					}
				}
			},
			grid: [
				{
					left: '10%',
					right: '10%',
					bottom: 200
				},
				{
					left: '10%',
					right: '10%',
					height: 80,
					bottom: 80
				}
			],
			xAxis: [
				{
					type: 'category',
					boundaryGap: false,
					// inverse: true,
					axisLine: { onZero: false },
					splitLine: { show: false },
					min: 'dataMin',
					max: 'dataMax'
				},
				{
					type: 'category',
					gridIndex: 1,
					boundaryGap: false,
					axisLine: { onZero: false },
					axisTick: { show: false },
					splitLine: { show: false },
					axisLabel: { show: false },
					min: 'dataMin',
					max: 'dataMax'
				}
			],
			yAxis: [
				{
					scale: true,
					splitArea: {
						show: true
					}
				},
				{
					scale: true,
					gridIndex: 1,
					splitNumber: 2,
					axisLabel: { show: false },
					axisLine: { show: false },
					axisTick: { show: false },
					splitLine: { show: false }
				}
			],
			dataZoom: [
				{
					type: 'inside',
					xAxisIndex: [0, 1],
					start: 10,
					end: 100
				},
				{
					show: true,
					xAxisIndex: [0, 1],
					type: 'slider',
					bottom: 10,
					start: 10,
					end: 100
				}
			],
			visualMap: {
				show: false,
				seriesIndex: 1,
				dimension: 6,
				pieces: [
					{
						value: 1,
						color: upColor
					},
					{
						value: -1,
						color: downColor
					}
				]
			},
			series: [
				{
					type: 'candlestick',
					itemStyle: {
						color: upColor,
						color0: downColor,
						borderColor: upBorderColor,
						borderColor0: downBorderColor
					},
					encode: {
						x: 0,
						y: [1, 4, 3, 2]
					}
				},
				{
					name: 'Volumn',
					type: 'bar',
					xAxisIndex: 1,
					yAxisIndex: 1,
					itemStyle: {
						color: '#7fbe9e'
					},
					large: true,
					encode: {
						x: 0,
						y: 5
					}
				}
			]
		}
		// 绘制图表
		Echarts.setOption(option)
		// 自适应大小
		window.onresize = () => {
			Echarts.resize()
		}
	})
</script>
