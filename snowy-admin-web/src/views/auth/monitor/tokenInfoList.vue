<template>
	<xn-form-container title="令牌列表" :width="650" :visible="visible" :destroy-on-close="true" @close="onClose">
		<a-button
			danger
			class="xn-mb10"
			:disabled="selectedRowKeys.length === 0"
			:loading="beatchExitLoading"
			@click="beachExitTokenValue"
			>批量强退</a-button
		>
		<a-table
			:columns="columns"
			:data-source="loadData"
			:row-selection="rowSelection"
			bordered
			:row-key="(record) => record.tokenValue"
			size="small"
		>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'tokenDevice'">
					<a-tag v-if="record.tokenDevice === 'PC'" color="blue">
						{{ $TOOL.dictTypeData('AUTH_DEVICE_TYPE', record.tokenDevice) }}
					</a-tag>
					<a-tag v-if="record.tokenDevice === 'APP'" color="purple">
						{{ $TOOL.dictTypeData('AUTH_DEVICE_TYPE', record.tokenDevice) }}
					</a-tag>
					<a-tag v-if="record.tokenDevice === 'MINI'" color="orange">
						{{ $TOOL.dictTypeData('AUTH_DEVICE_TYPE', record.tokenDevice) }}
					</a-tag>
				</template>
				<template v-if="column.dataIndex === 'tokenTimeout'">
					<a-tooltip>
						<template #title>{{ record.tokenTimeout }}</template>
						<a-progress
							v-if="record.tokenTimeoutPercent * 100 > 80"
							:percent="record.tokenTimeoutPercent * 100"
							:show-info="false"
							status="success"
						/>
						<a-progress
							v-if="record.tokenTimeoutPercent * 100 > 20 && record.tokenTimeoutPercent * 100 < 80"
							:percent="record.tokenTimeoutPercent * 100"
							:show-info="false"
							status="active"
						/>
						<a-progress
							v-if="record.tokenTimeoutPercent * 100 < 20"
							:percent="record.tokenTimeoutPercent * 100"
							:show-info="false"
							status="exception"
						/>
					</a-tooltip>
				</template>
				<template v-if="column.dataIndex === 'tokenValue'">
					<a-tooltip>
						<template #title>{{ record.tokenValue }}</template>
						{{ record.tokenValue }}
					</a-tooltip>
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a-popconfirm title="确定要强退此令牌吗？" @confirm="exitToken(record)">
						<a-button type="link" danger size="small" :loading="exitLoading">强退</a-button>
					</a-popconfirm>
				</template>
			</template>
		</a-table>
	</xn-form-container>
</template>

<script setup>
	import monitorApi from '@/api/auth/monitorApi'
	import { cloneDeep } from 'lodash-es'
	const columns = [
		{
			title: '登录设备',
			dataIndex: 'tokenDevice',
			width: 90
		},
		{
			title: '有效期',
			dataIndex: 'tokenTimeout'
		},
		{
			title: '令牌',
			dataIndex: 'tokenValue',
			ellipsis: true,
			width: 80
		}
	]
	if (hasPerm('authForceQuit')) {
		columns.push({
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: '100px',
			scopedSlots: { customRender: 'action' }
		})
	}
	// 字段数据
	const loadData = ref([])
	// 默认是关闭状态
	const visible = ref(false)
	// 多选的
	const selectedRowKeys = ref([])
	const exitLoading = ref(false)
	const beatchExitLoading = ref(false)
	const monitorType = ref()
	// 定义emit事件
	const emit = defineEmits({ successful: null })
	// 打开抽屉
	const onOpen = (tokenInfoList, type) => {
		monitorType.value = type
		loadData.value = cloneDeep(tokenInfoList)
		visible.value = true
	}
	// 关闭抽屉
	const onClose = () => {
		loadData.value = []
		monitorType.value = ''
		visible.value = false
	}
	// 多选
	const rowSelection = {
		onChange: (rowKeys) => {
			selectedRowKeys.value = rowKeys
		}
	}
	// 删除
	const exitToken = (record) => {
		let params = [
			{
				tokenValue: record.tokenValue
			}
		]
		exitLoading.value = true
		if (monitorType.value === 'B') {
			monitorApi.monitorTokenBExit(params).then(() => {
				exitTask(params)
				exitLoading.value = false
			})
		} else {
			monitorApi.monitorTokenCExit(params).then(() => {
				exitTask(params)
				exitLoading.value = false
			})
		}
	}
	// 批量强退
	const beachExitTokenValue = () => {
		let params = []
		selectedRowKeys.value.forEach((key) => {
			params.push({ tokenValue: key })
		})
		beatchExitLoading.value = true
		if (monitorType.value === 'B') {
			monitorApi.monitorTokenBExit(params).then(() => {
				exitTask(params)
				selectedRowKeys.value = []
				beatchExitLoading.value = false
			})
		} else {
			monitorApi.monitorTokenCExit(params).then(() => {
				exitTask(params)
				selectedRowKeys.value = []
				beatchExitLoading.value = false
			})
		}
	}
	const exitTask = (params) => {
		loadData.value.forEach((item, index, array) => {
			params.forEach((items) => {
				if (item.tokenValue === items.tokenValue) {
					delete array[index]
				}
			})
		})
		emit('successful')
	}
	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>
