<template>
	<a-row :gutter="[10, 10]">
		<a-col :span="12">
			<a-card title="基础折线图" :bordered="false">
				<ZheXianTu01 />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="多折线图-二氧化碳排放量来源" :bordered="false">
				<ZheXianTu02 />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="阶梯折线图" :bordered="false">
				<ZheXianTu03 />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="多指标折线图" :bordered="false">
				<ZheXianTu04 />
			</a-card>
		</a-col>
	</a-row>
</template>

<script setup name="g2ZheXianTu"></script>
