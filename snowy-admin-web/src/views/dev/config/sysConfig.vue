<template>
	<a-spin :spinning="loadSpinning">
		<a-form
			ref="formRef"
			:model="formData"
			:rules="formRules"
			layout="vertical"
			:label-col="{ ...layout.labelCol, offset: 0 }"
			:wrapper-col="{ ...layout.wrapperCol, offset: 0 }"
			style="width: 90%"
		>
			<a-row :gutter="16">
				<a-col :span="4">
					<a-form-item label="系统LOGO：" name="SNOWY_SYS_LOGO">
						<a-upload
							v-model:file-list="formData.SNOWY_SYS_LOGO"
							class="avatar-uploader"
							list-type="picture-card"
							:show-upload-list="false"
							:custom-request="customRequest"
							accept="image/png, image/jpeg, image/jpg"
						>
							<img v-if="imageUrl" :src="imageUrl" alt="avatar" style="max-height: 100px; max-width: 100px" />
							<div v-else>
								<plus-outlined />
								<div class="ant-upload-text">上传</div>
							</div>
						</a-upload>
					</a-form-item>
				</a-col>
			</a-row>
			<a-row :gutter="8">
				<a-col :span="8">
					<a-form-item label="系统名称：" name="SNOWY_SYS_NAME">
						<a-input v-model:value="formData.SNOWY_SYS_NAME" placeholder="请输入系统名称" />
					</a-form-item>
				</a-col>
				<a-col :span="14">
					<a-row :gutter="8">
						<a-col :span="12">
							<a-form-item label="后台验证码开关：" name="SNOWY_SYS_DEFAULT_CAPTCHA_OPEN_FLAG_FOR_B">
								<a-switch
									v-model:checked="formData.SNOWY_SYS_DEFAULT_CAPTCHA_OPEN_FLAG_FOR_B"
									checked-children="开"
									un-checked-children="关"
									placeholder="请选择后台验证码开关"
								/>
							</a-form-item>
						</a-col>
						<a-col :span="12">
							<a-form-item label="前台验证码开关：" name="SNOWY_SYS_DEFAULT_CAPTCHA_OPEN_FLAG_FOR_C">
								<a-switch
									v-model:checked="formData.SNOWY_SYS_DEFAULT_CAPTCHA_OPEN_FLAG_FOR_C"
									checked-children="开"
									un-checked-children="关"
									placeholder="请选择前台验证码开关"
								/>
							</a-form-item>
						</a-col>
					</a-row>
				</a-col>
			</a-row>
			<a-row :gutter="8">
				<a-col :span="8">
					<a-form-item label="系统版本：" name="SNOWY_SYS_VERSION">
						<a-input v-model:value="formData.SNOWY_SYS_VERSION" placeholder="请输入系统版本" />
					</a-form-item>
				</a-col>
				<a-col :span="14">
					<a-row :gutter="8">
						<a-col :span="12">
							<a-form-item label="全局后台验证码失效时间：" name="SNOWY_SYS_DEFAULT_CAPTCHA_EXPIRED_DURATION_FOR_B">
								<a-input-number
									v-model:value="formData.SNOWY_SYS_DEFAULT_CAPTCHA_EXPIRED_DURATION_FOR_B"
									placeholder="分钟"
								>
									<template #addonAfter> 分钟 </template>
								</a-input-number>
							</a-form-item>
						</a-col>
						<a-col :span="12">
							<a-form-item label="全局前台验证码失效时间：" name="SNOWY_SYS_DEFAULT_CAPTCHA_EXPIRED_DURATION_FOR_C">
								<a-input-number
									v-model:value="formData.SNOWY_SYS_DEFAULT_CAPTCHA_EXPIRED_DURATION_FOR_C"
									placeholder="分钟"
								>
									<template #addonAfter> 分钟 </template>
								</a-input-number>
							</a-form-item>
						</a-col>
					</a-row>
				</a-col>
			</a-row>
			<a-row :gutter="8">
				<a-col :span="8">
					<a-form-item label="版权信息：" name="SNOWY_SYS_COPYRIGHT">
						<a-input v-model:value="formData.SNOWY_SYS_COPYRIGHT" placeholder="请输入版权信息" />
					</a-form-item>
				</a-col>
				<a-col :span="14">
					<a-row :gutter="8">
						<a-col :span="12">
							<a-form-item label="默认文件引擎：" name="SNOWY_SYS_DEFAULT_FILE_ENGINE">
								<a-radio-group
									v-model:value="formData.SNOWY_SYS_DEFAULT_FILE_ENGINE"
									:options="fileEngineOptions"
									placeholder="请选择系统默认文件引擎"
								/>
							</a-form-item>
						</a-col>
						<a-col :span="12">
							<a-form-item label="默认短信引擎：" name="SNOWY_SYS_DEFAULT_SMS_ENGINE">
								<a-radio-group
									v-model:value="formData.SNOWY_SYS_DEFAULT_SMS_ENGINE"
									:options="smsEngineOptions"
									placeholder="请选择系统默认短信引擎"
								/>
							</a-form-item>
						</a-col>
					</a-row>
				</a-col>
			</a-row>
			<a-row :gutter="8">
				<a-col :span="8">
					<a-form-item label="版权链接URL：" name="SNOWY_SYS_COPYRIGHT_URL">
						<a-input v-model:value="formData.SNOWY_SYS_COPYRIGHT_URL" placeholder="请输入版权链接URL" />
					</a-form-item>
				</a-col>
				<a-col :span="14">
					<a-row :gutter="8">
						<a-col :span="12">
							<a-form-item label="默认邮件引擎：" name="SNOWY_SYS_DEFAULT_EMAIL_ENGINE">
								<a-radio-group
									v-model:value="formData.SNOWY_SYS_DEFAULT_EMAIL_ENGINE"
									:options="emailEngineOptions"
									placeholder="请选择系统默认邮件引擎"
								/>
							</a-form-item>
						</a-col>
						<a-col :span="12">
							<a-form-item label="默认消息推送引擎：" name="SNOWY_SYS_DEFAULT_PUSH_ENGINE">
								<a-radio-group
									v-model:value="formData.SNOWY_SYS_DEFAULT_PUSH_ENGINE"
									:options="pushEngineOptions"
									placeholder="请选择系统默认消息推送引擎"
								/>
							</a-form-item>
						</a-col>
					</a-row>
				</a-col>
			</a-row>

			<a-row :gutter="16">
				<a-col :span="8">
					<a-form-item label="默认快捷方式：" name="SNOWY_SYS_DEFAULT_WORKBENCH_DATA">
						<menuTreeSelect ref="menuTreeSelectRef" :resultData="true" />
					</a-form-item>
				</a-col>
				<a-col :span="14">
					<a-form-item label="系统描述：" name="SNOWY_SYS_DEFAULT_DESCRRIPTION">
						<a-textarea
							v-model:value="formData.SNOWY_SYS_DEFAULT_DESCRRIPTION"
							placeholder="请输入系统描述"
							:auto-size="{ minRows: 3, maxRows: 6 }"
						/>
					</a-form-item>
				</a-col>
			</a-row>
			<a-row :gutter="14">
				<a-col :span="12">
					<a-form-item>
						<a-button type="primary" :loading="submitLoading" @click="onSubmit()">保存</a-button>
						<a-button class="xn-ml10" @click="resetForm">重置</a-button>
					</a-form-item>
				</a-col>
			</a-row>
		</a-form>
	</a-spin>
