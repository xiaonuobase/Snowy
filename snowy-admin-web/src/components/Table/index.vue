<template>
	<div className="table-wrapper">
		<div className="s-table-tool">
			<div className="s-table-tool-left">
				<!-- 插槽操作按钮 -->
				<slot name="operator"></slot>
			</div>
			<!--  斑马纹 -->
			<div className="layout-items-center s-table-tool-right">
				<div className="layout-items-center ml-4" v-show="props.toolConfig.striped">
					<a-checkbox :checked="data.localSettings.rowClassNameSwitch" @change="changeRowClass"> 斑马纹 </a-checkbox>
				</div>
				<span v-for="item in tool">
					<!-- 刷新 -->
					<a-tooltip
						v-if="item.name === 'refresh' && props.toolConfig.refresh"
						:title="item.title"
						class="s-tool-item"
						@click="refresh"
					>
						<component class="icons" :is="item.icon"></component>
					</a-tooltip>

					<!-- 列展示 -->
					<a-popover
						v-if="item.isPopover && item.name === 'columnSetting' && props.toolConfig.columnSetting"
						trigger="click"
						placement="topLeft"
						overlayClassName="s-table-column-settings"
						arrow-point-at-center
					>
						<template #content>
							<columnSetting :columns="props.columns" @columnChange="columnChange" />
						</template>
						<a-tooltip :title="item.title" class="s-tool-item">
							<component class="icons" :is="item.icon"></component>
						</a-tooltip>
					</a-popover>
					<!-- 密度 -->
					<a-dropdown trigger="click" v-if="item.isDropdown && item.name == 'height' && props.toolConfig.height">
						<template #overlay>
							<a-menu selectable :selectedKeys="[data.customSize]" @click="changeHeight">
								<a-menu-item key="default">默认</a-menu-item>
								<a-menu-item key="middle">中等</a-menu-item>
								<a-menu-item key="small">紧凑</a-menu-item>
							</a-menu>
						</template>
						<a-tooltip :title="item.title" class="s-tool-item">
							<component class="icons" :is="item.icon"></component>
						</a-tooltip>
					</a-dropdown>
				</span>
			</div>
		</div>

		<!-- 统计列数据 -->
		<a-alert showIcon class="mb-4" v-if="props.alert">
			<template #message>
				<div>
					<span className="mr-3">
						已选择:{{ ' ' }}
						<a className="font-6">
							{{
								props.rowSelection && props.rowSelection.selectedRowKeys ? props.rowSelection.selectedRowKeys.length : 0
							}}
						</a>
					</span>
					<span className="mr-3" v-for="item in data.needTotalList">
						{{ item.title }} 总计{{ ' ' }}
						<a className="font-6">{{ !item.customRender ? item.total : item.customRender(item.total) }}</a>
					</span>
					<a
						v-show="
							props.rowSelection && props.rowSelection.selectedRowKeys && props.rowSelection.selectedRowKeys.length > 0
						"
						className="ml-6"
						@click="
							rowClear(
								typeof props.alert === 'boolean' && props.alert
									? clearSelected()
									: props.alert.clear && typeof props.alert.clear === 'function'
									  ? props.alert.clear()
									  : null
							)
						"
					>
						{{ ' ' }}
						清空{{ ' ' }}
					</a>
				</div>
			</template>
		</a-alert>

		<!-- 表格 -->
		<a-table
			v-bind="{ ...renderTableProps, ...data.localSettings }"
			@change="loadData"
			:row-key="(record) => record.id"
			@expand="
				(expanded, record) => {
					emit('expand', expanded, record)
				}
			"
		>
			<template #[item]="scope" v-for="item in renderSlots">
				<slot :name="item" :scope="scope" v-bind="scope || {}"></slot>
			</template>
		</a-table>
	</div>
