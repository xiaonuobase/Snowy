<template>
	<div v-if="indexShow" class="index-wrap">
		<xn-panel>
			<a-form ref="searchFormRef" :model="searchFormState">
				<a-row :gutter="10">
					<a-col :xs="24" :sm="8" :md="8" :lg="8" :xl="8">
						<a-form-item name="searchKey" label="关键词">
							<a-input v-model:value="searchFormState.searchKey" placeholder="请输入文件名称关键词" />
						</a-form-item>
					</a-col>
					<a-col :xs="24" :sm="8" :md="8" :lg="8" :xl="8">
						<a-form-item name="engine" label="存储位置">
							<a-select
								v-model:value="searchFormState.engine"
								placeholder="请选择存储位置"
								:options="engineOptions"
								:getPopupContainer="(trigger) => trigger.parentNode"
							/>
						</a-form-item>
					</a-col>
					<a-col :xs="24" :sm="8" :md="8" :lg="8" :xl="8">
						<a-form-item>
							<a-space>
								<a-button type="primary" @click="tableRef.refresh(true)">
									<template #icon>
										<SearchOutlined />
									</template>
									查询
								</a-button>
								<a-button @click="reset">
									<template #icon>
										<redo-outlined />
									</template>
									重置
								</a-button>
							</a-space>
						</a-form-item>
					</a-col>
				</a-row>
			</a-form>
			<s-table
				ref="tableRef"
				:columns="columns"
				:data="loadData"
				:expand-row-by-click="true"
				:alert="options.alert.show"
				bordered
				:row-key="(record) => record.id"
				:row-selection="options.rowSelection"
				:scroll="{ x: 'max-content' }"
			>
				<template #operator>
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
						<a-tag :color="$TOOL.dictTypeColor('FILE_ENGINE', record.engine)">{{
							$TOOL.dictTypeData('FILE_ENGINE', record.engine)
						}}</a-tag>
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
		</xn-panel>
		<uploadForm ref="uploadFormRef" @successful="tableRef.refresh()" />
		<detail ref="detailRef" />
		<!-- 预览文件下载中：挡住列表操作，显示进度，可取消 -->
		<div v-if="previewLoading" class="preview-loading-mask">
			<a-spin />
			<div class="preview-loading-name">正在加载：{{ previewLoadingName }}</div>
			<a-progress v-if="previewPercent !== null" :percent="previewPercent" class="preview-loading-progress" />
			<div class="preview-loading-size">{{ previewLoadedText }}</div>
			<a-button size="small" @click="cancelPreview">取消</a-button>
		</div>
	</div>
	<preview v-if="!indexShow" ref="previewRef" @goBack="previewBack" />
</template>

