<template>
	<a-tabs v-model:activeKey="activeKey" tab-position="left">
		<a-tab-pane key="dingTalk">
			<template #tab>
				<span>
					<img
						src="@/assets/images/authSource/dingtalk.png"
						style="width: 20px; height: 20px; margin-right: 8px; vertical-align: middle"
					/>
					钉钉
				</span>
			</template>
			<ding-talk-form />
		</a-tab-pane>
		<a-tab-pane key="feishu">
			<template #tab>
				<span>
					<img
						src="@/assets/images/authSource/feishu.png"
						style="width: 20px; height: 20px; margin-right: 8px; vertical-align: middle"
					/>
					飞书
				</span>
			</template>
			<feishu-form />
		</a-tab-pane>
		<a-tab-pane key="workWechat">
			<template #tab>
				<span>
					<img
						src="@/assets/images/authSource/workwechat.png"
						style="width: 20px; height: 20px; margin-right: 8px; vertical-align: middle"
					/>
					企业微信
				</span>
			</template>
			<work-wechat-form />
		</a-tab-pane>
	</a-tabs>
</template>

<script setup name="pushConfig">
	import WorkWechatForm from './workWechatForm.vue'
	import DingTalkForm from './dingTalkForm.vue'
	import FeishuForm from './feishuForm.vue'
	const activeKey = ref('dingTalk')
</script>
