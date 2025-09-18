<template>
	<a-divider>{{ $t('login.signInOther') }}</a-divider>
	<div class="login-oauth layout-center">
		<a-space align="start">
			<a @click="getLoginRenderUrl('iam')">
				<img style="width: 32px; height: 32px;" src="/src/assets/images/snowy-iam.png" alt="" />
			</a>
			<a @click="getLoginRenderUrl('gitee')">
				<GiteeIcon />
			</a>
			<a @click="getLoginRenderUrl('wechat')">
				<wechat-outlined class="bind-icon" :style="{ color: '#1AAD19' }" />
			</a>
		</a-space>
	</div>
</template>

<script setup name="threeLogin">
	import thirdApi from '@/api/auth/thirdApi'
	import WechatOutlined from "@ant-design/icons-vue/WechatOutlined";

	const getLoginRenderUrl = (platform) => {
		const param = {
			platform: platform,
			clientType: 'B'
		}
		thirdApi.thirdRender(param).then((data) => {
			window.location.href = data.authorizeUrl
		})
	}
</script>
<style scoped>
	.bind-icon {
		font-size: 32px;
	}
</style>
