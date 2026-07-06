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
import { ref } from 'vue'

export function useRequest(name, service) {
	if (typeof name !== 'string' || !name.trim()) {
		throw new Error('useRequest: first argument "name" must be a non-empty string')
	}
	if (typeof service !== 'function') {
		throw new Error('useRequest: second argument "service" must be a function')
	}

	const data = ref(null)
	const error = ref(null)
	const loading = ref(false)
	const capName = name.charAt(0).toUpperCase() + name.slice(1)

	const runAsync = (...args) => {
		return service(...args)
	}

	const run = async (...args) => {
		loading.value = true
		error.value = null
		try {
			const result = await runAsync(...args)
			data.value = result
			return result
		} catch (err) {
			error.value = err
			throw err
		} finally {
			loading.value = false
		}
	}

	return {
		[`${name}Data`]: data,
		[`${name}Error`]: error,
		[`${name}Loading`]: loading,
		[`fetch${capName}`]: run,
		[`fetch${capName}Async`]: runAsync
	}
}
