<template>
	<a-layout>
		<a-layout-sider
			v-if="!isMobile"
			v-model:collapsed="menuIsCollapse"
			:trigger="null"
			collapsible
			:theme="sideTheme"
			width="210"
		>
			<header id="snowyHeaderLogo" class="snowy-header-logo">
				<div class="snowy-header-left">
					<div class="logo-bar">
						<img class="logo" :src="sysBaseConfig.SNOWY_SYS_LOGO" />
						<span>{{ sysBaseConfig.SNOWY_SYS_NAME }}</span>
					</div>
				</div>
			</header>
			<div :class="menuIsCollapse ? 'admin-ui-side isCollapse' : 'admin-ui-side'">
				<div class="admin-ui-side-scroll">
					<a-menu
						v-model:openKeys="openKeys"
						v-model:selectedKeys="selectedKeys"
						:theme="sideTheme"
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
		<Side-m v-if="isMobile" />
		<!-- 右侧布局 -->
		<a-layout>
			<div id="snowyHeader" class="snowy-header">
				<div class="snowy-header-left" style="padding-left: 0px">
					<div v-if="!isMobile" class="panel-item hidden-sm-and-down" @click="menuIsCollapseClick">
						<MenuUnfoldOutlined v-if="menuIsCollapse" />
						<MenuFoldOutlined v-else />
					</div>
					<moduleMenu @switchModule="switchModule" />
					<top-bar v-if="!isMobile && breadcrumbOpen" />
				</div>
				<div class="snowy-header-right">
					<user-bar />
				</div>
			</div>
			<!-- 多标签 -->
			<Tags v-if="!isMobile && layoutTagsOpen" />
			<a-layout-content class="main-content-wrapper">
				<div id="admin-ui-main" class="admin-ui-main">
					<router-view v-slot="{ Component }">
						<keep-alive :include="keepLiveRoute">
							<component :is="Component" :key="route.name" v-if="routeShow" />
						</keep-alive>
					</router-view>
					<iframe-view />
					<div class="main-bottom-wrapper">
						<a style="color: #a0a0a0" :href="sysBaseConfig.SNOWY_SYS_COPYRIGHT_URL" target="_blank">{{
							sysBaseConfig.SNOWY_SYS_COPYRIGHT
						}}</a>
					</div>
				</div>
			</a-layout-content>
		</a-layout>
	</a-layout>
</template>

<script setup name="classicalLayout">
	import UserBar from '@/layout/components/userbar.vue'
	import Tags from '@/layout/components/tags.vue'
	import SideM from '@/layout/components/sideM.vue'
	import NavMenu from '@/layout/components/NavMenu.vue'
	import ModuleMenu from '@/layout/components/moduleMenu.vue'
	import IframeView from '@/layout/components/iframeView.vue'
	import TopBar from '@/layout/components/topbar.vue'

	const props = defineProps({
		isMobile: {
			type: Boolean,
			default: () => false
		},
		menuIsCollapse: {
			type: Boolean,
			default: () => false
		},
		sideTheme: {
			type: String,
			default: () => ''
		},
		sysBaseConfig: {
			type: Object,
			default: () => undefined
		},
		openKeys: {
			type: Array,
			default: () => []
		},
		selectedKeys: {
			type: Array,
			default: () => []
		},
		menu: {
			type: Array,
			default: () => []
		},
		breadcrumbOpen: {
			type: Boolean,
			default: () => false
		},
		layoutTagsOpen: {
			type: Boolean,
			default: () => false
		},
		keepLiveRoute: {
			type: Array,
			default: () => []
		},
		routeShow: {
			type: Boolean,
			default: () => false
		},
		route: {
			type: Object,
			default: () => undefined
		}
	})
	const emit = defineEmits(['onSelect', 'onOpenChange', 'menuIsCollapseClick', 'switchModule'])
	const onSelect = (obj) => {
		emit('onSelect', obj)
	}
	// 菜单展开时
	const onOpenChange = (keys) => {
		emit('onOpenChange', keys)
	}
	// 切换应用时
	const switchModule = (id) => {
		emit('switchModule', id)
	}
	const menuIsCollapseClick = () => {
		emit('menuIsCollapseClick')
	}
</script>
