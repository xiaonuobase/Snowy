<template>
	<div class="im panel-item" @click="handleOpen">
		<NotificationOutlined />
	</div>
	<a-app>
		<a-modal
			v-model:open="open"
			:closable="false"
			:footer="null"
			:mask-closable="false"
			:style="{ width: 'auto', height: 'auto', maxWidth: '60vw', maxHeight: '90vh' }"
		>
			<template #title></template>
			<div class="xn-im-total-container">
				<!-- 左侧 -->
				<div class="container-side">
					<a-popover trigger="click">
						<template #content>
							<a-avatar shape="square" :src="userInfo.avatar" :size="64" />
							<span>姓名：</span> {{ userInfo.name }}
							<br />
							<span>机构：</span> {{ userInfo.orgName }}-{{ userInfo.positionName }}
						</template>
						<a-avatar shape="square" size="large" :src="userInfo.avatar" class="container-side-avatar" />
					</a-popover>
					<a-menu
						style="width: auto"
						v-model:selectedKeys="leftSelectedKeys"
						mode="inline"
						theme="dark"
						:inline-collapsed="true"
						:items="leftMenuItems"
					/>
				</div>
				<!-- 中间 -->
				<div class="container-catalog">
					<div class="catalog-search">
						<a-input placeholder="搜索" />
					</div>
				</div>
				<!-- 聊天区 -->
				<div class="container-content">
					<div class="content-header">
						<a-flex>
							<div class="header-title">
								<span class="header-title-font">业务管理员</span>
							</div>
							<div class="header-close">
								<div class="header-close-icon">
									<CloseOutlined @click="handleClose" />
								</div>
							</div>
						</a-flex>
					</div>
					<div>dfsdfsf</div>
				</div>
			</div>
		</a-modal>
	</a-app>
</template>

<script setup name="demo">
	import { MessageOutlined, UserOutlined, TeamOutlined } from '@ant-design/icons-vue'
	import { globalStore } from '@/store'
	const open = ref(false)
	const leftSelectedKeys = ref(['1'])
	const leftMenuItems = ref([
		{
			key: '1',
			icon: () => h(MessageOutlined),
			label: '聊天',
			title: '聊天'
		},
		{
			key: '2',
			icon: () => h(UserOutlined),
			label: '用户',
			title: '用户'
		},
		{
			key: '3',
			icon: () => h(TeamOutlined),
			label: '群聊',
			title: '群聊'
		}
	])
	const store = globalStore()
	const userInfo = computed(() => {
		if (store.userInfo) {
			return store.userInfo
		} else {
			return {
				avatar: '',
				name: '',
				nickname: '',
				orgName: '',
				positionName: ''
			}
		}
	})

	const handleOpen = () => {
		open.value = true
	}
	const handleClose = () => {
		open.value = false
	}
</script>

<style lang="less" scoped>
	.xn-im-total-container {
		margin: -28px -24px -20px;
		min-height: 600px;
		display: flex;
	}
	.container-side {
		width: 65px;
		height: 600px;
		background-color: #001529;
		display: flex;
		flex-direction: column;
		align-items: center;
		flex-grow: 0;
	}
	.container-side-avatar {
		margin-top: 20px;
		margin-bottom: 20px;
		cursor: pointer;
	}
	.container-catalog {
		flex-grow: 0;
		width: 220px;
		border-right: 1px solid rgb(0 0 0 / 10%);
	}
	.container-content {
		flex-grow: 1;
	}
	.catalog-search {
		width: 100%;
		height: 60px;
		padding-left: 10px;
		padding-right: 10px;
		text-align: center;
		display: flex;
		justify-content: center;
		flex-direction: column-reverse;
		border-bottom: 1px solid rgb(0 0 0 / 10%);
	}
	.content-header {
		width: auto;
		height: 60px;
		border-bottom: 1px solid rgb(0 0 0 / 10%);
	}
	.header-title {
		min-width: 260px;
		margin-left: 20px;
		height: 60px;
		display: flex;
		align-items: center;
	}
	.header-title-font {
		font-weight: 600;
	}
	.header-close {
		width: 100%;
		float: right;
	}
	.header-close-icon {
		float: right;
		padding: 5px 10px 5px 5px;
	}
</style>
