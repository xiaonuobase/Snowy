<template>
	<a-drawer
		title="导入导出"
		:width="620"
		:visible="visible"
		:destroy-on-close="true"
		:footer-style="{ textAlign: 'right' }"
		@close="onClose"
	>
		<span
			>导入数据格式严格按照系统模板进行数据录入，请点击
			<a-button type="primary" size="small" @click="downloadImportUserTemplate">下载模板</a-button>
		</span>
		<a-divider dashed />
		<div>
			<a-spin :spinning="impUploadLoading">
				<a-upload-dragger :show-upload-list="false" :custom-request="customRequestLocal">
					<p class="ant-upload-drag-icon">
						<inbox-outlined></inbox-outlined>
					</p>
					<p class="ant-upload-text">单击或拖动文件到此区域进行上传</p>
					<p class="ant-upload-hint">仅支持xls、xlsx格式文件</p>
				</a-upload-dragger>
			</a-spin>
		</div>
		<a-alert v-if="impAlertStatus" type="info" :show-icon="false" banner closable @close="onImpClose" class="mt-3">
			<template #description>
				<p>导入总数：{{ impResultData.totalCount }} 条</p>
				<p>导入成功：{{ impResultData.successCount }} 条</p>
				<div v-if="impResultData.errorCount > 0">
					<p><span style="color: red">失败条数：</span>{{ impResultData.errorCount }} 条</p>
					<a-table :dataSource="impResultErrorDataSource" :columns="impErrorColumns" size="small" />
				</div>
			</template>
		</a-alert>
	</a-drawer>
</template>

<script setup name="userImpExp">
	import userApi from '@/api/sys/userApi'

	const impUploadLoading = ref(false)
	const impAlertStatus = ref(false)
	const impResultData = ref({})
	const impResultErrorDataSource = ref([])
	// 导入
	const customRequestLocal = (data) => {
		impUploadLoading.value = true
		const fileData = new FormData()
		fileData.append('file', data.file)
		return userApi
			.userImport(fileData)
			.then((res) => {
				impAlertStatus.value = true
				impResultData.value = res
				impResultErrorDataSource.value = res.errorDetail
			})
			.finally(() => {
				impUploadLoading.value = false
			})
	}
	// 关闭导入提示
	const onImpClose = () => {
		impAlertStatus.value = false
	}
	const impErrorColumns = [
		{
			title: '索引',
			dataIndex: 'index',
			width: '80px'
		},
		{
			title: '原因',
			dataIndex: 'msg'
		}
	]
	// 定义emit事件
	const emit = defineEmits({ successful: null })
	// 默认是关闭状态
	let visible = ref(false)
	const submitLoading = ref(false)

	// 打开抽屉
	const onOpen = () => {
		visible.value = true
	}
	// 关闭抽屉
	const onClose = () => {
		visible.value = false
		// 关闭导入的提示
		onImpClose()
	}
	// 下载用户导入模板
	const downloadImportUserTemplate = () => {
		userApi.userDownloadImportUserTemplate().then((res) => {
			const blob = new Blob([res.data], { type: 'application/octet-stream;charset=UTF-8' })
			const contentDisposition = res.headers['content-disposition']
			const patt = new RegExp('filename=([^;]+\\.[^\\.;]+);*')
			const $link = document.createElement('a')
			$link.href = URL.createObjectURL(blob)
			$link.download = decodeURIComponent(patt.exec(contentDisposition)[1])
			$link.click()
			document.body.appendChild($link)
			document.body.removeChild($link) // 下载完成移除元素
			window.URL.revokeObjectURL($link.href) // 释放掉blob对象
		})
	}
	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>
