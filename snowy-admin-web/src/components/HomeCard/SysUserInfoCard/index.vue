<template>
	<a-card :bordered="false">
		<div class="xn-card-line">
			<div class="xn-flex">
				<a-avatar class="xn-wh60" :src="userInfo.avatar" :size="{ xs: 24, sm: 32, md: 40, lg: 64, xl: 80, xxl: 100 }" />
				<div class="snowy-index-card-left-one-username">
					<span>{{ userInfo.name }}</span>
					<span>{{ userInfo.orgName }} | {{ userInfo.positionName }}</span>
				</div>
			</div>
			<span class="snowy-index-userinfo-time">
				{{ currentTime }}
			</span>
		</div>
	</a-card>
</template>

<script setup name="userInfo">
	import dayjs from 'dayjs'
	import weekday from 'dayjs/plugin/weekday'
	import localeData from 'dayjs/plugin/localeData'
	dayjs.extend(weekday)
	dayjs.extend(localeData)

	import { onBeforeUnmount } from 'vue'
	import tool from '@/utils/tool'
	const userInfo = tool.data.get('USER_INFO')

	const currentTime = ref(dayjs().format('YYYY年MM月DD日 HH时mm分ss秒'))
	// 运行定时器，一秒获取一次
	const interval = window.setInterval(() => {
		currentTime.value = dayjs().format('YYYY年MM月DD日 HH时mm分ss秒')
	}, 1000)
	// 这个界面不在我们视线中的时候，关闭定时器
	onBeforeUnmount(() => {
		window.clearInterval(interval)
	})
</script>

<style scoped>
	.xn-card-line {
		display: flex;
		justify-content: space-between;
	}
	.xn-wh60 {
		width: 60px;
		height: 60px;
	}
	.snowy-index-card-left-one-username {
		margin-left: 8px;
		display: flex;
		flex-direction: column;
		justify-content: center;
	}
	.snowy-index-card-left-one-username > span:nth-child(1) {
		font-weight: 600;
		margin: 2px;
		font-size: 18px;
	}
	.snowy-index-card-left-one-username > span:nth-child(2) {
		color: #6d737b;
		margin: 2px;
	}
	.snowy-index-userinfo-time {
		margin: 2px;
	}
	@media (max-width: 992px) {
		.snowy-index-userinfo-time {
			display: none;
		}
	}
	.xn-flex {
		display: flex;
	}
</style>
