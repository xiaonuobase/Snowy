<template>
	<a-row :gutter="10">
		<!-- 轮播图（小屏情况显示） -->
		<a-col :xs="24" :sm="24" :md="24" :lg="0" :xl="0">
			<biz-slideshow-card :config="slideshowConfig" />
		</a-col>
		<!-- 左侧 -->
		<a-col :xs="24" :sm="24" :md="16" :lg="16" :xl="16">
			<!-- 人员信息 -->
			<sys-user-info-card class="mb-2" />
			<a-row :gutter="10" class="mt-2">
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<!-- 快捷方式 -->
					<biz-shortcut-card />
				</a-col>
			</a-row>
			<a-row :gutter="10" class="mt-2">
				<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
					<!-- 通知公告 -->
					<biz-notice-card />
				</a-col>
				<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
					<!-- 站内信息 -->
					<biz-mini-message-card />
				</a-col>
			</a-row>
		</a-col>
		<!-- 右侧 -->
		<a-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
			<a-row :gutter="10">
				<a-col :xs="0" :sm="0" :md="0" :lg="24" :xl="24">
					<!-- 轮播图 -->
					<biz-slideshow-card :config="slideshowConfig" />
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<!-- 我的日程 -->
					<biz-schedule-card />
				</a-col>
			</a-row>
		</a-col>
	</a-row>
</template>

<script setup name="bizIndex">
	// 轮播图组件的配置，在业务首页下，获取对应位置的
	const slideshowConfig = {
		options: {
			place: 'BACK_INDEX'
		}
	}
</script>
