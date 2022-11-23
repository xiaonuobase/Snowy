import loginApi from '@/api/auth/loginApi'
import userCenterApi from '@/api/sys/userCenterApi'
import dictApi from '@/api/dev/dictApi'
import router from '@/router'
import tool from '@/utils/tool'
import { message } from 'ant-design-vue'

export const afterLogin = async (loginToken) => {
	tool.data.set('TOKEN', loginToken)
	// 获取登录的用户信息
	const loginUser = await loginApi.getLoginUser()
	tool.data.set('USER_INFO', loginUser)

	// 获取用户的菜单
	const menu = await userCenterApi.userLoginMenu()
	const indexMenu = menu[0].children[0].path
	tool.data.set('MENU', menu)
	// 重置系统默认应用
	tool.data.set('SNOWY_MENU_MODULE_ID', menu[0].id)
	message.success('登录成功')
	router.replace({
		path: indexMenu
	})
	dictApi.dictTree().then((data) => {
		// 设置字典到store中
		tool.data.set('DICT_TYPE_TREE_DATA', data)
	})
}
