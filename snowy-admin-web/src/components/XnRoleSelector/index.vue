<template>
	<!-- 这是引入后展示的样式 -->
	<div v-if="props.show">
		<a-tag v-for="data in dataArray" closable @close="deleteObj(data)" :key="data.id" class="mb-1">{{
			data.name
		}}</a-tag>
		<a-button
			type="primary"
			size="small"
			@click="openModal"
			v-if="(props.radioModel ? dataArray.length !== 1 : true) && addShow"
		>
			<slot name="button"></slot>
			<span v-if="!hasContent('button')">
				<plus-outlined />
				选择
			</span>
		</a-button>
	</div>
	<!-- 以下是弹窗内容 -->
	<a-modal
		v-model:open="visible"
		title="角色选择"
		:width="1000"
		:mask-closable="false"
		:destroy-on-close="true"
		@ok="handleOk"
		@cancel="handleClose"
	>
		<a-row :gutter="10">
			<a-col :span="7">
				<a-card size="small" :loading="cardLoading" class="selectorTreeDiv">
					<a-tree
						v-if="treeData"
						v-model:expandedKeys="defaultExpandedKeys"
						:tree-data="treeData"
						:field-names="treeFieldNames"
						@select="treeSelect"
					>
					</a-tree>
				</a-card>
			</a-col>
			<a-col :span="11">
				<div class="table-operator xn-mb10">
					<a-form ref="searchFormRef" name="advanced_search" class="ant-advanced-search-form" :model="searchFormState">
						<a-row :gutter="24">
							<a-col :span="12">
								<a-form-item name="searchKey">
									<a-input v-model:value="searchFormState.searchKey" placeholder="请输入职位名" />
								</a-form-item>
							</a-col>
							<a-col :span="12">
								<a-button type="primary" class="xn-mr-10" @click="loadData()"> 查询 </a-button>
								<a-button @click="reset()"> 重置 </a-button>
							</a-col>
						</a-row>
					</a-form>
				</div>
				<div class="selector-table">
					<a-table
						ref="tableRef"
						size="small"
						:columns="commons"
						:data-source="tableData"
						:expand-row-by-click="true"
						:loading="pageLoading"
						bordered
						:pagination="false"
					>
						<template #title>
							<span>待选择列表 {{ tableRecordNum }} 条</span>
							<div v-if="!radioModel" class="xn-fdr">
								<a-button type="dashed" size="small" @click="addAllPageRecord">添加当前数据</a-button>
							</div>
						</template>
						<template #bodyCell="{ column, record }">
							<template v-if="column.dataIndex === 'action'">
								<a-button type="dashed" size="small" @click="addRecord(record)"><PlusOutlined /></a-button>
							</template>
							<template v-if="column.dataIndex === 'category'">
								{{ $TOOL.dictTypeData('ROLE_CATEGORY', record.category) }}
							</template>
						</template>
					</a-table>
					<div class="mt-2">
						<a-pagination
							v-if="!isEmpty(tableData)"
							v-model:current="current"
							v-model:page-size="pageSize"
							:total="total"
							size="small"
							showSizeChanger
							@change="paginationChange"
						/>
					</div>
				</div>
			</a-col>
			<a-col :span="6">
				<div class="selector-table">
					<a-table
						ref="selectedTable"
						size="small"
						:columns="selectedCommons"
						:data-source="selectedData"
						:expand-row-by-click="true"
						:loading="selectedTableListLoading"
						bordered
					>
						<template #title>
							<span>已选择: {{ selectedData.length }}</span>
							<div v-if="!radioModel" class="xn-fdr">
								<a-button type="dashed" danger size="small" @click="delAllRecord">全部移除</a-button>
							</div>
						</template>
						<template #bodyCell="{ column, record }">
							<template v-if="column.dataIndex === 'action'">
								<a-button type="dashed" danger size="small" @click="delRecord(record)"><MinusOutlined /></a-button>
							</template>
						</template>
					</a-table>
				</div>
			</a-col>
		</a-row>
	</a-modal>
</template>

