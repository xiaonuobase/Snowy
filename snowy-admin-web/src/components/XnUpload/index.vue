<template>
	<div>
		<a-upload
			v-if="props.uploadMode === 'file'"
			v-model:file-list="fileList"
			name="file"
			:multiple="props.uploadNumber > 1"
			:action="action"
			:headers="headers"
			:maxCount="props.uploadNumber"
			:progress="progress"
			@change="handleChange"
			:showUploadList="props.showUploadList"
			:accept="acceptRef"
			:disabled="props.disabled"
		>
			<a-button>
				<upload-outlined />
				{{ props.uploadText }}
			</a-button>
		</a-upload>

		<a-upload
			v-if="props.uploadMode === 'video'"
			v-model:file-list="fileList"
			name="file"
			:multiple="props.uploadNumber > 1"
			:action="action"
			:headers="headers"
			:maxCount="props.uploadNumber"
			:before-upload="beforeUpload"
			list-type="picture-card"
			@change="handleChange"
			@preview="handlePreview"
			:progress="progress"
			:showUploadList="props.showUploadList"
			:accept="acceptRef"
			:disabled="props.disabled"
		>
			<div class="clearfix" v-if="fileList.length < props.uploadNumber">
				<plus-outlined />
				<div style="margin-top: 8px">{{ props.uploadText }}</div>
			</div>
		</a-upload>

		<a-upload
			v-if="props.uploadMode === 'image'"
			v-model:file-list="fileList"
			name="file"
			:multiple="props.uploadNumber > 1"
			:action="action"
			:headers="headers"
			:maxCount="props.uploadNumber"
			:before-upload="beforeUpload"
			list-type="picture-card"
			@change="handleChange"
			@preview="handlePreview"
			:progress="progress"
			:showUploadList="props.showUploadList"
			:accept="acceptRef"
			:disabled="props.disabled"
		>
			<div class="clearfix" v-if="fileList.length < props.uploadNumber">
				<plus-outlined />
				<div style="margin-top: 8px">{{ props.uploadText }}</div>
			</div>
		</a-upload>

		<!-- 文件预览 -->
		<a-modal :open="previewVisible" :title="previewTitle" :footer="null" :destroyOnClose="true" @cancel="handleCancel">
			<template v-if="props.uploadMode === 'image'">
				<img alt="example" style="width: 100%" :src="previewObj" />
			</template>
			<template v-else-if="props.uploadMode === 'video'">
				<video width="100%" controls type="video" id="video">
					<source :src="previewObj" type="video/mp4" />
					<object :data="previewObj" width="100%">
						<embed :src="previewObj" width="100%" />
					</object>
					您的浏览器不支持video标签哦，请联系管理员
				</video>
			</template>
			<template v-else> 暂不支持该文件预览 </template>
		</a-modal>

		<a-upload-dragger
			v-if="props.uploadMode === 'drag'"
			v-model:fileList="fileList"
			name="file"
			:multiple="props.uploadNumber > 1"
			:action="action"
			:headers="headers"
			:maxCount="props.uploadNumber"
			@change="handleChange"
			:progress="progress"
			:showUploadList="props.showUploadList"
			:accept="acceptRef"
			:disabled="props.disabled"
		>
			<p class="ant-upload-drag-icon">
				<inbox-outlined />
			</p>
			<p class="ant-upload-text">
				{{ props.uploadText }}
			</p>
			<p class="ant-upload-hint"></p>
		</a-upload-dragger>
		<slot name="explain"></slot>
	</div>
</template>

