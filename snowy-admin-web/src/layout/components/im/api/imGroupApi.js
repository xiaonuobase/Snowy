const request = (prop,url, ...arg) => {
	return prop(`/im/group/` + url, ...arg)
}
/**
 * IM-群组Api接口管理器
 *
 * @author chengchuanyao
 * @date  2024/07/19 10:42
 **/
export default {
	// 提交IM-群组表单 edit为true时为编辑，默认为新增
	imGroupSubmitForm(prop,data, edit = false) {
		return request(prop,edit ? 'edit' : 'add', data)
	},
	// 删除IM-群组
	imGroupDelete(prop,data) {
		return request(prop,'delete', data)
	},
	// 获取IM-群组详情
	imGroupDetail(prop,data) {
		return request(prop,'detail', data, 'get')
	},
	// 获取当前用户的群组
	imGroupListByUser(prop,data) {
		return request(prop,'listByUser', data, 'get')
	}
}
