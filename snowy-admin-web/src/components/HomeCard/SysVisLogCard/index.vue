<template>
	<a-card :title="title" :bordered="false" :loading="apiLoading" class="vis-log-card">
		<template #extra v-if="displayMore()">
			<a-button type="link" size="small" @click="leaveFor('/dev/vislog')">更多</a-button>
		</template>
		<div class="timeline-div">
			<a-timeline>
				<a-timeline-item :key="visLog.id" v-for="visLog in visLogList" :color="getTimelineColor(visLog.category)">
					<div class="log-item">
						<div class="log-header">
							<span class="log-name">{{ visLog.name }}</span>
							<span class="log-time">{{ $TOOL.parseTime(visLog.opTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
						</div>
						<p class="log-address">{{ visLog.opIp }} {{ visLog.opAddress }}</p>
					</div>
				</a-timeline-item>
			</a-timeline>
		</div>
	</a-card>
</template>

<script setup name="indexVisLog">
	import router from '@/router'
	import indexApi from '@/api/sys/indexApi'
	import { onMounted, ref } from 'vue'
	import tool from '@/utils/tool'
	const userInfo = tool.data.get('USER_INFO')
	const visLogList = ref([])
	const title = ref('访问记录')
	const apiLoading = ref(false)
	onMounted(() => {
		// 进来后执行查询
		getVisLogList()
	})
	// 是否展示更多按钮
	const displayMore = () => {
		return userInfo.roleCodeList && userInfo.roleCodeList.toString().indexOf('superAdmin') !== -1
	}
	// 查询数据
	const getVisLogList = () => {
		apiLoading.value = true
		indexApi
			.indexVisLogList()
			.then((data) => {
				visLogList.value = data
			})
			.catch(() => {})
			.finally(() => {
				apiLoading.value = false
			})
	}
	// 跳转
	const leaveFor = (url = '/') => {
		router.replace({
			path: url
		})
	}
	const getTimelineColor = (value) => {
		if (value === 'LOGOUT') return 'gray'
		return 'blue'
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
	.log-address {
		margin-bottom: 0;
		font-size: 12px;
		color: var(--disabled-color);
	}
	:deep(.ant-timeline-item-content) {
		margin-left: 25px;
	}
</style>
