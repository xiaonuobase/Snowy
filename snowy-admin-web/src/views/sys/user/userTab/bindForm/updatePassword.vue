<template>
	<div>
		<a-modal title="修改密码" :width="400" :open="visible" :destroy-on-close="true" @cancel="onClose">
			<a-skeleton active v-if="!updatePasswordConfig" />
			<a-form v-else ref="formRef" :model="formState" :rules="formRules" layout="vertical">
				<div v-if="updatePasswordConfig.SNOWY_SYS_DEFAULT_PASSWORD_UPDATE_VALID_TYPE_FOR_B === 'OLD'">
					<a-form-item label="旧密码：" name="password" has-feedback>
						<a-input-password
							v-model:value="formState.password"
							placeholder="请输入原密码"
							allow-clear
							autocomplete="off"
						/>
					</a-form-item>
				</div>
				<a-form-item
					name="phone"
					has-feedback
					v-if="updatePasswordConfig.SNOWY_SYS_DEFAULT_PASSWORD_UPDATE_VALID_TYPE_FOR_B === 'PHONE'"
				>
					<a-input v-model:value="formState.phone" placeholder="请输入手机号" allow-clear autocomplete="off" />
				</a-form-item>
				<a-form-item
					name="email"
					has-feedback
					v-if="updatePasswordConfig.SNOWY_SYS_DEFAULT_PASSWORD_UPDATE_VALID_TYPE_FOR_B === 'EMAIL'"
				>
					<a-input v-model:value="formState.email" placeholder="请输入邮箱号" allow-clear autocomplete="off" />
				</a-form-item>
				<a-form-item
					name="validCode"
					v-if="
						updatePasswordConfig.SNOWY_SYS_DEFAULT_PASSWORD_UPDATE_VALID_TYPE_FOR_B === 'PHONE' ||
						updatePasswordConfig.SNOWY_SYS_DEFAULT_PASSWORD_UPDATE_VALID_TYPE_FOR_B === 'EMAIL'
					"
				>
					<a-row :gutter="8">
						<a-col :span="17">
							<a-input v-model:value="formState.validCode" placeholder="验证码">
								<template #prefix>
									<mail-outlined class="text-black text-opacity-25" />
								</template>
							</a-input>
						</a-col>
						<a-col :span="7">
							<a-button class="xn-wd" @click="getValidCode" :disabled="state.sendBtn">
								{{ (!state.sendBtn && '获取验证码') || state.time + ' s' }}
							</a-button>
						</a-col>
					</a-row>
				</a-form-item>
				<a-form-item name="newPassword" has-feedback>
					<a-input-password
						v-model:value="formState.newPassword"
						placeholder="请输入新密码"
						allow-clear
						autocomplete="off"
					/>
				</a-form-item>
			</a-form>
			<template #footer>
				<a-button class="xn-mr8" @click="onClose">关闭</a-button>
				<a-button type="primary" :loading="submitLoading" @click="onSubmit">保存</a-button>
			</template>
		</a-modal>
		<a-modal
			v-model:open="captchaVisible"
			:width="400"
			title="验证"
			@cancel="captchaHandleCancel"
			@ok="captchaHandleOk"
			destroy-on-close
		>
			<a-form ref="updatePasswordFormModalRef" :model="captchaFormModalData" :rules="captchaFormModalRules">
				<a-form-item name="validCode">
					<a-row :gutter="8">
						<a-col :span="17">
							<a-input v-model:value="captchaFormModalData.validCode" placeholder="请输入验证码" size="large">
								<template #prefix>
									<verified-outlined class="xn-color-00025" />
								</template>
							</a-input>
						</a-col>
						<a-col :span="7">
							<img :src="captchaValidCodeBase64" class="xn-findform-line" @click="getCaptcha" />
						</a-col>
					</a-row>
				</a-form-item>
			</a-form>
		</a-modal>
	</div>
</template>

