<template>
	<div class="login-wrapper">
		<div class="login_background">
			<div class="logo_background">
				<a
					:class="{ 'no-link': !sysBaseConfig.SNOWY_SYS_COPYRIGHT_URL }"
					:href="sysBaseConfig.SNOWY_SYS_COPYRIGHT_URL"
					target="_blank"
					@click="handleLink"
				>
					<img :alt="sysBaseConfig.SNOWY_SYS_NAME" :src="sysBaseConfig.SNOWY_SYS_LOGO" />
					<label>{{ sysBaseConfig.SNOWY_SYS_NAME }}</label>
				</a>
			</div>
			<div class="version">
				<p>{{ sysBaseConfig.SNOWY_SYS_DEFAULT_DESCRRIPTION }}</p>
				<p>{{ sysBaseConfig.SNOWY_SYS_COPYRIGHT }} {{ sysBaseConfig.SNOWY_SYS_VERSION }}</p>
			</div>
		</div>
		<div class="login_main">
			<div class="login-form">
				<a-card>
					<div class="login-header">
						<h2>{{ $t('login.forgetPassword') }}</h2>
					</div>
					<a-tabs v-model:activeKey="activeKey">
						<a-tab-pane key="userPhone" :tab="$t('login.restPhoneType')">
							<phone-find-form />
						</a-tab-pane>
						<a-tab-pane key="userEmail" :tab="$t('login.restEmailType')" force-render>
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
	// logo链接
	const handleLink = (e) => {
		if (!sysBaseConfig.value.SNOWY_SYS_COPYRIGHT_URL) {
			e?.stopPropagation()
			e?.preventDefault()
		}
	}
</script>

<style lang="less" scoped>
	@import '../login/login';
</style>
