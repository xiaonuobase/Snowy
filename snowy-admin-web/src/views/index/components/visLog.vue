<template>
	<a-card title="访问记录" :bordered="false">
		<template #extra v-if="displayMore()"><a @click="leaveFor('/dev/vislog')">更多</a></template>
		<div class="timeline-div">
			<a-timeline>
				<a-timeline-item :key="visLog.id" v-for="visLog in visLogList" :color="getTimelineColor(visLog.category)"
					>{{ visLog.opTime }} {{ visLog.name }}
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
	onMounted(() => {
		// 进来后执行查询
		seleVisLogList()
	})
	// 是否展示更多按钮
	const displayMore = () => {
		return (userInfo.roleCodeList && userInfo.roleCodeList.indexOf("super") !== -1)
	}
	// 查询数据
	const seleVisLogList = () => {
		indexApi.indexVisLogList().then((data) => {
			visLogList.value = data
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
		margin-bottom: 0px;
		color: rgb(188, 189, 190);
	}
	.timeline-div {
		height: 300px;
		overflow: auto;
	}
</style>
