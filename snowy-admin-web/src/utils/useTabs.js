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
import { nextTick } from 'vue'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import router from '@/router'
import { iframeStore, keepAliveStore, viewTagsStore } from '@/store'
export default {
	// 刷新标签
	refresh() {
		NProgress.start()
		const keepAlive = keepAliveStore()
		const route = router.currentRoute.value
		keepAlive.removeKeepLive(route.name)
		keepAlive.setRouteShow(false)
		nextTick(() => {
			keepAlive.pushKeepLive(route.name)
			keepAlive.setRouteShow(true)
			NProgress.done()
		})
	},
	// 关闭标签
	close(tag) {
		const route = tag || router.currentRoute.value
		const store = viewTagsStore()
		store.removeViewTags(route)
		iframeStore().removeIframeList(route)
		keepAliveStore().removeKeepLive(route.name)
		const tagList = store.viewTags
		const latestView = tagList.slice(-1)[0]
		if (latestView) {
			router.push(latestView)
		} else {
			router.push('/')
		}
	},
	// 关闭标签后处理
	closeNext(next) {
		const route = router.currentRoute.value
		const store = viewTagsStore()
		store.removeViewTags(route)
		iframeStore().removeIframeList(route)
		keepAliveStore().removeKeepLive(route.name)
		if (next) {
			const tagList = store.viewTags
			next(tagList)
		}
	},
	// 关闭其他
	closeOther() {
		const route = router.currentRoute.value
		const store = viewTagsStore()
		const tagList = [...store.viewTags]
		tagList.forEach((tag) => {
			// eslint-disable-next-line prettier/prettier
			if ((tag.meta && tag.meta.affix) || route.fullPath == tag.fullPath) {
				return true
			} else {
				this.close(tag)
			}
		})
	},
	// 设置标题
	setTitle(title) {
		viewTagsStore().updateViewTagsTitle(title)
	}
}
