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
import config from '@/config'
import tool from '@/utils/tool'
import routerUtil from '@/utils/routerUtil'

import Layout from '@/layout/index.vue'
import Login from '@/views/auth/login/login.vue'
import Findpwd from '@/views/auth/findPwd/index.vue'
import Callback from '@/views/auth/login/callback.vue'

// 系统路由
const routes = [
	{
		name: 'layout',
		path: '/',
		component: Layout,
		redirect: tool.data.get('MENU') ? routerUtil.getIndexMenu(tool.data.get('MENU')).path : config.DASHBOARD_URL,
		children: []
	},
	{
		path: '/login',
		component: Login,
		meta: {
			title: '登录'
		}
	},
	{
		path: '/findpwd',
		component: Findpwd,
		meta: {
			title: '找回密码'
		}
	},
	{
		path: '/callback',
		component: Callback,
		meta: {
			title: '三方登录'
		}
	}
]

export default routes
