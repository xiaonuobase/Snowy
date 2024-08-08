<template>
	<a-card>
		<a-tabs size="large" v-model:activeKey="activeKey">
			<a-tab-pane v-for="item in tabListNoTitle" :key="item.key" :tab="item.tab">
				<category :type="item.key" />
			</a-tab-pane>
		</a-tabs>
	</a-card>
</template>

<script setup name="devDict">
	import Category from './category/index.vue'
	const activeKey = ref('FRM')
	const tabListNoTitle = ref([
		{ key: 'FRM', tab: '系统字典' },
		{ key: 'BIZ', tab: '业务字典' }
	])
</script>
<style lang="less" scoped>
	:deep(.ant-card-body) {
		padding-top: 0 !important;
	}
</style>
