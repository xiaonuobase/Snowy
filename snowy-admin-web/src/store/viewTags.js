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
import { useRouter } from 'vue-router'
import tool from '@/utils/tool'
import { keepAliveStore } from '@/store/keepAlive'

// 标签持久化到 sessionStorage 的 key（刷新后可恢复，关闭标签页/窗口自动清空）
const VIEW_TAGS_STORAGE_KEY = 'SNOWY_VIEW_TAGS'

// 路由对象里的 matched / components 不可序列化，持久化时只保留恢复所需字段。
// 注意：meta.breadcrumb 是扁平化时塞进来的带循环引用的数组（其元素是路由对象本身，
// 又含 meta.breadcrumb），直接 stringify 会抛 "Converting circular structure to JSON"，
// 所以这里必须浅拷贝 meta 并剔除 breadcrumb（标签栏不需要它，跳转后面包屑会从实时路由重新生成）。
const slimRoute = (route) => {
	const meta = route.meta ? { ...route.meta } : {}
	delete meta.breadcrumb
	return {
		path: route.path,
		fullPath: route.fullPath,
		name: route.name,
		query: route.query,
		params: route.params,
		meta
	}
}

export const viewTagsStore = defineStore('viewTags', () => {
	// 定义state
	const viewTags = ref([])
	const router = useRouter()
	// 监听路由变化
	watch(
		() => router.currentRoute.value,
		(newRoute) => {
			const viewTag = viewTags.value.find((item) => item.path === newRoute.path)
			if (viewTag) {
				Object.assign(viewTag, newRoute)
			}
		},
		{ immediate: true }
	)

	// ===== 持久化（参考 soybean admin 的 beforeunload 方案）=====
	// 把当前标签快照写入 sessionStorage
	const cacheViewTags = () => {
		try {
			tool.session.set(VIEW_TAGS_STORAGE_KEY, viewTags.value.map(slimRoute))
		} catch (e) {
			console.warn('viewTags 写入 sessionStorage 失败：', e)
		}
	}
	// 兜底：标签变化时也写一份，保证会话中途数据最新
	watch(viewTags, cacheViewTags, { deep: true })
	// 关键：刷新(F5)/关闭页面前同步写一次，刷新后必定能读到
	window.addEventListener('beforeunload', cacheViewTags)

	// 定义action
	const pushViewTags = (route) => {
		const target = viewTags.value.find((item) => item.path === route.path)
		const isName = route.name
		if (!target && isName) {
			viewTags.value.push(route)
		}
		if (target) {
			updateViewTags(route)
		}
	}
	const removeViewTags = (route) => {
		viewTags.value.forEach((item, index) => {
			if (item.path === route.path) {
				viewTags.value.splice(index, 1)
			}
		})
	}
	const updateViewTags = (route) => {
		viewTags.value.forEach((item, index) => {
			if (item.fullPath === route.fullPath) {
				viewTags.value[index] = { ...route, ...item }
			}
		})
	}
	// 更新或删除视图标签
	const updateOrRemoveViewTags = (routes) => {
		if (routes && routes.length > 0) {
			viewTags.value.forEach((item, index) => {
				// 使用path进行比较，忽略参数部分
				const target = routes.find((route) => route.path === item.path)
				if (!target) {
					// 路由不存在，删除
					viewTags.value.splice(index, 1)
				} else {
					// 路由存在，更新
					updateViewTags(target)
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

	// ===== 恢复：刷新后从 sessionStorage 还原标签 + keepAlive 列表 =====
	// 注意：刷新时动态路由是异步注册的（refreshApiMenu 未 await），执行到这里时
	// router.getRoutes() 往往还没有动态菜单路由，所以这里不能用路由表校验。
	// 失效标签（权限/菜单变更）由 layout 内 updateOrRemoveViewTags 在路由加载后统一清理。
	const restoreViewTags = () => {
		const cached = tool.session.get(VIEW_TAGS_STORAGE_KEY)
		if (!Array.isArray(cached) || cached.length === 0) return
		const kStore = keepAliveStore()
		cached.forEach((route) => {
			if (!route || !route.name || route.meta?.fullpage) return
			if (viewTags.value.find((item) => item.path === route.path)) return
			viewTags.value.push(route)
			if (route.meta?.keepLive !== false) {
				kStore.pushKeepLive(route.name)
			}
		})
	}

	const clearViewTags = () => {
		viewTags.value = []
		// 同步清掉 sessionStorage，避免同一标签页内换号后残留
		tool.session.remove(VIEW_TAGS_STORAGE_KEY)
	}

	return {
		viewTags,
		pushViewTags,
		removeViewTags,
		updateViewTags,
		updateViewTagsTitle,
		clearViewTags,
		updateOrRemoveViewTags,
		restoreViewTags,
		cacheViewTags
	}
})
