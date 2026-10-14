# 小诺文件预览的组件

## 说明

该组件支持图片、PDF、Word、Excel、PPT在线预览，其他类型的不支持

- Word 仅支持 `.docx`；Excel 支持 `.xls`、`.xlsx`；PPT 仅支持 `.pptx`（渲染库 @aiden0z/pptx-renderer，打开时按需加载，幻灯片滚动到可视区才渲染）
- `.doc`、`.ppt`、`.wps`、`.ofd` 会提示「该格式暂不支持在线预览，请下载后查看」
- 文本类（txt/log/md/csv/json/xml/yml/yaml）仅二进制模式支持，超过 2MB 只显示前 2MB
- 下载、返回为右下角悬浮圆形按钮（a-float-button），滚动时始终可见；放在弹窗内时跟随弹窗定位

## 使用

使用的时候直接在界面引入即可，配置相关参数就能行

组件有两种用法，`src` 与 `data` 二选一：

1. 地址模式：传 `src`，组件自动在地址后拼接 `token`（已含 `?` 用 `&`，否则用 `?`）
2. 二进制模式：传 `data`（Blob 或 ArrayBuffer），组件直接渲染已拿到的内容，不再请求地址、不拼接 token；适合需要后端鉴权、记录日志的预览接口

### props定义

| 序号 | 编码       | 类型                | 说明                                   | 默认       |
|----|----------|-------------------|--------------------------------------|----------|
| 1  | src      | String            | 文件地址（与 data 二选一）                     | ''       |
| 2  | data     | Blob, ArrayBuffer | 文件二进制内容（与 src 二选一）                   | null     |
| 3  | fileType | String            | 文件类型（后缀，如 docx、pdf）                  | defaults |
| 4  | fileName | String            | 文件名，二进制模式点下载按钮时作为保存的文件名              | ''       |
| 5  | showDownload | Boolean       | 是否显示下载按钮，调用方按下载权限控制                       | true     |
| 6  | downloadHandler | Function   | 二进制模式点下载时调用它（走正式下载接口：鉴权、计数、日志），不传则直接保存已拿到的内容 | null     |

### emits定义

| 序号 | 方法名    | 说明         |
|----|--------|------------|
| 1  | goBack | 点击返回按钮时触发 |

### 二进制模式示例

先用 `responseType: 'blob'` 的接口拿到文件流，再把 Blob 交给组件：

```vue
<template>
	<xn-file-preview v-if="previewData" :data="previewData" :file-type="fileType" :file-name="fileName" @goBack="onBack" />
</template>

<script setup>
	import fileApi from '@/api/xxx/fileApi'
	const previewData = shallowRef(null)
	const fileType = ref('')
	const fileName = ref('')
	const onPreview = (record) => {
		fileApi.preview({ id: record.id }).then((res) => {
			previewData.value = res.data
			fileType.value = record.suffix
			fileName.value = record.name
		})
	}
	// 返回时清空，释放内存
	const onBack = () => {
		previewData.value = null
	}
</script>
```

注意：后端以 JSON 返回错误时 `res.data.type` 含 `json`，应先交给 `downloadUtil.resultDownload(res)` 提示错误，不要传给组件。
