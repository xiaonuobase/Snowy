import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/im/message/` + url, ...arg)

/**
 * IM-消息Api接口管理器
 *
 * @author ChuZhong
 * @date  2024/07/19 10:41
 **/
export default {
	// 获取IM-消息分页
	imMessagePage(data) {
		return request('page', data, 'get')
	},
	// 提交IM-消息表单 edit为true时为编辑，默认为新增
	imMessageSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除IM-消息
	imMessageDelete(data) {
		return request('delete', data)
	},
	// 获取IM-消息详情
	imMessageDetail(data) {
		return request('detail', data, 'get')
	},
	// 通过当前用户查询跟所有用户的聊天记录
	queryChatRecord(data) {
		return request('queryChatRecord', data, 'get')
	},
	// 查询当前用户和指定用户的聊天记录-分页
	queryChatRecordWithUser(data) {
		return request('queryChatRecordWithUser', data, 'get')
	},
	// 将消息设置为已读
	setMessageRead(data) {
		return request('setRead', data)
	}
}
