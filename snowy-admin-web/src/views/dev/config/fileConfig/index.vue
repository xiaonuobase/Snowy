<template>
	<a-tabs v-model:activeKey="activeKey" tab-position="left">
		<a-tab-pane key="localFile" tab="本地文件">
			<localFileForm />
		</a-tab-pane>
		<a-tab-pane key="aliyunFile" tab="阿里文件">
			<aliyunFileForm />
		</a-tab-pane>
		<a-tab-pane key="tencentFile" tab="腾讯文件">
			<tencentFileForm />
		</a-tab-pane>
		<a-tab-pane key="minioFile" tab="MINIO文件">
			<minioFileForm />
		</a-tab-pane>
	</a-tabs>
</template>

<script setup name="fileConfig">
	import localFileForm from './localFileForm.vue'
	import aliyunFileForm from './aliyunFileForm.vue'
	import tencentFileForm from './tencentFileForm.vue'
	import minioFileForm from './minioFileForm.vue'
	const activeKey = ref('localFile')
</script>
