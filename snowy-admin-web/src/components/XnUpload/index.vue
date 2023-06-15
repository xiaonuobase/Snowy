<template>
	<a-upload
		v-if="props.uploadMode === 'defaults'"
		v-model:file-list="fileList"
		name="file"
		:action="action"
		:headers="headers"
		:maxCount="props.uploadNumber"
		@change="handleChange"
	>
		<a-button>
			<upload-outlined />
			上传
		</a-button>
	</a-upload>

	<a-upload-dragger
		v-if="props.uploadMode === 'drag'"
		v-model:fileList="fileList"
		name="file"
		:multiple="true"
		:action="action"
		:headers="headers"
		:maxCount="props.uploadNumber"
		@change="handleChange"
	>
		<p class="ant-upload-drag-icon">
			<inbox-outlined />
		</p>
		<p class="ant-upload-text">单击或拖动文件到此区域上传</p>
		<p class="ant-upload-hint"></p>
	</a-upload-dragger>
</template>

<script setup name="uploadIndex">
	import tool from '@/utils/tool'
	import sysConfig from '@/config/index'
	const fileList = ref([])
	const emit = defineEmits({ uploadDone: null })
	const headers = ref({
		token: tool.data.get('TOKEN')
	})
	const props = defineProps({
		action: {
			type: String,
			default: '/dev/file/uploadDynamicReturnUrl',
			required: false
		},
		// 上传方式 defaults || drag
		uploadMode: {
			type: String,
			default: 'defaults',
			required: false
		},
		// 上传数量
		uploadNumber: {
			type: Number,
			default: 1,
			required: false
		}
	})
	const action = sysConfig.API_URL + props.action

	// 上传时间，构造上传数组，主动推送至调用组件，单文件可以使用
	const handleChange = () => {
		// 单个文件可以使用回调方法，多个文件建议主动获取
		let result = []
		for (let a = 0; a < props.uploadNumber; a++) {
			const file = fileList.value[a]
			if (file && file.status === 'done' && file.response && file.response.code === 200) {
				const resultObj = {
					name: file.name,
					url: file.response.data
				}
				result.push(resultObj)
			}
		}
		if (result.length > 0) {
			emit('uploadDone', result)
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
