<template>
	<xn-form-container
		:title="formData.id ? '编辑轮播图' : '增加轮播图'"
		:width="700"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-row :gutter="16">
				<a-col :span="18">
					<a-form-item label="标题：" name="title">
						<a-input v-model:value="formData.title" placeholder="请输入标题" allow-clear />
					</a-form-item>
					<a-form-item label="排序：" name="sortCode">
						<a-input v-model:value="formData.sortCode" placeholder="请输入排序" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-form-item label="图片：" name="image">
						<xn-upload v-model:value="formData.image" uploadMode="image" />
					</a-form-item>
				</a-col>
				<a-col :span="24">
					<a-form-item label="展示位置：" name="place">
						<a-checkbox-group v-model:value="formData.place" placeholder="请选择展示位置" :options="placeOptions" />
					</a-form-item>
				</a-col>
				<a-col :span="24">
					<sub-form ref="sumFormRef" :data-array="formData.pathDetails" :place="formData.place" />
				</a-col>
			</a-row>
		</a-form>
		<template #footer>
			<a-button style="margin-right: 8px" @click="onClose">关闭</a-button>
			<a-button type="primary" @click="onSubmit" :loading="submitLoading">保存</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="devSlideshowForm">
	import tool from '@/utils/tool'
	import { cloneDeep } from 'lodash-es'
	import { required } from '@/utils/formRules'
	import slideshowApi from '@/api/dev/slideshowApi'
	import SubForm from './subForm.vue'
	// 抽屉状态
	const open = ref(false)
	const emit = defineEmits({ successful: null })
	const formRef = ref()
	const sumFormRef = ref()
	// 表单数据
	const formData = ref({})
	const submitLoading = ref(false)
	const placeOptions = ref([])

	// 打开抽屉
	const onOpen = (record) => {
		open.value = true
		if (record) {
			let recordData = cloneDeep(record)
			recordData.place = JSON.parse(recordData.place)
			recordData.pathDetails = JSON.parse(recordData.pathDetails)
			formData.value = Object.assign({}, recordData)
		} else {
			formData.value = {
				place: ['BACK_SYS_INDEX']
			}
		}
		placeOptions.value = tool.dictList('DEV_SLIDESHOW_PLACE')
	}
	// 关闭抽屉
	const onClose = () => {
		formRef.value.resetFields()
		formData.value = {}
		open.value = false
	}
	// 默认要校验的
	const formRules = {
		title: [required('请输入标题')],
		place: [required('请输入展示位置')],
		sortCode: [required('请输入排序')]
	}
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value.validate().then(() => {
			const formDataParam = cloneDeep(formData.value)
			formDataParam.place = JSON.stringify(formDataParam.place)
			const details = sumFormRef.value.getData()
			if (details === false) {
				return
			}
			formDataParam.pathDetails = JSON.stringify(details)
			submitLoading.value = true
			slideshowApi
				.devSlideshowSubmitForm(formDataParam, formDataParam.id)
				.then(() => {
					onClose()
					emit('successful')
				})
				.finally(() => {
					submitLoading.value = false
				})
		})
	}
	// 抛出函数
	defineExpose({
		onOpen
	})
</script>
