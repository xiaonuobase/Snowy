const request = (prop, url, ...arg) => {
	return prop(`/im/message/` + url, ...arg)
}

/**
 * IM-消息Api接口管理器
 *
 * @author chengchuanyao
 * @date  2024/07/19 10:41
 **/
export default {
	// 通过当前用户查询跟所有用户的聊天记录
	queryChatRecord(prop, data) {
		return request(prop, 'queryChatRecord', data, 'get')
	},
	// 查询当前用户和指定用户的聊天记录-分页
	queryChatRecordWithUser(prop, data) {
		return request(prop, 'queryChatRecordWithUser', data, 'get')
	},
	// 将消息设置为已读
	setMessageRead(prop, data) {
		return request(prop, 'setRead', data)
	},
	// 撤回消息
	recallMessage(prop, data) {
		return request(prop, 'recall', data)
	}
}
