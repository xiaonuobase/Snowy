<template>
	<xn-form-container title="发送站内信" :width="550" :visible="visible" :destroy-on-close="true" @close="onClose">
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-form-item label="主题：" name="subject">
				<a-input v-model:value="formData.subject" placeholder="请输入主题" allow-clear />
			</a-form-item>
			<a-form-item label="站内信分类：" name="category">
				<a-select
					v-model:value="formData.category"
					:options="categoryOptions"
					class="xn-wd"
					placeholder="请选择站内信分类"
				/>
			</a-form-item>
			<a-form-item label="正文：" name="content">
				<a-textarea v-model:value="formData.content" placeholder="请输入正文" :auto-size="{ minRows: 5, maxRows: 5 }" />
			</a-form-item>
			<a-form-item label="接收人：" name="receiverIdList">
				<xn-user-selector
					:org-tree-api="selectorApiFunction.orgTreeApi"
					:user-page-api="selectorApiFunction.userPageApi"
					data-type="object"
					v-model:value="formData.receiverIdList"
				/>
			</a-form-item>
		</a-form>
		<template #footer>
			<a-button class="xn-mr8" @click="onClose">关闭</a-button>
			<a-button type="primary" @click="onSubmit" :loading="sendLoading">发送</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="messageForm">
	import { required } from '@/utils/formRules'
	import { message } from 'ant-design-vue'
	import messageApi from '@/api/dev/messageApi'
	import userApi from '@/api/sys/userApi'
	import tool from '@/utils/tool'

	const sendLoading = ref(false)
	// 定义emit事件
	const emit = defineEmits({ successful: null })
	// 默认是关闭状态
	const visible = ref(false)
	const formRef = ref()
	// 表单数据
	const formData = ref({})
	// 打开抽屉
	const onOpen = () => {
		visible.value = true
		formData.value = {}
	}
	// 关闭抽屉
	const onClose = () => {
		emit('successful')
		visible.value = false
	}
	// 默认要校验的
	const formRules = {
		subject: [required('请输入主题')],
		category: [required('请选择站内信分类')]
	}
	// 站内信分类字典
	const categoryOptions = tool.dictList('MESSAGE_CATEGORY')
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value
			.validate()
			.then(() => {
				sendLoading.value = true
				messageApi
					.messageSend(formData.value)
					.then(() => {
						message.success('发送成功')
						onClose()
					})
					.finally(() => {
						sendLoading.value = false
					})
			})
			.catch(() => {})
	}
	// 传递设计器需要的API
	const selectorApiFunction = {
		orgTreeApi: (param) => {
			return userApi.userOrgTreeSelector(param).then((data) => {
				return Promise.resolve(data)
			})
		},
		userPageApi: (param) => {
			return userApi.userSelector(param).then((data) => {
				return Promise.resolve(data)
			})
		}
	}
	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>
