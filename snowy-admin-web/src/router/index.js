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
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import systemRouter from './systemRouter'
import { afterEach, beforeEach } from './scrollBehavior'
import whiteListRouters from './whiteList'
import userRoutes from '@/config/route'
import tool from '@/utils/tool'
import { cloneDeep } from 'lodash-es'
import { globalStore } from '@/store'
import { NextLoading } from '@/utils/loading'
import { useMenuStore } from '@/store/menu'

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
	if (!isGetRouter.value) {
		// 初始化菜单加载，代码位置不能变动
		const menuStore = useMenuStore()
		menuStore.refreshMenu()
		isGetRouter.value = true
		next({ ...to, replace: true })
		return false
	}

	const token = tool.data.get('TOKEN')

	// 页面刷新，加载loading
	if (from.path === '/' && to.path !== '/login' && !window.nextLoading && token) {
		NextLoading.start()
	}
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
		next()
		return false
	} else {
		if (token) {
			// 有token的时候才保存登录之前要访问的页面
			tool.data.set('LAST_VIEWS_PATH', to.fullPath)
			// 验证menu或则用户信息是否存在，不存在那么就是被删除或者退出或者清理缓存了
			if (!tool.data.get('MENU') || !tool.data.get('USER_INFO')) {
				tool.data.remove('TOKEN')
				next({
					path: '/login'
				})
				return false
			}
		}
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
	beforeEach(to, from)
	next()
})

router.afterEach((to, from) => {
	afterEach(to, from)
	NProgress.done()
	window.nextLoading && NextLoading.done()
})

router.onError((error) => {
	NProgress.done()
	window.nextLoading && NextLoading.done()
	console.error({
		message: '路由错误',
		description: error.message
	})
})

// 入侵追加自定义方法、对象
router.getMenu = () => {
	const menuStore = useMenuStore()
	let apiMenu = menuStore.menuData.value || tool.data.get('MENU') || []
	// 增加固定路由
	if (apiMenu.length === 0) {
		// 创建默认模块，显示默认菜单
		apiMenu[0] = cloneDeep(userRoutes.module[0])
	}
	const childrenApiMenu = apiMenu[0].children
	apiMenu[0].children = [...(childrenApiMenu ? childrenApiMenu : []), ...userRoutes.menu]
	return filterUrl(apiMenu)
}

const filterUrl = (map) => {
	const newMap = []
	const traverse = (maps) => {
		maps &&
			maps.forEach((item) => {
				item.meta = item.meta ? item.meta : {}
				// 处理iframe
				if (item.meta.type === 'iframe') {
					item.path = `/${item.name}`
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

export default router
