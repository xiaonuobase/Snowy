/**
 *  Copyright [2022] [https://www.xiaonuo.vip]
 *	Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：
 *	1.请不要删除和修改根目录下的LICENSE文件。
 *	2.请不要删除和修改Snowy源码头部的版权声明。
 *	3.本项目代码可免费商业使用，商业使用请保留源码和相关描述文件的项目出处，作者声明等。
 *	4.分发源码时候，请注明软件出处 https://www.xiaonuo.vip
 *	5.不可二次分发开源参与同类竞品，如有想法可联系团队xiaonuobase@qq.com商议合作。
 *	6.若您的项目无法满足以上几点，需要更多功能代码，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
import { defineStore } from 'pinia'
import tool from '@/utils/tool'
import { cloneDeep } from 'lodash-es'
import userRoutes from '@/config/route'
import { searchStore } from '@/store/search'
import router from '@/router'
import userCenterApi from '@/api/sys/userCenterApi'
import whiteList from '@/router/whiteList'
import routesData from '@/router/systemRouter'

// findPwd和login路由组件已静态加载，此处不在进行异步加载
const modules = import.meta.glob([
	'/src/views/**/**.vue',
	'!/src/views/auth/findPwd/**.vue',
	'!/src/views/auth/login/**.vue'
])
export const useMenuStore = defineStore('menuStore', () => {
	const menuData = ref([])
	const refreshFlag = ref(false)
	// 改变刷新标志
	const changeRefreshFlag = (flag) => {
		refreshFlag.value = flag
	}
	// 加载菜单
	const loadMenu = () => {
		// 获取用户菜单（缓存中可能是裁剪前的菜单，这里再裁剪一次，有变化则回写缓存）
		const cacheMenu = tool.data.get('MENU')
		const cacheMenuJson = JSON.stringify(cacheMenu)
		const apiMenu = pruneMenu(cacheMenu) || []
		if (Array.isArray(cacheMenu) && JSON.stringify(apiMenu) !== cacheMenuJson) {
			tool.data.set('MENU', apiMenu)
		}
		if (apiMenu.length === 0) {
			// 创建默认模块，显示默认菜单
			apiMenu[0] = cloneDeep(userRoutes.module[0])
		}
		const childrenApiMenu = apiMenu[0].children
		apiMenu[0].children = [...(childrenApiMenu ? childrenApiMenu : []), ...userRoutes.menu]
		let menuRouter = filterAsyncRouter(apiMenu)
		menuRouter = flatAsyncRoutes(menuRouter)
		menuData.value = menuRouter
		// 初始化搜索
		const search_store = searchStore()
		search_store.init(menuRouter)
	}
	// 过滤异步路由
	const filterAsyncRouter = (routerMap) => {
		const accessedRouters = []
		routerMap.forEach((item) => {
			item.meta = item.meta ? item.meta : {}
			// 处理外部链接特殊路由
			if (item.meta.type === 'iframe') {
				item.meta.url = item.path
				item.path = `/${item.name}`
			}
			// MAP转路由对象
			const route = {
				path: item.path,
				name: item.name,
				meta: item.meta,
				redirect: item.redirect,
				children: item.children ? filterAsyncRouter(item.children) : null,
				component: loadComponent(item.component)
			}
			accessedRouters.push(route)
		})
		return accessedRouters
	}
	// 将异步路由扁平化
	const flatAsyncRoutes = (routes, breadcrumb = []) => {
		const res = []
		routes.forEach((route) => {
			const tmp = { ...route }
			if (tmp.children) {
				const childrenBreadcrumb = [...breadcrumb]
				childrenBreadcrumb.push(route)
				const tmpRoute = { ...route }
				tmpRoute.meta.breadcrumb = childrenBreadcrumb
				delete tmpRoute.children
				res.push(tmpRoute)
				const childrenRoutes = flatAsyncRoutes(tmp.children, childrenBreadcrumb)
				childrenRoutes.map((item) => {
					res.push(item)
				})
			} else {
				const tmpBreadcrumb = [...breadcrumb]
				tmpBreadcrumb.push(tmp)
				tmp.meta.breadcrumb = tmpBreadcrumb
				res.push(tmp)
			}
		})
		return res
	}
	// 动态加载组件
	const loadComponent = (component) => {
		if (component) {
			if (component.includes('/')) {
				return modules[`/src/views/${component}.vue`]
			}
			return modules[`/src/views/${component}/index.vue`]
		} else {
			return () => import(/* @vite-ignore */ `/src/layout/other/empty.vue`)
		}
	}
	// 判断当前前端工程是否存在该页面组件（取值规则与loadComponent一致）
	const hasComponent = (component) => {
		if (!component) {
			return false
		}
		return !!modules[component.includes('/') ? `/src/views/${component}.vue` : `/src/views/${component}/index.vue`]
	}
	// 裁剪掉当前前端工程没有对应页面的菜单，裁剪后为空的目录、模块一并去掉
	const pruneMenuTree = (menuList) => {
		return menuList.filter((item) => {
			const type = item.meta ? item.meta.type : undefined
			if (Array.isArray(item.children)) {
				item.children = pruneMenuTree(item.children)
			}
			const hasChildren = Array.isArray(item.children) && item.children.length > 0
			if (type === 'menu') {
				// 菜单下还有保留下来的子菜单时不裁剪，避免连带去掉有页面的子菜单
				return hasComponent(item.component) || hasChildren
			}
			if (type === 'module' || type === 'catalog') {
				return hasChildren
			}
			return true
		})
	}
	// 查找第一个有页面且未隐藏的菜单
	const findFirstMenu = (menuList) => {
		for (const item of menuList || []) {
			if (!item.meta || item.meta.hidden === true) {
				continue
			}
			if (item.meta.type === 'menu' && hasComponent(item.component)) {
				return item
			}
			const child = findFirstMenu(item.children)
			if (child) {
				return child
			}
		}
		return null
	}
	// 是否存在固定首页标记
	const hasAffix = (menuList) => {
		return (menuList || []).some((item) => (item.meta && item.meta.affix === true) || hasAffix(item.children))
	}
	const pruneMenu = (menuList) => {
		if (!Array.isArray(menuList)) {
			return menuList
		}
		const result = pruneMenuTree(menuList)
		// 后端标记的固定首页如果被裁剪掉了，把裁剪后第一个模块下的第一个菜单标记为固定首页
		if (result.length > 0 && !hasAffix(result)) {
			const firstMenu = findFirstMenu(result[0].children)
			if (firstMenu) {
				firstMenu.meta.affix = true
			}
		}
		return result
	}
	// 从路由中移除菜单
	const removeFromRouter = () => {
		const routes = router.getRoutes()
		// 遍历所有路由
		routes.forEach((route) => {
			// 过滤白名单
			if (
				whiteList.filter((e) => e.path === route.path).length > 0 ||
				routesData.filter((e) => e.path === route.path).length > 0 ||
				route.meta.routeType === 'plugin'
			) {
				return
			}
			if (route.name && route.name !== 'layout') {
				router.removeRoute(route.name)
			}
		})
	}
	// 获取用户菜单（通过API重新初始化菜单，用于界面实时响应）
	const fetchMenu = async () => {
		const menu = await userCenterApi.userLoginMenu()
		tool.data.set('MENU', pruneMenu(menu))
		refreshMenu()
	}
	// 刷新菜单（非API刷新，用于路由守卫内使用）
	const refreshMenu = () => {
		loadMenu()
		removeFromRouter()
		addToRouter()
		changeRefreshFlag(true)
	}
	// 通过API刷新菜单（仅在layout的onMounted内使用，浏览器刷新只刷新一次）
	const refreshApiMenu = () => {
		userCenterApi.userLoginMenu().then((data) => {
			tool.data.set('MENU', pruneMenu(data))
			nextTick(() => {
				refreshMenu()
			})
		})
	}
	// 将菜单添加到路由
	const addToRouter = () => {
		menuData.value.forEach((item) => {
			router.addRoute('layout', item)
		})
	}
	return {
		menuData,
		loadMenu,
		addToRouter,
		refreshMenu,
		changeRefreshFlag,
		refreshFlag,
		fetchMenu,
		refreshApiMenu,
		pruneMenu
	}
})
