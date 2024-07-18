import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/biz/index/` + url, ...arg)

/**
 * 业务主页Api接口管理器
 *
 * @author yubaoshan
 * @date  2024/07/11 14:46
 **/
export default {
	// 获取轮播图列表
	bizIndexSlideshowList(data) {
		return request('slideshow/list', data, 'get')
	},
	// 获取通知公告列表
	bizIndexNoticeList(data) {
		return request('notice/list', data, 'get')
	},
	// 获取通知公告详情
	bizIndexNoticeDetail(data) {
		return request('notice/detail', data, 'get')
	}
}
