<template>
	<xn-panel :padding="0" :header-divider="false" :bottom-gap="0">
		<template #title>
			<a-tabs v-model:activeKey="noTitleKey" class="config-tabs">
				<a-tab-pane v-for="item in tabListNoTitle" :key="item.key" :tab="item.tab" />
			</a-tabs>
		</template>
		<div class="config-content">
			<SysConfig v-if="noTitleKey === 'sysConfig'" />
			<RegisterConfig v-else-if="noTitleKey === 'registerConfig'" />
			<LoginConfig v-else-if="noTitleKey === 'loginConfig'" />
			<PasswordConfig v-else-if="noTitleKey === 'passwordConfig'" />
			<EmailConfig v-else-if="noTitleKey === 'emailConfig'" />
			<EmailTemplateConfig v-else-if="noTitleKey === 'emailTemplateConfig'" />
			<SmsConfig v-else-if="noTitleKey === 'smsConfig'" />
			<SmsTemplateConfig v-else-if="noTitleKey === 'smsTemplateConfig'" />
			<FileConfig v-else-if="noTitleKey === 'fileConfig'" />
			<PushConfig v-else-if="noTitleKey === 'pushConfig'" />
			<ThirdConfig v-else-if="noTitleKey === 'thirdConfig'" />
			<OtherConfig v-else-if="noTitleKey === 'otherConfig'" />
		</div>
	</xn-panel>
</template>

<script setup name="devConfig">
	import { onMounted, onUnmounted } from 'vue'
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

	const noTitleKey = ref('sysConfig')

	const tabListNoTitle = [
		{ key: 'sysConfig', tab: '系统配置' },
		{ key: 'registerConfig', tab: '注册配置' },
		{ key: 'loginConfig', tab: '登录配置' },
		{ key: 'passwordConfig', tab: '密码配置' },
		{ key: 'emailConfig', tab: '邮件配置' },
		{ key: 'emailTemplateConfig', tab: '邮件模板' },
		{ key: 'smsConfig', tab: '短信配置' },
		{ key: 'smsTemplateConfig', tab: '短信模板' },
		{ key: 'fileConfig', tab: '文件配置' },
		{ key: 'pushConfig', tab: '推送配置' },
		{ key: 'thirdConfig', tab: '第三方配置' },
		{ key: 'otherConfig', tab: '其他配置' }
	]

	// AntdV 4.2.6 在标签页“更多”按钮上写死了 aria-hidden="true"，点击后该按钮会获得焦点，
	let ariaObserver = null
	const removeNavMoreAriaHidden = () => {
		document.querySelectorAll('.config-tabs .ant-tabs-nav-more[aria-hidden]').forEach((btn) => {
			btn.removeAttribute('aria-hidden')
		})
	}
	onMounted(() => {
		removeNavMoreAriaHidden()
		const tabsEl = document.querySelector('.config-tabs')
		if (tabsEl) {
			ariaObserver = new MutationObserver(removeNavMoreAriaHidden)
			ariaObserver.observe(tabsEl, { subtree: true, attributes: true, attributeFilter: ['aria-hidden'] })
		}
	})
	onUnmounted(() => {
		ariaObserver?.disconnect()
		ariaObserver = null
	})
</script>

<style lang="less" scoped>
	:deep(.xn-panel-title) {
		padding: 0 !important;
		flex: 1;
		/* flex 子项默认 min-width:auto 会被内容撑开，导致 tabs 量不出溢出、窄屏下不出现左右滚动按钮 */
		min-width: 0;
		overflow: hidden;
	}
	.config-tabs {
		margin-bottom: 0;
		width: 100%;
		min-width: 0;
	}
	.config-tabs :deep(.ant-tabs-nav) {
		margin-bottom: 0;
		padding-left: 24px;
	}
	.config-tabs :deep(.ant-tabs-tab) {
		font-size: 14px !important;
	}
	.config-content {
		padding: 16px 24px;
		height: 100%;
	}
</style>
