<template>
	<a-row :gutter="[10, 10]">
		<a-col :span="12">
			<a-card title="子弹图" :bordered="false">
				<ZiDanTu01 />
			</a-card>
		</a-col>
		<a-col :span="12">
			<a-card title="多组子弹图" :bordered="false">
				<ZiDanTu02 />
			</a-card>
		</a-col>
	</a-row>
</template>

<script setup name="g2ZiDanTu"></script>
