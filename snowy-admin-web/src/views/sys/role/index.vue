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
			<a-card :bordered="false" style="margin-bottom: 10px">
				<a-form ref="searchFormRef" name="advanced_search" class="ant-advanced-search-form" :model="searchFormState">
					<a-row :gutter="24">
						<a-col :span="8">
							<a-form-item name="searchKey" label="名称关键词">
								<a-input v-model:value="searchFormState.searchKey" placeholder="请输入角色名称关键词"></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="8">
							<a-button type="primary" @click="table.refresh(true)">
								<template #icon><SearchOutlined /></template>
								查询
							</a-button>
							<a-button class="snowy-buttom-left" @click="reset">
								<template #icon><redo-outlined /></template>
								重置
							</a-button>
						</a-col>
					</a-row>
				</a-form>
			</a-card>
			<a-card :bordered="false">
				<s-table
					ref="table"
					:columns="columns"
					:data="loadData"
					:expand-row-by-click="true"
					:alert="options.alert.show"
					bordered
					:row-key="(record) => record.id"
					:row-selection="options.rowSelection"
					@resizeColumn="handleResizeColumn"
				>
					<template #operator class="table-operator">
						<a-space>
							<a-button type="primary" @click="formRef.onOpen(undefined, searchFormState.category, searchFormState.orgId)">
								<template #icon><plus-outlined /></template>
								新增角色
							</a-button>
							<xn-batch-delete :selectedRowKeys="selectedRowKeys" @batchDelete="deleteBatchRole" />
						</a-space>
					</template>
					<template #bodyCell="{ column, record }">
						<template v-if="column.dataIndex === 'category'">
							{{ $TOOL.dictTypeData('ROLE_CATEGORY', record.category) }}
						</template>
						<template v-if="column.dataIndex === 'action'">
							<a @click="formRef.onOpen(record)">编辑</a>
							<a-divider type="vertical" />
							<a-popconfirm title="确定删除此角色？" @confirm="removeOrg(record)">
								<a-button type="link" danger size="small">删除</a-button>
							</a-popconfirm>
							<a-divider type="vertical" />
							<a-dropdown>
								<a class="ant-dropdown-link">
									授权
									<DownOutlined />
								</a>
								<template #overlay>
									<a-menu>
										<a-menu-item>
											<a @click="grantResourceFormRef.onOpen(record)">授权资源</a>
										</a-menu-item>
										<a-menu-item>
											<a @click="grantMobileResourceFormRef.onOpen(record)">授权移动端资源</a>
										</a-menu-item>
										<a-menu-item>
											<a @click="grantPermissionFormRef.onOpen(record)">授权权限</a>
										</a-menu-item>
										<a-menu-item>
											<a @click="openRoleUserSelector(record)">授权用户</a>
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
	<grantResourceForm ref="grantResourceFormRef" @successful="table.refresh()" />
	<grantMobileResourceForm ref="grantMobileResourceFormRef" @successful="table.refresh()" />
	<grantPermissionForm ref="grantPermissionFormRef" @successful="table.refresh()" />
	<Form ref="formRef" @successful="table.refresh()" />
	<user-selector-plus
		ref="userselectorPlusRef"
		:org-tree-api="selectorApiFunction.orgTreeApi"
		:user-page-api="selectorApiFunction.userPageApi"
		:checked-user-list-api="selectorApiFunction.checkedUserListApi"
		@onBack="userCallBack"
	/>
</template>

