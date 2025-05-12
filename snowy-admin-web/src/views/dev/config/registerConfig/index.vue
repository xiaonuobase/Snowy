<template>
	<a-tabs v-model:activeKey="activeKey" tab-position="left">
		<a-tab-pane key="bForm" tab="后台注册">
			<b-form />
		</a-tab-pane>
		<a-tab-pane key="cForm" tab="前台注册">
			<c-form />
		</a-tab-pane>
	</a-tabs>
</template>

<script setup name="registerConfig">
	import BForm from './bForm.vue'
	import CForm from './cForm.vue'

	const activeKey = ref('bForm')
</script>
