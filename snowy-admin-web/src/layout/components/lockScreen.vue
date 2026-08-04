<template>
	<transition name="lock-fade">
		<div v-show="visible" :class="['snowy-lock-screen', { dark: isDark }]">
			<div class="lock-screen-mask"></div>
			<div class="lock-screen-content">
				<div class="lock-screen-avatar">
					<img :src="userInfo.avatar" />
				</div>
				<div class="lock-screen-username">{{ userInfo.name }}</div>
				<div class="lock-screen-form">
					<a-input-password
						v-model:value="password"
						placeholder="请输入登录密码解锁"
						size="large"
						:style="{ borderRadius: 0 }"
						:status="errorMsg ? 'error' : ''"
						@keyup.enter="handleUnlock"
						@change="errorMsg = ''"
					>
						<template #prefix>
							<lock-outlined />
						</template>
					</a-input-password>
					<div class="lock-screen-error-msg">{{ errorMsg }}</div>
					<a-button
						type="primary"
						size="large"
						block
						:loading="loading"
						:style="{ borderRadius: 0 }"
						@click="handleUnlock"
					>
						解锁
					</a-button>
				</div>
			</div>
		</div>
	</transition>
</template>

<script setup>
	import { ref, computed } from 'vue'
	import { useRoute } from 'vue-router'
	import { Modal } from 'ant-design-vue'
	import { LockOutlined } from '@ant-design/icons-vue'
	import { globalStore } from '@/store'
	import tool from '@/utils/tool'
	import smCrypto from '@/utils/smCrypto'
	import axios from 'axios'
	import sysConfig from '@/config/index'

	const store = globalStore()
	const route = useRoute()
	const isLocked = computed(() => store.isLocked)
	const isDark = computed(() => store.theme === 'realDark')
	const userInfo = computed(() => store.userInfo)
	const password = ref('')
	const loading = ref(false)
	const errorMsg = ref('')

	// 锁屏界面是否可见（排除登录页和无Token状态）
	const visible = computed(() => {
		const token = tool.data.get('TOKEN')
		return isLocked.value && token && route.path !== '/login'
	})

	// 解锁
	const handleUnlock = async () => {
		if (!password.value) {
			errorMsg.value = '请输入密码'
			return
		}
		loading.value = true
		errorMsg.value = ''
		try {
			const param = {
				password: smCrypto.doSm2Encrypt(password.value)
			}
			const res = await axios.post('/api/sys/userCenter/unlock', param, {
				headers: {
					[sysConfig.TOKEN_NAME]: sysConfig.TOKEN_PREFIX + tool.data.get('TOKEN')
				}
			})
			if (res.data.code === 200) {
				store.setIsLocked(false)
				password.value = ''
			} else if (res.data.code === 401 || res.data.code === 1011007 || res.data.code === 1011008) {
				handleLogout(true)
			} else {
				errorMsg.value = res.data.msg || '密码错误'
			}
		} catch (err) {
			if (err.response && (err.response.status === 401 || err.response.status === 403)) {
				handleLogout(true)
			} else {
				errorMsg.value = '解锁失败，请重试'
			}
		} finally {
			loading.value = false
		}
	}

	// 退出登录/处理失效
	const handleLogout = (force = false) => {
		const doLogout = () => {
			tool.data.remove('TOKEN')
			tool.data.remove('USER_INFO')
			tool.data.remove('MENU')
			tool.data.remove('PERMISSIONS')
			tool.data.remove('SNOWY_IS_LOCKED')
			store.setIsLocked(false)
			window.location.reload()
		}

		if (force) {
			doLogout()
		} else {
			Modal.confirm({
				title: '提示',
				content: '确定退出登录吗？',
				onOk: () => {
					doLogout()
				}
			})
		}
	}
</script>

<style lang="less" scoped>
	.snowy-lock-screen {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		z-index: 2000;
		display: flex;
		align-items: center;
		justify-content: center;
		width: 100vw;
		height: 100vh;
		overflow: hidden;

		.lock-screen-mask {
			position: absolute;
			top: 0;
			left: 0;
			width: 100%;
			height: 100%;
			background: rgba(255, 255, 255, 0.1);
			backdrop-filter: blur(10px);
			-webkit-backdrop-filter: blur(10px);
			z-index: -1;
			will-change: backdrop-filter;
			transform: translateZ(0);
		}

		&.dark {
			.lock-screen-mask {
				background: rgba(0, 0, 0, 0.4);
			}
			.lock-screen-username {
				color: #fff;
			}
			.lock-screen-content {
				background: rgba(0, 0, 0, 0.3);
				border-color: rgba(255, 255, 255, 0.1);
			}
		}

		.lock-screen-content {
			width: 450px;
			padding: 60px 40px;
			text-align: center;
			background: rgba(255, 255, 255, 0.2);
			border: 1px solid rgba(255, 255, 255, 0.1);
			box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
			border-radius: 0;
			z-index: 1;
		}

		.lock-screen-avatar {
			width: 80px;
			height: 80px;
			margin: 0 auto 20px;
			border-radius: 50%;
			overflow: hidden;
			border: 4px solid rgba(255, 255, 255, 0.3);
			box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
			img {
				width: 100%;
				height: 100%;
				object-fit: cover;
			}
		}

		.lock-screen-username {
			font-size: 24px;
			font-weight: bold;
			color: #333;
			margin-bottom: 30px;
		}

		.lock-screen-form {
			.ant-input-affix-wrapper-lg {
				margin-bottom: 0;
			}
			.lock-screen-error-msg {
				color: #ff4d4f;
				text-align: left;
				font-size: 12px;
				min-height: 28px;
				line-height: 28px;
				padding-left: 2px;
			}
			.ant-btn {
				margin-top: 4px;
			}
		}
	}

	// 响应式处理
	@media (max-width: 768px) {
		.lock-screen-content {
			width: 90%;
			padding: 40px 20px;
		}
	}

	// 锁屏动画优化
	.lock-fade-enter-active,
	.lock-fade-leave-active {
		transition: opacity 0.3s ease;
		// 确保背景模糊在进入时立即应用，而不是从0开始插值
		.lock-screen-mask {
			transition: opacity 0.3s ease;
		}
		// 内容区域增加微小延迟，确保模糊效果先行渲染完成
		.lock-screen-content {
			transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
			transition-delay: 0.08s;
		}
	}

	.lock-fade-enter-from,
	.lock-fade-leave-to {
		opacity: 0;
		.lock-screen-content {
			opacity: 0;
			transform: scale(0.95);
		}
	}
</style>
