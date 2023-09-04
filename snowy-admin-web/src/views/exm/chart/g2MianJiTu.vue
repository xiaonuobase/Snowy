<template>
	<a-row :gutter="[10, 10]">
		<a-col :span="12">
			<a-card title="基础面积图" :bordered="false">
				<MianJiTu01 />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="渐变色面积图" :bordered="false">
				<MianJiTu02 />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="堆叠面积图" :bordered="false">
				<MianJiTu03 />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="百分比堆叠面积图" :bordered="false">
				<MianJiTu04 />
			</a-card>
		</a-col>
	</a-row>
</template>

<script setup name="g2MianJiTu"></script>
