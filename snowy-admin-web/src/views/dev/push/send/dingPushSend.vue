<template>
	<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
		<a-form-item label="推送方式：" name="pushType">
			<a-radio-group v-model:value="formData.pushType" button-style="solid" @change="pushTypeChange(formData.pushType)">
				<a-radio-button value="TEXT">文本消息</a-radio-button>
				<a-radio-button value="MARKDOWN">Markdown</a-radio-button>
				<a-radio-button value="LINK">LINK</a-radio-button>
			</a-radio-group>
		</a-form-item>
		<a-form-item label="通知类型：" name="noticeType" v-if="formData.pushType !== 'LINK'">
			<a-radio-group v-model:value="formData.noticeType">
				<a-radio value="NONE">无</a-radio>
				<a-radio value="PHONE" v-if="formData.pushType === 'TEXT'">指定手机号</a-radio>
				<a-radio value="ALL">通知所有人</a-radio>
			</a-radio-group>
		</a-form-item>
		<a-form-item
			label="消息标题："
			name="title"
			v-if="formData.pushType === 'MARKDOWN' || formData.pushType === 'LINK'"
		>
			<a-input v-model:value="formData.title" placeholder="请输入消息标题" allow-clear />
		</a-form-item>
		<a-form-item label="消息内容：" name="content" v-if="formData.pushType !== 'MARKDOWN'">
			<a-textarea
				v-model:value="formData.content"
				placeholder="请输入推送内容"
				:auto-size="{ minRows: 5, maxRows: 10 }"
			/>
		</a-form-item>
		<a-form-item label="Markdown内容：" v-if="formData.pushType === 'MARKDOWN'">
			<XnMdEditor
				v-model="markdownContent"
				@upload-image="handleUploadImage"
				:disabled-menus="[]"
				height="500px"
				left-toolbar="undo redo clear | h bold italic strikethrough quote | ul ol table hr | link image"
			/>
		</a-form-item>
		<a-form-item label="图片URL：" name="picUrl" v-if="formData.pushType === 'LINK'">
			<a-input v-model:value="formData.picUrl" placeholder="请输入图片URL" allow-clear />
		</a-form-item>
		<a-form-item label="跳转地址：" name="messageUrl" v-if="formData.pushType === 'LINK'">
			<a-input v-model:value="formData.messageUrl" placeholder="请输入跳转地址" allow-clear />
		</a-form-item>
		<div v-if="formData.pushType === 'TEXT'">
			<a-form-item label="手机号：" name="phones" v-if="formData.noticeType === 'PHONE'">
				<a-textarea
					v-model:value="formData.phones"
					placeholder="请输入手机号，多个逗号拼接"
					:auto-size="{ minRows: 2, maxRows: 5 }"
				/>
			</a-form-item>
		</div>
	</a-form>
</template>

<script setup name="DingPushSend">
	import { message } from 'ant-design-vue'
	import { required } from '@/utils/formRules'
	import pushApi from '@/api/dev/pushApi'
	import { cloneDeep } from 'lodash-es'
	import { XnMdEditor } from '@/components/XnMdEditor/mdEditor'
	import fileApi from '@/api/dev/fileApi'

	// 定义emit事件
	const emit = defineEmits({ loadingStart: null, loadingEnd: null })
	const formRef = ref()
	// 表单数据，也就是默认给一些数据
	const formData = ref({
		noticeType: 'NONE',
		pushType: 'TEXT'
	})
	const markdownContent = ref('')

	// 默认要校验的
	const formRules = {
		pushType: [required('请选择推送方式')],
		title: [required('请输入消息标题')],
		picUrl: [required('请输入图片URL')],
		messageUrl: [required('请输入跳转地址')],
		phones: [required('请输入手机号，多个逗号拼接')],
		noticeType: [required('请选择推送类型')],
		content: [required('请输入推送内容')]
	}
	// 切换消息类型
	const pushTypeChange = (value) => {
		if (value) {
			formData.value.noticeType = 'NONE'
		}
	}
	// 推送消息
	const send = () => {
		formRef.value
			.validate()
			.then(() => {
				emit('loadingStart')
				const formDataClone = cloneDeep(formData.value)
				if (formDataClone.pushType === 'TEXT') {
					pushApi
						.pushDingTalkText(formData.value)
						.then(() => {
							message.success('推送成功')
						})
						.catch(() => {})
						.finally(() => {
							emit('loadingEnd')
						})
				} else if (formDataClone.pushType === 'MARKDOWN') {
					formData.value.content = markdownContent.value
					pushApi
						.pushDingTalkMarkdown(formData.value)
						.then(() => {
							message.success('推送成功')
							markdownContent.value = ''
						})
						.catch(() => {})
						.finally(() => {
							emit('loadingEnd')
						})
				} else if (formDataClone.pushType === 'LINK') {
					pushApi
						.pushDingTalkLink(formData.value)
						.then(() => {
							message.success('推送成功')
						})
						.catch(() => {})
						.finally(() => {
							emit('loadingEnd')
						})
				} else {
					message.warning('不支持的推送类型')
				}
			})
			.catch(() => {})
	}

	// md编辑器上传图片
	const handleUploadImage = (event, insertImage, files) => {
		const fileData = new FormData()
		fileData.append('file', files[0])
		fileApi.fileUploadLocalReturnUrl(fileData).then((data) => {
			// 上传成功后回填编辑器中
			insertImage({
				url: data,
				desc: files[0].name
				// width: 'auto',
				// height: 'auto'
			})
		})
	}
	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		send
	})
</script>
