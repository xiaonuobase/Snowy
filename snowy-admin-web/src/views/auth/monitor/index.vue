<template>
	<analyse />
	<a-card :bordered="false" :body-style="{ 'padding-top': '10px' }">
		<a-tabs v-model:activeKey="activeKey">
			<a-tab-pane key="1" tab="B端会话">
				<monitor-b-tab />
			</a-tab-pane>
			<a-tab-pane key="2" tab="C端会话" force-render>
				<monitor-c-tab />
			</a-tab-pane>
		</a-tabs>
	</a-card>
</template>

<script setup name="authMonitor">
	import analyse from './analyse.vue'
	import monitorBTab from './bTab.vue'
	import monitorCTab from './cTab.vue'
	const activeKey = ref('1')
</script>
