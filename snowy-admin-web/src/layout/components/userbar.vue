<template>
	<div class="user-bar">
		<!-- 搜索面板 -->
		<panel-search v-if="!isMobile" />
		<div v-if="!isMobile" class="screen panel-item hidden-sm-and-down" @click="fullscreen">
			<fullscreen-outlined />
		</div>
		<!--		<dev-user-message />-->
		<a-dropdown class="user panel-item">
			<div class="user-avatar">
				<a-avatar :src="userInfo ? userInfo.avatar : undefined" />
				<label>{{ userName }}</label>
			</div>
			<template #overlay>
				<a-menu>
					<a-menu-item key="uc" @click="handleUser('uc')">
						<UserOutlined class="xn-mr8" />
						<span>个人中心</span>
					</a-menu-item>
					<a-menu-item key="clearCache" @click="handleUser('clearCache')">
						<loading3-quarters-outlined class="xn-mr8" />
						<span>清理缓存</span>
					</a-menu-item>
					<a-menu-divider />
					<a-menu-item key="outLogin" @click="handleUser('outLogin')">
						<export-outlined class="xn-mr8" />
						<span>退出登录</span>
					</a-menu-item>
				</a-menu>
			</template>
		</a-dropdown>
		<a-dropdown v-if="!isMobile" class="panel-item">
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
		<div v-if="setDrawer === 'true'" class="setting panel-item" @click="openSetting">
			<layout-outlined />
		</div>
	</div>

	<!-- 整体风格设置抽屉 -->
	<a-drawer v-model:open="settingDialog" :closable="false" width="300">
		<setting />
	</a-drawer>
</template>

<script setup name="layoutUserBar">
	import { createVNode } from 'vue'
	import { ExclamationCircleOutlined } from '@ant-design/icons-vue'
	import { Modal } from 'ant-design-vue'
	import screenFull from 'screenfull'
	import { message } from 'ant-design-vue'
	import Setting from './setting.vue'
	import router from '@/router'
	import tool from '@/utils/tool'
	import config from '@/config/index'
	import loginApi from '@/api/auth/loginApi'
	import DevUserMessage from './message.vue'
	import PanelSearch from './panel-search/index.vue'
	import { globalStore } from '@/store'
	import { useI18n } from 'vue-i18n'
	const { locale } = useI18n()

	const lang = ref(new Array(tool.data.get('APP_LANG') || config.LANG))
	const settingDialog = ref(false)
	const setDrawer = ref(import.meta.env.VITE_SET_DRAWER)
	const store = globalStore()
	const isMobile = computed(() => {
		return store.isMobile
	})
	const userInfo = computed(() => {
		return store.userInfo
	})
	const userName = ref(userInfo.value?.userName || '')

	// 个人信息
	const handleUser = (key) => {
		if (key === 'uc') {
			router.push({ path: '/usercenter' })
		}
		if (key === 'clearCache') {
			Modal.confirm({
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
			Modal.confirm({
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
							// 清理掉个人的一些信息
							tool.data.remove('TOKEN')
							tool.data.remove('USER_INFO')
							tool.data.remove('MENU')
							tool.data.remove('PERMISSIONS')
							router.replace({ path: '/login' })
							nextTick(() => {
								// 清理缓存内的个人信息
								store.userInfo = undefined
							})
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
	}
	// 设置多语言语种
	const handleIn18 = (key) => {
		lang.value = []
		lang.value.push(key)
		locale.value = key
		tool.data.set('APP_LANG', key)
	}
	// 设置抽屉
	const openSetting = () => {
		settingDialog.value = true
	}
	// 全屏
	const fullscreen = () => {
		const element = document.documentElement
		if (screenFull.isEnabled) {
			screenFull.toggle(element)
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
