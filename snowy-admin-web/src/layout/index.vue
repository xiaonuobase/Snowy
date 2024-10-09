<template>
	<!-- 经典布局 -->
	<ClassicalMenu
		v-if="layout === layoutEnum.CLASSICAL"
		:layout="layout"
		:isMobile="isMobile"
		:menuIsCollapse="menuIsCollapse"
		:sideTheme="sideTheme"
		:sysBaseConfig="sysBaseConfig"
		:openKeys="openKeys"
		:selectedKeys="selectedKeys"
		:menu="menu"
		:breadcrumbOpen="breadcrumbOpen"
		:layoutTagsOpen="layoutTagsOpen"
		:kStore="kStore"
		:footerCopyrightOpen="footerCopyrightOpen"
		:moduleMenuShow="moduleMenuShow"
		@onSelect="onSelect"
		@onOpenChange="onOpenChange"
		@switchModule="switchModule"
		@menuIsCollapseClick="menuIsCollapseClick"
	/>
	<!-- 双排菜单布局 -->
	<DoubleRowMenu
		v-else-if="layout === layoutEnum.DOUBLEROW"
		:layout="layout"
		:isMobile="isMobile"
		:sideTheme="sideTheme"
		:secondMenuSideTheme="secondMenuSideTheme"
		:sysBaseConfig="sysBaseConfig"
		:openKeys="openKeys"
		:selectedKeys="selectedKeys"
		:menuIsCollapse="menuIsCollapse"
		:doublerowSelectedKey="doublerowSelectedKey"
		:menu="menu"
		:nextMenu="nextMenu"
		:breadcrumbOpen="breadcrumbOpen"
		:layoutTagsOpen="layoutTagsOpen"
		:layoutSiderDowbleMenu="layoutSiderDowbleMenu"
		:kStore="kStore"
		:footerCopyrightOpen="footerCopyrightOpen"
		:moduleMenuShow="moduleMenuShow"
		@onSelect="onSelect"
		@switchModule="switchModule"
		@showMenu="showMenu"
	/>
	<!-- 顶部菜单布局 -->
	<TopMenu
		v-else-if="layout === layoutEnum.TOP"
		:layout="layout"
		:menuList="menuList"
		:menu="menu"
		:sysBaseConfig="sysBaseConfig"
		:moduleMenuShow="moduleMenuShow"
		:openKeys="openKeys"
		:selectedKeys="selectedKeys"
		:breadcrumbOpen="breadcrumbOpen"
		:footerCopyrightOpen="footerCopyrightOpen"
		:sideTheme="sideTheme"
		:isMobile="isMobile"
		:kStore="kStore"
		:layoutTagsOpen="layoutTagsOpen"
		@switchModule="switchModule"
		@onOpenChange="onOpenChange"
		@onSelect="onSelect"
	/>

	<!-- 退出最大化 -->
	<div class="main-maximize-exit" @click="exitMaximize">
		<fullscreen-exit-outlined class="xn-color-fff" />
	</div>
</template>

