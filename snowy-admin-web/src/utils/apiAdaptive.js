import config from '@/config'
// 业务自己新加了模块，当然只限制于微服务情况下，单体不用管
const bizCustomization = [
	{
		label: '/custom/',
		value: '/custom/'
	}
]
// 微服务环境下如果拆分为多个代码模块，那他的url是网关转发，这里就要配置
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
		label: '/gen/',
		value: '/api/webapp'
	},
	{
		label: '/biz/',
		value: '/api/bizapp'
	}
]
// 转换url
export const convertUrl = (url) => {
	if (config.CLOUD_SERVER === false) {
		return url
	}
	const apiArray = [...PREFIX, ...bizCustomization]
	const prefixUrlArray = apiArray.filter((f) => url.indexOf(f.label) > -1)
	if (prefixUrlArray && prefixUrlArray.length > 0) {
		return prefixUrlArray[0].value + url
	}
	return url
}
