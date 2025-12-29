import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/biz/group/` + url, ...arg)

/**
 * 用户组Api接口管理器
 *
 * @author yubaoshan
 * @date  2024/12/24 03:33
 **/
export default {
	// 获取用户组分页
	groupPage(data) {
		return request('page', data, 'get')
	},
	// 提交用户组表单 edit为true时为编辑，默认为新增
	groupSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除用户组
	groupDelete(data) {
		return request('delete', data)
	},
	// 获取用户组详情
	groupDetail(data) {
		return request('detail', data, 'get')
	},
	// 获取用户组下的用户
	groupOwnUser(data) {
		return request('ownUser', data, 'get')
	},
	// 获取机构树
	groupOrgTreeSelector(data) {
		return request('orgTreeSelector', data, 'get')
	},
	// 获取用户选择器
	groupUserSelector(data) {
		return request('userSelector', data, 'get')
	},
	// 给用户组授权用户
	groupGrantUser(data) {
		return request('grantUser', data)
	}
}
