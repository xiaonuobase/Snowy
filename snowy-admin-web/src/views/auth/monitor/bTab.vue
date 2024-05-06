<template>
	<s-table ref="tableRef" :columns="columns" :data="loadDataB" :alert="false" bordered :row-key="(record) => record.id">
		<template #bodyCell="{ column, record }">
			<template v-if="column.dataIndex === 'avatar'">
				<a-avatar :src="record.avatar" class="xn-wh25" />
			</template>
			<template v-if="column.dataIndex === 'tokenNumber'">
				{{ record.tokenSignList.length }}
			</template>
			<template v-if="column.dataIndex === 'action'">
				<a @click="tokenInfoList.onOpen(record.tokenSignList, 'B')">令牌列表</a>
				<a-divider type="vertical" />
				<a-space>
					<a-popconfirm title="确定要强退此用户吗？" @confirm="bExit(record)">
						<a-button type="link" danger size="small">强退</a-button>
					</a-popconfirm>
				</a-space>
			</template>
		</template>
	</s-table>
	<token-info-list ref="tokenInfoList" @successful="tableRef.refresh()" />
</template>

<script setup name="monitorBTab">
	import monitorApi from '@/api/auth/monitorApi'
	import TokenInfoList from './tokenInfoList.vue'
	const tableRef = ref(null)
	const tokenInfoList = ref()
	const columns = [
		{
			title: '头像',
			dataIndex: 'avatar',
			width: 60
		},
		{
			title: '账号',
			dataIndex: 'account'
		},
		{
			title: '姓名',
			dataIndex: 'name',
			ellipsis: true
		},
		{
			title: '登录地点',
			dataIndex: 'latestLoginAddress',
			ellipsis: true
		},
		{
			title: '最后登录时间',
			dataIndex: 'lastLoginTime',
			ellipsis: true
		},
		{
			title: '登录IP',
			dataIndex: 'latestLoginIp',
			width: 120,
			ellipsis: true
		},
		{
			title: '令牌数',
			dataIndex: 'tokenNumber',
			width: 70
		},
		{
			title: '操作',
			dataIndex: 'action',
			width: '150px',
			scopedSlots: { customRender: 'action' }
		}
	]
	// 列表数据
	const loadDataB = (parameter) => {
		return monitorApi.monitorBPage(parameter).then((res) => {
			return res
		})
	}
	// 强退B端session
	const bExit = (record) => {
		let params = [
			{
				userId: record.id
			}
		]
		monitorApi.monitorBExit(params).then(() => {
			tableRef.value.refresh(true)
		})
	}
</script>

<style scoped>
	.table-wrapper {
		margin-top: -16px !important;
	}
</style>
