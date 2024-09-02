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
import { prefixUrl } from '../utils/request.js'

const request = (prop, url, ...arg) => {
	return prop(prefixUrl(`/im/member/` + url), ...arg)
}
/**
 * IM-群组成员Api接口管理器
 *
 * @author chengchuanyao
 * @date  2024/07/19 10:42
 **/
export default {
	// 获取IM-群组成员分页
	imGroupMemberPage(prop, data) {
		return request(prop, 'page', data, 'get')
	},
	// 提交IM-群组成员表单 edit为true时为编辑，默认为新增
	imGroupMemberSubmitForm(prop, data, edit = false) {
		return request(prop, edit ? 'edit' : 'add', data)
	},
	// 删除IM-群组成员
	imGroupMemberDelete(prop, data) {
		return request(prop, 'delete', data)
	},
	// 在群组中禁言某个用户
	imGroupMemberMute(prop, data) {
		return request(prop, 'silence', data)
	},
	// 在群组中解除禁言某个用户
	imGroupMemberUnMute(prop, data) {
		return request(prop, 'cancelSilence', data)
	},
	//获取当前用户被禁言的群组
	imGroupMemberMuteList(prop, data) {
		return request(prop, 'getSilenceGroup', data, 'get')
	}
}
