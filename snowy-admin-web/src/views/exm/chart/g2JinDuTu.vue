<template>
	<a-row :gutter="[10, 10]">
		<a-col :span="12">
			<a-card title="仪表盘（多色）" :bordered="false">
				<JinDuTu01 />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="水波图" :bordered="false">
				<JinDuTu02 />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="渐变仪表盘" :bordered="false">
				<JinDuTu03 />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="样式自定义的水波图" :bordered="false">
				<JinDuTu04 />
			</a-card>
		</a-col>
	</a-row>
</template>

<script setup name="g2JinDuTu"></script>
