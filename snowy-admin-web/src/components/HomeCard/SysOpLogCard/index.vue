<template>
	<a-card :title="title" :bordered="false" :loading="apiLoading" class="op-log-card">
		<template #extra v-if="displayMore()">
			<a-button type="link" size="small" @click="leaveFor('/dev/oplog')">更多</a-button>
		</template>
		<div class="timeline-div">
			<a-timeline>
				<a-timeline-item :key="opLog.id" v-for="opLog in opLogList" :color="getTimelineColor(opLog.exeStatus)">
					<div class="log-item">
						<div class="log-header">
							<span class="log-name">{{ opLog.name }}</span>
							<span class="log-time">{{ opLog.opTime }}</span>
						</div>
						<a-tag :color="opLog.exeStatus === 'SUCCESS' ? 'success' : 'error'" size="small">
							{{ opLog.exeStatus === 'SUCCESS' ? '成功' : '失败' }}
						</a-tag>
					</div>
				</a-timeline-item>
			</a-timeline>
		</div>
	</a-card>
</template>

<script setup name="indexOpLog">
	import router from '@/router'
	import indexApi from '@/api/sys/indexApi'
	import { onMounted, ref } from 'vue'
	import tool from '@/utils/tool'
	const userInfo = tool.data.get('USER_INFO')
	const opLogList = ref([])
	const title = ref('操作记录')
	const apiLoading = ref(false)
	onMounted(() => {
		// 进来后执行查询
		getOpLogList()
	})

	// 是否展示更多按钮
	const displayMore = () => {
		return userInfo.roleCodeList && userInfo.roleCodeList.toString().indexOf('superAdmin') !== -1
	}
	const getOpLogList = () => {
		apiLoading.value = true
		indexApi
			.indexOpLogList()
			.then((data) => {
				opLogList.value = data
			})
			.catch(() => {})
			.finally(() => {
				apiLoading.value = false
			})
	}
	const leaveFor = (url = '/') => {
		router.replace({
			path: url
		})
	}
	const getTimelineColor = (value) => {
		return value === 'SUCCESS' ? 'green' : 'red'
	}
</script>
<style scoped lang="less">
	:deep(.ant-card-body) {
		padding-top: 0 !important;
	}
	.timeline-div {
		height: 330px;
		overflow-y: auto;
		padding: 10px 5px;
		/* 自定义滚动条 */
		&::-webkit-scrollbar {
			width: 4px;
		}
		&::-webkit-scrollbar-thumb {
			background: var(--border-color-split);
			border-radius: 2px;
		}
	}
	.log-item {
		margin-bottom: 5px;
	}
	.log-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 4px;
	}
	.log-name {
		font-weight: 500;
		color: var(--text-color);
	}
	.log-time {
		font-size: 12px;
		color: var(--text-color-secondary);
	}
	:deep(.ant-timeline-item-content) {
		margin-left: 25px;
	}
</style>
