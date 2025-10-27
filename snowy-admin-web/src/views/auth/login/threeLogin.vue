<template>
	<a-divider>{{ $t('login.signInOther') }}</a-divider>
	<div class="login-oauth layout-center">
		<a-space align="start">
			<a v-if="formData.SNOWY_THIRD_IAM_ALLOW_LOGIN_FLAG" @click="getLoginRenderUrl('IAM')">
				<img style="width: 32px; height: 32px;" src="/src/assets/images/authSource/iam.png" alt="" />
			</a>
			<a v-if="formData.SNOWY_THIRD_WECHAT_ALLOW_LOGIN_FLAG" @click="getLoginRenderUrl('WECHAT')">
				<img style="width: 32px; height: 32px;" src="/src/assets/images/authSource/wechat.png" alt="" />
			</a>
		</a-space>
	</div>
</template>

<script setup name="threeLogin">
	import configApi from "@/api/dev/configApi";
	import thirdApi from '@/api/auth/thirdApi'
	const formData = ref({})
	const getConfigSysThirdAllowFlagList = () => {
		configApi.configSysThirdAllowFlagList().then((data) => {
			data.forEach((item) => {
				formData.value[item.configKey] = transferBooleanInValue(item.configValue)
			})
		})
	}
	// 转换值
	const transferBooleanInValue = (value) => {
		if (value === 'true' || value === 'false') {
			return value === 'true'
		} else {
			return value
		}
	}
	const getLoginRenderUrl = (platform) => {
		const param = {
			platform: platform,
			clientType: 'B'
		}
		thirdApi.thirdRender(param).then((data) => {
			window.location.href = data.authorizeUrl
		})
	}
	getConfigSysThirdAllowFlagList();
</script>
<style scoped>
	.bind-icon {
		font-size: 32px;
	}
</style>