</template>
<script setup>
	import './index.less'
	import { tableProps } from 'ant-design-vue/es/table/Table.js'
	import columnSetting from './columnSetting.vue'
	import './index.less'

	import { useSlots } from 'vue'
	import { useRoute } from 'vue-router'
	const slots = useSlots()
	const route = useRoute()
	const emit = defineEmits(['expand'])
	const renderSlots = Object.keys(slots)

	const props = defineProps(
		Object.assign({}, tableProps(), {
			rowKey: {
				type: [String, Function],
				default: 'key'
			},
			data: {
				type: Function,
				required: true
			},
			pageNum: {
				type: Number,
				default: 1
			},
			size: {
				type: Number,
				default: 10
			},
			showSizeChanger: {
				type: Boolean,
				default: true
			},
			compSize: {
				type: String,
				default: 'middle'
			},
			alert: {
				type: [Object, Boolean],
				default: null
			},
			rowSelection: {
				type: Object,
				default: null
			},
			lineSelection: {
				type: Boolean,
				default: false
			},
			customRow: {
				type: Function,
				default: undefined
			},
			showPagination: {
				type: [String, Boolean],
				default: 'auto'
			},
			defaultPageSize: {
				type: Number,
				default: 10
			},
			pageSizeOptions: {
				type: Array,
				default: () => ['10', '20', '50', '100']
			},
			extraTool: {
				type: Array,
				default: () => []
			},
			// 配置工具栏
			toolConfig: {
				type: Object,
				default: () => ({
					refresh: false,
					height: false,
					columnSetting: false,
					striped: false
				})
			}
		})
	)

	const data = reactive({
		needTotalList: [],
		localLoading: false,
		localDataSource: [],
		localPagination: Object.assign({}, props.pagination),
		isFullscreen: false,
		customSize: props.compSize,
		columnsSetting: [],
		localSettings: {
			rowClassName: props.rowClassName,
			rowClassNameSwitch: Boolean(props.rowClassName)
		}
	})

	watch(
		() => props.pageNum,
		(newVal) => {
			Object.assign(data.localPagination, {
				current: newVal
			})
		}
	)
	watch(
		() => props.size,
		(newVal) => {
			Object.assign(data.localPagination, {
				size: newVal
			})
		}
	)
	watch(
		() => props.showSizeChanger,
		(newVal) => {
			Object.assign(data.localPagination, {
				showSizeChanger: newVal
			})
		}
	)
	watch(
		() => props.columns,
		(newVal) => {
			data.columnsSetting = newVal
		}
	)

	// 表格props
	const renderTableProps = ref([])
	// 右上角工具数组
	const tool = [
		{
			name: 'refresh',
			icon: 'sync-outlined',
			title: '刷新'
		},
		{
			name: 'height',
			icon: 'column-height-outlined',
			title: '密度',
			isDropdown: true
		},
		{
			name: 'columnSetting',
			icon: 'setting-outlined',
			title: '列设置',
			isPopover: true,
			visible: false
		}
	]

	// 刷新
	const refresh = (bool = false) => {
		bool &&
			(data.localPagination = Object.assign(
				{},
				{
					current: 1,
					pageSize: data.localPagination.pageSize
				}
			))
		loadData()
		getTableProps()
	}
	// 斑马纹勾选
	const changeRowClass = (value) => {
		const val = value.target.checked
		data.localSettings.rowClassNameSwitch = val
		const evenClass = val ? (_record, index) => (index % 2 === 1 ? 'table-striped' : null) : props.rowClassName
		data.localSettings.rowClassName = evenClass
	}
	// 密度切换
	const changeHeight = (v) => {
		data.customSize = v.key
		getTableProps()
	}
	// 列设置
	const columnChange = (val) => {
		data.columnsSetting = val
		getTableProps()
	}
	// 列清空
	const rowClear = (callback) => {
		callback
		clearSelected()
	}
	// 初始化
	const init = () => {
		const { current } = route.params
		const localPageNum = (current && parseInt(current)) || props.pageNum
		data.localPagination =
			(['auto', true].includes(props.showPagination) &&
				Object.assign({}, data.localPagination, {
					current: localPageNum,
					pageSize: props.size, //props.compSize, size// 改动
					showSizeChanger: props.showSizeChanger,
					defaultPageSize: props.defaultPageSize,
					pageSizeOptions: props.pageSizeOptions,
					showTotal: (total, range) => {
						return `${range[0]}-${range[1]} 共 ${total} 条 `
					}
				})) ||
			false
		data.needTotalList = initTotalList(props.columns)
		data.columnsSetting = props.columns
		loadData()
	}
	const initTotalList = (columns) => {
		const totalList = []
		columns &&
			columns instanceof Array &&
			columns.forEach((column) => {
				if (column.needTotal) {
					totalList.push({
						...column,
						total: 0
					})
				}
			})
		return totalList
	}
	// 加载数据方法 分页选项器 过滤条件 排序条件
	const loadData = (pagination, filters, sorter) => {
		data.localLoading = true
		const parameter = Object.assign(
			{
				current:
					(pagination && pagination.current) || (props.showPagination && data.localPagination.current) || props.pageNum,
				// 此处后端使用size作为分页参数
				size:
					(pagination && pagination.pageSize) ||
					(props.showPagination && data.localPagination.pageSize) ||
					props.pageSize ||
					data.localPagination.pageSize
			},
			(sorter &&
				sorter.field && {
					sortField: sorter.field
				}) ||
				{},
			(sorter &&
				sorter.order && {
					sortOrder: sorter.order
				}) ||
				{},
			{
				...filters
			}
		)
		const result = props.data(parameter)
		if ((typeof result === 'object' || typeof result === 'function') && typeof result.then === 'function') {
			result.then((r) => {
				if (r == null) {
					data.localLoading = false
					return
				}
				data.localPagination =
					(props.showPagination &&
						Object.assign({}, data.localPagination, {
							current: r.current, // pageNo, // 返回结果中的当前分页数
							total: r.total, // totalRows, // 返回结果中的总记录数
							showSizeChanger: props.showSizeChanger,
							pageSizeOptions: props.pageSizeOptions,
							showTotal: (total, range) => {
								return `${range[0]}-${range[1]} 共 ${total} 条 `
							},
							pageSize: (pagination && pagination.pageSize) || data.localPagination.pageSize
						})) ||
					false

				// 后端数据records为null保存修复
				if (r.records == null) {
					r.records = []
				}

				// 为防止删除数据后导致页面当前页面数据长度为 0 ,自动翻页到上一页
				if (r.records.length === 0 && props.showPagination && data.localPagination.current > 1) {
					data.localPagination.current--
					loadData()
					return
				}
				// 当情况满足时，表示数据不满足分页大小，关闭 table 分页功能
				try {
					/*
					if ((['auto', true].includes(props.showPagination) && r.total <= (r.pages * data.localPagination.size))) {
						data.localPagination.hideOnSinglePage = true
					}
					*/
					if (!props.showPagination) {
						data.localPagination.hideOnSinglePage = true
					}
				} catch (e) {
					data.localPagination = false
				}
				// 返回结果中的数组数据
				if (props.showPagination === false) {
					// 既然配置了不分页，那么我们这里接收到肯定是数组
					data.localDataSource = []
					if (r instanceof Array) {
						data.localDataSource = r
					}
				} else {
					data.localDataSource = r.records
				}
				data.localLoading = false
				getTableProps()
			})
		}
	}
	// 加载props
	const getTableProps = () => {
		let renderProps = {}
		const localKeys = Object.keys(data)
		Object.keys(tableProps()).forEach((k) => {
			const localKey = `local${k.substring(0, 1).toUpperCase()}${k.substring(1)}`
			if (localKeys.includes(localKey)) {
				renderProps[k] = data[localKey]
				return renderProps[k]
			}
			if (k === 'rowSelection') {
				if (props.rowSelection) {
					// 如果需要使用alert，则重新绑定 rowSelection 事件
					renderProps[k] = {
						...props.rowSelection,
						onChange: (selectedRowKeys, selectedRows) => {
							updateSelect(selectedRowKeys, selectedRows)
							typeof props[k].onChange !== 'undefined' && props[k].onChange(selectedRowKeys, selectedRows)
						}
					}
					return renderProps[k]
				} else if (!props.rowSelection) {
					// 如果没打算开启 rowSelection 则清空默认的选择项
					renderProps[k] = null
					return renderProps[k]
				}
			}
			if (k === 'customRow') {
				if (props.lineSelection && props.rowSelection) {
					// 如果需要 整行选择，则重新绑定 customRow 事件
					renderProps[k] = (record, index) => {
						return {
							...(typeof props.customRow !== 'undefined' && props.customRow(record, index)),
							onClick: (event) => {
								// 若存在原onClick则执行
								typeof data[k] !== 'undefined' &&
									typeof data[k](record, index).onClick !== 'undefined' &&
									data[k](record, index).onClick(event)
								// 记录为disabled则直接返回，默认为不可选
								const rowDisabled =
									typeof props.rowSelection.getCheckboxProps !== 'undefined' &&
									props.rowSelection.getCheckboxProps(record).disabled
								if (rowDisabled) return
								// 过滤自定义按钮的非空白区域
								const classList = event.target?.classList
								if (!classList.contains('ant-table-cell')) return
								const key = (typeof props.rowKey === 'function' && props.rowKey(record)) || props.rowKey || index
								let selectedRows = props.rowSelection.selectedRows
								let selectedRowKeys = props.rowSelection.selectedRowKeys
								const rowType = props.rowSelection?.type || 'checkbox'

								if (rowType === 'radio' || props.rowSelection.selectedRowKeys === undefined) {
									selectedRowKeys = [key]
									selectedRows = [record]
								} else if (!props.rowSelection.selectedRowKeys?.includes(key)) {
									selectedRowKeys.push(key)
									selectedRows.push(record)
								} else {
									const index = props.rowSelection.selectedRowKeys?.findIndex((itemKey) => itemKey === key)
									selectedRows.splice(index, 1)
									selectedRowKeys.splice(index, 1)
								}
								updateSelect(selectedRowKeys, selectedRows)
							}
						}
					}
					return renderProps[k]
				}
			}
			data[k] && (renderProps[k] = data[k])
			// 此处配置表格大小与要显示的列
			renderProps = {
				...renderProps,
				bordered: props.bordered,
				size: data.customSize, // 注意这个size是a-table组件需要的，这里不能跟别的地方成为compSize
				columns: data.columnsSetting.filter((value) => value.checked === undefined || value.checked)
			}
			return renderProps[k]
		})
		renderTableProps.value = renderProps
	}
	// 用于更新已选中的列表数据 total 统计
	const updateSelect = (selectedRowKeys, selectedRows) => {
		if (props.rowSelection) {
			// eslint-disable-next-line vue/no-mutating-props
			props.rowSelection.selectedRows = selectedRows
			// eslint-disable-next-line vue/no-mutating-props
			props.rowSelection.selectedRowKeys = selectedRowKeys
			getTableProps()
		}
		const list = data.needTotalList
		data.needTotalList = list.map((item) => {
			return {
				...item,
				total: selectedRows.reduce((sum, val) => {
					const total = sum + parseInt(get(val, item.dataIndex))
					return isNaN(total) ? 0 : total
				}, 0)
			}
		})
	}
	// 清空 table 已选中项
	const clearSelected = () => {
		if (props.rowSelection) {
			props.rowSelection.onChange([], [])
			updateSelect([], [])
			getTableProps()
		}
	}
	// 刷新并清空已选
	const clearRefreshSelected = (bool = false) => {
		refresh(bool)
		clearSelected()
	}
	// 暴露子组件的方法
	defineExpose({
		clearRefreshSelected,
		refresh,
		clearSelected
	})
	onMounted(() => {
		init()
	})
</script>
