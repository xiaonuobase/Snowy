<template>
	<a-tabs v-model:activeKey="activeKey" tab-position="left">
		<a-tab-pane key="oauthThird">
			<template #tab>
				<span>
					<img
						src="@/assets/images/authSource/oauth.png"
						style="width: 20px; height: 20px; margin-right: 8px; vertical-align: middle"
					/>
					OAUTH
				</span>
			</template>
			<oauthThirdForm />
		</a-tab-pane>
		<a-tab-pane key="oidcThird">
			<template #tab>
				<span>
					<img
						src="@/assets/images/authSource/oidc.png"
						style="width: 20px; height: 20px; margin-right: 8px; vertical-align: middle"
					/>
					OIDC
				</span>
			</template>
			<oidcThirdForm />
		</a-tab-pane>
		<a-tab-pane key="jwtThird">
			<template #tab>
				<span>
					<img
						src="@/assets/images/authSource/jwt.png"
						style="width: 20px; height: 20px; margin-right: 8px; vertical-align: middle"
					/>
					JWT
				</span>
			</template>
			<jwtThirdForm />
		</a-tab-pane>
		<a-tab-pane key="casThird">
			<template #tab>
				<span>
					<img
						src="@/assets/images/authSource/cas.png"
						style="width: 20px; height: 20px; margin-right: 8px; vertical-align: middle"
					/>
					CAS
				</span>
			</template>
			<casThirdForm />
		</a-tab-pane>
		<a-tab-pane key="samlThird">
			<template #tab>
				<span>
					<img
						src="@/assets/images/authSource/saml.png"
						style="width: 20px; height: 20px; margin-right: 8px; vertical-align: middle"
					/>
					SAML
				</span>
			</template>
			<samlThirdForm />
		</a-tab-pane>
		<a-tab-pane key="iamThird">
			<template #tab>
				<span>
					<img
						src="@/assets/images/authSource/iam.png"
						style="width: 20px; height: 20px; margin-right: 8px; vertical-align: middle"
					/>
					IAM
				</span>
			</template>
			<iamThirdForm />
		</a-tab-pane>
		<a-tab-pane key="wechatThird">
			<template #tab>
				<span>
					<img
						src="@/assets/images/authSource/wechat.png"
						style="width: 20px; height: 20px; margin-right: 8px; vertical-align: middle"
					/>
					微信
				</span>
			</template>
			<wechatThirdForm />
		</a-tab-pane>
		<a-tab-pane key="wechatMiniThird">
			<template #tab>
				<span>
					<img
						src="@/assets/images/authSource/wechatmini.png"
						style="width: 20px; height: 20px; margin-right: 8px; vertical-align: middle"
					/>
					微信小程序
				</span>
			</template>
			<wechatMiniThirdForm />
		</a-tab-pane>
		<a-tab-pane key="dingTalkThird">
			<template #tab>
				<span>
					<img
						src="@/assets/images/authSource/dingtalk.png"
						style="width: 20px; height: 20px; margin-right: 8px; vertical-align: middle"
					/>
					钉钉
				</span>
			</template>
			<dingTalkThirdForm />
		</a-tab-pane>
		<a-tab-pane key="workWechatThird">
			<template #tab>
				<span>
					<img
						src="@/assets/images/authSource/workwechat.png"
						style="width: 20px; height: 20px; margin-right: 8px; vertical-align: middle"
					/>
					企业微信
				</span>
			</template>
			<workWechatThirdForm />
		</a-tab-pane>
		<a-tab-pane key="feiShuThird">
			<template #tab>
				<span>
					<img
						src="@/assets/images/authSource/feishu.png"
						style="width: 20px; height: 20px; margin-right: 8px; vertical-align: middle"
					/>
					飞书
				</span>
			</template>
			<feiShuThirdForm />
		</a-tab-pane>
		<a-tab-pane key="weLinkThird">
			<template #tab>
				<span>
					<img
						src="@/assets/images/authSource/welink.png"
						style="width: 20px; height: 20px; margin-right: 8px; vertical-align: middle"
					/>
					WeLink
				</span>
			</template>
			<weLinkThirdForm />
		</a-tab-pane>
		<a-tab-pane key="yunZhiJiaThird">
			<template #tab>
				<span>
					<img
						src="@/assets/images/authSource/yunzhijia.png"
						style="width: 20px; height: 20px; margin-right: 8px; vertical-align: middle"
					/>
					云之家
				</span>
			</template>
			<yunZhiJiaThirdForm />
		</a-tab-pane>
		<a-tab-pane key="qqThird">
			<template #tab>
				<span>
					<img
						src="@/assets/images/authSource/qq.png"
						style="width: 20px; height: 20px; margin-right: 8px; vertical-align: middle"
					/>
					QQ
				</span>
			</template>
			<qqThirdForm />
		</a-tab-pane>
		<a-tab-pane key="weiboThird">
			<template #tab>
				<span>
					<img
						src="@/assets/images/authSource/weibo.png"
						style="width: 20px; height: 20px; margin-right: 8px; vertical-align: middle"
					/>
					微博
				</span>
			</template>
			<weiboThirdForm />
		</a-tab-pane>
		<a-tab-pane key="douYinThird">
			<template #tab>
				<span>
					<img
						src="@/assets/images/authSource/douyin.png"
						style="width: 20px; height: 20px; margin-right: 8px; vertical-align: middle"
					/>
					抖音
				</span>
			</template>
			<DouYinThirdForm />
		</a-tab-pane>
		<a-tab-pane key="alipayThird">
			<template #tab>
				<span>
					<img
						src="@/assets/images/authSource/alipay.png"
						style="width: 20px; height: 20px; margin-right: 8px; vertical-align: middle"
					/>
					支付宝
				</span>
			</template>
			<alipayThirdForm />
		</a-tab-pane>
	</a-tabs>
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
	const activeKey = ref('oauthThird')
</script>
