<template>
	<div v-show="$route.meta.type === 'iframe'" class="iframe-pages">
		<iframe
			v-for="item in iframeList"
			v-show="$route.meta.url === item.meta.url"
			:key="item.meta.url"
			:src="item.meta.url"
			frameborder="0"
		></iframe>
	</div>
</template>

<script>
	import { mapState, mapActions } from 'pinia'
	import { iframeStore, globalStore } from '@/store'

	export default {
		data() {
			return {}
		},
		computed: {
			...mapState(iframeStore, ['iframeList']),
			...mapState(globalStore, ['ismobile', 'layoutTags'])
		},
		watch: {
			$route(e) {
				this.push(e)
			}
		},
		created() {
			this.push(this.$route)
		},
		mounted() {},
		methods: {
			...mapActions(iframeStore, ['setIframeList', 'pushIframeList', 'clearIframeList']),
			push(route) {
				if (route.meta.type === 'iframe') {
					if (this.ismobile || !this.layoutTags) {
						this.setIframeList(route)
					} else {
						this.pushIframeList(route)
					}
				} else if (this.ismobile || !this.layoutTags) {
					this.clearIframeList()
				}
			}
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
