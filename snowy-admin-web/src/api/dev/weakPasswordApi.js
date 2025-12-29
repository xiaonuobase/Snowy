import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/dev/weakPassword/` + url, ...arg)

/**
 * 弱密码库Api接口管理器
 *
 * @author yubaoshan
 * @date  2025/05/31 01:45
 **/
export default {
	// 获取弱密码库分页
	weakPasswordPage(data) {
		return request('page', data, 'get')
	},
	// 提交弱密码库表单 edit为true时为编辑，默认为新增
	weakPasswordSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除弱密码库
	weakPasswordDelete(data) {
		return request('delete', data)
	},
	// 获取弱密码库详情
	weakPasswordDetail(data) {
		return request('detail', data, 'get')
	}
}
