const request = (prop, url, ...arg) => {
	return prop(`/im/member/` + url, ...arg)
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
