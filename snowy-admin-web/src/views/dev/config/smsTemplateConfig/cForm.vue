<template>
	<a-spin :spinning="loadSpinning">
		<a-table :dataSource="dataSource" :columns="columns" :pagination="false" bordered size="middle">
			<template #bodyCell="{ text, record, column }">
				<template v-if="column.dataIndex === 'templateCode'">
					<a-input
						v-model:value="record.templateCode"
						@input="handleInput($event, record)"
						placeholder="请输入模板号"
					/>
				</template>
				<template v-if="column.dataIndex === 'templateContent'">
					<div class="flex items-center">
						<span>{{ text }}</span>
						<a-button type="link" @click="copyContent(text)">
							<template #icon><copy-outlined /></template>
						</a-button>
					</div>
				</template>
			</template>
		</a-table>
		<div class="pt-3">
			<a-button type="primary" :loading="submitLoading" @click="onSubmit()">保存</a-button>
			<a-button class="xn-ml10" @click="() => resetCode()">重置</a-button>
		</div>
	</a-spin>
</template>

<script setup name="bForm">
	import { message } from 'ant-design-vue'
	import configApi from '@/api/dev/configApi'
	import { CopyOutlined } from '@ant-design/icons-vue'

	const formRef = ref()
	const formData = ref({})
	const submitLoading = ref(false)
	const loadSpinning = ref(true)
	const dataSource = ref([])
	const columns = [
		{
			title: '名称',
			dataIndex: 'remark',
			key: 'remark'
		},
		{
			title: '模板内容',
			dataIndex: 'templateContent',
			key: 'templateContent'
		},
		{
			title: '模板ID/CODE',
			dataIndex: 'templateCode',
			key: 'templateCode'
		}
	]

	// 查询此界面的配置项,并转为表单
	const param = {
		category: 'SMS_TEMPLATE_FOR_C'
	}
	configApi.configList(param).then((data) => {
		loadSpinning.value = false
		if (data) {
			dataSource.value = data.map((item) => {
				const configValueObj = JSON.parse(item.configValue)
				return {
					...item,
					templateCode: configValueObj.code,
					templateContent: configValueObj.content
				}
			})
		} else {
			message.warning('表单项不存在，请初始化数据库')
		}
	})

	// 验证并提交数据
	const onSubmit = () => {
		submitLoading.value = true
		const param = dataSource.value.map((item) => ({
			configKey: item.configKey,
			configValue: JSON.stringify({
				code: item.templateCode,
				content: item.templateContent
			})
		}))
		configApi
			.configEditForm(param)
			.then(() => {})
			.finally(() => {
				submitLoading.value = false
			})
	}
	// 复制内容
	const copyContent = (text) => {
		navigator.clipboard.writeText(text).then(() => {
			message.success('复制成功')
		})
	}
	// 输入框限制不能输入汉字
	const handleInput = (e, record) => {
		const value = e.target.value
		if (/[\u4e00-\u9fa5]/.test(value)) {
			record.templateCode = value.replace(/[\u4e00-\u9fa5]/g, '')
			message.warning('不能输入汉字')
		}
	}
	// 重置
	const resetCode = () => {
		dataSource.value = dataSource.value.map((item) => ({
			...item,
			templateCode: ''
		}))
	}
</script>
