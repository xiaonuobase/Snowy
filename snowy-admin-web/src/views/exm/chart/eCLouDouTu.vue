<template>
	<a-row :gutter="[10, 10]">
		<a-col :span="12">
			<a-card title="漏斗图" :bordered="false">
				<FunnelChart class="xn-ht400" />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="漏斗图(对比)" :bordered="false">
				<FunnelCompare class="xn-ht400" />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="漏斗图" :bordered="false">
				<CustomizedFunnel class="xn-ht400" />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="Funnel" :bordered="false">
				<MultipleFunnels class="xn-ht400" />
			</a-card>
		</a-col>
	</a-row>
</template>

<script setup name="eCLouDouTu">
	import FunnelChart from '@/components/Chart/eCLouDouTu/FunnelChart.vue'
	import FunnelCompare from '@/components/Chart/eCLouDouTu/FunnelCompare.vue'
	import CustomizedFunnel from '@/components/Chart/eCLouDouTu/CustomizedFunnel.vue'
	import MultipleFunnels from '@/components/Chart/eCLouDouTu/MultipleFunnels.vue'
</script>
