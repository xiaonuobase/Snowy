<script lang="jsx">
	import { tableProps } from 'ant-design-vue/es/table/Table.js'
	import { get } from 'lodash-es'
	import draggable from 'vuedraggable-es'
	import columnSetting from './columnSetting.vue'
	import './index.less'

	export default {
		components: {
			draggable,
			columnSetting
		},
		data() {
			return {
				needTotalList: [],
				selectedRows: [],
				selectedRowKeys: [],
				localLoading: false,
				localDataSource: [],
				localPagination: Object.assign({}, this.pagination),
				isFullscreen: false,
				customSize: this.compSize,
				columnsSetting: [],
				localSettings: {
					rowClassName: this.rowClassName,
					rowClassNameSwitch: Boolean(this.rowClassName)
				}
			}
		},
		// eslint-disable-next-line vue/order-in-components
		props: Object.assign({}, tableProps(), {
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
				type: String,
				default: '10'
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
			/** @Deprecated */
			showAlertInfo: {
				type: Boolean,
				default: false
			},
			showPagination: {
				type: [String, Boolean],
				default: 'auto'
			},
			/**
			 * enable page URI mode
			 *
			 * e.g:
			 * /users/1
			 * /users/2
			 * /users/3?queryParam=test
			 * ...
			 */
			pageURI: {
				type: Boolean,
				default: false
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
		}),
		watch: {
			'localPagination.current': function (val) {
				this.pageURI &&
					this.$router.push({
						...this.$route,
						name: this.$route.name,
						params: Object.assign({}, this.$route.params, {
							current: val
						})
					})
			},
			pageNum(val) {
				Object.assign(this.localPagination, {
					current: val
				})
			},
			size(val) {
				Object.assign(this.localPagination, {
					size: val
				})
			},
			showSizeChanger(val) {
				Object.assign(this.localPagination, {
					showSizeChanger: val
				})
			},
			columns(v) {
				this.columnsSetting = v
			}
		},
		created() {
			const { current } = this.$route.params
			const localPageNum = (this.pageURI && current && parseInt(current)) || this.pageNum
			this.localPagination =
				(['auto', true].includes(this.showPagination) &&
					Object.assign({}, this.localPagination, {
						current: localPageNum,
						size: this.size, //this.compSize, size// 改动
						showSizeChanger: this.showSizeChanger,
						showTotal: (total, range) => {
							return `${range[0]}-${range[1]} 共 ${total} 条 `
						}
					})) ||
				false
			this.needTotalList = this.initTotalList(this.columns)
			this.loadData()
			this.columnsSetting = this.columns
		},
		methods: {
			/**
			 * 表格重新加载方法
			 * 如果参数为 true, 则强制刷新到第一页
			 * @param bool Boolean
			 */
			refresh(bool = false) {
				bool && (this.localPagination = Object.assign({}, {
					current: 1, size: this.size
				}))
				this.loadData()
			},
			/**
			 * 加载数据方法
			 * @param {Object} pagination 分页选项器
			 * @param {Object} filters 过滤条件
			 * @param {Object} sorter 排序条件
			 */
			loadData(pagination, filters, sorter) {
				this.localLoading = true
				const parameter = Object.assign({
					current: (pagination && pagination.current) ||
							this.showPagination && this.localPagination.current || this.pageNum,
					size: (pagination && pagination.pageSize) ||
							this.showPagination && this.localPagination.pageSize || this.pageSize
					},
					(sorter && sorter.field && {
						sortField: sorter.field
					}) || {},
					(sorter && sorter.order && {
						sortOrder: sorter.order
					}) || {}, {
						...filters
					}
				)
				const result = this.data(parameter)
				// eslint-disable-next-line
        		if (
					(typeof result === 'object' || typeof result === 'function') &&
					typeof result.then === 'function'
				) {
					result.then((r) => {
						if (r == null) {
							this.localLoading = false
							return
						}
						this.localPagination =
							(this.showPagination &&
								Object.assign({}, this.localPagination, {
									current: r.current, // pageNo, // 返回结果中的当前分页数
									total: r.total, // totalRows, // 返回结果中的总记录数
									showSizeChanger: this.showSizeChanger,
									showTotal: (total, range) => {
										return `${range[0]}-${range[1]} 共 ${total} 条 `
									},
									size: (pagination && pagination.size) || this.localPagination.size
								})) ||
							false
						// 后端数据records为null保存修复
						if (r.records == null) {
							r.records = []
						}
						// 为防止删除数据后导致页面当前页面数据长度为 0 ,自动翻页到上一页
						if (r.records.length === 0 && this.showPagination && this.localPagination.current > 1) {
							this.localPagination.current--
							this.loadData()
							return
						}
						// 当情况满足时，表示数据不满足分页大小，关闭 table 分页功能
						try {
							/*
							if ((['auto', true].includes(this.showPagination) && r.total <= (r.pages * this.localPagination.size))) {
								this.localPagination.hideOnSinglePage = true
							}
							*/
							if (!this.showPagination) {
								this.localPagination.hideOnSinglePage = true
							}
						} catch (e) {
							this.localPagination = false
						}
						// 返回结果中的数组数据
						if (this.showPagination === false) {
							// 既然配置了不分页，那么我们这里接收到肯定是数组
							this.localDataSource = []
							if (r instanceof Array) {
								this.localDataSource = r
							}
						} else {
							this.localDataSource = r.records
						}
						this.localLoading = false
					})
				}
			},
			initTotalList(columns) {
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
			},
			/**
			 * 用于更新已选中的列表数据 total 统计
			 * @param selectedRowKeys
			 * @param selectedRows
			 */
			updateSelect(selectedRowKeys, selectedRows) {
				this.selectedRows = selectedRows
				this.selectedRowKeys = selectedRowKeys
				const list = this.needTotalList
				this.needTotalList = list.map((item) => {
					return {
						...item,
						total: selectedRows.reduce((sum, val) => {
							const total = sum + parseInt(get(val, item.dataIndex))
							return isNaN(total) ? 0 : total
						}, 0)
					}
				})
			},
			/**
			 * 清空 table 已选中项
			 */
			clearSelected() {
				if (this.rowSelection) {
					this.rowSelection.onChange([], [])
					this.updateSelect([], [])
				}
			},
			/**
			 * 刷新并清空已选
			 */
			clearRefreshSelected(bool = false) {
				this.refresh(bool)
				this.clearSelected()
			},
			/**
			 * 处理交给 table 使用者去处理 clear 事件时，内部选中统计同时调用
			 * @param callback
			 * @returns {*}
			 */
			renderClear(callback) {
				if (this.selectedRowKeys.length <= 0) return null
				return (
					<a
						className="ml-6"
						onClick={() => {
							callback()
							this.clearSelected()
						}}>
						{' '}
						清空{' '}
					</a>
				)
			},
			renderAlert() {
				// 绘制统计列数据
				// eslint-disable-next-line no-unused-vars
				const needTotalItems = this.needTotalList.map((item) => {
					return (
						<span className="mr-3">
							{item.title} 总计 {' '}
							<a className="font-6">{!item.customRender ? item.total : item.customRender(item.total)}</a>
						</span>
					)
				})

				// 绘制 清空 按钮
				// eslint-disable-next-line no-unused-vars
				const clearItem =
					typeof this.alert.clear === 'boolean' && this.alert.clear
						? this.renderClear(this.clearSelected)
						: typeof this.alert.clear === 'function'
						? this.renderClear(this.alert.clear)
						: null

				// 绘制 alert 组件
				if (alert) {
					const message = (
						<div>
							<span className="mr-3">
								已选择: <a className="font-6">{this.selectedRows.length}</a>
							</span>
							{needTotalItems}
							{clearItem}
						</div>
					)

					return <a-alert showIcon class="mb-4" message={message} />
				}
			},
			columnChange(val) {
				this.columnsSetting = val
			},
			renderHeader() {
				let tools = [
					{
						name: 'refresh',
						icon: <sync-outlined class="ml-4" />,
						title: '刷新',
						onClick: () => {
							this.refresh()
						}
					},
					{
						name: 'height',
						icon: <column-height-outlined />,
						title: '密度',
						isDropdown: true,
						menu: () => {
							const onClick = ({ key }) => {
								this.customSize = key
							}
							return (
								<a-menu onClick={onClick} selectable selectedKeys={[this.customSize]}>
									<a-menu-item key="default">默认</a-menu-item>
									<a-menu-item key="middle">中等</a-menu-item>
									<a-menu-item key="small">紧凑</a-menu-item>
								</a-menu>
							)
						},
						onClick: () => {}
					},
					{
						name: 'columnSetting',
						icon: <setting-outlined />,
						title: '列设置',
						isPopover: true,
						visible: false,
						menu: () => {
							return <columnSetting columns={this.columns} onColumnChange={this.columnChange} />
						},
						onClick: () => {}
					}
				]
				if (this.extraTool.length) {
					tools = tools.concat(this.extraTool)
				}

				// 斑马纹
				const changeRowClass = (value) => {
					const val = value.target.checked
					this.localSettings.rowClassNameSwitch = val
					const evenClass = val ? (_record, index) => (index % 2 === 1 ? 'table-striped' : null) : this.rowClassName
					this.localSettings.rowClassName = evenClass
				}
				return (
					<div className="s-table-tool">
						<div className="s-table-tool-left">{this.$slots.operator && this.$slots.operator()}</div>
						<div className="layout-items-center s-table-tool-right">
							{this.toolConfig.striped ? (
								<div className="layout-items-center ml-4">
									<a-checkbox
										checked={this.localSettings.rowClassNameSwitch}
										onChange={changeRowClass}
									>斑马纹
									</a-checkbox>
								</div>
							) : null}

							{tools.map((tool) => {
								if (!this.toolConfig[tool.name]) {
									return null
								}
								const tooltipEle = (
									<a-tooltip title={tool.title} class="s-tool-item" onClick={tool.onClick}>
										{tool.icon}
									</a-tooltip>
								)
								if (tool.isPopover) {
									return (
										<a-popover
											trigger={'click'}
											placement="topLeft"
											overlayClassName="s-table-column-settings"
											arrow-point-at-center
											content={tool.menu()}>
											{tooltipEle}
										</a-popover>
									)
								}
								if (tool.isDropdown) {
									return (
										<a-dropdown trigger={['click']} overlay={tool.menu()}>
											{tooltipEle}
										</a-dropdown>
									)
								}
								return tooltipEle
							})}
						</div>
					</div>
				)
			}
		},

		render() {
			let props = {}
			const localKeys = Object.keys(this.$data)
			const showAlert =
				(typeof this.alert === 'object' &&
					this.alert !== null &&
					this.alert.show &&
					typeof this.rowSelection.selectedRowKeys !== 'undefined') ||
				this.alert

			Object.keys(tableProps()).forEach((k) => {
				const localKey = `local${k.substring(0, 1).toUpperCase()}${k.substring(1)}`
				if (localKeys.includes(localKey)) {
					props[k] = this[localKey]
					return props[k]
				}
				if (k === 'rowSelection') {
					if (showAlert && this.rowSelection) {
						// 如果需要使用alert，则重新绑定 rowSelection 事件
						props[k] = {
							...this.rowSelection,
							selectedRows: this.selectedRows,
							selectedRowKeys: this.selectedRowKeys,
							onChange: (selectedRowKeys, selectedRows) => {
								this.updateSelect(selectedRowKeys, selectedRows)
								typeof this[k].onChange !== 'undefined' && this[k].onChange(selectedRowKeys, selectedRows)
							}
						}
						return props[k]
					} else if (!showAlert && this.rowSelection) {
						props[k] = {
							...this.rowSelection,
							selectedRows: this.selectedRows,
							selectedRowKeys: this.selectedRowKeys,
							onChange: (selectedRowKeys, selectedRows) => {
								this.updateSelect(selectedRowKeys, selectedRows)
								typeof this[k].onChange !== 'undefined' && this[k].onChange(selectedRowKeys, selectedRows)
							}
						}
						return props[k]
					} else if (!this.rowSelection) {
						// 如果没打算开启 rowSelection 则清空默认的选择项
						props[k] = null
						return props[k]
					}
				}
				this[k] && (props[k] = this[k])
				// 此处配置表格大小与要显示的列
				props = {
					...props,
					size: this.customSize, // 注意这个size是a-table组件需要的，这里不能跟别的地方成为compSize
					columns: this.columnsSetting.filter((value) => value.checked === undefined || value.checked)
				}
				return props[k]
			})
			const table = (
				<a-table
					{...props}
					{...this.localSettings}
					v-slots={this.$slots}
					onChange={this.loadData}
					onExpand={(expanded, record) => {
						this.$emit('expand', expanded, record)
					}}
				/>
			)

			return (
				<div className="table-wrapper">
					{this.renderHeader()}
					{showAlert ? this.renderAlert() : null}
					{table}
				</div>
			)
		}
	}
</script>
