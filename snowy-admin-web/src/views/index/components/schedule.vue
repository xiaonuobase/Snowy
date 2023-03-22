<template>
	<div>
		<a-calendar v-model:value="calendarValue" :fullscreen="false" @select="onPanelSelect" />
		<a-card :bordered="false">
			<a-timeline>
				<a-timeline-item :key="schedule.id" v-for="schedule in scheduleList">
					{{ schedule.scheduleTime }} {{ schedule.scheduleContent }}
					<a style="float: right"><delete-outlined @click="deleteSchedules(schedule)" /></a>
				</a-timeline-item>
			</a-timeline>
			<div class="add-schedule" @click="addSchedule"><plus-circle-two-tone /> 新增日程</div>
		</a-card>
		<xn-form-container
			title="增加日程"
			:width="700"
			:visible="visible"
			:destroy-on-close="true"
			@close="onClose"
		>
			<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
				<a-form-item label="时间：" name="scheduleTime">
					<a-time-picker v-model:value="scheduleTime" value-format="HH:mm:ss" />
				</a-form-item>
				<a-form-item label="日程描述：" name="scheduleContent">
					<a-textarea
						v-model:value="formData.scheduleContent"
						placeholder="请输入日程描述"
						:maxlength="20"
						:auto-size="{ minRows: 3, maxRows: 3 }"
					/>
				</a-form-item>
			</a-form>
			<template #footer>
				<a-button style="margin-right: 8px" @click="onClose">关闭</a-button>
				<a-button type="primary" :loading="submitLoading" @click="onSubmit">保存</a-button>
			</template>
		</xn-form-container>
	</div>
</template>

<script setup name="schedule">
	import dayjs from 'dayjs'
	import { required } from '@/utils/formRules'
	import { onMounted } from 'vue'
	import indexApi from '@/api/sys/indexApi'
	const scheduleList = ref([])
	const calendarValue = ref(dayjs())

	onMounted(() => {
		// 进来后执行查询
		seleScheduleList()
	})

	const seleScheduleList = () => {
		const param = {
			scheduleDate: calendarValue.value.format('YYYY-MM-DD')
		}
		indexApi.indexScheduleList(param).then((data) => {
			scheduleList.value = data
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
		scheduleTime: scheduleTime.value
	})
	const formRules = {
		scheduleTime: [required('请选择时间')],
		scheduleContent: [required('请输入日程备注')]
	}
	const submitLoading = ref(false)
	// 关闭抽屉
	const onClose = () => {
		visible.value = false
		scheduleTime.value = '09:00:00'
		formData.value.scheduleContent = ''
	}
	// 保存
	const onSubmit = () => {
		formRef.value.validate().then(() => {
			formData.value.scheduleTime = scheduleTime.value
			formData.value.scheduleDate = calendarValue.value.format('YYYY-MM-DD')
			submitLoading.value = true
			// 调用接口
			indexApi
				.indexScheduleAdd(formData.value)
				.then(() => {
					seleScheduleList()
					onClose()
				})
				.finally(() => {
					submitLoading.value = false
				})
		})
	}
</script>

<style scoped>
	.add-schedule {
		cursor: pointer;
		margin-top: -10px;
	}
</style>
