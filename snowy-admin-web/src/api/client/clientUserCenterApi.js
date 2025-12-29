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

const request = (url, ...arg) => baseRequest(`/client/userCenter/` + url, ...arg)
/**
 * C端用户个人控制器
 *
 * @author xuyuxiang
 * @date 2022-04-22 09:34:00
 */
export default {
	// 获取图片验证码
	clientUserGetPicCaptcha(data) {
		return request('getPicCaptcha', data, 'get')
	},
	// 找回密码获取手机验证码
	clientUserFindPasswordGetPhoneValidCode(data) {
		return request('findPasswordGetPhoneValidCode', data, 'get')
	},
	// 找回密码获取邮箱验证码
	clientUserFindPasswordGetEmailValidCode(data) {
		return request('findPasswordGetEmailValidCode', data, 'get')
	},
	// 通过手机号找回用户密码
	clientUserFindPasswordByPhone(data) {
		return request('findPasswordByPhone', data)
	},
	// 通过邮箱找回用户密码
	clientUserFindPasswordByEmail(data) {
		return request('findPasswordByEmail', data)
	},
	// 修改密码获取手机验证码
	clientUserUpdatePasswordGetPhoneValidCode(data) {
		return request('updatePasswordGetPhoneValidCode', data, 'get')
	},
	// 修改密码获取邮箱验证码
	clientUserUpdatePasswordGetEmailValidCode(data) {
		return request('updatePasswordGetEmailValidCode', data, 'get')
	},
	// 通过验证旧密码修改用户密码
	clientUserUpdatePasswordByOld(data) {
		return request('updatePasswordByOld', data)
	},
	// 通过验证手机号修改用户密码
	clientUserUpdatePasswordByPhone(data) {
		return request('updatePasswordByPhone', data)
	},
	// 通过验证邮箱修改用户密码
	clientUserUpdatePasswordByEmail(data) {
		return request('updatePasswordByEmail', data)
	},
	// 绑定手机号获取手机验证码
	clientUserBindPhoneGetPhoneValidCode(data) {
		return request('bindPhoneGetPhoneValidCode', data, 'get')
	},
	// 修改绑定手机号获取手机验证码
	clientUserUpdateBindPhoneGetPhoneValidCode(data) {
		return request('updateBindPhoneGetPhoneValidCode', data, 'get')
	},
	// 绑定手机号
	clientUserBindPhone(data) {
		return request('bindPhone', data)
	},
	// 绑定邮箱获取邮箱验证码
	clientUserBindEmailGetEmailValidCode(data) {
		return request('bindEmailGetEmailValidCode', data, 'get')
	},
	// 修改绑定邮箱获取邮箱验证码
	clientUserUpdateBindEmailGetEmailValidCode(data) {
		return request('updateBindEmailGetEmailValidCode', data, 'get')
	},
	// 绑定邮箱
	clientUserBindEmail(data) {
		return request('bindEmail', data)
	},
	// 修改用户头像
	clientUserUpdateAvatar(data) {
		return request('updateAvatar', data)
	},
	// 修改用户签名图片
	clientUserUpdateSignature(data) {
		return request('updateSignature', data)
	},
	// 编辑个人信息
	clientUserUpdateUserInfo(data) {
		return request('updateUserInfo', data)
	},
	// 根据id获取头像
	clientUserGetAvatarById(data) {
		return request('getAvatarById', data, 'get')
	},
	// 判断当前用户是否需要绑定手机号
	clientUserIsUserNeedBindPhone(data) {
		return request('isUserNeedBindPhone', data, 'get')
	},
	// 判断当前用户是否需要绑定邮箱
	clientUserIsUserNeedBindEmail(data) {
		return request('isUserNeedBindEmail', data, 'get')
	},
	// 判断当前用户密码是否过期
	clientUserIsUserPasswordExpired(data) {
		return request('isUserPasswordExpired', data, 'get')
	}
}
