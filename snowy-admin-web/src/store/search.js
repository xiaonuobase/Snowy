import '@/utils/objects'
import { defineStore } from 'pinia'

export const searchStore = defineStore('search', () => {
	// 定义state
	const pool = ref([])
	const hotkey = ref({
		open: 's',
		close: 'esc'
	})
	const active = ref(false)

	// 定义action
	const toggleActive = () => {
		active.value = !active.value
	}
	const setActive = (val) => {
		active.value = val
	}
	const init = (menu) => {
		const poolList = []
		const getFullName = function (meta) {
			if (meta.breadcrumb) {
				let list = []
				meta.breadcrumb.forEach((item) => {
					list.push(item.meta.title)
				})
				return list.join(' / ')
			}
			return meta.title
		}
		const push = function (menu) {
			menu.forEach((m) => {
				if ('menu' === m.meta.type) {
					if (m.children) {
						push(m.children)
					} else if (m.children === null) {
						poolList.push({
							icon: m.meta.icon,
							path: m.path,
							fullPath: m.path,
							name: m.meta.title,
							fullName: getFullName(m.meta),
							namePinyin: m.meta.title.toPinyin(),
							namePinyinFirst: m.meta.title.toPinyin(true)
						})
					}
				}
			})
		}
		push(menu)
		pool.value = poolList
	}

	return {
		pool,
		hotkey,
		active,
		toggleActive,
		setActive,
		init
	}
})
