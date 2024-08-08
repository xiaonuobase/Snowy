// 递归选中或取消子节点（a-tree专属方法）
export const checkOrUnCheckChildren = (checked, node, checkedKeys) => {
	if (node.children) {
		node.children.forEach((item) => {
			if (checked) {
				checkedKeys.checked.push(item.id)
			} else {
				checkedKeys.checked = checkedKeys.checked.filter((k) => k !== item.id)
			}
			checkOrUnCheckChildren(checked, item, checkedKeys)
		})
	}
	return checkedKeys.checked
}
