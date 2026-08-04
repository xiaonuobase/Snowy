<template>
	<transition name="up">
		<div v-if="visible" :class="['snowy-lock-screen', { dark: isDark }]">
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
						@keyup.enter="handleUnlock"
					>
						<template #prefix>
							<lock-outlined />
						</template>
					</a-input-password>
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
	import { message, Modal } from 'ant-design-vue'
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

	// 锁屏界面是否可见（排除登录页和无Token状态）
	const visible = computed(() => {
		const token = tool.data.get('TOKEN')
		return isLocked.value && token && route.path !== '/login'
	})

	// 解锁
	const handleUnlock = async () => {
		if (!password.value) {
			message.warning('请输入密码')
			return
		}
		loading.value = true
		try {
			const param = {
				password: smCrypto.doSm2Encrypt(password.value)
			}
			const res = await axios.post('/api/sys/userCenter/openSafe', param, {
				headers: {
					[sysConfig.TOKEN_NAME]: sysConfig.TOKEN_PREFIX + tool.data.get('TOKEN')
				}
			})
			if (res.data.code === 200) {
				store.setIsLocked(false)
				password.value = ''
				message.success('解锁成功')
			} else if (res.data.code === 401 || res.data.code === 1011007 || res.data.code === 1011008) {
				// Token 失效逻辑
				message.error('登录已失效，请重新登录')
				handleLogout(true)
			} else {
				message.error(res.data.msg || '密码错误')
			}
		} catch (err) {
			if (err.response && (err.response.status === 401 || err.response.status === 403)) {
				message.error('登录已失效，请重新登录')
				handleLogout(true)
			} else {
				message.error('解锁失败')
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
		z-index: 9999;
		display: flex;
		align-items: center;
		justify-content: center;
		width: 100vw;
		height: 100vh;
		overflow: hidden;
		// 核心磨砂玻璃效果：全透明背景 + 适度模糊
		background: rgba(255, 255, 255, 0.1);
		backdrop-filter: blur(10px);
		-webkit-backdrop-filter: blur(10px);
		transition: all 0.3s;

		&.dark {
			background: rgba(0, 0, 0, 0.4);
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
			// 内容区域磨砂感
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
				margin-bottom: 20px;
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

	.up-enter-active,
	.up-leave-active {
		transition: all 0.5s ease;
	}
	.up-enter-from,
	.up-leave-to {
		opacity: 0;
	}
</style>
