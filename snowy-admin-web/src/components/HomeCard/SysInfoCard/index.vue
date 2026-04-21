<template>
	<a-card title="系统信息" :bordered="false" class="sys-info-card">
		<a-descriptions :column="1" size="small">
			<a-descriptions-item label="Java版本">
				<a-tag color="blue">{{ sysInfo.javaVersion || '-' }}</a-tag>
			</a-descriptions-item>
			<a-descriptions-item label="操作系统">
				<span class="info-text">{{ sysInfo.osName || '-' }}</span>
			</a-descriptions-item>
			<a-descriptions-item label="服务器IP">
				<span class="info-text">{{ sysInfo.serverIp || '-' }}</span>
			</a-descriptions-item>
			<a-descriptions-item label="运行时长">
				<a-tag color="green">{{ sysInfo.runTime || '-' }}</a-tag>
			</a-descriptions-item>
			<a-descriptions-item label="最后登录">
				<span class="info-text">{{ userInfo.lastLoginTime || '首次登录' }}</span>
			</a-descriptions-item>
		</a-descriptions>
	</a-card>
</template>

<script setup name="sysInfoCard">
	import indexApi from '@/api/sys/indexApi'
	import tool from '@/utils/tool'

	const userInfo = tool.data.get('USER_INFO') || {}
	const sysInfo = ref({})

	onMounted(() => {
		indexApi
			.indexSysInfo()
			.then((data) => {
				sysInfo.value = data
			})
			.catch(() => {})
	})
</script>

<style scoped lang="less">
	:deep(.ant-card-body) {
		padding-top: 0 !important;
	}
	.sys-info-card {
		height: 100%;
	}
	.info-text {
		color: var(--text-color);
		font-size: 14px;
		font-weight: 500;
	}
</style>
