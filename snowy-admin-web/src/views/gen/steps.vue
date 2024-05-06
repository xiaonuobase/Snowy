<template>
	<div>
		<a-card class="steps-card" :bordered="false">
			<a-row class="xn-row">
				<a-col :span="6"></a-col>
				<a-col :span="12">
					<a-steps :current="current">
						<a-step v-for="item in steps" :key="item.title" :title="item.title" />
					</a-steps>
				</a-col>
				<a-col :span="6">
					<div class="xn-fdr">
						<a-button :disabled="current === 0" class="xn-ml8" @click="prev"> 上一步 </a-button>
						<a-button :disabled="current === 2" type="primary" class="xn-ml8" @click="next"> 继续 </a-button>
						<a-button type="primary" danger ghost class="xn-ml8" @click="emit('closed')"> 关闭 </a-button>
					</div>
				</a-col>
			</a-row>
		</a-card>

		<div v-if="current === 0">
			<basic ref="basicRef" />
		</div>
		<div v-if="current === 1">
			<config ref="configRef" />
		</div>
		<div v-if="current === 2">
			<a-card>
				<a-result status="success" title="操作成功" sub-title="此刻可预览代码，同时您可以一键生成代码啦">
					<template #extra>
						<a-space size="middle">
							<a-button v-if="current > 0" class="xn-ml8" @click="genPreviewRef.onOpen(recordData)">预览</a-button>
							<a-button
								v-if="current === steps.length - 1"
								type="primary"
								:loading="submitLoading"
								@click="seveGenerate"
								>生成并关闭</a-button
							>
						</a-space>
					</template>
				</a-result>
			</a-card>
			<genPreview ref="genPreviewRef" />
		</div>
	</div>
</template>
<script setup name="genSteps">
	import { message } from 'ant-design-vue'
	import downloadUtil from '@/utils/downloadUtil'
	import basic from './basic.vue'
	import config from './config.vue'
	import genPreview from './preview.vue'
	import genBasicApi from '@/api/gen/genBasicApi'

	const emit = defineEmits({ closed: null })
	const current = ref(0)
	const recordData = ref()
	const submitLoading = ref(false)
	const basicRef = ref()
	const configRef = ref()
	const genPreviewRef = ref()
	// 打开这个界面
	const configSteps = (record) => {
		basicRef.value.onOpen(record)
	}
	// 下一步
	const next = () => {
		current.value++
		// 判断是哪一步
		if (current.value === 1) {
			basicRef.value
				.onSubmit()
				.then((data) => {
					recordData.value = data
					current.value++
					nextTick(() => {
						configRef.value.onOpen(data)
					})
				})
				.catch(() => {})
			current.value--
		}
		if (current.value === 2) {
			configRef.value
				.onSubmit(recordData.value)
				.then((data) => {
					current.value++
				})
				.catch((err) => {
					message.warning(err)
				})
			current.value--
		}
	}
	// 上一步
	const prev = () => {
		current.value--
		if (current.value === 0) {
			nextTick(() => {
				basicRef.value.onOpen(recordData.value)
			})
		}
		if (current.value === 1) {
			nextTick(() => {
				configRef.value.onOpen(recordData.value)
			})
		}
	}
	// 分布步骤数据
	const steps = [
		{
			title: '基础信息',
			content: '基础信息'
		},
		{
			title: '详细配置',
			content: '详细配置'
		},
		{
			title: '完成',
			content: '已经配置好代码生成，现在可以生成代码啦'
		}
	]
	// 生成代码
	const seveGenerate = () => {
		const param = {
			id: recordData.value.id
		}
		if (recordData.value.generateType === 'PRO') {
			genBasicApi.basicExecGenPro(param).then(() => {
				message.success('操作成功')
				emit('closed')
			})
		} else {
			// 下载压缩包
			genBasicApi.basicExecGenBiz(param).then((res) => {
				downloadUtil.resultDownload(res)
				emit('closed')
			})
		}
	}
	// 抛出钩子
	defineExpose({
		configSteps
	})
</script>
<style scoped>
	.steps-card {
		margin-top: -12px;
		margin-left: -12px;
		margin-right: -12px;
		margin-bottom: 10px;
		padding-top: -10px;
	}
	.xn-row {
		margin-bottom: -10px;
		margin-top: -10px;
	}
	.xn-ml8 {
		margin-left: 8px;
	}
</style>
