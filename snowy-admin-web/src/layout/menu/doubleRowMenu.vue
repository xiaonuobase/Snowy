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
						<component :is="item.meta.icon" class="xn-pl10" />
					</template>
					<div class="snowy-doublerow-layout-menu-item-fort-div">
						<span class="snowy-doublerow-layout-menu-item-fort-div-span">
							{{ item.meta.title }}
						</span>
					</div>
				</a-menu-item>
			</a-menu>
		</a-layout-sider>
		<!-- 手机端情况下的左侧菜单 -->
		<Side-m v-if="isMobile" />
		<a-layout>
			<div id="snowyHeader" class="snowy-header">
				<div class="snowy-header-left xn-pl0">
					<moduleMenu v-if="moduleMenuShow" @switchModule="switchModule" />
				</div>
				<div class="snowy-header-right">
					<user-bar />
				</div>
			</div>
			<a-layout>
				<a-layout-sider
					v-if="!isMobile"
					v-show="layoutSiderDowbleMenu"
					:collapsed="menuIsCollapse"
					:trigger="null"
					width="170"
					collapsible
					:theme="secondMenuSideTheme"
				>
					<a-menu
						:collapsed="menuIsCollapse"
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
					<breadcrumb v-if="!isMobile && breadcrumbOpen" />
					<!-- 多标签 -->
					<Tags v-if="!isMobile && layoutTagsOpen" />
					<div class="main-content-wrapper">
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

	const emit = defineEmits(['onSelect', 'switchModule', 'showMenu'])
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
</style>
