<template>
	<a-tabs v-model:activeKey="activeKey" tab-position="left">
		<a-tab-pane key="bForm" tab="后台登录">
			<b-form />
		</a-tab-pane>
		<a-tab-pane key="cForm" tab="前台登录">
			<c-form />
		</a-tab-pane>
	</a-tabs>
</template>

<script setup name="loginConfig">
	import CForm from './cForm.vue'
	import BForm from './bForm.vue'
	const activeKey = ref('bForm')
</script>
