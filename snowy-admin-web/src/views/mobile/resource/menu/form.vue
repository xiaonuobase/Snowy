<template>
	<xn-form-container
		:title="formData.id ? '编辑移动端菜单' : '增加移动端菜单'"
		:width="700"
		:visible="visible"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-row :gutter="16">
				<a-col :span="12">
					<a-form-item label="上级菜单：" name="parentId">
						<a-tree-select
							v-model:value="formData.parentId"
							v-model:treeExpandedKeys="defaultExpandedKeys"
							class="xn-wd"
							:dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
							placeholder="请选择上级菜单"
							allow-clear
							tree-default-expand-all
							:tree-data="treeData"
							:field-names="{
								children: 'children',
								label: 'title',
								value: 'id'
							}"
							selectable="false"
							tree-line
							@change="parentChange(formData.parentId)"
						/>
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="名称：" name="title">
						<a-input v-model:value="formData.title" placeholder="请输入名称" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="菜单类型：" name="menuType">
						<a-radio-group
							v-model:value="formData.menuType"
							button-style="solid"
							:options="menuTypeOptions"
							option-type="button"
						/>
					</a-form-item>
				</a-col>
				<a-col :span="12" v-if="formData.menuType !== 'CATALOG'">
					<a-form-item name="path">
						<template #label>
							<a-tooltip>
								<template #title> 类型为内外链时，输入https开头的链接即可（例：https://xiaonuo.vip） </template>
								<question-circle-outlined />
							</a-tooltip>
							&nbsp
							{{ formData.menuType === 'MENU' || formData.menuType === 'CATALOG' ? '界面地址' : 'https链接地址' }}：
						</template>
						<a-input v-model:value="formData.path" placeholder="请输入" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="图标：" name="icon">
						<a-input v-model:value="formData.icon" class="xn-wdcalc-70" placeholder="请选择图标" allow-clear disabled />
						<a-button type="primary" @click="iconSelector.showIconModal(formData.icon)">选择</a-button>
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="颜色：" name="color">
						<color-picker v-model:value="formData.color" />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="是否正规则：" name="regType">
						<a-radio-group v-model:value="formData.regType" placeholder="请选择正规则" :options="regTypeOptions" />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="可用状态：" name="status">
						<a-radio-group v-model:value="formData.status" placeholder="请选择可用状态" :options="statusOptions" />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="排序码：" name="sortCode">
						<a-input-number class="xn-wd" v-model:value="formData.sortCode" placeholder="请输入排序码" :max="1000" />
					</a-form-item>
				</a-col>
			</a-row>
		</a-form>
		<template #footer>
			<a-button class="xn-mr8" @click="onClose">关闭</a-button>
			<a-button type="primary" @click="onSubmit" :loading="submitLoading">保存</a-button>
		</template>
		<icon-mobile-selector ref="iconSelector" @iconCallBack="iconCallBack" />
	</xn-form-container>
</template>

<script setup name="mobileMenuForm">
	import tool from '@/utils/tool'
	import { message } from 'ant-design-vue'
	import SnowflakeId from 'snowflake-id'
	import { cloneDeep } from 'lodash-es'
	import { required } from '@/utils/formRules'
	import mobileMenuApi from '@/api/mobile/resource/menuApi'
	import ColorPicker from '@/components/ColorPicker/index.vue'
	import IconMobileSelector from '@/components/Selector/iconMobileSelector.vue'
	// 抽屉状态
	const visible = ref(false)
	const emit = defineEmits({ successful: null })
	const formRef = ref()
	let iconSelector = ref()
	// 默认展开的节点(顶级)
	const defaultExpandedKeys = ref([0])
	const treeData = ref([])
	const formData = ref({})
	// 类别
	const moduleId = ref('')
	const submitLoading = ref(false)
	const regTypeOptions = ref([])
	const statusOptions = ref([])
	const menuTypeOptions = [
		{
			label: '目录',
			value: 'CATALOG'
		},
		{
			label: '菜单',
			value: 'MENU'
		},
		{
			label: '内链',
			value: 'IFRAME'
		},
		{
			label: '外链',
			value: 'LINK'
		}
	]

	// 打开抽屉
	const onOpen = (record, module) => {
		if (!module) {
			message.warning('请先添加菜单所属模块')
			return
		}
		moduleId.value = module
		visible.value = true
		// 设置默认的
		formData.value = {
			regType: 'YES',
			status: 'ENABLE',
			category: 'MENU',
			menuType: 'MENU',
			color: '#1677FF',
			sortCode: 99
		}
		if (record) {
			let recordData = cloneDeep(record)
			formData.value = Object.assign({}, recordData)
		}
		// 获取菜单树并加入顶级
		const treeParam = {
			module: module
		}
		mobileMenuApi.mobileMenuTreeSelector(treeParam).then((res) => {
			treeData.value = [
				{
					id: 0,
					parentId: '-1',
					title: '顶级',
					children: res
				}
			]
		})
		regTypeOptions.value = tool.dictList('MOBILE_REG_TYPE')
		statusOptions.value = tool.dictList('MOBILE_STATUS')
	}
	// 关闭抽屉
	const onClose = () => {
		formRef.value.resetFields()
		formData.value = {}
		visible.value = false
	}
	// 选择上级加载模块的选择框
	const parentChange = (value) => {
		if (value > 0) {
			// 执行接口去查询选择的上级是哪个类别，吧对应的也置为一样的
			const param = {
				id: value
			}
			mobileMenuApi.mobileMenuDetail(param).then((res) => {
				formData.value.module = res.module
			})
		} else {
			formData.value.module = null
		}
	}
	// 图标选择器回调
	const iconCallBack = (value) => {
		if (value) {
			formRef.value.clearValidate('icon')
		}
		formData.value.icon = value
	}
	// 默认要校验的
	const formRules = {
		parentId: [required('请选择上级')],
		title: [required('请输入名称')],
		path: [required('请输入界面路径')],
		icon: [required('请选择图标')],
		color: [required('请选择颜色')],
		regType: [required('请选择规则类型')],
		status: [required('请选择可用状态')]
	}
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value
			.validate()
			.then(() => {
				submitLoading.value = true
				const formDataParam = parameterChanges(cloneDeep(formData.value))
				mobileMenuApi
					.mobileMenuSubmitForm(formDataParam, formDataParam.id)
					.then(() => {
						onClose()
						emit('successful')
					})
					.finally(() => {
						submitLoading.value = false
					})
			})
			.catch(() => {})
	}
	// 提交之前转换数据
	const parameterChanges = (data) => {
		data.module = moduleId.value
		if (data.menuType === 'CATALOG') {
			const snowflake = new SnowflakeId()
			if (!data.path) {
				data.path = snowflake.generate()
			}
		}
		return data
	}
	// 抛出函数
	defineExpose({
		onOpen
	})
</script>
