<template>
	<a-row :gutter="[10, 10]">
		<a-col :span="12">
			<a-card title="基础K线图" :bordered="false">
				<basic-candlestick class="xn-ht400" />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="上证指数" :bordered="false">
				<shang-hai-index class="xn-ht400" />
			</a-card>
		</a-col>
	</a-row>
</template>
<!--ec官网：https://echarts.apache.org/zh/index.html-->
<!--可以看博客：https://blog.csdn.net/qq_41636947/article/details/110879791-->
<script setup name="eCKXianTu">
	import BasicCandlestick from '@/components/Chart/eCKXianTu/BasicCandlestick.vue'
	import ShangHaiIndex from '@/components/Chart/eCKXianTu/ShangHaiIndex.vue'
</script>
