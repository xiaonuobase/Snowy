<template>
	<div style="border: 1px solid #ccc">
		<Toolbar style="border-bottom: 1px solid #ccc" :editor="editorRef" :defaultConfig="toolbarConfig" :mode="mode" />
		<Editor
			:style="{ height: props.height, overflowY: 'hidden' }"
			v-model="contentValue"
			:defaultConfig="editorConfig"
			:mode="mode"
			@onCreated="handleCreated"
		/>
	</div>
</template>
<script setup name="XnEditor">
	import '@wangeditor/editor/dist/css/style.css' // 引入 css
	import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
	import fileApi from '@/api/dev/fileApi'

	const emit = defineEmits(['update:value'])

	const props = defineProps({
		value: {
			type: [String, Array],
			default: '',
			required: false
		},
		placeholder: {
			type: String,
			default: '请输入内容...'
		},
		height: {
			type: String,
			default: '500px'
		},
		mode: {
			type: String,
			default: 'default' // 或 'default' 'simple'
		},
		fileUploadFunction: {
			type: Function,
			default: undefined
		}
	})

	// 编辑器实例，必须用 shallowRef
	const editorRef = shallowRef()
	const toolbarConfig = {}
	const editorConfig = {
		MENU_CONF: {
			placeholder: props.placeholder,
			uploadImage: {
				// 自定义上传
				async customUpload(file, insertFn) {
					const param = new FormData()
					param.append('file', file)
					// 如果外部配置了文件上传，就用外部的，下面是兜底
					if (props.fileUploadFunction) {
						props
							.fileUploadFunction(param)
							.then((data) => {
								insertFn(data) // insertFn(url, alt, href)
							})
							.catch((err) => {
								console.error('err:' + err)
							})
					} else {
						fileApi.fileUploadDynamicReturnUrl(param).then((data) => {
							insertFn(data) // insertFn(url, alt, href)
						})
					}
				}
			},
			uploadVideo: {
				// 自定义上传
				async customUpload(file, insertFn) {
					const param = new FormData()
					param.append('file', file)
					// 如果外部配置了文件上传，就用外部的，下面是兜底
					if (props.fileUploadFunction) {
						props
							.fileUploadFunction(param)
							.then((data) => {
								insertFn(data) // insertFn(url, poster)
							})
							.catch((err) => {
								console.error('err:' + err)
							})
					} else {
						fileApi.fileUploadDynamicReturnUrl(param).then((data) => {
							insertFn(data) // insertFn(url, poster)
						})
					}
				}
			}
		}
	}
	// 内容 HTML
	const contentValue = ref('')
	// 监听数据回显
	watch(
		() => props.value,
		(newVal) => {
			contentValue.value = newVal
		},
		{ immediate: true, deep: true }
	)
	// 监听输入
	watch(contentValue, (newValue) => {
		emit('update:value', newValue)
	})
	// 记录 editor 实例，重要！
	const handleCreated = (editor) => {
		editorRef.value = editor
	}

	// 组件销毁时，也及时销毁编辑器
	onBeforeUnmount(() => {
		const editor = editorRef.value
		if (editor == null) return
		editor.destroy()
	})
</script>

<style scoped lang="less"></style>