<script setup>
	import { globalStore, keepAliveStore, viewTagsStore } from '@/store'
	import { themeEnum } from '@/layout/enum/themeEnum'
	import { layoutEnum } from '@/layout/enum/layoutEnum'
	import { useRoute, useRouter } from 'vue-router'
	import tool from '@/utils/tool'
	import { notification, Button } from 'ant-design-vue'
	import ClassicalMenu from '@/layout/menu/classicalMenu.vue'
	import DoubleRowMenu from '@/layout/menu/doubleRowMenu.vue'
	import TopMenu from '@/layout/menu/topMenu.vue'
	import { NextLoading } from '@/utils/loading'
	import { useMenuStore } from '@/store/menu'
	import { userStore } from '@/store/user'
	import { getLocalHash, checkHash } from '@/utils/version'
	import sysConfig from '@/config/index'
	import dictApi from '@/api/dev/dictApi'

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
	const moduleMenuShow = ref(true)
	const doublerowSelectedKey = ref([])
	const layoutSiderDowbleMenu = ref(true)
	const menuList = ref([])
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
	const themeColor = computed(() => {
		return store.themeColor
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
	const fixedWidth = computed(() => {
		return store.fixedWidth
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
	const footerCopyrightOpen = computed(() => {
		return store.footerCopyrightOpen
	})
	const sysBaseConfig = computed(() => {
		return store.sysBaseConfig
	})
	const module = computed(() => {
		return store.module
	})
	const sideTheme = computed(() => {
		return theme.value === themeEnum.REAL_DARK ? themeEnum.DARK : theme.value
	})
	const secondMenuSideTheme = computed(() => {
		return theme.value === themeEnum.REAL_DARK ? themeEnum.DARK : themeEnum.LIGHT
	})
	const roundedCornerStyleOpen = computed(() => {
		return store.roundedCornerStyleOpen
	})
	// 路由监听高亮
	const showThis = () => {
		// route是一个只读路由对象。需要使用 useRouter 函数来获取路由实例
		router.getRoutes().filter((item) => {
			if (item.path === route.path) {
				pMenu.value = item.meta.breadcrumb ? item.meta.breadcrumb[0] : {}
			}
		})
		// pMenu.value = route.meta.breadcrumb ? route.meta.breadcrumb[0] : {}
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
				const modelPidKey = getParentKeys(moduleMenu.value, route.path)
				moduleMenu.value.forEach((item) => {
					if (modelPidKey.includes(item.path)) {
						tagSwitchModule(item.id)
					}
				})
				const parentPath = pidKey[pidKey.length - 1]
				if (layout.value === layoutEnum.DOUBLEROW) {
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
			if (layout.value === layoutEnum.DOUBLEROW) {
				setDoubleRowSelectedKey()
			}
		})
	}
	const init = () => {
		// 执行-start
		moduleMenu.value = router.getMenu()
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
	}

	watchEffect(() => {
		const menuStore = useMenuStore()
		if (menuStore.refreshFlag) {
			init()
			// 更新标签
			viewTagsStore().updateOrRemoveViewTags(router.getRoutes())
			menuStore.changeRefreshFlag(false)
		}
	})

	onMounted(() => {
		// 取消loading
		NextLoading.done()
		showThis()
		onLayoutResize()
		window.addEventListener('resize', onLayoutResize)
		window.addEventListener('resize', getNav)
		switchoverTopHeaderThemeColor()
		settingTopHeaderThemeOrColor(theme.value, layout.value)
		settingFixedWidth()
		updateVersion()
		nextTick(() => {
			getNav()
			// 刷新登录信息，不影响其他
			userStore().refreshUserLoginUserInfo()
			// 刷新菜单信息，不影响其他
			useMenuStore().refreshApiMenu()
			// 刷新字典信息，不影响其他
			dictApi.dictTree().then((data) => {
				// 设置字典到store中
				tool.data.set('DICT_TYPE_TREE_DATA', data)
			})
		})
	})
	onBeforeUnmount(() => {
		window.removeEventListener('resize', onLayoutResize)
		window.removeEventListener('resize', getNav)
	})
	// 新版检测
	const updateVersion = () => {
		const updateVersionOpen = import.meta.env.VITE_VERSION_UPDATE
		if (updateVersionOpen) {
			setTimeout(async () => {
				// 本地
				let localVersion = getLocalHash()
				// 线上
				let onlineVersion = await checkHash()
				// 如果不一样，提示更新
				if (localVersion !== onlineVersion) {
					if (document.querySelector('.notification-update-version')) {
						return
					}
					const key = `open${Date.now()}`
					notification.open({
						type: 'info',
						message: '发现新版本',
						description: '检测到新版本，请刷新后使用',
						duration: 0,
						class: 'notification-update-version',
						btn: () =>
							h(
								Button,
								{
									type: 'primary',
									size: 'small',
									onClick: () => {
										notification.close(key)
										window.location.reload()
									}
								},
								{ default: () => '立即更新' }
							),
						key
					})
				}
			}, 3000)
		}
	}
	// 动态获取横向导航栏隐藏数量
	const getNav = () => {
		// 判断一下是不是顶部导航栏的模式
		if (layout.value !== 'top') return
		const menuNavList = menu.value
		menuList.value = menuNavList
		nextTick(() => {
			// 获取所有的导航菜单
			let liArr = document.querySelector('#topHeaderMenu').querySelectorAll('li')
			let allWidth = document.querySelector('#xn-line-nav').offsetWidth // 可以显示区域的宽度

			// 计算显示的宽度
			let num = 0
			let startIndex = 0
			for (const [index, item] of liArr.entries()) {
				num += item.offsetWidth
				if (num > allWidth) {
					startIndex = index - 1
					break
				}
			}
			// 判断显示出来的导航栏长度是否小于可以显示的区域
			if (num < allWidth) {
				menuList.value = menuNavList
				return
			}
			// 如果大于了显示的区域，就将其隐藏
			const showNav = menuNavList.slice(0, startIndex)
			const hiddenNav = menuNavList.slice(startIndex, menuNavList.length)
			menuList.value = showNav
			menuList.value.push({
				meta: {
					icon: 'rightCircle-outlined',
					title: '更多',
					type: 'catalog'
				},
				children: hiddenNav
			})
		})
	}

	// 鼠标滚动事件
	const handleMouseWheel = (event) => {
		let element = document.querySelector('#xn-line-nav')
		let element2 = document.querySelector('#topHeaderMenu')

		// 判断鼠标是向上滚动还是向下滚动的
		let delta = event.deltaY
		// 滚动菜单时变换的一个大小
		const num = 20

		// 滚动的距离
		let leftMove = Number(element2.style.left.slice(0, -2))
		// 父盒子相对子盒子滚动的一个判断，用来给盒子加一个临界点
		let remove = element.offsetWidth - element2.scrollWidth

		// 鼠标向下滚动
		// 满足子元素移动的距离大于两个元素相差的距离时
		if (delta < 0 && leftMove > remove) {
			element2.style.left = leftMove - num + 'px'
		} else if (delta > 0 && leftMove < 0) {
			// 鼠标向上滚动
			// 当移动的距离小于0的时候，可以向后滚动
			element2.style.left = leftMove + num + 'px'
		}
	}

	watch(route, () => {
		// 清理选中的
		selectedKeys.value = []
		showThis()
	})
	// 监听是否开启了顶栏颜色
	watch(layout, (newValue) => {
		document.body.setAttribute('data-layout', newValue)
		if (newValue.includes(layoutEnum.DOUBLEROW)) {
			showThis()
			setDoubleRowSelectedKey()
		}
		nextTick(() => {
			// 顶栏主题色
			switchoverTopHeaderThemeColor()
			// top下的顶栏
			settingTopHeaderThemeOrColor(theme.value, newValue)
			getNav()
			settingFixedWidth()
			let element = document.querySelector('#xn-line-nav')
			if (element) {
				element.addEventListener('mousewheel', handleMouseWheel, false)
			}
		})
	})
	watch(topHeaderThemeColorOpen, () => {
		switchoverTopHeaderThemeColor()
	})
	watch(fixedWidth, () => {
		settingFixedWidth()
	})
	watch(layoutTagsOpen, () => {
		settingFixedWidth()
	})
	watch(breadcrumbOpen, () => {
		settingFixedWidth()
	})
	watch(topHeaderThemeColorSpread, () => {
		switchoverTopHeaderThemeColor()
	})
	watch(theme, (newValue) => {
		settingTopHeaderThemeOrColor(newValue, layout.value)
	})
	watch(themeColor, () => {
		settingTopHeaderThemeOrColor(theme.value, layout.value)
	})
	watch(topHeaderThemeColorOpen, (newValue) => {
		const header = document.getElementById('snowyHeader')
		const topHeaderMenu = document.getElementById('topHeaderMenu')
		if (layout.value === layoutEnum.TOP) {
			if (newValue) {
				header.classList.add('top-snowy-header-layout')
				topHeaderMenu.classList.add('top-snowy-header-layout')
			} else {
				header.classList.remove('top-snowy-header-layout')
				topHeaderMenu.classList.remove('top-snowy-header-layout')
			}
		}
	})
	watch(roundedCornerStyleOpen, () => {
		settingTopHeaderThemeOrColor(theme.value, layout.value)
	})
	// 设置固定宽度
	const settingFixedWidth = () => {
		nextTick(() => {
			const breadcrumbWidth = document.querySelector('.admin-ui-breadcrumb')
			const showWidth = document.querySelector('.snowy-tags')
			const mainWidth = document.querySelector('.ant-layout-content')
			if (fixedWidth.value && layout.value === layoutEnum.TOP) {
				breadcrumbWidth?.classList.add('xn-mg050')
				showWidth?.classList.add('xn-mg050')
				mainWidth?.classList.add('xn-pd1180')
			} else {
				breadcrumbWidth?.classList.remove('xn-mg050')
				showWidth?.classList.remove('xn-mg050')
				mainWidth?.classList.remove('xn-pd1180')
			}
		})
	}

	// 设置顶栏颜色或者主题
	const settingTopHeaderThemeOrColor = (theme, layout) => {
		const header = document.getElementById('snowyHeader')
		const topHeaderMenu = document.getElementById('topHeaderMenu')

		if (topHeaderThemeColorOpen.value && layout === layoutEnum.TOP) {
			nextTick(() => {
				topHeaderMenu.classList.add('top-snowy-header-layout')
				header.classList.add('top-snowy-header-layout')
			})
		} else if (!topHeaderThemeColorOpen.value && layout === layoutEnum.TOP) {
			nextTick(() => {
				topHeaderMenu.classList.remove('top-snowy-header-layout')
				header.classList.remove('top-snowy-header-layout')
			})
		}
		if (theme === themeEnum.LIGHT && layout === layoutEnum.TOP) {
			header.classList.remove('top-snowy-header')
			header.classList.add('top-snowy-header-light')
		} else {
			header.classList.remove('top-snowy-header-light')
			if (layout === layoutEnum.TOP) {
				header.classList.add('top-snowy-header')
			} else {
				if (theme === themeEnum.REAL_DARK) {
					header.classList.add('top-snowy-header')
				} else {
					header.classList.remove('top-snowy-header')
				}
			}
		}
	}
	const menuIsCollapseClick = () => {
		store.toggleConfig('menuIsCollapse')
	}
	// 切换顶栏颜色
	const switchoverTopHeaderThemeColor = () => {
		try {
			// 界面顶栏设置颜色
			const header = document.getElementById('snowyHeader')
			topHeaderThemeColorOpen.value
				? header.classList.add('snowy-header-primary-color')
				: header.classList.remove('snowy-header-primary-color')
			// 判断是否开启了通栏
			const headerLogin = document.getElementById('snowyHeaderLogo')
			topHeaderThemeColorSpread.value
				? headerLogin.classList.add('snowy-header-logo-primary-color')
				: headerLogin.classList.remove('snowy-header-logo-primary-color')
			// eslint-disable-next-line no-empty
		} catch (e) {}
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
		if (layout.value === layoutEnum.DOUBLEROW) {
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
		getNav()
	}
	// 通过标签切换应用
	const tagSwitchModule = (id) => {
		// 将此模块的唯一值加入缓存
		tool.data.set('SNOWY_MENU_MODULE_ID', id)
		store.setModule(id)
		// 正儿八百的菜单
		menu.value = moduleMenu.value.filter((item) => item.id === id)[0].children
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

<style lang="less" scoped>
	.xn-color-fff {
		color: #fff;
	}
	.xn-pdl25 {
		padding-left: 11px;
	}
	.xn-menu-line {
		text-align: center;
		height: auto;
		line-height: 20px;
		flex: none;
		display: block;
		padding: 12px 0 !important;
	}
	.xn-navmenu-line {
		min-width: 0;
		flex: 1 1 0%;
		// padding: 0 20px;
		overflow: hidden;
	}
	.xn-bb0 {
		border-bottom: none;
		position: relative;
	}
	.ant-layout-content {
		display: flex;
		flex-direction: column;
	}
	.xn-pd1180 {
		padding: 10px 150px 0 150px;
	}
	.xn-pd050 {
		padding: 0 50px;
	}
	.xn-pl10 {
		padding-left: 10px;
	}
	.xn-mg050 {
		margin: 0px 150px;
	}
</style>
