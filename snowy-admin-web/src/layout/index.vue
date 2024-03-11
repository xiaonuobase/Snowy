<template>
	<!-- 经典布局 -->
	<a-layout v-if="layout === 'classical'">
		<a-layout-sider
			v-if="!isMobile"
			v-model:collapsed="menuIsCollapse"
			:trigger="null"
			collapsible
			:theme="sideTheme"
			width="210"
		>
			<header id="snowyHeaderLogo" class="snowy-header-logo">
				<div class="snowy-header-left">
					<div class="logo-bar">
						<img class="logo" :src="sysBaseConfig.SNOWY_SYS_LOGO" />
						<span>{{ sysBaseConfig.SNOWY_SYS_NAME }}</span>
					</div>
				</div>
			</header>
			<div :class="menuIsCollapse ? 'admin-ui-side isCollapse' : 'admin-ui-side'">
				<div class="admin-ui-side-scroll">
					<a-menu
						v-model:openKeys="openKeys"
						v-model:selectedKeys="selectedKeys"
						:theme="sideTheme"
						mode="inline"
						@select="onSelect"
						@openChange="onOpenChange"
					>
						<NavMenu :nav-menus="menu" />
					</a-menu>
				</div>
			</div>
		</a-layout-sider>
		<!-- 手机端情况下的左侧菜单 -->
		<Side-m v-if="isMobile" />
		<!-- 右侧布局 -->
		<a-layout>
			<div id="snowyHeader" class="snowy-header">
				<div class="snowy-header-left" style="padding-left: 0px">
					<div v-if="!isMobile" class="panel-item hidden-sm-and-down" @click="menuIsCollapseClick">
						<MenuUnfoldOutlined v-if="menuIsCollapse" />
						<MenuFoldOutlined v-else />
					</div>
					<moduleMenu v-if="moduleMenuShow" @switchModule="switchModule" />
					<top-bar v-if="!isMobile && breadcrumbOpen" />
				</div>
				<div class="snowy-header-right">
					<user-bar />
				</div>
			</div>
			<!-- 多标签 -->
			<Tags v-if="!isMobile && layoutTagsOpen" />
			<a-layout-content class="main-content-wrapper">
				<div id="admin-ui-main" class="admin-ui-main">
					<router-view v-slot="{ Component }">
						<keep-alive :include="kStore.keepLiveRoute">
							<component :is="Component" v-if="kStore.routeShow" :key="route.name" />
						</keep-alive>
					</router-view>
					<iframe-view />
					<div class="main-bottom-wrapper">
						<a style="color: #a0a0a0" :href="sysBaseConfig.SNOWY_SYS_COPYRIGHT_URL" target="_blank">{{
							sysBaseConfig.SNOWY_SYS_COPYRIGHT
						}}</a>
					</div>
				</div>
			</a-layout-content>
		</a-layout>
	</a-layout>
	<!-- 双排菜单布局 -->
	<a-layout v-else-if="layout === 'doublerow'">
		<a-layout-sider v-if="!isMobile" width="80" :theme="sideTheme" :trigger="null" collapsible>
			<header id="snowyHeaderLogo" class="snowy-header-logo">
				<div class="snowy-header-left">
					<div class="logo-bar">
						<router-link to="/">
							<img class="logo" :title="sysBaseConfig.SNOWY_SYS_NAME" :src="sysBaseConfig.SNOWY_SYS_LOGO" />
						</router-link>
					</div>
				</div>
			</header>
			<a-menu
				v-model:selectedKeys="doublerowSelectedKey"
				:theme="sideTheme"
				class="snowy-doublerow-layout-menu"
				v-for="item in menu"
				:key="item.path"
			>
				<a-menu-item
					:key="item.path"
					style="
						text-align: center;
						border-radius: 2px;
						height: auto;
						line-height: 20px;
						flex: none;
						display: block;
						padding: 12px 0 !important;
					"
					@click="showMenu(item)"
					v-if="!item.meta.hidden"
				>
					<a v-if="item.meta && item.meta.type === 'link'" :href="item.path" target="_blank" @click.stop="() => {}" />
					<template #icon>
						<component :is="item.meta.icon" style="padding-left: 10px" />
					</template>
					<div class="snowy-doublerow-layout-menu-item-fort-div">
						<span class="snowy-doublerow-layout-menu-item-fort-div-span">
							{{ item.meta.title }}
						</span>
					</div>
				</a-menu-item>
			</a-menu>
		</a-layout-sider>
		<a-layout-sider
			v-if="!isMobile"
			v-show="layoutSiderDowbleMenu"
			v-model:collapsed="menuIsCollapse"
			:trigger="null"
			width="170"
			collapsible
			:theme="secondMenuSideTheme"
		>
			<div v-if="!menuIsCollapse" id="snowyDoublerowSideTop" class="snowy-doublerow-side-top">
				<h2 class="snowy-title">{{ pMenu.meta.title }}</h2>
			</div>
			<a-menu
				v-model:collapsed="menuIsCollapse"
				v-model:openKeys="openKeys"
				v-model:selectedKeys="selectedKeys"
				mode="inline"
				:theme="secondMenuSideTheme"
				@select="onSelect"
			>
				<NavMenu :nav-menus="nextMenu" />
			</a-menu>
		</a-layout-sider>
		<!-- 手机端情况下的左侧菜单 -->
		<Side-m v-if="isMobile" />
		<a-layout>
			<div id="snowyHeader" class="snowy-header">
				<div class="snowy-header-left" style="padding-left: 0px">
					<moduleMenu v-if="moduleMenuShow" @switchModule="switchModule" />
					<top-bar v-if="!isMobile && breadcrumbOpen" />
				</div>
				<div class="snowy-header-right">
					<user-bar />
				</div>
			</div>
			<!-- 多标签 -->
			<Tags v-if="!isMobile && layoutTagsOpen" />
			<a-layout-content class="main-content-wrapper">
				<div id="admin-ui-main" class="admin-ui-main">
					<router-view v-slot="{ Component }">
						<keep-alive :include="kStore.keepLiveRoute">
							<component :is="Component" v-if="kStore.routeShow" :key="route.name" />
						</keep-alive>
					</router-view>
					<iframe-view />
					<div class="main-bottom-wrapper">
						<a style="color: #a0a0a0" :href="sysBaseConfig.SNOWY_SYS_COPYRIGHT_URL" target="_blank">{{
							sysBaseConfig.SNOWY_SYS_COPYRIGHT
						}}</a>
					</div>
				</div>
			</a-layout-content>
		</a-layout>
	</a-layout>
	<!-- 退出最大化 -->
	<div class="main-maximize-exit" @click="exitMaximize">
		<fullscreen-exit-outlined style="color: #fff" />
	</div>
