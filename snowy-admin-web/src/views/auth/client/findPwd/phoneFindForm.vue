<template>
	<a-form ref="phoneLoginFormRef" :model="phoneFormData" :rules="formRules">
		<a-form-item name="phone">
			<a-input v-model:value="phoneFormData.phone" placeholder="请输入手机号" size="large">
				<template #prefix>
					<mobile-outlined class="xn-color-00025" />
				</template>
			</a-input>
		</a-form-item>
		<a-form-item name="phoneValidCode">
			<a-row :gutter="8">
				<a-col :span="16">
					<a-input v-model:value="phoneFormData.phoneValidCode" placeholder="请输入短信验证码" size="large">
						<template #prefix>
							<mail-outlined class="xn-color-00025" />
						</template>
					</a-input>
				</a-col>
				<a-col :span="8">
					<a-button size="large" class="xn-wd" @click="getPhoneValidCode" :disabled="state.smsSendBtn">{{
						(!state.smsSendBtn && '获取验证码') || state.time + ' s'
					}}</a-button>
				</a-col>
			</a-row>
		</a-form-item>

		<a-form-item name="newPassword">
			<a-input-password v-model:value="phoneFormData.newPassword" placeholder="请输入新密码" size="large">
				<template #prefix>
					<LockOutlined class="xn-color-00025" />
				</template>
			</a-input-password>
		</a-form-item>

		<a-form-item>
			<a-row :gutter="8">
				<a-col :span="8">
					<a-button class="xn-wd" round size="large" href="/front/client/login">返回登录</a-button>
				</a-col>
				<a-col :span="16">
					<a-button type="primary" class="xn-wd" :loading="isFind" round size="large" @click="submitReset">
						重置密码
					</a-button>
				</a-col>
			</a-row>
		</a-form-item>
	</a-form>
	<a-modal v-model:open="visible" :width="400" title="机器验证" @cancel="handleCancel" @ok="handleOk">
		<a-form ref="phoneLoginFormModalRef" :model="phoneFormModalData" :rules="formModalRules">
			<a-form-item name="validCode">
				<a-row :gutter="8">
					<a-col :span="17">
						<a-input v-model:value="phoneFormModalData.validCode" placeholder="机器验证" size="large">
							<template #prefix>
								<verified-outlined class="xn-color-00025" />
							</template>
						</a-input>
					</a-col>
					<a-col :span="7">
						<img :src="validCodeBase64" class="xn-findform-line" @click="getPhonePicCaptcha" />
					</a-col>
				</a-row>
			</a-form-item>
		</a-form>
	</a-modal>
</template>

<script setup name="phoneFindForm">
	import { message } from 'ant-design-vue'
	import router from '@/router'
	import { required, rules } from '@/utils/formRules'
	import clientUserCenterApi from '@/api/client/clientUserCenterApi'
	import smCrypto from '@/utils/smCrypto'
	const phoneLoginFormRef = ref()
	const phoneFormData = ref({})
	const isFind = ref(false)
	let state = ref({
		time: 60,
		smsSendBtn: false
	})
	let formRules = ref({})
	const phoneValidCodeReqNo = ref('')

	// 点击获取短信验证码
	const getPhoneValidCode = () => {
		formRules.value.phone = [required('请输入手机号'), rules.phone]
		delete formRules.value.phoneValidCode
		delete formRules.value.newPassword
		phoneLoginFormRef.value.validate().then(() => {
			// 显示弹框
			visible.value = true
			// 获取内部图片验证码
			getPhonePicCaptcha()
		})
	}
	// 点击找回按钮
	const submitReset = () => {
		formRules.value.phone = [required('请输入手机号'), rules.phone]
		formRules.value.phoneValidCode = [required('请输入验证码'), rules.number]
		formRules.value.newPassword = [required('请输入新密码')]

		phoneLoginFormRef.value
			.validate()
			.then(() => {
				phoneFormData.value.validCode = phoneFormData.value.phoneValidCode
				phoneFormData.value.validCodeReqNo = phoneValidCodeReqNo.value
				phoneFormData.value.newPassword = smCrypto.doSm2Encrypt(phoneFormData.value.newPassword)
				isFind.value = true
				clientUserCenterApi
					.clientUserFindPasswordByPhone(phoneFormData.value)
					.then(() => {
						router.replace({
							path: '/'
						})
						message.success('找回成功')
					})
					.finally(() => {
						isFind.value = false
					})
			})
			.catch(() => {})
	}

	// 弹框的
	const visible = ref(false)
	const phoneLoginFormModalRef = ref()
	const phoneFormModalData = ref({})
	const validCodeBase64 = ref('')
	const formModalRules = {
		validCode: [required(), rules.lettersNum]
	}
	const getPhonePicCaptcha = () => {
		clientUserCenterApi.clientUserGetPicCaptcha().then((data) => {
			validCodeBase64.value = data.validCodeBase64
			phoneFormModalData.value.validCodeReqNo = data.validCodeReqNo
		})
	}
	const handleCancel = () => {
		visible.value = false
	}
	const handleOk = () => {
		// 获取到里面的验证码，并发送短信
		phoneLoginFormModalRef.value.validate().then(() => {
			visible.value = false
			// 发送短信，首先拿到刚刚输入的手机号
			phoneFormModalData.value.phone = phoneFormData.value.phone
			// 禁用发送按钮，并设置为倒计时
			state.value.smsSendBtn = true
			const interval = window.setInterval(() => {
				if (state.value.time-- <= 0) {
					state.value.time = 60
					state.value.smsSendBtn = false
					window.clearInterval(interval)
				}
			}, 1000)
			const hide = message.loading('验证码发送中..', 0)

			clientUserCenterApi
				.clientUserFindPasswordGetPhoneValidCode(phoneFormModalData.value)
				.then((data) => {
					phoneValidCodeReqNo.value = data
					visible.value = false
					setTimeout(hide, 500)
				})
				.catch(() => {
					setTimeout(hide, 100)
					clearInterval(interval)
					state.value.smsSendBtn = false
				})
				.finally(() => {
					phoneFormModalData.value.validCode = ''
				})
		})
	}
</script>
