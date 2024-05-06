<template>
	<a-tabs v-model:activeKey="activeKey" tab-position="left">
		<a-tab-pane key="xiaonuoSms" tab="小诺短信">
			<xiaonuo-sms-form />
		</a-tab-pane>
		<a-tab-pane key="aliyunSms" tab="阿里短信">
			<aliyun-sms-form />
		</a-tab-pane>
		<a-tab-pane key="tencentSms" tab="腾讯短信">
			<tencent-sms-form />
		</a-tab-pane>
	</a-tabs>
</template>

<script setup name="smsConfig">
	import XiaonuoSmsForm from './xiaonuoSmsForm.vue'
	import AliyunSmsForm from './aliyunSmsForm.vue'
	import TencentSmsForm from './tencentSmsForm.vue'
	const activeKey = ref('xiaonuoSms')
</script>
