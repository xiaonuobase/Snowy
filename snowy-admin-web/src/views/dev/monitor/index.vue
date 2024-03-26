<template>
	<a-spin :spinning="spinning">
		<a-row :gutter="[10, 10]" class="mb-2">
			<a-col :span="6">
				<a-card title="CPU监控" :bordered="false" class="monitor-center-row-col-card">
					<a-tooltip>
						<template #title>
							<div>CPU系统使用率：{{ devMonitorCpuInfo.cpuSysUseRate }}</div>
							<div>CPU用户使用率：{{ devMonitorCpuInfo.cpuUserUseRate }}</div>
							<div>CPU当前总使用率：{{ devMonitorCpuInfo.cpuTotalUseRate }}%</div>
							<div>CPU当前等待率：{{ devMonitorCpuInfo.cpuWaitRate }}</div>
							<div>CPU当前空闲率：{{ devMonitorCpuInfo.cpuFreeRate }}</div>
						</template>
						<a-progress
							type="dashboard"
							:stroke-color="getProgressColor(devMonitorCpuInfo.cpuTotalUseRate)"
							:percent="devMonitorCpuInfo.cpuTotalUseRate"
						/>
					</a-tooltip>
					<div>CPU当前总使用率</div>
				</a-card>
			</a-col>
			<a-col :span="6">
				<a-card title="内存信息" :bordered="false" class="monitor-center-row-col-card">
					<a-tooltip>
						<template #title>
							<div>内存总量：{{ devMonitorMemoryInfo.memoryTotal }}</div>
							<div>内存已用：{{ devMonitorMemoryInfo.memoryUsed }}</div>
							<div>内存剩余：{{ devMonitorMemoryInfo.memoryFree }}</div>
							<div>内存使用率：{{ devMonitorMemoryInfo.memoryUseRate }}%</div>
						</template>
						<a-progress
							type="dashboard"
							:stroke-color="getProgressColor(devMonitorMemoryInfo.memoryUseRate)"
							:percent="devMonitorMemoryInfo.memoryUseRate"
						/>
					</a-tooltip>
					<div>内存使用率</div>
				</a-card>
			</a-col>
			<a-col :span="6">
				<a-card title="存储信息" :bordered="false" class="monitor-center-row-col-card">
					<a-tooltip>
						<template #title>
							<div>存储总量：{{ devMonitorStorageInfo.storageTotal }}</div>
							<div>存储已用：{{ devMonitorStorageInfo.storageUsed }}</div>
							<div>存储剩余：{{ devMonitorStorageInfo.storageFree }}</div>
							<div>存储使用率：{{ devMonitorStorageInfo.storageUseRate }}%</div>
						</template>
						<a-progress
							type="dashboard"
							:stroke-color="getProgressColor(devMonitorStorageInfo.storageUseRate)"
							:percent="devMonitorStorageInfo.storageUseRate"
						/>
					</a-tooltip>
					<div>存储使用率</div>
				</a-card>
			</a-col>
			<a-col :span="6">
				<a-card title="网络信息" :bordered="false" class="monitor-center-row-col-card">
					<template #extra>
						<a-button title="测速" :loading="networkSpinning" @click="getMonitorNetworkInfo">
							<template #icon><UiwDashboard /></template>
						</a-button>
					</template>
					<a-spin :spinning="networkSpinning">
						<a-statistic
							title="上行速率"
							:value="devMonitorNetworkInfo.upLinkRate"
							:precision="2"
							:value-style="{ color: '#3f8600' }"
						>
							<template #prefix>
								<arrow-up-outlined />
							</template>
						</a-statistic>
						<a-statistic
							class="mt-4"
							title="下行速率"
							:value="devMonitorNetworkInfo.downLinkRate"
							:precision="2"
							:value-style="{ color: '#cf1322' }"
						>
							<template #prefix>
								<arrow-down-outlined />
							</template>
						</a-statistic>
					</a-spin>
				</a-card>
			</a-col>
		</a-row>
		<a-card title="CPU监控" :bordered="false" class="mb-2">
			<a-descriptions size="middle" :column="2" bordered>
				<a-descriptions-item label="CPU名称">{{ devMonitorCpuInfo.cpuName }}</a-descriptions-item>
				<a-descriptions-item label="CPU数量">{{ devMonitorCpuInfo.cpuNum }}</a-descriptions-item>
				<a-descriptions-item label="CPU物理核心数">{{ devMonitorCpuInfo.cpuPhysicalCoreNum }}</a-descriptions-item>
				<a-descriptions-item label="CPU逻辑核心数">{{ devMonitorCpuInfo.cpuLogicalCoreNum }}</a-descriptions-item>
			</a-descriptions>
		</a-card>

		<a-row :gutter="10" class="mb-2">
			<a-col :span="18">
				<a-card title="JVM信息" :bordered="false">
					<a-descriptions size="middle" :column="2" bordered>
						<a-descriptions-item label="JVM名称">{{ devMonitorJvmInfo.jvmName }}</a-descriptions-item>
						<a-descriptions-item label="JVM版本">{{ devMonitorJvmInfo.jvmVersion }}</a-descriptions-item>
						<a-descriptions-item label="JVM启动时间">{{ devMonitorJvmInfo.jvmStartTime }}</a-descriptions-item>
						<a-descriptions-item label="JVM运行时长">{{ devMonitorJvmInfo.jvmRunTime }}</a-descriptions-item>
						<a-descriptions-item label="Java版本">{{ devMonitorJvmInfo.javaVersion }}</a-descriptions-item>
						<a-descriptions-item label="Java安装路径">{{ devMonitorJvmInfo.javaPath }}</a-descriptions-item>
					</a-descriptions>
				</a-card>
			</a-col>
			<a-col :span="6">
				<a-card title="JVM监控" :bordered="false" class="monitor-center-row-col-card">
					<a-tooltip>
						<template #title>
							<div>JVM总分配内存：{{ devMonitorJvmInfo.jvmMemoryTotal }}</div>
							<div>JVM已用内存：{{ devMonitorJvmInfo.jvmMemoryUsed }}</div>
							<div>JVM剩余内存：{{ devMonitorJvmInfo.jvmMemoryFree }}</div>
							<div>JVM内存使用率：{{ devMonitorJvmInfo.jvmUseRate }}%</div>
						</template>
						<a-progress
							type="dashboard"
							:stroke-color="getProgressColor(devMonitorJvmInfo.jvmUseRate)"
							:percent="devMonitorJvmInfo.jvmUseRate"
						/>
					</a-tooltip>
					<div>JVM内存使用率</div>
				</a-card>
			</a-col>
		</a-row>

		<a-card title="服务器信息" :bordered="false" class="mb-2">
			<a-descriptions size="middle" :column="2" bordered>
				<a-descriptions-item label="服务器名称">{{ devMonitorServerInfo.serverName }}</a-descriptions-item>
				<a-descriptions-item label="服务器操作系统">{{ devMonitorServerInfo.serverOs }}</a-descriptions-item>
				<a-descriptions-item label="服务器IP">{{ devMonitorServerInfo.serverIp }}</a-descriptions-item>
				<a-descriptions-item label="服务器架构">{{ devMonitorServerInfo.serverArchitecture }}</a-descriptions-item>
			</a-descriptions>
		</a-card>
	</a-spin>
