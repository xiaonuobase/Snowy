<template>
	<a-card :bordered="false">
		<a-space>
			<a-radio-group v-model:value="moduleType" button-style="solid">
				<a-radio-button
					v-for="module in moduleTypeList"
					:key="module.id"
					:value="module.id"
					@click="moduleClock(module.id)"
				>
					<component :is="module.icon" />
					{{ module.title }}</a-radio-button
				>
			</a-radio-group>
			<a-input-search
				v-model:value="searchFormState.searchKey"
				placeholder="请输入菜单名称关键词"
				enter-button
				allowClear
				@search="onSearch"
			/>
		</a-space>
	</a-card>
	<a-card :bordered="false" class="mt-2">
		<s-table
			ref="table"
			:columns="columns"
			:data="loadData"
			:alert="options.alert.show"
			bordered
			:row-key="(record) => record.id"
			:show-pagination="false"
			:tool-config="toolConfig"
			:row-selection="options.rowSelection"
		>
			<template #operator class="table-operator">
				<a-space>
					<a-button type="primary" @click="form.onOpen(undefined, moduleType)">
						<template #icon><plus-outlined /></template>
						新增菜单
					</a-button>
					<xn-batch-delete :selectedRowKeys="selectedRowKeys" @batchDelete="deleteBatchMenu" />
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'path'">
					<span v-if="record.menuType === 'MENU'">{{ record.path }}</span>
					<span v-else>-</span>
				</template>
				<template v-if="column.dataIndex === 'component'">
					<span v-if="record.menuType === 'MENU'">{{ record.component }}</span>
					<span v-else>-</span>
				</template>
				<template v-if="column.dataIndex === 'icon'">
					<component :is="record.icon" />
				</template>
				<template v-if="column.dataIndex === 'menuType'">
					<a-tag v-if="record.menuType === 'CATALOG'" color="cyan">
						{{ $TOOL.dictTypeData('MENU_TYPE', record.menuType) }}
					</a-tag>
					<a-tag v-if="record.menuType === 'MENU'" color="blue">
						{{ $TOOL.dictTypeData('MENU_TYPE', record.menuType) }}
					</a-tag>
					<a-tag v-if="record.menuType === 'IFRAME'" color="purple">
						{{ $TOOL.dictTypeData('MENU_TYPE', record.menuType) }}
					</a-tag>
					<a-tag v-if="record.menuType === 'LINK'" color="orange">
						{{ $TOOL.dictTypeData('MENU_TYPE', record.menuType) }}
					</a-tag>
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a @click="form.onOpen(record, moduleType)">编辑</a>
						<a-divider type="vertical" />
						<a-popconfirm title="确定要删除此菜单吗？" @confirm="deleteMenu(record)">
							<a-button type="link" danger size="small">删除</a-button>
						</a-popconfirm>
						<div v-if="record.parentId === '0' || record.menuType === 'MENU'">
							<a-divider type="vertical" />
							<a-dropdown>
								<a class="ant-dropdown-link">
									更多
									<DownOutlined />
								</a>
								<template #overlay>
									<a-menu>
										<a-menu-item v-if="record.parentId === '0'">
											<a @click="changeModuleFormRef.onOpen(record)">更改模块</a>
										</a-menu-item>
										<a-menu-item v-if="record.menuType === 'MENU'">
											<a @click="button.onOpen(record)">按钮权限</a>
										</a-menu-item>
									</a-menu>
								</template>
							</a-dropdown>
						</div>
					</a-space>
				</template>
			</template>
		</s-table>
	</a-card>
	<Form ref="form" @successful="table.refresh(true)" />
	<changeModuleForm ref="changeModuleFormRef" @successful="table.refresh(true)" />
	<Button ref="button" />
</template>

<script setup name="sysMenu">
	import menuApi from '@/api/sys/resource/menuApi'
	import Form from './form.vue'
	import changeModuleForm from './changeModuleForm.vue'
	import Button from '../button/index.vue'
	let searchFormState = reactive({})
	const table = ref(null)
	let form = ref()
	let changeModuleFormRef = ref()
	let button = ref()
	let field = ref()
	const moduleType = ref()
	let moduleTypeList = ref([])
	const toolConfig = { refresh: true, height: true, columnSetting: false, striped: false }
	const columns = [
		{
			title: '显示名称',
			dataIndex: 'title',
			width: 260
		},
		{
			title: '图标',
			dataIndex: 'icon'
		},
		{
			title: '类型',
			dataIndex: 'menuType'
		},
		{
			title: '路由地址',
			dataIndex: 'path',
			ellipsis: true,
			width: 150
		},
		{
			title: '组件',
			dataIndex: 'component',
			ellipsis: true,
			width: 150
		},
		{
			title: '排序',
			dataIndex: 'sortCode',
			sorter: true
		},
		{
			title: '创建时间',
			dataIndex: 'createTime',
			ellipsis: true,
			sorter: true
		},
		{
			title: '操作',
			dataIndex: 'action',
			width: '200px',
			scopedSlots: { customRender: 'action' }
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
	const loadData = (parameter) => {
		if (!moduleType.value) {
			return menuApi.menuModuleSelector().then((data) => {
				moduleTypeList.value = data
				moduleType.value = data.length > 0 ? data[0].id : ''
				searchFormState.module = moduleType.value
				return menuApi.menuTree(Object.assign(parameter, searchFormState)).then((data) => {
					if (data) {
						return data
					} else {
						return []
					}
				})
			})
		} else {
			return menuApi.menuTree(Object.assign(parameter, searchFormState)).then((data) => {
				if (data) {
					return data
				} else {
					return []
				}
			})
		}
	}
	// 切换应用标签查询菜单列表
	const moduleClock = (value) => {
		searchFormState.module = value
		table.value.refresh(true)
	}
	// 查询
	const onSearch = () => {
		table.value.refresh(true)
	}
	/* const removeEmptyChildren = (data) => {
		if (data == null || data.length === 0) return;
		for (let i = 0; i < data.length; i++) {
			const item = data[i];
			if (item.children != null && item.children.length === 0) {
				item.children = null;
			} else {
				removeEmptyChildren(item.children);
			}
		}
		return data;
	};*/
	// 删除
	const deleteMenu = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		menuApi.menuDelete(params).then(() => {
			table.value.refresh(true)
		})
	}
	// 批量删除
	const deleteBatchMenu = (params) => {
		menuApi.menuDelete(params).then(() => {
			table.value.clearRefreshSelected()
		})
	}
</script>
