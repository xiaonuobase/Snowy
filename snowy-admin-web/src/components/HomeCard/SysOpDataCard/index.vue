<template>
	<a-card :title="title" :bordered="false" :loading="apiLoading" class="op-data-card">
		<div class="stat-grid">
			<div class="stat-item" v-for="item in statList" :key="item.key">
				<div class="stat-icon-wrapper" :class="item.colorClass">
					<component :is="item.icon" />
				</div>
				<div class="stat-label">{{ item.label }}</div>
				<div class="stat-value">{{ dataSource[item.key] }}</div>
			</div>
		</div>
	</a-card>
</template>

<script setup name="sysOpDataCard">
	import indexApi from '@/api/sys/indexApi'
	import { ReadOutlined, UsergroupDeleteOutlined, UserSwitchOutlined, TeamOutlined } from '@ant-design/icons-vue'
	import { onMounted, ref } from 'vue'

	const title = ref('运维一览')
	const apiLoading = ref(false)
	const dataSource = ref({
		sysDictCount: 0,
		bizDictCount: 0,
		backUserSessionCount: 0,
		clientUserSessionCount: 0,
		thirdUserCount: 0
	})

	const statList = [
		{ label: '系统字典', key: 'sysDictCount', icon: ReadOutlined, colorClass: 'stat-icon-blue' },
		{ label: '业务字典', key: 'bizDictCount', icon: ReadOutlined, colorClass: 'stat-icon-purple' },
		{ label: '后台在线', key: 'backUserSessionCount', icon: UsergroupDeleteOutlined, colorClass: 'stat-icon-green' },
		{ label: '前台在线', key: 'clientUserSessionCount', icon: UserSwitchOutlined, colorClass: 'stat-icon-orange' },
		{ label: '三方用户', key: 'thirdUserCount', icon: TeamOutlined, colorClass: 'stat-icon-pink' }
	]

	onMounted(() => {
		apiLoading.value = true
		indexApi
			.indexOpDataCount()
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
	.stat-grid {
		display: grid;
		grid-template-columns: repeat(3, 1fr);
		gap: 8px;
	}
	.stat-item {
		display: flex;
		flex-direction: column;
		align-items: center;
		padding: 8px 4px;
		border-radius: 8px;
		background: var(--background-color-light);
		transition: all 0.3s;
	}
	.stat-icon-wrapper {
		width: 32px;
		height: 32px;
		border-radius: 6px;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 16px;
		margin-bottom: 4px;
	}
	.stat-icon-blue {
		background-color: var(--blue-1);
		color: var(--blue-6);
	}
	.stat-icon-purple {
		background-color: var(--purple-1);
		color: var(--purple-6);
	}
	.stat-icon-green {
		background-color: var(--green-1);
		color: var(--green-6);
	}
	.stat-icon-orange {
		background-color: var(--orange-1);
		color: var(--orange-6);
	}
	.stat-icon-pink {
		background-color: var(--pink-1);
		color: var(--pink-6);
	}
	.stat-label {
		font-size: 13px;
		color: var(--text-color-secondary);
		margin-bottom: 2px;
		text-align: center;
	}
	.stat-value {
		font-size: 20px;
		font-weight: 700;
		color: var(--text-color);
	}
</style>
