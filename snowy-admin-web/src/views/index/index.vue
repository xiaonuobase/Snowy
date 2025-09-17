<template>
	<a-row :gutter="10">
		<!-- 轮播图（小屏情况显示） -->
		<a-col :xs="24" :sm="24" :md="24" :lg="0" :xl="0">
			<!-- 轮播图 -->
			<biz-slideshow-card :config="slideshowConfig" />
		</a-col>
		<a-col :xs="24" :sm="24" :md="24" :lg="16" :xl="16">
			<!-- 人员信息 -->
			<sys-user-info-card class="mb-2" />
			<!-- 业务数据 -->
			<sys-biz-data-card class="mb-2" />
			<!-- 运维一览 -->
			<sys-op-data-card class="mb-2" />
			<a-row :gutter="10">
				<!-- 访问记录 -->
				<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
					<sys-vis-log-card class="mb-2" />
				</a-col>
				<!-- 操作记录 -->
				<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
					<sys-op-log-card class="mb-2" />
				</a-col>
			</a-row>
		</a-col>
		<a-col :xs="24" :sm="24" :md="24" :lg="8" :xl="8">
			<a-row :gutter="10">
				<a-col :xs="0" :sm="0" :md="0" :lg="24" :xl="24">
					<!-- 轮播图 -->
					<biz-slideshow-card :config="slideshowConfig" />
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<!-- 基础工具 -->
					<sys-tool-data-card class="mb-2" />
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<!-- 周访问量 -->
					<sys-vis-chart-data-card />
				</a-col>
			</a-row>
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
