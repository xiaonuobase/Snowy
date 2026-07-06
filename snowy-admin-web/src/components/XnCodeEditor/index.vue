<template>
	<div class="xn-code-editor" :style="{ height: props.height }">
		<textarea ref="textareaRef"></textarea>
	</div>
</template>

<script setup name="XnCodeEditor">
	import { onMounted, ref, watch, onBeforeUnmount, nextTick } from 'vue'
	import Codemirror from 'codemirror'
	import { Form } from 'ant-design-vue'
	import 'codemirror/lib/codemirror.css'
	// 主题
	import 'codemirror/theme/material.css'
	// 模式
	import 'codemirror/mode/htmlmixed/htmlmixed'
	import 'codemirror/mode/xml/xml'
	import 'codemirror/mode/javascript/javascript'
	import 'codemirror/mode/css/css'
	// 插件
	import 'codemirror/addon/edit/closebrackets'
	import 'codemirror/addon/edit/closetag'
	import 'codemirror/addon/edit/matchbrackets'
	import 'codemirror/addon/display/placeholder'

	const props = defineProps({
		value: {
			type: String,
			default: ''
		},
		language: {
			type: String,
			default: 'htmlmixed'
		},
		placeholder: {
			type: String,
			default: '请输入内容...'
		},
		height: {
			type: String,
			default: '300px'
		},
		readOnly: {
			type: Boolean,
			default: false
		}
	})

	const emit = defineEmits(['update:value', 'change'])

	const formItemContext = Form.useInjectFormItemContext()
	const textareaRef = ref()
	let editor = null

	onMounted(() => {
		editor = Codemirror.fromTextArea(textareaRef.value, {
			mode: props.language,
			theme: 'material',
			lineNumbers: true,
			matchBrackets: true,
			autoCloseBrackets: true,
			autoCloseTags: true,
			readOnly: props.readOnly,
			placeholder: props.placeholder,
			lineWrapping: true,
			tabSize: 4,
			indentUnit: 4
		})

		editor.setValue(props.value || '')

		editor.on('change', (cm) => {
			const content = cm.getValue()
			emit('update:value', content)
			emit('change', content)
			nextTick(() => {
				formItemContext.onFieldChange()
			})
		})

		// 解决在 Tab、Drawer、Modal 中初次加载不渲染的问题
		setTimeout(() => {
			editor.refresh()
		}, 200)
	})

	const refresh = () => {
		if (editor) {
			nextTick(() => {
				editor.refresh()
			})
		}
	}

	watch(
		() => props.value,
		(newValue) => {
			if (editor && newValue !== editor.getValue()) {
				editor.setValue(newValue || '')
				// 赋值后刷新，确保长文本能正确渲染
				nextTick(() => {
					editor.refresh()
				})
			}
		}
	)

	watch(
		() => props.readOnly,
		(newValue) => {
			if (editor) {
				editor.setOption('readOnly', newValue)
			}
		}
	)

	onBeforeUnmount(() => {
		if (editor) {
			editor.toTextArea()
		}
	})

	defineExpose({
		refresh
	})
</script>

<style scoped lang="less">
	.xn-code-editor {
		border: 1px solid #455a64; // 深色边框，与 material 主题融合
		border-radius: 2px;
		overflow: hidden;
		font-size: 14px;
		line-height: 1.5;
		background-color: #263238;
	}
	:deep(.CodeMirror) {
		height: 100% !important;
		font-family: 'Fira Code', 'Courier New', Courier, monospace;
	}
	:deep(.CodeMirror-placeholder) {
		color: #607d8b !important;
	}

	/* 滚动条样式美化 */
	:deep(.CodeMirror-vscrollbar),
	:deep(.CodeMirror-hscrollbar) {
		&::-webkit-scrollbar {
			width: 8px !important;
			height: 8px !important;
		}
		&::-webkit-scrollbar-thumb {
			background-color: #546e7a !important;
			border-radius: 4px;
			&:hover {
				background-color: #78909c !important;
			}
		}
		&::-webkit-scrollbar-track {
			background-color: #263238 !important;
		}
	}

	/* 针对整体容器的滚动条美化（如果出现的话） */
	.xn-code-editor::-webkit-scrollbar {
		width: 8px;
		height: 8px;
	}
	.xn-code-editor::-webkit-scrollbar-thumb {
		background-color: #546e7a;
		border-radius: 4px;
	}
</style>
