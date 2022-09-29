<template>
	<div style="height: 500px">
		<vue3-tree-org
			style="background: var(--card-actions-background)"
			:data="data"
			:tool-bar="{ scale: true, restore: false, expand: false, zoom: true, fullscreen: true }"
			:disabled="false"
			:scalable="true"
			:draggable-on-node="false"
			:node-draggable="false"
			:clone-node-drag="false"
			:define-menus="[]"
			:default-expand-level="3"
		/>
	</div>
</template>
<!--该组件使用：https://gitee.com/sangtian152/vue3-tree-org-->
<script setup name="organizationChart">
	import { Vue3TreeOrg } from 'vue3-tree-org'
	import 'vue3-tree-org/lib/vue3-tree-org.css'

	import userCenterApi from '@/api/sys/userCenterApi'
	let data = ref({})
	userCenterApi.userLoginOrgTree().then((res) => {
		data.value = res[0]
	})
</script>

<style>
	.tree-org-node__content {
		background: var(--body-background) !important;
	}
	.tree-org {
		padding-top: 10px !important;
	}
</style>
