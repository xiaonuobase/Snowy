import dictApi from '@/api/dev/dictApi'
import userCenterApi from '@/api/sys/userCenterApi'
import router from '@/router'
import tool from '@/utils/tool'
import { message } from 'ant-design-vue'
import routerUtil from '@/utils/routerUtil'
import { useMenuStore } from '@/store/menu'
import { useUserStore } from '@/store/user'

export const afterLogin = async (loginToken) => {
	const route = router.currentRoute.value
	const menuStore = useMenuStore()
	tool.data.set('TOKEN', loginToken)
	// 初始化用户信息
	await useUserStore().initUserInfo()

	// 获取用户的菜单
	await menuStore.fetchMenu()
	const menu = tool.data.get('MENU')
	let indexMenu = routerUtil.getIndexMenu(menu).path

	// 重置系统默认应用
	tool.data.set('SNOWY_MENU_MODULE_ID', menu[0].id)

	if (tool.data.get('LAST_VIEWS_PATH')) {
		// 如果有缓存，将其登录跳转到最后访问的路由
		indexMenu = tool.data.get('LAST_VIEWS_PATH')
	}
	// 如果存在退出后换新账号登录，进行重新匹配，匹配无果则默认首页
	if (menu) {
		let routerTag = 0
		menu.forEach((item) => {
			if (item.children) {
				if (JSON.stringify(item.children).indexOf(indexMenu) > -1) {
					routerTag++
				}
			}
		})
		if (routerTag === 0) {
			// 取首页
			indexMenu = routerUtil.getIndexMenu(menu).path
		}
	}
	dictApi.dictTree().then((data) => {
		// 设置字典到store中
		tool.data.set('DICT_TYPE_TREE_DATA', data)
	})

	// 此处判断是否存在跳转页面，如存在则跳转，否则走原来逻辑
	if(route.query.redirect_uri) {
		// 跳转到回调页
		message.success('登录成功，即将跳转...')
		setTimeout(function () {
			window.location.href = route.query.redirect_uri;
		}, 500);
	} else if(route.query.redirect) {
		// 跳转到回调页
		message.success('登录成功，即将跳转...')
		setTimeout(function () {
			window.location.href = route.query.redirect;
		}, 500);
	} else if(route.query.back) {
		// 跳转到回调页
		message.success('登录成功，即将跳转...')
		setTimeout(function () {
			window.location.href = route.query.back;
		}, 500);
	} else {
		message.success('登录成功，即将跳转...')
		setTimeout(function () {
			// 跳转到首页
			router.replace({
				path: indexMenu
			}).then(() => {
				// 判断用户密码是否过期
				userCenterApi.userCenterIsUserPasswordExpired().then((expired) => {
					if (expired) {
						message.warning('当前登录密码已过期，请及时更改！')
					}
				})
			})
		}, 500);
	}
}
