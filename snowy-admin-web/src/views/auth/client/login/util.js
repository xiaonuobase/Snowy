import router from '@/router'
import tool from '@/utils/tool'
import { message } from 'ant-design-vue'
import clientLoginApi from '@/api/auth/client/clientLoginApi'

export const afterLogin = async (loginToken) => {
	tool.data.set('CLIENT_TOKEN', loginToken)
	const param = {
		token: loginToken
	}
	const clientLoginUserInfo = await clientLoginApi.clientGetLoginUser(param)
	tool.data.set('CLIENT_USER_INFO', clientLoginUserInfo)
	let indexMenu = '/front/client/index'
	message.success('登录成功')
	await router.replace({
		path: indexMenu
	})
}
