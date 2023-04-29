<template>
	<xn-form-container
		title="授权权限"
		:width="drawerWidth"
		:visible="visible"
		:destroy-on-close="true"
		:show-pagination="false"
		@close="onClose"
	>
		<a-alert
			message="注：此功能界面需要与代码查询条件配合使用，并非所有接口都需设置数据范围，多用于业务模块！"
			type="warning"
			closable
		/>
		<a-spin :spinning="spinningLoading">
			<a-table
				class="mt-4"
				size="middle"
				:columns="columns"
				:data-source="loadDatas"
				:row-key="(record) => record.api"
				bordered
			>
				<template #headerCell="{ column }">
					<template v-if="column.key === 'api'">
						<a-checkbox @update:checked="(val) => onCheckAllChange(val)"> 接口 </a-checkbox>
					</template>
					<template v-if="column.key === 'dataScope'">
						<span>{{ column.title }}</span>
						<a-radio-group v-model:value="scopeRadioValue" @change="radioChange" style="padding: 0 10px">
							<a-radio value="SCOPE_ALL"> 全部 </a-radio>
							<a-radio value="SCOPE_SELF"> 仅自已 </a-radio>
							<a-radio value="SCOPE_ORG"> 所属组织 </a-radio>
							<a-radio value="SCOPE_ORG_CHILD"> 所属组织及以下 </a-radio>
						</a-radio-group>
					</template>
				</template>
				<template #customFilterDropdown="{ setSelectedKeys, selectedKeys, confirm, clearFilters, column }">
					<div style="padding: 8px">
						<a-input
							ref="searchInput"
							placeholder="请输入接口或接口名称"
							:value="selectedKeys[0]"
							style="width: 188px; margin-bottom: 8px; display: block"
							@change="(e) => setSelectedKeys(e.target.value ? [e.target.value] : [])"
							@pressEnter="handleSearch(selectedKeys, confirm, column.dataIndex)"
						/>
						<a-button
							type="primary"
							size="small"
							style="width: 90px; margin-right: 8px"
							@click="handleSearch(selectedKeys, confirm, column.dataIndex)"
						>
							<template #icon><SearchOutlined /></template>
							搜索
						</a-button>
						<a-button size="small" style="width: 90px" @click="handleReset(clearFilters)"> 重置 </a-button>
					</div>
				</template>
				<template #customFilterIcon="{ filtered }">
					<search-outlined :style="{ color: filtered ? '#108ee9' : undefined }" />
				</template>
				<template #bodyCell="{ column, record }">
					<template v-if="column.dataIndex === 'api'">
						<a-checkbox :checked="record.check" @update:checked="(val) => changeApi(record, val)">
							{{ record.api }}
						</a-checkbox>
					</template>
					<template v-if="column.dataIndex === 'dataScope'">
						<template v-if="record.dataScope.length > 0">
							<template v-for="item in record.dataScope" :key="item.id + record.api">
								<a-radio
									v-model:checked="item.check"
									:name="item.title"
									@change="(evt) => changeDataScope(record, evt)"
								>
									<a-badge
										v-if="
											(item.value === 'SCOPE_ORG_DEFINE') &
											record.dataScope[4].check &
											(item.scopeDefineOrgIdList !== undefined)
										"
										:count="item.scopeDefineOrgIdList.length"
										:number-style="{ backgroundColor: '#52c41a' }"
									>
										{{ item.title }}</a-badge
									>
									<div v-else>{{ item.title }}</div>
								</a-radio>
							</template>
							<a-button v-if="record.dataScope[4].check" type="link" size="small" @click="handleDefineOrg(record)"
								>选择组织</a-button
							>
						</template>
					</template>
				</template>
			</a-table>
		</a-spin>
		<template #footer>
			<a-button style="margin-right: 8px" @click="onClose">关闭</a-button>
			<a-button type="primary" :loading="submitLoading" @click="onSubmit">保存</a-button>
		</template>
		<ScopeDefineOrg ref="scopeDefineOrgModal" @click="scopeDefineOrgClick" />
	</xn-form-container>
</template>

