<template>
	<div class="xn-ht500">
		<vue3-tree-org
			v-if="treeReady"
			class="xn-tree-line"
			:data="treeData"
			:tool-bar="{ scale: true, restore: false, expand: false, zoom: true, fullscreen: true }"
			:disabled="false"
			:center="true"
			:scalable="true"
			:draggable-on-node="false"
			:node-draggable="false"
			:clone-node-drag="false"
			:collapsable="true"
			:define-menus="[]"
			:default-expand-level="1"
			:lazy="true"
			:load="loadChildren"
		/>
		<div v-else class="xn-loading-wrap">
			<a-spin tip="加载中..." />
		</div>
	</div>
</template>
<!--该组件使用：https://gitee.com/sangtian152/vue3-tree-org-->
<script setup name="organizationChart">
	import { Vue3TreeOrg } from 'vue3-tree-org'
	import 'vue3-tree-org/lib/vue3-tree-org.css'
	import userCenterApi from '@/api/sys/userCenterApi'
	const treeData = ref({})
	const treeReady = ref(false)
	userCenterApi.userLoginOrgTree().then((data) => {
		treeData.value = data[0]
		treeReady.value = true
	})
	// 懒加载子节点
	const loadChildren = (node, resolve) => {
		const parentId = node.id
		if (!parentId) {
			resolve([])
			return
		}
		userCenterApi.userLoginOrgTreeChildren({ parentId }).then((data) => {
			resolve(data, true)
		})
	}
</script>

<style lang="less" scoped>
	:deep(tree-org-node__content) {
		background: var(--snowy-background-color);
	}
	:deep(.tree-org) {
		padding-top: 10px;
		padding-left: 10px;
	}
	.xn-tree-line {
		background: var(--snowy-background-color);
	}
	.xn-ht500 {
		height: 500px;
	}
	.xn-loading-wrap {
		height: 100%;
		display: flex;
		align-items: center;
		justify-content: center;
	}
	:deep(.zm-tree-handle .zm-tree-handle-item) {
		background: var(--snowy-background-color);
	}
</style>
