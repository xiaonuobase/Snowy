<template>
	<a-row :gutter="[10, 10]">
		<a-col :span="12">
			<a-card title="AQI 气泡图" :bordered="false">
				<ScatterAqiColor class="xn-ht400" />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="基础散点图" :bordered="false">
				<BasicScatterChart class="xn-ht400" />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="数据聚合" :bordered="false">
				<ClusteringProcess class="xn-ht400" />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="涟漪特效散点图" :bordered="false">
				<EffectScatterChart class="xn-ht400" />
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
