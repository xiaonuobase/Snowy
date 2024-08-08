<template>
	<a-card :bordered="false" :title="title" :loading="apiLoading">
		<template #extra><a @click="leaveFor('/biz/notice')">更多</a></template>
		<a-table
			:columns="columns"
			:data-source="dataSource"
			size="small"
			:pagination="false"
			:showHeader="false"
			:scroll="{ y: 260 }"
		>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'title'">
					<a-popover>
						<template #content>
							<a-flex>
								<img v-if="record.image" :src="record.image" style="width: 100px; height: 70px" />
								<div style="padding-left: 10px; width: 300px; max-height: 100px; text-overflow: ellipsis">
									{{ record.digest }}
									<div style="float: right; padding-right: 10px">
										<a-button type="primary" size="small" @click="detailRef.onOpen(record.id)">详情</a-button>
									</div>
								</div>
							</a-flex>
						</template>
						<div v-if="record.type === 'NOTICE'">
							<a-badge color="green" />
							{{ record.title }}
						</div>
						<div v-if="record.type === 'ANNOUNCEMENT'">
							<a-badge color="blue" />
							{{ record.title }}
						</div>
						<div v-if="record.type === 'WARNING'">
							<a-badge color="orange" />
							{{ record.title }}
						</div>
					</a-popover>
				</template>
			</template>
		</a-table>
		<detail ref="detailRef" />
	</a-card>
</template>

<script setup name="bizIndexNoticeCard">
	import bizIndexApi from '@/api/biz/bizIndexApi'
	import router from '@/router'
	import Detail from './detail.vue'
	const detailRef = ref()
	const columns = [
		{
			title: '名称',
			dataIndex: 'title',
			align: 'left'
		},
		{
			title: '时间',
			dataIndex: 'createTime',
			align: 'right',
			width: '150px'
		}
	]
	const title = ref('通知公告')
	const dataSource = ref([])
	const apiLoading = ref(false)
	onMounted(() => {
		apiLoading.value = true
		bizIndexApi
			.bizIndexNoticeList()
			.then((data) => {
				dataSource.value = data
			})
			.catch(() => {})
			.finally(() => {
				apiLoading.value = false
			})
	})
	const leaveFor = (url = '/') => {
		router.replace({
			path: url
		})
	}
</script>
<style scoped>
	:deep(.ant-card-body) {
		padding-top: 0 !important;
	}
</style>
