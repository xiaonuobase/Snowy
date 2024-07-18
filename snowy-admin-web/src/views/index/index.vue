<template>
	<a-row :gutter="10">
		<a-col :span="16" :xs="24" :sm="24" :md="24" :lg="16" :xl="16">
			<!-- 人员信息 -->
			<sys-user-info-card class="mb-2" />
			<!-- 业务数据 -->
			<sys-biz-data-card class="mb-2" />
			<!-- 运维一览 -->
			<sys-op-data-card class="mb-2" />
			<a-row :gutter="10">
				<!-- 访问记录 -->
				<a-col :span="12" :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
					<sys-vis-log-card class="mb-2" />
				</a-col>
				<!-- 操作记录 -->
				<a-col :span="12" :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
					<sys-op-log-card class="mb-2" />
				</a-col>
			</a-row>
		</a-col>
		<a-col :span="8" :xs="24" :sm="24" :md="24" :lg="8" :xl="8">
			<!-- 轮播图 -->
			<biz-slideshow-card :config="slideshowConfig" class="mb-2" />
			<!-- 基础工具 -->
			<sys-tool-data-card class="mb-2" />
			<!-- 周访问量 -->
			<sys-vis-chart-data-card />
		</a-col>
	</a-row>
</template>

<script setup name="indexHome">
	// 轮播图配置
	const slideshowConfig = {
		options: {
			place: 'BACK_SYS_INDEX'
		}
	}
</script>
