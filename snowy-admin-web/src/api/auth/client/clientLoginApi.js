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
import { baseRequest } from '@/utils/request'
import tool from '@/utils/tool'

const request = (url, ...arg) => baseRequest(`/auth/c/` + url, ...arg)
/**
 * 登录
 *
 * @author yubaoshan
 * @date 2025-05-31 23:55:10
 */
export default {
	// C端获取图片验证码
	clientGetPicCaptcha(data) {
		return request('getPicCaptcha', data, 'get')
	},
	// C端获取手机验证码
	clientGetPhoneValidCode(data) {
		return request('getPhoneValidCode', data, 'get')
	},
	// C端获取邮箱验证码
	clientGetEmailValidCode(data) {
		return request('getEmailValidCode', data, 'get')
	},
	// C端账号密码登录
	clientLogin(data) {
		return request('doLogin', data, 'post', false)
	},
	// C端手机验证码登录
	clientLoginByPhone(data) {
		return request('doLoginByPhone', data, 'post', false)
	},
	// C端邮箱验证码登录
	clientLoginByEmail(data) {
		return request('doLoginByEmail', data, 'post', false)
	},
	// 退出
	clientLogout(data) {
		return request('doLogout', data, 'get')
	},
	// 获取用户信息
	clientGetLoginUser(data) {
		return request('getLoginUser', data, 'get')
	},
	// C端注册
	clientRegister(data) {
		return request('register', data, 'post')
	}
}
