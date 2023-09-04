<template>
	<a-row :gutter="[10, 10]">
		<a-col :span="12">
			<a-card title="基础漏斗图" :bordered="false">
				<LouDouTu01 />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="对比漏斗图" :bordered="false">
				<LouDouTu02 />
			</a-card>
		</a-col>
	</a-row>
</template>

<script setup name="g2LouDouTu"></script>
