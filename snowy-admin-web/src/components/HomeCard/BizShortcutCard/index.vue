<template>
	<a-card :title="title" :bordered="false" :loading="apiLoading" class="shortcut-card">
		<div class="card-div">
			<a-row v-if="shortcutList && shortcutList.length > 0" :gutter="16">
				<a-col :span="6" :key="shortcut.id" v-for="shortcut in shortcutList" :xs="12" :sm="8" :md="6" :lg="8" :xl="6">
					<shortcutCard
						class="mb-3"
						:icon="shortcut.icon ? shortcut.icon : 'menu-outlined'"
						:label="shortcut.title"
						@click="leaveFor(shortcut.path)"
					/>
				</a-col>
			</a-row>
			<div v-else class="empty-state">
				<appstore-outlined class="empty-icon" />
				<p class="empty-text">暂无快捷方式</p>
				<p class="empty-hint">前往个人中心配置常用功能</p>
			</div>
		</div>
	</a-card>
</template>

<script setup name="shortcut">
	import { AppstoreOutlined } from '@ant-design/icons-vue'
	import router from '@/router'
	import userCenterApi from '@/api/sys/userCenterApi'
	import ShortcutCard from '@/components/ShortcutCard/index.vue'
	import { onMounted, ref } from 'vue'
	const shortcutList = ref([])
	const title = ref('快捷方式')
	const apiLoading = ref(false)
	onMounted(() => {
		// 进来后执行查询
		getUserLoginWorkbench()
	})
	const getUserLoginWorkbench = () => {
		apiLoading.value = true
		userCenterApi
			.userLoginWorkbench()
			.then((data) => {
				if (data) {
					shortcutList.value = JSON.parse(data).shortcut
				}
			})
			.catch(() => {})
			.finally(() => {
				apiLoading.value = false
			})
	}
	const leaveFor = (url = '/') => {
		router.replace({
			path: url
		})
	}
</script>

<style scoped lang="less">
	:deep(.ant-card-body) {
		padding-top: 0 !important;
	}
	.card-div {
		overflow-y: auto;
		height: 240px;
		padding: 5px;
		scrollbar-width: none;
		&::-webkit-scrollbar {
			display: none;
		}
	}
	.empty-state {
		height: 100%;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		gap: 6px;
	}
	.empty-icon {
		font-size: 36px;
		color: var(--primary-color);
		opacity: 0.25;
	}
	.empty-text {
		margin: 0;
		font-size: 14px;
		color: var(--text-color-secondary);
		font-weight: 500;
	}
	.empty-hint {
		margin: 0;
		font-size: 12px;
		color: var(--text-color-secondary);
		opacity: 0.6;
	}
</style>
