import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/gen/config/` + url, ...arg)

export default {
	// 获取代码生成详情配置列表
	configList(data) {
		return request('list', data, 'get')
	},
	// 删除代码生成详情配置
	configDelete(data) {
		return request('delete', data)
	},
	// 获取代码生成详情配置详情
	configDetail(data) {
		return request('detail', data, 'get')
	},
	// 批量编辑代码生成详细配置
	configEditBatch(data) {
		return request('editBatch', data)
	}
}
