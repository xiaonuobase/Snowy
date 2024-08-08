<template>
	<xn-form-container
		:title="formData.id ? '编辑人员' : '增加人员'"
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
								<a-date-picker v-model:value="formData.birthday" value-format="YYYY-MM-DD" class="xn-wd" />
							</a-form-item>
						</a-col>
					</a-row>
					<a-row :gutter="16">
						<a-col :span="8">
							<a-form-item label="选择组织：" name="orgId">
								<a-tree-select
									v-model:value="formData.orgId"
									class="xn-wd"
									:dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
									placeholder="请选择组织"
									allow-clear
									tree-default-expand-all
									:tree-data="treeData"
									:tree-default-expanded-keys="treeDefaultExpandedKeys"
									:field-names="{
										children: 'children',
										label: 'name',
										value: 'id'
									}"
									@change="selePositionData(formData.orgId, 0)"
								></a-tree-select>
							</a-form-item>
						</a-col>
						<a-col :span="8">
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
						<a-col :span="8">
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
					</a-row>
					<a-row :gutter="16">
						<a-col :span="8">
							<a-form-item label="员工编号：" name="empNo">
								<a-input v-model:value="formData.empNo" placeholder="请输入员工编号" allow-clear />
							</a-form-item>
						</a-col>
						<a-col :span="8">
							<a-form-item label="职级：" name="positionLevel">
								<a-input v-model:value="formData.positionLevel" placeholder="请输入职级" allow-clear />
							</a-form-item>
						</a-col>
						<a-col :span="8">
							<a-form-item label="入职日期：" name="entryDate">
								<a-date-picker v-model:value="formData.entryDate" value-format="YYYY-MM-DD" class="xn-wd" />
							</a-form-item>
						</a-col>
					</a-row>

					<a-form-item label="任职信息" name="positionJson">
						<a-row :gutter="10" class="form-row">
							<a-col :span="7" class="form-row-con"> 机构 </a-col>
							<a-col :span="7" class="form-row-con"> 职位 </a-col>
							<a-col :span="7" class="form-row-con"> 主管 </a-col>
							<a-col :span="3" class="form-row-con">
								<a-button type="primary" @click="addDomains()" size="small">
									<PlusOutlined />
									增加
								</a-button>
							</a-col>
						</a-row>
						<div :key="positionInfo" v-for="(positionInfo, index) in formData.positionJson">
							<a-row :gutter="10">
								<a-col :span="7">
									<a-form-item
										:name="['positionJson', index, 'orgId']"
										:rules="{ required: true, message: '请选择机构' }"
									>
										<a-tree-select
											v-model:value="positionInfo.orgId"
											class="xn-wd"
											:dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
											placeholder="请选择机构"
											allow-clear
											tree-default-expand-all
											:tree-data="treeData"
											:tree-default-expanded-keys="treeDefaultExpandedKeys"
											:field-names="{ children: 'children', label: 'name', value: 'id' }"
											@select="childOrgSelect(positionInfo, 0, index)"
										></a-tree-select>
									</a-form-item>
								</a-col>
								<a-col :span="7">
									<a-form-item
										:name="['positionJson', index, 'positionId']"
										:rules="{ required: true, message: '请选择岗位' }"
									>
										<xn-page-select
											ref="xnChildPositionPageSelectRef"
											v-model:value="positionInfo.positionId"
											placeholder="请选择岗位"
											allow-clear
											:page-function="selectApiFunction.childPositionSelector"
											:echo-function="selectApiFunction.echoPosition"
										/>
									</a-form-item>
								</a-col>
								<a-col :span="7">
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
								<a-col :span="3" class="xn-mt4">
									<a-button size="small" type="primary" danger ghost @click="delDomains(index)">移除</a-button>
								</a-col>
							</a-row>
						</div>
					</a-form-item>
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
			<a-button class="xn-mr8" @click="onClose">关闭</a-button>
			<a-button type="primary" :loading="formLoading" @click="onSubmit">保存</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="bizUser">
	import bizUserApi from '@/api/biz/bizUserApi'
	import { required } from '@/utils/formRules'
	import tool from '@/utils/tool'
	import userCenterApi from '@/api/sys/userCenterApi'
	// 默认是关闭状态
	const visible = ref(false)
	const formRef = ref()
	const activeTabsKey = ref('1')
	const emit = defineEmits({ successful: null })
	const formLoading = ref(false)
	const treeData = ref([])
	const treeDefaultExpandedKeys = ref([])
	// 分页select组件dom定义
	const xnPositionPageSelectRef = ref()
	const xnUserPageSelectRef = ref()
	const xnChildPositionPageSelectRef = ref()
	const xnChildUserPageSelectRef = ref()
	// 表单数据
	const formData = ref({})

	// 打开抽屉
	const onOpen = (record, orgId) => {
		visible.value = true
		formData.value = {
			gender: '男',
			positionJson: []
		}
		if (orgId) {
			formData.value.orgId = orgId
			// 通过机构再查询职位、主管
			nextTick(() => {
				selePositionData(orgId)
			})
		}
		if (record) {
			convertFormData(record)
		}
		nextTick(() => {
			// 机构选择器数据
			bizUserApi.userOrgTreeSelector().then((res) => {
				if (res !== null) {
					treeData.value = res
					// 默认展开2级
					treeData.value.forEach((item) => {
						// 因为0的顶级
						if (item.parentId === '0') {
							treeDefaultExpandedKeys.value.push(item.id)
							// 取到下级ID
							if (item.children) {
								item.children.forEach((items) => {
									treeDefaultExpandedKeys.value.push(items.id)
								})
							}
						}
					})
				}
			})
		})
	}
	// 关闭抽屉
	const onClose = () => {
		treeData.value = []
		treeDefaultExpandedKeys.value = []
		visible.value = false
	}
	// 回显数据
	const convertFormData = (record) => {
		const param = {
			id: record.id
		}
		// 查询详情
		bizUserApi.userDetail(param).then((data) => {
			if (data.positionJson) {
				// 替换表单中的格式与后端查到的
				data.positionJson = JSON.parse(data.positionJson)
			}
			formData.value = Object.assign(formData.value, data)
			// 这里再写一次是因为上面需要先加载增行，下面再进行循环赋值
			if (data.positionJson) {
				// 遍历进行补充
				data.positionJson.map((item, index) => {
					childOrgSelect(item, 1, index)
					return item
				})
			}
			selePositionData(formData.value.orgId)
		})
	}
	// 默认要校验的
	const formRules = {
		account: [required('请输入账号')],
		name: [required('请输入姓名')],
		sex: [required('请选择性别')],
		orgId: [required('请选择组织')],
		positionId: [required('请选择岗位')]
	}
	// 机构选择后查询对应的职位
	const selePositionData = (orgId, type) => {
		if (orgId) {
			const xnPositionPageSelectParam = {
				orgId: orgId
			}
			xnPositionPageSelectRef.value.onPage(xnPositionPageSelectParam)
			xnUserPageSelectRef.value.onPage()
			// 此类型代表选择的时候重置后面的职位
			if (type === 0) {
				formData.value.positionId = undefined
				formData.value.directorId = undefined
			}
		} else {
			formData.value.positionId = undefined
			formData.value.directorId = undefined
		}
	}
	// 传递选择组件需要的API
	const selectApiFunction = {
		positionSelector: (param) => {
			return bizUserApi.userPositionSelector(param).then((data) => {
				return Promise.resolve(data)
			})
		},
		userSelector: (param) => {
			return bizUserApi.userSelector(param).then((data) => {
				return Promise.resolve(data)
			})
		},
		childPositionSelector: (param) => {
			return bizUserApi.userPositionSelector(param).then((data) => {
				return Promise.resolve(data)
			})
		},
		childUserSelector: (param) => {
			return bizUserApi.userSelector(param).then((data) => {
				return Promise.resolve(data)
			})
		},
		// 通过id回显数据接口
		echoPosition: (param) => {
			return userCenterApi.userCenterGetPositionListByIdList(param).then((data) => {
				return Promise.resolve(data)
			})
		},
		echoUser: (param) => {
			return userCenterApi.userCenterGetUserListByIdList(param).then((data) => {
				return Promise.resolve(data)
			})
		}
	}
	// 附属职位信息增行
	const addDomains = () => {
		if (formData.value.positionJson === null) {
			formData.value.positionJson = []
		}
		formData.value.positionJson.push({
			orgId: undefined,
			positionId: undefined,
			directorId: undefined
		})
	}
	// 删减行
	const delDomains = (index) => {
		formData.value.positionJson.splice(index, 1)
	}
	// 子表行内选择机构
	const childOrgSelect = async (data, type, index) => {
		// 说明正在切换机构，我们就将他的后面的设置空
		if (type === 0) {
			formData.value.positionJson.filter((item, serial) => {
				if (item.orgId === data.orgId && serial === index) {
					item.positionId = undefined
					item.directorId = undefined
				}
			})
		}
		const param = {
			orgId: data.orgId
		}
		nextTick(() => {
			xnChildPositionPageSelectRef.value[index].onPage(param)
			xnChildUserPageSelectRef.value[index].onPage(param)
		})
	}
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value
			.validate()
			.then(() => {
				// 因为不切断，我下面转换数据格式，影响上面表单会报错
				let formDatas = JSON.parse(JSON.stringify(formData.value))
				if (formDatas.positionJson && formDatas.positionJson.length > 0) {
					formDatas.positionJson = JSON.stringify(formDatas.positionJson)
				} else {
					delete formDatas.positionJson
				}
				formLoading.value = true
				bizUserApi
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
