<template>
	<a-card :title="title" :bordered="false">
		<a-carousel class="snowy-right-card-one" autoplay arrows>
			<template #prevArrow>
				<div class="custom-slick-arrow" style="left: 10px; z-index: 1">
					<LeftOutlined />
				</div>
			</template>
			<template #nextArrow>
				<div class="custom-slick-arrow" style="right: 10px"><RightOutlined /></div>
			</template>
			<img
				v-for="item in slideshowList"
				:key="item.id"
				:src="item.image"
				class="carousel-images"
				@click="leaveForOpen(item.pathDetails)"
			/>
			<a-empty v-if="isEmpty(slideshowList)" :image="Empty.PRESENTED_IMAGE_SIMPLE" />
		</a-carousel>
	</a-card>
</template>

<script setup name="carousel">
	import bizIndexApi from '@/api/biz/bizIndexApi'
	import { Empty } from 'ant-design-vue'
	import { isEmpty, cloneDeep } from 'lodash-es'
	import router from '@/router'
	const slideshowList = ref([])
	const title = ref('')
	// 外部传来的参数
	const props = defineProps({
		config: {
			type: Object,
			default: () => {}
		}
	})
	// 界面加载完后异步查询
	onMounted(() => {
		// 获得业务首页轮播图
		const param = {
			// 这是字典内维护的该位置
			place: props.config.options.place ? props.config.options.place : 'BACK_SYS_INDEX'
		}
		bizIndexApi
			.bizIndexSlideshowList(param)
			.then((data) => {
				slideshowList.value = data
			})
			.catch(() => {})
	})
	// URL跟路由的跳转
	const leaveForOpen = (value) => {
		if (isEmpty(value)) {
			return
		}
		const detail = cloneDeep(value)
		let result = {}
		if (typeof detail !== 'object') {
			result = JSON.parse(detail)
		}
		// json内包含且是开启了点击，否则不处理
		if (result.whetherToClick && result.whetherToClick === 'ENABLE') {
			if (result.skipMode && result.skipMode === 'URL') {
				window.open(result.url)
			}
			if (result.skipMode && result.skipMode === 'ROUTER') {
				router.replace({
					path: result.url
				})
			}
		}
	}
</script>

<style scoped>
	.carousel-images {
		height: 180px;
		width: 100%;
		cursor: pointer;
	}
	.snowy-right-card-one {
		height: 180px;
	}
	.ant-carousel :deep(.slick-slide) {
		text-align: center;
		height: 180px;
		line-height: 150px;
		background: #1890ff;
		overflow: hidden;
	}
	.ant-carousel :deep(.slick-arrow.custom-slick-arrow) {
		width: 25px;
		height: 25px;
		font-size: 25px;
		color: #fff;
		background-color: rgba(31, 45, 61, 0.11);
		opacity: 0.3;
		z-index: 1;
	}
	.ant-carousel :deep(.custom-slick-arrow:before) {
		display: none;
	}
	.ant-carousel :deep(.custom-slick-arrow:hover) {
		opacity: 0.5;
	}
	.ant-carousel :deep(.slick-slide h3) {
		color: #fff;
	}
	:deep(.ant-card-body) {
		padding: 0 !important;
	}
</style>
