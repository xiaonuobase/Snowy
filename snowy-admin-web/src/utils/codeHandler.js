/**
 *  Copyright [2022] [https://www.xiaonuo.vip]
 *	Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：
 *	1.请不要删除和修改根目录下的LICENSE文件。
 *	2.请不要删除和修改Snowy源码头部的版权声明。
 *	3.本项目代码可免费商业使用，商业使用请保留源码和相关描述文件的项目出处，作者声明等。
 *	4.分发源码时候，请注明软件出处 https://www.xiaonuo.vip
 *	5.不可二次分发开源参与同类竞品，如有想法可联系团队xiaonuobase@qq.com商议合作。
 *	6.若您的项目无法满足以上几点，需要更多功能代码，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */

import { Modal, message, Input } from 'ant-design-vue'
import { h } from 'vue'
import tool from '@/utils/tool'
import smCrypto from '@/utils/smCrypto'
// 直接引 store/global，避免经 store/index 间接依赖 user.js -> loginApi -> request.js 形成循环引用
import { globalStore } from '@/store/global'
// 此处与 userCenterApi 构成循环引用（api -> request -> 本文件），
// 但双方对彼此的引用都在函数体内延迟取值，模块加载期不会触发，故安全；
// 切勿在 userCenterApi 顶层新增立即执行的代码，否则该环会在加载期断裂
import userCenterApi from '@/api/sys/userCenterApi'

/** 需要重新登录的业务码 */
export const RELOGIN_CODES = [401, 1011007, 1011008]
/** 触发二级认证 */
export const SAFE_AUTH_CODE = 4011
/** 会话已被锁屏，除解锁与退出外的接口都会被拦下 */
export const LOCKED_CODE = 4012
/** 解锁连续失败超限，服务端已强制退出登录 */
export const UNLOCK_FAIL_LIMIT_CODE = 4013

// 重登与二级认证弹窗的层级，需高于锁屏遮罩（z-index 2000），否则锁屏时弹出会被遮罩盖住导致点不到
const GLOBAL_MODAL_Z_INDEX = 9000
// 重新登录弹窗是否已弹出，用于保持其唯一性
const loginBack = ref(false)

// 弹出重新登录提示，点击确定后清理登录态并刷新，由路由守卫送回登录页
const showReloginModal = (content) => {
	if (loginBack.value) {
		return
	}
	loginBack.value = true
	Modal.error({
		title: '提示：',
		okText: '重新登录',
		zIndex: GLOBAL_MODAL_Z_INDEX,
		content: content,
		onOk: () => {
			loginBack.value = false
			tool.clearLoginCache()
			window.location.reload()
		}
	})
}

// 登录态失效
export const handleRelogin = () => {
	showReloginModal('登录已失效， 请重新登录')
}

// 解锁连续失败超限，服务端已注销会话
export const handleUnlockFailLimit = (data) => {
	showReloginModal(data.msg)
	return Promise.reject(data)
}

// 服务端会话处于锁屏态，同步本地状态弹出解锁界面，请求本身直接失败
export const handleLocked = (data) => {
	globalStore().setIsLocked(true)
	return Promise.reject(data)
}

// 开启二级认证的弹窗，认证通过后重试原请求
// service 为 axios 实例，由调用方传入：重放原请求要以 config 直接调用实例本身，api 层封装给不了这个能力
export const handleSafeAuth = (service, config, safeType) => {
	return new Promise((resolve, reject) => {
		let password = ''
		Modal.confirm({
			title: '二级认证',
			width: 400,
			zIndex: GLOBAL_MODAL_Z_INDEX,
			content: () =>
				h('div', [
					h('div', { style: 'margin-bottom: 10px' }, '该操作涉及敏感数据，请验证登录密码'),
					h(Input.Password, {
						placeholder: '请输入登录密码',
						onChange: (e) => {
							password = e.target.value
						}
					})
				]),
			onOk: () => {
				if (!password) {
					message.warning('请输入密码')
					// 此处必须返回 rejected promise：onOk 返回 falsy 的非 promise 值会被 antd 判定为直接关闭弹窗
					return Promise.reject()
				}
				const param = {
					password: smCrypto.doSm2Encrypt(password),
					safeType: safeType
				}
				// 调用开启二级认证接口，认证失败时 rejection 原样透传给 Modal，弹窗保持打开供用户重试
				return userCenterApi.userOpenSafe(param).then(() => {
					message.success('认证成功')
					// 认证成功后重试原请求
					resolve(service(config))
				})
			},
			onCancel: () => {
				reject()
			}
		})
	})
}
