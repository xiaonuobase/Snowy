<template>
	<!-- 经典布局 -->
	<template v-if="layout === 'classical'">
		<a-layout>
			<a-layout-sider
				v-if="!ismobile"
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
				<div :class="menuIsCollapse ? 'aminui-side isCollapse' : 'aminui-side'">
					<div class="adminui-side-scroll">
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
			<Side-m v-if="ismobile" />
			<!-- 右侧布局 -->
			<a-layout>
				<div id="snowyHeader" class="snowy-header">
					<div class="snowy-header-left" style="padding-left: 0px">
						<div v-if="!ismobile" class="panel-item hidden-sm-and-down" @click="menuIsCollapseClick">
							<MenuUnfoldOutlined v-if="menuIsCollapse" />
							<MenuFoldOutlined v-else />
						</div>
						<moduleMenu @switchModule="switchModule" />
						<Topbar v-if="!ismobile && breadcrumbOpen" />
					</div>
					<div class="snowy-header-right">
						<userbar />
					</div>
				</div>
				<!-- 多标签 -->
				<Tags v-if="!ismobile && layoutTagsOpen" />
				<a-layout-content class="main-content-wrapper">
					<div id="adminui-main" class="adminui-main">
						<router-view v-slot="{ Component }">
							<keep-alive :include="keepLiveRoute">
								<component :is="Component" :key="$route.name" v-if="routeShow" />
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
	</template>

	<!-- 双排菜单布局 -->
	<template v-else-if="layout === 'doublerow'">
		<a-layout>
			<a-layout-sider v-if="!ismobile" width="80" :theme="sideTheme" :trigger="null" collapsible>
				<header id="snowyHeaderLogo" class="snowy-header-logo">
					<div class="snowy-header-left">
						<div class="logo-bar">
							<router-link to="/">
								<img class="logo" :title="sysBaseConfig.SNOWY_SYS_NAME" :src="sysBaseConfig.SNOWY_SYS_LOGO" />
							</router-link>
						</div>
					</div>
				</header>
				<a-menu v-model:selectedKeys="doublerowSelectedKey" :theme="sideTheme" class="snowy-doublerow-layout-menu">
					<a-menu-item
						v-for="item in menu"
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
					>
						<a
							v-if="item.meta && item.meta.type === 'link'"
							:href="item.path"
							target="_blank"
							@click.stop="() => {}"
						></a>
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
				v-if="!ismobile"
				v-show="layoutSiderDowbleMenu"
				v-model:collapsed="menuIsCollapse"
				:trigger="null"
				width="170"
				collapsible
				:theme="secondMenuSideTheme"
			>
				<div v-if="!menuIsCollapse" id="snowyDoublerowSideTop" class="snowy-doublerow-side-top">
					<h2 class="snowy-title">{{ pmenu.meta.title }}</h2>
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
			<Side-m v-if="ismobile" />
			<a-layout>
				<div id="snowyHeader" class="snowy-header">
					<div class="snowy-header-left" style="padding-left: 0px">
						<div v-if="!ismobile" class="panel-item hidden-sm-and-down" @click="menuIsCollapseClick">
							<MenuUnfoldOutlined v-if="menuIsCollapse" />
							<MenuFoldOutlined v-else />
						</div>
						<moduleMenu @switchModule="switchModule" />
						<Topbar v-if="!ismobile && breadcrumbOpen" />
					</div>
					<div class="snowy-header-right">
						<userbar />
					</div>
				</div>
				<!-- 多标签 -->
				<Tags v-if="!ismobile && layoutTagsOpen"></Tags>
				<a-layout-content class="main-content-wrapper">
					<div id="adminui-main" class="adminui-main">
						<router-view v-slot="{ Component }">
							<keep-alive :include="keepLiveRoute">
								<component :is="Component" v-if="routeShow" :key="$route.name" />
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
	</template>

	<!-- 退出最大化 -->
	<div class="main-maximize-exit" @click="exitMaximize">
		<fullscreen-exit-outlined style="color: #fff" />
	</div>
</template>

