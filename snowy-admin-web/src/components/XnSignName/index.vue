<template>
	<xn-form-container :visible="visible" :width="700" title="电子签名" @close="handleClear" @ok="handleOk">
		<a-row :gutter="5">
			<a-col :xs="24" :sm="24" :md="24" :lg="12" :xl="12">
				<div class="imgBox">
					<vue-esign
						ref="esignRef"
						v-model:bgColor="bgColor"
						:width="800"
						:height="400"
						:quality="1"
						:is-crop="isCrop"
						:line-width="lineWidth"
						:line-color="lineColor"
					/>
				</div>
			</a-col>
			<a-col :xs="24" :sm="24" :md="24" :lg="12" :xl="12">
				<div class="imgBox">
					<img v-if="resultImg" :src="resultImg"/>
					<a-empty v-else />
				</div>
			</a-col>
		</a-row>
		<div class="xn-mt10">
			<a-form>
				<a-row :gutter="16">
					<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
						<a-form-item label="画笔粗细：">
							<a-input-number v-model:value="lineWidth" :min="1" :max="20" />
						</a-form-item>
					</a-col>
					<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
						<a-form-item label="是否裁剪：">
							<a-checkbox v-model:checked="isCrop"></a-checkbox>
						</a-form-item>
					</a-col>
					<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
						<a-form-item>
							<a-space>
								<a-button type="primary" @click="handleGenerate">预览</a-button>
								<a-button @click="handleReset">清屏</a-button>
							</a-space>
						</a-form-item>
					</a-col>
				</a-row>
			</a-form>
		</div>
		<template #footer>
			<a-button class="xn-mr8" @click="handleClear">取消</a-button>
			<a-button type="primary" @click="handleOk">确定</a-button>
		</template>
	</xn-form-container>
</template>

<script setup>
	import { message } from 'ant-design-vue'
	import VueEsign from './vueEsign.vue'
	const visible = ref(false)
	const esignRef = ref(false)
	const resultImg = ref('')
	const isCrop = ref(false)
	const lineWidth = ref(10)
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
		esignRef.value.reset()
		resultImg.value = ''
	}
	const handleGenerate = () => {
		esignRef.value
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
		esignRef.value
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
	.imgBox {
		height: 160px;
		width: 100%;
		border: 1px solid rgb(236 236 236);
		margin-top: 10px;
	}
	.imgBox img {
		height: 160px;
		width: 100%;
	}
	.xn-mt10 {
		margin-top: 10px;
	}
</style>
