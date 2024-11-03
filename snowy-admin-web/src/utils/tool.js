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
/**
 * @Descripttion: 工具集
 * @version: 1.1
 * @LastEditors: yubaoshan
 * @LastEditTime: 2022年4月19日10:58:41
 */
const tool = {}

// localStorage
tool.data = {
	set(table, settings) {
		const _set = JSON.stringify(settings)
		const SNOWYSTRING = table.slice(0, 6) === 'SNOWY_' && table !== 'SNOWY_SYS_BASE_CONFIG'
		if (SNOWYSTRING) {
			let localSetting = JSON.parse(localStorage.getItem('SNOWY_SETTING')) || {}
			let newSetting = {}
			newSetting[table] = _set
			return localStorage.setItem('SNOWY_SETTING', JSON.stringify(Object.assign(localSetting, newSetting)))
		} else return localStorage.setItem(table, _set)
	},
	get(table) {
		const SNOWYSTRING = table.slice(0, 6) === 'SNOWY_' && table !== 'SNOWY_SYS_BASE_CONFIG'
		const SNOWY_SETTING = JSON.parse(localStorage.getItem('SNOWY_SETTING')) || {}
		let data = SNOWYSTRING ? SNOWY_SETTING[table] : localStorage.getItem(table)
		try {
			data = JSON.parse(data)
		} catch (err) {
			return null
		}
		return data
	},
	remove(table) {
		return localStorage.removeItem(table)
	},

	clear() {
		return localStorage.clear()
	}
}

// sessionStorage
tool.session = {
	set(table, settings) {
		const _set = JSON.stringify(settings)
		return sessionStorage.setItem(table, _set)
	},
	get(table) {
		let data = sessionStorage.getItem(table)
		try {
			data = JSON.parse(data)
		} catch (err) {
			return null
		}
		return data
	},
	remove(table) {
		return sessionStorage.removeItem(table)
	},
	clear() {
		return sessionStorage.clear()
	}
}

// 千分符
tool.groupSeparator = (num) => {
	num = `${num}`
	if (!num.includes('.')) num += '.'

	return num
		.replace(/(\d)(?=(\d{3})+\.)/g, ($0, $1) => {
			return `${$1},`
		})
		.replace(/\.$/, '')
}

// 获取所有字典数组
tool.dictDataAll = () => {
	return tool.data.get('DICT_TYPE_TREE_DATA')
}

// 字典翻译方法，界面插槽使用方法 {{ $TOOL.dictType('sex', record.sex) }}
tool.dictTypeData = (dictValue, value) => {
	const dictTypeTree = tool.dictDataAll()
	if (!dictTypeTree) {
		return '需重新登录'
	}
	const tree = dictTypeTree.find((item) => item.dictValue === dictValue)
	if (!tree) {
		return '无此字典'
	}
	const children = tree.children
	const dict = children.find((item) => item.dictValue === value)
	return dict ? dict.dictLabel : '无此字典项'
}

// 获取某个code下字典的列表，多用于字典下拉框
tool.dictTypeList = (dictValue) => {
	const dictTypeTree = tool.dictDataAll()
	if (!dictTypeTree) {
		return []
	}
	const tree = dictTypeTree.find((item) => item.dictValue === dictValue)
	if (tree && tree.children) {
		return tree.children
	}
	return []
}

// 获取某个code下字典的列表，基于dictTypeList 改进，保留老的，逐步替换
tool.dictList = (dictValue) => {
	const dictTypeTree = tool.dictDataAll()
	if (!dictTypeTree) {
		return []
	}
	const tree = dictTypeTree.find((item) => item.dictValue === dictValue)
	if (tree) {
		return tree.children.map((item) => {
			return {
				value: item['dictValue'],
				label: item['name']
			}
		})
	}
	return []
}

// 树形翻译 需要指定最顶级的 parentValue  和当级的value
tool.translateTree = (parentValue, value) => {
	const tree = tool.dictDataAll().find((item) => item.dictValue === parentValue)
	const targetNode = findNodeByValue(tree, value)
	return targetNode ? targetNode.dictLabel : ''
}
const findNodeByValue = (node, value) => {
	if (node.dictValue === value) {
		return node
	}
	if (node.children) {
		for (let i = 0; i < node.children.length; i++) {
			const result = findNodeByValue(node.children[i], value)
			if (result) {
				return result
			}
		}
	}
	return null
}

// 生成UUID
tool.snowyUuid = () => {
	let uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) => {
		let r = (Math.random() * 16) | 0,
			v = c === 'x' ? r : (r & 0x3) | 0x8
		return v.toString(16)
	})
	// 首字符转换成字母
	return 'xn' + uuid.slice(2)
}

// 输入位数获得英文字母大小写随机码
tool.generateString = (length = 8) => {
	let result = ''
	let characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'
	let charactersLength = characters.length
	for (let i = 0; i < length; i++) {
		result += characters.charAt(Math.floor(Math.random() * charactersLength))
	}
	return result
}

tool.parseTime = (time, cFormat) => {
	if (time == null || time.length === 0) {
	  return ''
	}

	const format = cFormat || '{y}-{m}-{d} {h}:{i}:{s}'
	let date
	if (typeof time === 'object') {
	  date = time
	} else {
	  if ((typeof time === 'string') && (/^[0-9]+$/.test(time))) {
		time = parseInt(time)
	  }
	  if ((typeof time === 'number') && (time.toString().length === 10)) {
		time = time * 1000
	  }
	  date = new Date(time)
	}
	const formatObj = {
	  y: date.getFullYear(),
	  m: date.getMonth() + 1,
	  d: date.getDate(),
	  h: date.getHours(),
	  i: date.getMinutes(),
	  s: date.getSeconds(),
	  a: date.getDay()
	}
	const time_str = format.replace(/{(y|m|d|h|i|s|a)+}/g, (result, key) => {
	  let value = formatObj[key]
	  // Note: getDay() returns 0 on Sunday
	  if (key === 'a') { return ['日', '一', '二', '三', '四', '五', '六'][value ] }
	  if (result.length > 0 && value < 10) {
		value = '0' + value
	  }
	  return value || 0
	})
	return time_str
}

export default tool
