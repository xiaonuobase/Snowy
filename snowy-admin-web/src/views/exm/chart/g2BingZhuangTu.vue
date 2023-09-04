<template>
	<a-row :gutter="[10, 10]">
		<a-col :span="12">
			<a-card title="基础饼图" :bordered="false">
				<BingZhuangTu01 />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="饼图-蜘蛛布局标签" :bordered="false">
				<BingZhuangTu02 />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="基础环图" :bordered="false">
				<BingZhuangTu03 />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="环图统计指标卡" :bordered="false">
				<BingZhuangTu04 />
			</a-card>
		</a-col>
	</a-row>
</template>

<script setup name="g2BingZhuangTu"></script>
