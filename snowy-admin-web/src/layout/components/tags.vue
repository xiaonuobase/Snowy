<template>
	<div class="snowy-tags">
		<xn-context-menu
			class="right-menu"
			:target="contextMenuTarget"
			:show="contextMenuVisible"
			@update:show="(show) => (contextMenuVisible = show)"
			@get-context-menu="handleTabContextMenu"
		>
			<div class="right-menu-item" @click="refreshTab">
				<reload-outlined class="snowy-header-tags-right" />
				<div class="pl-3 snowy-header-tags-right-font">刷新</div>
			</div>

			<div class="right-menu-item" @click="closeTabs">
				<close-outlined class="snowy-header-tags-right" />
				<div class="pl-3 snowy-header-tags-right-font">关闭</div>
			</div>

			<div class="right-menu-item" @click="closeOtherTabs">
				<close-outlined class="snowy-header-tags-right" />
				<div class="pl-3 snowy-header-tags-right-font">关闭其他</div>
			</div>

			<div class="right-menu-item" @click="maximize">
				<expand-outlined class="snowy-header-tags-right" />
				<div class="pl-3 snowy-header-tags-right-font">最大化</div>
			</div>
			<div class="right-menu-item" @click="openWindow">
				<select-outlined class="snowy-header-tags-right" />
				<div class="pl-3 snowy-header-tags-right-font">新窗口</div>
			</div>
		</xn-context-menu>
		<a-tabs
			v-model:activeKey="activeKey"
			type="editable-card"
			:class="[{ 'snowy-radius': roundedCornerStyleOpen }, 'snowy-admin-tabs']"
			:animated="!roundedCornerStyleOpen"
			hide-add
			ref="tabs"
			@edit="onTabRemove"
			@tabClick="onTabClick"
			@mouseup="onTabUp"
		>
			<template #leftExtra>
				<div class="snowy-admin-tabs-arrow" @click="scrollLeft">
					<left-outlined />
				</div>
			</template>
			<template #rightExtra>
				<div class="snowy-admin-tabs-arrow" @click="scrollRight">
					<right-outlined />
				</div>
			</template>

			<a-tab-pane v-for="tag in tagList" :key="tag.fullPath" :closable="!tag.meta.affix">
				<template #tab>
					<span :key="tag.meta.key">
						{{ tag.meta.title }}
					</span>
				</template>
			</a-tab-pane>
		</a-tabs>
	</div>
</template>

