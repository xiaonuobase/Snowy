<template>
	<a-card :bordered="false" :body-style="{ 'padding-bottom': '0px' }" class="mb-2">
		<a-form ref="formRef" name="advanced_search" :model="searchFormState" class="ant-advanced-search-form">
			<a-row :gutter="24">
				<a-col :span="8">
					<a-form-item label="关键字" name="searchKey">
						<a-input v-model:value="searchFormState.searchKey" placeholder="请输入用户名或昵称关键词"></a-input>
					</a-form-item>
				</a-col>
				<a-col :span="8">
					<a-form-item label="分类" name="category">
						<a-select v-model:value="searchFormState.category" placeholder="请选择分类" :options="categoryOptions">
						</a-select>
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-button type="primary" @click="table.refresh(true)">查询</a-button>
					<a-button style="margin: 0 8px" @click="() => formRef.resetFields()">重置</a-button>
				</a-col>
			</a-row>
		</a-form>
	</a-card>
	<a-card :bordered="false">
		<s-table
			ref="table"
			:columns="columns"
			:data="loadData"
			:alert="false"
			bordered
			:row-key="(record) => record.id"
			:tool-config="toolConfig"
		>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'avatar'">
					<a-avatar :src="record.avatar" style="margin-bottom: -5px; margin-top: -5px" />
				</template>
				<template v-if="column.dataIndex === 'category'">
					{{ $TOOL.dictTypeData('THIRD_CATEGORY', record.category) }}
				</template>
			</template>
		</s-table>
	</a-card>
</template>

<script setup name="authThird">
	import thirdApi from '@/api/auth/thirdApi'
	import tool from '@/utils/tool'
	let searchFormState = reactive({})
	const formRef = ref()
	const table = ref()
	const toolConfig = { refresh: true, height: true, columnSetting: false, striped: false }
	const columns = [
		{
			title: '头像',
			dataIndex: 'avatar',
			align: 'center',
			width: '80px'
		},
		{
			title: '姓名',
			dataIndex: 'name',
			ellipsis: true
		},
		{
			title: '昵称',
			dataIndex: 'nickname',
			ellipsis: true
		},
		{
			title: '性别',
			dataIndex: 'gender',
			width: '100px'
		},
		{
			title: '分类',
			dataIndex: 'category'
		},
		{
			title: '创建时间',
			dataIndex: 'createTime',
			ellipsis: true,
			sorter: true
		}
	]
	const loadData = (parameter) => {
		return thirdApi.thirdPage(Object.assign(parameter, searchFormState)).then((res) => {
			return res
		})
	}
	// 分类
	const categoryOptions = tool.dictTypeList('THIRD_CATEGORY').map((item) => {
		return {
			value: item['dictValue'],
			label: item['name']
		}
	})
</script>