<script setup name="grantResourceForm">
	import { SearchOutlined } from '@ant-design/icons-vue'
	import roleApi from '@/api/sys/roleApi'
	import ScopeDefineOrg from './scopeDefineOrg.vue'

	const visible = ref(false)
	const spinningLoading = ref(false)
	const scopeDefineOrgModal = ref(null)
	const emit = defineEmits({ successful: null })
	const submitLoading = ref(false)
	const CustomValue = 'SCOPE_ORG_DEFINE'
	// 抽屉的宽度
	const drawerWidth = 1000
	// 自动获取宽度，默认获取浏览器的宽度的90%
	//(window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth) * 0.9
	let loadDatas = ref([])
	// 标题接口列搜索, 数据范围默认
	const scopeRadioValue = ref()
	const state = reactive({
		searchText: '',
		searchedColumn: ''
	})
	const searchInput = ref()

	const columns = [
		{
			key: 'api',
			title: '接口',
			dataIndex: 'api',
			width: 380,
			customFilterDropdown: true,
			onFilter: (value, record) => record.api.includes(value),
			onFilterDropdownVisibleChange: (visible) => {
				if (visible) {
					setTimeout(() => {
						searchInput.value.focus()
					}, 100)
				}
			}
		},
		{
			key: 'dataScope',
			title: '数据范围',
			dataIndex: 'dataScope'
		}
	]
	// 获取数据
	const loadData = async () => {
		spinningLoading.value = true
		const res = await roleApi.rolePermissionTreeSelector()
		// 获取他已有的权限
		const param = {
			id: grantPermissionParam.id
		}
		const resOwn = await roleApi.roleOwnPermission(param)
		// 数据转换
		echoModuleData(res, resOwn)
		spinningLoading.value = false
	}
	// 数据转换
	const echoModuleData = (res, resOwn) => {
		res.forEach((api) => {
			const obj = {
				api: api,
				dataScope: datascope(api),
				check: false
			}
			if (resOwn.grantInfoList.length > 0) {
				resOwn.grantInfoList.forEach((item) => {
					if (item.apiUrl === subStrApi(api)) {
						obj.check = true
						// dataScopeInfo
						obj.dataScope.forEach((o) => {
							if (o.value === item.scopeCategory) {
								o.check = true
								// 如果是自定义
								if (item.scopeCategory === 'SCOPE_ORG_DEFINE') {
									o.scopeDefineOrgIdList = item.scopeDefineOrgIdList
								}
							}
						})
					}
				})
			}
			loadDatas.value.push(obj)
		})
	}
	const datascope = (id) => {
		return [
			{
				id: `SCOPE_ALL_${id}`,
				title: '全部',
				value: 'SCOPE_ALL',
				check: false
			},
			{
				id: `SCOPE_SELF_${id}`,
				title: '仅自己',
				value: 'SCOPE_SELF',
				check: false
			},
			{
				id: `SCOPE_ORG_${id}`,
				title: '所属组织',
				value: 'SCOPE_ORG',
				check: false
			},
			{
				id: `SCOPE_ORG_CHILD_${id}`,
				title: '所属组织及以下',
				value: 'SCOPE_ORG_CHILD',
				check: false
			},

			{
				id: `SCOPE_ORG_DEFINE_${id}`,
				title: '自定义',
				value: 'SCOPE_ORG_DEFINE',
				check: false
			}
		]
	}
	// 点击数据权限选择
	const changeDataScope = (record, evt) => {
		const name = evt.target.name
		// 这里做互斥，每个
		record.dataScope.forEach((item) => {
			if (item.title !== name) {
				item.check = false
			}
		})
		changeChildCheckBox(record, evt)
	}
	// 处理自定义
	const handleDefineOrg = (recordDataScope) => {
		// 弹框选择子自定义
		const data = recordDataScope.dataScope.find((f) => f.value === CustomValue)
		// 选中了
		if (data.check) {
			// 获取到选中的key数组，传过去，让其那边回显
			const checkKeysStr = recordDataScope.dataScope[4].scopeDefineOrgIdList
			scopeDefineOrgModal.value.onOpen(data.id, checkKeysStr)
		} else {
			// 清理缓存中的结构,去掉就行
			handleDatascope(false, record.id, null)
		}
	}
	// 自定义数据弹窗回调
	const scopeDefineOrgClick = (value) => {
		handleDatascope(true, value.dataScopeId, value.defineOrgIdData.scopeDefineOrgIdList)
	}
	// 处理Datascope数据被选中自定义或取消自定义数据
	const handleDatascope = (check, id, orgData) => {
		loadDatas.value.forEach((item) => {
			if (id === 'SCOPE_ORG_DEFINE_' + item.api) {
				item.dataScope.forEach((items) => {
					if (items.value === 'SCOPE_ORG_DEFINE') {
						if (check) {
							items.scopeDefineOrgIdList = orgData
						} else {
							items.scopeDefineOrgIdList = []
						}
					}
				})
			}
		})
	}

	// 打开抽屉
	const onOpen = (record) => {
		grantPermissionParam.id = record.id
		visible.value = true
		loadData()
	}
	// 关闭抽屉
	const onClose = () => {
		// 将这些缓存的给清空
		loadDatas.value = []
		scopeRadioValue.value = ''
		visible.value = false
	}
	// 全选
	const onCheckAllChange = (value) => {
		spinningLoading.value = true
		loadDatas.value.forEach((data) => {
			changeApi(data, value)
			spinningLoading.value = false
		})
	}

	// 选中接口
	const changeApi = (record, val) => {
		record.check = val
		if (val) {
			let checkStatus = 0
			for (let i = 0; i < record.dataScope.length; i++) {
				if (record.dataScope[i].check) {
					checkStatus++
				}
			}
			if (checkStatus === 0) {
				record.dataScope[0].check = true
			}
		} else {
			// 去掉已选中的
			record.dataScope.forEach((item) => {
				item.check = false
				if (item.value === 'SCOPE_ORG_DEFINE') {
					item.scopeDefineOrgIdList = []
				}
			})
		}
	}
	// 设置选中状态
	const changeChildCheckBox = (record, evt) => {
		let checked = evt.target.checked
		if (!checked) {
			record.check = false
		} else if (checked) {
			record.check = checked
		}
	}
	// 提交数据模型
	let grantPermissionParam = {
		// 角色id
		id: '',
		// 授权权限信息
		grantInfoList: []
	}
	// 提交之前转换数据
	const convertData = () => {
		grantPermissionParam.grantInfoList = []
		loadDatas.value.forEach((table) => {
			if (table.check) {
				table.dataScope.forEach((item) => {
					if (item.check) {
						const dataScopeInfo = {
							apiUrl: subStrApi(table.api),
							scopeCategory: item.value,
							scopeDefineOrgIdList: item.scopeDefineOrgIdList === undefined ? [] : item.scopeDefineOrgIdList
						}
						grantPermissionParam.grantInfoList.push(dataScopeInfo)
					}
				})
			}
		})
		return grantPermissionParam
	}
	// 截取api串中的中文及括号
	const subStrApi = (api) => {
		return api.substring(0, api.indexOf('['))
	}
	// 验证并提交数据
	const onSubmit = () => {
		const param = convertData()
		submitLoading.value = true
		roleApi
			.roleGrantPermission(param)
			.then(() => {
				onClose()
				emit('successful')
			})
			.finally(() => {
				submitLoading.value = false
			})
	}
	// 标题接口列搜索
	const handleSearch = (selectedKeys, confirm, dataIndex) => {
		confirm()
		state.searchText = selectedKeys[0]
		state.searchedColumn = dataIndex
	}
	// 标题接口列搜索重置
	const handleReset = (clearFilters) => {
		clearFilters()
		state.searchText = ''
	}
	// 标题数据范围列radio-group事件
	const radioChange = (obj) => {
		loadDatas.value.forEach((data) => {
			data.dataScope.forEach((data) => {
				if (obj.target.value === data.value) {
					data.check = true
				} else {
					data.check = false
				}
			})
		})
	}
	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>

<style scoped>
	/* 重写复选框的样式 */
	.ant-checkbox-wrapper {
		margin-left: 0px !important;
		padding-top: 2px !important;
		padding-bottom: 2px !important;
	}
</style>
