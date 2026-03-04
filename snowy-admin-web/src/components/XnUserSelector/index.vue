<template>
	<!-- 这是引入后展示的样式 -->
	<a-flex wrap="wrap" gap="small" v-if="props.userShow">
		<div
			class="user-container"
			v-for="(user, index) in userObj"
			:key="user.id"
			@mouseover="onMouseEnter(index)"
			@mouseleave="onMouseLeave(index)"
		>
			<span class="user-delete">
				<CloseCircleFilled
					:class="index === deleteShow ? 'show-delete-icon' : ''"
					class="delete-icon"
					@click="deleteUser(user)"
				/>
				<a-avatar :src="user.avatar" />
			</span>
			<span class="user-name">{{ user.name }}</span>
		</div>
		<a-button shape="circle" @click="openModal" v-if="(props.radioModel ? userObj.length !== 1 : true) && addShow">
			<PlusOutlined />
		</a-button>
		<slot name="button"></slot>
	</a-flex>

	<!-- 以下是弹窗内容 -->
	<a-modal
		v-model:open="visible"
		title="用户选择"
		:width="1000"
		:mask-closable="false"
		:destroy-on-close="true"
		@ok="handleOk"
		@cancel="handleClose"
	>
		<a-row :gutter="10">
			<a-col :xs="0" :sm="0" :md="0" :lg="7" :xl="7">
				<a-card size="small" class="selectorTreeDiv">
					<a-spin :spinning="cardLoading">
						<a-tree
							v-if="treeData"
							v-model:expandedKeys="defaultExpandedKeys"
							:show-line="{ showLeafIcon: false }"
							:tree-data="treeData"
							:field-names="treeFieldNames"
							:load-data="onLoadData"
							:height="treeHeight"
							@select="treeSelect"
						>
						</a-tree>
					</a-spin>
				</a-card>
			</a-col>
			<a-col :xs="24" :sm="24" :md="24" :lg="11" :xl="11">
				<div class="table-operator xn-mb10">
					<a-form ref="searchFormRef" :model="searchFormState">
						<a-row :gutter="10">
							<a-col :xs="24" :sm="8" :md="8" :lg="0" :xl="0">
								<a-form-item label="组织：" name="orgId">
									<a-tree-select
										v-model:value="searchFormState.orgId"
										class="xn-wd"
										:dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
										placeholder="请选择组织"
										allow-clear
										:tree-data="treeData"
										:field-names="treeSelectFieldNames"
										selectable="false"
										tree-line
									/>
								</a-form-item>
							</a-col>
							<a-col :xs="24" :sm="8" :md="8" :lg="8" :xl="8">
								<a-form-item name="searchKey">
									<a-input v-model:value="searchFormState.searchKey" placeholder="请输入用户名" />
								</a-form-item>
							</a-col>
							<a-col :xs="24" :sm="8" :md="8" :lg="8" :xl="8">
								<a-form-item>
									<a-space>
										<a-button type="primary" class="xn-mr-10" @click="loadData()"> 查询 </a-button>
										<a-button @click="reset()"> 重置 </a-button>
									</a-space>
								</a-form-item>
							</a-col>
						</a-row>
					</a-form>
				</div>
				<div class="user-table">
					<a-table
						ref="tableRef"
						size="small"
						:columns="commons"
						:data-source="tableData"
						:expand-row-by-click="true"
						:loading="pageLoading"
						bordered
						:pagination="false"
						:scroll="{ x: 'max-content' }"
					>
						<template #title>
							<span>待选择列表 {{ tableRecordNum }} 条</span>
							<div v-if="!radioModel" class="xn-fdr">
								<a-button type="dashed" size="small" @click="addAllPageRecord">添加当前数据</a-button>
							</div>
						</template>
						<template #bodyCell="{ column, record }">
							<template v-if="column.dataIndex === 'avatar'">
								<a-avatar :src="record.avatar" style="margin-bottom: -5px; margin-top: -5px" />
							</template>
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
			<a-col :xs="24" :sm="24" :md="24" :lg="6" :xl="6">
				<div class="user-table">
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

