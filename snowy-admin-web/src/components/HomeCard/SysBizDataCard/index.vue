<template>
	<a-card :title="title" :bordered="false">
		<a-row>
			<a-col :span="6">
				<a-statistic :value="dataSource.userCount">
					<template #title>
						<user-outlined style="color: #1890ff" />
						用户数量
					</template>
				</a-statistic>
			</a-col>
			<a-col :span="6">
				<a-statistic :value="dataSource.orgCount">
					<template #title>
						<cluster-outlined style="color: rgba(229, 159, 18, 0.35)" />
						组织数量
					</template>
				</a-statistic>
			</a-col>
			<a-col :span="6">
				<a-statistic :value="dataSource.positionCount">
					<template #title>
						<apartment-outlined style="color: rgba(245, 6, 6, 0.2)" />
						职位数量
					</template>
				</a-statistic>
			</a-col>
			<a-col :span="6">
				<a-statistic :value="dataSource.roleCount">
					<template #title>
						<deployment-unit-outlined style="color: #09c755" />
						角色数量
					</template>
				</a-statistic>
			</a-col>
		</a-row>
	</a-card>
</template>

<script setup name="sysBizDataCard">
	import indexApi from '@/api/sys/indexApi'
	const title = ref('业务数据')
	const dataSource = ref({
		userCount: 0,
		roleCount: 0,
		orgCount: 0,
		positionCount: 0
	})
	onMounted(() => {
		indexApi.indexBizDataCount().then((data) => {
			dataSource.value = data
		})
	})
</script>

<style scoped>
	:deep(.ant-card-body) {
		padding-top: 0 !important;
	}
</style>
