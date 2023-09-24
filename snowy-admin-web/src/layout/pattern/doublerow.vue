<template>
	<a-layout>
		<a-layout-sider v-if="!isMobile" width="80" :theme="sideTheme" :trigger="null" collapsible>
			<header id="snowyHeaderLogo" class="snowy-header-logo">
				<div class="snowy-header-left">
					<div class="logo-bar">
						<router-link to="/">
							<img class="logo" :title="sysBaseConfig.SNOWY_SYS_NAME" :src="sysBaseConfig.SNOWY_SYS_LOGO" />
						</router-link>
					</div>
				</div>
			</header>
			<a-menu v-model:selectedKeys="doublerowSelectedKey" :theme="sideTheme" class="snowy-doublerow-layout-menu">
				<a-menu-item
					v-for="item in menu"
					:key="item.path"
					style="
						text-align: center;
						border-radius: 2px;
						height: auto;
						line-height: 20px;
						flex: none;
						display: block;
						padding: 12px 0 !important;
					"
					@click="showMenu(item)"
				>
					<a v-if="item.meta && item.meta.type === 'link'" :href="item.path" target="_blank" @click.stop="() => {}"></a>
					<template #icon>
						<component :is="item.meta.icon" style="padding-left: 10px" />
					</template>
					<div class="snowy-doublerow-layout-menu-item-fort-div">
						<span class="snowy-doublerow-layout-menu-item-fort-div-span">
							{{ item.meta.title }}
						</span>
					</div>
				</a-menu-item>
			</a-menu>
		</a-layout-sider>
		<a-layout-sider
			v-if="!isMobile"
			v-show="layoutSiderDowbleMenu"
			v-model:collapsed="menuIsCollapse"
			:trigger="null"
			width="170"
			collapsible
			:theme="secondMenuSideTheme"
		>
			<div v-if="!menuIsCollapse" id="snowyDoublerowSideTop" class="snowy-doublerow-side-top">
				<h2 class="snowy-title">{{ pMenu.meta.title }}</h2>
			</div>
			<a-menu
				v-model:collapsed="menuIsCollapse"
				v-model:openKeys="openKeys"
				v-model:selectedKeys="selectedKeys"
				mode="inline"
				:theme="secondMenuSideTheme"
				@select="onSelect"
			>
				<NavMenu :nav-menus="nextMenu" />
			</a-menu>
		</a-layout-sider>
		<!-- 手机端情况下的左侧菜单 -->
		<Side-m v-if="isMobile" />
		<a-layout>
			<div id="snowyHeader" class="snowy-header">
				<div class="snowy-header-left" style="padding-left: 0px">
					<moduleMenu @switchModule="switchModule" />
					<top-bar v-if="!isMobile && breadcrumbOpen" />
				</div>
				<div class="snowy-header-right">
					<user-bar />
				</div>
			</div>
			<!-- 多标签 -->
			<Tags v-if="!isMobile && layoutTagsOpen"></Tags>
			<a-layout-content class="main-content-wrapper">
				<div id="admin-ui-main" class="admin-ui-main">
					<router-view v-slot="{ Component }">
						<keep-alive :include="keepLiveRoute">
							<component :is="Component" v-if="routeShow" :key="route.name" />
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

<script setup name="doublerowLayout">
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
		},
		layoutSiderDowbleMenu: {
			type: Boolean,
			default: () => true
		},
		secondMenuSideTheme: {
			type: String,
			default: () => undefined
		},
		nextMenu: {
			type: Array,
			default: () => []
		},
		doublerowSelectedKey: {
			type: Array,
			default: () => []
		},
		pMenu: {
			type: Object,
			default: () => undefined
		}
	})
	const emit = defineEmits(['onSelect', 'showMenu', 'onOpenChange', 'menuIsCollapseClick', 'switchModule'])
	const onSelect = (obj) => {
		emit('onSelect', obj)
	}
	const showMenu = (route) => {
		emit('showMenu', route)
	}
	const menuIsCollapseClick = () => {
		emit('menuIsCollapseClick')
	}
	// 切换应用时
	const switchModule = (id) => {
		emit('switchModule', id)
	}
</script>
