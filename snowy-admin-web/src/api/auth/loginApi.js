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

const request = (url, ...arg) => baseRequest(`/auth/b/` + url, ...arg)
/**
 * 登录
 *
 * @author yubaoshan
 * @date 2022-09-22 22:33:20
 */
export default {
	// B端获取图片验证码
	getPicCaptcha(data) {
		return request('getPicCaptcha', data, 'get')
	},
	// B端获取手机验证码
	getPhoneValidCode(data) {
		return request('getPhoneValidCode', data, 'get')
	},
	// B端账号密码登录
	login(data) {
		return request('doLogin', data, 'post', false)
	},
	// B端手机验证码登录
	loginByPhone(data) {
		return request('doLoginByPhone', data, 'post', false)
	},
	// 退出
	logout(data) {
		return request('doLogout', data, 'get')
	},
	// 获取用户信息
	getLoginUser(data) {
		return request('getLoginUser', data, 'get')
	}
}
