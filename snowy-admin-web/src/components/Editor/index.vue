<template>
	<Editor v-model="contentValue" :init="init" :disabled="disabled" :placeholder="placeholder" @onClick="onClick" />
</template>

<script>
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

	export default {
		components: {
			Editor
		},
		props: {
			modelValue: {
				type: String,
				default: ''
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
			}
		},
		data() {
			return {
				init: {
					language_url: '/tinymce/langs/zh_CN.js',
					language: 'zh_CN',
					skin_url: '/tinymce/skins/ui/oxide',
					content_css: '/tinymce/skins/content/default/content.css',
					menubar: false,
					statusbar: true,
					plugins: this.plugins,
					toolbar: this.toolbar,
					fontsize_formats: '12px 14px 16px 18px 20px 22px 24px 28px 32px 36px 48px 56px 72px',
					height: this.height,
					placeholder: this.placeholder,
					branding: false,
					resize: true,
					elementpath: true,
					content_style: '',
					images_upload_handler(blobInfo, progress) {
						return new Promise((resolve, reject) => {
							const data = new FormData()
							data.append('file', blobInfo.blob(), blobInfo.filename())
							fileApi.fileUploadDynamicReturnUrl(data).then((res) => {
								return resolve(res)
							}).catch((err) => {
								return reject('err:' + err)
							})
						})
					},
					setup: (editor) => {
						editor.on('init', function () {
							this.getBody().style.fontSize = '14px'
						})
					}
				},
				contentValue: this.modelValue
			}
		},
		watch: {
			modelValue(val) {
				this.contentValue = val
			},
			contentValue(val) {
				this.$emit('update:modelValue', val)
			}
		},
		mounted() {
			tinymce.init({})
		},
		methods: {
			onClick(e) {
				this.$emit('onClick', e, tinymce)
			}
		}
	}
</script>
