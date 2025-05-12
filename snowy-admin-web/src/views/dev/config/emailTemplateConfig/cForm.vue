<template>
	<a-spin :spinning="loadSpinning">
		<a-table :dataSource="dataSource" :columns="columns" :pagination="false" bordered size="middle">
			<template #bodyCell="{ record, column }">
				<template v-if="column.dataIndex === 'subject'">
					<a-tag :bordered="false" color="processing">{{ JSON.parse(record.configValue).subject }}</a-tag>
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a-button size="small" @click="emailPreviewRef.onOpen(JSON.parse(record.configValue).content)">
							<EyeOutlined />
							预览
						</a-button>
						<a-button type="primary" size="small" @click="emailSettingRef.onOpen(record)">
							<SettingOutlined />
							配置
						</a-button>
					</a-space>
				</template>
			</template>
		</a-table>
		<email-preview ref="emailPreviewRef" />
		<email-setting ref="emailSettingRef" @successful="onSubmit" />
	</a-spin>
</template>

<script setup name="cForm">
	import { message } from 'ant-design-vue'
	import configApi from '@/api/dev/configApi'
	import { SettingOutlined, EyeOutlined } from '@ant-design/icons-vue'
	import EmailPreview from './preview.vue'
	import EmailSetting from './setting.vue'

	const emailPreviewRef = ref()
	const emailSettingRef = ref({})
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
			title: '邮件主题',
			dataIndex: 'subject',
			key: 'subject'
		},
		{
			title: '配置',
			dataIndex: 'action',
			key: 'action',
			width: 200
		}
	]
	onMounted(() => {
		initData()
	})
	// 初始化数据
	const initData = () => {
		// 查询此界面的配置项,并转为表单
		const param = {
			category: 'EMAIL_TEMPLATE_FOR_C'
		}
		configApi.configList(param).then((data) => {
			loadSpinning.value = false
			if (data) {
				dataSource.value = data
			} else {
				message.warning('表单项不存在，请初始化数据库')
			}
		})
	}

	// 提交数据
	const onSubmit = (param) => {
		submitLoading.value = true
		configApi
			.configEditForm(param)
			.then(() => {
				initData()
			})
			.finally(() => {
				submitLoading.value = false
			})
	}
</script>
