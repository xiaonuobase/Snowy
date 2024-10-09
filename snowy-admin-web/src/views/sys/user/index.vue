<template>
	<a-row :gutter="10">
		<a-col :xs="24" :sm="24" :md="24" :lg="5" :xl="5">
			<a-card class="mb-3" :bordered="false" :loading="cardLoading">
				<a-tree
					v-if="treeData.length > 0"
					v-model:expandedKeys="defaultExpandedKeys"
					:tree-data="treeData"
					:field-names="treeFieldNames"
					@select="treeSelect"
				>
				</a-tree>
				<a-empty v-else :image="Empty.PRESENTED_IMAGE_SIMPLE" />
			</a-card>
		</a-col>
		<a-col :xs="24" :sm="24" :md="24" :lg="19" :xl="19">
			<a-card :bordered="false" class="xn-mb10">
				<a-form ref="searchFormRef" name="advanced_search" class="ant-advanced-search-form" :model="searchFormState">
					<a-row :gutter="24">
						<a-col :span="8">
							<a-form-item name="searchKey" :label="$t('common.searchKey')">
								<a-input
									v-model:value="searchFormState.searchKey"
									:placeholder="$t('user.placeholderNameAndSearchKey')"
								/>
							</a-form-item>
						</a-col>
						<a-col :span="8">
							<a-form-item name="userStatus" :label="$t('user.userStatus')">
								<a-select
									v-model:value="searchFormState.userStatus"
									:placeholder="$t('user.placeholderUserStatus')"
									:getPopupContainer="(trigger) => trigger.parentNode"
								>
									<a-select-option v-for="item in statusData" :key="item.value" :value="item.value">{{
										item.label
									}}</a-select-option>
								</a-select>
							</a-form-item>
						</a-col>
						<a-col :span="8">
							<a-button type="primary" @click="tableRef.refresh(true)">
								<template #icon><SearchOutlined /></template>
								{{ $t('common.searchButton') }}
							</a-button>
							<a-button class="snowy-button-left" @click="reset">
								<template #icon><redo-outlined /></template>
								{{ $t('common.resetButton') }}
							</a-button>
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
					:row-key="(record) => record.id"
					:row-selection="options.rowSelection"
				>
					<template #operator class="table-operator">
						<a-space>
							<a-button type="primary" @click="formRef.onOpen(undefined, searchFormState.orgId)">
								<template #icon><plus-outlined /></template>
								<span>{{ $t('common.addButton') }}{{ $t('model.user') }}</span>
							</a-button>
							<a-button @click="ImpExpRef.onOpen()">
								<template #icon><import-outlined /></template>
								<span>{{ $t('common.imports') }}</span>
							</a-button>
							<a-button @click="exportBatchUserVerify">
								<template #icon><export-outlined /></template>
								{{ $t('user.batchExportButton') }}
							</a-button>
							<xn-batch-button
								:buttonName="$t('common.batchRemoveButton')"
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
							<a-switch :loading="loading" :checked="record.userStatus === 'ENABLE'" @change="editStatus(record)" />
						</template>
						<template v-if="column.dataIndex === 'action'">
							<a @click="formRef.onOpen(record)">{{ $t('common.editButton') }}</a>
							<a-divider type="vertical" />
							<a-popconfirm :title="$t('user.popconfirmDeleteUser')" placement="topRight" @confirm="removeUser(record)">
								<a-button type="link" danger size="small">
									{{ $t('common.removeButton') }}
								</a-button>
							</a-popconfirm>
							<a-divider type="vertical" />
							<a-dropdown>
								<a class="ant-dropdown-link">
									{{ $t('common.more') }}
									<DownOutlined />
								</a>
								<template #overlay>
									<a-menu>
										<a-menu-item>
											<a-popconfirm
												:title="$t('user.popconfirmResatUserPwd')"
												placement="topRight"
												@confirm="resetPassword(record)"
											>
												<a>{{ $t('user.resetPassword') }}</a>
											</a-popconfirm>
										</a-menu-item>
										<a-menu-item>
											<a @click="selectRole(record)">{{ $t('user.grantRole') }}</a>
										</a-menu-item>
										<a-menu-item>
											<a @click="grantResourceFormRef.onOpen(record)">{{ $t('user.grantResource') }}</a>
										</a-menu-item>
										<a-menu-item>
											<a @click="grantPermissionFormRef.onOpen(record)">{{ $t('user.grantPermission') }}</a>
										</a-menu-item>
										<a-menu-item>
											<a @click="exportUserInfo(record)">{{ $t('user.exportUserInfo') }}</a>
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
		:show="false"
		:role-global="true"
		@onBack="roleBack"
	/>
	<ImpExp ref="ImpExpRef" />
	<grantResourceForm ref="grantResourceFormRef" @successful="tableRef.refresh()" />
	<grantPermissionForm ref="grantPermissionFormRef" @successful="tableRef.refresh()" />
