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
import { defineStore } from 'pinia'

export const viewTagsStore = defineStore('viewTags', () => {
	// 定义state
	const viewTags = ref([])

	// 定义action
	const pushViewTags = (route) => {
		const target = viewTags.value.find((item) => item.path === route.path)
		const isName = route.name
		if (!target && isName) {
			viewTags.value.push(route)
		}
		if (target) {
			viewTags.value.forEach((item, index) => {
				if (item.path === route.path) {
					viewTags.value[index] = { ...route, ...item }
					// Object.assign(item, route)
				}
			})
		}
	}
	const removeViewTags = (route) => {
		viewTags.value.forEach((item, index) => {
			if (item.fullPath === route.fullPath) {
				viewTags.value.splice(index, 1)
			}
		})
	}
	const updateViewTags = (route) => {
		viewTags.value.forEach((item, index) => {
			if (item.fullPath === route.fullPath) {
				viewTags.value[index] = { ...route, ...item }
				// Object.assign(item, route)
			}
		})
	}
	// 更新或删除视图标签
	const updateOrRemoveViewTags = (routes) => {
		if (routes && routes.length > 0) {
			viewTags.value.forEach((item, index) => {
				const target = routes.find((route) => route.path === item.fullPath)
				if (!target) {
					// 路由不存在，删除
					viewTags.value.splice(index, 1)
				} else {
					// 路由存在，更新
					viewTags.value = viewTags.value.map((item) => {
						if (item.fullPath === target.path) {
							return { ...item, meta: target.meta }
						}
						return item
					})
				}
			})
		}
	}
	const updateViewTagsTitle = (title = '') => {
		const nowFullPath = location.hash.substring(1)
		viewTags.value.forEach((item) => {
			if (item.fullPath === nowFullPath) {
				item.meta.key = Date.now()
				item.meta.title = title
			}
		})
	}
	const clearViewTags = () => {
		viewTags.value = []
	}

	return {
		viewTags,
		pushViewTags,
		removeViewTags,
		updateViewTags,
		updateViewTagsTitle,
		clearViewTags,
		updateOrRemoveViewTags
	}
})
