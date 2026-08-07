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
						ref="passwordRef"
						v-model:value="password"
						placeholder="请输入登录密码解锁"
						size="large"
						:style="{ borderRadius: 0 }"
						:status="inputStatus"
						@keyup.enter="handleUnlock"
						@change="
							() => {
								inputStatus = ''
								errorMessage = ''
							}
						"
					>
						<template #prefix>
							<lock-outlined />
						</template>
					</a-input-password>
					<div class="lock-screen-error-msg">{{ errorMessage }}</div>
					<a-button
						type="primary"
						size="large"
						block
						class="lock-screen-unlock-btn"
						:loading="loading"
						:style="{ borderRadius: 0 }"
						@click="handleUnlock"
					>
						解锁
					</a-button>
					<a-button type="link" block class="lock-screen-logout-btn" @click="handleBackToLogin"> 返回登录 </a-button>
				</div>
			</div>
		</div>
	</transition>
</template>

<script setup name="lockScreen">
	import { useRoute, useRouter } from 'vue-router'
	import { LockOutlined } from '@ant-design/icons-vue'
	import { globalStore } from '@/store'
	import { useMenuStore } from '@/store/menu'
	import { useDictStore } from '@/store/dict'
	import tool from '@/utils/tool'
	import smCrypto from '@/utils/smCrypto'
	import userCenterApi from '@/api/sys/userCenterApi'

	const store = globalStore()
	const route = useRoute()
	const router = useRouter()
	const isDark = computed(() => store.theme === 'realDark')
	const userInfo = computed(() => store.userInfo || {})
	const passwordRef = ref()
	const password = ref('')
	const loading = ref(false)
	const inputStatus = ref('')
	const errorMessage = ref('')

	// 锁屏界面是否可见，登录页不展示
	const visible = computed(() => store.isLocked && route.path !== '/login')

	// 解锁
	const handleUnlock = () => {
		if (!password.value) {
			inputStatus.value = 'error'
			errorMessage.value = '请输入登录密码'
			return
		}
		loading.value = true
		inputStatus.value = ''
		errorMessage.value = ''
		userCenterApi
			.userUnlock({ password: smCrypto.doSm2Encrypt(password.value) }, { skipErrorMessage: true })
			.then(() => {
				store.setIsLocked(false)
				password.value = ''
				// 锁屏期间刷新过页面的话，菜单与字典的请求会被4012拦下，解锁后补拉一次
				useMenuStore().refreshApiMenu()
				useDictStore().refreshDict()
			})
			.catch((err) => {
				inputStatus.value = 'error'
				errorMessage.value = err.msg || '解锁失败，请重试'
			})
			.finally(() => {
				loading.value = false
			})
	}

	// 忘记密码等情况下的兜底出口：清空登录态回到登录页重新登录
	const handleBackToLogin = () => {
		tool.clearLoginCache()
		store.setIsLocked(false)
		store.setUserInfo(undefined)
		router.replace({ path: '/login' }).then(() => {
			window.location.reload()
		})
	}

	// 锁屏弹出时自动聚焦密码框，避免还要先点一下输入框
	watch(visible, (isVisible) => {
		if (isVisible) {
			nextTick(() => passwordRef.value?.focus())
		} else {
			password.value = ''
			inputStatus.value = ''
			errorMessage.value = ''
		}
	})
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
			.lock-screen-unlock-btn {
				margin-top: 16px;
			}
			.lock-screen-logout-btn {
				margin-top: 8px;
			}
			.lock-screen-error-msg {
				color: #ff4d4f;
				font-size: 14px;
				min-height: 28px;
				line-height: 28px;
				text-align: left;
				padding-left: 2px;
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
