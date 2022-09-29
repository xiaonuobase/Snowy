<template>
	<a-row :gutter="[10, 10]">
		<a-col :span="12">
			<a-card title="某站点用户Access From" :bordered="false">
				<RefererOfAWebsite style="height: 400px" />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="圆角环形图" :bordered="false">
				<DoughnutChartWithRoundedCorner style="height: 400px" />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="饼图自定义样式" :bordered="false">
				<CustomizedPie style="height: 400px" />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="基础南丁格尔玫瑰图" :bordered="false">
				<NightingaleChart style="height: 400px" />
			</a-card>
		</a-col>
	</a-row>
</template>

<script setup name="eCBingZhuangTu">
	import BasicCandlestick from '@/components/Chart/eCBingZhuangTu/RefererOfAWebsite.vue'
	import DoughnutChartWithRoundedCorner from '@/components/Chart/eCBingZhuangTu/DoughnutChartWithRoundedCorner.vue'
	import CustomizedPie from '@/components/Chart/eCBingZhuangTu/CustomizedPie.vue'
	import NightingaleChart from '@/components/Chart/eCBingZhuangTu/NightingaleChart.vue'
</script>

<style scoped></style>
