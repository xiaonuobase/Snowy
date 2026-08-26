<template>
	<a-layout>
		<a-layout-sider
			v-if="!isPhone"
			:width="firstSideWidth"
			:class="{ 'doublerow-first-side-tablet': isTablet }"
			:theme="sideTheme"
			:trigger="null"
			collapsible
			v-show="displayLayout"
		>
			<header id="snowyHeaderLogo" class="snowy-header-logo">
				<div class="snowy-header-left">
					<div class="logo-bar">
						<router-link to="/">
							<img class="logo" :title="sysBaseConfig.SNOWY_SYS_NAME" :src="sysBaseConfig.SNOWY_SYS_LOGO" />
						</router-link>
					</div>
				</div>
			</header>
			<a-menu
				:selectedKeys="doublerowSelectedKey"
				:theme="sideTheme"
				class="snowy-doublerow-layout-menu"
				v-for="item in menu"
				:key="item.path"
			>
				<a-menu-item
					:key="item.path"
					style="
						text-align: center;
						height: auto;
						line-height: 20px;
						flex: none;
						display: block;
						padding: 12px 0 !important;
					"
					@click="showMenu(item)"
					v-if="!item.meta.hidden"
				>
					<a v-if="item.meta && item.meta.type === 'link'" :href="item.path" target="_blank" @click.stop="() => {}" />
					<template #icon>
						<!-- 平板尺寸下只留图标，模块名改由右侧气泡提示承载 -->
						<a-tooltip v-if="isTablet" placement="right" :title="item.meta.title">
							<component :is="item.meta.icon" />
						</a-tooltip>
						<component v-else :is="item.meta.icon" class="xn-pl10" />
					</template>
					<div v-if="!isTablet" class="snowy-doublerow-layout-menu-item-fort-div">
						<span class="snowy-doublerow-layout-menu-item-fort-div-span">
							{{ item.meta.title }}
						</span>
					</div>
				</a-menu-item>
			</a-menu>
		</a-layout-sider>
		<!-- 手机端情况下的左侧菜单 -->
		<Side-m v-if="isPhone" v-show="displayLayout" />
		<a-layout>
			<div id="snowyHeader" class="snowy-header" v-show="displayLayout">
				<div class="snowy-header-left xn-pl0">
					<moduleMenu v-if="moduleMenuShow" @switchModule="switchModule" />
				</div>
				<div class="snowy-header-right">
					<user-bar />
				</div>
			</div>
			<a-layout>
				<div v-show="displayLayout"></div>
				<a-layout-sider
					v-if="!isPhone"
					v-show="displayLayout && layoutSiderDowbleMenu"
					:collapsed="secondSideCollapsed"
					:trigger="null"
					width="170"
					collapsible
					:theme="secondMenuSideTheme"
				>
					<a-menu
						:collapsed="secondSideCollapsed"
						:openKeys="openKeys"
						:selectedKeys="selectedKeys"
						mode="inline"
						:theme="secondMenuSideTheme"
						@select="onSelect"
					>
						<NavMenu :nav-menus="nextMenu" />
					</a-menu>
				</a-layout-sider>
				<a-layout-content>
					<breadcrumb v-if="!isMobile && breadcrumbOpen" v-show="displayLayout" />
					<!-- 多标签 -->
					<Tags v-if="!isMobile && layoutTagsOpen" v-show="displayLayout" />
					<div :class="displayLayout ? 'main-content-wrapper' : 'main-content-wrapper main-content-wrapper-max'">
						<div id="admin-ui-main" class="admin-ui-main">
							<router-view v-slot="{ Component }">
								<keep-alive :include="kStore.keepLiveRoute">
									<component :is="Component" v-if="kStore.routeShow" :key="route.name" />
								</keep-alive>
							</router-view>
							<iframe-view />
							<div v-if="footerCopyrightOpen" class="main-bottom-wrapper">
								<a class="xn-color-a0a0a0" :href="sysBaseConfig.SNOWY_SYS_COPYRIGHT_URL" target="_blank">{{
									sysBaseConfig.SNOWY_SYS_COPYRIGHT
								}}</a>
							</div>
						</div>
					</div>
				</a-layout-content>
			</a-layout>
		</a-layout>
	</a-layout>
</template>

