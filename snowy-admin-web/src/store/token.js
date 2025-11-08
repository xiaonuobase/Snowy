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
import { defineStore } from 'pinia'
import tool from '@/utils/tool'

// 此方法用于业务中有需要监听是否登录成功有token时操作一些业务逻辑
export const useTokenStore = defineStore('token', () => {
	// 当前 token 值（来源于本地存储）
	const token = ref(tool.data.get('TOKEN'))
	const intervalId = ref(null)

	// 从本地存储同步 token
	const syncFromStorage = () => {
		token.value = tool.data.get('TOKEN')
	}

	// 启动定时同步（保证同标签页内登录/退出后能实时更新）
	const startWatch = (ms = 300) => {
		if (intervalId.value !== null) return
		intervalId.value = setInterval(syncFromStorage, ms)
	}

	// 停止定时同步
	const stopWatch = () => {
		if (intervalId.value !== null) {
			clearInterval(intervalId.value)
			intervalId.value = null
		}
	}

	const hasToken = computed(() => !!token.value && token.value !== '')

	return { token, hasToken, syncFromStorage, startWatch, stopWatch }
})
