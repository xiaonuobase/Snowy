<template>
	<a-row :gutter="[10, 10]">
		<a-col :span="12">
			<a-card title="基础柱状图" :bordered="false">
				<ZhuZhuangTu01 />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="堆叠柱状图" :bordered="false">
				<ZhuZhuangTu02 />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="分组柱状图" :bordered="false">
				<ZhuZhuangTu03 />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="分组柱状图" :bordered="false">
				<ZhuZhuangTu04 />
			</a-card>
		</a-col>
	</a-row>
</template>

<script setup name="g2ZhuZhuangTu"></script>
