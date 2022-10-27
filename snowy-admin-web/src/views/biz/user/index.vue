<template>
	<a-row>
		<a-col :span="5">
			<a-card class="cardImp" :bordered="false" :loading="cardLoading">
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
		<a-col :span="19">
			<a-card :bordered="false">
				<s-table
					ref="table"
					:columns="columns"
					:data="loadData"
					:expand-row-by-click="true"
					bordered
					:alert="options.alert.show"
					:row-key="(record) => record.id"
					:row-selection="options.rowSelection"
				>
					<template #operator class="table-operator">
						<a-form
							ref="searchFormRef"
							name="advanced_search"
							class="ant-advanced-search-form"
							:model="searchFormState"
						>
							<a-row :gutter="24">
								<a-col :span="6">
									<a-form-item name="searchKey">
										<a-input v-model:value="searchFormState.searchKey" placeholder="请输入姓名或账号"></a-input>
									</a-form-item>
								</a-col>
								<a-col :span="6">
									<a-form-item name="userStatus">
										<a-select v-model:value="searchFormState.userStatus" placeholder="请选择状态">
											<a-select-option v-for="item in statusData" :key="item.dictValue" :value="item.dictValue">{{
												item.name
											}}</a-select-option>
										</a-select>
									</a-form-item>
								</a-col>
								<a-col :span="6">
									<a-button type="primary" @click="table.refresh(true)">{{ $t('common.searchButton') }}</a-button>
									<a-button class="snowy-buttom-left" @click="() => searchFormRef.resetFields()">{{
										$t('common.resetButton')
									}}</a-button>
								</a-col>
								<a-col :span="6">
									<a-button type="primary" class="primaryAdd" @click="form.onOpen()" v-if="hasPerm('bizUserAdd')">
										<span>{{ $t('common.addButton') }}{{ $t('model.bizUser') }}</span>
									</a-button>
									<a-button danger @click="removeBatchUser()" v-if="hasPerm('bizUserBatchDelete')">{{
										$t('common.batchRemoveButton')
									}}</a-button>
								</a-col>
							</a-row>
						</a-form>
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
							<a @click="form.onOpen(record)" v-if="hasPerm('bizUserEdit')">{{ $t('common.editButton') }}</a>
							<a-divider type="vertical" v-if="hasPerm(['bizUserEdit', 'bizUserGrantRole'], 'and')" />
							<a @click="selectRole(record)" v-if="hasPerm('bizUserGrantRole')">角色</a>
							<a-divider type="vertical" v-if="hasPerm(['bizUserGrantRole', 'bizUserPwdReset'], 'and')" />
							<a-popconfirm title="确定重置此人员密码？" @confirm="resetPassword(record)">
								<a v-if="hasPerm('bizUserPwdReset')">重置密码</a>
							</a-popconfirm>
							<a-divider type="vertical" v-if="hasPerm(['bizUserPwdReset', 'bizUserDelete'], 'and')" />
							<a-popconfirm title="确定要删除此人员吗？" @confirm="removeUser(record)">
								<a-button type="link" danger size="small" v-if="hasPerm('bizUserDelete')">{{
									$t('common.removeButton')
								}}</a-button>
							</a-popconfirm>
						</template>
					</template>
				</s-table>
			</a-card>
		</a-col>
	</a-row>
	<Form ref="form" @successful="table.refresh(true)" />
	<role-selector-plus
		ref="RoleSelectorPlus"
		page-url="/biz/user/roleSelector"
		org-url="/biz/user/orgTreeSelector"
		:role-global="false"
		@onBack="roleBack"
	/>
</template>
<script setup name="bizUser">
	import { message, Empty } from 'ant-design-vue'
	import { getCurrentInstance } from 'vue'
	import bizUserApi from '@/api/biz/bizUserApi'
	import roleSelectorPlus from '@/components/Selector/roleSelectorPlus.vue'
	import Form from './form.vue'

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
			dataIndex: 'gender',
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
		}
	]
	if (hasPerm(['bizUserEdit', 'bizUserGrantRole', 'bizUserPwdReset', 'bizUserDelete'])) {
		columns.push({
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: '240px'
		})
	}
	const { proxy } = getCurrentInstance()
	const statusData = proxy.$TOOL.dictTypeList('COMMON_STATUS')
	const searchFormRef = ref()
	let defaultExpandedKeys = ref([])
	let searchFormState = reactive({})
	const table = ref(null)
	const treeData = ref([])
	let selectedRowKeys = ref([])
	const treeFieldNames = { children: 'children', title: 'name', key: 'id' }
	let form = ref(null)
	let RoleSelector = ref()
	let RoleSelectorPlus = ref()
	const selectedRecord = ref({})
	const loading = ref(false)
	const cardLoading = ref(true)

	// 表格查询 返回 Promise 对象
	const loadData = (parameter) => {
		return bizUserApi.userPage(Object.assign(parameter, searchFormState)).then((res) => {
			return res
		})
	}
	// 左侧树查询
	bizUserApi.userOrgTreeSelector().then((res) => {
		cardLoading.value = false
		if (res !== null) {
			treeData.value = res
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
	})
	// 列表选择配置
	const options = {
		alert: {
			show: false,
			clear: () => {
				selectedRowKeys = ref([])
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
			searchFormState.orgId = selectedKeys.toString()
		} else {
			delete searchFormState.orgId
		}
		table.value.refresh(true)
	}
	// 修改状态
	const editStatus = (record) => {
		loading.value = true
		if (record.userStatus === 'ENABLE') {
			bizUserApi.userDisableUser(record).then(() => {
				loading.value = false
				table.value.refresh()
			})
		} else {
			bizUserApi.userEnableUser(record).then(() => {
				loading.value = false
				table.value.refresh()
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
			table.value.refresh()
		})
	}
	// 批量删除人员
	const removeBatchUser = () => {
		if (selectedRowKeys.value.length < 1) {
			message.warning('请选择一条或多条数据')
			return
		}
		const params = selectedRowKeys.value.map((m) => {
			return {
				id: m
			}
		})
		bizUserApi.userDelete(params).then(() => {
			table.value.clearRefreshSelected()
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
			RoleSelectorPlus.value.showRolePlusModal(data)
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
				params.roleIdList.push(item.id)
			})
		}
		bizUserApi.grantRole(params).then(() => {})
	}
	// 重置人员密码
	const resetPassword = (record) => {
		bizUserApi.userResetPassword(record).then(() => {})
	}
</script>

<style scoped>
	.cardImp {
		margin-right: 10px;
	}
	.ant-form-item {
		margin-bottom: 0 !important;
	}
	.primaryAdd {
		margin-right: 10px;
	}
	.snowy-table-avatar {
		margin-top: -10px;
		margin-bottom: -10px;
	}
	.snowy-buttom-left {
		margin-left: 8px;
	}
</style>
