<template>
	<a-tabs v-model:activeKey="activeKey" tab-position="left">
		<a-tab-pane key="aliyunSms" tab="阿里短信">
			<aliyunSmsForm />
		</a-tab-pane>
		<a-tab-pane key="tencentSms" tab="腾讯短信">
			<tencentSmsForm />
		</a-tab-pane>
	</a-tabs>
</template>

<script setup name="smsConfig">
	import aliyunSmsForm from './aliyunSmsForm.vue'
	import tencentSmsForm from './tencentSmsForm.vue'
	const activeKey = ref('aliyunSms')
</script>