</template>

<script setup name="sysUser">
	import { message, Empty } from 'ant-design-vue'
	import { isEmpty } from 'lodash-es'
	import tool from '@/utils/tool'
	import downloadUtil from '@/utils/downloadUtil'
	import userApi from '@/api/sys/userApi'
	import orgApi from '@/api/sys/orgApi'
	import Form from './form.vue'
	import ImpExp from './impExp.vue'
	import GrantResourceForm from './grantResourceForm.vue'
	import GrantPermissionForm from './grantPermissionForm.vue'

	const columns = [
		{
			title: '头像',
			dataIndex: 'avatar',
			align: 'center',
			width: '80px'
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
			dataIndex: 'genderName',
			width: '50px'
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
			dataIndex: 'userStatus',
			width: '80px'
		},
		{
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: '220px'
		}
	]
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
	const ImpExpRef = ref()
	const grantResourceFormRef = ref()
	const grantPermissionFormRef = ref()
	// 表格查询 返回 Promise 对象
	const loadData = (parameter) => {
		return userApi.userPage(Object.assign(parameter, searchFormState.value)).then((res) => {
			return res
		})
	}
	// 左侧树查询
	orgApi.orgTree().then((res) => {
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
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
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
			userApi
				.userDisableUser(record)
				.then(() => {
					tableRef.value.refresh()
				})
				.finally(() => {
					loading.value = false
				})
		} else {
			userApi
				.userEnableUser(record)
				.then(() => {
					tableRef.value.refresh()
				})
				.finally(() => {
					loading.value = false
				})
		}
	}
	// 删除用户
	const removeUser = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		userApi.userDelete(params).then(() => {
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
		userApi.userExport(params).then((res) => {
			downloadUtil.resultDownload(res)
			tableRef.value.clearSelected()
		})
	}
	// 批量删除
	const deleteBatchUser = (params) => {
		userApi.userDelete(params).then(() => {
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
		userApi.userOwnRole(param).then((data) => {
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
		userApi.grantRole(params).then(() => {})
	}
	// 重置用户密码
	const resetPassword = (record) => {
		userApi.userResetPassword(record).then(() => {})
	}
	// 导出用户信息
	const exportUserInfo = (record) => {
		const params = {
			id: record.id
		}
		userApi.userExportUserInfo(params).then((res) => {
			downloadUtil.resultDownload(res)
		})
	}
	// 传递设计器需要的API
	const selectorApiFunction = {
		orgTreeApi: (param) => {
			return userApi.userOrgTreeSelector(param).then((data) => {
				return Promise.resolve(data)
			})
		},
		rolePageApi: (param) => {
			return userApi.userRoleSelector(param).then((data) => {
				return Promise.resolve(data)
			})
		}
	}
</script>

<style scoped>
	.ant-form-item {
		margin-bottom: 0 !important;
	}
	.snowy-table-avatar {
		margin-top: -10px;
		margin-bottom: -10px;
	}
	.snowy-button-left {
		margin-left: 8px;
	}
</style>