<script setup name="updatePassword">
	import { required, rules } from '@/utils/formRules'
	import userCenterApi from '@/api/sys/userCenterApi'
	import smCrypto from '@/utils/smCrypto'
	import { cloneDeep } from 'lodash-es'
	import { message } from 'ant-design-vue'

	// 定义emit事件
	const emit = defineEmits({ successful: null })
	// 默认是关闭状态
	const visible = ref(false)
	const formRef = ref()
	// 表单数据
	const formState = ref({})
	const submitLoading = ref(false)
	const updatePasswordConfig = ref({})

	// 打开抽屉
	const onOpen = () => {
		visible.value = true
		// 获得密码策略配置
		userCenterApi.userGetUpdatePasswordValidConfig().then((data) => {
			updatePasswordConfig.value = data
		})
	}
	// 关闭抽屉
	const onClose = () => {
		visible.value = false
		updatePasswordConfig.value = {}
		formRef.value.resetFields()
	}
	// 默认要校验的
	const formRules = {
		password: [required('请输入原密码')],
		phone: [required('请输入手机号'), rules.phone],
		phoneValidCode: [required('请输入手机验证码'), rules.number],
		email: [required('请输入邮箱号'), rules.email],
		emailValidCode: [required('请输入邮箱验证码'), rules.number],
		newPassword: [
			required('请输入新密码'),
			{
				validator: (rule, value) => {
					if (!value) return Promise.resolve()

					// 检查密码长度
					const minLength = updatePasswordConfig.value.SNOWY_SYS_DEFAULT_PASSWORD_MIN_LENGTH_FOR_B
					const maxLength = updatePasswordConfig.value.SNOWY_SYS_DEFAULT_PASSWORD_MAX_LENGTH_FOR_B
					if (value.length < minLength) {
						return Promise.reject(`密码长度不能小于${minLength}位`)
					}
					if (value.length > maxLength) {
						return Promise.reject(`密码长度不能大于${maxLength}位`)
					}

					// 检查连续相同字符
					const maxSameChar =
						updatePasswordConfig.value.SNOWY_SYS_DEFAULT_PASSWORD_NOT_ALLOW_CONTINUOUS_SAME_CHARACTER_LENGTH_FOR_B
					let maxCount = 1
					let currentChar = value[0]
					let currentCount = 1
					for (let i = 1; i < value.length; i++) {
						if (value[i] === currentChar) {
							currentCount++
							maxCount = Math.max(maxCount, currentCount)
						} else {
							currentChar = value[i]
							currentCount = 1
						}
					}
					if (maxCount > maxSameChar) {
						return Promise.reject(`密码中不能包含${maxSameChar}个以上相同字符`)
					}

					// 检查密码复杂度
					const complexity = updatePasswordConfig.value.SNOWY_SYS_DEFAULT_PASSWORD_COMPLEXITY_FOR_B
					const hasNumber = /\d/.test(value)
					const hasLowerCase = /[a-z]/.test(value)
					const hasUpperCase = /[A-Z]/.test(value)
					const hasSpecial = /[!@#$%^&*(),.?":{}|<>]/.test(value)

					switch (complexity) {
						case 'REG0':
							break
						case 'REG1':
							if (!(hasNumber && (hasLowerCase || hasUpperCase))) {
								return Promise.reject('密码必须包含数字和字母')
							}
							break
						case 'REG2':
							if (!(hasNumber && hasUpperCase)) {
								return Promise.reject('密码必须包含数字和大写字母')
							}
							break
						case 'REG3':
							if (!(hasNumber && hasUpperCase && hasLowerCase && hasSpecial)) {
								return Promise.reject('密码必须包含数字、大写字母、小写字母和特殊字符')
							}
							break
						case 'REG4':
							if ([hasNumber, hasLowerCase || hasUpperCase, hasSpecial].filter(Boolean).length < 2) {
								return Promise.reject('密码至少包含数字、字母和特殊字符中的两种')
							}
							break
						case 'REG5':
							if ([hasNumber, hasUpperCase, hasLowerCase, hasSpecial].filter(Boolean).length < 3) {
								return Promise.reject('密码至少包含数字、大写字母、小写字母和特殊字符的三种')
							}
							break
					}

					return Promise.resolve()
				}
			}
		]
	}

	let state = ref({
		time: 60,
		sendBtn: false
	})
	// 获取验证码
	const getValidCode = () => {
		formRef.value
			.validateFields([
				updatePasswordConfig.value.SNOWY_SYS_DEFAULT_PASSWORD_UPDATE_VALID_TYPE_FOR_B === 'PHONE' ? 'phone' : 'email'
			])
			.then(() => {
				captchaVisible.value = true
				getCaptcha()
			})
	}

	// 提交数据
	const onSubmit = async () => {
		formRef.value
			.validate()
			.then(() => {
				submitLoading.value = true
				const cloneFormData = cloneDeep(formState.value)
				if (updatePasswordConfig.value.SNOWY_SYS_DEFAULT_PASSWORD_UPDATE_VALID_TYPE_FOR_B === 'OLD') {
					cloneFormData.password = smCrypto.doSm2Encrypt(formState.value.password)
					cloneFormData.newPassword = smCrypto.doSm2Encrypt(formState.value.newPassword)
					userCenterApi
						.userUpdatePasswordByOld(cloneFormData)
						.then(() => {
							onClose()
							emit('successful')
						})
						.finally(() => {
							submitLoading.value = false
						})
				} else if (updatePasswordConfig.value.SNOWY_SYS_DEFAULT_PASSWORD_UPDATE_VALID_TYPE_FOR_B === 'PHONE') {
					userCenterApi
						.userUpdatePasswordByPhone(cloneFormData)
						.then(() => {
							onClose()
							emit('successful')
						})
						.finally(() => {
							submitLoading.value = false
						})
				} else if (updatePasswordConfig.value.SNOWY_SYS_DEFAULT_PASSWORD_UPDATE_VALID_TYPE_FOR_B === 'EMAIL') {
					userCenterApi
						.userUpdatePasswordByEmail(cloneFormData)
						.then(() => {
							onClose()
							emit('successful')
						})
						.finally(() => {
							submitLoading.value = false
						})
				} else {
					submitLoading.value = false
					message.warning('未知的修改密码方式')
				}
			})
			.catch(() => {})
	}

	const captchaVisible = ref(false)
	const captchaFormModalData = ref({})
	const updatePasswordFormModalRef = ref()
	const captchaFormModalRules = {
		validCode: [required('图形验证码不能为空')]
	}
	const captchaValidCodeBase64 = ref('')
	// 获得图形验证码
	const getCaptcha = () => {
		userCenterApi.userGetPicCaptcha().then((data) => {
			if (data) {
				captchaValidCodeBase64.value = data.validCodeBase64
				formState.value.validCodeReqNo = data.validCodeReqNo
			}
		})
	}
	// 图形验证码窗口关闭
	const captchaHandleCancel = () => {
		captchaVisible.value = false
		captchaValidCodeBase64.value = ''
	}
	// 图形验证码提交
	const captchaHandleOk = () => {
		// 验证码
		updatePasswordFormModalRef.value.validate().then(() => {
			captchaVisible.value = false
			// 禁用发送按钮，并设置为倒计时
			state.value.sendBtn = true
			const interval = window.setInterval(() => {
				if (state.value.time-- <= 0) {
					state.value.time = 60
					state.value.sendBtn = false
					window.clearInterval(interval)
				}
			}, 1000)
			const hide = message.loading('验证码发送中..', 0)
			const param = {
				validCodeReqNo: formState.value.validCodeReqNo,
				validCode: captchaFormModalData.value.validCode
			}
			if (updatePasswordConfig.value.SNOWY_SYS_DEFAULT_PASSWORD_UPDATE_VALID_TYPE_FOR_B === 'PHONE') {
				param.phone = formState.value.phone
				userCenterApi
					.userUpdatePasswordGetPhoneValidCode(param)
					.then((validCodeReqNo) => {
						formState.value.validCodeReqNo = validCodeReqNo
						setTimeout(hide, 500)
					})
					.catch(() => {
						setTimeout(hide, 100)
						clearInterval(interval)
						state.value.sendBtn = false
					})
					.finally(() => {
						captchaFormModalData.value.validCode = ''
					})
			} else if (updatePasswordConfig.value.SNOWY_SYS_DEFAULT_PASSWORD_UPDATE_VALID_TYPE_FOR_B === 'EMAIL') {
				param.email = formState.value.email
				userCenterApi
					.userUpdatePasswordGetEmailValidCode(param)
					.then((validCodeReqNo) => {
						formState.value.validCodeReqNo = validCodeReqNo
						setTimeout(hide, 500)
					})
					.catch(() => {
						setTimeout(hide, 100)
						clearInterval(interval)
						state.value.sendBtn = false
					})
					.finally(() => {
						captchaFormModalData.value.validCode = ''
					})
			} else {
				message.warning('未知的发送验证码方式')
			}
		})
	}

	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>
