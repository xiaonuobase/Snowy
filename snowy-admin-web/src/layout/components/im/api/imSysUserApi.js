import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/im/user/` + url, ...arg)

/**
 * IM-用户Api接口管理器
 *
 * @author chengchuanyao
 * @date  2024/07/19 10:42
 **/
export default {
	// 获取用户分页列表
	imUserList(data) {
		return request('list', data, 'get')
	}
}
