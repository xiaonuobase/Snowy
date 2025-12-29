<template>
	<xn-form-container
		:title="formData.id ? '编辑用户' : '增加用户'"
		:width="800"
		:visible="visible"
		:destroy-on-close="true"
		:body-style="{ 'padding-top': '0px' }"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-tabs v-model:activeKey="activeTabsKey">
				<a-tab-pane key="1" tab="基础信息" force-render>
					<a-row :gutter="16">
						<a-col :span="12">
							<a-form-item label="账号：" name="account">
								<a-input v-model:value="formData.account" placeholder="请输入账号" allow-clear />
							</a-form-item>
						</a-col>
						<a-col :span="12">
							<a-form-item label="姓名：" name="name">
								<a-input v-model:value="formData.name" placeholder="请输入姓名" allow-clear />
							</a-form-item>
						</a-col>
					</a-row>
					<a-row :gutter="16">
						<a-col :span="12">
							<a-form-item label="性别：" name="gender">
								<a-radio-group v-model:value="formData.gender" :options="genderOptions"> </a-radio-group>
							</a-form-item>
						</a-col>
						<a-col :span="12">
							<a-form-item label="昵称：" name="nickname">
								<a-input v-model:value="formData.nickname" placeholder="请输入昵称" allow-clear />
							</a-form-item>
						</a-col>
					</a-row>
					<a-row :gutter="16">
						<a-col :span="12">
							<a-form-item label="手机号：" name="phone">
								<a-input v-model:value="formData.phone" placeholder="请输入手机" allow-clear />
							</a-form-item>
						</a-col>
						<a-col :span="12">
							<a-form-item label="邮箱：" name="email">
								<a-input v-model:value="formData.email" placeholder="请输入邮箱" allow-clear />
							</a-form-item>
						</a-col>
					</a-row>
					<a-row :gutter="16">
						<a-col :span="12">
							<a-form-item label="出生日期：" name="birthday">
								<a-date-picker v-model:value="formData.birthday" value-format="YYYY-MM-DD" style="width: 100%" />
							</a-form-item>
						</a-col>
						<a-col :span="12">
							<a-form-item label="年龄：" name="age">
								<a-input-number v-model:value="formData.age" placeholder="请输入年龄" allow-clear style="width: 100%" />
							</a-form-item>
						</a-col>
					</a-row>
				</a-tab-pane>
				<a-tab-pane key="2" tab="更多信息" force-render>
					<a-row :gutter="16">
						<a-col :span="12">
							<a-form-item label="民族：" name="nation">
								<a-select v-model:value="formData.nation" placeholder="请选择民族" :options="nationOptions"> </a-select>
							</a-form-item>
						</a-col>
						<a-col :span="12">
							<a-form-item label="籍贯：" name="nativePlace">
								<a-input v-model:value="formData.nativePlace" placeholder="请输入籍贯" allow-clear />
							</a-form-item>
						</a-col>
					</a-row>
					<a-row :gutter="16">
						<a-col :span="12">
							<a-form-item label="家庭住址：" name="homeAddress">
								<a-textarea
									v-model:value="formData.homeAddress"
									placeholder="请输入家庭住址"
									:auto-size="{ minRows: 2, maxRows: 5 }"
									allow-clear
								/>
							</a-form-item>
						</a-col>
						<a-col :span="12">
							<a-form-item label="通信地址：" name="mailingAddress">
								<a-textarea
									v-model:value="formData.mailingAddress"
									placeholder="请输入通信地址"
									:auto-size="{ minRows: 2, maxRows: 5 }"
									allow-clear
								/>
							</a-form-item>
						</a-col>
					</a-row>
					<a-row :gutter="16">
						<a-col :span="12">
							<a-form-item label="证件类型：" name="idCardType">
								<a-select v-model:value="formData.idCardType" placeholder="请选择证件类型" :options="idcardTypeOptions">
								</a-select>
							</a-form-item>
						</a-col>
						<a-col :span="12">
							<a-form-item label="证件号码：" name="idCardNumber">
								<a-input v-model:value="formData.idCardNumber" placeholder="请输入证件号码" allow-clear />
							</a-form-item>
						</a-col>
					</a-row>
					<a-row :gutter="16">
						<a-col :span="12">
							<a-form-item label="文化程度：" name="cultureLevel">
								<a-select
									v-model:value="formData.cultureLevel"
									placeholder="请选择文化程度"
									:options="cultureLevelOptions"
								>
								</a-select>
							</a-form-item>
						</a-col>
						<a-col :span="12">
							<a-form-item label="政治面貌：" name="politicalOutlook">
								<a-input v-model:value="formData.politicalOutlook" placeholder="请输入政治面貌" allow-clear />
							</a-form-item>
						</a-col>
					</a-row>
					<a-row :gutter="16">
						<a-col :span="12">
							<a-form-item label="毕业学校：" name="college">
								<a-input v-model:value="formData.college" placeholder="请输入毕业学校" allow-clear />
							</a-form-item>
						</a-col>
						<a-col :span="12">
							<a-form-item label="学历：" name="education">
								<a-input v-model:value="formData.education" placeholder="请输入学历" allow-clear />
							</a-form-item>
						</a-col>
					</a-row>
					<a-row :gutter="16">
						<a-col :span="12">
							<a-form-item label="学制：" name="eduLength">
								<a-input v-model:value="formData.eduLength" placeholder="请输入学制" allow-clear />
							</a-form-item>
						</a-col>
						<a-col :span="12">
							<a-form-item label="学位：" name="degree">
								<a-input v-model:value="formData.degree" placeholder="请输入学位" allow-clear />
							</a-form-item>
						</a-col>
					</a-row>
					<a-row :gutter="16">
						<a-col :span="12">
							<a-form-item label="家庭电话：" name="homeTel">
								<a-input v-model:value="formData.homeTel" placeholder="请输入家庭电话" allow-clear />
							</a-form-item>
						</a-col>
						<a-col :span="12">
							<a-form-item label="办公电话：" name="officeTel">
								<a-input v-model:value="formData.officeTel" placeholder="请输入办公电话" allow-clear />
							</a-form-item>
						</a-col>
					</a-row>
					<a-row :gutter="16">
						<a-col :span="12">
							<a-form-item label="紧急联系人：" name="emergencyContact">
								<a-input v-model:value="formData.emergencyContact" placeholder="请输入紧急联系人" allow-clear />
							</a-form-item>
						</a-col>
						<a-col :span="12">
							<a-form-item label="紧急联系电话：" name="emergencyPhone">
								<a-input v-model:value="formData.emergencyPhone" placeholder="请输入紧急联系电话" allow-clear />
							</a-form-item>
						</a-col>
					</a-row>
					<a-row :gutter="16">
						<a-col :span="12">
							<a-form-item label="紧急联系人地址：" name="emergencyAddress">
								<a-textarea
									v-model:value="formData.emergencyAddress"
									placeholder="请输入紧急联系人地址"
									:auto-size="{ minRows: 2, maxRows: 5 }"
									allow-clear
								/>
							</a-form-item>
						</a-col>
					</a-row>
				</a-tab-pane>
			</a-tabs>
		</a-form>
		<template #footer>
			<a-button style="margin-right: 8px" @click="onClose">关闭</a-button>
			<a-button type="primary" :loading="formLoading" @click="onSubmit">保存</a-button>
		</template>
	</xn-form-container>
