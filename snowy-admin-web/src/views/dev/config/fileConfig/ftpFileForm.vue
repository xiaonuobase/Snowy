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
					<a-form-item label="FTP主机：" name="SNOWY_FILE_FTP_HOST">
						<a-input v-model:value="formData.SNOWY_FILE_FTP_HOST" placeholder="请输入FTP主机" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item label="FTP端口：" name="SNOWY_FILE_FTP_PORT">
						<a-input v-model:value="formData.SNOWY_FILE_FTP_PORT" placeholder="请输入FTP端口" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item label="FTP用户名：" name="SNOWY_FILE_FTP_USERNAME">
						<a-input v-model:value="formData.SNOWY_FILE_FTP_USERNAME" placeholder="请输入FTP用户名" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item label="FTP密码：" name="SNOWY_FILE_FTP_PASSWORD">
						<a-input-password v-model:value="formData.SNOWY_FILE_FTP_PASSWORD" placeholder="请输入FTP密码" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item label="FTP基础目录：" name="SNOWY_FILE_FTP_BASE_PATH">
						<a-input v-model:value="formData.SNOWY_FILE_FTP_BASE_PATH" placeholder="请输入FTP基础目录" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item label="FTP储存桶：" name="SNOWY_FILE_FTP_DEFAULT_BUCKET_NAME">
						<a-input v-model:value="formData.SNOWY_FILE_FTP_DEFAULT_BUCKET_NAME" placeholder="请输入FTP储存桶" />
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

<script setup name="ftpFileForm">
	import { cloneDeep } from 'lodash-es'
	import { required } from '@/utils/formRules'
	import { message } from 'ant-design-vue'
	import configApi from '@/api/dev/configApi'

	const formRef = ref()
	const formData = ref({})
	const submitLoading = ref(false)
	const loadSpinning = ref(true)

	// 查询此界面的配置项,并转为表单
	const param = {
		category: 'FILE_FTP'
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

	// 默认要校验的
	const formRules = {
		SNOWY_FILE_FTP_HOST: [required('请输入FTP主机')],
		SNOWY_FILE_FTP_PORT: [required('请输入FTP端口')],
		SNOWY_FILE_FTP_USERNAME: [required('请输入FTP用户名')],
		SNOWY_FILE_FTP_PASSWORD: [required('请输入FTP密码')],
		SNOWY_FILE_FTP_BASE_PATH: [required('请输入FTP基础目录')],
		SNOWY_FILE_FTP_DEFAULT_BUCKET_NAME: [required('请输入FTP储存桶')]
	}
	// 验证并提交数据
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
