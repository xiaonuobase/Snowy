<template>
	<a-card :bordered="false" class="user-info-card">
		<div class="xn-card-line">
			<div class="xn-flex">
				<a-avatar class="user-avatar" :src="userInfo.avatar" />
				<div class="user-details">
					<div class="welcome-text">{{ welcomeMessage }}，{{ userInfo.name }}</div>
					<div class="user-org">{{ userInfo.orgName }} | {{ userInfo.positionName }}</div>
				</div>
			</div>
			<div class="time-box">
				<span class="time-text">{{ currentTime }}</span>
			</div>
		</div>
	</a-card>
</template>

<script setup name="userInfo">
	import dayjs from 'dayjs'
	import weekday from 'dayjs/plugin/weekday'
	import localeData from 'dayjs/plugin/localeData'
	dayjs.extend(weekday)
	dayjs.extend(localeData)

	import { ref, onBeforeUnmount, computed } from 'vue'
	import tool from '@/utils/tool'
	const userInfo = tool.data.get('USER_INFO')

	const currentTime = ref(dayjs().format('YYYY年MM月DD日 HH:mm:ss'))

	const welcomeMessage = computed(() => {
		const hour = dayjs().hour()
		if (hour < 9) return '早上好'
		if (hour < 12) return '上午好'
		if (hour < 14) return '中午好'
		if (hour < 18) return '下午好'
		return '晚上好'
	})

	// 运行定时器，一秒获取一次
	const interval = window.setInterval(() => {
		currentTime.value = dayjs().format('YYYY年MM月DD日 HH:mm:ss')
	}, 1000)
	// 这个界面不在我们视线中的时候，关闭定时器
	onBeforeUnmount(() => {
		window.clearInterval(interval)
	})
</script>

<style scoped lang="less">
	.user-info-card {
		background: var(--component-background);
	}
	.xn-card-line {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}
	.user-avatar {
		width: 60px !important;
		height: 60px !important;
		line-height: 60px !important;
		border: 2px solid var(--component-background);
		box-shadow: 0 2px 8px var(--shadow-color);
		:deep(img) {
			width: 60px !important;
			height: 60px !important;
		}
	}
	.user-details {
		margin-left: 20px;
		display: flex;
		flex-direction: column;
		justify-content: center;
	}
	.welcome-text {
		font-weight: 600;
		font-size: 18px;
		color: var(--text-color);
		margin-bottom: 2px;
	}
	.user-org {
		color: var(--text-color-secondary);
		font-size: 14px;
	}
	.time-box {
		text-align: right;
	}
	.time-text {
		font-size: 16px;
		color: var(--primary-color);
		font-weight: 500;
	}
	@media (max-width: 992px) {
		.time-box {
			display: none;
		}
	}
	.xn-flex {
		display: flex;
		align-items: center;
	}
</style>