</template>

<script setup>
	import clientUserApi from '@/api/client/clientUserApi'
	import { required } from '@/utils/formRules'
	import tool from '@/utils/tool'
	// 默认是关闭状态
	const visible = ref(false)
	const formRef = ref()
	const activeTabsKey = ref('1')
	const emit = defineEmits({ successful: null })
	const formLoading = ref(false)
	// 表单数据
	const formData = ref({})
	// 打开抽屉
	const onOpen = (record) => {
		visible.value = true
		if (record) {
			formData.value = record
		} else {
			formData.value = {
				gender: '男'
			}
		}
	}
	// 关闭抽屉
	const onClose = () => {
		visible.value = false
	}
	// 默认要校验的
	const formRules = {
		account: [required('请输入账号')],
		name: [required('请输入姓名')],
		gender: [required('请选择性别')]
	}
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value
			.validate()
			.then(() => {
				clientUserApi
					.submitForm(formData.value, formData.value.id)
					.then(() => {
						onClose()
						emit('successful')
					})
					.finally(() => {
						formLoading.value = false
					})
			})
			.catch(() => {})
	}
	// 性别
	const genderOptions = tool.dictList('GENDER')
	// 民族
	const nationOptions = tool.dictList('NATION')
	// 身份证件
	const idcardTypeOptions = tool.dictList('IDCARD_TYPE')
	// 文化程度
	const cultureLevelOptions = tool.dictList('CULTURE_LEVEL')

	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>

<style scoped lang="less">
	.form-row {
		background-color: var(--item-hover-bg);
		margin-left: 0 !important;
		margin-bottom: 10px;
	}
	.form-row-con {
		padding-bottom: 5px;
		padding-top: 5px;
		padding-left: 15px;
	}
</style>
