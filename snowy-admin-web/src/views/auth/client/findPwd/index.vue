<template>
	<div class="login-wrapper">
		<div class="login_main">
			<div class="login-form">
				<a-card>
					<div class="login-header">
						<h2>忘记密码</h2>
					</div>
					<a-tabs v-model:activeKey="activeKey">
						<a-tab-pane key="userPhone" tab="手机号找回">
							<phone-find-form />
						</a-tab-pane>
						<a-tab-pane key="userEmail" tab="邮箱找回" force-render>
							<email-find-form />
						</a-tab-pane>
					</a-tabs>
				</a-card>
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
