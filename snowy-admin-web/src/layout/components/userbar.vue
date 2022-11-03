<template>
	<div class="user-bar">
		<div v-if="!ismobile" class="search panel-item hidden-sm-and-down" @click="handleSearchClick">
			<search-outlined />
		</div>
		<div v-if="!ismobile" class="screen panel-item hidden-sm-and-down" @click="fullscreen">
			<fullscreen-outlined />
		</div>
		<!--<devUserMessage />-->
		<a-dropdown class="user panel-item">
			<div class="user-avatar">
				<a-avatar :src="userInfo.avatar" />
				<label>{{ userName }}</label>
			</div>
			<template #overlay>
				<a-menu>
					<a-menu-item key="uc" @click="handleUser('uc')">
						<UserOutlined style="margin-right: 8px" />
						<span>个人中心</span>
					</a-menu-item>
					<a-menu-item key="clearCache" @click="handleUser('clearCache')">
						<loading3-quarters-outlined style="margin-right: 8px" />
						<span>清理缓存</span>
					</a-menu-item>
					<a-menu-divider />
					<a-menu-item key="outLogin" @click="handleUser('outLogin')">
						<export-outlined style="margin-right: 8px" />
						<span>退出登录</span>
					</a-menu-item>
				</a-menu>
			</template>
		</a-dropdown>
		<a-dropdown v-if="!ismobile" class="panel-item">
			<global-outlined />
			<template #overlay>
				<a-menu :selected-keys="lang">
					<a-menu-item key="zh-cn" @click="handleIn18('zh-cn')">
						<span>简体中文</span>
					</a-menu-item>
					<a-menu-item key="en" @click="handleIn18('en')">
						<span>English</span>
					</a-menu-item>
				</a-menu>
			</template>
		</a-dropdown>
		<div class="setting panel-item" @click="openSetting">
			<layout-outlined />
		</div>
	</div>

	<!-- 整体风格设置抽屉 -->
	<a-drawer v-model:visible="settingDialog" :closable="false" width="300">
		<setting></setting>
	</a-drawer>
	<!-- 搜索面板 -->
	<a-modal
		:visible="searchActive"
		:closable="false"
		:footer="null"
		width="600px"
		style="overflow: hidden"
		destroyOnClose
		dialogClass="searchModal"
		:bodyStyle="{ maxHeight: '520px', overflow: 'auto', padding: '14px' }"
		@cancel="searchPanelClose"
	>
		<panel-search ref="panelSearch" @close="searchPanelClose" />
	</a-modal>
</template>

<script>
	import { createVNode } from 'vue'
	import { ExclamationCircleOutlined } from '@ant-design/icons-vue'
	import screenfull from 'screenfull'
	import { message } from 'ant-design-vue'
	import setting from './setting.vue'
	import router from '@/router'
	import tool from '@/utils/tool'
	import loginApi from '@/api/auth/loginApi'
	import devUserMessage from './message.vue'
	import panelSearch from './panel-search/index.vue'
	import mixinSearch from './mixins/search'
	export default {
		components: {
			setting,
			devUserMessage,
			panelSearch
		},
		mixins: [mixinSearch],
		data() {
			return {
				lang: [],
				settingDialog: false,
				userInfo: {},
				userName: '',
				userNameF: ''
			}
		},
		computed: {
			ismobile() {
				return this.$store.state.global.ismobile
			},
			userInfoWatch() {
				return this.$store.state.global.userInfo
			}
		},
		watch: {
			userInfoWatch(newVal, oldVal) {
				this.userInfo = newVal
			}
		},
		created() {
			// 获取默认语言
			this.lang = new Array(this.$TOOL.data.get('APP_LANG') || this.$CONFIG.LANG)
			this.userInfo = this.$TOOL.data.get('USER_INFO')
			this.userName = this.userInfo?.userName || ''
			this.userNameF = this.userName.substring(0, 1)
		},
		methods: {
			// 个人信息
			handleUser(key) {
				if (key === 'uc') {
					router.push({ path: '/usercenter' })
				}
				if (key === 'clearCache') {
					this.$confirm({
						title: '提示',
						content: '确认清理所有缓存？',
						icon: createVNode(ExclamationCircleOutlined),
						maskClosable: false,
						okText: '确定',
						cancelText: '取消',
						onOk() {
							message.loading('正在清理中...', 1)
							tool.data.clear()
							setTimeout(() => {
								router.replace({ path: '/login' })
								location.reload()
							}, 100)
						},
						onCancel() {}
					})
				}
				if (key === 'outLogin') {
					this.$confirm({
						title: '提示',
						content: '确认退出当前用户？',
						icon: createVNode(ExclamationCircleOutlined),
						maskClosable: false,
						onOk() {
							// 取得缓存中的token
							const token = tool.data.get('TOKEN')
							const param = {
								token: token
							}
							message.loading('退出中...', 1)
							loginApi
								.logout(param)
								.then(() => {
									// message.c
									// 清理掉个人的一些信息
									tool.data.remove('TOKEN')
									tool.data.remove('USER_INFO')
									tool.data.remove('MENU')
									tool.data.remove('PERMISSIONS')
									router.replace({ path: '/login' })
								})
								.catch(() => {
									tool.data.clear()
									router.replace({ path: '/login' })
									location.reload()
								})
						},
						onCancel() {}
					})
				}
			},
			// 设置多语言语种
			handleIn18(key) {
				this.lang = []
				this.lang.push(key)
				this.$i18n.locale = key
				this.$TOOL.data.set('APP_LANG', key)
			},
			// 设置抽屉
			openSetting() {
				this.settingDialog = true
			},
			// 全屏
			fullscreen() {
				const element = document.documentElement
				if (screenfull.isEnabled) {
					screenfull.toggle(element)
				}
			},
			// 搜索
			fullSearch() {}
		}
	}
</script>

<style lang="less" scoped>
	:deep(.ant-modal) {
		top: 20px;
	}
	:deep(.ant-modal-content) {
		border-radius: 10px;
	}
	.user-bar {
		display: flex;
		align-items: center;
		height: 100%;
	}
	.user-bar .panel-item {
		padding: 0 10px;
		cursor: pointer;
		height: 100%;
		display: flex;
		align-items: center;
	}
	.user-bar .panel-item i {
	}
	.user-bar .panel-item:hover {
		background: var(--header-color-split);
	}
	.user-bar .user-avatar {
		height: 49px;
		display: flex;
		align-items: center;
	}
	.user-bar .user-avatar label {
		display: inline-block;
		margin-left: 5px;
		cursor: pointer;
	}
	.setting {
		margin-right: 10px;
	}
</style>
