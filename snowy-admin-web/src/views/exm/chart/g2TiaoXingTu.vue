<template>
	<a-row :gutter="[10, 10]">
		<a-col :span="12">
			<a-card title="基础条形图" :bordered="false">
				<TiaoXingTu01 />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="堆叠条形图" :bordered="false">
				<TiaoXingTu02 />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="分组条形图" :bordered="false">
				<TiaoXingTu03 />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="区间条形图" :bordered="false">
				<TiaoXingTu04 />
			</a-card>
		</a-col>
	</a-row>
</template>

<script setup name="g2TiaoXingTu"></script>
