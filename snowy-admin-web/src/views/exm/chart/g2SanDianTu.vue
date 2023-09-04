<template>
	<a-row :gutter="[10, 10]">
		<a-col :span="12">
			<a-card title="散点图颜色映射" :bordered="false">
				<SanDianTu01 />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="伪 3D 气泡图" :bordered="false">
				<SanDianTu02 />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="散点图图形标签" :bordered="false">
				<SanDianTu03 />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="气泡图-自定义tooltip" :bordered="false">
				<SanDianTu04 />
			</a-card>
		</a-col>
	</a-row>
</template>

<script setup name="g2SanDianTu"></script>
