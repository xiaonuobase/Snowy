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
						style="width: 65px"
						v-model:selectedKeys="leftSelectedKeys"
						mode="inline"
						theme="dark"
						:inline-collapsed="true"
						:items="leftMenuItems"
					/>
				</div>
				<!-- 目录区 -->
				<div class="container-catalog">
					<div class="catalog-search">
						<a-input placeholder="搜索" />
					</div>
					<div class="catalog-content">
						<div
							:class="['catalog-content-li', selectRecordId === record.id ? 'li-checked' : '']"
							:key="record.id"
							v-for="record in chattingRecords"
							@click="recordClick(record)"
						>
							<a-avatar shape="square" size="large" :src="record.avatar" class="catalog-content-li-avatar" />
							<a-badge status="success" style="padding-top: 32px; margin-left: -8px" />
							<div class="catalog-content-li-user">
								<div>
									<span>{{ record.name }}</span
									><span class="catalog-content-li-user-time">{{ record.lastTime }}</span>
								</div>
								<span class="catalog-content-li-user-last-msg">{{ record.lastMessage }}</span>
							</div>
						</div>
					</div>
				</div>
				<!-- 聊天区 -->
				<div class="container-content">
					<div class="content-header">
						<a-flex>
							<div class="header-title">
								<span class="header-title-font" v-if="selectRecordId">业务管理员</span>
							</div>
							<div class="header-close">
								<div class="header-close-icon">
									<CloseOutlined @click="handleClose" />
								</div>
							</div>
						</a-flex>
					</div>
					<!-- 聊天内容区域 -->
					<div class="content-center" v-if="selectRecordId" style="display: flex; flex-direction: column">
						<div class="message-content">消息聊天</div>
						<div class="message-send">
							<div class="message-send-list">
								<FileImageOutlined class="send-list-icon" />
								<FolderOutlined class="send-list-icon" />
							</div>
							<a-textarea :bordered="false" placeholder="输入消息..." :auto-size="{ minRows: 4, maxRows: 8 }" />
							<a-button style="float: right; margin-top: 5px" type="primary">发送</a-button>
						</div>
					</div>
					<!-- 未命中任何聊天 -->
					<div class="content-center-miss" v-else>
						<img src="./image/content.png" class="content-center-miss-image" />
						<span style="font-weight: 600">打开系统的另一扇窗</span>
						<span style="color: #bbbbbb">主动一点，工作会更轻松！</span>
					</div>
				</div>
			</div>
		</a-modal>
	</a-app>
</template>

<script setup name="demo">
	import { MessageOutlined, TeamOutlined } from '@ant-design/icons-vue'
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
			icon: () => h(TeamOutlined),
			label: '通讯录',
			title: '通讯录'
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
	const chattingRecords = ref([
		{
			id: '1213212',
			name: '超管',
			avatar: userInfo.value.avatar,
			lastMessage: '最后说了个啥',
			lastTime: '12:00'
		},
		{
			id: '355222',
			name: '业务管理员',
			avatar: userInfo.value.avatar,
			lastMessage: '[图片]',
			lastTime: '14:00'
		}
	])
	// 选中的
	const selectRecordId = ref('')

	const recordClick = (record) => {
		// 点击某条
		console.log(JSON.stringify(record))
		selectRecordId.value = record.id
	}
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
	// 目录区 -start
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
	.catalog-content {
	}
	.catalog-content-li {
		background-color: #ffffff;
		border-bottom: 1px solid rgb(0 0 0 / 0.06);
		display: flex;
		cursor: pointer;
	}
	.li-checked {
		background-color: #e7e7e7;
	}
	.catalog-content-li-avatar {
		margin-left: 10px;
		margin-top: 10px;
		margin-bottom: 10px;
		flex-grow: 0;
	}
	.catalog-content-li-user {
		margin-left: 5px;
		margin-top: 10px;
		margin-right: 10px;
		display: flex;
		flex-direction: column;
		flex-grow: 1;
	}
	.catalog-content-li-user-time {
		float: right;
		color: #b3b3b3;
	}
	.catalog-content-li-user-last-msg {
		font-size: 12px;
		color: #b3b3b3;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
		width: 120px;
	}

	// 目录区 -end
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
	.content-center-miss {
		display: flex;
		flex-direction: column;
		align-items: center;
		padding-top: 100px;
	}
	.content-center-miss-image {
		width: 180px;
	}
	.content-center {
		height: 90%;
	}
	.message-content {
		border-bottom: 1px solid rgb(0 0 0 / 10%);
		padding: 16px;
		flex: 1;
	}
	.message-send {
		padding: 5px 10px 10px;
	}
	.message-send-list {
		margin-bottom: 5px;
	}
	.send-list-icon {
		margin-left: 5px;
		font-size: 15px;
	}
</style>
