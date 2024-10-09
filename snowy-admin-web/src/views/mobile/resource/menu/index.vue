<template>
	<a-card :bordered="false" :body-style="{ 'padding-bottom': '0px' }" class="mb-2">
		<a-form ref="searchFormRef" name="advanced_search" :model="searchFormState" class="ant-advanced-search-form">
			<a-space style="align-items: normal">
				<a-radio-group v-model:value="module" button-style="solid">
					<a-radio-button
						v-for="module in moduleList"
						:key="module.id"
						:value="module.id"
						@click="moduleClock(module.id)"
					>
						<component :is="module.icon" />
						{{ module.title }}</a-radio-button
					>
				</a-radio-group>
				<a-form-item name="searchKey">
					<a-space>
						<a-input v-model:value="searchFormState.searchKey" placeholder="请输入模块名称关键词"></a-input>
						<a-button type="primary" @click="tableRef.refresh(true)">查询</a-button>
						<a-button class="xn-mg08" @click="reset">重置</a-button>
					</a-space>
				</a-form-item>
			</a-space>
		</a-form>
	</a-card>
	<a-card :bordered="false">
		<s-table
			ref="tableRef"
			:columns="columns"
			:data="loadData"
			:alert="options.alert.show"
			bordered
			:row-key="(record) => record.id"
			:tool-config="toolConfig"
			:show-pagination="false"
			:row-selection="options.rowSelection"
		>
			<template #operator class="table-operator">
				<a-space>
					<a-button type="primary" @click="formRef.onOpen(undefined, module)">
						<template #icon><plus-outlined /></template>
						新增
					</a-button>
					<xn-batch-button
						buttonName="批量删除"
						icon="DeleteOutlined"
						buttonDanger
						:selectedRowKeys="selectedRowKeys"
						@batchCallBack="deleteBatchMobileMenu"
					/>
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'path'">
					<span v-if="record.menuType === 'CATALOG'">-</span>
					<span v-else>{{ record.path }}</span>
				</template>
				<template v-if="column.dataIndex === 'icon'">
					<a-tag :color="record.color">
						<span class="snowy xn-icons" :class="record.icon"></span>
					</a-tag>
				</template>
				<template v-if="column.dataIndex === 'regType'">
					{{ $TOOL.dictTypeData('MOBILE_REG_TYPE', record.regType) }}
				</template>
				<template v-if="column.dataIndex === 'status'">
					{{ $TOOL.dictTypeData('MOBILE_STATUS', record.status) }}
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a @click="formRef.onOpen(record, module)">编辑</a>
						<a-divider type="vertical" />
						<a-popconfirm title="确定要删除吗？" @confirm="deleteMobileMenu(record)">
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
	<Form ref="formRef" @successful="tableRef.refresh(true)" />
	<changeModuleForm ref="changeModuleFormRef" @successful="tableRef.refresh(true)" />
	<Button ref="button" />
</template>

<script setup name="mobileMenuIndex">
	import Form from './form.vue'
	import ChangeModuleForm from './changeModuleForm.vue'
	import Button from '../button/index.vue'
	import mobileMenuApi from '@/api/mobile/resource/menuApi'
	const searchFormState = ref({})
	let moduleList = ref([])
	const module = ref()
	const tableRef = ref()
	const formRef = ref()
	const searchFormRef = ref()
	const changeModuleFormRef = ref()
	const button = ref()
	const toolConfig = { refresh: true, height: true, columnSetting: true, striped: false }
	const columns = [
		{
			title: '名称',
			dataIndex: 'title'
		},
		{
			title: '界面路径',
			dataIndex: 'path',
			ellipsis: true
		},
		{
			title: '图标',
			dataIndex: 'icon',
			width: 80
		},
		{
			title: '正规则',
			dataIndex: 'regType'
		},
		{
			title: '可用状态',
			dataIndex: 'status'
		},
		{
			title: '创建时间',
			dataIndex: 'createTime',
			ellipsis: true
		},
		{
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: 200
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
		if (!module.value) {
			return mobileMenuApi.mobileMenuModuleSelector().then((data) => {
				moduleList.value = data
				module.value = data.length > 0 ? data[0].id : ''
				searchFormState.value.module = module.value
				return mobileMenuApi.mobileMenuTree(Object.assign(parameter, searchFormState.value)).then((data) => {
					if (data) {
						return data
					}
					return []
				})
			})
		} else {
			return mobileMenuApi.mobileMenuTree(Object.assign(parameter, searchFormState.value)).then((data) => {
				if (data) {
					return data
				}
				return []
			})
		}
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
	// 切换模块标签查询菜单列表
	const moduleClock = (value) => {
		searchFormState.value.module = value
		tableRef.value.refresh(true)
	}
	// 删除
	const deleteMobileMenu = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		mobileMenuApi.mobileMenuDelete(params).then(() => {
			tableRef.value.refresh(true)
		})
	}
	// 批量删除
	const deleteBatchMobileMenu = (params) => {
		mobileMenuApi.mobileMenuDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
</script>
