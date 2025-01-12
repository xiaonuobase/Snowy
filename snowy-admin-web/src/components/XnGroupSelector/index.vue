<template>
	<div v-if="props.show">
		<a-tag v-for="data in dataArray" closable @close="deleteObj(data)" :key="data.id" class="mb-1">
			{{ data.name }}
		</a-tag>
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
	<a-modal
		v-model:open="visible"
		title="用户组选择"
		:width="700"
		:mask-closable="false"
		:destroy-on-close="true"
		@ok="handleOk"
		@cancel="handleClose"
	>
		<a-row :gutter="10">
			<a-col :span="14">
				<div class="table-operator xn-mb10">
					<a-form ref="searchFormRef" name="advanced_search" class="ant-advanced-search-form" :model="searchFormState">
						<a-row :gutter="24">
							<a-col :span="12">
								<a-form-item name="searchKey">
									<a-input v-model:value="searchFormState.searchKey" placeholder="请输入名称" />
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
					<a-table size="small" :columns="commons" bordered :data-source="tableData" :loading="pageLoading">
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
						</template>
					</a-table>
				</div>
			</a-col>
			<a-col :span="10">
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

<script setup name="groupSelector">
	import { message } from 'ant-design-vue'
	import { remove, isEmpty, cloneDeep } from 'lodash-es'
	import { useSlots } from 'vue'
	import userCenterApi from '@/api/sys/userCenterApi'
	const dataArray = ref([])
	const visible = ref(false)
	const tableData = ref([])
	const searchFormState = ref({})
	const searchFormRef = ref()
	const selectedTable = ref()
	const selectedData = ref([])
	const recordIds = ref([])
	const selectedTableListLoading = ref(false)
	const tableRecordNum = ref()
	const pageLoading = ref(false)
	// 分页相关
	const current = ref(0) // 当前页数
	const pageSize = ref(20) // 每页条数
	const total = ref(0) // 数据总数
	const slots = useSlots()
	const hasContent = (slotName) => {
		return !!(slots[slotName] && slots[slotName]().length > 0)
	}
	const props = defineProps({
		radioModel: {
			type: Boolean,
			default: () => false
		},
		dataIsConverterFlw: {
			type: Boolean,
			default: () => false
		},
		selectorConverterKey: {
			type: String,
			default: () => 'USER_GROUP'
		},
		dataPageApi: {
			type: Function
		},
		dataDetailByIdListApi: {
			type: Function
		},
		// eslint-disable-next-line vue/require-prop-types
		value: {
			default: () => []
		},
		dataType: {
			type: String,
			default: () => 'array' // string、array
		},
		show: {
			type: Boolean,
			default: () => true
		},
		addShow: {
			type: Boolean,
			default: () => true
		},
		processModelId: {
			type: String,
			default: () => undefined
		}
	})
	const emit = defineEmits(['update:value', 'onBack', 'onChange'])
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
	// 打开弹框
	const showModel = (ids = []) => {
		const data = goDataConverter(ids)
		recordIds.value = data
		getDataNameById(data)
		openModal()
	}
	// 点击删除
	const deleteObj = (obj) => {
		// 删除显示的
		remove(dataArray.value, (item) => item.id === obj.id)
		// 删除缓存的
		remove(selectedData.value, (item) => item.id === obj.id)
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
	// 获取列表的api
	const groupPage = (param) => {
		if (typeof props.dataPageApi === 'function') {
			return props.dataPageApi(param)
		} else {
			message.warning('未配置选择器API')
			return Promise.resolve([])
		}
	}
	// 获取选中列表的api
	const groupListByIdList = (param) => {
		if (typeof props.dataDetailByIdListApi === 'function') {
			return props.dataDetailByIdListApi(param)
		} else {
			return userCenterApi.userCenterGetGroupListByIdList(param).then((data) => {
				return Promise.resolve(data)
			})
		}
	}
	const openModal = () => {
		if (typeof props.dataPageApi !== 'function') {
			message.warning('未配置选择器需要的dataPageApi接口')
			return
		}
		visible.value = true
		searchFormState.value.size = pageSize.value
		loadData()
		if (isEmpty(recordIds.value)) {
			return
		}
		const param = {
			idList: recordIds.value
		}
		selectedTableListLoading.value = true
		groupListByIdList(param)
			.then((data) => {
				selectedData.value = data
			})
			.finally(() => {
				selectedTableListLoading.value = false
			})
	}
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
	const handleClose = () => {
		searchFormState.value = {}
		tableRecordNum.value = 0
		tableData.value = []
		current.value = 0
		pageSize.value = 20
		total.value = 0
		visible.value = false
	}
	const loadData = () => {
		pageLoading.value = true
		groupPage(searchFormState.value)
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
	const reset = () => {
		delete searchFormState.value.searchKey
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
			obj.key = props.selectorConverterKey
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
			groupListByIdList(param).then((data) => {
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
			} else {
				dataArray.value = []
				selectedData.value = []
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
