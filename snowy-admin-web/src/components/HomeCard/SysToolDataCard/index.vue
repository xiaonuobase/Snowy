<template>
	<a-card :title="title" :bordered="false" :loading="apiLoading" class="tool-data-card">
		<a-row :gutter="10">
			<a-col :span="12" v-for="item in statList" :key="item.key">
				<div class="stat-item">
					<div class="stat-icon" :class="item.colorClass">
						<component :is="item.icon" />
					</div>
					<a-statistic :value="dataSource[item.key]" :title="item.label" />
				</div>
			</a-col>
		</a-row>
	</a-card>
</template>

<script setup name="sysToolDataCard">
	import indexApi from '@/api/sys/indexApi'
	import { FileFilled, MessageFilled, MailFilled, NotificationFilled } from '@ant-design/icons-vue'
	import { onMounted, ref } from 'vue'

	const title = ref('基础工具')
	const apiLoading = ref(false)
	const dataSource = ref({
		fileCount: 0,
		smsCount: 0,
		emailCount: 0,
		messageCount: 0
	})

	const statList = [
		{ label: '文件', key: 'fileCount', icon: FileFilled, colorClass: 'stat-icon-blue' },
		{ label: '短信', key: 'smsCount', icon: MessageFilled, colorClass: 'stat-icon-green' },
		{ label: '邮件', key: 'emailCount', icon: MailFilled, colorClass: 'stat-icon-orange' },
		{ label: '站内信', key: 'messageCount', icon: NotificationFilled, colorClass: 'stat-icon-red' }
	]

	onMounted(() => {
		apiLoading.value = true
		indexApi
			.indexToolDataCount()
			.then((data) => {
				dataSource.value = data
			})
			.catch(() => {})
			.finally(() => {
				apiLoading.value = false
			})
	})
</script>

<style scoped lang="less">
	:deep(.ant-card-body) {
		padding-top: 0 !important;
	}
	.stat-item {
		display: flex;
		align-items: center;
		padding: 12px;
		background: var(--background-color-light);
		transition: all 0.3s;
		margin-bottom: 10px;
	}
	.stat-icon {
		width: 36px;
		height: 36px;
		border-radius: 6px;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 18px;
		margin-right: 12px;
	}
	.stat-icon-blue {
		background-color: var(--blue-1);
		color: var(--blue-6);
	}
	.stat-icon-green {
		background-color: var(--green-1);
		color: var(--green-6);
	}
	.stat-icon-orange {
		background-color: var(--orange-1);
		color: var(--orange-6);
	}
	.stat-icon-red {
		background-color: var(--red-1);
		color: var(--red-5);
	}
	:deep(.ant-statistic-title) {
		font-size: 13px;
		color: var(--text-color-secondary);
		margin-bottom: 2px;
	}
	:deep(.ant-statistic-content-value) {
		font-size: 22px;
		font-weight: 700;
		color: var(--text-color);
	}
</style>
