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
// 微服务环境下如果拆分为多个代码模块，那他的url是网关转发，这里就要配置，其次在api文件已经引用此类了
var SERVER_TYPE = null

const PREFIX = [
	{
		label: '/mobile/',
		value: '/api/webapp'
	},
	{
		label: '/sys/',
		value: '/api/webapp'
	},
	{
		label: '/auth/',
		value: '/api/webapp'
	},
	{
		label: '/client/',
		value: '/api/webapp'
	},
	{
		label: '/dev/',
		value: '/api/webapp'
	},
	{
		label: '/biz/',
		value: '/api/bizapp'
	},
	{
		label: '/ten/',
		value: '/api/tenapp'
	},
	{
		label: '/flw/',
		value: '/api/flwapp'
	},
	{
		label: '/im/',
		value: '/api/bizapp'
	},
	{
		label: '/ws/',
		value: '/api/bizapp'
	}
]

// 设置服务类型
export const setServerType = (serverType) => {
	return new Promise((resolve, reject) => {
		// 设置服务类型
		SERVER_TYPE = serverType
		resolve(serverType)
	})
}

// 匹配并返回接口前缀
export const prefixUrl = (url) => {
	if (SERVER_TYPE === 'SNOWY_CLOUD') {
		const prefixUrlArray = PREFIX.filter((f) => url.indexOf(f.label) > -1)
		if (prefixUrlArray && prefixUrlArray.length > 0) {
			return prefixUrlArray[0].value + url
		}
		return url
	} else {
		return url
	}
}
