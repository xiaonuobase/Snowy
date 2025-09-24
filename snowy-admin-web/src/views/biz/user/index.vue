<template>
	<a-row :gutter="10">
		<a-col :xs="0" :sm="0" :md="0" :lg="4" :xl="4">
			<a-card :bordered="false" :loading="cardLoading" class="left-tree-container">
				<a-tree
					v-if="treeData.length > 0"
					v-model:expandedKeys="defaultExpandedKeys"
					:tree-data="treeData"
					:field-names="treeFieldNames"
					@select="treeSelect"
				/>
				<a-empty v-else :image="Empty.PRESENTED_IMAGE_SIMPLE" />
			</a-card>
		</a-col>
		<a-col :xs="24" :sm="24" :md="24" :lg="20" :xl="20">
			<a-card :bordered="false" class="xn-mb10">
				<a-form ref="searchFormRef" :model="searchFormState">
					<a-row :gutter="10">
						<a-col :xs="24" :sm="8" :md="8" :lg="0" :xl="0">
							<a-form-item label="所属机构" name="orgId">
								<a-tree-select
									v-model:value="searchFormState.orgId"
									class="xn-wd"
									:dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
									placeholder="请选择所属机构"
									allow-clear
									:tree-data="treeData"
									:field-names="{
											children: 'children',
											label: 'name',
											value: 'id'
										}"
									selectable="false"
									tree-line
								/>
							</a-form-item>
						</a-col>
						<a-col :xs="24" :sm="8" :md="8" :lg="8" :xl="8">
							<a-form-item name="searchKey" label="关键词">
								<a-input
									v-model:value="searchFormState.searchKey"
									placeholder="请输入姓名或关键词"
								/>
							</a-form-item>
						</a-col>
						<a-col :xs="24" :sm="8" :md="8" :lg="8" :xl="8">
							<a-form-item name="userStatus" label="人员状态">
								<a-select
									v-model:value="searchFormState.userStatus"
									placeholder="请选择人员状态"
									:getPopupContainer="(trigger) => trigger.parentNode"
								>
									<a-select-option v-for="item in statusData" :key="item.value" :value="item.value">{{
										item.label
									}}</a-select-option>
								</a-select>
							</a-form-item>
						</a-col>
						<a-col :xs="24" :sm="8" :md="8" :lg="8" :xl="8">
							<a-form-item>
								<a-space>
									<a-button type="primary" @click="tableRef.refresh(true)">
										<template #icon><SearchOutlined /></template>
										查询
									</a-button>
									<a-button @click="reset">
										<template #icon><redo-outlined /></template>
										重置
									</a-button>
								</a-space>
							</a-form-item>
						</a-col>
					</a-row>
				</a-form>
			</a-card>
			<a-card :bordered="false">
				<s-table
					ref="tableRef"
					:columns="columns"
					:data="loadData"
					:expand-row-by-click="true"
					bordered
					:alert="options.alert.show"
					:tool-config="toolConfig"
					:row-key="(record) => record.id"
					:row-selection="options.rowSelection"
					:scroll="{ x: 'max-content' }"
				>
					<template #operator class="table-operator">
						<a-space>
							<a-button
								type="primary"
								@click="formRef.onOpen(undefined, searchFormState.orgId)"
								v-if="hasPerm('bizUserAdd')"
							>
								<template #icon><plus-outlined /></template>
								<span>增加</span>
							</a-button>
							<a-button @click="exportBatchUserVerify" v-if="hasPerm('bizUserBatchExport')">
								<template #icon><export-outlined /></template>
								批量导出
							</a-button>
							<xn-batch-button
								v-if="hasPerm('bizUserBatchDelete')"
								buttonName="批量删除"
								icon="DeleteOutlined"
								buttonDanger
								:selectedRowKeys="selectedRowKeys"
								@batchCallBack="deleteBatchUser"
							/>
						</a-space>
					</template>
					<template #bodyCell="{ column, record }">
						<template v-if="column.dataIndex === 'avatar'">
							<a-avatar :src="record.avatar" style="margin-bottom: -5px; margin-top: -5px" />
						</template>
						<template v-if="column.dataIndex === 'gender'">
							{{ $TOOL.dictTypeData('GENDER', record.gender) }}
						</template>
						<template v-if="column.dataIndex === 'userStatus'">
							<a-switch
								:loading="loading"
								:checked="record.userStatus === 'ENABLE'"
								@change="editStatus(record)"
								v-if="hasPerm('bizUserUpdataStatus')"
							/>
							<span v-else>{{ $TOOL.dictTypeData('COMMON_STATUS', record.userStatus) }}</span>
						</template>
						<template v-if="column.dataIndex === 'action'">
							<a @click="formRef.onOpen(record)" v-if="hasPerm('bizUserEdit')">编辑</a>
							<a-divider type="vertical" v-if="hasPerm(['bizUserEdit', 'bizUserDelete'], 'and')" />
							<a-popconfirm title="确定要删除吗？" @confirm="removeUser(record)">
								<a-button type="link" danger size="small" v-if="hasPerm('bizUserDelete')">
									删除
								</a-button>
							</a-popconfirm>
							<a-divider
								type="vertical"
								v-if="hasPerm(['bizUserGrantRole', 'bizUserPwdReset', 'bizUserExportUserInfo'])"
							/>
							<a-dropdown v-if="hasPerm(['bizUserGrantRole', 'bizUserPwdReset', 'bizUserExportUserInfo'])">
								<a class="ant-dropdown-link">
									更多
									<DownOutlined />
								</a>
								<template #overlay>
									<a-menu>
										<a-menu-item v-if="hasPerm('bizUserPwdReset')">
											<a-popconfirm
												title="确定要重置吗？"
												placement="topRight"
												@confirm="resetPassword(record)"
											>
												<a>重置密码</a>
											</a-popconfirm>
										</a-menu-item>
										<a-menu-item v-if="hasPerm('bizUserGrantRole')">
											<a @click="selectRole(record)">授权角色</a>
										</a-menu-item>
										<a-menu-item v-if="hasPerm('bizUserExportUserInfo')">
											<a @click="exportUserInfo(record)">导出信息</a>
										</a-menu-item>
									</a-menu>
								</template>
							</a-dropdown>
						</template>
					</template>
				</s-table>
			</a-card>
		</a-col>
	</a-row>
	<Form ref="formRef" @successful="tableRef.refresh()" />
	<xn-role-selector
		ref="RoleSelectorPlusRef"
		:org-tree-api="selectorApiFunction.orgTreeApi"
		:role-page-api="selectorApiFunction.rolePageApi"
		:add-show="false"
		:role-global="true"
		@onBack="roleBack"
	/>
