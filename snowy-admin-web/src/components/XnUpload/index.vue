<template>
	<div>
		<a-upload
			v-if="props.uploadMode === 'file'"
			v-model:file-list="fileList"
			name="file"
			:action="action"
			:headers="headers"
			:maxCount="props.uploadNumber"
			:progress="progress"
			@change="handleChange"
			:showUploadList="props.showUploadList"
			:accept="accept"
		>
			<a-button>
				<upload-outlined />
				{{ props.uploadText }}
			</a-button>
		</a-upload>

		<a-upload
			v-if="props.uploadMode === 'image'"
			v-model:file-list="fileList"
			name="file"
			:action="action"
			:headers="headers"
			:maxCount="props.uploadNumber"
			:before-upload="beforeUpload"
			list-type="picture-card"
			@change="handleChange"
			@preview="handlePreview"
			:progress="progress"
			:showUploadList="props.showUploadList"
			:accept="accept"
		>
			<div class="clearfix" v-if="fileList.length < props.uploadNumber">
				<plus-outlined />
				<div style="margin-top: 8px">{{ props.uploadText }}</div>
			</div>
		</a-upload>
		<a-modal
			v-if="props.uploadMode === 'image'"
			:open="previewVisible"
			:title="previewTitle"
			:footer="null"
			@cancel="handleCancel"
		>
			<img alt="example" style="width: 100%" :src="previewImage" />
		</a-modal>

		<a-upload-dragger
			v-if="props.uploadMode === 'drag'"
			v-model:fileList="fileList"
			name="file"
			:multiple="true"
			:action="action"
			:headers="headers"
			:maxCount="props.uploadNumber"
			@change="handleChange"
			:progress="progress"
			:showUploadList="props.showUploadList"
			:accept="accept"
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
	import sysConfig from '@/config/index'
	import { convertUrl } from '@/utils/apiAdaptive'
	import { message, Upload } from 'ant-design-vue'
	import { cloneDeep } from 'lodash-es'
	const fileList = ref([])
	const emit = defineEmits(['update:value', 'onChange'])
	const previewVisible = ref(false)
	const previewTitle = ref('')
	const previewImage = ref('')
	const headers = ref({
		token: tool.data.get('TOKEN')
	})
	const accept = ref('')
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
		// 上传样式或图片方式 file || drag || image
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
		}
	})
	const action =
		props.uploadResultType === 'id'
			? sysConfig.API_URL + props.uploadReturnIdApi
			: sysConfig.API_URL + props.uploadDynamicReturnUrlApi

	// 构造文件对象
	const buildFileObject = (url, id) => {
		return {
			data: url ? url : sysConfig.API_URL + props.uploadIdDownloadUrl + id,
			name: url ? url : id,
			url: url ? url : sysConfig.API_URL + props.uploadIdDownloadUrl + id,
			status: 'done',
			response: {
				data: url ? url : id,
				code: 200
			}
		}
	}
	// 处理回显
	const echo = (newVal) => {
		// 字符串隔离情况
		if (props.uploadResultCategory === 'interval') {
			// id隔离
			if (props.uploadResultType === 'id') {
				newVal.split(',').forEach((id) => {
					const file = buildFileObject(undefined, id)
					fileList.value.push(file)
					fileList.value.reverse()
				})
			}
			// url隔离
			if (props.uploadResultType === 'url') {
				newVal.split(',').forEach((url) => {
					const file = buildFileObject(url)
					fileList.value.push(file)
					fileList.value.reverse()
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
						e.url = sysConfig.API_URL + props.uploadIdDownloadUrl + e.response.data
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
						fileList.value.push(buildFileObject(undefined, id))
					})
				}
				// url数组
				if (props.uploadResultType === 'url') {
					newVal.forEach((url) => {
						fileList.value.push(buildFileObject(url))
					})
				}
			}
		}
	}
	// 监听参数
	watch(
		() => props.value,
		(newVal) => {
			if (props.value && newVal) {
				fileList.value = []
				echo(newVal)
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
					accept.value = props.accept
				} else {
					accept.value = 'image/*'
				}
			} else {
				accept.value = props.accept
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
		const isPNG = file.type.startsWith('image/')
		if (!isPNG) {
			message.warning('只能上传图片类型文件')
		}
		return isPNG || Upload.LIST_IGNORE
	}
	// 预览图片
	const handlePreview = async (file) => {
		previewVisible.value = true
		previewTitle.value = file.name
		// 如果返回的是id
		if (props.uploadResultType === 'id') {
			previewImage.value = sysConfig.API_URL + props.uploadIdDownloadUrl + file.response.data
		} else {
			previewImage.value = file.response.data
		}
	}
	// 关闭预览窗口
	const handleCancel = () => {
		previewVisible.value = false
		previewTitle.value = ''
		previewImage.value = ''
	}
	// 上传事件
	const handleChange = (uploads) => {
		let result = []
		const file = uploads.file
		if (file && (file.status === 'done' || file.status === 'removed') && file.response && file.response.code === 200) {
			uploads.fileList.forEach((f) => {
				result.push(f)
			})
		}
		if (result.length > 0) {
			if (props.uploadResultCategory === 'interval') {
				const resultIntervalValue = ref('')
				result.forEach((data) => {
					resultIntervalValue.value =
						data.response.data + (resultIntervalValue.value ? ',' + resultIntervalValue.value : '')
				})
				emit('update:value', resultIntervalValue)
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
					emit('onChange', newResult)
				} else {
					const resultArrayValue = ref([])
					result.forEach((data) => {
						resultArrayValue.value.push(data.response.data)
					})
					emit('update:value', resultArrayValue)
					emit('onChange', resultArrayValue)
				}
			}
			return
		}
		emit('update:value', undefined)
		emit('onChange', undefined)
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
