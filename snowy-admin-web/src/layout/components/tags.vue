<template>
	<div class="snowy-tags">
		<a-tabs
			v-model:activeKey="activeKey"
			type="editable-card"
			class="snowy-admin-tabs"
			hide-add
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

				<a-dropdown>
					<div class="snowy-admin-tabs-drop">
						<DownOutlined />
					</div>
					<template #overlay>
						<a-menu>
							<a-menu-item>
								<div class="layout-items-center" @click="refreshTab">
									<reload-outlined class="snowy-header-tags-right" />
									<div class="pl-3">刷新</div>
								</div>
							</a-menu-item>
							<a-menu-item>
								<div class="layout-items-center" @click="closeOtherTabs">
									<close-outlined class="snowy-header-tags-right" />
									<div class="pl-3">关闭其他标签</div>
								</div>
							</a-menu-item>
							<a-menu-item>
								<div class="layout-items-center" @click="maximize">
									<expand-outlined class="snowy-header-tags-right" />
									<div class="pl-3">最大化</div>
								</div>
							</a-menu-item>
							<a-menu-item>
								<div class="layout-items-center" @click="openWindow">
									<select-outlined class="snowy-header-tags-right" />
									<div class="pl-3">在新的窗口中打开</div>
								</div>
							</a-menu-item>
						</a-menu>
					</template>
				</a-dropdown>
			</template>
			<a-tab-pane v-for="tag in tagList" :key="tag.fullPath" :tab="tag.meta.title" :closable="!tag.meta.affix">
			</a-tab-pane>
		</a-tabs>
	</div>
</template>

<script>
	import { LeftOutlined, RightOutlined } from '@ant-design/icons-vue'
	import Sortable from 'sortablejs'
	import tool from '@/utils/tool'

	export default {
		name: 'Tags',
		props: {},
		data() {
			return {
				tagList: this.$store.state.viewTags.viewTags,
				activeKey: this.$route.fullPath
			}
		},
		watch: {
			$route(e) {
				this.addViewTags(e)
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
		methods: {
			onTabClick(tab) {
				this.$router.push(tab)
			},
			getCurrentTag() {
				return this.tagList.find((tag) => tag.fullPath === this.activeKey)
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
					this.$store.commit('pushViewTags', route)
					this.$store.commit('pushKeepLive', route.name)
				}
			},
			// 高亮tag
			isActive(route) {
				return route.fullPath === this.$route.fullPath
			},
			// 关闭tag
			closeSelectedTag(tag, autoPushLatestView = true) {
				this.$store.commit('removeViewTags', tag)
				this.$store.commit('removeIframeList', tag)
				this.$store.commit('removeKeepLive', tag.name)
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
				const nowTag = this.getCurrentTag()
				// 判断是否当前路由，否的话跳转
				// eslint-disable-next-line eqeqeq
				if (this.$route.fullPath !== nowTag.fullPath) {
					this.$router.push({
						path: nowTag.fullPath,
						query: nowTag.query
					})
				}
				this.$store.commit('refreshIframe', nowTag)
				setTimeout(() => {
					this.$store.commit('removeKeepLive', nowTag.name)
					this.$store.commit('setRouteShow', false)
					this.$nextTick(() => {
						this.$store.commit('pushKeepLive', nowTag.name)
						this.$store.commit('setRouteShow', true)
					})
				}, 0)
			},
			// TAB 关闭
			closeTabs() {
				const nowTag = this.getCurrentTag()
				if (!nowTag.meta.affix) {
					this.closeSelectedTag(nowTag)
				}
			},
			// TAB 关闭其他
			closeOtherTabs() {
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
			// TAB 最大化（包括标签栏）
			maximize() {
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
</style>
