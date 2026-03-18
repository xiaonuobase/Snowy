<template>
	<XnResizablePanel direction="row" :initial-size="300" :min-size="200" :max-size="500" :md="0">
		<template #left>
			<div ref="treeContainerRef" style="height: 100%; display: flex; flex-direction: column">
				<a-input-search
					v-model:value="treeSearchKey"
					placeholder="搜索组织"
					allow-clear
					size="small"
					style="margin-bottom: 8px; flex-shrink: 0"
					@search="onTreeSearch"
				/>
				<div style="flex: 1; overflow: hidden">
					<xn-tree-skeleton v-if="treeLoading && treeData.length === 0" />
					<a-tree
						v-else-if="treeData.length > 0"
						v-model:expandedKeys="defaultExpandedKeys"
						v-model:loadedKeys="treeLoadedKeys"
						:show-line="{ showLeafIcon: false }"
						:tree-data="treeData"
						:field-names="treeFieldNames"
						:load-data="searchMode ? undefined : onLoadData"
						:height="treeHeight"
						@select="treeSelect"
					/>
					<a-empty v-else :image="Empty.PRESENTED_IMAGE_SIMPLE" />
				</div>
			</div>
		</template>
		<template #right>
			<a-form ref="searchFormRef" :model="searchFormState">
				<a-row :gutter="10">
					<a-col :xs="24" :sm="8" :md="8" :lg="0" :xl="0">
						<a-form-item label="组织：" name="categoryOrOrgId">
							<a-tree-select
								v-model:value="searchFormState.categoryOrOrgId"
								class="xn-wd"
								:dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
								placeholder="请选择组织"
								allow-clear
								:tree-data="treeData"
								:field-names="treeSelectFieldNames"
								tree-line
								:load-data="onLoadData"
								@change="onCategoryOrOrgIdSelect"
							/>
						</a-form-item>
					</a-col>
					<a-col :xs="24" :sm="8" :md="8" :lg="8" :xl="8">
						<a-form-item name="searchKey" label="关键词">
							<a-input v-model:value="searchFormState.searchKey" placeholder="请输入角色名称关键词"></a-input>
						</a-form-item>
					</a-col>
					<a-col :xs="24" :sm="8" :md="8" :lg="8" :xl="8">
						<a-form-item>
							<a-space>
								<a-button type="primary" @click="tableRef.refresh(true)">
									<template #icon>
										<SearchOutlined />
									</template>
									查询
								</a-button>
								<a-button @click="reset">
									<template #icon>
										<redo-outlined />
									</template>
									重置
								</a-button>
							</a-space>
						</a-form-item>
					</a-col>
				</a-row>
			</a-form>
			<s-table
				ref="tableRef"
				:columns="columns"
				:data="loadData"
				:expand-row-by-click="true"
				:alert="options.alert.show"
				bordered
				:row-key="(record) => record.id"
				:row-selection="options.rowSelection"
				:scroll="{ x: 'max-content' }"
			>
				<template #operator>
					<a-space>
						<a-button
							type="primary"
							@click="formRef.onOpen(undefined, searchFormState.category, searchFormState.orgId)"
						>
							<template #icon><plus-outlined /></template>
							新增角色
						</a-button>
						<xn-batch-button
							buttonName="批量删除"
							icon="DeleteOutlined"
							buttonDanger
							:selectedRowKeys="selectedRowKeys"
							@batchCallBack="deleteBatchRole"
						/>
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
		</template>
	</XnResizablePanel>
	<grantResourceForm ref="grantResourceFormRef" @successful="tableRef.refresh()" />
	<grantMobileResourceForm ref="grantMobileResourceFormRef" @successful="tableRef.refresh()" />
	<grantPermissionForm ref="grantPermissionFormRef" @successful="tableRef.refresh()" />
	<Form ref="formRef" @successful="tableRef.refresh()" />
	<xn-user-selector
		ref="userSelectorPlusRef"
		:org-tree-api="selectorApiFunction.orgTreeApi"
		:user-page-api="selectorApiFunction.userPageApi"
		data-type="object"
		:user-show="false"
		@onBack="userCallBack"
	/>
</template>

