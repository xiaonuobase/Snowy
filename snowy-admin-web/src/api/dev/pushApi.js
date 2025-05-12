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

const request = (url, ...arg) => baseRequest(`/dev/push/` + url, ...arg)
/**
 * 消息推送
 *
 * @author yubaoshan
 * @date 2022-09-22 22:33:20
 */
export default {
	// 获取消息推送分页
	pushPage(data) {
		return request('page', data, 'get')
	},
	// 删除消息推送
	pushDelete(data) {
		return request('delete', data)
	},
	// 获取消息推送详情
	pushDetail(data) {
		return request('detail', data, 'get')
	},
	// 动态推送消息
	pushDynamicText(data) {
		return request('pushDynamicText', data)
	},

	// 推送飞书TEXT消息
	pushFeiShuText(data) {
		return request('pushFeiShuText', data)
	},

	// 推送钉钉TEXT消息
	pushDingTalkText(data) {
		return request('pushDingTalkText', data)
	},
	// 推送消息——钉钉MARKDOWN
	pushDingTalkMarkdown(data) {
		return request('pushDingTalkMarkdown', data)
	},
	// 推送消息——钉钉LINK
	pushDingTalkLink(data) {
		return request('pushDingTalkLink', data)
	},

	// 推送消息——企业微信TXT
	pushWorkWechatText(data) {
		return request('pushWorkWechatText', data)
	},
	// 推送消息——企业微信MARKDOWN
	pushWorkWechatMarkdown(data) {
		return request('pushWorkWechatMarkdown', data)
	},
	// 推送消息——企业微信NEWS
	pushWorkWechatNews(data) {
		return request('pushWorkWechatNews', data)
	}
}