</template>
<script setup name="bizUser">
	import { message, Empty } from 'ant-design-vue'
	import { isEmpty } from 'lodash-es'
	import tool from '@/utils/tool'
	import downloadUtil from '@/utils/downloadUtil'
	import bizUserApi from '@/api/biz/bizUserApi'
	import bizOrgApi from '@/api/biz/bizOrgApi'
	import Form from './form.vue'

	const columns = [
		{
			title: '头像',
			dataIndex: 'avatar',
			align: 'center'
		},
		{
			title: '账号',
			dataIndex: 'account',
			ellipsis: true
		},
		{
			title: '姓名',
			dataIndex: 'name'
		},
		{
			title: '性别',
			dataIndex: 'gender'
		},
		{
			title: '手机',
			dataIndex: 'phone',
			ellipsis: true
		},
		{
			title: '机构',
			dataIndex: 'orgName',
			ellipsis: true
		},
		{
			title: '职位',
			dataIndex: 'positionName',
			ellipsis: true
		},
		{
			title: '状态',
			dataIndex: 'userStatus'
		}
	]
	if (hasPerm(['bizUserEdit', 'bizUserGrantRole', 'bizUserPwdReset', 'bizUserExportUserInfo', 'bizUserDelete'])) {
		columns.push({
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			fixed: 'right'
		})
	}
	const toolConfig = { refresh: true, height: true, columnSetting: true }
	const statusData = tool.dictList('COMMON_STATUS')
	const searchFormRef = ref()
	const defaultExpandedKeys = ref([])
	const searchFormState = ref({})
	const tableRef = ref(null)
	const treeData = ref([])
	const selectedRowKeys = ref([])
	const treeFieldNames = { children: 'children', title: 'name', key: 'id' }
	const formRef = ref(null)
	const RoleSelectorPlusRef = ref()
	const selectedRecord = ref({})
	const loading = ref(false)
	const cardLoading = ref(true)
	// 表格查询 返回 Promise 对象
	const loadData = (parameter) => {
		return bizUserApi.userPage(Object.assign(parameter, searchFormState.value)).then((res) => {
			return res
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
	// 左侧树查询
	bizOrgApi
		.orgTree()
		.then((res) => {
			cardLoading.value = false
			if (res !== null) {
				treeData.value = res
				if (isEmpty(defaultExpandedKeys.value)) {
					// 默认展开2级
					treeData.value.forEach((item) => {
						// 因为0的顶级
						if (item.parentId === '0') {
							defaultExpandedKeys.value.push(item.id)
							// 取到下级ID
							if (item.children) {
								item.children.forEach((items) => {
									defaultExpandedKeys.value.push(items.id)
								})
							}
						}
					})
				}
			}
		})
		.finally(() => {
			cardLoading.value = false
		})
	// 列表选择配置
	const options = {
		alert: {
			show: false,
			clear: () => {
				selectedRowKeys.value = ref([])
			}
		},
		rowSelection: {
			onChange: (selectedRowKey, selectedRows) => {
				selectedRowKeys.value = selectedRowKey
			}
		}
	}
	// 点击树查询
	const treeSelect = (selectedKeys) => {
		if (selectedKeys.length > 0) {
			searchFormState.value.orgId = selectedKeys.toString()
		} else {
			delete searchFormState.value.orgId
		}
		tableRef.value.refresh(true)
	}
	// 修改状态
	const editStatus = (record) => {
		loading.value = true
		if (record.userStatus === 'ENABLE') {
			bizUserApi
				.userDisableUser(record)
				.then(() => {
					tableRef.value.refresh()
				})
				.finally(() => {
					loading.value = false
				})
		} else {
			bizUserApi
				.userEnableUser(record)
				.then(() => {
					tableRef.value.refresh()
				})
				.finally(() => {
					loading.value = false
				})
		}
	}
	// 删除人员
	const removeUser = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		bizUserApi.userDelete(params).then(() => {
			tableRef.value.refresh()
		})
	}
	// 批量导出校验并加参数
	const exportBatchUserVerify = () => {
		if ((selectedRowKeys.value.length < 1) & !searchFormState.value.searchKey & !searchFormState.value.userStatus) {
			message.warning('请输入查询条件或勾选要导出的信息')
		}
		if (selectedRowKeys.value.length > 0) {
			const params = {
				userIds: selectedRowKeys.value
					.map((m) => {
						return m
					})
					.join()
			}
			exportBatchUser(params)
			return
		}
		if (searchFormState.value.searchKey || searchFormState.value.userStatus) {
			const params = {
				searchKey: searchFormState.value.searchKey,
				userStatus: searchFormState.value.userStatus
			}
			exportBatchUser(params)
		}
	}
	// 批量导出
	const exportBatchUser = (params) => {
		bizUserApi.userExport(params).then((res) => {
			downloadUtil.resultDownload(res)
			tableRef.value.clearSelected()
		})
	}
	// 批量删除
	const deleteBatchUser = (params) => {
		bizUserApi.userDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
	// 打开角色选择器
	const selectRole = (record) => {
		selectedRecord.value = record
		// 查询到已有角色，并转为ids的格式，给角色选择器
		const param = {
			id: record.id
		}
		bizUserApi.userOwnRole(param).then((data) => {
			RoleSelectorPlusRef.value.showModel(data)
		})
	}
	// 角色选择回调
	const roleBack = (value) => {
		let params = {
			id: selectedRecord.value.id,
			roleIdList: []
		}
		if (value.length > 0) {
			value.forEach((item) => {
				params.roleIdList.push(item)
			})
		}
		bizUserApi.grantRole(params).then(() => {})
	}
	// 重置人员密码
	const resetPassword = (record) => {
		bizUserApi.userResetPassword(record).then(() => {})
	}
	// 导出用户信息
	const exportUserInfo = (record) => {
		const params = {
			id: record.id
		}
		bizUserApi.userExportUserInfo(params).then((res) => {
			downloadUtil.resultDownload(res)
		})
	}
	// 传递设计器需要的API
	const selectorApiFunction = {
		orgTreeApi: (param) => {
			return bizUserApi.userOrgTreeSelector(param).then((orgTree) => {
				return Promise.resolve(orgTree)
			})
		},
		rolePageApi: (param) => {
			return bizUserApi.userRoleSelector(param).then((data) => {
				return Promise.resolve(data)
			})
		}
	}
</script>

<style scoped>

</style>
