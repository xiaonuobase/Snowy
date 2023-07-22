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
				<div class="pl-3">刷新</div>
			</div>

			<div class="right-menu-item" @click="closeTabs">
				<close-outlined class="snowy-header-tags-right" />
				<div class="pl-3">关闭</div>
			</div>

			<div class="right-menu-item" @click="closeOtherTabs">
				<close-outlined class="snowy-header-tags-right" />
				<div class="pl-3">关闭其他标签</div>
			</div>

			<div class="right-menu-item" @click="maximize">
				<expand-outlined class="snowy-header-tags-right" />
				<div class="pl-3">最大化</div>
			</div>
			<div class="right-menu-item" @click="openWindow">
				<select-outlined class="snowy-header-tags-right" />
				<div class="pl-3">新窗口打开</div>
			</div>
		</xn-context-menu>
		<a-tabs
			v-model:activeKey="activeKey"
			type="editable-card"
			class="snowy-admin-tabs"
			hide-add
			ref="tabs"
			@edit="onTabRemove"
			@tabClick="onTabClick"
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

			<a-tab-pane v-for="tag in tagList" :key="tag.fullPath" :tab="tag.meta.title" :closable="!tag.meta.affix">
			</a-tab-pane>
		</a-tabs>
	</div>
</template>

<script>
	import tool from '@/utils/tool'
	import XnContextMenu from '@/components/XnContextMenu/index.vue'
	import { globalStore, iframeStore, keepAliveStore, viewTagsStore } from '@/store'
	import { mapState, mapActions } from 'pinia'

	export default {
		name: 'Tags',
		components: { XnContextMenu },
		props: {},
		data() {
			return {
				// tagList: [],
				activeKey: this.$route.fullPath,
				maxTabs: 20,
				contextMenuTarget: null,
				contextMenuVisible: false,
				currentContextMenuTabIndex: 0
			}
		},
		computed: {
			...mapState(viewTagsStore, ['viewTags']),
			...mapState(globalStore, ['layoutTagsOpen']),
			tagList() {
				return this.viewTags
			}
		},
		watch: {
			$route(to) {
				this.addViewTags(to)
			},
			layoutTagsOpen() {
				this.closeOtherCacheTabs()
			}
		},
		created() {
			const module = this.$router.getMenu()
			const indexMenu = tool.data.get('MENU') ? tool.data.get('MENU')[0].children[0].path : this.$CONFIG.DASHBOARD_URL
			// eslint-disable-next-line eqeqeq
			const dashboardRoute = this.treeFind(module, (node) => node.path === indexMenu)
			if (dashboardRoute) {
				dashboardRoute.fullPath = dashboardRoute.path
				this.addViewTags(dashboardRoute)
				this.addViewTags(this.$route)
			}
		},
		mounted() {
			const tabNavList = document.querySelector('.ant-tabs-nav-list')
			if (tabNavList) {
				this.contextMenuTarget = tabNavList
			}
		},
		methods: {
			...mapActions(viewTagsStore, ['addViewTags', 'pushViewTags', 'removeViewTags']),
			...mapActions(iframeStore, ['addIframe', 'removeIframeList', 'refreshIframe']),
			...mapActions(keepAliveStore, ['pushKeepLive', 'removeKeepLive', 'setRouteShow']),
			handleTabContextMenu(evt) {
				evt.preventDefault()
				let target = evt.target
				if (target.classList.contains('ant-tabs-tab-btn')) {
					target = target.parentNode
				}
				const tabList = document.querySelectorAll('.ant-tabs-nav-list .ant-tabs-tab')
				this.currentContextMenuTabIndex = Array.from(tabList).findIndex((tab) => tab === target)
			},
			onTabClick(tab) {
				this.$router.push(tab)
			},
			getCurrentTag() {
				return this.tagList[this.currentContextMenuTabIndex]
			},
			onTabRemove(tabKey, action) {
				if (action === 'remove') {
					const tag = this.tagList.find((tag) => tag.fullPath === tabKey)
					this.closeSelectedTag(tag)
				}
			},
			getTabWrapEl() {
				return document.querySelector('.ant-tabs-nav-wrap')
			},
			scrollLeft() {
				const wrapEl = this.getTabWrapEl()
				if (wrapEl) {
					const event = new WheelEvent('wheel', { deltaX: 0, deltaY: -100 })
					wrapEl.dispatchEvent(event)
				}
			},
			scrollRight() {
				const wrapEl = this.getTabWrapEl()
				if (wrapEl) {
					const event = new WheelEvent('wheel', { deltaX: 0, deltaY: 100 })
					wrapEl.dispatchEvent(event)
				}
			},
			// 查找树
			treeFind(tree, func) {
				for (const data of tree) {
					if (func(data)) return data
					if (data.children) {
						const res = this.treeFind(data.children, func)
						if (res) return res
					}
				}
				return null
			},
			// 增加tag
			addViewTags(route) {
				this.activeKey = route.fullPath
				if (route.name && !route.meta.fullpage) {
					this.pushViewTags(route)
					this.pushKeepLive(route.name)
				}
				if (this.tagList.length - 1 > this.maxTabs) {
					const firstTag = this.tagList[1]
					this.removeViewTags(firstTag)
				}
			},
			// 高亮tag
			isActive(route) {
				return route.fullPath === this.$route.fullPath
			},
			// 关闭tag
			closeSelectedTag(tag, autoPushLatestView = true) {
				this.removeViewTags(tag)
				this.removeIframeList(tag)
				this.removeKeepLive(tag.name)
				if (autoPushLatestView && this.isActive(tag)) {
					const latestView = this.tagList.slice(-1)[0]
					if (latestView) {
						this.$router.push(latestView)
					} else {
						this.$router.push('/')
					}
				}
			},
			// TAB 刷新
			refreshTab() {
				this.contextMenuVisible = false
				const nowTag = this.getCurrentTag()
				// 判断是否当前路由，否的话跳转
				// eslint-disable-next-line eqeqeq
				if (this.$route.fullPath !== nowTag.fullPath) {
					this.$router.push({
						path: nowTag.fullPath,
						query: nowTag.query
					})
				}
				this.refreshIframe(nowTag)
				setTimeout(() => {
					this.removeKeepLive(nowTag.name)
					this.setRouteShow(false)
					this.$nextTick(() => {
						this.pushKeepLive(nowTag.name)
						this.setRouteShow(true)
					})
				}, 0)
			},
			// TAB 关闭
			closeTabs() {
				this.contextMenuVisible = false
				const nowTag = this.getCurrentTag()
				if (!nowTag.meta.affix) {
					this.closeSelectedTag(nowTag)
				}
			},
			// TAB 关闭其他
			closeOtherTabs() {
				this.contextMenuVisible = false
				const nowTag = this.getCurrentTag()
				// 判断是否当前路由，否的话跳转
				// eslint-disable-next-line eqeqeq
				if (this.$route.fullPath !== nowTag.fullPath) {
					this.$router.push({
						path: nowTag.fullPath,
						query: nowTag.query
					})
				}
				const tags = [...this.tagList]
				tags.forEach((tag) => {
					// eslint-disable-next-line eqeqeq
					if ((tag.meta && tag.meta.affix) || nowTag.fullPath === tag.fullPath) {
						return true
					} else {
						this.closeSelectedTag(tag, false)
					}
				})
			},
			// 多标签功能关闭时关闭被缓存的标签
			closeOtherCacheTabs() {
				const tags = [...this.tagList]
				tags.forEach((tag) => {
					this.closeSelectedTag(tag, false)
				})
			},
			// TAB 最大化（包括标签栏）
			maximize() {
				this.contextMenuVisible = false
				const nowTag = this.getCurrentTag()
				// 判断是否当前路由，否的话跳转
				// eslint-disable-next-line eqeqeq
				if (this.$route.fullPath !== nowTag.fullPath) {
					this.$router.push({
						path: nowTag.fullPath,
						query: nowTag.query
					})
				}
				document.getElementById('app').classList.add('main-maximize')
			},
			// 新窗口打开
			openWindow() {
				this.contextMenuVisible = false
				const nowTag = this.getCurrentTag()
				const url = nowTag.href || '/'
				if (!nowTag.meta.affix) {
					this.closeSelectedTag(nowTag)
				}
				window.open(url)
			}
		}
	}
</script>

<style lang="less">
	.snowy-admin-tabs {
		&.ant-tabs {
			background: var(--component-background);
			box-shadow: var(--header-light-shadow);
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
						transition: background-color 0.3s, color 0.3s;
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
		background: #fff;
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
			padding: 10px 20px;
			color: #333;
			cursor: pointer;
			&:hover {
				background: var(--primary-1);
			}
		}
	}
</style>
