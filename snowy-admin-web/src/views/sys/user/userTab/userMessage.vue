<template>
	<a-row :gutter="10">
		<a-col :span="4">
			<a-menu id="userMessage" v-model:selected-keys="selectedKeys" mode="inline" @click="handleClick">
				<a-menu-item :key="messageCategory.value" v-for="messageCategory in messageCategoryList">{{
					messageCategory.label
				}}</a-menu-item>
			</a-menu>
		</a-col>
		<a-col :span="20">
			<div class="xn-mt-16">
				<s-table ref="tableRef" :columns="columns" :data="loadData" bordered :row-key="(record) => record.id">
					<template #bodyCell="{ column, record }">
						<template v-if="column.dataIndex === 'subject'">
							<ellipsis :length="40" tooltip>
								{{ record.subject }}
							</ellipsis>
						</template>
						<template v-if="column.dataIndex === 'read'">
							<span v-if="record.read" class="xn-color-d9d9d9">已读</span>
							<span v-else class="xn-color-ff4d4f">未读</span>
						</template>
						<template v-if="column.dataIndex === 'action'">
							<a-space>
								<a @click="detailRef.onOpen(record)">详情</a>
							</a-space>
						</template>
					</template>
				</s-table>
			</div>
		</a-col>
		<detail ref="detailRef" @refresh="refresh" />
	</a-row>
</template>

<script setup name="userMessage">
	import Detail from './userMessage/detail.vue'
	import userCenterApi from '@/api/sys/userCenterApi'
	import tool from '@/utils/tool'
	import { nextTick } from 'vue'

	const messageCategoryList = tool.dictList('MESSAGE_CATEGORY')
	const selectedKeys = ref(new Array(messageCategoryList[0].value))
	const tableRef = ref()
	const detailRef = ref()
	const columns = [
		{
			title: '主题',
			dataIndex: 'subject'
		},
		{
			title: '发送时间',
			dataIndex: 'createTime',
			sorter: true
		},
		{
			title: '是否已读',
			dataIndex: 'read',
			width: '100px'
		},
		{
			title: '操作',
			dataIndex: 'action',
			width: '100px'
		}
	]
	const loadData = (parameter) => {
		parameter.category = selectedKeys.value[0]
		return userCenterApi.userLoginUnreadMessagePage(parameter).then((data) => {
			return data
		})
	}
	const refresh = () => {
		tableRef.value.refresh(false)
	}
	// 点击左侧菜单切换数据查询
	const handleClick = () => {
		// 先让上面的变量赋值，咱在查询
		nextTick(() => {
			tableRef.value.refresh(true)
		})
	}
</script>

<style lang="less" scoped>
	.xn-mt-16 {
		margin-top: -16px
	}
</style>
