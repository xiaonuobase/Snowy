<template>
	<analyse />
	<xn-panel>
		<a-tabs v-model:activeKey="activeKey">
			<a-tab-pane key="1" tab="B端会话">
				<monitor-b-tab />
			</a-tab-pane>
			<a-tab-pane key="2" tab="C端会话" force-render>
				<monitor-c-tab />
			</a-tab-pane>
		</a-tabs>
	</xn-panel>
</template>

<script setup name="authMonitor">
	import Analyse from './analyse.vue'
	import MonitorBTab from './bTab.vue'
	import MonitorCTab from './cTab.vue'
	const activeKey = ref('1')
</script>
