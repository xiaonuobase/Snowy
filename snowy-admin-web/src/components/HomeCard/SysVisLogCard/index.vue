<template>
	<a-card :title="title" :bordered="false" :loading="apiLoading">
		<template #extra v-if="displayMore()"><a @click="leaveFor('/dev/vislog')">更多</a></template>
		<div class="timeline-div">
			<a-timeline>
				<a-timeline-item :key="visLog.id" v-for="visLog in visLogList" :color="getTimelineColor(visLog.category)">
					{{ $TOOL.parseTime(visLog.opTime, '{y}-{m}-{d} {h}:{i}:{s}')  }} {{ visLog.name }}
					<p class="timeline-item-p">{{ visLog.opIp }} {{ visLog.opAddress }}</p>
				</a-timeline-item>
			</a-timeline>
		</div>
	</a-card>
</template>

<script setup name="indexVisLog">
	import router from '@/router'
	import indexApi from '@/api/sys/indexApi'
	import { onMounted } from 'vue'
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
	// 获取颜色
	const getTimelineColor = (value) => {
		if (value === 'LOGIN') {
			return 'blue'
		}
		if (value === 'LOGOUT') {
			return 'gray'
		}
	}
</script>
<style scoped>
	.ant-timeline-item {
		padding-top: 5px;
		padding-bottom: 10px !important;
	}
	.timeline-item-p {
		margin-bottom: 0;
		color: rgb(188, 189, 190);
	}
	.timeline-div {
		height: 300px;
		overflow: auto;
	}
</style>
