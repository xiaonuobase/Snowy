<template>
	<a-card class="snowy-right-card-one" :bordered="false">
		<template #cover>
			<img
				src="/src/assets/images/index_001.png"
				class="carousel-images"
				@click="leaveForOpen('https://www.xiaonuo.vip')"
			/>
		</template>
	</a-card>
</template>

<script setup name="carousel">
	import { LeftCircleOutlined, RightCircleOutlined } from '@ant-design/icons-vue'
	const carouselList = ref([
		{
			images: '/src/assets/images/index_001.png',
			url: 'https://www.xiaonuo.vip'
		},
		{
			images: '/src/assets/images/index_002.png',
			url: 'https://www.xiaonuo.vip'
		}
	])
	// 打开一个新窗口
	const leaveForOpen = (url) => {
		if (url) {
			window.open(url)
		}
	}
</script>

<style scoped>
	.carousel-images {
		height: 150px;
		width: 100%;
		cursor: pointer;
	}
	.snowy-right-card-one {
		height: 150px;
	}

	.ant-carousel :deep(.slick-slide) {
		text-align: center;
		height: 150px;
		line-height: 150px;
		background: #364d79;
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
</style>
