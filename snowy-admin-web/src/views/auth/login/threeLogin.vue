<template>
	<a-divider>{{ $t('login.signInOther') }}</a-divider>
	<div class="login-oauth layout-center">
		<a-space align="start">
			<a @click="getLoginRenderUrl('gitee')"><GiteeIcon /></a>
			<a-button type="primary" shape="circle">
				<wechat-filled />
			</a-button>
		</a-space>
	</div>
</template>

<script setup name="threeLogin">
	import thirdApi from '@/api/auth/thirdApi'

	const getLoginRenderUrl = (platform) => {
		const param = {
			platform: platform
		}
		thirdApi.thirdRender(param).then((data) => {
			window.location.href = data.authorizeUrl
		})
	}
</script>
