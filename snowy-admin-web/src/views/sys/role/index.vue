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
							<a-button class="snowy-buttom-left" @click="() => searchFormRef.resetFields()">
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
							<a-button type="primary" @click="form.onOpen()">
								<template #icon><plus-outlined /></template>
								新增角色
							</a-button>
							<a-button danger @click="deleteBatchRole()">删除</a-button>
						</a-space>
					</template>
					<template #bodyCell="{ column, record }">
						<template v-if="column.dataIndex === 'category'">
							{{ $TOOL.dictTypeData('ROLE_CATEGORY', record.category) }}
						</template>
						<template v-if="column.dataIndex === 'action'">
							<a @click="form.onOpen(record)">编辑</a>
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
											<a @click="GrantResourceForm.onOpen(record)">授权资源</a>
										</a-menu-item>
										<a-menu-item>
											<a @click="GrantPermissionForm.onOpen(record)">授权权限</a>
										</a-menu-item>
										<a-menu-item>
											<a @click="openRoleUserSelector(record)">授权用户</a>
										</a-menu-item>
									</a-menu>
								</template>
							</a-dropdown>
						</template>
					</template>
					<!-- <template #summary>
						<a-table-summary-row>
							<a-table-summary-cell :index="0">汇 总</a-table-summary-cell>
							<a-table-summary-cell :index="1"></a-table-summary-cell>
							<a-table-summary-cell :index="2"></a-table-summary-cell>
							<a-table-summary-cell :index="3">
								<a-typography-text type="danger">{{ summaryData.sortCode }}</a-typography-text>
							</a-table-summary-cell>
							<a-table-summary-cell :index="4"></a-table-summary-cell>
						</a-table-summary-row>
					</template> -->
				</s-table>
			</a-card>
		</a-col>
	</a-row>
	<grantResourceForm ref="GrantResourceForm" @successful="table.refresh(true)" />
	<grantPermissionForm ref="GrantPermissionForm" @successful="table.refresh(true)" />
	<Form ref="form" @successful="table.refresh(true)" />
	<user-selector-plus
		ref="userselectorPlusRef"
		page-url="/sys/role/userSelector"
		org-url="/sys/role/orgTreeSelector"
		@onBack="userCallBack"
	/>
</template>

<script setup name="sysRole">
	import { message, Empty } from 'ant-design-vue'
	import roleApi from '@/api/sys/roleApi'
	import orgApi from '@/api/sys/orgApi'
	import grantResourceForm from './grantResourceForm.vue'
	import grantPermissionForm from './grantPermissionForm.vue'
	import userSelectorPlus from '@/components/Selector/userSelectorPlus.vue'
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
			dataIndex: 'sortCode'
		},
		{
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: '200px'
		}
	]
	let selectedRowKeys = ref([])
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
	// 定义tableDOM
	const table = ref()
	const form = ref()
	const GrantResourceForm = ref()
	const GrantPermissionForm = ref()
	const userselectorPlusRef = ref()
	const searchFormRef = ref()
	let searchFormState = reactive({})
	// 默认展开的节点
	let defaultExpandedKeys = ref([])
	const treeData = ref([])
	// 替换treeNode 中 title,key,children
	const treeFieldNames = { children: 'children', title: 'name', key: 'id' }
	const cardLoading = ref(true)
	// 记录数据
	const recordCacheData = ref({})

	// 表格查询 返回 Promise 对象
	const loadData = (parameter) => {
		let param = Object.assign(parameter, searchFormState)
		summaryDataReq(param)
		return roleApi.rolePage(param).then((res) => {
			return res
		})
	}
	// 计算汇总数据
	const summaryData = {}
	const summaryDataReq = (param) => {
		summaryData.sortCode = ref(0)
		roleApi.roleSummary(param).then((summary) => {
			if(summary != null){
				return summaryData.sortCode = summary.sortCode
			}
		})
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
	// 点击树查询
	const treeSelect = (selectedKeys) => {
		if (selectedKeys.length > 0) {
			if (selectedKeys[0] === 'GLOBAL') {
				searchFormState.category = selectedKeys[0]
				delete searchFormState.orgId
			} else {
				searchFormState.orgId = selectedKeys.toString()
				delete searchFormState.category
			}
		} else {
			delete searchFormState.category
			delete searchFormState.orgId
		}
		table.value.refresh(true)
	}
	// 可伸缩列
	const handleResizeColumn = (w, col) => {
		col.width = w;
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
	const deleteBatchRole = () => {
		if (selectedRowKeys.value.length < 1) {
			message.warning('请选择一条或多条数据')
			return false
		}
		const params = selectedRowKeys.value.map((m) => {
			return {
				id: m
			}
		})
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
