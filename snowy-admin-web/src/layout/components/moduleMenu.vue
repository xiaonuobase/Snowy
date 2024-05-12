<template>
	<div class="layout-items-center" v-if="moduleUnfoldOpen && layout !== layoutEnum.TOP">
		<a-menu
			v-model:selectedKeys="selectedKeys"
			mode="horizontal"
			v-if="menu && menu.length > 1 && !isMobile"
			class="module-menu"
			id="moduleMenu"
		>
			<a-menu-item
				v-for="item in menu"
				:key="item.id"
				class="xn-pxn-r"
				@click="moduleClick(item.id)"
				:class="{ 'ant-menu-item-select': item.id === module }"
			>
				<template #icon>
					<component :is="item.meta.icon" />
				</template>
				<span class="xn-ml-5">{{ item.meta.title }}</span>
			</a-menu-item>
		</a-menu>
	</div>
	<div v-else>
		<a-popover v-if="menu.length > 1 && !isMobile" placement="bottomLeft">
			<template #content>
				<a-row :gutter="[0, 5]" class="module-row">
					<div v-for="item in menu" :key="item.id">
						<a-col :span="6">
							<a-tag
								class="module-card"
								:class="roundedCornerStyleOpen ? 'module-card-radius-round' : 'module-card-radius-default'"
								:color="item.color"
								@click="moduleClick(item.id)"
							>
								<component :is="item.meta.icon" class="module-card-icon" />
								<div class="module-card-font">{{ item.meta.title }}</div>
							</a-tag>
						</a-col>
					</div>
				</a-row>
			</template>
			<div class="panel-item hidden-sm-and-down module-card-scope">
				<appstore-outlined />
			</div>
		</a-popover>
	</div>
</template>

<script setup>
	import router from '@/router'
	import tool from '@/utils/tool'
	import { globalStore } from '@/store'
	import { watch } from 'vue'
	import { storeToRefs } from 'pinia'
	import { layoutEnum } from '@/layout/enum/layoutEnum'
	const store = globalStore()
	const { moduleUnfoldOpen, topHeaderThemeColorOpen } = storeToRefs(store)
	const moduleBackColor = ref(topHeaderThemeColorOpen)
	const module = computed(() => {
		return store.module
	})
	const isMobile = computed(() => {
		return store.isMobile
	})
	const themeColor = computed(() => {
		return store.themeColor
	})
	const theme = computed(() => {
		return store.theme
	})
	// 圆角风格
	const roundedCornerStyleOpen = computed(() => {
		return store.roundedCornerStyleOpen
	})
	// 监听目录是否折叠
	watch(moduleUnfoldOpen, () => {
		nextTick(() => {
			setModuleBackColor()
		})
	})
	// 切换应用后
	watch(module, (newValue) => {
		selectedKeys.value = [newValue]
		setSelectedKeys()
	})
	// 颜色变化后
	watch(themeColor, () => {
		nextTick(() => {
			setModuleBackColor()
		})
	})
	// 暗黑明亮变化后
	watch(theme, () => {
		nextTick(() => {
			setModuleBackColor()
		})
	})
	// 屏幕缩小后再放大
	watch(isMobile, (newValue) => {
		if (!newValue) {
			nextTick(() => {
				setModuleBackColor()
			})
		}
	})
	// 监听是否开启了顶栏颜色
	watch(topHeaderThemeColorOpen, (newValue) => {
		moduleBackColor.value = newValue
		setModuleBackColor()
	})
	// 监听圆角变化
	watch(roundedCornerStyleOpen, () => {
		nextTick(() => {
			setModuleBackColor()
		})
	})
	const emit = defineEmits({ switchModule: null })
	const menu = router.getMenu()
	const selectedKeys = ref([module.value])
	const moduleClick = (id) => {
		emit('switchModule', id)
		tool.data.set('SNOWY_MENU_MODULE_ID', id)
		nextTick(() => {
			setSelectedKeys()
		})
	}
	const layout = computed(() => {
		return store.layout
	})
	onMounted(() => {
		setModuleBackColor()
	})
	// 设置背景色
	const setModuleBackColor = () => {
		if (moduleUnfoldOpen.value) {
			try {
				const moduleMenu = document.getElementById('moduleMenu')
				moduleBackColor.value
					? moduleMenu.classList.add('module-menu-color')
					: moduleMenu.classList.remove('module-menu-color')
				// eslint-disable-next-line no-empty
			} catch (err) {}
			setSelectedKeys()
		}
	}
	// 设置选中
	const setSelectedKeys = () => {
		// 顶部应用列表让显示出来默认的
		moduleBackColor.value
			? (selectedKeys.value = new Array([]))
			: (selectedKeys.value = [tool.data.get('SNOWY_MENU_MODULE_ID')])
	}
</script>

<style lang="less">
	.xn-pxn-r {
		position: relative;
	}
	.module-row {
		max-width: 357px;
	}
	.module-card {
		width: 70px;
		height: 70px;
		background-color: #0d84ff;
		text-align: center;
		align-items: center;
		cursor: pointer;
	}
	.module-card-radius-default {
		border-radius: 2px;
	}
	.module-card-radius-round {
		border-radius: 6px;
	}
	.module-card-icon {
		color: white;
		font-size: 16px;
		margin-top: 15px;
	}
	.module-card-font {
		color: white;
	}
	.ant-menu-horizontal > .ant-menu-item::after,
	.ant-menu-horizontal > .ant-menu-submenu::after {
		content: none;
	}
	.module-menu {
		line-height: 50px;
		border-bottom: 0;
		width: 105%;
		flex: 0 0 auto;
	}
	.module-menu-color {
		color: white;
		background-color: var(--primary-color);
	}
	.module-menu-color .ant-menu-item {
		color: white !important;
	}
	.module-card-scope {
		height: 49px;
	}
	.ant-menu-item-select {
		color: #ccc;
		background-color: var(--primary-7);
	}
	.xn-ml-5 {
		margin-left: -5px;
	}
	.ant-menu-horizontal > .ant-menu-item::after,
	.ant-menu-horizontal > .ant-menu-submenu::after {
		display: none;
	}
</style>