<script setup>
	import { useRoute } from 'vue-router'
	import UserBar from '@/layout/components/userbar.vue'
	import Tags from '@/layout/components/tags.vue'
	import SideM from '@/layout/components/sideM.vue'
	import NavMenu from '@/layout/components/NavMenu.vue'
	import ModuleMenu from '@/layout/components/moduleMenu.vue'
	import IframeView from '@/layout/components/iframeView.vue'
	import Breadcrumb from '@/layout/components/breadcrumb.vue'

	const props = defineProps({
		layout: { type: String }, // 布局信息
		isMobile: { type: Boolean }, // 是否移动端（窄屏，含平板与手机）
		isPhone: { type: Boolean }, // 是否手机尺寸（小于 768px）
		isTablet: { type: Boolean }, // 是否平板尺寸（768px ~ 991px）
		sideTheme: { type: String },
		menuIsCollapse: {},
		sysBaseConfig: { type: Object },
		openKeys: { type: Array },
		selectedKeys: { type: Array },
		doublerowSelectedKey: { type: Array },
		nextMenu: { type: Array },
		menu: { type: Array }, // 菜单
		breadcrumbOpen: { type: Boolean }, //面包屑
		layoutTagsOpen: { type: Boolean },
		layoutSiderDowbleMenu: { type: Boolean },
		kStore: { type: Object }, // 获取的仓库数据
		footerCopyrightOpen: { type: Boolean }, //页脚版权信息
		moduleMenuShow: { type: Boolean },
		secondMenuSideTheme: {}
	})
	// 平板尺寸下一级目录栏收成纯图标栏，桌面保持出厂的 80
	const firstSideWidth = computed(() => {
		return props.isTablet ? 56 : 80
	})
	// 平板尺寸下二级菜单收成图标栏，避免两排侧边栏占去内容区过多横向空间
	const secondSideCollapsed = computed(() => {
		return props.isTablet ? true : props.menuIsCollapse
	})
	const emit = defineEmits(['onSelect', 'switchModule', 'showMenu', 'displayLayoutChange'])
	const displayLayout = ref(true)
	const route = useRoute()
	watch(route, () => {
		nextTick(() => {
			displayLayout.value = displayLayoutResult()
		})
		if (displayLayout.value) {
			emit('displayLayoutChange')
		}
	})
	onMounted(() => {
		nextTick(() => {
			displayLayout.value = displayLayoutResult()
		})
	})
	const displayLayoutResult = () => {
		// keep-alive 缓存名单由「打开/关闭标签」生命周期管理（tags.vue 的 addViewTags/closeSelectedTag），
		// 这里不再按 meta.keepLive 踢出，保证已打开的标签切换时不重新挂载
		if (
			route.meta.displayLayout === undefined ||
			route.meta.displayLayout === null ||
			route.meta.displayLayout === 'null'
		) {
			return true
		} else {
			return route.meta.displayLayout
		}
	}
	const onSelect = (obj) => {
		emit('onSelect', obj)
	}
	const switchModule = (id) => {
		emit('switchModule', id)
	}
	const showMenu = (route) => {
		emit('showMenu', route)
	}
</script>
<style lang="less" scoped>
	.xn-color-fff {
		color: #fff;
	}
	.xn-pdl25 {
		padding-left: 11px;
	}
	.xn-menu-line {
		text-align: center;
		height: auto;
		line-height: 20px;
		flex: none;
		display: block;
		padding: 12px 0 !important;
	}
	.xn-navmenu-line {
		min-width: 0;
		flex: 1 1 0%;
		overflow: hidden;
	}
	.xn-bb0 {
		border-bottom: none;
		position: relative;
	}
	.ant-layout-content {
		display: flex;
		flex-direction: column;
	}
	.xn-pd1180 {
		padding: 10px 150px 0 150px;
	}
	.xn-pd050 {
		padding: 0 50px;
	}
	.xn-pl10 {
		padding-left: 10px;
	}
	.xn-mg050 {
		margin: 0px 150px;
	}
	.main-content-wrapper-max {
		padding: 0;
	}
	/* 平板尺寸下第一排收成纯图标栏：logo 居中，菜单项去掉左右内边距让图标居中并放大 */
	.doublerow-first-side-tablet {
		:deep(.snowy-header-logo) {
			justify-content: center;
		}
		/* 清掉 snowy-header-left 的 20px 左内边距，否则 logo 会被顶得右偏 */
		:deep(.snowy-header-logo .snowy-header-left) {
			padding-left: 0;
		}
		:deep(.snowy-header-logo .logo-bar .logo) {
			margin-right: 0;
		}
		:deep(.ant-menu-item .ant-menu-item-icon) {
			font-size: 18px;
			margin-inline-end: 0 !important;
		}
		/* 标题已隐藏，清掉图标与标题之间的预留间距，否则图标会左偏 */
		:deep(.ant-menu-item .ant-menu-item-icon + span) {
			margin-inline-start: 0 !important;
		}
	}
</style>
