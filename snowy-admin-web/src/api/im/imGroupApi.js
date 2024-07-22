import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/im/group/` + url, ...arg)

/**
 * IM-群组Api接口管理器
 *
 * @author ChuZhong
 * @date  2024/07/19 10:42
 **/
export default {
	// 获取IM-群组分页
	imGroupPage(data) {
		return request('page', data, 'get')
	},
	// 提交IM-群组表单 edit为true时为编辑，默认为新增
	imGroupSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除IM-群组
	imGroupDelete(data) {
		return request('delete', data)
	},
	// 获取IM-群组详情
	imGroupDetail(data) {
		return request('detail', data, 'get')
	}
}
