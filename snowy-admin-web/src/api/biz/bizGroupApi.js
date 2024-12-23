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
	bizGroupPage(data) {
		return request('page', data, 'get')
	},
	// 提交用户组表单 edit为true时为编辑，默认为新增
	bizGroupSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除用户组
	bizGroupDelete(data) {
		return request('delete', data)
	},
	// 获取用户组详情
	bizGroupDetail(data) {
		return request('detail', data, 'get')
	},
	// 获取用户组下的用户
	bizGroupOwnUser(data) {
		return request('ownUser', data, 'get')
	},
	// 获取机构树
	bizGroupOrgTreeSelector(data) {
		return request('orgTreeSelector', data, 'get')
	},
	// 获取用户选择器
	bizGroupUserSelector(data) {
		return request('userSelector', data, 'get')
	},
	// 给用户组授权用户
	bizGroupGrantUser(data) {
		return request('grantUser', data)
	}
}
