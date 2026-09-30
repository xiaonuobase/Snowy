import router from '@/router'
import tool from '@/utils/tool'
import { message } from 'ant-design-vue'
import clientLoginApi from '@/api/auth/client/clientLoginApi'

export const afterLogin = async (loginToken) => {
	tool.data.set('CLIENT_TOKEN', loginToken)
	const param = {
		token: loginToken
	}
	// 获取用户信息失败时回滚登录态，否则残留的 CLIENT_TOKEN 会让刷新后直接进入C端页面
	const clientLoginUserInfo = await clientLoginApi.clientGetLoginUser(param).catch((err) => {
		tool.data.remove('CLIENT_TOKEN')
		tool.data.remove('CLIENT_USER_INFO')
		message.error('登录成功，但系统初始化失败，请重新登录')
		throw err
	})
	tool.data.set('CLIENT_USER_INFO', clientLoginUserInfo)
	let indexMenu = '/front/client/index'
	message.success('登录成功')
	await router.replace({
		path: indexMenu
	})
}
