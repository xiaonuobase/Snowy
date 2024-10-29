<template>
	<Editor v-model="contentValue" :init="init" :disabled="disabled" :placeholder="placeholder" @onClick="onClick" />
</template>

<script setup name="Editor">
	import fileApi from '@/api/dev/fileApi'
	import Editor from '@tinymce/tinymce-vue'
	import tinymce from 'tinymce/tinymce'
	import 'tinymce/themes/silver'
	import 'tinymce/icons/default'
	import 'tinymce/models/dom'
	// 引入编辑器插件
	import 'tinymce/plugins/code' // 编辑源码
	import 'tinymce/plugins/image' // 插入编辑图片
	import 'tinymce/plugins/link' // 超链接
	import 'tinymce/plugins/preview' // 预览
	import 'tinymce/plugins/table' // 表格
	import 'tinymce/plugins/lists' // 列表编号
	import 'tinymce/plugins/advlist' //高级列表编号

	const emit = defineEmits(['update:value', 'onClick', 'onChange'])
	const props = defineProps({
		value: {
			type: [String, Array],
			default: '',
			required: false
		},
		placeholder: {
			type: String,
			default: ''
		},
		height: {
			type: Number,
			default: 300
		},
		disabled: {
			type: Boolean,
			default: false
		},
		plugins: {
			type: [String, Array],
			default: 'code image link preview table lists advlist'
		},
		toolbar: {
			type: [String, Array],
			default:
				'undo redo |  forecolor backcolor bold italic underline strikethrough link | blocks fontfamily fontsize | \
				alignleft aligncenter alignright alignjustify outdent indent lineheight | bullist numlist | \
				image table  preview | code selectall'
		},
		fileUploadFunction: {
			type: Function,
			default: undefined
		}
	})
	const contentValue = ref()
	const init = ref({
		language_url: '/tinymce/langs/zh_CN.js',
		language: 'zh_CN',
		skin_url: '/tinymce/skins/ui/oxide',
		content_css: '/tinymce/skins/content/default/content.css',
		menubar: false,
		statusbar: true,
		plugins: props.plugins,
		toolbar: props.toolbar,
		fontsize_formats: '12px 14px 16px 18px 20px 22px 24px 28px 32px 36px 48px 56px 72px',
		height: props.height,
		placeholder: props.placeholder,
		branding: false,
		resize: true,
		elementpath: true,
		content_style: '',
		selector: '#textarea1',
		skin: 'oxide-dark',
		images_upload_handler(blobInfo, progress) {
			return new Promise((resolve, reject) => {
				const param = new FormData()
				param.append('file', blobInfo.blob(), blobInfo.filename())
				// 如果外部配置了文件上传，就用外部的，下面是兜底
				if (props.fileUploadFunction) {
					props
						.fileUploadFunction(param)
						.then((data) => {
							return resolve(data)
						})
						.catch((err) => {
							return reject('err:' + err)
						})
				} else {
					fileApi.fileUploadDynamicReturnUrl(param).then((data) => {
						return resolve(data)
					})
				}
			})
		},
		setup: (editor) => {
			editor.on('init', () => {
				// getBody().style.fontSize = '14px'
			})
		}
	})
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
	const onClick = (e) => {
		emit('onClick', e, tinymce)
	}
	onMounted(() => {
		tinymce.init({})
	})
</script>
<style lang="less">
	.tox-toolbar__primary {
		border-bottom: 1px solid rgb(5 5 5 / 0%) !important;
	}
</style>
