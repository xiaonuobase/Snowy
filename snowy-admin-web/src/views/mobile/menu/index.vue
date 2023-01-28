<template>
    <a-card :bordered="false">
        <a-form ref="searchFormRef" name="advanced_search" :model="searchFormState" class="ant-advanced-search-form mb-4">
            <a-row :gutter="24">
                <a-col :span="6">
                    <a-form-item label="关键词" name="searchKey">
                        <a-input v-model:value="searchFormState.searchKey" placeholder="请输入关键词" />
                    </a-form-item>
                </a-col>
                <a-col :span="6">
                    <a-form-item label="分类" name="category">
                        <a-select v-model:value="searchFormState.category" placeholder="请选择分类" :options="categoryOptions" />
                    </a-form-item>
                </a-col>
                <a-col :span="6">
                    <a-form-item label="可用状态" name="status">
                        <a-select v-model:value="searchFormState.status" placeholder="请选择可用状态" :options="statusOptions" />
                    </a-form-item>
                </a-col>
                <a-col :span="6">
                    <a-button type="primary" @click="table.refresh(true)">查询</a-button>
                    <a-button style="margin: 0 8px" @click="() => searchFormRef.resetFields()">重置</a-button>
                </a-col>
            </a-row>
        </a-form>
        <s-table
            ref="table"
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
                    <a-button type="primary" @click="formRef.onOpen()">
                        <template #icon><plus-outlined /></template>
                        新增
                    </a-button>
                    <a-button danger @click="deleteBatchMobileMenu()">删除</a-button>
                </a-space>
            </template>
            <template #bodyCell="{ column, record }">
                <template v-if="column.dataIndex === 'category'">
                    {{ $TOOL.dictTypeData('MOBILE_CATEGORY', record.category) }}
                </template>
                <template v-if="column.dataIndex === 'isRegExp'">
                    {{ $TOOL.dictTypeData('MOBILE_IS_REG_EXP', record.isRegExp) }}
                </template>
                <template v-if="column.dataIndex === 'status'">
                    {{ $TOOL.dictTypeData('MOBILE_STATUS', record.status) }}
                </template>
                <template v-if="column.dataIndex === 'action'">
                    <a-space>
                        <a @click="formRef.onOpen(record)">编辑</a>
                        <a-divider type="vertical" />
                        <a-popconfirm title="确定要删除吗？" @confirm="deleteMobileMenu(record)">
                            <a-button type="link" danger size="small">删除</a-button>
                        </a-popconfirm>
                    </a-space>
                </template>
            </template>
        </s-table>
    </a-card>
    <Form ref="formRef" @successful="table.refresh(true)" />
</template>

<script setup name="mobileMenuIndex">
    import { message } from 'ant-design-vue'
    import tool from '@/utils/tool'
    import Form from './form.vue'
    import mobileMenuApi from '@/api/mobile/mobileMenuApi'
    let searchFormState = reactive({})
    const searchFormRef = ref()
    const table = ref()
    const formRef = ref()
    const toolConfig = { refresh: true, height: true, columnSetting: true, striped: false }
    const columns = [
        {
            title: '名称',
            dataIndex: 'title'
        },
        {
            title: '界面路径',
            dataIndex: 'pages',
            ellipsis: true
        },
        {
            title: '分类',
            dataIndex: 'category'
        },
        {
            title: '图标',
            dataIndex: 'icon'
        },
        {
            title: '正规则',
            dataIndex: 'isRegExp'
        },
        {
            title: '可用状态',
            dataIndex: 'status'
        },
        {
            title: '排序码',
            dataIndex: 'sortCode'
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
			width: '150px'
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
        const searchFormParam = JSON.parse(JSON.stringify(searchFormState))
        return mobileMenuApi.mobileMenuTree(Object.assign(parameter, searchFormParam)).then((data) => {
			if (data) {
				return data
			}
			return []
        })
    }
    // 删除
    const deleteMobileMenu = (record) => {
        let params = [
            {
                id: record.id
            }
        ]
        mobileMenuApi.mobileMenuDelete(params).then(() => {
            table.value.refresh(true)
        })
    }
    // 批量删除
    const deleteBatchMobileMenu = () => {
        if (selectedRowKeys.value.length < 1) {
            message.warning('请选择一条或多条数据')
            return false
        }
        const params = selectedRowKeys.value.map((m) => {
            return {
                id: m
            }
        })
        mobileMenuApi.mobileMenuDelete(params).then(() => {
            table.value.clearRefreshSelected()
        })
    }
    const categoryOptions = tool.dictList('MOBILE_CATEGORY')
    const statusOptions = tool.dictList('MOBILE_STATUS')
</script>
