<template>
	<div class="content-wrapper">
		<div class="content-box">
			<div class="content-form">
				<div class="content-header">
					<!-- 加载状态容器 -->
					<div class="loading-container" v-if="loading">
						<div class="loading-spinner"></div>
						<p class="loading-text">{{ tipText }}</p>
					</div>
					<!-- 错误状态容器 -->
					<div class="error-container" v-else>
						<p class="error-text">{{ tipText }}</p>
						<button class="retry-btn" @click="tryJump">重试</button>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script setup name="ssoLogin">
	import { useRoute } from "vue-router";
	import ssoApi from "@/api/auth/ssoApi";
	import { afterLogin } from "@/views/auth/login/util";
	import { ref, onMounted } from 'vue';
	import tool from "@/utils/tool";
	import loginApi from "@/api/auth/loginApi";

	const route = useRoute();
	const tipText = ref('加载中...');
	const loading = ref(true);  // 新增加载状态控制

	// 从url中查询到指定名称的参数值
	const getParam = (name, defaultValue) => {
		const query = window.location.search.substring(1);
		const vars = query.split("&");
		for (let i = 0; i < vars.length; i++) {
			const pair = vars[i].split("=");
			if (pair[0] === name) {
				return pair[1];
			}
		}
		return defaultValue === undefined ? null : defaultValue;
	};

	const ticket = getParam('ticket') || route.query.ticket;

	// 生命周期
	onMounted(async () => {
		await tryJump();
	});

	// 跳转
	const tryJump = async () => {
		// 重置加载状态
		loading.value = true;
		tipText.value = '加载中...';

		try {
			let existToken = tool.data.get('TOKEN');
			if (existToken) {
				const isLogin = await loginApi.isLogin();
				if (isLogin) {
					await goHome(existToken);
				} else {
					await redirectSsoAuthUrl(window.location.href);
				}
			} else {
				if (ticket) {
					await doLoginByTicket(ticket);
				} else {
					await redirectSsoAuthUrl(window.location.href);
				}
			}
		} catch (error) {
			loading.value = false;
			tipText.value = '处理失败，请重试';
			console.error('SSO登录失败:', error);
		}
	}

	// 跳转首页
	const goHome = async (loginToken) => {
		tipText.value = '验证成功，即将跳转...';
		setTimeout(async () => {
			await afterLogin(loginToken);
		}, 500);
	}

	// 处理SSO登录回调
	const doLoginByTicket = async (ticket) => {
		const loginToken = await ssoApi.doLoginByTicket({ ticket: ticket });
		tipText.value = '验证成功，即将跳转...';
		setTimeout(async () => {
			await afterLogin(loginToken);
		}, 500);
	}

	// 重定向到SSO登录页
	const redirectSsoAuthUrl = async (redirectUrl) => {
		const authUrl = await ssoApi.getSsoAuthUrl({ redirectUrl: redirectUrl });
		tipText.value = '即将跳转至SSO登录页...';
		setTimeout(() => {
			window.location.href = authUrl;
		}, 500);
	}
</script>

<style scoped>
	.content-wrapper {
		position: relative;
		width: 100vw;
		height: 100vh;
		overflow: hidden;
		background: linear-gradient(135deg, #0f5cb3 0%, #1677ff 50%, #4096ff 100%);
		box-shadow: inset 0 0 200px rgba(255, 255, 255, 0.1);
		transition: background-color 0.5s ease;
	}

	.content-box {
		position: absolute;
		width: 100vw;
		height: 100vh;
		top: 0;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.content-form {
		width: 450px;
		margin: auto;
		max-width: 100%;
		height: 100%;
		display: flex;
		align-items: center;
		transition: all 0.3s ease;
	}

	.content-header {
		width: 100%;
		text-align: center;
		margin-bottom: 20px;
	}

	.loading-container {
		text-align: center;
		color: white;
		padding: 20px;
		background-color: rgba(0, 0, 0, 0.1);
		border-radius: 8px;
	}

	.loading-spinner {
		width: 40px;
		height: 40px;
		margin: 0 auto 20px;
		border: 4px solid rgba(255, 255, 255, 0.2);
		border-radius: 50%;
		border-top-color: white;
		animation: spin 1s ease-in-out infinite;
	}

	.loading-text {
		font-size: 16px;
		color: rgba(255, 255, 255, 0.9);
		transition: opacity 0.3s ease;
	}

	.error-container {
		text-align: center;
		color: white;
		padding: 20px;
		background-color: rgba(0, 0, 0, 0.1);
		border-radius: 8px;
	}

	.error-text {
		font-size: 16px;
		color: rgba(255, 255, 255, 0.9);
		margin-bottom: 20px;
	}

	.retry-btn {
		background-color: rgba(255, 255, 255, 0.2);
		color: white;
		border: none;
		border-radius: 4px;
		padding: 8px 16px;
		font-size: 14px;
		cursor: pointer;
		transition: background-color 0.3s ease;
	}

	.retry-btn:hover {
		background-color: rgba(255, 255, 255, 0.3);
	}

	/* 旋转动画 */
	@keyframes spin {
		to { transform: rotate(360deg); }
	}
</style>
