<template>
	<a-card :title="title" :bordered="false" :loading="apiLoading" class="biz-data-card">
		<a-row :gutter="16">
			<a-col :xs="12" :sm="12" :md="12" :lg="6" :xl="6">
				<div class="stat-item">
					<div class="stat-icon stat-icon-blue">
						<user-outlined />
					</div>
					<a-statistic :value="dataSource.userCount" title="用户数量" />
				</div>
			</a-col>
			<a-col :xs="12" :sm="12" :md="12" :lg="12" :xl="6">
				<div class="stat-item">
					<div class="stat-icon stat-icon-orange">
						<cluster-outlined />
					</div>
					<a-statistic :value="dataSource.orgCount" title="组织数量" />
				</div>
			</a-col>
			<a-col :xs="12" :sm="12" :md="12" :lg="12" :xl="6">
				<div class="stat-item">
					<div class="stat-icon stat-icon-red">
						<apartment-outlined />
					</div>
					<a-statistic :value="dataSource.positionCount" title="职位数量" />
				</div>
			</a-col>
			<a-col :xs="12" :sm="12" :md="12" :lg="12" :xl="6">
				<div class="stat-item">
					<div class="stat-icon stat-icon-green">
						<deployment-unit-outlined />
					</div>
					<a-statistic :value="dataSource.roleCount" title="角色数量" />
				</div>
			</a-col>
		</a-row>
	</a-card>
</template>

<script setup name="sysBizDataCard">
	import indexApi from '@/api/sys/indexApi'
	import { UserOutlined, ClusterOutlined, ApartmentOutlined, DeploymentUnitOutlined } from '@ant-design/icons-vue'
	import { onMounted, ref } from 'vue'

	const title = ref('业务数据')
	const apiLoading = ref(false)
	const dataSource = ref({
		userCount: 0,
		roleCount: 0,
		orgCount: 0,
		positionCount: 0
	})
	onMounted(() => {
		apiLoading.value = true
		indexApi
			.indexBizDataCount()
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
		padding: 10px;
		background: var(--background-color-light);
		transition: all 0.3s;
	}
	.stat-icon {
		width: 48px;
		height: 48px;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 24px;
		margin-right: 16px;
	}
	.stat-icon-blue {
		background-color: var(--blue-1);
		color: var(--blue-6);
	}
	.stat-icon-orange {
		background-color: var(--orange-1);
		color: var(--orange-6);
	}
	.stat-icon-red {
		background-color: var(--red-1);
		color: var(--red-5);
	}
	.stat-icon-green {
		background-color: var(--green-1);
		color: var(--green-6);
	}
	:deep(.ant-statistic-title) {
		font-size: 14px;
		color: var(--text-color-secondary);
		margin-bottom: 4px;
	}
	:deep(.ant-statistic-content-value) {
		font-size: 26px;
		font-weight: 700;
		color: var(--text-color);
	}
</style>
