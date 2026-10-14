<template>
	<div>
		<!-- 右下角悬浮按钮，滚动时始终可见；在弹窗内会跟随弹窗定位 -->
		<a-float-button-group shape="circle">
			<!-- 二进制模式：传了下载处理函数走调用方的下载接口（鉴权、计数、日志），否则用已拿到的内容下载 -->
			<a-float-button v-if="isDataMode && props.showDownload" tooltip="下载" @click="onDownload">
				<template #icon><download-outlined /></template>
			</a-float-button>
			<a-float-button v-else-if="!isDataMode && props.showDownload" tooltip="下载" :href="props.src" target="_blank">
				<template #icon><download-outlined /></template>
			</a-float-button>
			<a-float-button type="primary" tooltip="返回" @click="emit('goBack')">
				<template #icon><rollback-outlined /></template>
			</a-float-button>
		</a-float-button-group>
		<a-card :bordered="false" :body-style="{ padding: 0 }">
			<a-spin :spinning="loading">
				<div v-if="hasSource" :class="{ 'xn-loading-body': loading }">
					<vue-office-docx
						v-if="renderType === 'docx'"
						ref="docxRef"
						:src="docxSource"
						class="xn-ht82"
						@rendered="docxRenderedHandler"
						@error="errorHandler"
					/>
					<vue-office-excel
						v-else-if="renderType === 'excel'"
						:src="excelSource"
						:options="excelOptions"
						class="xn-ht82"
						@rendered="renderedHandler"
						@error="errorHandler"
					/>
					<template v-else-if="renderType === 'pdf'">
						<vue-pdf-embed
							v-if="pdfSource"
							annotation-layer
							text-layer
							:source="pdfSource"
							@rendered="renderedHandler"
							@renderingFailed="errorHandler"
							@loadingFailed="errorHandler"
						/>
					</template>
					<div v-else-if="renderType === 'pptx'" ref="pptxRef" class="xn-ht82 xn-pptx-wrap" />
					<img v-else-if="renderType === 'image'" :src="imageSource" class="xn-mwh" />
					<div v-else-if="renderType === 'text'">
						<a-alert
							v-if="textTruncated"
							type="info"
							show-icon
							message="内容较大，仅显示前 2MB，完整内容请下载查看"
							class="xn-text-tip"
						/>
						<pre class="xn-text-pre">{{ textContent }}</pre>
					</div>
					<a-result
						v-else-if="renderType === 'unsupported'"
						status="info"
						title="该格式暂不支持在线预览，请下载后查看"
					/>
					<a-result v-else status="warning" title="不支持预览的文件类型" />
				</div>
			</a-spin>
		</a-card>
	</div>
</template>

