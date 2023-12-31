<template>
	<a-tabs v-model:activeKey="activeKey" tab-position="left">
		<a-tab-pane key="wechatThird" tab="微信">
			<wechatThirdForm />
		</a-tab-pane>
		<a-tab-pane key="giteeThird" tab="GITEE">
			<giteeThirdForm />
		</a-tab-pane>
	</a-tabs>
</template>

<script setup name="thirdConfig">
	import WechatThirdForm from './wechatThirdForm.vue'
	import GiteeThirdForm from './giteeThirdForm.vue'
	const activeKey = ref('wechatThird')
</script>
