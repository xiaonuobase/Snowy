<template>
	<div class="login-container">
		<div class="login-content">
			<!-- 左侧品牌展示区 -->
			<div class="login-left">
				<div class="brand-info">
					<img :src="sysBaseConfig.SNOWY_SYS_LOGO" class="brand-logo" alt="Logo" />
					<h1 class="brand-title">{{ sysBaseConfig.SNOWY_SYS_NAME }}</h1>
					<p class="brand-desc">{{ sysBaseConfig.SNOWY_SYS_DEFAULT_DESCRRIPTION }}</p>
				</div>
				<div class="brand-footer">
					<p>{{ sysBaseConfig.SNOWY_SYS_COPYRIGHT }}</p>
				</div>
			</div>

			<!-- 右侧找回密码表单区 -->
			<div class="login-right">
				<div class="login-form-wrapper">
					<div class="login-header">
						<h2>找回密码</h2>
						<p>请选择找回方式并按照提示操作</p>
					</div>

					<a-tabs v-model:activeKey="activeKey" class="login-tabs">
						<a-tab-pane key="userPhone" tab="手机找回">
							<phone-find-form />
						</a-tab-pane>
						<a-tab-pane key="userEmail" tab="邮箱找回" force-render>
							<email-find-form />
						</a-tab-pane>
					</a-tabs>

					<div class="register-footer">
						想起密码了？ <a href="/front/client/login">立即登录</a>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script setup>
	import PhoneFindForm from './phoneFindForm.vue'
	import EmailFindForm from './emailFindForm.vue'
	import { globalStore } from '@/store'

	const store = globalStore()
	const activeKey = ref('userPhone')
	const sysBaseConfig = computed(() => {
		return store.sysBaseConfig
	})
</script>

<style lang="less" scoped>
	@import '../login/login';
</style>