<script setup>
	import tool from '@/utils/tool'
	import { message } from 'ant-design-vue'
	//引入VueOfficeDocx组件
	import VueOfficeDocx from '@vue-office/docx'
	//引入相关样式
	import '@vue-office/docx/lib/index.css'
	//引入VueOfficeExcel组件
	import VueOfficeExcel from '@vue-office/excel'
	//引入相关样式
	import '@vue-office/excel/lib/index.css'
	//引Pdf组件
	// 调用组件：https://github.com/hrynko/vue-pdf-embed
	import VuePdfEmbed from 'vue-pdf-embed'
	import 'vue-pdf-embed/dist/styles/annotationLayer.css'
	import 'vue-pdf-embed/dist/styles/textLayer.css'

	// 后缀 → 渲染方式，新增格式时在这里改映射并补一个渲染分支即可
	const RENDER_TYPE_MAP = {
		docx: 'docx',
		xls: 'excel',
		xlsx: 'excel',
		pdf: 'pdf',
		pptx: 'pptx',
		png: 'image',
		jpg: 'image',
		jpeg: 'image',
		gif: 'image',
		bmp: 'image',
		webp: 'image',
		ico: 'image',
		svg: 'image',
		txt: 'text',
		log: 'text',
		md: 'text',
		csv: 'text',
		json: 'text',
		xml: 'text',
		yml: 'text',
		yaml: 'text',
		doc: 'unsupported',
		ppt: 'unsupported',
		wps: 'unsupported',
		ofd: 'unsupported'
	}
	// 文本预览最多读取的字节数（2MB）
	const TEXT_MAX_BYTES = 2 * 1024 * 1024

	const loading = ref(false)
	const emit = defineEmits({ goBack: null })
	const props = defineProps({
		// 文件地址（与 data 二选一），会自动拼接 token
		src: {
			type: String,
			default: '',
			required: false
		},
		// 文件二进制内容（Blob 或 ArrayBuffer，与 src 二选一），不再请求地址
		data: {
			type: [Blob, ArrayBuffer],
			default: null,
			required: false
		},
		// 文件类型
		fileType: {
			type: String,
			default: 'defaults',
			required: false
		},
		// 文件名，二进制模式下载时作为保存的文件名
		fileName: {
			type: String,
			default: '',
			required: false
		},
		// 是否显示下载按钮，调用方按下载权限控制
		showDownload: {
			type: Boolean,
			default: true,
			required: false
		},
		// 二进制模式的下载处理函数，传入后点击下载调用它而不是直接保存已拿到的内容
		downloadHandler: {
			type: Function,
			default: null,
			required: false
		}
	})

	const docxRef = ref()
	const pptxRef = ref()
	// PPT 查看器实例与取消控制器，切换或卸载时销毁
	let pptxViewer = null
	let pptxAbort = null
	// 二进制模式统一转成的 Blob
	const blobData = shallowRef(null)
	// 由二进制生成的临时地址，图片、Excel、下载共用，切换或卸载时释放
	const objectUrl = shallowRef('')
	// PDF 二进制内容（pdf.js 会转移其内存，每次重新生成）
	const pdfData = shallowRef(null)
	// 文本预览内容
	const textContent = ref('')
	// 文本是否被截断
	const textTruncated = ref(false)
	// 加载序号，丢弃过期的异步读取结果
	let loadSeq = 0

	// 是否为二进制模式
	const isDataMode = computed(() => !!props.data)
	// 是否有可预览的内容
	const hasSource = computed(() => isDataMode.value || !!props.src)
	// 小写后缀
	const fileSuffix = computed(() => (props.fileType || '').toLowerCase())
	// 渲染方式，地址模式不支持文本预览，保持原有提示
	const renderType = computed(() => {
		const type = RENDER_TYPE_MAP[fileSuffix.value] || 'unknown'
		return type === 'text' && !isDataMode.value ? 'unknown' : type
	})
	// 地址模式带 token 的地址，已有参数用 & 拼接，否则用 ?
	const srcWithToken = computed(() => {
		const joiner = props.src.includes('?') ? '&' : '?'
		return props.src + joiner + 'token=' + tool.data.get('TOKEN')
	})
	// Word 渲染来源
	const docxSource = computed(() => (isDataMode.value ? blobData.value : srcWithToken.value))
	// Excel 渲染来源，二进制模式传自管的临时地址，避免组件内部生成的地址无法释放
	const excelSource = computed(() => (isDataMode.value ? objectUrl.value : srcWithToken.value))
	// Excel 渲染参数，xls 需要先转换格式
	const excelOptions = computed(() => ({ xls: fileSuffix.value === 'xls' }))
	// PDF 渲染来源
	const pdfSource = computed(() => (isDataMode.value ? pdfData.value : srcWithToken.value))
	// 图片渲染来源
	const imageSource = computed(() => (isDataMode.value ? objectUrl.value : srcWithToken.value))

	// 取消未完成的 PPT 渲染并销毁查看器
	const releasePptx = () => {
		if (pptxAbort) {
			pptxAbort.abort()
			pptxAbort = null
		}
		if (pptxViewer) {
			pptxViewer.destroy()
			pptxViewer = null
		}
	}

	// 释放二进制及临时地址
	const releaseData = () => {
		releasePptx()
		if (objectUrl.value) {
			URL.revokeObjectURL(objectUrl.value)
		}
		objectUrl.value = ''
		blobData.value = null
		pdfData.value = null
		textContent.value = ''
		textTruncated.value = false
	}

	// 按 UTF-8 解码，出现乱码时按 GB18030 重新解码以兼容 Windows 下保存的中文文本
	const decodeText = (buffer) => {
		const bytes = new Uint8Array(buffer)
		const utf8Text = new TextDecoder('utf-8').decode(bytes, { stream: true })
		return utf8Text.includes('\uFFFD') ? new TextDecoder('gb18030').decode(bytes, { stream: true }) : utf8Text
	}

	// 根据当前来源与类型准备预览内容
	const loadPreview = () => {
		loadSeq += 1
		const seq = loadSeq
		releaseData()
		loading.value = false
		if (!hasSource.value) {
			return
		}
		if (isDataMode.value) {
			blobData.value = props.data instanceof Blob ? props.data : new Blob([props.data])
			objectUrl.value = URL.createObjectURL(blobData.value)
		}
		const type = renderType.value
		if (type === 'docx' || type === 'excel') {
			loading.value = true
		} else if (type === 'pdf') {
			loading.value = true
			if (isDataMode.value) {
				blobData.value.arrayBuffer().then((buffer) => {
					if (seq !== loadSeq) return
					pdfData.value = new Uint8Array(buffer)
				})
			}
		} else if (type === 'text') {
			loading.value = true
			textTruncated.value = blobData.value.size > TEXT_MAX_BYTES
			blobData.value
				.slice(0, TEXT_MAX_BYTES)
				.arrayBuffer()
				.then((buffer) => {
					if (seq !== loadSeq) return
					textContent.value = decodeText(buffer)
					loading.value = false
				})
		} else if (type === 'pptx') {
			loading.value = true
			renderPptx(seq)
		}
	}

	// 取 PPT 二进制：二进制模式直接读，地址模式按带 token 的地址下载
	const loadPptxBuffer = () => {
		if (isDataMode.value) {
			return blobData.value.arrayBuffer()
		}
		return fetch(srcWithToken.value).then((response) => {
			if (!response.ok) {
				throw new Error('文件获取失败：' + response.status)
			}
			return response.arrayBuffer()
		})
	}

	// 渲染 PPT，渲染库按需加载，不进主包；幻灯片滚动到可视区才渲染
	const renderPptx = (seq) => {
		const abort = new AbortController()
		pptxAbort = abort
		Promise.all([loadPptxBuffer(), import('@aiden0z/pptx-renderer'), nextTick()])
			.then(([buffer, { PptxViewer, RECOMMENDED_ZIP_LIMITS }]) => {
				if (seq !== loadSeq) return null
				if (!pptxRef.value) {
					throw new Error('预览容器未就绪')
				}
				return PptxViewer.open(buffer, pptxRef.value, {
					fitMode: 'contain',
					zipLimits: RECOMMENDED_ZIP_LIMITS,
					scrollContainer: pptxRef.value,
					listOptions: { windowed: true },
					signal: abort.signal,
					onSlideRendered: (index, element) => sanitizeLinks(element)
				})
			})
			.then((viewer) => {
				if (!viewer) return
				// 渲染期间已切换文件或关闭预览，直接销毁
				if (seq !== loadSeq) {
					viewer.destroy()
					return
				}
				pptxViewer = viewer
				loading.value = false
			})
			.catch((error) => {
				if (seq !== loadSeq || abort.signal.aborted) return
				console.error('PPT 渲染失败', error)
				errorHandler()
			})
	}

	watch(() => [props.src, props.data, props.fileType], loadPreview, { immediate: true })

	onBeforeUnmount(() => {
		loadSeq += 1
		releaseData()
	})

	// 渲染完成
	const renderedHandler = () => {
		loading.value = false
	}
	// 渲染失败
	const errorHandler = () => {
		loading.value = false
		message.warning('渲染失败，请尝试重新打开！')
	}
	// Word 渲染完成后处理文档内链接
	const docxRenderedHandler = () => {
		loading.value = false
		sanitizeLinks(docxRef.value && docxRef.value.$el)
	}
	// 去掉脚本链接，外部链接改为新窗口打开，避免当前页面被跳走
	const sanitizeLinks = (root) => {
		if (!root || !root.querySelectorAll) return
		root.querySelectorAll('a[href]').forEach((link) => {
			// 浏览器解析地址时会忽略空白和控制字符，判断前同样去掉
			const href = Array.from(link.getAttribute('href'))
				.filter((char) => char.charCodeAt(0) > 32)
				.join('')
				.toLowerCase()
			if (href.startsWith('javascript:')) {
				link.removeAttribute('href')
				return
			}
			// 文档内书签跳转保留在当前页
			if (href.startsWith('#')) return
			link.setAttribute('target', '_blank')
			link.setAttribute('rel', 'noopener noreferrer')
		})
	}
	// 二进制模式点击下载：优先交给调用方的下载处理函数
	const onDownload = () => {
		if (props.downloadHandler) {
			props.downloadHandler()
			return
		}
		downloadData()
	}
	// 二进制模式下载，用传入的文件名保存
	const downloadData = () => {
		if (!objectUrl.value) return
		const link = document.createElement('a')
		link.href = objectUrl.value
		link.download = props.fileName || 'file.' + fileSuffix.value
		document.body.appendChild(link)
		link.click()
		document.body.removeChild(link)
	}
</script>

<style lang="less" scoped>
	.xn-mwh {
		max-width: 100%;
	}
	.xn-ht82 {
		height: 82vh;
	}
	.xn-loading-body {
		min-height: 200px;
	}
	.xn-pptx-wrap {
		overflow: auto;
		background: #f5f5f5;
	}
	.xn-text-tip {
		margin-bottom: 8px;
	}
	.xn-text-pre {
		max-height: 82vh;
		overflow: auto;
		margin: 0;
		padding: 12px;
		background: #fafafa;
		border: 1px solid #f0f0f0;
		border-radius: 4px;
		font-family: Consolas, Monaco, 'Courier New', monospace;
		font-size: 13px;
		line-height: 1.6;
		white-space: pre;
	}
</style>