<script setup name="sysRole">
	import { Empty } from 'ant-design-vue'
	import { triggerRef, onMounted, onActivated, onUnmounted } from 'vue'
	import roleApi from '@/api/sys/roleApi'
	import orgApi from '@/api/sys/orgApi'
	import GrantResourceForm from './grantResourceForm.vue'
	import GrantMobileResourceForm from './grantMobileResourceForm.vue'
	import GrantPermissionForm from './grantPermissionForm.vue'
	import Form from './form.vue'

	const columns = [
		{
			title: '角色名称',
			dataIndex: 'name'
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
			fixed: 'right'
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
	const tableRef = ref()
	const formRef = ref()
	const grantResourceFormRef = ref()
	const grantMobileResourceFormRef = ref()
	const grantPermissionFormRef = ref()
	const userSelectorPlusRef = ref()
	const searchFormRef = ref()
	const searchFormState = ref({})
	// 默认展开的节点
	const defaultExpandedKeys = ref([])
	const treeData = ref([])
	// 替换treeNode 中 title,key,children
	const treeFieldNames = { children: 'children', title: 'name', key: 'id' }
	const treeSelectFieldNames = { children: 'children', label: 'name', value: 'id' }
	// 记录数据
	const recordCacheData = ref({})
	// 树容器高度自适应
	const treeContainerRef = ref(null)
	const treeHeight = ref(0)
	let resizeObserver = null
	const calcTreeHeight = () => {
		if (treeContainerRef.value) {
			treeHeight.value = treeContainerRef.value.clientHeight - 40
		}
	}
	onMounted(() => {
		calcTreeHeight()
		if (treeContainerRef.value) {
			resizeObserver = new ResizeObserver(calcTreeHeight)
			resizeObserver.observe(treeContainerRef.value)
		}
	})
	onActivated(calcTreeHeight)
	onUnmounted(() => {
		if (resizeObserver) {
			resizeObserver.disconnect()
		}
	})

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
		delete searchFormState.value.categoryOrOrgId
		delete searchFormState.value.orgId
		delete searchFormState.value.category
		tableRef.value.refresh(true)
	}
	const treeLoading = ref(true)
	const treeSearchKey = ref('')
	const searchMode = ref(false)
	const treeLoadedKeys = ref([])
	const collectTreeKeys = (nodes) => {
		const keys = []
		const traverse = (list) => {
			if (!list) return
			list.forEach((node) => {
				keys.push(node.id)
				if (node.children) traverse(node.children)
			})
		}
		traverse(nodes)
		return keys
	}
	const onTreeSearch = (value) => {
		if (!value || !value.trim()) {
			// 先清空树数据和展开状态，再切换模式，避免懒加载风暴导致卡死
			treeData.value = []
			defaultExpandedKeys.value = []
			treeLoadedKeys.value = []
			searchMode.value = false
			loadTreeData()
			return
		}
		treeLoading.value = true
		searchMode.value = true
		orgApi
			.orgTree({ searchKey: value.trim() })
			.then((res) => {
				if (res !== null) {
					// 搜索模式下也保留全局节点
					treeData.value = [{ id: 'GLOBAL', name: '全局', isLeaf: true }, ...res]
					defaultExpandedKeys.value = collectTreeKeys(res)
				} else {
					treeData.value = [{ id: 'GLOBAL', name: '全局', isLeaf: true }]
				}
			})
			.finally(() => {
				treeLoading.value = false
			})
	}
	// 加载左侧的树
	const loadTreeData = () => {
		treeLoading.value = true
		orgApi
			.orgTree()
			.then((res) => {
				if (res !== null) {
					// 树中插入全局角色类型
					const globalRoleType = [
						{
							id: 'GLOBAL',
							parentId: '-1',
							name: '全局',
							isLeaf: true
						}
					]
					treeLoadedKeys.value = []
					defaultExpandedKeys.value = []
					treeData.value = globalRoleType.concat(
						res.map((item) => {
							return {
								...item,
								isLeaf: item.isLeaf === undefined ? false : item.isLeaf
							}
						})
					)
					// 只有一个根节点时才自动展开
					if (treeData.value.length === 1) {
						defaultExpandedKeys.value = [treeData.value[0].id]
					}
				}
			})
			.finally(() => {
				treeLoading.value = false
			})
	}
	loadTreeData()
	// 懒加载子节点
	const onLoadData = (treeNode) => {
		return new Promise((resolve) => {
			if (treeNode.dataRef.children || treeNode.dataRef.isLeaf) {
				resolve()
				return
			}
			orgApi
				.orgTree({ parentId: treeNode.dataRef.id })
				.then((res) => {
					treeNode.dataRef.children = res.map((item) => {
						return {
							...item,
							isLeaf: item.isLeaf === undefined ? false : item.isLeaf
						}
					})
					triggerRef(treeData)
					resolve()
				})
				.catch(() => {
					resolve()
				})
		})
	}
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
		tableRef.value.refresh(true)
	}
	// 下拉树点击的情况
	const onCategoryOrOrgIdSelect = (selectedId) => {
		searchFormState.value.current = 0
		if (selectedId === 'GLOBAL') {
			searchFormState.value.category = selectedId
			delete searchFormState.value.orgId
		} else {
			searchFormState.value.orgId = selectedId
			delete searchFormState.value.category
		}
	}
	// 删除
	const removeOrg = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		roleApi.roleDelete(params).then(() => {
			tableRef.value.refresh(true)
		})
	}
	// 批量删除
	const deleteBatchRole = (params) => {
		roleApi.roleDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
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
			userSelectorPlusRef.value.showUserPlusModal(data)
		})
	}
	// 人员选择器回调
	const userCallBack = (value) => {
		const param = {
			id: recordCacheData.value.id,
			grantInfoList: value
		}
		roleApi.roleGrantUser(param).then(() => {})
	}
	// 传递设计器需要的API
	const selectorApiFunction = {
		orgTreeApi: (param) => {
			return orgApi.orgTreeSelector(param).then((data) => {
				return Promise.resolve(data)
			})
		},
		userPageApi: (param) => {
			return roleApi.roleUserSelector(param).then((data) => {
				return Promise.resolve(data)
			})
		}
	}
</script>
