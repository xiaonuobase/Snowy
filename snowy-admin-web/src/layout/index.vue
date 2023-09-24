<template>
	<!-- 经典布局 -->
	<classical
		v-if="layout === 'classical'"
		:is-mobile="isMobile"
		:menu-is-collapse="menuIsCollapse"
		:side-theme="sideTheme"
		:sys-base-config="sysBaseConfig"
		:open-keys="openKeys"
		:selected-keys="selectedKeys"
		:menu="menu"
		:breadcrumb-open="breadcrumbOpen"
		:layout-tags-open="layoutTagsOpen"
		:keep-live-route="keepLiveRoute"
		:route-show="routeShow"
		:route="route"
		@onSelect="onSelect"
		@onOpenChange="onOpenChange"
		@menuIsCollapseClick="menuIsCollapseClick"
		@switchModule="switchModule"
	/>
	<!-- 双排菜单布局 -->
	<double-row
		v-else-if="layout === 'doublerow'"
		:is-mobile="isMobile"
		:menu-is-collapse="menuIsCollapse"
		:side-theme="sideTheme"
		:sys-base-config="sysBaseConfig"
		:open-keys="openKeys"
		:selected-keys="selectedKeys"
		:menu="menu"
		:breadcrumb-open="breadcrumbOpen"
		:layout-tags-open="layoutTagsOpen"
		:keep-live-route="keepLiveRoute"
		:route-show="routeShow"
		:route="route"
		:layoutSiderDowbleMenu="layoutSiderDowbleMenu"
		:secondMenuSideTheme="secondMenuSideTheme"
		:nextMenu="nextMenu"
		:doublerowSelectedKey="doublerowSelectedKey"
		:pMenu="pMenu"
		@onSelect="onSelect"
		@showMenu="showMenu"
		@onOpenChange="onOpenChange"
		@menuIsCollapseClick="menuIsCollapseClick"
		@switchModule="switchModule"
	/>
	<!-- 退出最大化 -->
	<div class="main-maximize-exit" @click="exitMaximize">
		<fullscreen-exit-outlined style="color: #fff" />
	</div>
</template>

