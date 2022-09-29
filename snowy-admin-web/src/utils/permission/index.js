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
import tool from '@/utils/tool'

/**
 * 权限判断是否能看到这个按钮，同时后端也做了校验，前端只是显示与不显示
 * @param {string, array} data 按钮的权限点，可以是单个字符串，也可以是数组
 * @param {string} rule or代表或，and代表与
 * 使用方法：
 * 例如 buttonCodeList 的数据为： ['button1', 'button2', 'button3']
 * 想要判断 button1 的权限，可以写成：hasPerm('button1')
 * 想要判断 button1 或 button2 的权限，可以写成：hasPerm(['button1', 'button2' ])
 * 想要判断 button1 与 button2 的权限，可以写成：hasPerm(['button1', 'button2' ], 'and')
 */
export function hasPerm(data, rule = 'or') {
	if (!data) {
		return false
	}
	const userInfo = tool.data.get('USER_INFO')
	if (!userInfo) {
		return false
	}
	const { buttonCodeList } = userInfo
	if (!buttonCodeList) {
		return false
	}
	if (Array.isArray(data)) {
		const fn = rule === 'or' ? 'some' : 'every'
		return data[fn]((item) => buttonCodeList.includes(item))
	}
	return buttonCodeList.includes(data)
}
