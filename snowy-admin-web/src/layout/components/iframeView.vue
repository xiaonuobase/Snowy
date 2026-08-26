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
	const layoutTagsOpen = computed(() => {
		return store.layoutTagsOpen
	})
	// 窄屏或未开启多标签时多标签栏不显示，此时内嵌页只保留当前一个，其余情况随多标签共存
	const iframeSingleMode = computed(() => {
		return isMobile.value || !layoutTagsOpen.value
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
			if (iframeSingleMode.value) {
				setIframeList(route)
			} else {
				pushIframeList(route)
			}
		} else if (iframeSingleMode.value) {
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
