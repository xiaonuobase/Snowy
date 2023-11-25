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
import userRoutes from '@/config/route'

// 获取第一个界面
const getIndexMenu = (menu) => {
	if (menu[0] && menu[0].children) {
		let indexMenu = menu[0].children[0]
		// 如果第一个菜单为目录，接着往下找
		if (indexMenu.meta.type === 'catalog') {
			indexMenu = traverseChild(menu)
		}
		return indexMenu
	} else {
		return userRoutes.menu[0]
	}
}
// 遍历进行判断，其中处理了被隐藏的
const traverseChild = (menu) => {
	if (menu[0] && menu[0].children !== undefined) {
		if (menu[0].children.length > 0) {
			if (menu[0].children[0] && menu[0].children[0].meta.hidden && menu[0].children[0].meta.hidden === true) {
				return menu[0]
			} else {
				return traverseChild(menu[0].children)
			}
		}
	} else {
		return menu[0]
	}
}

export default {
	getIndexMenu
}
