<template>
	<div class="adminui-topbar">
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

<script>
	export default {
		data() {
			return {
				breadList: []
			}
		},
		watch: {
			$route() {
				this.getBreadcrumb()
			}
		},
		created() {
			this.getBreadcrumb()
		},
		methods: {
			getBreadcrumb() {
				const matched = this.$route.meta.breadcrumb
				this.breadList = matched
			}
		}
	}
</script>
