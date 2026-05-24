<template>
	<xn-form-container title="权限调整" :width="1000" :visible="visible" :destroy-on-close="true" @close="onClose">
		<a-row :gutter="16" align="middle" style="margin-bottom: 12px">
			<a-col>
				<a-space :size="12">
					<span style="color: #86909c; font-size: 13px">被调整人：</span>
					<a-tag color="blue">{{ sourceUser.name }}（{{ sourceUser.account }}）</a-tag>
				</a-space>
			</a-col>
			<a-col>
				<a-space :size="12">
					<span style="color: #86909c; font-size: 13px">调整方式：</span>
					<a-radio-group v-model:value="transferMode" button-style="solid" size="small">
						<a-radio-button value="TRANSFER">转移</a-radio-button>
						<a-radio-button value="COPY">复制</a-radio-button>
						<a-radio-button value="DELETE">删除</a-radio-button>
					</a-radio-group>
				</a-space>
			</a-col>
			<a-col v-if="transferMode !== 'DELETE'">
				<a-space :size="12">
					<span style="color: #86909c; font-size: 13px">调整对象：</span>
					<template v-if="targetUser.id">
						<a-tag color="green" closable @close="clearTargetUser">
							{{ targetUser.name }}（{{ targetUser.account }}）
						</a-tag>
					</template>
					<a-button v-else size="small" @click="openTargetUserSelector">
						<template #icon><plus-outlined /></template>
						选择人员
					</a-button>
				</a-space>
			</a-col>
		</a-row>

		<a-spin :spinning="resourceLoading">
			<a-table
				:columns="resourceColumns"
				:data-source="resourceList"
				:pagination="false"
				row-key="resourceType"
				size="middle"
			>
				<template #headerCell="{ column }">
					<template v-if="column.dataIndex === 'transferAll'">
						<a-space :size="6">
							<span>全部转移</span>
							<a-switch :checked="isAllChecked" size="small" @change="toggleAllChecked" />
						</a-space>
					</template>
				</template>
				<template #bodyCell="{ column, record }">
					<template v-if="column.dataIndex === 'resourceTypeName'">
						<a-space>
							<a-badge :color="getResColor(record.resourceType)" />
							{{ record.resourceTypeName }}
						</a-space>
					</template>
					<template v-if="column.dataIndex === 'transferAll'">
						<a-switch v-model:checked="record.checked" size="small" @change="onCheckAllChange(record)" />
					</template>
					<template v-if="column.dataIndex === 'selectAction'">
						<a v-if="!record.checked && record.totalCount > 0" @click="openResourceDetail(record)"> 选择 </a>
						<span v-else style="color: #c9cdd4">选择</span>
					</template>
					<template v-if="column.dataIndex === 'selectedCount'">
						<a-tag v-if="getDisplayCount(record) > 0" color="blue">{{ getDisplayCount(record) }}</a-tag>
						<span v-else style="color: #c9cdd4">0</span>
					</template>
					<template v-if="column.dataIndex === 'totalCount'">
						<span :style="{ fontWeight: record.totalCount > 0 ? '600' : 'normal' }">{{ record.totalCount }}</span>
					</template>
				</template>
			</a-table>
		</a-spin>

		<a-modal
			v-model:open="detailModalVisible"
			:title="'选择' + currentDetailType.resourceTypeName"
			width="600px"
			@ok="handleDetailOk"
		>
			<a-spin :spinning="detailLoading">
				<a-table
					:columns="detailColumns"
					:data-source="detailList"
					:pagination="false"
					row-key="id"
					:row-selection="{
						selectedRowKeys: selectedDetailIds,
						onChange: onDetailSelectChange
					}"
					size="small"
					:scroll="{ y: 400 }"
				/>
			</a-spin>
		</a-modal>

		<template #footer>
			<a-button class="xn-mr8" @click="onClose">关闭</a-button>
			<a-button type="primary" :loading="executeLoading" :disabled="!canExecute" @click="handleExecute">
				<template #icon><swap-outlined /></template>
				开始执行
			</a-button>
		</template>

		<xn-user-selector
			ref="targetUserSelectorRef"
			radio-model
			:user-show="false"
			data-type="object"
			:org-tree-api="selectorApiFunction.orgTreeApi"
			:user-page-api="selectorApiFunction.userPageApi"
			@onBack="targetUserCallBack"
		/>
	</xn-form-container>
</template>

