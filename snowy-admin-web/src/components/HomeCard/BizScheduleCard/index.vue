<template>
	<a-card :title="title" :bordered="false" class="schedule-card" :loading="apiLoading">
		<div class="card-body-inner">
			<div class="calendar-wrapper">
				<a-calendar v-model:value="calendarValue" :fullscreen="false" @select="onPanelSelect" />
			</div>
			<div class="schedule-list">
				<a-timeline v-if="scheduleList.length > 0">
					<a-timeline-item :key="schedule.id" v-for="schedule in scheduleList" color="blue">
						<div class="schedule-item">
							<span class="schedule-time">{{ schedule.scheduleTime }}</span>
							<span class="schedule-content">{{ schedule.scheduleContent }}</span>
							<a-button type="link" danger size="small" class="delete-btn" @click="deleteSchedules(schedule)">
								<template #icon><delete-outlined /></template>
							</a-button>
						</div>
					</a-timeline-item>
				</a-timeline>
				<a-empty v-else :image="SimpleImage" description="暂无日程" />
			</div>
			<div class="add-schedule-btn" @click="addSchedule"><plus-circle-outlined /> <span>新增日程</span></div>
		</div>

		<xn-form-container title="新增日程" :width="400" :visible="visible" :destroy-on-close="true" @close="onClose">
			<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
				<a-form-item label="时间" name="scheduleTime">
					<a-time-picker v-model:value="scheduleTime" value-format="HH:mm:ss" style="width: 100%" />
				</a-form-item>
				<a-form-item label="日程描述" name="scheduleContent">
					<a-textarea
						v-model:value="formData.scheduleContent"
						placeholder="请输入日程描述"
						:maxlength="20"
						:auto-size="{ minRows: 3, maxRows: 3 }"
					/>
				</a-form-item>
			</a-form>
			<template #footer>
				<a-button @click="onClose">取消</a-button>
				<a-button type="primary" :loading="submitLoading" @click="onSubmit">保存</a-button>
			</template>
		</xn-form-container>
	</a-card>
</template>

<script setup name="schedule">
	import dayjs from 'dayjs'
	import weekday from 'dayjs/plugin/weekday'
	import localeData from 'dayjs/plugin/localeData'
	import { Empty } from 'ant-design-vue'
	dayjs.extend(weekday)
	dayjs.extend(localeData)

	const SimpleImage = Empty.PRESENTED_IMAGE_SIMPLE

	import { required } from '@/utils/formRules'
	import { onMounted, ref } from 'vue'
	import indexApi from '@/api/sys/indexApi'
	import { PlusCircleOutlined, DeleteOutlined } from '@ant-design/icons-vue'

	const title = ref('我的日程')
	const scheduleList = ref([])
	const calendarValue = ref(dayjs())
	const apiLoading = ref(false)
	const submitLoading = ref(false)

	onMounted(() => {
		// 进来后执行查询
		seleScheduleList()
	})

	const seleScheduleList = () => {
		const param = {
			scheduleDate: calendarValue.value.format('YYYY-MM-DD')
		}
		apiLoading.value = true
		indexApi
			.indexScheduleList(param)
			.then((data) => {
				scheduleList.value = data
			})
			.catch(() => {})
			.finally(() => {
				apiLoading.value = false
			})
	}

	// 点击某一天
	const onPanelSelect = () => {
		seleScheduleList()
	}
	// 新增日程
	const addSchedule = () => {
		visible.value = true
	}
	// 删除
	const deleteSchedules = (schedule) => {
		const params = [
			{
				id: schedule.id
			}
		]
		indexApi.indexScheduleDeleteSchedule(params).then(() => {
			seleScheduleList()
		})
	}

	// 以下部分是抽屉的
	const visible = ref(false)
	const scheduleTime = ref('09:00:00')
	const formRef = ref()
	const formData = ref({
		scheduleTime: scheduleTime.value,
		scheduleContent: ''
	})
	const formRules = {
		scheduleTime: [required('请选择时间')],
		scheduleContent: [required('请输入日程备注')]
	}

	const onClose = () => {
		visible.value = false
		formData.value.scheduleContent = ''
	}

	const onSubmit = () => {
		formRef.value.validate().then(() => {
			submitLoading.value = true
			const params = {
				scheduleDate: calendarValue.value.format('YYYY-MM-DD'),
				scheduleTime: scheduleTime.value,
				scheduleContent: formData.value.scheduleContent
			}
			indexApi
				.indexScheduleAdd(params)
				.then(() => {
					onClose()
					seleScheduleList()
				})
				.finally(() => {
					submitLoading.value = false
				})
		})
	}
</script>

<style scoped lang="less">
	:deep(.ant-card-body) {
		padding-top: 0 !important;
		height: calc(100% - 57px);
		display: flex;
		flex-direction: column;
	}
	.schedule-card {
		height: 100%;
	}
	.card-body-inner {
		flex: 1;
		display: flex;
		flex-direction: column;
	}
	.calendar-wrapper {
		border-bottom: 1px solid #f0f0f0;
		:deep(.ant-picker-calendar-header) {
			padding: 8px 0 !important;
		}
	}
	.schedule-list {
		flex: 1;
		overflow-y: auto;
		padding: 8px 0;
		&::-webkit-scrollbar {
			width: 4px;
		}
		&::-webkit-scrollbar-thumb {
			background: #e8e8e8;
			border-radius: 2px;
		}
	}
	.schedule-item {
		display: flex;
		align-items: center;
		position: relative;
		padding-right: 30px;
		&:hover {
			.delete-btn {
				opacity: 1;
			}
		}
	}
	.schedule-time {
		font-weight: 600;
		color: #1890ff;
		margin-right: 12px;
		font-size: 13px;
	}
	.schedule-content {
		color: #595959;
		font-size: 13px;
	}
	:deep(.snowy-theme-dark) & {
		.calendar-wrapper {
			border-bottom-color: #303030;
		}
		.schedule-list {
			&::-webkit-scrollbar-thumb {
				background: #333;
			}
		}
		.schedule-content {
			color: rgba(255, 255, 255, 0.85);
		}
	}
	.delete-btn {
		position: absolute;
		right: 0;
		opacity: 0;
		transition: opacity 0.3s;
	}
	.add-schedule-btn {
		margin-top: 8px;
		padding: 8px;
		border: 1px dashed #d9d9d9;
		text-align: center;
		color: #8c8c8c;
		cursor: pointer;
		transition: all 0.3s;
		display: flex;
		align-items: center;
		justify-content: center;
		gap: 8px;
		&:hover {
			color: #1890ff;
			border-color: #1890ff;
			background: #f0f7ff;
		}
	}
</style>
