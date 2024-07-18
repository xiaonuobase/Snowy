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

const request = (url, ...arg) => baseRequest(`/sys/index/` + url, ...arg)
/**
 * 系统首页控制器
 *
 * @author yubaoshan
 * @date 2022-09-22 22:33:20
 */
export default {
	// 添加当前用户日程
	indexScheduleAdd(data) {
		return request('schedule/add', data)
	},
	// 删除日程
	indexScheduleDeleteSchedule(data) {
		return request('schedule/deleteSchedule', data)
	},
	// 获取当前用户日程列表
	indexScheduleList(data) {
		return request('schedule/list', data, 'get')
	},
	// 获取当前用户站内信列表
	indexMessageList(data) {
		return request('message/list', data, 'get')
	},
	// 获取站内信详情
	indexMessageDetail(data) {
		return request('message/detail', data, 'get')
	},
	//站内信全部标记已读
	indexMessageAllMarkRead(data) {
		return request('message/allMessageMarkRead', data)
	},
	// 获取当前用户访问日志列表
	indexVisLogList(data) {
		return request('visLog/list', data, 'get')
	},
	// 获取当前用户操作日志列表
	indexOpLogList(data) {
		return request('opLog/list', data, 'get')
	},
	// 获取基础系统业务数据
	indexBizDataCount(data) {
		return request('bizDataCount', data, 'get')
	},
	// 获取运维一览数据
	indexOpDataCount(data) {
		return request('opDataCount', data, 'get')
	},
	// 获取基础工具数据
	indexToolDataCount(data) {
		return request('toolDataCount', data, 'get')
	}
}
