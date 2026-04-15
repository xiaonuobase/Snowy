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
					<a-row :gutter="16" class="!mx-0">
						<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
							<a-form-item label="账号：" name="account">
								<a-input v-model:value="formData.account" placeholder="请输入账号" allow-clear />
							</a-form-item>
						</a-col>
						<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
							<a-form-item label="姓名：" name="name">
								<a-input v-model:value="formData.name" placeholder="请输入姓名" allow-clear />
							</a-form-item>
						</a-col>
						<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
							<a-form-item label="性别：" name="gender">
								<a-radio-group v-model:value="formData.gender" :options="genderOptions" />
							</a-form-item>
						</a-col>
						<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
							<a-form-item label="昵称：" name="nickname">
								<a-input v-model:value="formData.nickname" placeholder="请输入昵称" allow-clear />
							</a-form-item>
						</a-col>
						<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
							<a-form-item label="手机号：" name="phone">
								<a-input v-model:value="formData.phone" placeholder="请输入手机" allow-clear />
							</a-form-item>
						</a-col>
						<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
							<a-form-item label="邮箱：" name="email">
								<a-input v-model:value="formData.email" placeholder="请输入邮箱" allow-clear />
							</a-form-item>
						</a-col>
						<a-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
							<a-form-item label="选择组织：" name="orgId">
								<xn-tree-select
									ref="orgTreeSelectRef"
									v-model:value="formData.orgId"
									:tree-api="userApi.userOrgTreeSelector"
									:ancestor-api="userApi.userGetAncestorNodes"
									placeholder="请选择组织"
									@change="selePositionData(formData.orgId, 0)"
								/>
							</a-form-item>
						</a-col>
						<a-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
							<a-form-item label="选择职位：" name="positionId">
								<xn-page-select
									ref="xnPositionPageSelectRef"
									v-model:value="formData.positionId"
									placeholder="请选择职位"
									allow-clear
									:page-function="selectApiFunction.positionSelector"
									:echo-function="selectApiFunction.echoPosition"
								/>
							</a-form-item>
						</a-col>
						<a-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
							<a-form-item label="选择主管：" name="directorId">
								<xn-page-select
									ref="xnUserPageSelectRef"
									v-model:value="formData.directorId"
									placeholder="请选择主管"
									allow-clear
									:page-function="selectApiFunction.userSelector"
									:echo-function="selectApiFunction.echoUser"
								/>
							</a-form-item>
						</a-col>
						<a-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
							<a-form-item label="员工编号：" name="empNo">
								<a-input v-model:value="formData.empNo" placeholder="请输入员工编号" allow-clear />
							</a-form-item>
						</a-col>
						<a-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
							<a-form-item label="职级：" name="positionLevel">
								<a-input v-model:value="formData.positionLevel" placeholder="请输入职级" allow-clear />
							</a-form-item>
						</a-col>
						<a-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
							<a-form-item label="入职日期：" name="entryDate">
								<a-date-picker v-model:value="formData.entryDate" value-format="YYYY-MM-DD" class="xn-wd" />
							</a-form-item>
						</a-col>
						<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
							<a-form-item label="任职信息" name="positionJson">
								<a-row :gutter="10" class="bg-[var(--item-hover-bg)] p-1 mb-2.5 !mx-0">
									<a-col :span="7" class="text-center truncate"> 机构 </a-col>
									<a-col :span="7" class="text-center truncate"> 职位 </a-col>
									<a-col :span="7" class="text-center truncate"> 主管 </a-col>
									<a-col :span="3" class="text-center">
										<a-button type="primary" @click="addDomains()" size="small">
											<PlusOutlined />
											<span class="!hidden md:!inline">增加</span>
										</a-button>
									</a-col>
								</a-row>
								<div :key="positionInfo" v-for="(positionInfo, index) in formData.positionJson">
									<a-row :gutter="10" class="!mx-0">
										<a-col :xs="24" :sm="24" :md="7" :lg="7" :xl="7" class="text-center truncate">
											<a-form-item
												:name="['positionJson', index, 'orgId']"
												:rules="{ required: true, message: '请选择组织' }"
											>
												<xn-tree-select
													:ref="(el) => setChildTreeSelectRef(el, index)"
													v-model:value="positionInfo.orgId"
													:tree-api="userApi.userOrgTreeSelector"
													:ancestor-api="userApi.userGetAncestorNodes"
													placeholder="请选择组织"
													@change="childOrgSelect(positionInfo, 0, index)"
												/>
											</a-form-item>
										</a-col>
										<a-col :xs="24" :sm="24" :md="7" :lg="7" :xl="7" class="text-center truncate">
											<a-form-item
												:name="['positionJson', index, 'positionId']"
												:rules="{ required: true, message: '请选择职位' }"
											>
												<xn-page-select
													ref="xnChildPositionPageSelectRef"
													v-model:value="positionInfo.positionId"
													placeholder="请选择职位"
													allow-clear
													:page-function="selectApiFunction.childPositionSelector"
													:echo-function="selectApiFunction.echoPosition"
												/>
											</a-form-item>
										</a-col>
										<a-col :xs="24" :sm="24" :md="7" :lg="7" :xl="7" class="text-center truncate">
											<a-form-item :name="['positionJson', index, 'directorId']">
												<xn-page-select
													ref="xnChildUserPageSelectRef"
													v-model:value="positionInfo.directorId"
													placeholder="请选择主管"
													allow-clear
													:page-function="selectApiFunction.childUserSelector"
													:echo-function="selectApiFunction.echoUser"
												/>
											</a-form-item>
										</a-col>
										<a-col :xs="24" :sm="24" :md="3" :lg="3" :xl="3" class="text-center">
											<a-form-item>
												<a-button type="primary" danger ghost @click="delDomains(index)" size="small">
													<DeleteOutlined />
													移除
												</a-button>
											</a-form-item>
										</a-col>
									</a-row>
								</div>
							</a-form-item>
						</a-col>
					</a-row>
				</a-tab-pane>
				<a-tab-pane key="2" tab="更多信息" force-render>
					<a-row :gutter="16" class="!mx-0">
						<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
							<a-form-item label="出生日期：" name="birthday">
								<a-date-picker v-model:value="formData.birthday" value-format="YYYY-MM-DD" class="xn-wd" />
							</a-form-item>
						</a-col>
						<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
							<a-form-item label="民族：" name="nation">
								<a-select v-model:value="formData.nation" placeholder="请选择民族" :options="nationOptions" />
							</a-form-item>
						</a-col>
						<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
							<a-form-item label="籍贯：" name="nativePlace">
								<a-input v-model:value="formData.nativePlace" placeholder="请输入籍贯" allow-clear />
							</a-form-item>
						</a-col>
						<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
							<a-form-item label="家庭电话：" name="homeTel">
								<a-input v-model:value="formData.homeTel" placeholder="请输入家庭电话" allow-clear />
							</a-form-item>
						</a-col>
						<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
							<a-form-item label="家庭住址：" name="homeAddress">
								<a-textarea
									v-model:value="formData.homeAddress"
									placeholder="请输入家庭住址"
									:auto-size="{ minRows: 2, maxRows: 5 }"
									allow-clear
								/>
							</a-form-item>
						</a-col>
						<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
							<a-form-item label="证件类型：" name="idCardType">
								<a-select
									v-model:value="formData.idCardType"
									placeholder="请选择证件类型"
									:options="idcardTypeOptions"
								/>
							</a-form-item>
						</a-col>
						<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
							<a-form-item label="证件号码：" name="idCardNumber">
								<a-input v-model:value="formData.idCardNumber" placeholder="请输入证件号码" allow-clear />
							</a-form-item>
						</a-col>
						<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
							<a-form-item label="文化程度：" name="cultureLevel">
								<a-select
									v-model:value="formData.cultureLevel"
									placeholder="请选择文化程度"
									:options="cultureLevelOptions"
								/>
							</a-form-item>
						</a-col>
						<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
							<a-form-item label="政治面貌：" name="politicalOutlook">
								<a-input v-model:value="formData.politicalOutlook" placeholder="请输入政治面貌" allow-clear />
							</a-form-item>
						</a-col>
						<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
							<a-form-item label="毕业学校：" name="college">
								<a-input v-model:value="formData.college" placeholder="请输入毕业学校" allow-clear />
							</a-form-item>
						</a-col>
						<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
							<a-form-item label="学历：" name="education">
								<a-input v-model:value="formData.education" placeholder="请输入学历" allow-clear />
							</a-form-item>
						</a-col>
						<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
							<a-form-item label="学制：" name="eduLength">
								<a-input v-model:value="formData.eduLength" placeholder="请输入学制" allow-clear />
							</a-form-item>
						</a-col>
						<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
							<a-form-item label="学位：" name="degree">
								<a-input v-model:value="formData.degree" placeholder="请输入学位" allow-clear />
							</a-form-item>
						</a-col>
						<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
							<a-form-item label="紧急联系人：" name="emergencyContact">
								<a-input v-model:value="formData.emergencyContact" placeholder="请输入紧急联系人" allow-clear />
							</a-form-item>
						</a-col>
						<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
							<a-form-item label="紧急联系电话：" name="emergencyPhone">
								<a-input v-model:value="formData.emergencyPhone" placeholder="请输入紧急联系电话" allow-clear />
							</a-form-item>
						</a-col>
						<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
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
			<a-button class="xn-mr8" @click="onClose">关闭</a-button>
			<a-button type="primary" :loading="formLoading" @click="onSubmit">保存</a-button>
		</template>
	</xn-form-container>
