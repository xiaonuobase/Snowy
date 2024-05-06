<template>
	<div class="xn-ht500">
		<vue3-tree-org
			class="xn-tree-line"
			:data="treeData"
			:tool-bar="{ scale: true, restore: false, expand: false, zoom: true, fullscreen: true }"
			:disabled="false"
			:center="true"
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
	const treeData = ref({})
	userCenterApi.userLoginOrgTree().then((data) => {
		treeData.value = data[0]
	})
</script>

<style lang="less" scoped>
	:deep(tree-org-node__content) {
		background: var(--body-background);
	}
	:deep(.tree-org) {
		padding-top: 10px;
		padding-left: 10px;
	}
	.xn-tree-line {
		background: var(--card-actions-background);
	}
	.xn-ht500 {
		height: 500px;
	}
</style>
