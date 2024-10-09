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
export default (error) => {
	// 过滤HTTP请求错误
	if (error.code) {
		return false
	}
	const errorMap = {
		InternalError: 'Javascript引擎内部错误',
		ReferenceError: '未找到对象',
		TypeError: '使用了错误的类型或对象',
		RangeError: '使用内置对象时，参数超范围',
		SyntaxError: '语法错误',
		EvalError: '错误的使用了Eval',
		URIError: 'URI错误'
	}
	const errorName = errorMap[error.name] || '未知错误'
	nextTick(() => {
		console.error(errorName + ' ' + error)
	})
}
