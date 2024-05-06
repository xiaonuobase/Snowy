<template>
	<a-layout>
		<a-layout class="layout">
			<div id="snowyHeader" class="snowy-header top-snowy-header xn-pd050">
				<div class="snowy-header-left xn-pl0">
					<header id="snowyHeaderLogo" class="snowy-header-logo">
						<div class="snowy-header-left">
							<div class="logo-bar">
								<img class="logo" :src="sysBaseConfig.SNOWY_SYS_LOGO" />
								<span>{{ sysBaseConfig.SNOWY_SYS_NAME }}</span>
							</div>
						</div>
					</header>
				</div>
				<moduleMenu v-if="moduleMenuShow" @switchModule="switchModule" class="xn-pdl25" />
				<div class="xn-navmenu-line" id="xn-line-nav">
					<a-menu
						class="xn-bb0"
						id="topHeaderMenu"
						:selectedKeys="selectedKeys"
						:theme="sideTheme"
						mode="horizontal"
						@select="onSelect"
						@openChange="onOpenChange"
						collapsed="true"
					>
						<NavMenu :nav-menus="menuList" />
					</a-menu>
				</div>
				<div class="snowy-header-right">
					<user-bar />
				</div>
			</div>
			<!-- 手机端情况下的左侧菜单 -->
			<Side-m v-if="isMobile" />
			<breadcrumb v-if="!isMobile && breadcrumbOpen" />
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
		layout: {},
		menu: { type: Array }, // 菜单
		menuList: { type: Array }, // 菜单
		sysBaseConfig: { type: Object },
		moduleMenuShow: { type: Boolean },
		selectedKeys: { type: Array },
		openKeys: { type: Array },
		sideTheme: { type: String },
		isMobile: { type: Boolean }, // 是否移动端
		breadcrumbOpen: { type: Boolean }, //面包屑
		layoutTagsOpen: { type: Boolean },
		layoutSiderDowbleMenu: { type: Boolean },
		kStore: { type: Object }, // 获取的仓库数据
		footerCopyrightOpen: { type: Boolean } //页脚版权信息
	})

	const emit = defineEmits(['onSelect', 'switchModule', 'onOpenChange'])
	const onSelect = (obj) => {
		emit('onSelect', obj)
	}
	const switchModule = (id) => {
		emit('switchModule', id)
	}
	const onOpenChange = (keys) => {
		emit('onOpenChange', keys)
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
