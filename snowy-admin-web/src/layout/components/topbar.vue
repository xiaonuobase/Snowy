<template>
	<div class="admin-ui-topbar">
		<div class="left-panel">
			<a-breadcrumb>
				<template v-for="item in breadList" :key="item.title">
					<a-breadcrumb-item v-if="item.path != '/' && !item.meta.hiddenBreadcrumb" :key="item.meta.title">{{
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
