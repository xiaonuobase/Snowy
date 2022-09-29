<template>
	<a-modal ref="signModel" v-model:visible="visible" :width="600" title="电子签名" @cancel="handleClear" @ok="handleOk">
		<a-row :gutter="5">
			<a-col :span="15">
				<div style="border: 1px solid rgb(236 236 236)">
					<vue-esign
						ref="esign"
						v-model:bgColor="bgColor"
						:width="800"
						:height="400"
						:is-crop="isCrop"
						:line-width="lineWidth"
						:line-color="lineColor"
					/>
				</div>
			</a-col>
			<a-col :span="9">
				<div style="height: 90px; width: auto">
					<img :src="resultImg" style="height: 90px; width: 100%; border: 1px solid rgb(236 236 236)" />
				</div>
			</a-col>
		</a-row>
		<div style="margin-top: 10px">
			<a-space>
				<a-form>
					<a-row :gutter="16">
						<a-col :span="12">
							<a-form-item label="画笔粗细：">
								<a-input-number v-model:value="lineWidth" :min="1" :max="20" />
							</a-form-item>
						</a-col>
						<a-col :span="12">
							<a-form-item>
								<div style="padding-right: 50px">是否裁剪：<a-checkbox v-model:checked="isCrop"></a-checkbox></div>
							</a-form-item>
						</a-col>
					</a-row>
				</a-form>
				<a-button type="primary" @click="handleGenerate">预览</a-button>
				<a-button @click="handleReset">清屏</a-button>
			</a-space>
		</div>
	</a-modal>
</template>

<script setup>
	import { message } from 'ant-design-vue'
	import vueEsign from './vueEsign.vue'
	const signModel = ref(false)
	const visible = ref(false)
	const esign = ref(false)
	const resultImg = ref('')
	const isCrop = ref(false)
	const lineWidth = ref(6)
	const lineColor = ref('#000000')
	const bgColor = ref('')
	const props = defineProps(['image'])
	// eslint-disable-next-line vue/no-setup-props-destructure
	resultImg.value = props.image
	const emit = defineEmits({ successful: null })
	const show = () => {
		visible.value = true
	}
	const handleReset = () => {
		esign.value.reset()
		resultImg.value = ''
	}
	const handleGenerate = () => {
		esign.value
			.generate()
			.then((res) => {
				resultImg.value = res
			})
			.catch(() => {
				message.warning('无任何签字')
			})
	}
	const handleClear = () => {
		visible.value = false
	}
	const handleOk = () => {
		esign.value
			.generate()
			.then((res) => {
				emit('successful', res)
				handleClear()
			})
			.catch(() => {
				message.warning('无任何签字')
			})
	}
	defineExpose({
		show
	})
</script>

<style scoped>
	.ant-form-item {
		margin-bottom: 0px !important;
	}
</style>
