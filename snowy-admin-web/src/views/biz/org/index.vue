<template>
	<XnResizablePanel direction="row" :initial-size="300" :min-size="200" :max-size="500" :md="0">
		<template #left>
			<div ref="treeContainerRef" style="height: 100%; display: flex; flex-direction: column">
				<a-input-search
					v-model:value="treeSearchKey"
					placeholder="搜索机构"
					allow-clear
					size="small"
					style="margin-bottom: 8px; flex-shrink: 0"
					@search="onTreeSearch"
				/>
				<div style="flex: 1; overflow: hidden">
					<xn-tree-skeleton v-if="treeLoading && treeData.length === 0" />
					<a-spin v-else-if="treeData.length > 0" :spinning="treeLoading">
						<a-tree
							v-model:expandedKeys="defaultExpandedKeys"
							v-model:loadedKeys="treeLoadedKeys"
							:show-line="{ showLeafIcon: false }"
							:tree-data="treeData"
							:field-names="treeFieldNames"
							:load-data="searchMode ? undefined : onLoadData"
							:height="treeHeight"
							@select="treeSelect"
						/>
					</a-spin>
					<a-empty v-else :image="Empty.PRESENTED_IMAGE_SIMPLE" />
				</div>
			</div>
		</template>
		<template #right>
			<a-form ref="searchFormRef" :model="searchFormState">
				<a-row :gutter="10">
					<a-col :xs="24" :sm="8" :md="8" :lg="0" :xl="0">
						<a-form-item label="上级机构：" name="parentId">
							<a-tree-select
								v-model:value="searchFormState.parentId"
								class="xn-wd"
								:dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
								placeholder="请选择上级机构"
								allow-clear
								:tree-data="treeData"
								:field-names="treeSelectFieldNames"
								tree-line
								:load-data="onLoadData"
							/>
						</a-form-item>
					</a-col>
					<a-col :xs="24" :sm="8" :md="8" :lg="8" :xl="8">
						<a-form-item name="searchKey" label="名称">
							<a-input v-model:value="searchFormState.searchKey" placeholder="请输入名称关键词" />
						</a-form-item>
					</a-col>
					<a-col :xs="24" :sm="16" :md="16" :lg="16" :xl="16">
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
			<s-table
				ref="tableRef"
				:columns="columns"
				:data="loadData"
				:expand-row-by-click="true"
				:alert="options.alert.show"
				bordered
				:row-key="(record) => record.id"
				:tool-config="toolConfig"
				:row-selection="options.rowSelection"
				:scroll="{ x: 'max-content' }"
			>
				<template #operator>
					<a-space>
						<a-button
							type="primary"
							@click="formRef.onOpen(undefined, searchFormState.parentId)"
							v-if="hasPerm('bizOrgAdd')"
						>
							<template #icon><plus-outlined /></template>
							新增
						</a-button>
						<xn-batch-button
							v-if="hasPerm('bizOrgCopy')"
							buttonName="批量复制"
							icon="CopyOutlined"
							:isPopconFirm="false"
							:selectedRowKeys="selectedRowKeys"
							@batchCallBack="copyBatchOrg"
						/>
						<xn-batch-button
							v-if="hasPerm('bizOrgBatchDelete')"
							buttonName="批量删除"
							icon="DeleteOutlined"
							buttonDanger
							:selectedRowKeys="selectedRowKeys"
							@batchCallBack="deleteBatchOrg"
						/>
					</a-space>
				</template>
				<template #bodyCell="{ column, record }">
					<template v-if="column.dataIndex === 'category'">
						{{ $TOOL.dictTypeData('ORG_CATEGORY', record.category) }}
					</template>
					<template v-if="column.dataIndex === 'action'">
						<a @click="formRef.onOpen(record)" v-if="hasPerm('bizOrgEdit')">编辑</a>
						<a-divider type="vertical" v-if="hasPerm(['bizOrgEdit', 'bizOrgDelete'], 'and')" />
						<a-popconfirm title="删除此机构与下级机构吗？" @confirm="removeOrg(record)">
							<a-button type="link" danger size="small" v-if="hasPerm('bizOrgDelete')">删除</a-button>
						</a-popconfirm>
					</template>
				</template>
			</s-table>
		</template>
	</XnResizablePanel>
	<Form ref="formRef" @successful="onFormSuccess" />
	<CopyForm ref="copyFormRef" @successful="onCopyFormSuccess" />
