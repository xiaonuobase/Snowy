<template>
	<xn-form-container title="模板设置" :width="800" :open="open" :destroy-on-close="true" @close="onClose">
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-form-item label="主题：" name="subject">
				<a-input v-model:value="formData.subject" />
			</a-form-item>
			<a-form-item name="content">
				<template #label>
					<div style="display: flex">
						<div>内容模板：</div>
						<div>
							<a-button size="small" @click="emailPreviewRef.onOpen(formData.content)">
								<EyeOutlined />
								预览
							</a-button>
						</div>
					</div>
				</template>
				<a-textarea
					v-model:value="formData.content"
					placeholder="请输入邮件模板"
					allow-clear
					:auto-size="{ minRows: 20, maxRows: 20 }"
				/>
			</a-form-item>
		</a-form>
		<template #footer>
			<a-button class="xn-mr8" @click="onClose">关闭</a-button>
			<a-button type="primary" @click="onSubmit">保存</a-button>
		</template>
	</xn-form-container>
	<email-preview ref="emailPreviewRef" />
</template>

<script setup name="emailTemplateSetting">
	import EmailPreview from './preview.vue'
	import { required } from '@/utils/formRules'
	// 默认是关闭状态
	const open = ref(false)
	const formRef = ref()
	const emailPreviewRef = ref()
	const emit = defineEmits({ successful: null })
	// 表单数据
	const formData = ref({})
	// 打开编辑的key
	const recordConfigKey = ref('')
	// 默认要校验的
	const formRules = {
		subject: [required('请输入邮件主题')],
		content: [required('请输入邮件模板内容')]
	}
	// 打开抽屉
	const onOpen = (record) => {
		open.value = true
		formData.value = JSON.parse(record.configValue)
		recordConfigKey.value = record.configKey
	}
	// 关闭抽屉
	const onClose = () => {
		formData.value = {}
		recordConfigKey.value = ''
		open.value = false
	}
	// 提交数据
	const onSubmit = () => {
		// 验证完成后把数据回传到父界面，由父界面调用接口进行修改
		formRef.value.validate().then((value) => {
			const configKeyClone = recordConfigKey.value
			const configValueClone = JSON.stringify(value)
			const param = [
				{
					configKey: configKeyClone,
					configValue: configValueClone
				}
			]
			emit('successful', param)
			onClose()
		})
	}
	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>
