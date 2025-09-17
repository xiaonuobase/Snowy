<template>
	<div>
		<a-modal title="绑定动态口令" :width="800" :open="visible" :destroy-on-close="true" @cancel="onClose">
			<a-skeleton active v-if="!otpInfo" />
			<div v-else>
				<a-alert type="info" banner class="mb-3">
					<template #description>
						<p>1.打开Google Authenticator或者Okta Verify等动态口令应用。</p>
						<p>2.点击“扫一扫”或者“手动输入”，将动态口令应用中的二维码扫描到应用中。</p>
						<p>3.在下方输入框中输入动态口令，即可完成绑定/解绑。</p>
					</template>
				</a-alert>
				<a-row :gutter="8">
					<a-col :xs="24" :sm="24" :md="24" :lg="8" :xl="8">
						<img style="width: 100%;vertical-align: middle" :src="otpInfo.otpInfoBase64" />
					</a-col>
					<a-col :xs="24" :sm="24" :md="24" :lg="16" :xl="16">
						<a-descriptions :column="1" size="middle" bordered class="mb-2">
							<a-descriptions-item label="发行者">{{otpInfo.otpInfo.issuer}}</a-descriptions-item>
							<a-descriptions-item label="账号">{{otpInfo.otpInfo.account}}</a-descriptions-item>
							<a-descriptions-item label="密钥">{{otpInfo.otpInfo.secretKey}}</a-descriptions-item>
							<a-descriptions-item label="算法">{{otpInfo.otpInfo.algorithm}}</a-descriptions-item>
							<a-descriptions-item label="位数">{{otpInfo.otpInfo.digits}}</a-descriptions-item>
							<a-descriptions-item label="周期">{{otpInfo.otpInfo.period}}</a-descriptions-item>
						</a-descriptions>
					</a-col>
				</a-row>
				<a-form ref="formRef" :model="formState" :rules="formRules" layout="vertical">
					<a-form-item
						label="动态口令"
						name="otpCode"
						has-feedback
					>
						<a-input v-model:value="formState.otpCode" placeholder="请输入动态口令" allow-clear autocomplete="off"/>
					</a-form-item>
				</a-form>
			</div>
			<template #footer>
				<a-button class="xn-mr8" @click="onClose">关闭</a-button>
				<a-button type="primary" :loading="submitLoading" @click="onSubmit">保存</a-button>
			</template>
		</a-modal>
	</div>
</template>

<script setup name="bindOtp">
	import { required } from '@/utils/formRules'
	import userCenterApi from '@/api/sys/userCenterApi'
	import {message} from "ant-design-vue";

	// 定义emit事件
	const emit = defineEmits({ successful: null })
	// 默认是关闭状态
	const visible = ref(false)
	const formRef = ref()
	// 表单数据
	const formState = ref({})
	const submitLoading = ref(false)
	const otpInfo = ref({otpInfo: {}, otpInfoBase64: ''})
	const bindType = ref()
	// 打开抽屉
	const onOpen = async (type) => {
		visible.value = true
		bindType.value = type
		// 获得动态口令信息
		await userCenterApi.userCenterGetOtpInfo().then((data) => {
			otpInfo.value = data
		})
	}
	// 关闭抽屉
	const onClose = () => {
		visible.value = false
		otpInfo.value = {otpInfo: {}, otpInfoBase64: ''}
		formState.value = {}
		formRef.value.resetFields()
	}
	// 默认要校验的
	const formRules = {
		otpCode: [required('请输入动态口令')]
	}

	// 提交数据
	const onSubmit = async () => {
		formRef.value
			.validate()
			.then(() => {
				submitLoading.value = true
				if(bindType.value === 'bind') {
					userCenterApi
						.userCenterBindOtp(formState.value)
						.then(() => {
							message.success('绑定成功')
							visible.value = false
							emit('successful')
						})
						.finally(() => {
							submitLoading.value = false
						})
				} else {
					userCenterApi
						.userCenterUnBindOtp(formState.value)
						.then(() => {
							message.success('解绑成功')
							visible.value = false
							formRef.value.resetFields()
							emit('successful')
						})
						.finally(() => {
							submitLoading.value = false
						})
				}
			})
			.catch(() => {})
	}

	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>
