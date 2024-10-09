<template>
	<xn-form-container
		:title="formData.id ? '编辑通知公告' : '增加通知公告'"
		:width="1000"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-row :gutter="16">
				<a-col :span="19">
					<a-form-item label="标题：" name="title">
						<a-input v-model:value="formData.title" placeholder="请输入标题" allow-clear />
					</a-form-item>
					<a-form-item name="type">
						<template #label>
							<a-tooltip>
								<template #title> 这里只是标签的类型 </template>
								<question-circle-outlined />
								类型：
							</a-tooltip>
						</template>
						<a-radio-group v-model:value="formData.type" placeholder="请选择类型" :options="typeOptions" />
					</a-form-item>
				</a-col>
				<a-col :span="5">
					<a-form-item label="封面图：" name="image">
						<xn-upload v-model:value="formData.image" uploadMode="image" />
					</a-form-item>
				</a-col>
				<a-col :span="24">
					<a-form-item label="内容：" name="content">
						<xn-editor v-model:value="formData.content" placeholder="请输入内容" />
					</a-form-item>
				</a-col>
				<a-col :span="24">
					<a-form-item label="摘要：" name="digest">
						<a-textarea
							v-model:value="formData.digest"
							placeholder="请输入摘要"
							:auto-size="{ minRows: 3, maxRows: 5 }"
							:showCount="true"
							:maxlength="60"
						/>
					</a-form-item>
				</a-col>
				<a-col :span="18">
					<a-form-item label="发布位置：" name="place">
						<a-checkbox-group v-model:value="formData.place" placeholder="请选择发布位置" :options="placeOptions" />
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-form-item label="排序：" name="sortCode">
						<a-input-number
							v-model:value="formData.sortCode"
							placeholder="请输入排序"
							:max="1000"
							style="width: 100%"
						/>
					</a-form-item>
				</a-col>
				<a-col :span="24">
					<a-form-item label="备注：" name="remark">
						<a-textarea
							v-model:value="formData.remark"
							placeholder="请输入备注"
							:auto-size="{ minRows: 3, maxRows: 5 }"
						/>
					</a-form-item>
				</a-col>
			</a-row>
		</a-form>
		<template #footer>
			<a-button style="margin-right: 8px" @click="onClose">关闭</a-button>
			<a-button type="primary" @click="onSubmit" :loading="submitLoading">保存</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="bizNoticeForm">
	import tool from '@/utils/tool'
	import { cloneDeep } from 'lodash-es'
	import { required } from '@/utils/formRules'
	import bizNoticeApi from '@/api/biz/bizNoticeApi'
	// 抽屉状态
	const open = ref(false)
	const emit = defineEmits({ successful: null })
	const formRef = ref()
	// 表单数据
	const formData = ref({})
	const submitLoading = ref(false)
	const typeOptions = ref([])
	const placeOptions = ref([])
	const statusOptions = ref([])

	// 打开抽屉
	const onOpen = (record) => {
		open.value = true
		if (record) {
			let recordData = cloneDeep(record)
			recordData.place = JSON.parse(recordData.place)
			formData.value = Object.assign({}, recordData)
		} else {
			formData.value = {
				type: 'NOTICE',
				place: ['BACK_MOBILE', 'BACK_INDEX'],
				sortCode: 99
			}
		}
		typeOptions.value = tool.dictList('BIZ_NOTICE_TYPE')
		placeOptions.value = tool.dictList('BIZ_NOTICE_PLACE')
		statusOptions.value = tool.dictList('BIZ_NOTICE_STATUS')
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
		content: [required('请输入内容')],
		digest: [required('请输入摘要')],
		type: [required('请选择类型')],
		place: [required('请选择发布位置')],
		sortCode: [required('请输入排序')]
	}
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value.validate().then(() => {
			submitLoading.value = true
			const formDataParam = cloneDeep(formData.value)
			formDataParam.place = JSON.stringify(formDataParam.place)
			bizNoticeApi
				.bizNoticeSubmitForm(formDataParam, formDataParam.id)
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
