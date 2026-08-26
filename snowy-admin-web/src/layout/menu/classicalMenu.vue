<template>
	<a-layout>
		<a-layout-sider
			v-if="!isPhone"
			:collapsed="sideCollapsed"
			:trigger="null"
			collapsible
			:theme="sideTheme"
			width="210"
			v-show="displayLayout"
		>
			<header id="snowyHeaderLogo" class="snowy-header-logo">
				<div class="snowy-header-left">
					<div class="logo-bar">
						<img class="logo" :src="sysBaseConfig.SNOWY_SYS_LOGO" />
						<span :style="{ fontSize: sysNameFontSize }" :title="sysBaseConfig.SNOWY_SYS_NAME">{{
							sysBaseConfig.SNOWY_SYS_NAME
						}}</span>
					</div>
				</div>
			</header>
			<div :class="sideCollapsed ? 'admin-ui-side isCollapse' : 'admin-ui-side'">
				<div class="admin-ui-side-scroll">
					<a-menu
						:openKeys="openKeys"
						:selectedKeys="selectedKeys"
						:theme="sideTheme"
						:class="[!roundedCornerStyleOpen ? 'no-radius-menu' : '']"
						mode="inline"
						@select="onSelect"
						@openChange="onOpenChange"
					>
						<NavMenu :nav-menus="menu" />
					</a-menu>
				</div>
			</div>
		</a-layout-sider>
		<!-- 手机端情况下的左侧菜单 -->
		<Side-m v-if="isPhone" v-show="displayLayout" />
		<!-- 右侧布局 -->
		<a-layout>
			<div id="snowyHeader" class="snowy-header" v-show="displayLayout">
				<div class="snowy-header-left xn-pl0">
					<div v-if="!isPhone" class="panel-item hidden-sm-and-down" @click="menuIsCollapseClick">
						<MenuUnfoldOutlined v-if="sideCollapsed" />
						<MenuFoldOutlined v-else />
					</div>
					<moduleMenu v-if="moduleMenuShow" @switchModule="switchModule" />
				</div>
				<div class="snowy-header-right">
					<user-bar />
				</div>
			</div>
			<Breadcrumb v-if="!isMobile && breadcrumbOpen" v-show="displayLayout" />
			<!-- 多标签 -->
			<Tags v-if="!isMobile && layoutTagsOpen" v-show="displayLayout" />
			<a-layout-content
				:class="displayLayout ? 'main-content-wrapper' : 'main-content-wrapper main-content-wrapper-max'"
			>
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
			</a-layout-content>
		</a-layout>
	</a-layout>
</template>

