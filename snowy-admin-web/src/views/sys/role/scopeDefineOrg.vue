<template>
	<a-modal
		v-model:open="visible"
		title="选择组织"
		:width="400"
		:mask-closable="false"
		:destroy-on-close="true"
		@ok="handleOk"
		@cancel="onClose"
	>
		<div class="scopeDefineOrgTreeDiv">
			<a-tree
				v-model:expandedKeys="defaultExpandedKeys"
				v-model:checkedKeys="checkedKeys"
				:tree-data="treeData"
				:field-names="treeFieldNames"
				checkable
				check-strictly
				:selectable="false"
				@check="treeCheck"
			>
			</a-tree>
		</div>
	</a-modal>
</template>

<script setup="props, context" name="scopeDefineOrg">
	import roleApi from '@/api/sys/roleApi'
	import { checkOrUnCheckChildren } from '@/utils/treeHandler'
	const visible = ref(false)
	let defaultExpandedKeys = ref([])
	let checkedKeys = ref([])
	const treeData = ref([])

	const resultDataModel = {
		dataScopeId: '',
		defineOrgIdData: {
			scopeCategory: 'SCOPE_ORG_DEFINE',
			scopeDefineOrgIdList: []
		}
	}

	// 打开此界面需要具体某条菜单的id跟选中的
	const onOpen = (id, checkKeys) => {
		visible.value = true
		resultDataModel.dataScopeId = id
		// const treeData = data.data;
		roleApi.roleOrgTreeSelector().then((res) => {
			if (res !== null) {
				treeData.value = res
				// 赋值选中项
				echoOrgSelectKeys(checkKeys)
				// 默认展开2级
				treeData.value.forEach((item) => {
					// 因为0的顶级
					if (item.parentId === '0') {
						defaultExpandedKeys.value.push(item.id)
						// 取到下级ID
						if (item.children) {
							item.children.forEach((items) => {
								defaultExpandedKeys.value.push(items.id)
							})
						}
					}
				})
			}
		})
	}
	const onClose = () => {
		visible.value = false
	}
	// 回显机构的选中项
	const echoOrgSelectKeys = (checkKeys) => {
		checkedKeys.value = []
		if (checkKeys && checkKeys.length > 0) {
			checkKeys
				.toString()
				.split(',')
				.forEach((key) => {
					checkedKeys.value.push(key)
				})
		}
	}
	// 替换treeNode 中 title,key,children
	const treeFieldNames = { children: 'children', title: 'name', key: 'id' }

	// 选中触发
	const treeCheck = (checkedKeys, { checked, node }) => {
		resultDataModel.defineOrgIdData.scopeDefineOrgIdList = checkOrUnCheckChildren(checked, node, checkedKeys)
	}
	// 定义emit事件
	const emit = defineEmits({
		click: null
	})
	const handleOk = () => {
		emit('click', resultDataModel)
		visible.value = false
	}

	defineExpose({
		onOpen
	})
</script>

<style lang="less">
	// 穿梭框宽度重写
	.ant-transfer-list {
		width: 220px !important;
	}
	.scopeDefineOrgTreeDiv {
		max-height: 450px;
		overflow: auto;
	}
</style>
