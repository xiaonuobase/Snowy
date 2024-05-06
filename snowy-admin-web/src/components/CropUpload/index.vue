<template>
	<a-modal v-model:open="visible" :width="700" title="头像裁剪" @cancel="handleClear" @ok="cropOk">
		<a-row :gutter="10">
			<!-- 裁剪区 -->
			<a-col :span="17">
				<VueCropper
					ref="cropper"
					class="cropper"
					:img="option.img"
					:output-size="option.size"
					:output-type="option.outputType"
					:info="true"
					:full="option.full"
					:can-move="option.canMove"
					:can-move-box="option.canMoveBox"
					:fixed-box="option.fixedBox"
					:original="option.original"
					:auto-crop="option.autoCrop"
					:auto-crop-width="option.autoCropWidth"
					:auto-crop-height="option.autoCropHeight"
					:center-box="option.centerBox"
					:high="option.high"
					:info-true="option.infoTrue"
					:max-img-size="option.maxImgSize"
					@realTime="getCropdata"
				/>
			</a-col>
			<a-col :span="7">
				<div class="xn-cj">
					<a-image :src="previewUrl" />
				</div>
				<div class="xn-cj-two">
					<div>
						<a-image :src="previewUrl" />
					</div>
					<div>
						<a-image :src="previewUrl" />
					</div>
				</div>
			</a-col>
		</a-row>
		<div class="xn-tl">
			<a-space>
				<a-button @click="cropper.changeScale(1)">放大</a-button>
				<a-button @click="cropper.changeScale(-1)">缩小</a-button>
				<a-button @click="cropper.rotateLeft()">向左旋转</a-button>
				<a-button @click="cropper.rotateRight()">向右旋转</a-button>
			</a-space>
			<div class="xn-pt">
				<a-upload
					name="file"
					:show-upload-list="false"
					:custom-request="() => {}"
					accept="image/png, image/jpeg, image/gif, image/jpg"
					@change="uploadChange"
				>
					<a-button type="primary">
						<UploadOutlined />
						点击上传图片
					</a-button>
				</a-upload>
			</div>
			<div class="xn-pt">请上传图片文件，建议不超过2M</div>
		</div>
	</a-modal>
</template>

<script setup>
	import 'vue-cropper/dist/index.css'
	// eslint-disable-next-line no-unused-vars
	import { VueCropper } from 'vue-cropper'

	const cropper = ref()
	const getBase64 = (img, callback) => {
		const reader = new FileReader()
		reader.addEventListener('load', () => callback(reader.result))
		reader.readAsDataURL(img)
	}

	const props = defineProps({
		imgSrc: {
			type: String,
			default: () => ''
		},
		circle: {
			type: Boolean,
			default: () => false
		}
	})
	const emit = defineEmits({ successful: null })
	const visible = ref(false)
	const option = ref({
		img: '',
		size: 1,
		full: false,
		outputType: 'png',
		canMove: true,
		fixedBox: props.circle,
		original: false,
		canMoveBox: true,
		autoCrop: true,
		autoCropWidth: 200,
		autoCropHeight: 200,
		centerBox: false,
		high: false,
		cropData: {},
		enlarge: 1,
		mode: 'contain',
		maxImgSize: 3000,
		limitMinSize: [100, 120]
	})

	const previewUrl = ref('')
	const fileName = ref('')
	const cropOk = () => {
		cropper.value.getCropBlob((blobData) => {
			emit('successful', { fileName: fileName.value, blobData: blobData })
			handleClear()
		})
	}
	const handleClear = () => {
		visible.value = false
		option.value.img = previewUrl.value = fileName.value = ''
	}
	const uploadChange = ({ file }) => {
		getBase64(file.originFileObj, (imageUrl) => {
			fileName.value = file.name
			option.value.img = imageUrl
		})
	}
	const show = () => {
		if (props.imgSrc) {
			option.value.img = props.imgSrc
			// fileName.value = ''
		}
		visible.value = true
	}
	const getCropdata = () => {
		cropper.value.getCropData((data) => {
			previewUrl.value = data
		})
	}
	defineExpose({
		show
	})
</script>

<style lang="less" scoped>
	.circle {
		border-radius: 50%;
	}
	.cropper {
		height: 280px;
	}
	.xn-cj {
		width: 165px;
		height: 165px;
		border: 1px solid #e9e9e9;
		border-radius: 2px
	}
	.xn-pt {
		padding-top: 10px;
	}
	.xn-tl {
		text-align: center;
		padding-top: 10px
	}
	.xn-cj-two {
		padding-top: 10px;
		display: flex

	}
	.xn-cj-two > div:first-child {
		height: 100px;
		width: 100px;
		border: 1px solid #e9e9e9;
		border-radius: 2px
	}
	.xn-cj-two > div:nth-child(2)  {
		height: 60px;
		width: 60px;
		border: 1px solid #e9e9e9;
		margin-left: 5px;
		border-radius: 2px
	}
</style>