<script>
	import SideM from './components/sideM.vue'
	import Topbar from './components/topbar.vue'
	import Tags from './components/tags.vue'
	import NavMenu from './components/NavMenu.vue'
	import userbar from './components/userbar.vue'
	import iframeView from './components/iframeView.vue'
	import moduleMenu from './components/moduleMenu.vue'
	import { ThemeModeEnum } from '@/utils/enum'
	import { globalStore, keepAliveStore } from '@/store'
	import { mapState, mapStores, mapActions } from 'pinia'
	import tool from '@/utils/tool'

	export default defineComponent({
		name: 'Index',
		components: {
			SideM,
			Topbar,
			Tags,
			NavMenu,
			userbar,
			moduleMenu,
			iframeView
		},
		data() {
			return {
				menu: [],
				moduleMenu: [],
				nextMenu: [],
				pmenu: {},
				doublerowSelectedKey: [],
				layoutSiderDowbleMenu: true,
				onSelectTag: false,
				selectedKeys: [],
				openKeys: [],
				openKeysOther: []
			}
		},
		computed: {
			...mapStores(globalStore),
			...mapState(globalStore, [
				'theme',
				'ismobile',
				'layout',
				'layoutTagsOpen',
				'menuIsCollapse',
				'breadcrumbOpen',
				'topHanderThemeColorOpen',
				'topHanderThemeColorSpread',
				'topHanderThemeColor',
				'sideUniqueOpen',
				'sysBaseConfig'
			]),
			...mapState(keepAliveStore, ['keepLiveRoute', 'routeShow']),
			sideTheme() {
				const theme = this.theme
				return theme === ThemeModeEnum.REAL_DARK ? ThemeModeEnum.DARK : theme
			},
			secondMenuSideTheme() {
				const theme = this.theme
				return theme === ThemeModeEnum.REAL_DARK ? ThemeModeEnum.DARK : ThemeModeEnum.LIGHT
			}
		},
		watch: {
			$route() {
				this.showThis()
			},
			layout: {
				handler(val) {
					document.body.setAttribute('data-layout', val)
					if (val.includes('doublerow')) {
						this.setDoublerowSelectedKey()
					}
					this.$nextTick(() => {
						// 顶栏主题色
						this.switchoverTopHanderThemeColor()
					})
				},
				immediate: true
			},
			topHanderThemeColorOpen() {
				this.switchoverTopHanderThemeColor()
			},
			topHanderThemeColorSpread() {
				this.switchoverTopHanderThemeColor()
			}
		},
		created() {
			// 判断浏览器宽度，顺手加入缓存
			this.onLayoutResize()
			window.addEventListener('resize', this.onLayoutResize)
			this.moduleMenu = this.$router.getMenu()
			// 获取缓存中的菜单模块是哪个
			const menuModuleId = tool.data.get('SNOWY_MENU_MODULE_ID')
			let menu = []
			if (menuModuleId) {
				// 防止切换一个无此应用的人
				const module = this.$router.getMenu().filter((item) => item.id === menuModuleId)
				if (module.length > 0) {
					menu = module[0].children
				} else {
					menu = this.$router.getMenu()[0].children
				}
			} else {
				menu = this.$router.getMenu()[0].children
			}
			// 此菜单为正常模块下的菜单
			this.menu = this.filterUrl(menu)
			this.showThis()
		},
		mounted() {
			this.switchoverTopHanderThemeColor()
		},
		methods: {
			...mapActions(globalStore, ['setTheme', 'setIsmobile', 'setLayout', 'setMenuIsCollapse']),
			// 切换应用
			switchModule(id) {
				const menu = this.moduleMenu
				if (menu.length > 0) {
					const menus = menu.filter((item) => item.id === id)[0].children
					if (menus.length > 0) {
						// 将此模块的唯一值加入缓存
						tool.data.set('SNOWY_MENU_MODULE_ID', id)
						// 正儿八百的菜单
						this.menu = this.filterUrl(menus)
						// 然后将其跳转至指定界面，默认始终取排序第一的
						const path = this.traverseChild(this.menu)
						this.$router.push({ path })
					} else {
						this.$message.warning('该模块下无任何菜单')
					}
				}
			},
			// 遍历子集获取一个path
			traverseChild(menu) {
				if (menu[0].children !== undefined) {
					if (menu[0].children.length > 0) {
						return this.traverseChild(menu[0].children)
					} else {
						return menu[0].path
					}
				} else {
					return menu[0].path
				}
			},
			// 当菜单被选中时
			onSelect(obj) {
				this.onSelectTag = true
				const pathLength = obj.keyPath.length
				const path = obj.keyPath[pathLength - 1]
				this.$router.push({ path })
				// 设置选中
				this.selectedKeys = obj.selectedKeys
			},
			onLayoutResize() {
				const clientWidth = document.body.clientWidth
				this.setIsmobile(clientWidth < 992)
			},
			// 路由监听高亮
			showThis() {
				this.pmenu = this.$route.meta.breadcrumb ? this.$route.meta.breadcrumb[0] : {}
				const nextTickMenu = this.filterUrl(this.pmenu.children)
				this.$nextTick(() => {
					let routeMenu = this.filterUrl(this.pmenu.children)
					const active = this.$route.meta.active || this.$route.fullPath
					const parentPathArray = this.getParentKeys(routeMenu, active)
					if (parentPathArray) {
						const parentPath = parentPathArray[parentPathArray.length - 1]
						// 这一串操作下来只为取到最上面的路由的孩子们，最后成为双排菜单的第二排
						const nextMenuTemp = nextTickMenu.filter((item) => item.path === parentPath)[0].children
						if (nextMenuTemp) {
							this.nextMenu = nextTickMenu.filter((item) => item.path === parentPath)[0].children
						}
					}
					this.selectedKeys = new Array(active)
					if (!this.onSelectTag) {
						const pidKey = this.getParentKeys(this.menu, active)
						this.openKeys = pidKey
					} else if (this.sideUniqueOpen) {
						const pidKey = this.getParentKeys(this.menu, active)
						this.openKeys = pidKey
					}
					// 双排菜单下
					if (this.layout === 'doublerow') {
						this.setDoublerowSelectedKey()
					}
				})
			},
			// 双排菜单下点击显示右侧分栏
			showMenu(route) {
				this.pmenu = route
				if (this.pmenu.children) {
					this.nextMenu = this.filterUrl(this.pmenu.children)
				}
				if (!route.children || route.children.length === 0) {
					this.layoutSiderDowbleMenu = false
					this.$router.push({ path: route.path })
				} else {
					this.layoutSiderDowbleMenu = true
				}
				if (this.layout === 'doublerow') {
					this.doublerowSelectedKey = [route.path]
				}
			},
			// 设置双排菜单下的首列默认选中
			setDoublerowSelectedKey() {
				const pidKey = this.getParentKeys(this.menu, this.selectedKeys.toString())
				this.$nextTick(() => {
					const pidKeyArray = []
					for (const key in pidKey) {
						pidKeyArray.push(key)
					}
					if (pidKeyArray.length > 1) {
						this.layoutSiderDowbleMenu = true
					} else {
						this.layoutSiderDowbleMenu = false
					}
				})
				// 设置第一排选中的
				this.menu.forEach((item) => {
					if (pidKey !== undefined) {
						if (pidKey[pidKey.length - 1].toString() === item.path) {
							this.doublerowSelectedKey = [item.path]
						}
					}
				})
			},
			// 菜单展开/关闭的回调
			onOpenChange(keys) {
				if (this.sideUniqueOpen) {
					// 获取最新的
					const openKey = keys[keys.length - 1]
					if (keys.length > 1) {
						// 获取上级
						const pidKey = this.getParentKeys(this.menu, openKey)
						this.openKeys = pidKey
					} else {
						this.openKeys = Array.of(openKey) // new Array(openKey);
					}
				} else {
					this.openKeys = keys
				}
			},
			// 获取上级keys
			getParentKeys(data, val) {
				// 递归父级key
				for (const element of data) {
					if (element.path === val) {
						return [element.path]
					}
					if (element.children) {
						const far = this.getParentKeys(element.children, val)
						if (far) {
							return far.concat(element.path)
						}
					}
				}
			},
			// 转换外部链接的路由
			filterUrl(map) {
				const newMap = []
				// eslint-disable-next-line no-unused-expressions
				map &&
					map.forEach((item) => {
						item.meta = item.meta ? item.meta : {}
						// 处理隐藏
						if (item.meta.hidden) {
							return false
						}
						// 处理http
						if (item.meta.type === 'iframe') {
							item.path = `/i/${item.name}`
						}
						// 递归循环
						if (item.children && item.children.length > 0) {
							item.children = this.filterUrl(item.children)
						}
						newMap.push(item)
					})
				return newMap
			},
			menuIsCollapseClick() {
				this.globalStore.toggleConfig('menuIsCollapse')
			},
			// 退出最大化
			exitMaximize() {
				document.getElementById('app').classList.remove('main-maximize')
			},
			// 切换顶栏颜色
			switchoverTopHanderThemeColor() {
				// 界面顶栏设置颜色
				const header = document.getElementById('snowyHeader')
				this.topHanderThemeColorOpen
					? header.classList.add('snowy-header-primary-color')
					: header.classList.remove('snowy-header-primary-color')
				// 判断是否开启了通栏
				const headerLogin = document.getElementById('snowyHeaderLogo')
				try {
					this.topHanderThemeColorSpread
						? headerLogin.classList.add('snowy-header-logo-primary-color')
						: headerLogin.classList.remove('snowy-header-logo-primary-color')
				} catch (e) {}
				// 如果是双排菜单，吧第二排的也给渲染了
				if (this.layout === 'doublerow') {
					const snowyDoublerowSideTop = document.getElementById('snowyDoublerowSideTop')
					try {
						this.topHanderThemeColorSpread
							? snowyDoublerowSideTop.classList.add('snowy-doublerow-side-top-primary-color')
							: snowyDoublerowSideTop.classList.remove('snowy-doublerow-side-top-primary-color')
					} catch (e) {}
				}
			}
		}
	})
</script>
