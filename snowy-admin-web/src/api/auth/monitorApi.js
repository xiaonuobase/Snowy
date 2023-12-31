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

const request = (url, ...arg) => baseRequest(`/auth/` + url, ...arg)
/**
 * 绘画
 *
 * @author yubaoshan
 * @date 2022-09-22 22:33:20
 */
export default {
	// 会话统计
	monitorAnalysis(data) {
		return request('session/analysis', data, 'get')
	},
	// 获取会话分页
	monitorBPage(data) {
		return request('session/b/page', data, 'get')
	},
	// 获取会话分页
	monitorCPage(data) {
		return request('session/c/page', data, 'get')
	},
	// 强退B端session
	monitorBExit(data) {
		return request('session/b/exit', data)
	},
	// 强退C端session
	monitorCExit(data) {
		return request('session/c/exit', data)
	},
	// 强退B端token
	monitorTokenBExit(data) {
		return request('token/b/exit', data)
	},
	// 强退C端token
	monitorTokenCExit(data) {
		return request('token/c/exit', data)
	}
}
