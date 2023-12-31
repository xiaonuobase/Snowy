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

const request = (url, ...arg) => baseRequest(`/dev/email/` + url, ...arg)
/**
 * 邮件
 *
 * @author yubaoshan
 * @date 2022-09-22 22:33:20
 */
export default {
	// 获取邮件分页
	emailPage(data) {
		return request('page', data, 'get')
	},
	// 发送邮件——本地TXT
	emailSendLocalTxt(data) {
		return request('sendLocalTxt', data)
	},
	// 发送邮件——本地HTML
	emailSendLocalHtml(data) {
		return request('sendLocalHtml', data)
	},
	// 发送邮件——阿里云TXT
	emailSendAliyunTxt(data) {
		return request('sendAliyunTxt', data)
	},
	// 发送邮件——阿里云HTML
	emailSendAliyunHtml(data) {
		return request('sendAliyunHtml', data)
	},
	// 发送邮件——阿里云TMP
	emailSendAliyunTmp(data) {
		return request('sendAliyunTmp', data)
	},
	// 发送邮件——腾讯云TXT
	emailSendTencentTxt(data) {
		return request('sendTencentTxt', data)
	},
	// 发送邮件——腾讯云HTML
	emailSendTencentHtml(data) {
		return request('sendTencentHtml', data)
	},
	// 发送邮件——腾讯云TMP
	emailSendTencentTmp(data) {
		return request('sendTencentTmp', data)
	},
	// 删除邮件
	emailDelete(data) {
		return request('delete', data)
	},
	// 获取邮件详情
	emailDetail(data) {
		return request('detail', data, 'get')
	}
}
