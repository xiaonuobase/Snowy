import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/dev/slideshow/` + url, ...arg)

/**
 * 轮播图Api接口管理器
 *
 * @author yubaoshan
 * @date  2024/07/13 00:31
 **/
export default {
	// 获取轮播图分页
	devSlideshowPage(data) {
		return request('page', data, 'get')
	},
	// 提交轮播图表单 edit为true时为编辑，默认为新增
	devSlideshowSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除轮播图
	devSlideshowDelete(data) {
		return request('delete', data)
	},
	// 禁用轮播图
	devSlideshowDisableStatus(data) {
		return request('disableStatus', data)
	},
	// 启用轮播图
	devSlideshowEnableStatus(data) {
		return request('enableStatus', data)
	}
}
