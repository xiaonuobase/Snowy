<template>
	<div class="admin-ui-breadcrumb">
		<div class="left-panel">
			<a-breadcrumb>
				<template v-for="item in breadList" :key="item.title">
					<a-breadcrumb-item v-if="item.path !== '/' && !item.meta.hiddenBreadcrumb" :key="item.meta.title">{{
						item.meta.title
					}}</a-breadcrumb-item>
				</template>
			</a-breadcrumb>
		</div>
		<div class="center-panel"></div>
		<div class="right-panel">
			<slot></slot>
		</div>
	</div>
</template>
<script setup>
	import { watch } from 'vue'
	import { useRoute } from 'vue-router'

	const route = useRoute()
	const breadList = ref([])

	watch(route, () => {
		getBreadcrumb()
	})

	onBeforeMount(() => {
		getBreadcrumb()
	})

	const getBreadcrumb = () => {
		breadList.value = route.meta.breadcrumb
	}
</script>
<style scoped>
	.admin-ui-breadcrumb {
		padding-left: 15px;
		background: var(--breadcrumb-background);
		min-height: 40px;
		display: flex;
		border-bottom: 1px solid var(--header-bottom);
	}
	.admin-ui-breadcrumb .left-panel {
		display: flex;
		align-items: center;
	}
	.admin-ui-breadcrumb .right-panel {
		display: flex;
		align-items: center;
	}
</style>