<script setup name="uploadIndex">
	import tool from '@/utils/tool'
	import fileApi from '@/api/dev/fileApi'
	import { convertUrl } from '@/utils/apiAdaptive'
	import { message, Upload } from 'ant-design-vue'
	import { cloneDeep } from 'lodash-es'
	const fileList = ref([])
	const emit = defineEmits(['update:value', 'onChange', 'onSuccessful'])
	const previewVisible = ref(false)
	const previewTitle = ref('')
	const previewObj = ref('')
	const headers = ref({
		token: tool.data.get('TOKEN')
	})
	const acceptRef = ref('')
	const props = defineProps({
		// 上传返回id
		uploadReturnIdApi: {
			type: String,
			default: convertUrl('/dev/file/uploadLocalReturnId'),
			required: false
		},
		// 上传返回url
		uploadDynamicReturnUrlApi: {
			type: String,
			default: convertUrl('/dev/file/uploadDynamicReturnUrl'),
			required: false
		},
		// 当上传接口为id的情况下，配置下载接口
		uploadIdDownloadUrl: {
			type: String,
			default: convertUrl('/dev/file/download?id='),
			required: false
		},
		// 上传样式或图片方式 file || video || drag || image
		uploadMode: {
			type: String,
			default: 'file',
			required: false
		},
		// 上传数量
		uploadNumber: {
			type: Number,
			default: 1,
			required: false
		},
		// 上传按钮文字
		uploadText: {
			type: String,
			default: '上传',
			required: false
		},
		// 上传返回id或url
		uploadResultType: {
			type: String,
			default: 'url',
			required: false
		},
		// 上传返回分类 数组或字符串逗号隔离 interval | array
		uploadResultCategory: {
			type: String,
			default: 'interval',
			required: false
		},
		// 跟antdv官方一样，是否显示文件列表
		showUploadList: {
			type: Boolean,
			default: true,
			required: false
		},
		// 跟antdv官方一样，接受上传的文件类型
		accept: {
			type: String,
			default: '',
			required: false
		},
		// 是否是完整的结果（就是文件上传返回什么，该组件返回什么，uploadResultCategory必须为array）
		completeResult: {
			type: Boolean,
			default: false,
			required: false
		},
		// 父组件传来的参数
		value: {
			type: [String, Array],
			default: undefined,
			required: false
		},
		// 组件禁用状态
		disabled: {
			type: Boolean,
			default: false,
			required: false
		}
	})
	const action =
		props.uploadResultType === 'id' ? '/api' + props.uploadReturnIdApi : '/api' + props.uploadDynamicReturnUrlApi

	// 构造文件对象
	const buildFileObject = (url, id) => {
		let name = id
		if (url) {
			// 提取最后一段路径
			name = url.substring(url.lastIndexOf('/') + 1)
			// 去除查询参数
			if (name.includes('?')) {
				name = name.split('?')[0]
			}
			// 特殊处理：如果是 download 接口且带 id 参数，尝试提取 id 作为备选名称
			if ((name === 'download' || !name) && url.includes('id=')) {
				const match = url.match(/[?&]id=([^&]+)/)
				if (match) {
					name = match[1]
				}
			}
		}
		return {
			data: url ? url : '/api' + props.uploadIdDownloadUrl + id,
			name: name,
			url: url ? url : '/api' + props.uploadIdDownloadUrl + id,
			status: 'done',
			response: {
				data: url ? url : id,
				code: 200
			}
		}
	}
	// 处理回显
	const echo = (newVal) => {
		const oldFileList = cloneDeep(fileList.value)
		fileList.value = []
		const idsToFetch = []
		const urlsToFetch = []

		// 字符串隔离情况
		if (props.uploadResultCategory === 'interval') {
			// id隔离
			if (props.uploadResultType === 'id') {
				newVal.split(',').forEach((id) => {
					const existing = oldFileList.find((f) => f.response?.data === id)
					if (existing) {
						fileList.value.push(existing)
					} else {
						const file = buildFileObject(undefined, id)
						fileList.value.push(file)
						idsToFetch.push(id)
					}
				})
			}
			// url隔离
			if (props.uploadResultType === 'url') {
				newVal.split(',').forEach((url) => {
					const existing = oldFileList.find((f) => f.url === url || f.response?.data === url)
					if (existing) {
						fileList.value.push(existing)
					} else {
						const file = buildFileObject(url)
						fileList.value.push(file)
						urlsToFetch.push(url)
					}
				})
			}
		}
		// 如果是数组的情况下
		if (props.uploadResultCategory === 'array') {
			if (props.completeResult) {
				// 得去掉数组里面的thumbUrl，一个base64太大，无用
				let newResult = cloneDeep(newVal)
				newResult.map((e) => {
					if (e.thumbUrl) {
						delete e.thumbUrl
					}
					if (props.uploadResultType === 'id') {
						e.url = '/api' + props.uploadIdDownloadUrl + e.response.data
					}
					if (props.uploadResultType === 'url') {
						e.url = e.response.data
					}
				})
				fileList.value = newResult
			} else {
				// id数组
				if (props.uploadResultType === 'id') {
					newVal.forEach((id) => {
						const existing = oldFileList.find((f) => f.response?.data === id)
						if (existing) {
							fileList.value.push(existing)
						} else {
							const file = buildFileObject(undefined, id)
							fileList.value.push(file)
							idsToFetch.push(id)
						}
					})
				}
				// url数组
				if (props.uploadResultType === 'url') {
					newVal.forEach((url) => {
						const existing = oldFileList.find((f) => f.url === url || f.response?.data === url)
						if (existing) {
							fileList.value.push(existing)
						} else {
							const file = buildFileObject(url)
							fileList.value.push(file)
							urlsToFetch.push(url)
						}
					})
				}
			}
		}

		// 异步获取文件详情以修正名称
		if (idsToFetch.length > 0) {
			fileApi.fileGetFileListByIds(idsToFetch).then((data) => {
				if (data) {
					fileList.value.forEach((file) => {
						const detail = data.find((d) => d.id === file.response?.data)
						if (detail) {
							file.name = detail.name
						}
					})
				}
			})
		}
		if (urlsToFetch.length > 0) {
			fileApi.fileGetFileListByUrlList({ urlList: urlsToFetch }).then((data) => {
				if (data) {
					fileList.value.forEach((file) => {
						const detail = data.find((d) => d.downloadPath === file.url || d.downloadPath === file.response?.data)
						if (detail) {
							file.name = detail.name
						}
					})
				}
			})
		}
	}
	// 监听参数
	watch(
		() => props.value,
		(newVal) => {
			if (newVal) {
				echo(newVal)
			} else {
				fileList.value = []
			}
		},
		{ immediate: true, deep: true }
	)
	// 监听上传类型，从而设置对应的文件类型
	watch(
		() => props.uploadMode,
		(newVal) => {
			if (newVal && newVal === 'image') {
				if (props.accept) {
					acceptRef.value = props.accept
				} else {
					acceptRef.value = 'image/*'
				}
			} else {
				acceptRef.value = props.accept
			}
		},
		{ immediate: true, deep: true }
	)
	// 进度条
	const progress = {
		strokeWidth: 5,
		format: (percent) => parseFloat(percent.toFixed(2)) + '%'
	}
	// 这是兜底逻辑，保准只让这个image类型上传图片
	const beforeUpload = (file) => {
		if (props.uploadMode == 'image') {
			const isPNG = file.type.startsWith('image/')
			if (!isPNG) {
				message.warning('只能上传图片类型文件')
			}
			return isPNG || Upload.LIST_IGNORE
		} else if (props.uploadMode == 'video') {
			const isVideo = file.type.startsWith('video/')
			if (!isVideo) {
				message.warning('只能上传视频类型文件')
			}
			return isVideo || Upload.LIST_IGNORE
		}
	}
	// 预览资源
	const handlePreview = async (file) => {
		previewVisible.value = true
		previewTitle.value = file.name
		// 如果返回的是id
		if (props.uploadResultType === 'id') {
			previewObj.value = '/api' + props.uploadIdDownloadUrl + file.response.data
		} else {
			previewObj.value = file.response.data
		}
	}
	// 关闭预览窗口
	const handleCancel = () => {
		previewVisible.value = false
		previewTitle.value = ''
		previewObj.value = ''
	}
	// 上传事件
	const handleChange = (uploads) => {
		const file = uploads.file
		if (file.status === 'done' || file.status === 'removed') {
			if (file.status === 'done' && file.response && file.response.code !== 200) {
				message.error(file.response.message || '上传失败')
				return
			}
			const result = uploads.fileList.filter((f) => f.status === 'done' && f.response && f.response.code === 200)
			if (result.length > 0) {
				if (props.uploadResultCategory === 'interval') {
					let resultIntervalValue = ''
					result.forEach((data) => {
						resultIntervalValue = (resultIntervalValue ? resultIntervalValue + ',' : '') + data.response.data
					})
					emit('update:value', resultIntervalValue)
					emit('onSuccessful', resultIntervalValue)
					emit('onChange', resultIntervalValue)
				} else if (props.uploadResultCategory === 'array') {
					if (props.completeResult) {
						// 得去掉数组里面的thumbUrl，一个base64太大，无用
						let newResult = cloneDeep(result)
						newResult.map((e) => {
							if (e.thumbUrl) {
								delete e.thumbUrl
							}
						})
						emit('update:value', newResult)
						emit('onSuccessful', newResult)
						emit('onChange', newResult)
					} else {
						let resultArrayValue = []
						result.forEach((data) => {
							resultArrayValue.push(data.response.data)
						})
						emit('update:value', resultArrayValue)
						emit('onSuccessful', resultArrayValue)
						emit('onChange', resultArrayValue)
					}
				}
				return
			}
			emit('update:value', undefined)
			emit('onChange', undefined)
		}
	}
	// 通过DOM获取上传的文件
	const uploadFileList = () => {
		if (fileList.value) {
			const result = []
			// 只返回这些就够用了，其他基本用不到
			fileList.value.forEach((item) => {
				const obj = {
					name: item.name,
					type: item.type,
					size: item.size,
					url: item.response.data
				}
				result.push(obj)
			})
			return result
		} else {
			return []
		}
	}
	// 抛出这个获取文件列表的方法
	defineExpose({
		uploadFileList
	})
</script>