<script setup>
	import { useRoute } from 'vue-router'
	import { MenuUnfoldOutlined, MenuFoldOutlined } from '@ant-design/icons-vue'
	import UserBar from '@/layout/components/userbar.vue'
	import Tags from '@/layout/components/tags.vue'
	import SideM from '@/layout/components/sideM.vue'
	import NavMenu from '@/layout/components/NavMenu.vue'
	import ModuleMenu from '@/layout/components/moduleMenu.vue'
	import IframeView from '@/layout/components/iframeView.vue'
	import Breadcrumb from '@/layout/components/breadcrumb.vue'
	import { globalStore } from '@/store'

	const store = globalStore()
	const roundedCornerStyleOpen = computed(() => {
		return store.roundedCornerStyleOpen
	})

	const props = defineProps({
		layout: { type: String }, // 布局信息
		isMobile: { type: Boolean }, // 是否移动端（窄屏，含平板与手机）
		isPhone: { type: Boolean }, // 是否手机尺寸（小于 768px）
		isTablet: { type: Boolean }, // 是否平板尺寸（768px ~ 991px）
		menuIsCollapse: { type: Boolean }, // 菜单是否折叠
		sideTheme: { type: String },
		sysBaseConfig: { type: Object },
		openKeys: { type: Array },
		selectedKeys: { type: Array },
		menu: { type: Array }, // 菜单
		breadcrumbOpen: { type: Boolean }, //面包屑
		layoutTagsOpen: { type: Boolean },
		kStore: { type: Object }, // 获取的仓库数据
		footerCopyrightOpen: { type: Boolean }, //页脚版权信息
		moduleMenuShow: { type: Boolean }
	})
	const emit = defineEmits(['onSelect', 'onOpenChange', 'switchModule', 'menuIsCollapseClick', 'displayLayoutChange'])
	// 系统名称的视觉宽度权重：中文与全角符号按一个字宽计，英文数字按约六成字宽计
	const sysNameWeight = computed(() => {
		const sysName = props.sysBaseConfig ? props.sysBaseConfig.SNOWY_SYS_NAME || '' : ''
		let weight = 0
		for (const word of sysName) {
			weight += /[一-龥＀-￯]/.test(word) ? 1 : 0.6
		}
		return weight
	})
	// 系统名称字号按长度分档，避免长名称顶到侧边栏右边缘造成左右留白失衡
	const sysNameFontSize = computed(() => {
		const weight = sysNameWeight.value
		if (weight <= 6) {
			return '20px'
		}
		if (weight <= 8) {
			return '17px'
		}
		if (weight <= 10) {
			return '14px'
		}
		return '13px'
	})
	// 平板尺寸下侧边菜单默认收成图标栏，给内容区让出横向空间
	const tabletSideCollapse = ref(true)
	const sideCollapsed = computed(() => {
		return props.isTablet ? tabletSideCollapse.value : props.menuIsCollapse
	})
	// 每次进入平板尺寸都重新收起，避免沿用上一次在平板下手动展开的状态
	watch(
		() => props.isTablet,
		(newValue) => {
			if (newValue) {
				tabletSideCollapse.value = true
			}
		}
	)
	const displayLayout = ref(true)
	const route = useRoute()
	watch(route, () => {
		nextTick(() => {
			displayLayout.value = displayLayoutResult()
			if (displayLayout.value) {
				emit('displayLayoutChange')
			}
		})
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
	const onOpenChange = (keys) => {
		emit('onOpenChange', keys)
	}
	const switchModule = (id) => {
		emit('switchModule', id)
	}
	const menuIsCollapseClick = () => {
		// 平板尺寸下折叠状态仅本地生效，不写入用户的全局菜单折叠配置
		if (props.isTablet) {
			tabletSideCollapse.value = !tabletSideCollapse.value
			return
		}
		emit('menuIsCollapseClick')
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
	/* 系统名称区：logo 与名称整组水平居中，左右留白自动对称 */
	.snowy-header-logo {
		justify-content: center;
		padding: 0 10px;
	}
	/* 清掉出厂的 20px 左内边距，否则整组会被顶得右偏 */
	.snowy-header-logo .snowy-header-left {
		padding-left: 0;
		min-width: 0;
	}
	.snowy-header-logo .logo-bar {
		min-width: 0;
	}
	.snowy-header-logo .logo-bar .logo {
		flex-shrink: 0;
		margin-right: 8px;
	}
	/* 字号已按字数分档，这里再兜一层省略号，超长时可悬停看全称 */
	.snowy-header-logo .logo-bar > span {
		min-width: 0;
		overflow: hidden;
		white-space: nowrap;
		text-overflow: ellipsis;
	}
	/* 侧边栏折叠后名称已隐藏，清掉它预留的右间距，让 logo 正居中 */
	.ant-layout-sider-collapsed .snowy-header-logo .logo-bar .logo {
		margin-right: 0;
	}
	.no-radius-menu :deep(.ant-menu-item),
	.no-radius-menu :deep(.ant-menu-submenu-title) {
		border-radius: 0 !important;
		margin-inline: 0 !important;
		width: 100% !important;
		border-left: 4px solid transparent !important;
		border-right: 4px solid transparent !important;
		transition:
			background 0.3s,
			color 0.3s !important;
	}
</style>
