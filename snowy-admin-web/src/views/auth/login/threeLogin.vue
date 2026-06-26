<template>
	<template v-if="hasAnyThirdLogin">
		<a-divider>{{ $t('login.signInOther') }}</a-divider>
		<div class="login-oauth layout-center">
			<a-space align="start">
				<a v-if="formData.SNOWY_THIRD_OAUTH_ALLOW_LOGIN_FLAG" @click="getLoginRenderUrl('OAUTH')">
					<img style="width: 32px; height: 32px" src="/src/assets/images/authSource/oauth.png" alt="" />
				</a>
				<a v-if="formData.SNOWY_THIRD_OIDC_ALLOW_LOGIN_FLAG" @click="getLoginRenderUrl('OIDC')">
					<img style="width: 32px; height: 32px" src="/src/assets/images/authSource/oidc.png" alt="" />
				</a>
				<a v-if="formData.SNOWY_THIRD_JWT_ALLOW_LOGIN_FLAG" @click="getLoginRenderUrl('JWT')">
					<img style="width: 32px; height: 32px" src="/src/assets/images/authSource/jwt.png" alt="" />
				</a>
				<a v-if="formData.SNOWY_THIRD_CAS_ALLOW_LOGIN_FLAG" @click="getLoginRenderUrl('CAS')">
					<img style="width: 32px; height: 32px" src="/src/assets/images/authSource/cas.png" alt="" />
				</a>
				<a v-if="formData.SNOWY_THIRD_SAML_ALLOW_LOGIN_FLAG" @click="getLoginRenderUrl('SAML')">
					<img style="width: 32px; height: 32px" src="/src/assets/images/authSource/saml.png" alt="" />
				</a>
				<a v-if="formData.SNOWY_THIRD_IAM_ALLOW_LOGIN_FLAG" @click="getLoginRenderUrl('IAM')">
					<img style="width: 32px; height: 32px" src="/src/assets/images/authSource/iam.png" alt="" />
				</a>
				<a v-if="formData.SNOWY_THIRD_WECHAT_ALLOW_LOGIN_FLAG" @click="getLoginRenderUrl('WECHAT')">
					<img style="width: 32px; height: 32px" src="/src/assets/images/authSource/wechat.png" alt="" />
				</a>
				<a v-if="formData.SNOWY_THIRD_DINGTALK_ALLOW_LOGIN_FLAG" @click="getLoginRenderUrl('DINGTALK')">
					<img style="width: 32px; height: 32px" src="/src/assets/images/authSource/dingtalk.png" alt="" />
				</a>
				<a v-if="formData.SNOWY_THIRD_WORKWECHAT_ALLOW_LOGIN_FLAG" @click="getLoginRenderUrl('WORKWECHAT')">
					<img style="width: 32px; height: 32px" src="/src/assets/images/authSource/workwechat.png" alt="" />
				</a>
				<a v-if="formData.SNOWY_THIRD_FEISHU_ALLOW_LOGIN_FLAG" @click="getLoginRenderUrl('FEISHU')">
					<img style="width: 32px; height: 32px" src="/src/assets/images/authSource/feishu.png" alt="" />
				</a>
				<a v-if="formData.SNOWY_THIRD_WELINK_ALLOW_LOGIN_FLAG" @click="getLoginRenderUrl('WELINK')">
					<img style="width: 32px; height: 32px" src="/src/assets/images/authSource/welink.png" alt="" />
				</a>
				<a v-if="formData.SNOWY_THIRD_YUNZHIJIA_ALLOW_LOGIN_FLAG" @click="getLoginRenderUrl('YUNZHIJIA')">
					<img style="width: 32px; height: 32px" src="/src/assets/images/authSource/yunzhijia.png" alt="" />
				</a>
				<a v-if="formData.SNOWY_THIRD_QQ_ALLOW_LOGIN_FLAG" @click="getLoginRenderUrl('QQ')">
					<img style="width: 32px; height: 32px" src="/src/assets/images/authSource/qq.png" alt="" />
				</a>
				<a v-if="formData.SNOWY_THIRD_WEIBO_ALLOW_LOGIN_FLAG" @click="getLoginRenderUrl('WEIBO')">
					<img style="width: 32px; height: 32px" src="/src/assets/images/authSource/weibo.png" alt="" />
				</a>
				<a v-if="formData.SNOWY_THIRD_DOUYIN_ALLOW_LOGIN_FLAG" @click="getLoginRenderUrl('DOUYIN')">
					<img style="width: 32px; height: 32px" src="/src/assets/images/authSource/douyin.png" alt="" />
				</a>
				<a v-if="formData.SNOWY_THIRD_ALIPAY_ALLOW_LOGIN_FLAG" @click="getLoginRenderUrl('ALIPAY')">
					<img style="width: 32px; height: 32px" src="/src/assets/images/authSource/alipay.png" alt="" />
				</a>
			</a-space>
		</div>
	</template>
</template>

<script setup name="threeLogin">
	import { ref, computed } from 'vue'
	import configApi from '@/api/dev/configApi'
	import thirdApi from '@/api/auth/thirdApi'
	const formData = ref({})
	const hasAnyThirdLogin = computed(() => {
		return Object.values(formData.value).some((v) => v === true)
	})
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
	getConfigSysThirdAllowFlagList()
</script>
<style scoped>
	.bind-icon {
		font-size: 32px;
	}
</style>
