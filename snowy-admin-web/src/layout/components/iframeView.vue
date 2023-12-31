<template>
	<div v-show="route.meta.type === 'iframe'" class="iframe-pages">
		<div></div>
		<iframe
			v-for="item in iframeList"
			v-show="route.meta.url === item.meta.url"
			:key="item.meta.url"
			:src="item.meta.url"
			frameborder="0"
		></iframe>
	</div>
</template>
<script setup>
	import { useRoute, useRouter } from 'vue-router'
	import { iframeStore, globalStore } from '@/store'
	const iStore = iframeStore()
	const store = globalStore()
	const route = useRoute()
	const router = useRouter()

	const iframeList = computed(() => {
		return iStore.iframeList
	})

	const isMobile = computed(() => {
		return store.isMobile
	})
	const layoutTags = computed(() => {
		return store.layoutTags
	})

	watch(route, () => {
		push(router.currentRoute.value)
	})
	onBeforeMount(() => {
		push(router.currentRoute.value)
	})

	const setIframeList = iStore.setIframeList
	const pushIframeList = iStore.pushIframeList
	const clearIframeList = iStore.clearIframeList
	const push = (route) => {
		if (route.meta.type === 'iframe') {
			if (isMobile || !layoutTags) {
				setIframeList(route)
			} else {
				pushIframeList(route)
			}
		} else if (isMobile || !layoutTags) {
			clearIframeList()
		}
	}
</script>

<style scoped>
	.iframe-pages {
		width: 100%;
		height: 100%;
		background: #fff;
	}
	iframe {
		border: 0;
		width: 100%;
		height: 100%;
		display: block;
	}
</style>
