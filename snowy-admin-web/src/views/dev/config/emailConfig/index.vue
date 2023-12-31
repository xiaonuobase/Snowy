<template>
	<a-tabs v-model:activeKey="activeKey" tab-position="left">
		<a-tab-pane key="localEmail" tab="本地邮件">
			<localEmailForm />
		</a-tab-pane>
		<a-tab-pane key="aliyunEmail" tab="阿里邮件">
			<aliyunEmailForm />
		</a-tab-pane>
		<a-tab-pane key="tencentEmail" tab="腾讯邮件">
			<tencentEmailForm />
		</a-tab-pane>
	</a-tabs>
</template>

<script setup name="emailConfig">
	import LocalEmailForm from './localEmailForm.vue'
	import AliyunEmailForm from './aliyunEmailForm.vue'
	import TencentEmailForm from './tencentEmailForm.vue'
	const activeKey = ref('localEmail')
</script>
