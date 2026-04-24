<template>
	<a-spin :spinning="loadSpinning">
		<a-form
			ref="formRef"
			:model="formData"
			:rules="formRules"
			layout="vertical"
			:label-col="{ ...layout.labelCol, offset: 0 }"
			:wrapper-col="{ ...layout.wrapperCol, offset: 0 }"
		>
			<a-row :gutter="8">
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item label="RUSTFS通道KEY：" name="SNOWY_FILE_RUSTFS_ACCESS_KEY">
						<a-input v-model:value="formData.SNOWY_FILE_RUSTFS_ACCESS_KEY" placeholder="请输入RUSTFS通道KEY" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item label="RUSTFS密钥KEY：" name="SNOWY_FILE_RUSTFS_SECRET_KEY">
						<a-input v-model:value="formData.SNOWY_FILE_RUSTFS_SECRET_KEY" placeholder="请输入RUSTFS密钥KEY" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item label="RUSTFS端点：" name="SNOWY_FILE_RUSTFS_END_POINT">
						<a-input v-model:value="formData.SNOWY_FILE_RUSTFS_END_POINT" placeholder="请输入RUSTFS端点" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item label="RUSTFS储存桶：" name="SNOWY_FILE_RUSTFS_DEFAULT_BUCKET_NAME">
						<a-input v-model:value="formData.SNOWY_FILE_RUSTFS_DEFAULT_BUCKET_NAME" placeholder="请输入RUSTFS储存桶" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item label="RUSTFS区域：" name="SNOWY_FILE_RUSTFS_REGION">
						<a-input v-model:value="formData.SNOWY_FILE_RUSTFS_REGION" placeholder="请输入RUSTFS区域" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item>
						<a-space>
							<a-button type="primary" :loading="submitLoading" @click="onSubmit()">保存</a-button>
							<a-button @click="() => formRef.resetFields()">重置</a-button>
						</a-space>
					</a-form-item>
				</a-col>
			</a-row>
		</a-form>
	</a-spin>
</template>

<script setup name="rustfsFileForm">
	import { cloneDeep } from 'lodash-es'
	import { required } from '@/utils/formRules'
	import { message } from 'ant-design-vue'
	import configApi from '@/api/dev/configApi'

	const formRef = ref()
	const formData = ref({})
	const submitLoading = ref(false)
	const loadSpinning = ref(true)

	const param = {
		category: 'FILE_RUSTFS'
	}
	configApi.configList(param).then((data) => {
		loadSpinning.value = false
		if (data) {
			data.forEach((item) => {
				formData.value[item.configKey] = item.configValue
			})
		} else {
			message.warning('表单项不存在，请初始化数据库')
		}
	})

	const formRules = {
		SNOWY_FILE_RUSTFS_ACCESS_KEY: [required('请输入RUSTFS通道KEY')],
		SNOWY_FILE_RUSTFS_SECRET_KEY: [required('请输入RUSTFS密钥KEY')],
		SNOWY_FILE_RUSTFS_END_POINT: [required('请输入RUSTFS端点')],
		SNOWY_FILE_RUSTFS_DEFAULT_BUCKET_NAME: [required('请输入RUSTFS储存桶')],
		SNOWY_FILE_RUSTFS_REGION: [required('请输入RUSTFS区域')]
	}
	const onSubmit = () => {
		formRef.value
			.validate()
			.then(() => {
				submitLoading.value = true
				let submitParam = cloneDeep(formData.value)
				const param = Object.entries(submitParam).map((item) => {
					return {
						configKey: item[0],
						configValue: item[1]
					}
				})
				configApi
					.configEditForm(param)
					.then(() => {})
					.finally(() => {
						submitLoading.value = false
					})
			})
			.catch(() => {})
	}
	const layout = {
		labelCol: {
			span: 24
		},
		wrapperCol: {
			span: 12
		}
	}
</script>
