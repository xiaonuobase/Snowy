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
import pinyin from 'js-pinyin'

// 中文转拼音 传入仅首字母
Object.defineProperty(String.prototype, 'toPinyin', {
	writable: false,
	enumerable: false,
	configurable: true,
	value: function (first) {
		let str = this
		if (first) {
			return pinyin.getCamelChars(str).replace(/\uD83C[\uDF00-\uDFFF]|\uD83D[\uDC00-\uDE4F]/g, '')
		}
		return pinyin.getFullChars(str).replace(/\uD83C[\uDF00-\uDFFF]|\uD83D[\uDC00-\uDE4F]/g, '')
	}
})

// 字符检索 传入检索值
Object.defineProperty(String.prototype, 'filter', {
	writable: false,
	enumerable: false,
	configurable: true,
	value: function (input) {
		let str = this
		let en = str.toLowerCase().includes(input.toLowerCase())
		let zhFull = str.toPinyin().toLowerCase().includes(input.toLowerCase())
		let zhFirst = str.toPinyin(true).toLowerCase().includes(input.toLowerCase())
		return en || zhFull || zhFirst
	}
})
