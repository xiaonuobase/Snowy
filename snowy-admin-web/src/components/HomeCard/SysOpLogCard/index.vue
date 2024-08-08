<template>
	<a-card :title="title" :bordered="false" :loading="apiLoading">
		<template #extra v-if="displayMore()"><a @click="leaveFor('/dev/oplog')">更多</a></template>
		<div class="timeline-div">
			<a-timeline>
				<a-timeline-item :key="opLog.id" v-for="opLog in opLogList" :color="getTimelineColor(opLog.exeStatus)"
					>{{ opLog.opTime }} {{ opLog.name }}
				</a-timeline-item>
			</a-timeline>
		</div>
	</a-card>
</template>

<script setup name="indexOpLog">
	import router from '@/router'
	import indexApi from '@/api/sys/indexApi'
	import { onMounted } from 'vue'
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
	// 获取颜色
	const getTimelineColor = (value) => {
		if (value === 'SUCCESS') {
			return 'blue'
		} else {
			return 'red'
		}
	}
</script>
<style scoped>
	.ant-timeline-item {
		padding-top: 5px;
		padding-bottom: 10px !important;
	}
	.timeline-div {
		height: 300px;
		overflow: auto;
	}
</style>