</template>

<script setup name="bizOrg">
	import { Empty } from 'ant-design-vue'
	import { triggerRef, onMounted, onActivated, onUnmounted } from 'vue'
	import bizOrgApi from '@/api/biz/bizOrgApi'
	import Form from './form.vue'
	import CopyForm from './copyForm.vue'

	const columns = [
		{
			title: '机构名称',
			dataIndex: 'name'
		},
		{
			title: '分类',
			dataIndex: 'category'
		},
		{
			title: '排序',
			dataIndex: 'sortCode'
		}
	]
	if (hasPerm(['bizOrgEdit', 'bizOrgDelete'])) {
		columns.push({
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			fixed: 'right'
		})
	}
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
	const toolConfig = { refresh: true, height: true, columnSetting: true }
	// 定义tableDOM
	const tableRef = ref(null)
	const formRef = ref()
	const searchFormRef = ref()
	const searchFormState = ref({})
	// 默认展开的节点
	const defaultExpandedKeys = ref([])
	const treeData = ref([])
	// 替换treeNode 中 title,key,children
	const treeFieldNames = { children: 'children', title: 'name', key: 'id' }
	const treeSelectFieldNames = { children: 'children', label: 'name', value: 'id' }
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
		return bizOrgApi.orgPage(Object.assign(parameter, searchFormState.value)).then((res) => {
			return res
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
	const treeLoading = ref(true)
	const treeSearchKey = ref('')
	const searchMode = ref(false)
	const treeLoadedKeys = ref([])
	// 收集树所有节点key，用于搜索时全部展开
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
	// 树搜索
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
		bizOrgApi
			.orgTree({ searchKey: value.trim() })
			.then((res) => {
				if (res !== null) {
					treeData.value = res
					defaultExpandedKeys.value = collectTreeKeys(res)
				} else {
					treeData.value = []
				}
			})
			.finally(() => {
				treeLoading.value = false
			})
	}
	// 加载左侧的树
	const loadTreeData = () => {
		treeLoading.value = true
		bizOrgApi
			.orgTree()
			.then((res) => {
				if (res !== null) {
					// 重置懒加载状态和展开状态，避免树组件缓存导致无法展开
					treeLoadedKeys.value = []
					defaultExpandedKeys.value = []
					treeData.value = res.map((item) => {
						return {
							...item,
							isLeaf: item.isLeaf === undefined ? false : item.isLeaf
						}
					})
					// 只有一个根节点时自动展开
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
	// 刷新树数据（增删改后调用，使用全量树接口保留展开状态）
	const refreshTreeData = () => {
		treeLoading.value = true
		treeData.value = []
		bizOrgApi
			.orgTree({ searchKey: '' })
			.then((res) => {
				if (res !== null) {
					treeData.value = res
				}
			})
			.finally(() => {
				treeLoading.value = false
			})
	}
	// 懒加载子节点
	const onLoadData = (treeNode) => {
		return new Promise((resolve) => {
			if (treeNode.dataRef.children || treeNode.dataRef.isLeaf) {
				resolve()
				return
			}
			bizOrgApi
				.orgTree({
					parentId: treeNode.dataRef.id
				})
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
		})
	}
	// 点击树查询
	const treeSelect = (selectedKeys) => {
		if (selectedKeys.length > 0) {
			searchFormState.value.parentId = selectedKeys.toString()
		} else {
			delete searchFormState.value.parentId
		}
		tableRef.value.refresh(true)
	}
	// 删除
	const removeOrg = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		bizOrgApi.orgDelete(params).then(() => {
			tableRef.value.refresh(true)
			refreshTreeData()
		})
	}
	// 批量删除
	const deleteBatchOrg = (params) => {
		bizOrgApi.orgDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
			refreshTreeData()
		})
	}
	// 表单成功回调
	const onFormSuccess = () => {
		tableRef.value.refresh()
		refreshTreeData()
	}
	// 复制表单成功回调
	const onCopyFormSuccess = () => {
		tableRef.value.clearRefreshSelected()
		refreshTreeData()
	}
	// 批量复制
	const copyFormRef = ref()
	const copyBatchOrg = (params) => {
		copyFormRef.value.onOpen(params)
	}
</script>
