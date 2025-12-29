<template>
	<a-tabs v-model:activeKey="activeKey" tab-position="left">
		<a-tab-pane key="dingTalk" tab="钉钉">
			<ding-talk-form />
		</a-tab-pane>
		<a-tab-pane key="feishu" tab="飞书">
			<feishu-form />
		</a-tab-pane>
		<a-tab-pane key="workWechat" tab="企业微信">
			<work-wechat-form />
		</a-tab-pane>
	</a-tabs>
</template>

<script setup name="pushConfig">
	import WorkWechatForm from './workWechatForm.vue'
	import DingTalkForm from './dingTalkForm.vue'
	import FeishuForm from './feishuForm.vue'
	const activeKey = ref('dingTalk')
</script>
