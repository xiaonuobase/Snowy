<template>
	<a-row :gutter="10" class="mb-2">
		<a-col :span="16">
			<a-card :bordered="false" title="周统计">
				<lineChart ref="lineChartRef" />
			</a-card>
		</a-col>
		<a-col :span="8">
			<a-card :bordered="false" title="总比例">
				<pieChart ref="pieChartRef" />
			</a-card>
		</a-col>
	</a-row>

	<a-card :bordered="false">
		<s-table ref="tableRef" :columns="columns" :data="loadData" bordered :row-key="(record) => record.id">
			<template #operator class="table-operator">
				<a-form ref="formRef" name="advanced_search" :model="searchFormState" class="ant-advanced-search-form">
					<a-space>
						<a-radio-group v-model:value="visLogType" button-style="solid">
							<a-radio-button
								v-for="visLog in visLogTypeList"
								:key="visLog.value"
								:value="visLog.value"
								@click="visLogTypeClock(visLog.value)"
							>
								{{ visLog.label }}
							</a-radio-button>
						</a-radio-group>
						<a-input-search
							v-model:value="searchFormState.searchKey"
							placeholder="请输入名称关键词"
							enter-button
							allowClear
							@search="onSearch"
						/>
						<a-popconfirm title="确定清空登录登出日志吗？" @confirm="deleteBatchVisLog()">
							<a-button danger>清空</a-button>
						</a-popconfirm>
					</a-space>
				</a-form>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a @click="detailRef.onOpen(record)">详情</a>
					</a-space>
				</template>
			</template>
		</s-table>
	</a-card>
	<detail ref="detailRef" />
</template>

<script setup name="devVislog">
	import logApi from '@/api/dev/logApi'
	import LineChart from './lineChart.vue'
	import PieChart from './pieChart.vue'
	import Detail from './detail.vue'
	const searchFormState = ref({})
	const formRef = ref()
	const tableRef = ref()
	const detailRef = ref()
	const lineChartRef = ref()
	const pieChartRef = ref()

	const visLogType = ref('LOGIN')
	const visLogTypeList = ref([
		{
			label: '登录日志',
			value: 'LOGIN'
		},
		{
			label: '登出日志',
			value: 'LOGOUT'
		}
	])
	const columns = [
		{
			title: '名称',
			dataIndex: 'name'
		},
		{
			title: 'IP地址',
			dataIndex: 'opIp'
		},
		{
			title: '地址',
			dataIndex: 'opAddress'
		},
		{
			title: '浏览器',
			dataIndex: 'opBrowser'
		},
		{
			title: '设备',
			dataIndex: 'opOs'
		},
		{
			title: '时间',
			dataIndex: 'opTime',
			sorter: true
		},
		{
			title: '用户',
			dataIndex: 'opUser'
		},
		{
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: '100px'
		}
	]
	// 切换应用标签查询
	const visLogTypeClock = (value) => {
		searchFormState.value.category = value
		tableRef.value.refresh(true)
	}
	// 查询
	const onSearch = () => {
		if (searchFormState.value.searchKey) {
			tableRef.value.refresh(true)
		}
	}
	const loadData = (parameter) => {
		searchFormState.value.category = searchFormState.value.category ? searchFormState.value.category : visLogType.value
		return logApi.logPage(Object.assign(parameter, searchFormState.value)).then((data) => {
			return data
		})
	}
	// 清空
	const deleteBatchVisLog = () => {
		const param = {
			category: searchFormState.value.category ? searchFormState.value.category : visLogType.value
		}
		logApi.logDelete(param).then(() => {
			tableRef.value.refresh(true)
		})
	}
</script>
