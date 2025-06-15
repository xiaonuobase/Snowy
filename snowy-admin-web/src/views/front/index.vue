<script setup lang="ts">
	import { ref, onMounted } from 'vue'
	import { useRouter } from 'vue-router'
	import { message } from 'ant-design-vue'
	import tool from '@/utils/tool'
	import clientLoginApi from '@/api/auth/client/clientLoginApi'

	const router = useRouter()
	const userInfo = ref<any>({})

	// 获取用户信息
	const getUserInfo = () => {
		const clientUserInfo = tool.data.get('CLIENT_USER_INFO')
		if (clientUserInfo) {
			userInfo.value = clientUserInfo
		}
	}

	// 退出登录
	const handleLogout = async () => {
		try {
			const param = {
				token: tool.data.get('CLIENT_TOKEN')
			}
			await clientLoginApi.clientLogout(param)
			tool.data.remove('CLIENT_TOKEN')
			tool.data.remove('CLIENT_USER_INFO')
			message.success('退出成功')
			router.push('/front/client/login')
		} catch (error) {
			message.error('退出失败')
		}
	}

	onMounted(() => {
		getUserInfo()
	})
	const headerStyle = {
		textAlign: 'center',
		height: 64,
		paddingInline: 50,
		lineHeight: '64px',
		backgroundColor: '#ffffff'
	}
	const contentStyle = {
		minHeight: 120,
		lineHeight: '120px',
		color: '#fff'
	}
	const footerStyle = {
		textAlign: 'center',
		color: '#fff'
	}
</script>

<template>
	<a-layout>
		<a-layout-header :style="headerStyle">
			<div style="height: 64px; display: flex; align-items: center; justify-content: space-around">
				<a-avatar :size="50" :src="userInfo.avatar" />
				<span>
					<span style="margin-right: 10px">{{ userInfo.name || userInfo.nickname || userInfo.account }}</span>
					<a-button type="primary" danger @click="handleLogout" size="small">退出登录</a-button>
				</span>
			</div>
		</a-layout-header>
		<a-layout-content :style="contentStyle">
			<div class="user-center">
				<a-card title="基本信息" class="info-card">
					<a-descriptions :column="{ xs: 1, sm: 2, md: 3 }">
						<a-descriptions-item label="账号">{{ userInfo.account }}</a-descriptions-item>
						<a-descriptions-item label="姓名">{{ userInfo.name }}</a-descriptions-item>
						<a-descriptions-item label="性别">{{ userInfo.gender }}</a-descriptions-item>
						<a-descriptions-item label="年龄">{{ userInfo.age || '未设置' }}</a-descriptions-item>
						<a-descriptions-item label="生日">{{ userInfo.birthday || '未设置' }}</a-descriptions-item>
						<a-descriptions-item label="籍贯">{{ userInfo.nativePlace || '未设置' }}</a-descriptions-item>
					</a-descriptions>
				</a-card>
				<a-card title="联系方式" class="info-card">
					<a-descriptions :column="{ xs: 1, sm: 2, md: 3 }">
						<a-descriptions-item label="手机号码">{{ userInfo.phone || '未设置' }}</a-descriptions-item>
						<a-descriptions-item label="电子邮箱">{{ userInfo.email || '未设置' }}</a-descriptions-item>
						<a-descriptions-item label="家庭电话">{{ userInfo.homeTel || '未设置' }}</a-descriptions-item>
						<a-descriptions-item label="办公电话">{{ userInfo.officeTel || '未设置' }}</a-descriptions-item>
						<a-descriptions-item label="紧急联系人">{{ userInfo.emergencyContact || '未设置' }}</a-descriptions-item>
						<a-descriptions-item label="紧急联系电话">{{ userInfo.emergencyPhone || '未设置' }}</a-descriptions-item>
					</a-descriptions>
				</a-card>
				<a-card title="教育背景" class="info-card">
					<a-descriptions :column="{ xs: 1, sm: 2, md: 3 }">
						<a-descriptions-item label="文化程度">{{ userInfo.cultureLevel || '未设置' }}</a-descriptions-item>
						<a-descriptions-item label="政治面貌">{{ userInfo.politicalOutlook || '未设置' }}</a-descriptions-item>
						<a-descriptions-item label="毕业院校">{{ userInfo.college || '未设置' }}</a-descriptions-item>
						<a-descriptions-item label="学历">{{ userInfo.education || '未设置' }}</a-descriptions-item>
						<a-descriptions-item label="学制">{{ userInfo.eduLength || '未设置' }}</a-descriptions-item>
						<a-descriptions-item label="学位">{{ userInfo.degree || '未设置' }}</a-descriptions-item>
					</a-descriptions>
				</a-card>
				<a-card title="登录信息" class="info-card">
					<a-descriptions :column="{ xs: 1, sm: 2, md: 3 }">
						<a-descriptions-item label="最近登录IP">{{ userInfo.latestLoginIp }}</a-descriptions-item>
						<a-descriptions-item label="最近登录地址">{{ userInfo.latestLoginAddress }}</a-descriptions-item>
						<a-descriptions-item label="最近登录时间">{{ userInfo.latestLoginTime }}</a-descriptions-item>
						<a-descriptions-item label="最近登录设备">{{ userInfo.latestLoginDevice }}</a-descriptions-item>
						<a-descriptions-item label="上次登录IP">{{ userInfo.lastLoginIp }}</a-descriptions-item>
						<a-descriptions-item label="上次登录地址">{{ userInfo.lastLoginAddress }}</a-descriptions-item>
					</a-descriptions>
				</a-card>
			</div>
		</a-layout-content>
		<a-layout-footer :style="footerStyle">Footer</a-layout-footer>
	</a-layout>
</template>

<style scoped lang="less">
	.user-center {
		padding: 10px 300px;
		background: #f0f2f5;
		.header-card {
			margin-bottom: 10px;
		}
		.info-card {
			margin-bottom: 10px;
		}
		.user-header {
			display: flex;

			position: relative;
			.logout-btn {
				position: absolute;
				right: 0;
				top: 50%;
				transform: translateY(-50%);
			}
		}
	}
</style>
