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
// 导入扩展路由
import clientExpRouter from '@/router/clientExpRouter'
import tool from '@/utils/tool'

const ClientLogin = () => import('@/views/auth/client/login/login.vue')
const ClientFindPwd = () => import('@/views/auth/client/findPwd/index.vue')
const ClientRegister = () => import('@/views/auth/client/register/index.vue')
const ClientFrontIndex = () => import('@/views/front/index.vue')

// 前台基础路由
const routes = [
	{
		path: '/front/client/login',
		component: ClientLogin,
		meta: {
			title: '前台登录'
		}
	},
	{
		path: '/front/client/findPwd',
		component: ClientFindPwd,
		meta: {
			title: '找回密码'
		}
	},
	{
		path: '/front/client/register',
		component: ClientRegister,
		meta: {
			title: '用户注册'
		}
	},
	{
		path: '/front/client/index',
		component: ClientFrontIndex,
		meta: {
			title: '个人主页'
		}
	}
]

// 开放路由列表
const clientOpenRouter = ['/front/client/login', '/front/client/findPwd', '/front/client/register']

/**
 * 验证C端路由访问权限
 * @param {string} path - 路由路径
 * @returns {Object} - 返回验证结果，包含是否通过验证和重定向路径
 */
export const validateClientAccess = (path) => {
	// 如果不是C端路由，直接返回true
	if (!path.includes('/front/client/')) {
		return { valid: true }
	}

	// 如果是开放路由，直接通过
	if (clientOpenRouter.includes(path)) {
		return { valid: true }
	}

	// 检查是否有客户端token
	const clientToken = tool.data.get('CLIENT_TOKEN')
	if (!clientToken) {
		return {
			valid: false,
			redirectPath: '/front/client/login'
		}
	}
	return { valid: true }
}

const exportRoutes = [...routes, ...clientExpRouter]

export default exportRoutes