<script setup>
	import XnContextMenu from '@/components/XnContextMenu/index.vue'
	import { globalStore, iframeStore, keepAliveStore, viewTagsStore } from '@/store'
	import routerUtil from '@/utils/routerUtil'
	import { useRoute, useRouter } from 'vue-router'
	import { watch } from 'vue'

	const route = useRoute()
	const router = useRouter()
	const store = globalStore()
	const kStore = keepAliveStore()
	const iStore = iframeStore()
	const vStore = viewTagsStore()

	const activeKey = ref()
	const maxTabs = ref(20)
	const contextMenuTarget = ref(null)
	const contextMenuVisible = ref(false)
	const currentContextMenuTabIndex = ref(0)

	const viewTags = computed(() => {
		return vStore.viewTags
	})

	const layoutTagsOpen = computed(() => {
		return store.layoutTagsOpen
	})
	const roundedCornerStyleOpen = computed(() => {
		return store.roundedCornerStyleOpen
	})

	const tagList = computed(() => {
		return viewTags.value
	})
	watch(route, (to) => {
		addViewTags(to)
		activeKey.value = to.fullPath
	})
	watch(layoutTagsOpen, () => {
		// closeOtherCacheTabs()
	})
	onMounted(() => {
		const tabNavList = document.querySelector('.ant-tabs-nav-list')
		if (tabNavList) {
			contextMenuTarget.value = tabNavList
		}
	})
	// 增加tag
	const addViewTags = (to) => {
		activeKey.value = to.fullPath
		if (to.name && !to.meta.fullpage) {
			vStore.pushViewTags(to)
			kStore.pushKeepLive(to.name)
		}
		if (tagList.value.length - 1 > maxTabs.value) {
			const firstTag = tagList.value[1]
			vStore.removeViewTags(firstTag)
		}
	}

	// 查找树
	const treeFind = (tree, func) => {
		for (const data of tree) {
			if (func(data)) return data
			if (data.children) {
				const res = treeFind(data.children, func)
				if (res) return res
			}
		}
		return null
	}

	const getCurrentTag = () => {
		return tagList.value[currentContextMenuTabIndex.value]
	}

	// 高亮tag
	const isActive = (to) => {
		return to.path === route.path
	}
	// 关闭tag
	const closeSelectedTag = (tag, autoPushLatestView = true) => {
		vStore.removeViewTags(tag)
		iStore.removeIframeList(tag)
		kStore.removeKeepLive(tag.name)
		if (autoPushLatestView && isActive(tag)) {
			const latestView = tagList.value.slice(-1)[0]
			if (latestView) {
				router.push(latestView)
			} else {
				router.push('/')
			}
		}
	}
	// TAB 刷新
	const refreshTab = () => {
		contextMenuVisible.value = false
		const nowTag = getCurrentTag()
		// 判断是否当前路由，否的话跳转
		// eslint-disable-next-line eqeqeq
		if (route.fullPath !== nowTag.fullPath) {
			router.push({
				path: nowTag.fullPath,
				query: nowTag.query
			})
		}
		iStore.refreshIframe(nowTag)
		setTimeout(() => {
			kStore.removeKeepLive(nowTag.name)
			kStore.setRouteShow(false)
			nextTick(() => {
				kStore.pushKeepLive(nowTag.name)
				kStore.setRouteShow(true)
			})
		}, 0)
	}

	// TAB 关闭
	const closeTabs = () => {
		contextMenuVisible.value = false
		const nowTag = getCurrentTag()
		if (!nowTag.meta.affix) {
			closeSelectedTag(nowTag)
		}
	}
	// TAB 关闭其他
	const closeOtherTabs = () => {
		contextMenuVisible.value = false
		const nowTag = getCurrentTag()
		// 判断是否当前路由，否的话跳转
		// eslint-disable-next-line eqeqeq
		if (route.fullPath !== nowTag.fullPath) {
			router.push({
				path: nowTag.fullPath,
				query: nowTag.query
			})
		}
		const tags = [...tagList.value]
		tags.forEach((tag) => {
			// eslint-disable-next-line eqeqeq
			if ((tag.meta && tag.meta.affix) || nowTag.fullPath === tag.fullPath) {
				return true
			} else {
				closeSelectedTag(tag, false)
			}
		})
	}

	// 多标签功能关闭时关闭被缓存的标签
	const closeOtherCacheTabs = () => {
		const tags = [...tagList]
		tags.forEach((tag) => {
			closeSelectedTag(tag, false)
		})
	}
	// TAB 最大化（包括标签栏）
	const maximize = () => {
		contextMenuVisible.value = false
		const nowTag = getCurrentTag()
		// 判断是否当前路由，否的话跳转
		// eslint-disable-next-line eqeqeq
		if (route.fullPath !== nowTag.fullPath) {
			router.push({
				path: nowTag.fullPath,
				query: nowTag.query
			})
		}
		document.getElementById('app').classList.add('main-maximize')
	}
	// 新窗口打开
	const openWindow = () => {
		contextMenuVisible.value = false
		const nowTag = getCurrentTag()
		const url = nowTag.path || '/'
		if (!nowTag.meta.affix) {
			closeSelectedTag(nowTag)
		}
		window.open(url)
	}

	const handleTabContextMenu = (evt) => {
		evt.preventDefault()
		let target = evt.target
		// 修复关闭时出现"使用了错误的类型或对象"的问题
		while (!target.classList.contains('ant-tabs-tab')) {
			if (target.classList.contains('ant-tabs')) {
				currentContextMenuTabIndex.value = 0
				return
			}
			target = target.parentNode
		}
		const tabList = document.querySelectorAll('.ant-tabs-nav-list .ant-tabs-tab')
		currentContextMenuTabIndex.value = Array.from(tabList).findIndex((tab) => tab === target)
	}

	const module = router.getMenu()
	const indexMenu = routerUtil.getIndexMenu(module).path
	// eslint-disable-next-line eqeqeq
	const dashboardRoute = treeFind(module, (node) => node.path === indexMenu)
	if (dashboardRoute) {
		dashboardRoute.fullPath = dashboardRoute.path
		addViewTags(dashboardRoute)
		addViewTags(route)
	}

	const onTabRemove = (tabKey, action) => {
		if (action === 'remove') {
			const tag = tagList.value.find((tag) => tag.fullPath === tabKey)
			closeSelectedTag(tag)
		}
	}
	const onTabClick = (tab) => {
		router.push(tab)
	}
	// 处理鼠标放开事件
	const onTabUp = (e) => {
		// 鼠标中键
		if (e.which === 2) {
			handleTabContextMenu(e)
			closeTabs()
		}
	}
	const getTabWrapEl = () => {
		return document.querySelector('.ant-tabs-nav-wrap')
	}
	const scrollLeft = () => {
		const wrapEl = getTabWrapEl()
		if (wrapEl) {
			const event = new WheelEvent('wheel', { deltaX: 0, deltaY: -100 })
			wrapEl.dispatchEvent(event)
		}
	}
	const scrollRight = () => {
		const wrapEl = getTabWrapEl()
		if (wrapEl) {
			const event = new WheelEvent('wheel', { deltaX: 0, deltaY: 100 })
			wrapEl.dispatchEvent(event)
		}
	}