</template>

<script setup>
	import userApi from '@/api/sys/userApi'
	import userCenterApi from '@/api/sys/userCenterApi'
	import { required } from '@/utils/formRules'
	import tool from '@/utils/tool'

	const visible = ref(false)
	const formRef = ref()
	const activeTabsKey = ref('1')
	const emit = defineEmits({ successful: null })
	const formLoading = ref(false)
	const formData = ref({})

	// 组件 ref
	const orgTreeSelectRef = ref()
	const childTreeSelectRefs = ref({})
	const xnPositionPageSelectRef = ref()
	const xnUserPageSelectRef = ref()
	const xnChildPositionPageSelectRef = ref()
	const xnChildUserPageSelectRef = ref()

	// 动态 ref 收集器
	const setChildTreeSelectRef = (el, index) => {
		if (el) {
			childTreeSelectRefs.value[index] = el
		}
	}

	// 打开抽屉
	const onOpen = (record, orgId) => {
		visible.value = true
		formData.value = { gender: '男', positionJson: [] }
		if (orgId) {
			formData.value.orgId = orgId
			nextTick(() => selePositionData(orgId))
		}
		nextTick(() => {
			if (record) {
				// 编辑模式
				convertFormData(record).then(() => {
					const allOrgIds = collectAllOrgIds()
					// 主表树回显
					orgTreeSelectRef.value.echo(allOrgIds)
					// 任职信息的子行树回显（共享同一份 treeData，无需重复请求）
				})
			} else if (orgId) {
				// 新增 + 预选组织
				orgTreeSelectRef.value.echo([orgId])
			} else {
				// 新增
				orgTreeSelectRef.value.init()
			}
		})
	}

	// 收集所有需要回显的 orgId
	const collectAllOrgIds = () => {
		const ids = []
		if (formData.value.orgId) ids.push(formData.value.orgId)
		if (formData.value.positionJson) {
			formData.value.positionJson.forEach((item) => {
				if (item.orgId && !ids.includes(item.orgId)) ids.push(item.orgId)
			})
		}
		return ids
	}

	// 回显数据
	const convertFormData = (record) => {
		return userApi.userDetail({ id: record.id }).then((data) => {
			if (data.positionJson) {
				data.positionJson = JSON.parse(data.positionJson)
			}
			formData.value = Object.assign(formData.value, data)
			if (data.positionJson) {
				data.positionJson.forEach((item, index) => {
					childOrgSelect(item, 1, index)
				})
			}
			selePositionData(formData.value.orgId)
		})
	}

	// 关闭抽屉
	const onClose = () => {
		childTreeSelectRefs.value = {}
		visible.value = false
	}

	// 默认校验规则
	const formRules = {
		account: [required('请输入账号')],
		name: [required('请输入姓名')],
		sex: [required('请选择性别')],
		orgId: [required('请选择组织')],
		positionId: [required('请选择职位')]
	}

	// 机构选择后查询对应的职位
	const selePositionData = (orgId, type) => {
		if (orgId) {
			xnPositionPageSelectRef.value.onPage({ orgId })
			xnUserPageSelectRef.value.onPage()
			if (type === 0) {
				formData.value.positionId = undefined
				formData.value.directorId = undefined
			}
		} else {
			formData.value.positionId = undefined
			formData.value.directorId = undefined
		}
	}

	// API 函数
	const selectApiFunction = {
		positionSelector: (param) => userApi.userPositionSelector(param),
		userSelector: (param) => userApi.userSelector(param),
		childPositionSelector: (param) => userApi.userPositionSelector(param),
		childUserSelector: (param) => userApi.userSelector(param),
		echoPosition: (param) => userCenterApi.userCenterGetPositionListByIdList(param),
		echoUser: (param) => userCenterApi.userCenterGetUserListByIdList(param)
	}

	// 任职信息增行
	const addDomains = () => {
		if (formData.value.positionJson === null) {
			formData.value.positionJson = []
		}
		formData.value.positionJson.push({ orgId: undefined, positionId: undefined, directorId: undefined })
	}

	// 删减行
	const delDomains = (index) => {
		formData.value.positionJson.splice(index, 1)
		delete childTreeSelectRefs.value[index]
	}

	// 子表行内选择机构
	const childOrgSelect = (data, type, index) => {
		if (type === 0) {
			formData.value.positionJson.filter((item, serial) => {
				if (item.orgId === data.orgId && serial === index) {
					item.positionId = undefined
					item.directorId = undefined
				}
			})
		}
		nextTick(() => {
			xnChildPositionPageSelectRef.value[index].onPage({ orgId: data.orgId })
			xnChildUserPageSelectRef.value[index].onPage({ orgId: data.orgId })
		})
	}

	// 提交
	const onSubmit = () => {
		formRef.value
			.validate()
			.then(() => {
				let formDatas = JSON.parse(JSON.stringify(formData.value))
				if (formDatas.positionJson && formDatas.positionJson.length > 0) {
					formDatas.positionJson = JSON.stringify(formDatas.positionJson)
				} else {
					delete formDatas.positionJson
				}
				formLoading.value = true
				userApi
					.submitForm(formDatas, formDatas.id)
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

	const genderOptions = tool.dictList('GENDER')
	const nationOptions = tool.dictList('NATION')
	const idcardTypeOptions = tool.dictList('IDCARD_TYPE')
	const cultureLevelOptions = tool.dictList('CULTURE_LEVEL')

	defineExpose({ onOpen })
</script>