</template>

<script setup>
	import UserBar from '@/layout/components/userbar.vue'
	import Tags from '@/layout/components/tags.vue'
	import SideM from '@/layout/components/sideM.vue'
	import NavMenu from '@/layout/components/NavMenu.vue'
	import ModuleMenu from '@/layout/components/moduleMenu.vue'
	import IframeView from '@/layout/components/iframeView.vue'
	import TopBar from '@/layout/components/topbar.vue'
	import { globalStore, keepAliveStore } from '@/store'
	import { ThemeModeEnum } from '@/utils/enum'
	import { useRoute, useRouter } from 'vue-router'
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
	const moduleMenuData = ref([])
	const moduleMenuShow = ref(true)
	const doublerowSelectedKey = ref([])
	const layoutSiderDowbleMenu = ref(true)
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
		// 当关闭多标签时，清理keepAlive的缓存
		if (!store.layoutTagsOpen) {
			kStore.keepLiveRoute = []
		}
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
	const sideTheme = computed(() => {
		return theme.value === ThemeModeEnum.REAL_DARK ? ThemeModeEnum.DARK : theme.value
	})
	const secondMenuSideTheme = computed(() => {
		return theme.value === ThemeModeEnum.REAL_DARK ? ThemeModeEnum.DARK : ThemeModeEnum.LIGHT
	})
	// 路由监听高亮
	const showThis = () => {
		pMenu.value = route.meta.breadcrumb ? route.meta.breadcrumb[0] : {}
		// 展开的
		nextTick(() => {
			// 取得默认路由地址并设置展开
			let active = route.meta.active || route.path
			// 如果是目录，必须往下找
			if (route.meta.type === 'catalog') {
				active = traverseChild(pMenu.value.children, active).path
			}
			selectedKeys.value = new Array(active)
			const pidKey = getParentKeys(pMenu.value.children, active)
			// 判断是隐藏的路由，找其上级
			if (route.meta.hidden && pidKey) {
				if (pidKey.length > 1) {
					selectedKeys.value = new Array(pidKey[1])
				}
			}
			const nextTickMenu = pMenu.value.children
			if (pidKey) {
				const modelPidKey = getParentKeys(moduleMenuData.value, route.path)
				moduleMenuData.value.forEach((item) => {
					if (modelPidKey.includes(item.path)) {
						tagSwitchModule(item.id)
					}
				})
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
	moduleMenuData.value = router.getMenu()
	// 获取缓存中的菜单模块是哪个
	const menuModuleId = tool.data.get('SNOWY_MENU_MODULE_ID')
	if (menuModuleId) {
		// 防止切换一个无此应用的人
		const module = router.getMenu().filter((item) => item.id === menuModuleId)
		if (module.length > 0) {
			menu.value = module[0].children
		} else {
			menu.value = router.getMenu()[0].children
		}
	} else {
		menu.value = router.getMenu()[0].children
	}
	showThis()

	onMounted(() => {
		onLayoutResize()
		window.addEventListener('resize', onLayoutResize)
		switchoverTopHeaderThemeColor()
	})
	watch(route, (newValue) => {
		// 清理选中的
		selectedKeys.value = []
		showThis()
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
			// eslint-disable-next-line no-empty
		} catch (e) {}
		// 如果是双排菜单，吧第二排的也给渲染了
		if (layout.value === 'doublerow') {
			const snowyDoublerowSideTop = document.getElementById('snowyDoublerowSideTop')
			try {
				topHeaderThemeColorSpread.value
					? snowyDoublerowSideTop.classList.add('snowy-doublerow-side-top-primary-color')
					: snowyDoublerowSideTop.classList.remove('snowy-doublerow-side-top-primary-color')
				// eslint-disable-next-line no-empty
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
			nextMenu.value = pMenu.value.children
		}
		if (!route.children || route.children.length === 0) {
			layoutSiderDowbleMenu.value = false
			router.push({ path: route.path })
		} else {
			if (route.children) {
				let hidden = 0
				route.children.forEach((item) => {
					if (item.meta.hidden && item.meta.hidden === true) {
						hidden++
					}
				})
				// 如果全部都隐藏了，就跳转这个，不展开另一排
				if (hidden === route.children.length) {
					layoutSiderDowbleMenu.value = false
					router.push({ path: route.path })
				} else {
					layoutSiderDowbleMenu.value = true
				}
			} else {
				layoutSiderDowbleMenu.value = false
			}
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
		if (moduleMenuData.value.length > 0) {
			showThis()
			const menus = moduleMenuData.value.filter((item) => item.id === id)[0].children
			if (menus.length > 0) {
				// 正儿八百的菜单
				menu.value = menus
				const firstMenu = traverseChild(menu.value)
				const path = firstMenu.path
				// 如果是外链
				if (firstMenu.menuType === 'LINK') {
					window.open(path)
				} else {
					// 将此模块的唯一值加入缓存
					tool.data.set('SNOWY_MENU_MODULE_ID', id)
					// 然后将其跳转至指定界面，默认始终取排序第一的
					router.push({ path })
				}
			} else {
				message.warning('该模块下无任何菜单')
			}
		}
	}
	// 通过标签切换应用
	const tagSwitchModule = (id) => {
		// 将此模块的唯一值加入缓存
		tool.data.set('SNOWY_MENU_MODULE_ID', id)
		store.setModule(id)
		// 正儿八百的菜单
		menu.value = moduleMenuData.value.filter((item) => item.id === id)[0].children
	}
	// 遍历获取子集
	const traverseChild = (menu) => {
		if (menu[0] && menu[0].children !== undefined) {
			if (menu[0].children.length > 0) {
				if (menu[0].children[0] && menu[0].children[0].meta.hidden && menu[0].children[0].meta.hidden === true) {
					return menu[0]
				} else {
					return traverseChild(menu[0].children)
				}
			}
		} else {
			return menu[0]
		}
	}
	// 退出最大化
	const exitMaximize = () => {
		document.getElementById('app').classList.remove('main-maximize')
		moduleMenuShow.value = false
		nextTick(() => {
			moduleMenuShow.value = true
		})
	}
</script>
