import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/sys/user/transfer/` + url, ...arg)

/**
 * 权限转移Api接口管理器
 *
 * @author yubaoshan
 * @date 2026/05/13
 */
export default {
	// 获取用户可转移资源列表
	transferResourceList(data) {
		return request('resourceList', data, 'get')
	},
	// 获取资源明细列表
	transferResourceDetail(data) {
		return request('resourceDetail', data, 'get')
	},
	// 执行权限转移
	transferExecute(data) {
		return request('execute', data)
	},
	// 获取机构树选择器
	transferOrgTreeSelector(data) {
		return request('orgTreeSelector', data, 'get')
	},
	// 获取用户选择器
	transferUserSelector(data) {
		return request('userSelector', data, 'get')
	}
}