</template>

<script setup name="sysConfig">
	import { cloneDeep } from 'lodash-es'
	import { required } from '@/utils/formRules'
	import { message } from 'ant-design-vue'
	import { globalStore } from '@/store'
	import configApi from '@/api/dev/configApi'
	import tool from '@/utils/tool'
	import MenuTreeSelect from '@/components/TreeSelect/menuTreeSelect.vue'

	// 定义emit事件
	const emit = defineEmits({ successful: null })
	const formRef = ref()
	// 表单数据，也就是默认给一些数据
	const formData = ref({})
	const submitLoading = ref(false)
	const imageUrl = ref('')
	const menuTreeSelectRef = ref()
	const loadSpinning = ref(true)
	const store = globalStore()
	// 查询此界面的配置项,并转为表单
	const param = {
		category: 'SYS_BASE'
	}
	configApi.configList(param).then((data) => {
		loadSpinning.value = false
		if (data) {
			data.forEach((item) => {
				// 如果是系统的logo，它这个组件要一个数组，我将其单独处理
				if (item.configKey === 'SNOWY_SYS_LOGO') {
					formData.value[item.configKey] = [item.configValue]
					// 让其回显
					imageUrl.value = item.configValue
				} else if (item.configKey === 'SNOWY_SYS_DEFAULT_WORKBENCH_DATA') {
					try {
						menuTreeSelectRef.value.setSelectData(JSON.parse(item.configValue).shortcut)
						// eslint-disable-next-line no-empty
					} catch (e) {}
				} else {
					formData.value[item.configKey] = transferBooleanInValue(item.configValue)
				}
			})
		} else {
			message.warning('表单项不存在，请初始化数据库')
		}
	})
	// 文件引擎
	const fileEngineOptions = tool.dictList('FILE_ENGINE')
	// 短信引擎
	const smsEngineOptions = tool.dictList('SMS_ENGINE')
	// 邮件引擎
	const emailEngineOptions = tool.dictList('EMAIL_ENGINE')
	// 消息推送引擎
	const pushEngineOptions = tool.dictList('PUSH_ENGINE')

	// 转换值
	const transferBooleanInValue = (value) => {
		if (value === 'true' || value === 'false') {
			return value === 'true'
		} else {
			return value
		}
	}
	const customRequest = (data) => {
		formData.value.SNOWY_SYS_LOGO = ref([])
		getBase64(data.file)
			.then((res) => {
				imageUrl.value = res
				formData.value.SNOWY_SYS_LOGO.push(res)
			})
			.catch((err) => {})
	}

	// 文件转base64，用于显示图片
	const getBase64 = (file) => {
		return new Promise((resolve, reject) => {
			// FileReader类就是专门用来读文件的
			const reader = new FileReader()
			reader.readAsDataURL(file)
			// 成功和失败返回对应的信息，reader.result一个base64，可以直接使用
			reader.onload = () => resolve(reader.result)
			// 失败返回失败的信息
			reader.onerror = (error) => reject(error)
		})
	}

	// 默认要校验的
	const formRules = {
		SNOWY_SYS_LOGO: [required('请上传系统LOGO')],
		SNOWY_SYS_NAME: [required('请输入系统名称')],
		SNOWY_SYS_VERSION: [required('请输入系统版本')],
		SNOWY_SYS_COPYRIGHT: [required('请输入版权信息')],
		SNOWY_SYS_COPYRIGHT_URL: [required('请输入版权链接URL')],
		SNOWY_SYS_DEFAULT_FILE_ENGINE: [required('请选择系统默认文件引擎')],
		SNOWY_SYS_DEFAULT_SMS_ENGINE: [required('请选择系统默认短信引擎')],
		SNOWY_SYS_DEFAULT_EMAIL_ENGINE: [required('请选择系统默认邮件引擎')],
		SNOWY_SYS_DEFAULT_PUSH_ENGINE: [required('请选择系统默认消息推送引擎')],
		SNOWY_SYS_DEFAULT_CAPTCHA_OPEN_FLAG_FOR_C: [required('请选择前台验证码开关')],
		SNOWY_SYS_DEFAULT_CAPTCHA_OPEN_FLAG_FOR_B: [required('请选择后台验证码开关')],
		SNOWY_SYS_DEFAULT_CAPTCHA_EXPIRED_DURATION_FOR_C: [required('请输入过期时间')],
		SNOWY_SYS_DEFAULT_CAPTCHA_EXPIRED_DURATION_FOR_B: [required('请输入过期时间')],
		SNOWY_SYS_DEFAULT_PASSWORD: [required('请输入系统重置密码默认密码')]
	}
	// 表单固定label实现
	const labelCol = ref({
		style: {
			width: '200px'
		}
	})
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value
			.validate()
			.then(() => {
				submitLoading.value = true
				let submitParam = cloneDeep(formData.value)
				// 创建快捷方式
				const shortcut = {
					shortcut: menuTreeSelectRef.value.getSelectData()
				}
				submitParam.SNOWY_SYS_DEFAULT_WORKBENCH_DATA = JSON.stringify(shortcut)
				submitParam.SNOWY_SYS_LOGO = submitParam.SNOWY_SYS_LOGO[0]
				const param = Object.entries(submitParam).map((item) => {
					return {
						configKey: item[0],
						configValue: item[1]
					}
				})
				configApi
					.configEditForm(param)
					.then(() => {
						// 执行成功后改变界面使其生效
						tool.data.set('SNOWY_SYS_BASE_CONFIG', submitParam)
						store.setSysBaseConfig(submitParam)
					})
					.finally(() => {
						submitLoading.value = false
					})
			})
			.catch(() => {})
	}
	// 重置表单，且设置默认值
	const resetForm = () => {
		imageUrl.value = ''
		formData.value = {
			SNOWY_SYS_DEFAULT_CAPTCHA_OPEN_FLAG_FOR_C: true,
			SNOWY_SYS_DEFAULT_CAPTCHA_OPEN_FLAG_FOR_B: true,
			SNOWY_SYS_DEFAULT_FILE_ENGINE: 'LOCAL',
			SNOWY_SYS_DEFAULT_SMS_ENGINE: 'XIAONUO',
			SNOWY_SYS_DEFAULT_EMAIL_ENGINE: 'LOCAL',
			SNOWY_SYS_DEFAULT_PUSH_ENGINE: 'DINGTALK'
		}
	}
	const layout = {
		labelCol: {
			span: 16
		},
		wrapperCol: {
			span: 22
		}
	}
</script>
