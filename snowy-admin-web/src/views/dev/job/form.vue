<template>
	<a-drawer
		:title="formData.id ? '编辑定时任务' : '增加定时任务'"
		:width="500"
		:visible="visible"
		:destroy-on-close="true"
		:body-style="{ paddingBottom: '80px' }"
		:footer-style="{ textAlign: 'right' }"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-form-item label="定时任务名称：" name="name">
				<a-input v-model:value="formData.name" placeholder="请输入定时任务名称" allow-clear />
			</a-form-item>
			<a-form-item label="分类：" name="category">
				<a-select v-model:value="formData.category" placeholder="请选择分类" :options="categoryOptions"> </a-select>
			</a-form-item>
			<a-form-item label="任务类名：" name="actionClass">
				<a-select v-model:value="formData.actionClass" placeholder="请选择任务类名" :options="actionClassOptions">
				</a-select>
			</a-form-item>
			<a-form-item label="表达式：" name="cronExpression">
				<cron v-model:modelValue="formData.cronExpression"></cron>
			</a-form-item>
			<a-form-item label="排序:" name="sortCode">
				<a-slider v-model:value="formData.sortCode" :max="100" />
			</a-form-item>
		</a-form>
		<template #footer>
			<a-button style="margin-right: 8px" @click="onClose">关闭</a-button>
			<a-button type="primary" @click="onSubmit" :loading="submitLoading">保存</a-button>
		</template>
	</a-drawer>
</template>

<script setup name="devJobForm">
	import { required } from '@/utils/formRules'
	import tool from '@/utils/tool'
	import jobApi from '@/api/dev/jobApi'
	import Cron from '@/components/Cron/index.vue'
	// 默认是关闭状态
	let visible = $ref(false)
	const emit = defineEmits({ successful: null })
	const formRef = ref()
	// 表单数据
	const formData = ref({})
	const submitLoading = ref(false)
	// 定时任务类
	const actionClassOptions = ref([])

	// 打开抽屉
	const onOpen = (record) => {
		visible = true
		formData.value = {
			sortCode: 99
		}
		if (record) {
			formData.value = Object.assign({}, record)
		}
		// 加载定时任务类列表
		jobApi.jobGetActionClass().then((data) => {
			actionClassOptions.value = data.map((item) => {
				return {
					value: item,
					label: item
				}
			})
		})
	}
	// 关闭抽屉
	const onClose = () => {
		formRef.value.resetFields()
		visible = false
	}

	// 默认要校验的
	const formRules = {
		name: [required('请输入定时任务名称')],
		category: [required('请选择分类')],
		actionClass: [required('请输入任务类名')],
		cronExpression: [required('请输入表达式')],
		sortCode: [required('请滑动排序')]
	}

	// 验证并提交数据
	const onSubmit = () => {
		formRef.value
			.validate()
			.then(() => {
				submitLoading.value = true
				jobApi
					.submitForm(formData.value, !formData.value.id)
					.then(() => {
						onClose()
						emit('successful')
					})
					.finally(() => {
						submitLoading.value = false
					})
			})
	}
	// 分类
	const categoryOptions = tool.dictTypeList('JOB_CATEGORY').map((item) => {
		return {
			value: item['dictValue'],
			label: item['name']
		}
	})
	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>
