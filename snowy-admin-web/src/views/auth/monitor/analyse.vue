<template>
	<div style="padding-bottom: 10px">
		<a-row :gutter="16">
			<a-col :span="6">
				<a-card class="snowy-monitor-card" :bordered="false">
					<template #cover>
						<team-outlined style="color: #69c0ff" class="snowy-monitor-card-icon" />
						<div class="snowy-monitor-card-div">
							<span class="snowy-monitor-card-span">当前会话数：</span
							><span class="snowy-monitor-card-span">{{ analysisObj.currentSessionTotalCount }}</span>
						</div>
					</template>
				</a-card>
			</a-col>
			<a-col :span="6">
				<a-card class="snowy-monitor-card" :bordered="false">
					<template #cover>
						<verified-outlined style="color: rgb(255, 156, 110)" class="snowy-monitor-card-icon" />
						<div class="snowy-monitor-card-div">
							<span class="snowy-monitor-card-span">最大签发令牌：</span
							><span class="snowy-monitor-card-span">{{ analysisObj.maxTokenCount }}</span>
						</div>
					</template>
				</a-card>
			</a-col>
			<a-col :span="6">
				<a-card class="snowy-monitor-card" :bordered="false">
					<template #cover>
						<rise-outlined style="color: rgb(255, 133, 192)" class="snowy-monitor-card-icon" />
						<div class="snowy-monitor-card-div">
							<span class="snowy-monitor-card-span">1小时内新增：</span
							><span class="snowy-monitor-card-span">{{ analysisObj.oneHourNewlyAdded }}</span>
						</div>
					</template>
				</a-card>
			</a-col>
			<a-col :span="6">
				<a-card class="snowy-monitor-card" :bordered="false">
					<template #cover>
						<pie-chart-outlined style="color: rgb(92, 219, 211)" class="snowy-monitor-card-icon" />
						<div class="snowy-monitor-card-div">
							<span class="snowy-monitor-card-span">B/C端占比：</span
							><span class="snowy-monitor-card-span">{{ analysisObj.proportionOfBAndC }}</span>
						</div>
					</template>
				</a-card>
			</a-col>
		</a-row>
	</div>
</template>

<script setup name="monitorAnalyse">
	import monitorApi from '@/api/auth/monitorApi'

	// 预置空数据
	const analysisObj = ref({
		currentSessionTotalCount: '0',
		maxTokenCount: '0',
		oneHourNewlyAdded: '0',
		proportionOfBAndC: '0/0'
	})
	monitorApi.monitorAnalysis().then((res) => {
		analysisObj.value = res
	})
</script>

<style scoped>
	.snowy-monitor-card {
		height: 100px;
	}
	.snowy-monitor-card-icon {
		font-size: 30px;
		padding-top: 18px;
		padding-bottom: 5px;
	}
	.snowy-monitor-card-div {
		display: flex;
		justify-content: center;
	}
	.snowy-monitor-card-span {
		font-size: 16px;
	}
</style>
