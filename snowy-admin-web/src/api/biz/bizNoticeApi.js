import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/biz/notice/` + url, ...arg)

/**
 * 通知公告Api接口管理器
 *
 * @author yubaoshan
 * @date  2024/07/11 14:46
 **/
export default {
	// 获取通知公告分页
	bizNoticePage(data) {
		return request('page', data, 'get')
	},
	// 提交通知公告表单 edit为true时为编辑，默认为新增
	bizNoticeSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除通知公告
	bizNoticeDelete(data) {
		return request('delete', data)
	},
	// 获取通知公告详情
	bizNoticeDetail(data) {
		return request('detail', data, 'get')
	},
	// 禁用通知公告
	bizNoticeDisableStatus(data) {
		return request('disableStatus', data)
	},
	// 启用通知公告
	bizNoticeEnableStatus(data) {
		return request('enableStatus', data)
	}
}
