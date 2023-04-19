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
import { createRouter, createWebHistory } from 'vue-router'
import { notification } from 'ant-design-vue'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import systemRouter from './systemRouter'
import { afterEach, beforeEach } from './scrollBehavior'
import whiteListRouters from './whiteList'
import userRoutes from '@/config/route'
import tool from '@/utils/tool'
import { cloneDeep } from 'lodash-es'
const modules = import.meta.glob('/src/views/**/**.vue')
import { globalStore, searchStore } from '@/store'

// 进度条配置
NProgress.configure({ showSpinner: false, speed: 500 })

// 系统特殊路由
const routes_404 = [
	{
		path: '/:pathMatch(.*)*',
		hidden: true,
		component: () => import('@/layout/other/404.vue')
	}
]
// 系统路由
const routes = [...systemRouter, ...whiteListRouters, ...routes_404]

const router = createRouter({
	history: createWebHistory(),
	routes
})

// 设置标题
// document.title = sysBaseConfig.SNOWY_SYS_NAME

// 判断是否已加载过动态/静态路由
const isGetRouter = ref(false)

// 白名单校验
const exportWhiteListFromRouter = (router) => {
	const res = []
	for (const item of router) res.push(item.path)
	return res
}
const whiteList = exportWhiteListFromRouter(whiteListRouters)

router.beforeEach(async (to, from, next) => {
	NProgress.start()
	const store = globalStore()

	const sysBaseConfig = tool.data.get('SNOWY_SYS_BASE_CONFIG') || store.sysBaseConfig
	// 动态标题
	document.title = to.meta.title
		? `${to.meta.title} - ${sysBaseConfig.SNOWY_SYS_NAME}`
		: `${sysBaseConfig.SNOWY_SYS_NAME}`

	// 过滤白名单
	if (whiteList.includes(to.path)) {
		next()
		// NProgress.done()
		return false
	}

	const token = tool.data.get('TOKEN')
	if (to.path === '/login') {
		// 当用户输入了login路由，将其跳转首页即可
		if (token) {
			next({
				path: '/'
			})
			return false
		}
		// 删除路由(替换当前layout路由)
		router.addRoute(routes[0])
		isGetRouter.value = false
		next()
		return false
	} else {
		// 这里需要使用 localStorage 保存登录之前要访问的页面
		tool.data.set('LAST_VIEWS_PATH', to.fullPath)
	}
	if (!token) {
		next({
			path: '/login'
		})
		return false
	}
	// 整页路由处理
	if (to.meta.fullpage) {
		to.matched = [to.matched[to.matched.length - 1]]
	}
	// 加载动态/静态路由
	if (!isGetRouter.value) {
		const apiMenu = tool.data.get('MENU') || []
		if (apiMenu.length === 0) {
			// 创建默认模块，显示默认菜单
			apiMenu[0] = cloneDeep(userRoutes.module[0])
			const userMenu = userRoutes.menu
			const childrenApiMenu = apiMenu[0].children
			apiMenu[0].children = [...userMenu, ...childrenApiMenu]
		}
		let menuRouter = filterAsyncRouter(apiMenu)
		menuRouter = flatAsyncRoutes(menuRouter)
		menuRouter.forEach((item) => {
			router.addRoute('layout', item)
		})

		const search_store = searchStore()
		search_store.init(menuRouter)
		isGetRouter.value = true
		next({ ...to, replace: true })
		return false
	}
	beforeEach(to, from)
	next()
})

router.afterEach((to, from) => {
	afterEach(to, from)
	NProgress.done()
})

router.onError((error) => {
	NProgress.done()
	notification.error({
		message: '路由错误',
		description: error.message
	})
})

// 入侵追加自定义方法、对象
router.getMenu = () => {
	let apiMenu = tool.data.get('MENU') || []
	// 增加固定路由
	if (apiMenu.length === 0) {
		// 创建默认模块，显示默认菜单
		apiMenu[0] = cloneDeep(userRoutes.module[0])
		const userMenu = userRoutes.menu
		const childrenApiMenu = apiMenu[0].children
		apiMenu[0].children = [...userMenu, ...childrenApiMenu]
	}
	return apiMenu
}

// 转换
const filterAsyncRouter = (routerMap) => {
	const accessedRouters = []
	routerMap.forEach((item) => {
		item.meta = item.meta ? item.meta : {}
		// 处理外部链接特殊路由
		if (item.meta.type === 'iframe') {
			item.meta.url = item.path
			item.path = `/i/${item.name}`
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

// 路由扁平化
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

export default router
