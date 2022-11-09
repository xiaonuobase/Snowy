<template>
	<a-card :bordered="false" v-if="indexShow">
		<s-table
			ref="table"
			:columns="columns"
			:data="loadDate"
			:expand-row-by-click="true"
			:alert="options.alert.show"
			bordered
			:row-key="(record) => record.id"
			:row-selection="options.rowSelection"
			:toolConfig="{ refresh: true, height: true, columnSetting: true, striped: false }"
		>
			<template #operator class="table-operator">
				<a-space>
					<a-button type="primary" @click="openConfig()">
						新建
					</a-button>
					<a-button danger @click="deleteBatchCodeGen()">删除</a-button>
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'tablePrefix'">
					{{tablePrefixFilter(record.tablePrefix)}}
				</template>
				<template v-if="column.dataIndex === 'generateType'">
					{{generateTypeFilter(record.generateType)}}
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a @click="genPreviewRef.onOpen(record)">预览</a>
					<a-divider type="vertical" />
					<a-popconfirm title="确定生成代码？" @confirm="execGen(record)">
						<a-button type="link" size="small">生成</a-button>
					</a-popconfirm>
					<a-divider type="vertical" />
					<a @click="openConfig(record)">配置</a>
					<a-divider type="vertical" />
					<a-popconfirm title="删除此信息？" @confirm="deleteCodeGen(record)">
						<a-button type="link" danger size="small">删除</a-button>
					</a-popconfirm>
				</template>
			</template>
		</s-table>
	</a-card>
	<steps v-else ref="stepsRef" @successful="table.refresh(true)" @closed="closeConfig()"/>
	<genPreview ref="genPreviewRef" />
</template>

<script setup name="genIndex">
	import { message } from 'ant-design-vue'
	import steps from './steps.vue'
	import genPreview from './preview.vue'
	import genBasicApi from '@/api/gen/genBasicApi'

	const table = ref()
	const indexShow = ref(true)
	const stepsRef = ref()
	const genPreviewRef = ref()

	const columns = [
		{
			title: '业务名',
			dataIndex: 'busName',
			ellipsis: true
		},
		{
			title: '功能名',
			dataIndex: 'functionName',
			ellipsis: true
		},
		{
			title: '类名',
			dataIndex: 'className',
			ellipsis: true
		},
		{
			title: '包名',
			dataIndex: 'packageName',
			ellipsis: true
		},
		{
			title: '作者',
			dataIndex: 'authorName',
			ellipsis: true
		},
		{
			title: '移除表前缀',
			dataIndex: 'tablePrefix',
			ellipsis: true
		},
		{
			title: '生成方式',
			dataIndex: 'generateType',
			ellipsis: true
		},
		{
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: '220px'
		}
	]
	// 表格查询 返回 Promise 对象
	const loadDate = (parameter) => {
		return genBasicApi.basicPage(parameter).then((data) => {
			return data
		})
	}
	// 列表选择配置
	let selectedRowKeys = ref([])
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
	const generateTypeFilter = (text) => {
		const array = [
			{
				label: '压缩包',
				value: 'ZIP'
			},
			{
				label: '项目内',
				value: 'PRO'
			}
		]
		return array.find((f) => f.value === text).label
	}
	const tablePrefixFilter = (text) => {
		const array = [
			{
				label: '移除',
				value: 'Y'
			},
			{
				label: '不移除',
				value: 'N'
			}
		]
		return array.find((f) => f.value === text).label
	}
	// 生成代码
	const execGen = (record) => {
		const param = {
			id: record.id
		}
		if (record.generateType === 'PRO') {
			genBasicApi.basicExecGenPro(param).then(() => {
				message.success('操作成功')
				table.value.refresh()
			})
		} else {
			// 下载压缩包
			genBasicApi.basicExecGenBiz(param).then((res) => {
				const blob = new Blob([res.data],{type: 'application/octet-stream;charset=UTF-8'});
				const contentDisposition = res.headers['content-disposition']
				const patt = new RegExp('filename=([^;]+\\.[^\\.;]+);*')
				const $link = document.createElement("a");
				$link.href = URL.createObjectURL(blob);
				$link.download = decodeURIComponent(patt.exec(contentDisposition)[1])
				$link.click();
				document.body.appendChild($link);
				document.body.removeChild($link); // 下载完成移除元素
				window.URL.revokeObjectURL($link.href); // 释放掉blob对象
			})
		}
	}
	// 删除
	const deleteCodeGen = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		genBasicApi.basicDelete(params).then(() => {
			table.value.refresh()
		})
	}
	// 批量删除
	const deleteBatchCodeGen = () => {
		if (selectedRowKeys.value.length < 1) {
			message.warning('请选择一条或多条数据')
			return false
		}
		const params = selectedRowKeys.value.map((m) => {
			return {
				id: m
			}
		})
		genBasicApi.basicDelete(params).then(() => {
			table.value.refresh()
		})
	}
	// 打开配置界面
	const openConfig = (record) => {
		indexShow.value = false
		nextTick(() => {
			stepsRef.value.configSteps(record)
		})
	}
	// 关闭配置界面
	const closeConfig = () => {
		indexShow.value = true
	}
</script>
