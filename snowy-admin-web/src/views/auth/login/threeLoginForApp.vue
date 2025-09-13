<template>
	<a-divider>{{ $t('login.signInOther') }}</a-divider>
	<div class="login-oauth layout-center">
		<a-space align="start">
			<a @click="renderAuthSource(record)" v-for="record in appAuthSourceList" :key="record.authSourceId">
				<img :src="record.authSourceLogo" class="record-img"/>
			</a>
		</a-space>
	</div>
</template>

<script setup name="threeLoginForApp">
	/*import authLoginApi from '@/api/iam/auth/authLoginApi'
	import authSourceApi from '@/api/iam/auth/authSourceApi'*/
	// 定义emit事件
	const emit = defineEmits({ updateLoginTypes: null, updateSystemName: null })

	const props = defineProps({
		appId: {
			type: String,
			default: () => {}
		},
		loginTypes: {
			type: Object,
			default: () => {}
		}
	})
	const appAuthSourceList = ref([])
	const init = () => {
		const param = {
			appId: props.appId
		}
		/*authLoginApi.getAppAuthSourceList(param).then((data) => {
			const appName = data.appName
			const authAppLinkResultList = data.authAppLinkResultList
			let phoneLogin = authAppLinkResultList.filter((item) => item.authSourceTemplateCode === 'PHONE').length > 0?'true':'false';
			let emailLogin = authAppLinkResultList.filter((item) => item.authSourceTemplateCode === 'EMAIL').length > 0?'true':'false';
			let otpLogin = authAppLinkResultList.filter((item) => item.authSourceTemplateCode === 'OTP').length > 0?'true':'false';
			appAuthSourceList.value = authAppLinkResultList.filter((item) => !item.isBuildIn);
			phoneLogin = props.loginTypes.phoneLogin === 'true' && phoneLogin === 'true'?'true':'false'
			emailLogin = props.loginTypes.emailLogin === 'true' && emailLogin === 'true'?'true':'false'
			otpLogin = props.loginTypes.otpLogin === 'true' && otpLogin === 'true'?'true':'false'
			emit('updateLoginTypes', { phoneLogin, emailLogin, otpLogin })
			emit('updateSystemName', appName)
		})*/
	}
	const renderAuthSource = (record) => {
		const param = {
			appId: props.appId,
			authSourceId: record.authSourceId,
			clientType: 'B'
		}
		/*authSourceApi.authSourceRender(param).then((data) => {
			window.location.href = data
		})*/
	}
	defineExpose({
		init
	})
</script>
<style scoped>
.record-img {
	width: 32px;
	height: 32px;
}
</style>
