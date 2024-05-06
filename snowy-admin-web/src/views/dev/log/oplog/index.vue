<template>
	<a-row :gutter="10" class="mb-2">
		<a-col :span="16">
			<a-card :bordered="false" title="周统计">
				<columnChart />
			</a-card>
		</a-col>
		<a-col :span="8">
			<a-card :bordered="false" title="总比例">
				<pieChart />
			</a-card>
		</a-col>
	</a-row>

	<a-card :bordered="false">
		<s-table ref="tableRef" :columns="columns" :data="loadData" bordered :row-key="(record) => record.id">
			<template #operator class="table-operator">
				<a-form ref="formRef" name="advanced_search" :model="searchFormState" class="ant-advanced-search-form">
					<a-space>
						<a-radio-group v-model:value="opLogType" button-style="solid">
							<a-radio-button
								v-for="opLog in opLogTypeList"
								:key="opLog.value"
								:value="opLog.value"
								@click="opLogTypeClock(opLog.value)"
							>
								{{ opLog.label }}
							</a-radio-button>
						</a-radio-group>
						<a-input-search
							v-model:value="searchFormState.searchKey"
							placeholder="请输入名称关键词"
							enter-button
							allowClear
							@search="onSearch"
						/>
						<a-popconfirm title="确定清空操作与异常日志吗？" @confirm="deleteBatchOpLog()">
							<a-button danger>清空</a-button>
						</a-popconfirm>
					</a-space>
				</a-form>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'className'">
					<ellipsis :length="70" tooltip>
						{{ record.className }}
					</ellipsis>
				</template>
				<template v-if="column.dataIndex === 'methodName'">
					<ellipsis :length="20" tooltip>
						{{ record.methodName }}
					</ellipsis>
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a @click="getDetail(record.id)">详情</a>
					</a-space>
				</template>
			</template>
		</s-table>
	</a-card>
	<detail ref="detailRef" />
</template>

<script setup name="devOplog">
	import logApi from '@/api/dev/logApi'
	import ColumnChart from './columnChart.vue'
	import PieChart from './pieChart.vue'
	import Detail from './detail.vue'
	const searchFormState = ref({})
	const formRef = ref()
	const tableRef = ref()
	const detailRef = ref()
	const opLogType = ref('OPERATE')
	let opLogTypeList = ref([
		{
			label: '操作日志',
			value: 'OPERATE'
		},
		{
			label: '异常日志',
			value: 'EXCEPTION'
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
			title: '类名称',
			dataIndex: 'className'
		},
		{
			title: '方法名称',
			dataIndex: 'methodName'
		},
		{
			title: '操作时间',
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
	// 获取日志详情
	const getDetail = (id) => {
		let param = { id: id }
		logApi.logDetail(param).then((data) => {
			detailRef.value.onOpen(data)
		})
	}
	// 切换应用标签查询
	const opLogTypeClock = (value) => {
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
		searchFormState.value.category = searchFormState.value.category ? searchFormState.value.category : opLogType.value
		return logApi.logPage(Object.assign(parameter, searchFormState.value)).then((data) => {
			return data
		})
	}
	// 清空
	const deleteBatchOpLog = () => {
		const param = {
			category: searchFormState.category ? searchFormState.category : opLogType.value
		}
		logApi.logDelete(param).then(() => {
			tableRef.value.refresh(true)
		})
	}
</script>
