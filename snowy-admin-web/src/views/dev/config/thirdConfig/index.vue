<template>
	<div class="third-config-container">
		<div class="third-config-left">
			<a-collapse v-model:activeKey="collapseKeys" :bordered="false">
				<a-collapse-panel key="protocol" header="标准协议">
					<a-menu v-model:selectedKeys="selectedKeys" mode="inline" :bordered="false" @click="onMenuClick">
						<a-menu-item key="oauthThird">
							<img src="@/assets/images/authSource/oauth.png" class="third-icon" />
							OAUTH
						</a-menu-item>
						<a-menu-item key="oidcThird">
							<img src="@/assets/images/authSource/oidc.png" class="third-icon" />
							OIDC
						</a-menu-item>
						<a-menu-item key="jwtThird">
							<img src="@/assets/images/authSource/jwt.png" class="third-icon" />
							JWT
						</a-menu-item>
						<a-menu-item key="casThird">
							<img src="@/assets/images/authSource/cas.png" class="third-icon" />
							CAS
						</a-menu-item>
						<a-menu-item key="samlThird">
							<img src="@/assets/images/authSource/saml.png" class="third-icon" />
							SAML
						</a-menu-item>
						<a-menu-item key="iamThird">
							<img src="@/assets/images/authSource/iam.png" class="third-icon" />
							IAM
						</a-menu-item>
					</a-menu>
				</a-collapse-panel>
				<a-collapse-panel key="social" header="社交平台">
					<a-menu v-model:selectedKeys="selectedKeys" mode="inline" :bordered="false" @click="onMenuClick">
						<a-menu-item key="wechatThird">
							<img src="@/assets/images/authSource/wechat.png" class="third-icon" />
							微信
						</a-menu-item>
						<a-menu-item key="wechatMiniThird">
							<img src="@/assets/images/authSource/wechatmini.png" class="third-icon" />
							微信小程序
						</a-menu-item>
						<a-menu-item key="dingTalkThird">
							<img src="@/assets/images/authSource/dingtalk.png" class="third-icon" />
							钉钉
						</a-menu-item>
						<a-menu-item key="workWechatThird">
							<img src="@/assets/images/authSource/workwechat.png" class="third-icon" />
							企业微信
						</a-menu-item>
						<a-menu-item key="feiShuThird">
							<img src="@/assets/images/authSource/feishu.png" class="third-icon" />
							飞书
						</a-menu-item>
						<a-menu-item key="weLinkThird">
							<img src="@/assets/images/authSource/welink.png" class="third-icon" />
							WeLink
						</a-menu-item>
						<a-menu-item key="yunZhiJiaThird">
							<img src="@/assets/images/authSource/yunzhijia.png" class="third-icon" />
							云之家
						</a-menu-item>
						<a-menu-item key="qqThird">
							<img src="@/assets/images/authSource/qq.png" class="third-icon" />
							QQ
						</a-menu-item>
						<a-menu-item key="weiboThird">
							<img src="@/assets/images/authSource/weibo.png" class="third-icon" />
							微博
						</a-menu-item>
						<a-menu-item key="douYinThird">
							<img src="@/assets/images/authSource/douyin.png" class="third-icon" />
							抖音
						</a-menu-item>
						<a-menu-item key="alipayThird">
							<img src="@/assets/images/authSource/alipay.png" class="third-icon" />
							支付宝
						</a-menu-item>
					</a-menu>
				</a-collapse-panel>
			</a-collapse>
		</div>
		<div class="third-config-right">
			<oauthThirdForm v-if="activeForm === 'oauthThird'" />
			<oidcThirdForm v-else-if="activeForm === 'oidcThird'" />
			<jwtThirdForm v-else-if="activeForm === 'jwtThird'" />
			<casThirdForm v-else-if="activeForm === 'casThird'" />
			<samlThirdForm v-else-if="activeForm === 'samlThird'" />
			<iamThirdForm v-else-if="activeForm === 'iamThird'" />
			<wechatThirdForm v-else-if="activeForm === 'wechatThird'" />
			<wechatMiniThirdForm v-else-if="activeForm === 'wechatMiniThird'" />
			<dingTalkThirdForm v-else-if="activeForm === 'dingTalkThird'" />
			<workWechatThirdForm v-else-if="activeForm === 'workWechatThird'" />
			<feiShuThirdForm v-else-if="activeForm === 'feiShuThird'" />
			<weLinkThirdForm v-else-if="activeForm === 'weLinkThird'" />
			<yunZhiJiaThirdForm v-else-if="activeForm === 'yunZhiJiaThird'" />
			<qqThirdForm v-else-if="activeForm === 'qqThird'" />
			<weiboThirdForm v-else-if="activeForm === 'weiboThird'" />
			<DouYinThirdForm v-else-if="activeForm === 'douYinThird'" />
			<alipayThirdForm v-else-if="activeForm === 'alipayThird'" />
		</div>
	</div>
</template>

<script setup name="thirdConfig">
	import OauthThirdForm from './oauthThirdForm.vue'
	import OidcThirdForm from './oidcThirdForm.vue'
	import JwtThirdForm from './jwtThirdForm.vue'
	import CasThirdForm from './casThirdForm.vue'
	import SamlThirdForm from './samlThirdForm.vue'
	import IamThirdForm from './iamThirdForm.vue'
	import WechatThirdForm from './wechatThirdForm.vue'
	import WechatMiniThirdForm from './wechatMiniThirdForm.vue'
	import DingTalkThirdForm from './dingTalkThirdForm.vue'
	import WorkWechatThirdForm from './workWechatThirdForm.vue'
	import FeiShuThirdForm from './feiShuThirdForm.vue'
	import WeLinkThirdForm from './weLinkThirdForm.vue'
	import YunZhiJiaThirdForm from './yunZhiJiaThirdForm.vue'
	import QqThirdForm from './qqThirdForm.vue'
	import WeiboThirdForm from './weiboThirdForm.vue'
	import DouYinThirdForm from './douYinThirdForm.vue'
	import AlipayThirdForm from './alipayThirdForm.vue'

	const collapseKeys = ref(['protocol', 'social'])
	const selectedKeys = ref(['oauthThird'])
	const activeForm = ref('oauthThird')

	const onMenuClick = ({ key }) => {
		activeForm.value = key
		selectedKeys.value = [key]
	}
</script>

<style scoped>
	.third-config-container {
		display: flex;
		height: 100%;
	}
	.third-config-left {
		width: 180px;
		flex-shrink: 0;
		border-right: 1px solid #f0f0f0;
		overflow-y: auto;
		scrollbar-width: none;
		-ms-overflow-style: none;
	}
	.third-config-left::-webkit-scrollbar {
		display: none;
	}
	.third-config-right {
		flex: 1;
		padding-left: 24px;
		overflow-y: auto;
		scrollbar-width: none;
		-ms-overflow-style: none;
	}
	.third-config-right::-webkit-scrollbar {
		display: none;
	}
	.third-icon {
		width: 20px;
		height: 20px;
		margin-right: 8px;
		vertical-align: middle;
	}
	.third-config-left :deep(.ant-collapse-content-box) {
		padding: 0 !important;
	}
	.third-config-left :deep(.ant-menu) {
		border-right: none;
	}
	.third-config-left :deep(.ant-menu-item) {
		height: 36px;
		line-height: 36px;
	}
</style>