<script setup name="devFile">
	import fileApi from '@/api/dev/fileApi'
	import UploadForm from './uploadForm.vue'
	import Detail from './detail.vue'
	import Preview from './preview.vue'
	import tool from '@/utils/tool'
	import downloadUtil from '@/utils/downloadUtil'
	import { message } from 'ant-design-vue'

	// 可在页面内预览的后缀（与预览组件能渲染的格式一致；doc、ppt 等旧格式组件不支持）
	const PREVIEW_SUFFIX_LIST = [
		...['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp', 'ico'],
		...['pdf', 'docx', 'xls', 'xlsx', 'pptx'],
		...['txt', 'log', 'md', 'csv', 'json', 'xml', 'yml', 'yaml']
	]
	// Office 类后缀，渲染开销大，单独限制大小
	const PREVIEW_OFFICE_SUFFIX_LIST = ['docx', 'xls', 'xlsx']
	// 预览大小上限（字节）：Office 20MB，其他 50MB
	const PREVIEW_OFFICE_MAX_SIZE = 20 * 1024 * 1024
	const PREVIEW_MAX_SIZE = 50 * 1024 * 1024

	// 定义tableDOM
	const tableRef = ref()
	const formRef = ref()
	const searchFormRef = ref()
	const searchFormState = ref({})
	const uploadFormRef = ref()
	const detailRef = ref()
	const previewRef = ref()
	const indexShow = ref(true)
	// 预览文件是否正在下载
	const previewLoading = ref(false)
	// 正在下载的预览文件名
	const previewLoadingName = ref('')
	// 预览文件已下载字节数
	const previewLoaded = ref(0)
	// 预览文件总字节数，取响应的文件大小，未知时为0
	const previewTotal = ref(0)
	// 取消当前预览请求的控制器
	let previewController = null
	// 下载百分比，总大小未知时不显示进度条
	const previewPercent = computed(() =>
		previewTotal.value ? Math.floor((previewLoaded.value / previewTotal.value) * 100) : null
	)
	// 字节数转为可读大小
	const formatBytes = (bytes) =>
		bytes >= 1024 * 1024 ? (bytes / 1024 / 1024).toFixed(1) + ' MB' : Math.ceil(bytes / 1024) + ' KB'
	// 已下载大小文案
	const previewLoadedText = computed(() =>
		previewTotal.value
			? `${formatBytes(previewLoaded.value)} / ${formatBytes(previewTotal.value)}`
			: `已下载 ${formatBytes(previewLoaded.value)}`
	)

	const columns = [
		{
			title: '文件名称',
			dataIndex: 'name',
			ellipsis: true
		},
		{
			title: '缩略图',
			dataIndex: 'thumbnail',
			ellipsis: true
		},
		{
			title: '文件大小',
			dataIndex: 'sizeInfo',
			ellipsis: true
		},
		{
			title: '文件后缀',
			dataIndex: 'suffix',
			ellipsis: true
		},
		{
			title: '储存引擎',
			dataIndex: 'engine',
			ellipsis: true
		},
		{
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			fixed: 'right'
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
	// 判断是否显示预览按钮（与预览组件能渲染的格式一致）
	const previewDisplay = (record) => {
		return !!record.suffix && PREVIEW_SUFFIX_LIST.includes(record.suffix.toLowerCase())
	}
	// 预览：先带进度下载文件内容，再交给预览组件渲染
	const onPreview = (record) => {
		const suffix = (record.suffix || '').toLowerCase()
		const maxSize = PREVIEW_OFFICE_SUFFIX_LIST.includes(suffix) ? PREVIEW_OFFICE_MAX_SIZE : PREVIEW_MAX_SIZE
		// 超过预览大小上限的不在页面内渲染，避免浏览器卡顿（sizeKb 单位为KB）
		if (Number(record.sizeKb) * 1024 > maxSize) {
			message.warning('文件较大，请下载后查看')
			return
		}
		// 上一个预览还在加载时先取消，只显示最后点击的文件
		cancelPreview()
		const controller = new AbortController()
		previewController = controller
		previewLoadingName.value = record.name
		previewLoaded.value = 0
		previewTotal.value = 0
		previewLoading.value = true
		let finished = false
		// 大文件耗时长，不设超时，由用户自行取消
		fileApi
			.fileAuthDownload(
				{ id: record.id },
				{
					signal: controller.signal,
					timeout: 0,
					onDownloadProgress: (event) => {
						previewLoaded.value = event.loaded
						previewTotal.value = event.total || 0
					}
				}
			)
			.then((res) => {
				finished = true
				// 非200时拦截器已提示并返回空
				if (!res) return
				// 出错时后端以JSON返回错误信息，交给下载工具解析并提示
				if (res.data.type && res.data.type.includes('json')) {
					downloadUtil.resultDownload(res)
					return
				}
				indexShow.value = false
				nextTick(() => {
					previewRef.value.onOpen(record, res.data)
				})
			})
			.finally(() => {
				// 已被取消或被新预览替换的请求不再改动加载状态
				if (previewController !== controller) return
				previewController = null
				previewLoading.value = false
				// 网络中断等失败全局只记控制台，这里补一句提示
				if (!finished) {
					message.error('文件加载失败，请稍后重试')
				}
			})
	}
	// 取消正在加载的预览
	const cancelPreview = () => {
		if (previewController) {
			previewController.abort()
			previewController = null
		}
		previewLoading.value = false
	}
	// 预览返回
	const previewBack = () => {
		indexShow.value = true
	}
	// 关闭页面时中止未完成的预览下载
	onBeforeUnmount(() => {
		cancelPreview()
	})
	// 删除
	const deleteFile = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		fileApi.fileDelete(params).then(() => {
			tableRef.value.refresh()
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
	.index-wrap {
		position: relative;
		height: 100%;
	}
	.preview-loading-mask {
		position: absolute;
		inset: 0;
		z-index: 10;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		gap: 12px;
		background: rgba(255, 255, 255, 0.75);
	}
	.preview-loading-name {
		max-width: 60%;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}
	.preview-loading-progress {
		width: 320px;
	}
	.preview-loading-size {
		color: rgba(0, 0, 0, 0.45);
		font-size: 12px;
	}
	.record-img {
		width: 40px;
		height: 40px;
	}
</style>
