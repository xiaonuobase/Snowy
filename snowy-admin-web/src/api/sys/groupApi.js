import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/sys/group/` + url, ...arg)

/**
 * 用户组Api接口管理器
 *
 * @author yubaoshan
 * @date  2024/12/21 01:25
 **/
export default {
	// 获取用户组分页
	sysGroupPage(data) {
		return request('page', data, 'get')
	},
	// 提交用户组表单 edit为true时为编辑，默认为新增
	sysGroupSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除用户组
	sysGroupDelete(data) {
		return request('delete', data)
	},
	// 获取用户组详情
	sysGroupDetail(data) {
		return request('detail', data, 'get')
	},
	// 获取用户组下的用户
	sysGroupOwnUser(data) {
		return request('ownUser', data, 'get')
	},
	// 获取机构树
	sysGroupOrgTreeSelector(data) {
		return request('orgTreeSelector', data, 'get')
	},
	// 获取用户选择器
	sysGroupUserSelector(data) {
		return request('userSelector', data, 'get')
	},
	// 给用户组授权用户
	sysGroupGrantUser(data) {
		return request('grantUser', data)
	}
}
