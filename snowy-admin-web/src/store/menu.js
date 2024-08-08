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
		// 获取用户菜单
		const apiMenu = tool.data.get('MENU') || []
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
	// 从路由中移除菜单
	const removeFromRouter = () => {
		const routes = router.getRoutes()
		// 遍历所有路由
		routes.forEach((route) => {
			// 过滤白名单
			if (
				whiteList.filter((e) => e.path === route.path).length > 0 ||
				routesData.filter((e) => e.path === route.path).length > 0
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
		tool.data.set('MENU', menu)
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
			tool.data.set('MENU', data)
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
	return { menuData, loadMenu, addToRouter, refreshMenu, changeRefreshFlag, refreshFlag, fetchMenu, refreshApiMenu }
})