<script setup>
	import Classical from '@/layout/pattern/classical.vue'
	import DoubleRow from '@/layout/pattern/doublerow.vue'
	import { globalStore, keepAliveStore } from '@/store'
	import { ThemeModeEnum } from '@/utils/enum'
	import { useRouter, useRoute } from 'vue-router'
	import tool from '@/utils/tool'
	import { message } from 'ant-design-vue'

	const store = globalStore()
	const kStore = keepAliveStore()
	const route = useRoute()
	const router = useRouter()
	const menu = ref([])
	const pMenu = ref({})
	const nextMenu = ref([])
	const selectedKeys = ref([])
	const openKeys = ref([])
	const onSelectTag = ref(false)
	const moduleMenu = ref([])
	const doublerowSelectedKey = ref([])
	const layoutSiderDowbleMenu = ref(true)
	const currentRoute = ref()
	// computed计算方法 - start
	const layout = computed(() => {
		return store.layout
	})
	const isMobile = computed(() => {
		return store.isMobile
	})
	const menuIsCollapse = computed(() => {
		return store.menuIsCollapse
	})
	const theme = computed(() => {
		return store.theme
	})
	const layoutTagsOpen = computed(() => {
		return store.layoutTagsOpen
	})
	const breadcrumbOpen = computed(() => {
		return store.breadcrumbOpen
	})
	const topHeaderThemeColorOpen = computed(() => {
		return store.topHeaderThemeColorOpen
	})
	const topHeaderThemeColorSpread = computed(() => {
		return store.topHeaderThemeColorSpread
	})
	const sideUniqueOpen = computed(() => {
		return store.sideUniqueOpen
	})
	const sysBaseConfig = computed(() => {
		return store.sysBaseConfig
	})
	const module = computed(() => {
		return store.module
	})
	const keepLiveRoute = computed(() => {
		return kStore.keepLiveRoute
	})
	const routeShow = computed(() => {
		return kStore.routeShow
	})
	const sideTheme = computed(() => {
		return theme.value === ThemeModeEnum.REAL_DARK ? ThemeModeEnum.DARK : theme.value
	})
	const secondMenuSideTheme = computed(() => {
		return theme.value === ThemeModeEnum.REAL_DARK ? ThemeModeEnum.DARK : ThemeModeEnum.LIGHT
	})
	// 转换外部链接的路由
	const filterUrl = (map) => {
		const newMap = []
		const traverse = (maps) => {
			maps &&
				maps.forEach((item) => {
					item.meta = item.meta ? item.meta : {}
					// 处理隐藏
					if (item.meta.hidden) {
						return false
					}
					// 处理iframe
					if (item.meta.type === 'iframe') {
						item.path = `/i/${item.name}`
					}
					// 递归循环
					if (item.children && item.children.length > 0) {
						item.children = filterUrl(item.children)
					}
					newMap.push(item)
				})
		}
		traverse(map)
		return newMap
	}
	// 路由监听高亮
	const showThis = () => {
		pMenu.value = route.meta.breadcrumb ? route.meta.breadcrumb[0] : {}
		// 展开的
		nextTick(() => {
			// 取得默认路由地址并设置展开
			const active = route.meta.active || route.fullPath
			selectedKeys.value = new Array(active)
			const pidKey = getParentKeys(pMenu.value.children, active)
			const nextTickMenu = pMenu.value.children
			if (pidKey) {
				const parentPath = pidKey[pidKey.length - 1]
				if (layout.value === 'doublerow') {
					// 这一串操作下来只为取到最上面的路由的孩子们，最后成为双排菜单的第二排
					const nextMenuTemp = nextTickMenu.filter((item) => item.path === parentPath)[0].children
					if (nextMenuTemp) {
						nextMenu.value = nextTickMenu.filter((item) => item.path === parentPath)[0].children
					}
				}
			}
			if (!onSelectTag.value || sideUniqueOpen.value) {
				openKeys.value = pidKey
			}
			// 双排菜单下
			if (layout.value === 'doublerow') {
				setDoubleRowSelectedKey()
			}
		})
	}

	// 执行-start
	moduleMenu.value = router.getMenu()
	// 获取缓存中的菜单模块是哪个
	const menuModuleId = tool.data.get('SNOWY_MENU_MODULE_ID')
	let initMenu = []
	if (menuModuleId) {
		// 防止切换一个无此应用的人
		const module = router.getMenu().filter((item) => item.id === menuModuleId)
		if (module.length > 0) {
			initMenu = module[0].children
		} else {
			initMenu = router.getMenu()[0].children
		}
	} else {
		initMenu = router.getMenu()[0].children
	}
	menu.value = filterUrl(initMenu)
	showThis()

	onMounted(() => {
		switchoverTopHeaderThemeColor()
	})
	watch(route, (newValue) => {
		currentRoute.value = route.path
		// 清理选中的
		selectedKeys.value = []
		showThis()
		if (layoutTagsOpen.value) {
			const pidKey = getParentKeys(moduleMenu.value, route.path)
			moduleMenu.value.forEach((item) => {
				if (pidKey.includes(item.path)) {
					tagSwitchModule(item.id, route.path)
				}
			})
		}
	})
	// 监听是否开启了顶栏颜色
	watch(layout, (newValue) => {
		document.body.setAttribute('data-layout', newValue)
		if (newValue.includes('doublerow')) {
			showThis()
			setDoubleRowSelectedKey()
		}
		nextTick(() => {
			// 顶栏主题色
			switchoverTopHeaderThemeColor()
		})
	})
	watch(topHeaderThemeColorOpen, (newValue) => {
		console.log(topHeaderThemeColorOpen)
		switchoverTopHeaderThemeColor()
	})
	watch(topHeaderThemeColorSpread, (newValue) => {
		switchoverTopHeaderThemeColor()
	})

	const menuIsCollapseClick = () => {
		store.toggleConfig('menuIsCollapse')
	}
	// 切换顶栏颜色
	const switchoverTopHeaderThemeColor = () => {
		console.log('刷新完之后' + topHeaderThemeColorOpen.value)
		// 界面顶栏设置颜色
		const header = document.getElementById('snowyHeader')
		topHeaderThemeColorOpen.value
			? header.classList.add('snowy-header-primary-color')
			: header.classList.remove('snowy-header-primary-color')
		// 判断是否开启了通栏
		const headerLogin = document.getElementById('snowyHeaderLogo')
		try {
			topHeaderThemeColorSpread.value
				? headerLogin.classList.add('snowy-header-logo-primary-color')
				: headerLogin.classList.remove('snowy-header-logo-primary-color')
		} catch (e) {}
		// 如果是双排菜单，吧第二排的也给渲染了
		if (layout.value === 'doublerow') {
			const snowyDoublerowSideTop = document.getElementById('snowyDoublerowSideTop')
			try {
				topHeaderThemeColorSpread.value
					? snowyDoublerowSideTop.classList.add('snowy-doublerow-side-top-primary-color')
					: snowyDoublerowSideTop.classList.remove('snowy-doublerow-side-top-primary-color')
			} catch (e) {}
		}
	}

	// 设置双排菜单下的首列默认选中
	const setDoubleRowSelectedKey = () => {
		const pidKey = getParentKeys(menu.value, selectedKeys.value.toString())
		nextTick(() => {
			const pidKeyArray = []
			for (const key in pidKey) {
				pidKeyArray.push(key)
			}
			layoutSiderDowbleMenu.value = pidKeyArray.length > 1
		})
		// 设置第一排选中的
		menu.value.forEach((item) => {
			if (pidKey !== undefined) {
				if (pidKey[pidKey.length - 1].toString() === item.path) {
					doublerowSelectedKey.value = [item.path]
				}
			}
		})
	}
	// 菜单展开/关闭的回调
	const onOpenChange = (keys) => {
		if (sideUniqueOpen.value) {
			// 获取最新的
			const openKey = keys[keys.length - 1]
			if (keys.length > 1) {
				// 获取上级
				openKeys.value = getParentKeys(menu.value, openKey)
			} else {
				openKeys.value = Array.of(openKey) // new Array(openKey);
			}
		} else {
			openKeys.value = keys
		}
	}
	// 获取上级keys
	const getParentKeys = (data, val) => {
		const traverse = (array, val) => {
			// 递归父级key
			for (const element of array) {
				if (element.path === val) {
					return [element.path]
				}
				if (element.children) {
					const far = traverse(element.children, val)
					if (far) {
						return far.concat(element.path)
					}
				}
			}
		}
		return traverse(data, val)
	}
	// 双排菜单下点击显示右侧分栏
	const showMenu = (route) => {
		pMenu.value = route
		if (pMenu.value.children) {
			nextMenu.value = filterUrl(pMenu.value.children)
		}
		if (!route.children || route.children.length === 0) {
			layoutSiderDowbleMenu.value = false
			router.push({ path: route.path })
		} else {
			layoutSiderDowbleMenu.value = true
		}
		if (layout.value === 'doublerow') {
			doublerowSelectedKey.value = [route.path]
		}
	}
	// 当菜单被选中时
	const onSelect = (obj) => {
		onSelectTag.value = true
		const pathLength = obj.keyPath.length
		const path = obj.keyPath[pathLength - 1]
		router.push({ path })
		// 设置选中
		selectedKeys.value = obj.selectedKeys
	}
	const onLayoutResize = () => {
		const clientWidth = document.body.clientWidth
		store.setIsMobile(clientWidth < 992)
	}
	// 切换应用
	const switchModule = (id) => {
		if (moduleMenu.value.length > 0) {
			showThis()
			const menus = moduleMenu.value.filter((item) => item.id === id)[0].children
			if (menus.length > 0) {
				// 将此模块的唯一值加入缓存
				tool.data.set('SNOWY_MENU_MODULE_ID', id)
				// 正儿八百的菜单
				menu.value = filterUrl(menus)
				// 然后将其跳转至指定界面，默认始终取排序第一的
				const path = traverseChild(menu.value)
				router.push({ path })
			} else {
				message.warning('该模块下无任何菜单')
			}
		}
	}
	// 通过标签切换应用
	const tagSwitchModule = (id, path) => {
		// 将此模块的唯一值加入缓存
		tool.data.set('SNOWY_MENU_MODULE_ID', id)
		store.setModule(id)
		const menus = moduleMenu.value.filter((item) => item.id === id)[0].children
		// 正儿八百的菜单
		menu.value = filterUrl(menus)
		router.push({ path })
	}
	// 遍历子集获取一个path
	const traverseChild = (menu) => {
		if (menu[0].children !== undefined) {
			if (menu[0].children.length > 0) {
				return traverseChild(menu[0].children)
			} else {
				return menu[0].path
			}
		} else {
			return menu[0].path
		}
	}
	// 退出最大化
	const exitMaximize = () => {
		document.getElementById('app').classList.remove('main-maximize')
	}
</script>
