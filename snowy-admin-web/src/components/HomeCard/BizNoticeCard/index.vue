<template>
	<a-card :bordered="false" :title="title" :loading="apiLoading" class="notice-card">
		<template #extra>
			<a-button type="link" size="small" @click="leaveFor('/biz/notice')">更多</a-button>
		</template>
		<div class="notice-list">
			<a-table
				:columns="columns"
				:data-source="dataSource"
				size="small"
				:pagination="false"
				:showHeader="false"
				class="notice-table"
			>
				<template #bodyCell="{ column, record }">
					<template v-if="column.dataIndex === 'title'">
						<a-popover>
							<template #content>
								<div class="popover-content">
									<img v-if="record.image" :src="record.image" class="notice-img" />
									<div class="notice-detail">
										<p class="notice-digest">{{ record.digest }}</p>
										<div class="notice-footer">
											<a-button type="primary" size="small" @click="detailRef.onOpen(record.id)">详情</a-button>
										</div>
									</div>
								</div>
							</template>
							<div class="notice-title-wrapper">
								<a-badge :color="getBadgeColor(record.type)" />
								<span class="notice-title">{{ record.title }}</span>
							</div>
						</a-popover>
					</template>
					<template v-if="column.dataIndex === 'createTime'">
						<span class="notice-time">{{ $TOOL.parseTime(record.createTime, '{m}-{d}') }}</span>
					</template>
				</template>
			</a-table>
		</div>
		<detail ref="detailRef" />
	</a-card>
</template>

<script setup name="bizIndexNoticeCard">
	import bizIndexApi from '@/api/biz/bizIndexApi'
	import router from '@/router'
	import Detail from './detail.vue'
	import { onMounted, ref } from 'vue'

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
			width: 80
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

	const getBadgeColor = (type) => {
		const colors = {
			NOTICE: 'green',
			ANNOUNCEMENT: 'blue',
			WARNING: 'orange'
		}
		return colors[type] || 'blue'
	}
</script>

<style scoped lang="less">
	:deep(.ant-card-body) {
		padding-top: 0 !important;
	}
	.notice-list {
		height: 300px;
		overflow-y: auto;
		padding: 0 5px;
		&::-webkit-scrollbar {
			width: 4px;
		}
		&::-webkit-scrollbar-thumb {
			background: #e8e8e8;
			border-radius: 2px;
		}
	}
	.notice-table {
		:deep(.ant-table-cell) {
			padding: 10px 8px !important;
		}
	}
	.notice-title-wrapper {
		display: flex;
		align-items: center;
		cursor: pointer;
	}
	.notice-title {
		margin-left: 8px;
		color: #262626;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
		max-width: 200px;
	}
	:deep(.snowy-theme-dark) & {
		.notice-title {
			color: rgba(255, 255, 255, 0.85);
		}
		.notice-digest {
			color: rgba(255, 255, 255, 0.45);
		}
	}
	.notice-time {
		color: #8c8c8c;
		font-size: 12px;
	}
	.popover-content {
		display: flex;
		width: 350px;
	}
	.notice-img {
		width: 100px;
		height: 70px;
		object-fit: cover;
		border-radius: 4px;
	}
	.notice-detail {
		flex: 1;
		padding-left: 12px;
	}
	.notice-digest {
		font-size: 13px;
		color: #595959;
		margin-bottom: 8px;
		display: -webkit-box;
		-webkit-line-clamp: 2;
		-webkit-box-orient: vertical;
		overflow: hidden;
	}
	.notice-footer {
		text-align: right;
	}
</style>
