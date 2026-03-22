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
					</a-row>
					<a-row :gutter="16">
						<a-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
							<a-form-item label="选择组织：" name="orgId">
								<a-spin :spinning="treeLoading">
									<a-tree-select
										v-model:value="formData.orgId"
										class="xn-wd"
										:dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
										placeholder="请选择组织"
										allow-clear
										tree-line
										:tree-data="treeData"
										v-model:treeExpandedKeys="treeDefaultExpandedKeys"
										:field-names="treeFieldNames"
										:load-data="onLoadData"
										@change="selePositionData(formData.orgId, 0)"
									/>
								</a-spin>
							</a-form-item>
						</a-col>
						<a-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
							<a-form-item label="选择职位：" name="positionId">
								<a-spin :spinning="treeLoading">
									<xn-page-select
										ref="xnPositionPageSelectRef"
										v-model:value="formData.positionId"
										placeholder="请选择职位"
										allow-clear
										:page-function="selectApiFunction.positionSelector"
										:echo-function="selectApiFunction.echoPosition"
									/>
								</a-spin>
							</a-form-item>
						</a-col>
						<a-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
							<a-form-item label="选择主管：" name="directorId">
								<a-spin :spinning="treeLoading">
									<xn-page-select
										ref="xnUserPageSelectRef"
										v-model:value="formData.directorId"
										placeholder="请选择主管"
										allow-clear
										:page-function="selectApiFunction.userSelector"
										:echo-function="selectApiFunction.echoUser"
									/>
								</a-spin>
							</a-form-item>
						</a-col>
					</a-row>
					<a-row :gutter="16">
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
					</a-row>

					<a-form-item label="任职信息" name="positionJson">
						<a-row :gutter="10" class="form-row mb-5">
							<a-col :span="7"> 机构 </a-col>
							<a-col :span="7"> 职位 </a-col>
							<a-col :span="7"> 主管 </a-col>
							<a-col :span="3">
								<a-button type="primary" @click="addDomains()" size="small">
									<PlusOutlined />
									增加
								</a-button>
							</a-col>
						</a-row>
						<div :key="positionInfo" v-for="(positionInfo, index) in formData.positionJson">
							<a-row :gutter="10">
								<a-col :xs="24" :sm="24" :md="7" :lg="7" :xl="7">
									<a-form-item
										:name="['positionJson', index, 'orgId']"
										:rules="{ required: true, message: '请选择组织' }"
									>
										<a-spin :spinning="treeLoading">
											<a-tree-select
												v-model:value="positionInfo.orgId"
												class="xn-wd"
												:dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
												placeholder="请选择组织"
												allow-clear
												tree-line
												:tree-data="treeData"
												v-model:treeExpandedKeys="childTreeExpandedKeys[index]"
												:field-names="treeFieldNames"
												:load-data="onLoadData"
												@change="childOrgSelect(positionInfo, 0, index)"
											/>
										</a-spin>
									</a-form-item>
								</a-col>
								<a-col :xs="24" :sm="24" :md="7" :lg="7" :xl="7">
									<a-form-item
										:name="['positionJson', index, 'positionId']"
										:rules="{ required: true, message: '请选择职位' }"
									>
										<a-spin :spinning="treeLoading">
											<xn-page-select
												ref="xnChildPositionPageSelectRef"
												v-model:value="positionInfo.positionId"
												placeholder="请选择职位"
												allow-clear
												:page-function="selectApiFunction.childPositionSelector"
												:echo-function="selectApiFunction.echoPosition"
											/>
										</a-spin>
									</a-form-item>
								</a-col>
								<a-col :xs="24" :sm="24" :md="7" :lg="7" :xl="7">
									<a-form-item :name="['positionJson', index, 'directorId']">
										<a-spin :spinning="treeLoading">
											<xn-page-select
												ref="xnChildUserPageSelectRef"
												v-model:value="positionInfo.directorId"
												placeholder="请选择主管"
												allow-clear
												:page-function="selectApiFunction.childUserSelector"
												:echo-function="selectApiFunction.echoUser"
											/>
										</a-spin>
									</a-form-item>
								</a-col>
								<a-col :xs="24" :sm="24" :md="3" :lg="3" :xl="3">
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
				</a-tab-pane>
				<a-tab-pane key="2" tab="更多信息" force-render>
					<a-row :gutter="16">
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
						<!--						<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
							<a-form-item label="通信地址：" name="mailingAddress">
								<a-textarea
									v-model:value="formData.mailingAddress"
									placeholder="请输入通信地址"
									:auto-size="{ minRows: 2, maxRows: 5 }"
									allow-clear
								/>
							</a-form-item>
						</a-col>-->
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
	// 默认是关闭状态
	const visible = ref(false)
	const formRef = ref()
	const activeTabsKey = ref('1')
	const emit = defineEmits({ successful: null })
	const formLoading = ref(false)
	const treeLoading = ref(false)
	const treeData = ref([])
	const treeDefaultExpandedKeys = ref([])
	const childTreeExpandedKeys = ref([])
	// 分页select组件dom定义
	const xnPositionPageSelectRef = ref()
	const xnUserPageSelectRef = ref()
	const xnChildPositionPageSelectRef = ref()
	const xnChildUserPageSelectRef = ref()
	// 表单数据
	const formData = ref({})
	const treeFieldNames = { children: 'children', label: 'name', key: 'id', value: 'id' }

	// 将祖先扁平节点合并到懒加载根节点中
	const buildTreeWithAncestors = (rootNodes, ancestorNodes) => {
		const allNodes = [...rootNodes]
		const existingIds = new Set(allNodes.map((n) => n.id))
		ancestorNodes.forEach((node) => {
			if (!existingIds.has(node.id)) {
				allNodes.push(node)
				existingIds.add(node.id)
			}
		})
		const parentChildMap = new Map()
		allNodes.forEach((node) => {
			const pid = node.parentId
			if (!parentChildMap.has(pid)) {
				parentChildMap.set(pid, [])
			}
			const siblings = parentChildMap.get(pid)
			if (!siblings.find((n) => n.id === node.id)) {
				siblings.push(node)
			}
		})
		const ancestorIdSet = new Set(ancestorNodes.map((n) => n.id))
		const buildBranch = (parentId) => {
			const children = parentChildMap.get(parentId)
			if (!children) return undefined
			return children.map((child) => {
				const node = { ...child, isLeaf: child.isLeaf === undefined ? false : child.isLeaf }
				if (ancestorIdSet.has(child.id) && parentChildMap.has(child.id)) {
					node.children = buildBranch(child.id)
				}
				return node
			})
		}
		return buildBranch('0') || []
	}
	const collectAncestorKeysFromFlat = (ancestorNodes, selectedIds) => {
		const selectedSet = new Set(selectedIds)
		return ancestorNodes.filter((n) => !selectedSet.has(n.id) || !n.isLeaf).map((n) => n.id)
	}
	// 加载懒加载树（无需展开到指定节点时使用）
	const loadLazyTree = () => {
		return userApi.userOrgTreeSelector().then((res) => {
			if (res !== null) {
				treeData.value = res.map((item) => {
					return {
						...item,
						isLeaf: item.isLeaf === undefined ? false : item.isLeaf
					}
				})
				// 只有一个根节点时才自动展开
				if (treeData.value.length === 1) {
					treeDefaultExpandedKeys.value.push(treeData.value[0].id)
				}
			}
		})
	}
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
		nextTick(() => {
			if (record) {
				// 编辑模式：懒加载根节点 + 详情 + 祖先路径
				treeLoading.value = true
				const detailPromise = convertFormData(record)
				detailPromise.then(() => {
					// 收集所有需要回显的orgId
					const allOrgIds = []
					if (formData.value.orgId) {
						allOrgIds.push(formData.value.orgId)
					}
					if (formData.value.positionJson) {
						formData.value.positionJson.forEach((item) => {
							if (item.orgId && !allOrgIds.includes(item.orgId)) {
								allOrgIds.push(item.orgId)
							}
						})
					}
					if (allOrgIds.length > 0) {
						const rootPromise = userApi.userOrgTreeSelector()
						const ancestorPromise = userApi.userGetAncestorNodes(allOrgIds)
						Promise.all([rootPromise, ancestorPromise])
							.then(([rootNodes, ancestorNodes]) => {
								const roots = (rootNodes || []).map((item) => ({
									...item,
									isLeaf: item.isLeaf === undefined ? false : item.isLeaf
								}))
								treeData.value = buildTreeWithAncestors(roots, ancestorNodes || [])
								const expandKeys = collectAncestorKeysFromFlat(ancestorNodes || [], allOrgIds)
								treeDefaultExpandedKeys.value = expandKeys
								// 任职信息：每行独立展开
								if (formData.value.positionJson) {
									formData.value.positionJson.forEach((item, index) => {
										if (item.orgId) {
											childTreeExpandedKeys.value[index] = [...expandKeys]
										}
									})
								}
							})
							.finally(() => {
								treeLoading.value = false
							})
					} else {
						loadLazyTree().finally(() => {
							treeLoading.value = false
						})
					}
				})
			} else if (orgId) {
				// 新增模式且有orgId：懒加载根节点 + 祖先路径
				treeLoading.value = true
				const rootPromise = userApi.userOrgTreeSelector()
				const ancestorPromise = userApi.userGetAncestorNodes([orgId])
				Promise.all([rootPromise, ancestorPromise])
					.then(([rootNodes, ancestorNodes]) => {
						const roots = (rootNodes || []).map((item) => ({
							...item,
							isLeaf: item.isLeaf === undefined ? false : item.isLeaf
						}))
						treeData.value = buildTreeWithAncestors(roots, ancestorNodes || [])
						treeDefaultExpandedKeys.value = collectAncestorKeysFromFlat(ancestorNodes || [], [orgId])
					})
					.finally(() => {
						treeLoading.value = false
					})
			} else {
				// 新增模式无orgId：懒加载树
				loadLazyTree()
			}
		})
	}
	// 懒加载子节点
	const onLoadData = (treeNode) => {
		return new Promise((resolve) => {
			if (treeNode.dataRef.children) {
				resolve()
				return
			}
			userApi
				.userOrgTreeSelector({
					parentId: treeNode.dataRef.id
				})
				.then((res) => {
					treeNode.dataRef.children = res.map((item) => {
						return {
							...item,
							isLeaf: item.isLeaf === undefined ? false : item.isLeaf
						}
					})
					treeData.value = [...treeData.value]
					resolve()
				})
		})
	}
	// 关闭抽屉
	const onClose = () => {
		treeData.value = []
		treeDefaultExpandedKeys.value = []
		childTreeExpandedKeys.value = []
		visible.value = false
	}
	// 回显数据
	const convertFormData = (record) => {
		const param = {
			id: record.id
		}
		// 查询详情
		return userApi.userDetail(param).then((data) => {
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
		positionId: [required('请选择职位')]
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
			return userApi.userPositionSelector(param).then((data) => {
				return Promise.resolve(data)
			})
		},
		userSelector: (param) => {
			return userApi.userSelector(param).then((data) => {
				return Promise.resolve(data)
			})
		},
		childPositionSelector: (param) => {
			return userApi.userPositionSelector(param).then((data) => {
				return Promise.resolve(data)
			})
		},
		childUserSelector: (param) => {
			return userApi.userSelector(param).then((data) => {
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
		childTreeExpandedKeys.value.push([])
	}
	// 删减行
	const delDomains = (index) => {
		formData.value.positionJson.splice(index, 1)
		childTreeExpandedKeys.value.splice(index, 1)
	}

	// 子表行内选择机构
	const childOrgSelect = (data, type, index) => {
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
		margin-bottom: 10px;
		padding: 4px;
	}
</style>
