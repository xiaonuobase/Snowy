<template>
	<div v-if="indexShow">
		<a-card :bordered="false" class="xn-mb10">
			<a-form ref="searchFormRef" name="advanced_search" class="ant-advanced-search-form" :model="searchFormState">
				<a-row :gutter="24">
					<a-col :span="8">
						<a-form-item name="searchKey" label="名称关键词">
							<a-input v-model:value="searchFormState.searchKey" placeholder="请输入文件名称关键词" />
						</a-form-item>
					</a-col>
					<a-col :span="8">
						<a-form-item name="engine" label="存储位置">
							<a-select
								v-model:value="searchFormState.engine"
								placeholder="请选择存储位置"
								:options="engineOptions"
								:getPopupContainer="(trigger) => trigger.parentNode"
							/>
						</a-form-item>
					</a-col>
					<a-col :span="8">
						<a-button type="primary" @click="tableRef.refresh(true)">
							<template #icon><SearchOutlined /></template>
							查询
						</a-button>
						<a-button class="snowy-button-left" @click="reset">
							<template #icon><redo-outlined /></template>
							重置
						</a-button>
					</a-col>
				</a-row>
			</a-form>
		</a-card>
		<a-card :bordered="false">
			<s-table
				ref="tableRef"
				:columns="columns"
				:data="loadData"
				:expand-row-by-click="true"
				:alert="options.alert.show"
				bordered
				:row-key="(record) => record.id"
				:row-selection="options.rowSelection"
			>
				<template #operator class="table-operator">
					<a-space>
						<a-button type="primary" @click="() => uploadFormRef.openUpload()">
							<UploadOutlined />
							文件上传
						</a-button>
						<xn-batch-button
							buttonName="批量删除"
							icon="DeleteOutlined"
							buttonDanger
							:selectedRowKeys="selectedRowKeys"
							@batchCallBack="deleteBatchFile"
						/>
					</a-space>
				</template>
				<template #bodyCell="{ column, record }">
					<template v-if="column.dataIndex === 'thumbnail'">
						<img
							:src="record.thumbnail"
							class="record-img"
							v-if="
								record.suffix === 'png' ||
								record.suffix === 'jpg' ||
								record.suffix === 'jpeg' ||
								record.suffix === 'ico' ||
								record.suffix === 'bmp' ||
								record.suffix === 'gif'
							"
						/>
						<img
							src="/src/assets/images/fileImg/docx.png"
							class="record-img"
							v-else-if="record.suffix === 'doc' || record.suffix === 'docx'"
						/>
						<img
							src="/src/assets/images/fileImg/xlsx.png"
							class="record-img"
							v-else-if="record.suffix === 'xls' || record.suffix === 'xlsx'"
						/>
						<img src="/src/assets/images/fileImg/zip.png" class="record-img" v-else-if="record.suffix === 'zip'" />
						<img src="/src/assets/images/fileImg/rar.png" class="record-img" v-else-if="record.suffix === 'rar'" />
						<img
							src="/src/assets/images/fileImg/ppt.png"
							class="record-img"
							v-else-if="record.suffix === 'ppt' || record.suffix === 'pptx'"
						/>
						<img src="/src/assets/images/fileImg/pdf.png" class="record-img" v-else-if="record.suffix === 'pdf'" />
						<img src="/src/assets/images/fileImg/txt.png" class="record-img" v-else-if="record.suffix === 'txt'" />
						<img src="/src/assets/images/fileImg/html.png" class="record-img" v-else-if="record.suffix === 'html'" />
						<img src="/src/assets/images/fileImg/file.png" class="record-img" v-else />
					</template>
					<template v-if="column.dataIndex === 'engine'">
						{{ $TOOL.dictTypeData('FILE_ENGINE', record.engine) }}
					</template>
					<template v-if="column.dataIndex === 'action'">
						<a v-if="previewDisplay(record)" @click="onPreview(record)">预览</a>
						<a-divider v-if="previewDisplay(record)" type="vertical" />
						<a @click="detailRef.onOpen(record)">详情</a>
						<a-divider type="vertical" />
						<a :href="record.downloadPath" target="_blank">下载</a>
						<a-divider type="vertical" />
						<a-popconfirm title="删除此文件？" @confirm="deleteFile(record)">
							<a-button type="link" danger size="small">删除</a-button>
						</a-popconfirm>
					</template>
				</template>
			</s-table>
		</a-card>
		<uploadForm ref="uploadFormRef" @successful="tableRef.refresh(true)" />
		<detail ref="detailRef" />
	</div>
	<preview v-if="!indexShow" ref="previewRef" @goBack="previewBack" />
</template>

<script setup name="devFile">
	import fileApi from '@/api/dev/fileApi'
	import UploadForm from './uploadForm.vue'
	import Detail from './detail.vue'
	import Preview from './preview.vue'
	import tool from '@/utils/tool'

	// 定义tableDOM
	const tableRef = ref()
	const formRef = ref()
	const searchFormRef = ref()
	const searchFormState = ref({})
	const uploadFormRef = ref()
	const detailRef = ref()
	const previewRef = ref()
	const indexShow = ref(true)

	const columns = [
		{
			title: '文件名称',
			dataIndex: 'name',
			ellipsis: true
		},
		{
			title: '缩略图',
			dataIndex: 'thumbnail',
			ellipsis: true,
			width: 80
		},
		{
			title: '文件大小',
			dataIndex: 'sizeInfo',
			ellipsis: true,
			width: 120
		},
		{
			title: '文件后缀',
			dataIndex: 'suffix',
			ellipsis: true,
			width: 120
		},
		{
			title: '储存引擎',
			dataIndex: 'engine',
			ellipsis: true,
			width: 120
		},
		{
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: 220
		}
	]
	const selectedRowKeys = ref([])
	// 列表选择配置
	const options = {
		alert: {
			show: false,
			clear: () => {
				selectedRowKeys.value = ref([])
			}
		},
		rowSelection: {
			onChange: (selectedRowKey, selectedRows) => {
				selectedRowKeys.value = selectedRowKey
			}
		}
	}
	// 表格查询 返回 Promise 对象
	const loadData = (parameter) => {
		return fileApi.filePage(Object.assign(parameter, searchFormState.value)).then((data) => {
			return data
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
	// 判断是否显示预览按钮
	const previewDisplay = (record) => {
		if (!record.suffix) {
			return false
		}
		const suffix = record.suffix.toLowerCase()
		if (
			suffix === 'doc' ||
			suffix === 'docx' ||
			suffix === 'xls' ||
			suffix === 'xlsx' ||
			suffix === 'pdf' ||
			suffix === 'jpg' ||
			suffix === 'png' ||
			suffix === 'gif' ||
			suffix === 'svg' ||
			suffix === 'ico' ||
			suffix === 'tmp' ||
			suffix === 'jpeg'
		) {
			return true
		}
	}
	// 预览
	const onPreview = (record) => {
		indexShow.value = false
		nextTick(() => {
			previewRef.value.onOpen(record)
		})
	}
	// 预览返回
	const previewBack = () => {
		indexShow.value = true
	}
	// 删除
	const deleteFile = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		fileApi.fileDelete(params).then(() => {
			tableRef.value.refresh(true)
		})
	}
	// 批量删除
	const deleteBatchFile = (params) => {
		fileApi.fileDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
	// 存储位置
	const engineOptions = tool.dictList('FILE_ENGINE')
</script>

<style scoped>
	.record-img {
		width: 40px;
		height: 40px;
	}
	.ant-form-item {
		margin-bottom: 0 !important;
	}
	.snowy-button-left {
		margin-left: 8px;
	}
</style>
