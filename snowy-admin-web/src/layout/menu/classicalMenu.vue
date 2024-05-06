<template>
	<a-layout>
		<a-layout-sider
			v-if="!isMobile"
			:collapsed="menuIsCollapse"
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
						v-bind:openKeys="openKeys"
						v-bind:selectedKeys="selectedKeys"
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
				<div class="snowy-header-left xn-pl0">
					<div v-if="!isMobile" class="panel-item hidden-sm-and-down" @click="menuIsCollapseClick">
						<MenuUnfoldOutlined v-if="menuIsCollapse" />
						<MenuFoldOutlined v-else />
					</div>
					<moduleMenu v-if="moduleMenuShow" @switchModule="switchModule" />
				</div>
				<div class="snowy-header-right">
					<user-bar />
				</div>
			</div>
			<Breadcrumb v-if="!isMobile && breadcrumbOpen" />
			<!-- 多标签 -->
			<Tags v-if="!isMobile && layoutTagsOpen" />
			<a-layout-content class="main-content-wrapper">
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
	const route = useRoute()

	import UserBar from '@/layout/components/userbar.vue'
	import Tags from '@/layout/components/tags.vue'
	import SideM from '@/layout/components/sideM.vue'
	import NavMenu from '@/layout/components/NavMenu.vue'
	import ModuleMenu from '@/layout/components/moduleMenu.vue'
	import IframeView from '@/layout/components/iframeView.vue'
	import Breadcrumb from '@/layout/components/breadcrumb.vue'

	const props = defineProps({
		layout: { type: String }, // 布局信息
		isMobile: { type: Boolean }, // 是否移动端
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

	const emit = defineEmits(['onSelect', 'onOpenChange', 'switchModule', 'menuIsCollapseClick'])
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
</style>
