import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/im/member/` + url, ...arg)

/**
 * IM-群组成员Api接口管理器
 *
 * @author ChuZhong
 * @date  2024/07/19 10:42
 **/
export default {
	// 获取IM-群组成员分页
	imGroupMemberPage(data) {
		return request('page', data, 'get')
	},
	// 提交IM-群组成员表单 edit为true时为编辑，默认为新增
	imGroupMemberSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除IM-群组成员
	imGroupMemberDelete(data) {
		return request('delete', data)
	},
	// 获取IM-群组成员详情
	imGroupMemberDetail(data) {
		return request('detail', data, 'get')
	},
	// 在群组中禁言某个用户
	imGroupMemberMute(data) {
		return request('silence', data)
	},
	// 在群组中解除禁言某个用户
	imGroupMemberUnMute(data) {
		return request('cancelSilence', data)
	},
	//获取当前用户被禁言的群组
	imGroupMemberMuteList(data) {
		return request('getSilenceGroup', data, 'get')
	}
}
