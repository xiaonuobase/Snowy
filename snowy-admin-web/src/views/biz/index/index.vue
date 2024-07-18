<template>
	<a-row :gutter="10">
		<!-- 左侧 -->
		<a-col :span="16">
			<!-- 人员信息 -->
			<sys-user-info-card class="mb-2" />
			<a-row :gutter="10" class="mt-2">
				<a-col :span="24">
					<!-- 快捷方式 -->
					<biz-shortcut-card />
				</a-col>
			</a-row>
			<a-row :gutter="10" class="mt-2">
				<a-col :span="12">
					<!-- 通知公告 -->
					<biz-notice-card />
				</a-col>
				<a-col :span="12">
					<!-- 站内信息 -->
					<biz-mini-message-card />
				</a-col>
			</a-row>
		</a-col>
		<!-- 右侧 -->
		<a-col :span="8">
			<!-- 轮播图 -->
			<biz-slideshow-card :config="slideshowConfig" />
			<!-- 我的日程 -->
			<biz-schedule-card />
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
