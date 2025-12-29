<template>
	<a-card
		class="xn-wd"
		:bordered="false"
		:tab-list="tabListNoTitle"
		:active-tab-key="noTitleKey"
		@tabChange="(key) => onTabChange(key, 'noTitleKey')"
	>
		<p v-if="noTitleKey === 'sysConfig'">
			<SysConfig />
		</p>
		<p v-else-if="noTitleKey === 'registerConfig'">
			<RegisterConfig />
		</p>
		<p v-else-if="noTitleKey === 'loginConfig'">
			<LoginConfig />
		</p>
		<p v-else-if="noTitleKey === 'passwordConfig'">
			<PasswordConfig />
		</p>
		<p v-else-if="noTitleKey === 'emailConfig'">
			<EmailConfig />
		</p>
		<p v-else-if="noTitleKey === 'emailTemplateConfig'">
			<EmailTemplateConfig />
		</p>
		<p v-else-if="noTitleKey === 'smsConfig'">
			<SmsConfig />
		</p>
		<p v-else-if="noTitleKey === 'smsTemplateConfig'">
			<SmsTemplateConfig />
		</p>
		<p v-else-if="noTitleKey === 'fileConfig'">
			<FileConfig />
		</p>
		<p v-else-if="noTitleKey === 'pushConfig'">
			<PushConfig />
		</p>
		<p v-else-if="noTitleKey === 'thirdConfig'">
			<ThirdConfig />
		</p>
		<p v-else-if="noTitleKey === 'otherConfig'">
			<OtherConfig />
		</p>
	</a-card>
</template>

<script setup name="devConfig">
	import SysConfig from './sysConfig.vue'
	import RegisterConfig from './registerConfig/index.vue'
	import LoginConfig from './loginConfig/index.vue'
	import PasswordConfig from './passwordConfig/index.vue'
	import EmailConfig from './emailConfig/index.vue'
	import EmailTemplateConfig from './emailTemplateConfig/index.vue'
	import SmsConfig from './smsConfig/index.vue'
	import SmsTemplateConfig from './smsTemplateConfig/index.vue'
	import FileConfig from './fileConfig/index.vue'
	import ThirdConfig from './thirdConfig/index.vue'
	import OtherConfig from './otherConfig/index.vue'
	import PushConfig from './pushConfig/index.vue'
	const key = ref('sysConfig')
	const noTitleKey = ref('sysConfig')

	const tabListNoTitle = [
		{
			key: 'sysConfig',
			tab: '系统配置'
		},
		{
			key: 'registerConfig',
			tab: '注册配置'
		},
		{
			key: 'loginConfig',
			tab: '登录配置'
		},
		{
			key: 'passwordConfig',
			tab: '密码配置'
		},
		{
			key: 'emailConfig',
			tab: '邮件配置'
		},
		{
			key: 'emailTemplateConfig',
			tab: '邮件模板'
		},
		{
			key: 'smsConfig',
			tab: '短信配置'
		},
		{
			key: 'smsTemplateConfig',
			tab: '短信模板'
		},
		{
			key: 'fileConfig',
			tab: '文件配置'
		},
		{
			key: 'pushConfig',
			tab: '推送配置'
		},
		{
			key: 'thirdConfig',
			tab: '第三方配置'
		},
		{
			key: 'otherConfig',
			tab: '其他配置'
		}
	]

	const onTabChange = (value, type) => {
		if (type === 'key') {
			key.value = value
		} else if (type === 'noTitleKey') {
			noTitleKey.value = value
		}
	}
</script>
<style lang="less" scoped>
	:deep(.ant-tabs-tab) {
		font-size: 14px !important;
	}
</style>
