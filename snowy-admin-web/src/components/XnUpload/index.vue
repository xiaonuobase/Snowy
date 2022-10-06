<template>
	<a-upload
		v-if="props.uploadMode === 'defaults'"
		v-model:file-list="fileList"
		name="file"
		:action="action"
		:headers="headers"
		:maxCount="props.uploadMumber"
		@change="handleChange"
	>
		<a-button>
			<upload-outlined></upload-outlined>
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
		:maxCount="props.uploadMumber"
		@change="handleChange"
	>
		<p class="ant-upload-drag-icon">
			<inbox-outlined></inbox-outlined>
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
		uploadMumber: {
			type: Number,
			default: 1,
			required: false
		}
	})
	const action = sysConfig.API_URL + props.action

	const handleChange = () => {
		let result = []
		for (let a = 0; a < props.uploadMumber; a++) {
			const file = fileList.value[a]
			if (file.status === 'done' && file.response && file.response.code === 200) {
				result.push(file.response.data)
			}
		}
		if (result.length > 0) {
			emit('uploadDone', result)
		}
	}
</script>