<script setup name="userSelector">
	import { message } from 'ant-design-vue'
	import { remove, isEmpty, cloneDeep } from 'lodash-es'
	import userCenterApi from '@/api/sys/userCenterApi'
	import { triggerRef } from 'vue'
	// 弹窗是否打开
	const visible = ref(false)
	const deleteShow = ref('')
	// 主表格common
	const commons = [
		{
			title: '操作',
			dataIndex: 'action',
			align: 'center'
		},
		{
			title: '头像',
			dataIndex: 'avatar'
		},
		{
			title: '用户名',
			dataIndex: 'name',
			ellipsis: true
		},
		{
			title: '账号',
			dataIndex: 'account'
		}
	]
	// 选中表格的表格common
	const selectedCommons = [
		{
			title: '操作',
			dataIndex: 'action',
			align: 'center'
		},
		{
			title: '用户名',
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
		userPageApi: {
			type: Function
		},
		userListByIdListApi: {
			type: Function
		},
		value: {
			default: () => ''
		},
		dataType: {
			type: String,
			default: () => 'string'
		},
		userShow: {
			type: Boolean,
			default: () => true
		},
		addShow: {
			type: Boolean,
			default: () => true
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
	// 替换treeNode 中 title,key,children
	const treeFieldNames = { children: 'children', title: 'name', key: 'id' }
	const treeSelectFieldNames = { children: 'children', label: 'name', value: 'id' }
	// 获取机构树数据
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
	const treeHeight = ref(400)
	// 获取选中列表的api
	const userListByIdList = (param) => {
		if (typeof props.userListByIdListApi === 'function') {
			return props.userListByIdListApi(param)
		} else {
			return userCenterApi.userCenterGetUserListByIdList(param).then((data) => {
				return Promise.resolve(data)
			})
		}
	}
	// 打开弹框
	const showUserPlusModal = (ids = []) => {
		const data = goDataConverter(ids)
		recordIds.value = data
		getUserAvatarById(data)
		openModal()
	}
	const onMouseEnter = (index) => {
		deleteShow.value = index
	}
	const onMouseLeave = (index) => {
		deleteShow.value = ''
	}
	// 懒加载子节点
	const onLoadData = (treeNode) => {
		return new Promise((resolve) => {
			if (typeof props.orgTreeApi !== 'function' || treeNode.dataRef.children || treeNode.dataRef.isLeaf) {
				resolve()
				return
			}
			props
				.orgTreeApi({
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

	// 打开弹框
	const openModal = () => {
		if (typeof props.orgTreeApi !== 'function' || typeof props.userPageApi !== 'function') {
			message.warning('未配置用户选择器API')
			return
		}
		visible.value = true
		// 动态计算树高度，适配不同屏幕
		treeHeight.value = Math.min(Math.max(window.innerHeight - 350, 250), 460)
		// 获取机构树
		props
			.orgTreeApi()
			.then((data) => {
				if (!isEmpty(data)) {
					treeData.value = data.map((item) => {
						return {
							...item,
							isLeaf: item.isLeaf === undefined ? false : item.isLeaf
						}
					})
					// 只有一个根节点时才自动展开
					if (treeData.value.length === 1) {
						defaultExpandedKeys.value.push(treeData.value[0].id)
					}
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
		userListByIdList(param)
			.then((data) => {
				selectedData.value = data
				userObj.value = data
			})
			.finally(() => {
				selectedTableListLoading.value = false
			})
	}
	// 点击头像删除用户
	const deleteUser = (user) => {
		// 删除显示的
		remove(userObj.value, (item) => item.id === user.id)
		// 删除缓存的
		remove(recordIds.value, (item) => item === user.id)
		const value = []
		const showUser = []
		userObj.value.forEach((item) => {
			const obj = {
				id: item.id,
				name: item.name
			}
			value.push(item.id)
			// 拷贝一份obj数据
			const objClone = cloneDeep(obj)
			objClone.avatar = item.avatar
			showUser.push(objClone)
		})
		userObj.value = showUser
		// 判断是否做数据的转换为工作流需要的
		const resultData = outDataConverter(value)
		emit('update:value', resultData)
		emit('onBack', resultData)
	}
	// 查询主表格数据
	const loadData = () => {
		pageLoading.value = true
		props
			.userPageApi(searchFormState.value)
			.then((data) => {
				current.value = data.current
				// pageSize.value = data.size
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
			searchFormState.value.orgId = selectedKeys.toString()
		} else {
			delete searchFormState.value.orgId
		}
		loadData()
	}
	const userObj = ref([])
	// 确定
	const handleOk = () => {
		userObj.value = []
		const value = []
		const showUser = []
		selectedData.value.forEach((item) => {
			const obj = {
				id: item.id,
				name: item.name
			}
			value.push(item.id)
			// 拷贝一份obj数据
			const objClone = cloneDeep(obj)
			objClone.avatar = item.avatar
			showUser.push(objClone)
		})
		userObj.value = showUser
		// 判断是否做数据的转换为工作流需要的
		const resultData = outDataConverter(value)
		emit('update:value', resultData)
		emit('onBack', resultData)
		handleClose()
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
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
		// userObj.value = []
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
			data = userObj.value
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
			obj.key = 'USER'
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
	const getUserAvatarById = (ids) => {
		if (isEmpty(userObj.value) && !isEmpty(ids)) {
			const param = {
				idList: recordIds.value
			}
			// 这里必须转为数组类型的
			userListByIdList(param).then((data) => {
				userObj.value = data
			})
		}
	}
	watch(
		() => props.value,
		(newValue) => {
			if (!isEmpty(props.value)) {
				const ids = goDataConverter(newValue)
				recordIds.value = ids
				getUserAvatarById(ids)
			} else {
				userObj.value = []
				selectedData.value = []
			}
		},
		{
			immediate: true // 立即执行
		}
	)
	defineExpose({
		showUserPlusModal
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
	.user-table {
		overflow: auto;
		max-height: 450px;
	}

	.user-container {
		display: flex;
		align-items: center; /* 垂直居中 */
		flex-direction: column;
		margin-right: 10px;
		text-align: center;
	}
	.user-avatar {
		width: 30px;
		border-radius: 50%; /* 设置为50%以创建圆形头像 */
	}
	.user-name {
		font-size: 12px;
		max-width: 50px;
		white-space: nowrap;
		overflow: hidden;
	}
	.user-delete {
		z-index: 99;
		color: rgba(0, 0, 0, 0.25);
		position: relative;
		display: flex;
		flex-direction: column;
	}
	.delete-icon {
		position: absolute;
		right: -2px;
		z-index: 5;
		top: -3px;
		cursor: pointer;
		visibility: hidden;
	}
	.show-delete-icon {
		visibility: visible;
	}
</style>