<script setup name="sysRole">
	import { Empty } from 'ant-design-vue'
	import { isEmpty } from 'lodash-es'
	import roleApi from '@/api/sys/roleApi'
	import orgApi from '@/api/sys/orgApi'
	import userCenterApi from '@/api/sys/userCenterApi'
	import GrantResourceForm from './grantResourceForm.vue'
	import GrantMobileResourceForm from './grantMobileResourceForm.vue'
	import GrantPermissionForm from './grantPermissionForm.vue'
	import UserSelectorPlus from '@/components/Selector/userSelectorPlus.vue'
	import Form from './form.vue'

	const columns = [
		{
			title: '角色名称',
			dataIndex: 'name',
			resizable: true,
			width: 150
		},
		{
			title: '分类',
			dataIndex: 'category'
		},
		{
			title: '排序',
			dataIndex: 'sortCode',
			width: 100
		},
		{
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: '200px'
		}
	]
	const selectedRowKeys = ref([])
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
	// 定义tableDOM
	const table = ref()
	const formRef = ref()
	const grantResourceFormRef = ref()
	const grantMobileResourceFormRef = ref()
	const grantPermissionFormRef = ref()
	const userselectorPlusRef = ref()
	const searchFormRef = ref()
	const searchFormState = ref({})
	// 默认展开的节点
	const defaultExpandedKeys = ref([])
	const treeData = ref([])
	// 替换treeNode 中 title,key,children
	const treeFieldNames = { children: 'children', title: 'name', key: 'id' }
	const cardLoading = ref(true)
	// 记录数据
	const recordCacheData = ref({})

	// 表格查询 返回 Promise 对象
	const loadData = (parameter) => {
		let param = Object.assign(parameter, searchFormState.value)
		return roleApi.rolePage(param).then((res) => {
			return res
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		table.value.refresh(true)
	}
	// 加载左侧的树
	orgApi.orgTree().then((res) => {
		cardLoading.value = false
		if (res !== null) {
			// 树中插入全局角色类型
			const globalRoleType = [
				{
					id: 'GLOBAL',
					parentId: '-1',
					name: '全局'
				}
			]
			treeData.value = globalRoleType.concat(res)
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
	// 点击树查询
	const treeSelect = (selectedKeys) => {
		if (selectedKeys.length > 0) {
			if (selectedKeys[0] === 'GLOBAL') {
				searchFormState.value.category = selectedKeys[0]
				delete searchFormState.value.orgId
			} else {
				searchFormState.value.orgId = selectedKeys.toString()
				delete searchFormState.value.category
			}
		} else {
			delete searchFormState.value.category
			delete searchFormState.value.orgId
		}
		table.value.refresh(true)
	}
	// 可伸缩列
	const handleResizeColumn = (w, col) => {
		col.width = w
	}
	// 删除
	const removeOrg = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		roleApi.roleDelete(params).then(() => {
			table.value.refresh(true)
		})
	}
	// 批量删除
	const deleteBatchRole = (params) => {
		roleApi.roleDelete(params).then(() => {
			table.value.clearRefreshSelected()
		})
	}
	// 打开用户选择器
	const openRoleUserSelector = (record) => {
		// 打开人员选择器的时候，缓存一个记录数据
		recordCacheData.value = record
		// 查询接口，查到这个角色是多少个用户都有它
		const param = {
			id: record.id
		}
		roleApi.roleOwnUser(param).then((data) => {
			userselectorPlusRef.value.showUserPlusModal(data)
		})
	}
	// 人员选择器回调
	const userCallBack = (value) => {
		const param = {
			id: recordCacheData.value.id,
			grantInfoList: value.map((item) => {
				return item.id
			})
		}
		roleApi.roleGrantUser(param).then(() => {})
	}
	// 传递设计器需要的API
	const selectorApiFunction = {
		orgTreeApi: (param) => {
			return roleApi.roleOrgTreeSelector(param).then((data) => {
				return Promise.resolve(data)
			})
		},
		userPageApi: (param) => {
			return roleApi.roleUserSelector(param).then((data) => {
				return Promise.resolve(data)
			})
		},
		checkedUserListApi: (param) => {
			return userCenterApi.userCenterGetUserListByIdList(param).then((data) => {
				return Promise.resolve(data)
			})
		}
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
	.snowy-buttom-left {
		margin-left: 8px;
	}
</style>