<script setup name="roleSelector">
	import { message } from 'ant-design-vue'
	import { remove, isEmpty, cloneDeep } from 'lodash-es'
	import userCenterApi from '@/api/sys/userCenterApi'
	import { useSlots } from 'vue'
	// 弹窗是否打开
	const visible = ref(false)
	// 主表格common
	const commons = [
		{
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: 50
		},
		{
			title: '名称',
			dataIndex: 'name',
			ellipsis: true
		},
		{
			title: '分类',
			dataIndex: 'category'
		}
	]
	// 选中表格的表格common
	const selectedCommons = [
		{
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: 50
		},
		{
			title: '名称',
			dataIndex: 'name',
			ellipsis: true
		}
	]
	const props = defineProps({
		radioModel: {
			type: Boolean,
			default: () => false
		},
		dataIsConverterFlw: {
			type: Boolean,
			default: () => false
		},
		orgTreeApi: {
			type: Function
		},
		rolePageApi: {
			type: Function
		},
		roleListByIdListApi: {
			type: Function
		},
		value: {
			default: () => ''
		},
		dataType: {
			type: String,
			default: () => 'array'
		},
		show: {
			type: Boolean,
			default: () => true
		},
		addShow: {
			type: Boolean,
			default: () => true
		},
		// 是否展示‘全局’这个节点
		roleGlobal: {
			type: Boolean,
			default: true,
			required: false
		}
	})
	// 主表格的ref 名称
	const tableRef = ref()
	// 选中表格的ref 名称
	const selectedTable = ref()
	const tableRecordNum = ref()
	const searchFormState = ref({})
	const searchFormRef = ref()
	const cardLoading = ref(true)
	const pageLoading = ref(false)
	const selectedTableListLoading = ref(false)
	const slots = useSlots()
	// 替换treeNode 中 title,key,children
	const treeFieldNames = { children: 'children', title: 'name', key: 'id' }
	// 获取职位树数据
	const treeData = ref()
	//  默认展开二级树的节点id
	const defaultExpandedKeys = ref([])
	const emit = defineEmits(['update:value', 'onBack'])
	const tableData = ref([])
	const selectedData = ref([])
	const recordIds = ref([])
	// 分页相关
	const current = ref(0) // 当前页数
	const pageSize = ref(10) // 每页条数
	const total = ref(0) // 数据总数
	const hasContent = (slotName) => {
		return !!(slots[slotName] && slots[slotName]().length > 0)
	}
	// 打开弹框
	const showModel = (ids = []) => {
		const data = goDataConverter(ids)
		recordIds.value = data
		getDataNameById(data)
		openModal()
	}
	// 获取机构树的api
	const orgTree = (param) => {
		if (typeof props.orgTreeApi === 'function') {
			return props.orgTreeApi(param)
		} else {
			message.warning('未配置机构树API')
			return Promise.resolve([])
		}
	}
	// 获取列表的api
	const rolePage = (param) => {
		if (typeof props.rolePageApi === 'function') {
			return props.rolePageApi(param)
		} else {
			message.warning('未配置角色选择器API')
			return Promise.resolve([])
		}
	}
	// 获取选中列表的api
	const roleListByIdList = (param) => {
		if (typeof props.roleListByIdListApi === 'function') {
			return props.roleListByIdListApi(param)
		} else {
			return userCenterApi.userCenterGetRoleListByIdList(param).then((data) => {
				return Promise.resolve(data)
			})
		}
	}
	const openModal = () => {
		if (typeof props.orgTreeApi !== 'function' || typeof props.rolePageApi !== 'function') {
			message.warning('未配置角色选择器API')
			return
		}
		visible.value = true
		// 获取机构树
		orgTree()
			.then((data) => {
				if (!isEmpty(data)) {
					treeData.value = data
					// 树中插入全局角色类型
					if (props.roleGlobal) {
						const globalRoleType = [
							{
								id: 'GLOBAL',
								parentId: '-1',
								name: '全局'
							}
						]
						treeData.value = globalRoleType.concat(data)
					}
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
			.finally(() => {
				cardLoading.value = false
			})
		searchFormState.value.size = pageSize.value
		loadData()
		if (isEmpty(recordIds.value)) {
			return
		}
		const param = {
			idList: recordIds.value
		}
		selectedTableListLoading.value = true
		roleListByIdList(param)
			.then((data) => {
				selectedData.value = data
			})
			.finally(() => {
				selectedTableListLoading.value = false
			})
	}
	// 点击删除
	const deleteObj = (obj) => {
		// 删除显示的
		remove(dataArray.value, (item) => item.id === obj.id)
		// 删除缓存的
		remove(recordIds.value, (item) => item === obj.id)
		const value = []
		const showData = []
		dataArray.value.forEach((item) => {
			const obj = {
				id: item.id,
				name: item.name
			}
			value.push(item.id)
			// 拷贝一份obj数据
			const objClone = cloneDeep(obj)
			showData.push(objClone)
		})
		dataArray.value = showData
		// 判断是否做数据的转换为工作流需要的
		const resultData = outDataConverter(value)
		emit('update:value', resultData)
		emit('onBack', resultData)
	}
	// 查询主表格数据
	const loadData = () => {
		// 如果不是用全局的，我们每次查询加上这个条件
		if (!props.roleGlobal) {
			searchFormState.value.category = 'ORG'
		}
		pageLoading.value = true
		rolePage(searchFormState.value)
			.then((data) => {
				current.value = data.current
				total.value = data.total
				// 重置、赋值
				tableData.value = []
				tableRecordNum.value = 0
				tableData.value = data.records
				if (data.records) {
					tableRecordNum.value = data.records.length
				} else {
					tableRecordNum.value = 0
				}
			})
			.finally(() => {
				pageLoading.value = false
			})
	}
	// pageSize改变回调分页事件
	const paginationChange = (page, pageSize) => {
		searchFormState.value.current = page
		searchFormState.value.size = pageSize
		loadData()
	}
	const judge = () => {
		return !(props.radioModel && selectedData.value.length > 0)
	}
	// 添加记录
	const addRecord = (record) => {
		if (!judge()) {
			message.warning('只可选择一条')
			return
		}
		const selectedRecord = selectedData.value.filter((item) => item.id === record.id)
		if (selectedRecord.length === 0) {
			selectedData.value.push(record)
		} else {
			message.warning('该记录已存在')
		}
	}
	// 添加全部
	const addAllPageRecord = () => {
		let newArray = selectedData.value.concat(tableData.value)
		let list = []
		for (let item1 of newArray) {
			let flag = true
			for (let item2 of list) {
				if (item1.id === item2.id) {
					flag = false
				}
			}
			if (flag) {
				list.push(item1)
			}
		}
		selectedData.value = list
	}
	// 删减记录
	const delRecord = (record) => {
		remove(selectedData.value, (item) => item.id === record.id)
	}
	// 删减记录
	const delAllRecord = () => {
		selectedData.value = []
	}
	// 点击树查询
	const treeSelect = (selectedKeys) => {
		searchFormState.value.current = 0
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
		loadData()
	}
	const dataArray = ref([])
	// 确定
	const handleOk = () => {
		dataArray.value = []
		const value = []
		const showData = []
		selectedData.value.forEach((item) => {
			const obj = {
				id: item.id,
				name: item.name
			}
			value.push(item.id)
			// 拷贝一份obj数据
			const objClone = cloneDeep(obj)
			showData.push(objClone)
		})
		dataArray.value = showData
		// 判断是否做数据的转换为工作流需要的
		const resultData = outDataConverter(value)
		emit('update:value', resultData)
		emit('onBack', resultData)
		handleClose()
	}
	// 重置
	const reset = () => {
		delete searchFormState.value.searchKey
		loadData()
	}
	const handleClose = () => {
		searchFormState.value = {}
		tableRecordNum.value = 0
		tableData.value = []
		current.value = 0
		pageSize.value = 20
		total.value = 0
		selectedData.value = []
		// dataArray.value = []
		visible.value = false
	}
	// 数据进入后转换
	const goDataConverter = (data) => {
		if (props.dataIsConverterFlw) {
			const resultData = []
			// 处理对象
			if (!isEmpty(data.value)) {
				const values = data.value.split(',')
				if (values.length > 0) {
					values.forEach((id) => {
						resultData.push(id)
					})
				} else {
					resultData.push(data.value)
				}
			} else {
				// 处理数组
				if (!isEmpty(data) && !isEmpty(data[0]) && !isEmpty(data[0].value)) {
					const values = data[0].value.split(',')
					for (let i = 0; i < values.length; i++) {
						resultData.push(values[i])
					}
				}
			}
			return resultData
		} else {
			if (getValueType() !== 'string') {
				return data
			}
			if (data.length > 1) {
				const resultData = []
				data.split(',').forEach((id) => {
					resultData.push(id)
				})
				return resultData
			} else {
				return data
			}
		}
	}
	// 数据出口转换器
	const outDataConverter = (data) => {
		if (props.dataIsConverterFlw) {
			data = dataArray.value
			const obj = {}
			let label = ''
			let value = ''
			for (let i = 0; i < data.length; i++) {
				if (data.length === i + 1) {
					label = label + data[i].name
					value = value + data[i].id
				} else {
					label = label + data[i].name + ','
					value = value + data[i].id + ','
				}
			}
			obj.key = 'ROLE'
			obj.label = label
			obj.value = value
			obj.extJson = ''
			return obj
		} else {
			if (getValueType() !== 'string') {
				return data
			}
			let resultData = ''
			data.forEach((id) => {
				resultData = resultData + ',' + id
			})
			resultData = resultData.substring(1, resultData.length)
			return resultData
		}
	}
	// 获取数据类型
	const getValueType = () => {
		if (props.dataType) {
			return props.dataType
		} else {
			if (props.radioModel) {
				return 'string'
			}
			return typeof typeof props.value
		}
	}
	const getDataNameById = (ids) => {
		if (isEmpty(dataArray.value) && !isEmpty(ids)) {
			const param = {
				idList: recordIds.value
			}
			// 这里必须转为数组类型的
			roleListByIdList(param).then((data) => {
				dataArray.value = data
			})
		}
	}
	watch(
		() => props.value,
		(newValue) => {
			if (!isEmpty(props.value)) {
				const ids = goDataConverter(newValue)
				recordIds.value = ids
				getDataNameById(ids)
			}
		},
		{
			immediate: true // 立即执行
		}
	)
	defineExpose({
		showModel
	})
</script>
<style lang="less" scoped>
	.xn-mr-5 {
		margin-right: 5px;
	}
	.xn-mr-10 {
		margin-right: 10px;
	}
	.selectorTreeDiv {
		max-height: 500px;
		overflow: auto;
	}
	.ant-form-item {
		margin-bottom: 0 !important;
	}
	.selector-table {
		overflow: auto;
		max-height: 450px;
	}
</style>
