<template>
	<a-row :gutter="[10, 10]">
		<a-col :span="12">
			<a-card title="基础折线图" :bordered="false">
				<BasicLineChart style="height: 400px" />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="折线图堆叠" :bordered="false">
				<StackedLineChart style="height: 400px" />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="堆叠面积图" :bordered="false">
				<StackedAreaChart style="height: 400px" />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="阶梯折线图" :bordered="false">
				<StepLine style="height: 400px" />
			</a-card>
		</a-col>
	</a-row>
</template>

<script setup name="eCXianXingTu">
	import BasicLineChart from '@/components/Chart/eCXianXingTu/BasicLineChart.vue'
	import StackedLineChart from '@/components/Chart/eCXianXingTu/StackedLineChart.vue'
	import StackedAreaChart from '@/components/Chart/eCXianXingTu/StackedAreaChart.vue'
	import StepLine from '@/components/Chart/eCXianXingTu/StepLine.vue'
</script>

<style scoped></style>
