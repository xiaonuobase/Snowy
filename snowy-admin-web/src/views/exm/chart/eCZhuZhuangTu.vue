<template>
	<a-row :gutter="[10, 10]">
		<a-col :span="12">
			<a-card title="基础柱状图" :bordered="false">
				<BasicBar class="xn-ht400" />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="柱状图标签旋转" :bordered="false">
				<BarLabelRotation class="xn-ht400" />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="交错正负轴标签" :bordered="false">
				<BarChartWithNegativeValue class="xn-ht400" />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="水印 - ECharts 下载统计" :bordered="false">
				<WatermarkEChartsDownload class="xn-ht400" />
			</a-card>
		</a-col>
	</a-row>
</template>

<script setup name="eCZhuZhuangTu">
	import BasicBar from '@/components/Chart/eCZhuZhuangTu/BasicBar.vue'
	import BarLabelRotation from '@/components/Chart/eCZhuZhuangTu/BarLabelRotation.vue'
	import BarChartWithNegativeValue from '@/components/Chart/eCZhuZhuangTu/BarChartWithNegativeValue.vue'
	import WatermarkEChartsDownload from '@/components/Chart/eCZhuZhuangTu/WatermarkEChartsDownload.vue'
</script>
