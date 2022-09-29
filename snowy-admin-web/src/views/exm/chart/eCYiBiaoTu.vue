<template>
	<a-row :gutter="[10, 10]">
		<a-col :span="12">
			<a-card title="基础仪表盘" :bordered="false">
				<GaugeBasicChart style="height: 400px" />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="阶段速度仪表盘" :bordered="false">
				<StageSpeedGauge style="height: 400px" />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="气温仪表盘" :bordered="false">
				<TemperatureGaugeChart style="height: 400px" />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="进度仪表盘" :bordered="false">
				<GrogressGauge style="height: 400px" />
			</a-card>
		</a-col>
	</a-row>
</template>

<script setup name="eCYiBiaoTu">
	import GaugeBasicChart from '@/components/Chart/eCYiBiaoTu/GaugeBasicChart.vue'
	import StageSpeedGauge from '@/components/Chart/eCYiBiaoTu/StageSpeedGauge.vue'
	import TemperatureGaugeChart from '@/components/Chart/eCYiBiaoTu/TemperatureGaugeChart.vue'
	import GrogressGauge from '@/components/Chart/eCYiBiaoTu/GrogressGauge.vue'
</script>

<style scoped></style>
