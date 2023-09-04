<template>
	<a-row :gutter="[10, 10]">
		<a-col :span="12">
			<a-card title="基础词云图" :bordered="false">
				<CiYunTu01 />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="词云图-每次渲染保持位置不变" :bordered="false">
				<CiYunTu02 />
			</a-card>
		</a-col>
	</a-row>
</template>

<script setup name="g2CiYunTu"></script>