<script setup>
	import { ref, computed } from 'vue'
	import { PlusOutlined, SwapOutlined } from '@ant-design/icons-vue'
	import { message, Modal } from 'ant-design-vue'
	import transferApi from '@/api/sys/transferApi'
	import userCenterApi from '@/api/sys/userCenterApi'

	const emit = defineEmits(['successful'])
	const visible = ref(false)
	const sourceUser = ref({})
	const targetUser = ref({})
	const transferMode = ref('TRANSFER')
	const resourceList = ref([])
	const resourceLoading = ref(false)
	const executeLoading = ref(false)
	const targetUserSelectorRef = ref()

	const selectorApiFunction = {
		orgTreeApi: (param) => {
			return transferApi.transferOrgTreeSelector(param).then((data) => {
				return Promise.resolve(data)
			})
		},
		userPageApi: (param) => {
			return transferApi.transferUserSelector(param).then((data) => {
				return Promise.resolve(data)
			})
		}
	}

	const detailModalVisible = ref(false)
	const detailLoading = ref(false)
	const detailList = ref([])
	const selectedDetailIds = ref([])
	const currentDetailType = ref({})
	const selectedResourceMap = ref({})

	const RES_COLOR_MAP = {
		SUBORDINATE: 'purple',
		ROLE: 'blue',
		ORG_DIRECTOR: 'orange',
		TODO_TASK: 'cyan',
		FUTURE_TASK: 'green',
		DONE_TASK: 'red'
	}

	const resourceColumns = [
		{ title: '资源类型', dataIndex: 'resourceTypeName', width: '35%' },
		{ title: '全部转移', dataIndex: 'transferAll', width: '13%', align: 'center' },
		{ title: '部分选择', dataIndex: 'selectAction', width: '13%', align: 'center' },
		{ title: '已选', dataIndex: 'selectedCount', width: '13%', align: 'center' },
		{ title: '总计', dataIndex: 'totalCount', width: '13%', align: 'center' }
	]

	const detailColumns = computed(() => {
		return currentDetailType.value.detailColumns || [{ title: '名称', dataIndex: 'name' }]
	})

	const isAllChecked = computed(() => {
		const available = resourceList.value.filter((r) => r.totalCount > 0)
		return available.length > 0 && available.every((r) => r.checked)
	})

	const toggleAllChecked = (checked) => {
		resourceList.value.forEach((r) => {
			if (r.totalCount > 0) {
				r.checked = checked
				if (checked) {
					selectedResourceMap.value[r.resourceType] = []
				}
			}
		})
	}

	const canExecute = computed(() => {
		if (transferMode.value !== 'DELETE' && !targetUser.value.id) return false
		return resourceList.value.some((r) => r.checked || selectedResourceMap.value[r.resourceType]?.length > 0)
	})

	const getResColor = (type) => RES_COLOR_MAP[type] || 'blue'

	const getSelectedCount = (record) => {
		return selectedResourceMap.value[record.resourceType]?.length || 0
	}

	const getDisplayCount = (record) => {
		return record.checked ? record.totalCount : getSelectedCount(record)
	}

	const onOpen = (record) => {
		visible.value = true
		sourceUser.value = { id: record.id, name: record.name, account: record.account }
		targetUser.value = {}
		transferMode.value = 'TRANSFER'
		selectedResourceMap.value = {}
		loadResourceList()
	}

	const onClose = () => {
		visible.value = false
		sourceUser.value = {}
		targetUser.value = {}
		resourceList.value = []
		selectedResourceMap.value = {}
	}

	const openTargetUserSelector = () => {
		targetUserSelectorRef.value.showUserPlusModal()
	}

	const targetUserCallBack = (userIds) => {
		if (userIds && userIds.length > 0) {
			userCenterApi.userCenterGetUserListByIdList({ idList: userIds }).then((data) => {
				if (data && data.length > 0) {
					targetUser.value = data[0]
				}
			})
		}
	}

	const clearTargetUser = () => {
		targetUser.value = {}
	}

	const loadResourceList = () => {
		resourceLoading.value = true
		selectedResourceMap.value = {}
		transferApi.transferResourceList({ sourceUserId: sourceUser.value.id }).then((data) => {
			resourceList.value = data.map((item) => ({
				...item,
				checked: false
			}))
			resourceLoading.value = false
		})
	}

	const onCheckAllChange = (record) => {
		if (record.checked) {
			selectedResourceMap.value[record.resourceType] = []
		}
	}

	const openResourceDetail = (record) => {
		currentDetailType.value = record
		detailModalVisible.value = true
		detailLoading.value = true
		selectedDetailIds.value = selectedResourceMap.value[record.resourceType] || []
		transferApi
			.transferResourceDetail({
				sourceUserId: sourceUser.value.id,
				resourceType: record.resourceType
			})
			.then((data) => {
				detailList.value = data
				detailLoading.value = false
			})
	}

	const onDetailSelectChange = (keys) => {
		selectedDetailIds.value = keys
	}

	const handleDetailOk = () => {
		selectedResourceMap.value[currentDetailType.value.resourceType] = [...selectedDetailIds.value]
		detailModalVisible.value = false
	}

	const handleExecute = () => {
		const modeLabel = transferMode.value === 'TRANSFER' ? '转移' : transferMode.value === 'COPY' ? '复制' : '删除'
		const items = resourceList.value
			.filter((r) => r.checked || selectedResourceMap.value[r.resourceType]?.length > 0)
			.map((r) => ({
				resourceType: r.resourceType,
				transferAll: r.checked,
				selectedIds: r.checked ? [] : selectedResourceMap.value[r.resourceType] || []
			}))

		if (items.length === 0) {
			message.warning('请至少选择一项转移内容')
			return
		}

		Modal.confirm({
			title: '确认执行',
			content: `确认将 ${sourceUser.value.name} 的 ${items.length} 项内容${modeLabel}${transferMode.value !== 'DELETE' ? '给 ' + targetUser.value.name : ''}？`,
			onOk() {
				executeLoading.value = true
				transferApi
					.transferExecute({
						sourceUserId: sourceUser.value.id,
						targetUserId: targetUser.value.id,
						transferMode: transferMode.value,
						transferItems: items
					})
					.then(() => {
						message.success('权限调整执行成功')
						onClose()
						emit('successful')
					})
					.finally(() => {
						executeLoading.value = false
					})
			}
		})
	}

	defineExpose({ onOpen })
</script>
