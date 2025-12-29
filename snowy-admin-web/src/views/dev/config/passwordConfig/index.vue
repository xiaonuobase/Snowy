<template>
	<a-tabs v-model:activeKey="activeKey" tab-position="left">
		<a-tab-pane key="bForm" tab="后台密码">
			<b-form />
		</a-tab-pane>
		<a-tab-pane key="cForm" tab="前台密码">
			<c-form />
		</a-tab-pane>
		<a-tab-pane key="weakPassword" tab="弱密码库">
			<weak-password />
		</a-tab-pane>
	</a-tabs>
</template>

<script setup name="passwordConfig">
	import CForm from './cForm.vue'
	import BForm from './bForm.vue'
	import WeakPassword from './weakPassword/index.vue'
	const activeKey = ref('bForm')
</script>
