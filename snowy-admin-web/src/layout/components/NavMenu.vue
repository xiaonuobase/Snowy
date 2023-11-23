<template>
	<div v-if="navMenus.length <= 0" style="padding: 20px">
		<a-alert message="无任何菜单" type="info" :closable="false" />
	</div>
	<template v-for="navMenu in navMenus" :key="navMenu">
		<a-menu-item v-if="!hasChildren(navMenu) & !hasHidden(navMenu)" :key="navMenu.path">
			<template v-if="navMenu.meta.icon" #icon>
				<component :is="navMenu.meta.icon" />
			</template>
			<a
				v-if="navMenu.meta && navMenu.meta.type === 'link'"
				:href="navMenu.path"
				target="_blank"
				@click.stop="() => {}"
			>{{ navMenu.meta.title }}</a
			>
			<a v-else>{{ navMenu.meta.title }}</a>
		</a-menu-item>
		<a-sub-menu v-else-if="!hasHidden(navMenu)" :key="navMenu.path" :title="navMenu.meta.title">
			<template v-if="navMenu.meta.icon" #icon>
				<component :is="navMenu.meta.icon" />
			</template>
			<NavMenu :nav-menus="navMenu.children" />
		</a-sub-menu>
	</template>
</template>

<script setup>
	const props = defineProps({
		navMenus: {
			type: Array,
			default: () => []
		}
	})
	const hasChildren = (item) => {
		return item.children && !item.children.every((item) => item.meta.hidden)
	}
	// 是否隐藏
	const hasHidden = (item) => {
		if (item.meta.hidden === true) {
			return true
		}
		// 为空跟false，都会显示
		return false
	}
</script>