</template>

<script setup name="devMonitor">
	import { onMounted } from 'vue'
	import monitorApi from '@/api/dev/monitorApi'
	const spinning = ref(false)
	const networkSpinning = ref(false)
	// CPU信息
	const devMonitorCpuInfo = ref({})
	// 内存信息
	const devMonitorMemoryInfo = ref({})
	// 储存信息
	const devMonitorStorageInfo = ref({})
	// 网络信息
	const devMonitorNetworkInfo = ref({})
	// 服务器数据
	const devMonitorServerInfo = ref({})
	// JVM信息
	const devMonitorJvmInfo = ref({})

	onMounted(() => {
		getMonitorServerInfo()
		getMonitorNetworkInfo()
	})

	const getMonitorServerInfo = () => {
		spinning.value = true
		monitorApi
			.monitorServerInfo()
			.then((data) => {
				devMonitorCpuInfo.value = data.devMonitorCpuInfo
				devMonitorMemoryInfo.value = data.devMonitorMemoryInfo
				devMonitorStorageInfo.value = data.devMonitorStorageInfo
				devMonitorServerInfo.value = data.devMonitorServerInfo
				devMonitorJvmInfo.value = data.devMonitorJvmInfo
			})
			.finally(() => {
				spinning.value = false
			})
	}

	const getMonitorNetworkInfo = () => {
		networkSpinning.value = true
		monitorApi
			.monitorNetworkInfo()
			.then((data) => {
				networkSpinning.value = false
				devMonitorNetworkInfo.value = data.devMonitorNetworkInfo
			})
			.finally(() => {
				networkSpinning.value = false
			})
	}

	// 获取颜色 30以下绿色，30-80蓝色， 80往上红色
	const getProgressColor = (value) => {
		const values = Number(value)
		if (values <= 30) {
			return '#49aa19'
		} else if ((values > 30) & (values <= 80)) {
			return '#1890fe'
		} else if (values > 80) {
			return '#e60000'
		}
	}
</script>

<style scoped>
	.monitor-center-row-col-card {
		text-align: center;
	}
	:deep(.ant-card-extra) {
		padding: 8px 0 !important;
	}
</style>