</script>
<style lang="less">
	.snowy-admin-tabs {
		overflow: hidden; // 新增
		&.ant-tabs {
			z-index: 99;
			.ant-tabs-nav {
				margin-bottom: 0;
				.ant-tabs-extra-content {
					display: flex;
				}
				.ant-tabs-nav-wrap {
					.ant-tabs-ink-bar {
						visibility: visible;
					}
					.ant-tabs-tab-with-remove {
						padding-right: 4px;
					}
					.ant-tabs-tab {
						background: none;
						height: 40px;
						line-height: 40px;
						transition:
							background-color 0.3s,
							color 0.3s;
						padding: 0 16px;
						border-radius: 0;
						border: none;
						margin: 0;
						.ant-tabs-tab-remove {
							margin: 0;
							padding: 0 5px;
						}
					}
					.ant-tabs-tab-active {
						background: var(--primary-1);
					}
				}
			}
			.snowy-admin-tabs-drop,
			.snowy-admin-tabs-arrow,
			.ant-tabs-nav-operations .ant-tabs-nav-more {
				padding: 0;
				width: 40px;
				height: 40px;
				line-height: 40px;
				text-align: center;
				cursor: pointer;
				.anticon {
					font-size: 12px;
					vertical-align: -1px;
				}
			}
		}
	}
	.right-menu {
		position: fixed;
		background: var(--tag-background);
		z-index: 999;
		border: 1px solid #eee;
		box-shadow: 0 0.5em 1em 0 rgb(0 0 0 / 10%);
		border-radius: 1px;
	}
	.snowy-tags {
		.right-menu-item {
			display: flex;
			align-items: center;
			text-align: center;
			padding: 10px 10px;
			color: #333;
			cursor: pointer;
			&:hover {
				background: var(--primary-1);
			}
		}
	}
	.snowy-tags {
		height: 40px;
		background: var(--snowy-background-color);
	}
	.snowy-tags ul {
		display: flex;
		overflow: hidden;
		padding-left: 0;
	}
	.snowy-tags li {
		cursor: pointer;
		display: inline-block;
		float: left;
		line-height: 39.5px;
		position: relative;
		flex-shrink: 0;
	}
	.snowy-tags li::after {
		content: ' ';
		width: 1px;
		height: 100%;
		position: absolute;
		right: 0px;
		background-image: linear-gradient(#fff, #e6e6e6);
	}
	.snowy-tags li a {
		padding: 0 10px;
		width: 100%;
		height: 100%;
		text-decoration: none;
		display: flex;
		align-items: center;
	}
	.snowy-tags li i {
		margin-left: 10px;
		border-radius: 3px;
		width: 18px;
		height: 18px;
		display: flex;
		align-items: center;
		justify-content: center;
	}
	.snowy-tags li i:hover {
		background: rgba(0, 0, 0, 0.2);
		color: @body-background;
	}
	.snowy-tags li:hover {
		background: @body-background;
	}
	.snowy-tags li.active {
		background: @primary-color;
	}
	.snowy-tags li.active a {
		color: var(--font-color);
	}
	.snowy-tags li.sortable-ghost {
		opacity: 0;
	}
	.snowy-header-tags-right {
		margin-right: 5px;
		color: var(--font-color);
	}
	.snowy-header-tags-right-font {
		color: var(--font-color);
	}
	.snowy-radius .ant-tabs-tab-active {
		position: relative;
		z-index: 1;
		border-radius: 10px 10px 0 0 !important;
		box-shadow:
			12px 15px 0 0 var(--primary-1),
			-12px 15px 0 0 var(--primary-1);
	}
	.snowy-radius .ant-tabs-tab-active::before {
		content: '';
		position: absolute;
		left: -13px;
		bottom: 1px;
		width: 13px;
		height: 40px;
		background: var(--primary-radius);
		border-radius: 0 0 20px 0;
	}
	.snowy-radius .ant-tabs-tab-active::after {
		content: '';
		position: absolute;
		right: -13px;
		bottom: 1px;
		width: 13px;
		height: 40px;
		background: var(--primary-radius);
		border-radius: 0 0 0 20px;
	}

	.snowy-radius .ant-tabs-ink-bar {
		visibility: hidden !important;
	}
</style>
