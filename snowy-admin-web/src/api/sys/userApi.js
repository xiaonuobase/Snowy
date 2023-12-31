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

const request = (url, ...arg) => baseRequest(`/sys/user/` + url, ...arg)
/**
 * 用户接口api
 *
 * @author yubaoshan
 * @date 2022-09-22 22:33:20
 */
export default {
	// 获取用户分页
	userPage(data) {
		return request('page', data, 'get')
	},
	// 提交表单 edit为true时为编辑，默认为新增
	submitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除用户
	userDelete(data) {
		return request('delete', data)
	},
	// 获取用户详情
	userDetail(data) {
		return request('detail', data, 'get')
	},
	// 禁用用户
	userDisableUser(data) {
		return request('disableUser', data)
	},
	// 启用用户
	userEnableUser(data) {
		return request('enableUser', data)
	},
	// 重置用户密码
	userResetPassword(data) {
		return request('resetPassword', data)
	},
	// 获取组织选择器
	userOrgTreeSelector(data) {
		return request('orgTreeSelector', data, 'get')
	},
	// 获取职位选择器
	userPositionSelector(data) {
		return request('positionSelector', data, 'get')
	},
	// 获取角色选择器
	userRoleSelector(data) {
		return request('roleSelector', data, 'get')
	},
	// 获取用户选择器
	userSelector(data) {
		return request('userSelector', data, 'get')
	},
	// 用户拥有角色
	userOwnRole(data) {
		return request('ownRole', data, 'get')
	},
	// 给用户授权角色
	grantRole(data) {
		return request('grantRole', data)
	},
	// 获取用户拥有资源
	userOwnResource(data) {
		return request('ownResource', data, 'get')
	},
	// 给用户授权资源
	userGrantResource(data) {
		return request('grantResource', data)
	},
	// 获取用户拥有权限
	userOwnPermission(data) {
		return request('ownPermission', data, 'get')
	},
	// 给用户授权权限
	userGrantPermission(data) {
		return request('grantPermission', data)
	},
	// 下载用户导入模板
	userDownloadImportUserTemplate(data) {
		return request('downloadImportUserTemplate', data, 'get', {
			responseType: 'blob'
		})
	},
	// 用户导入
	userImport(data) {
		return request('import', data)
	},
	// 用户导出
	userExport(data) {
		return request('export', data, 'get', {
			responseType: 'blob'
		})
	},
	// 导出用户个人信息
	userExportUserInfo(data) {
		return request('exportUserInfo', data, 'get', {
			responseType: 'blob'
		})
	}
}
