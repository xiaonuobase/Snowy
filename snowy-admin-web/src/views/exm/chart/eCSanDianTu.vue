<template>
	<a-row :gutter="[10, 10]">
		<a-col :span="12">
			<a-card title="AQI 气泡图" :bordered="false">
				<ScatterAqiColor style="height: 400px" />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="基础散点图" :bordered="false">
				<BasicScatterChart style="height: 400px" />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="数据聚合" :bordered="false">
				<ClusteringProcess style="height: 400px" />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="涟漪特效散点图" :bordered="false">
				<EffectScatterChart style="height: 400px" />
			</a-card>
		</a-col>
	</a-row>
</template>

<script setup name="eCSanDianTu">
	import BasicScatterChart from '@/components/Chart/eCSanDianTu/BasicScatterChart.vue'
	import ClusteringProcess from '@/components/Chart/eCSanDianTu/ClusteringProcess.vue'
	import EffectScatterChart from '@/components/Chart/eCSanDianTu/EffectScatterChart.vue'
	import ScatterAqiColor from '@/components/Chart/eCSanDianTu/ScatterAqiColor.vue'
</script>

<style scoped></style>
