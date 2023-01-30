<template>
    <a-drawer
        :title="formData.id ? '编辑移动端菜单' : '增加移动端菜单'"
        :width="600"
        :visible="visible"
        :destroy-on-close="true"
        :footer-style="{ textAlign: 'right' }"
        @close="onClose"
    >
        <a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
            <a-row :gutter="16">
                <a-col :span="12">
                    <a-form-item label="上级菜单：" name="parentId">
						<a-tree-select
							v-model:value="formData.parentId"
							v-model:treeExpandedKeys="defaultExpandedKeys"
							style="width: 100%"
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
						></a-tree-select>
                    </a-form-item>
                </a-col>
                <a-col :span="12">
                    <a-form-item label="名称：" name="title">
                        <a-input v-model:value="formData.title" placeholder="请输入名称" allow-clear />
                    </a-form-item>
                </a-col>
                <a-col :span="12">
                    <a-form-item label="界面路径：" name="pages">
                        <a-input v-model:value="formData.pages" placeholder="请输入界面路径" allow-clear />
                    </a-form-item>
                </a-col>
                <a-col :span="12">
                    <a-form-item label="图标：" name="icon">
						<a-input v-model:value="formData.icon" style="width: calc(100% - 70px)" placeholder="请选择图标" allow-clear />
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
            </a-row>
			<a-form-item label="排序码：" name="sortCode">
				<a-slider v-model:value="formData.sortCode" :max="1000" style="width: 100%" />
			</a-form-item>
        </a-form>
        <template #footer>
            <a-button style="margin-right: 8px" @click="onClose">关闭</a-button>
            <a-button type="primary" @click="onSubmit" :loading="submitLoading">保存</a-button>
        </template>
		<Icon-selector ref="iconSelector" @iconCallBack="iconCallBack" />
    </a-drawer>
</template>

<script setup name="mobileMenuForm">
    import tool from '@/utils/tool'
    import { cloneDeep } from 'lodash-es'
    import { required } from '@/utils/formRules'
    import mobileMenuApi from '@/api/mobile/resource/menuApi'
	import ColorPicker from '@/components/ColorPicker/index.vue'
	import IconSelector from '@/components/Selector/iconSelector.vue'
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

    // 打开抽屉
    const onOpen = (record, module) => {
		moduleId.value = module
        visible.value = true
		// 设置默认的
		formData.value = {
			regType: 'YES',
			status: 'ENABLE',
			category: 'MENU'
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
		formData.value.icon = value
	}
    // 默认要校验的
    const formRules = {
        parentId: [required('请选择上级')],
        title: [required('请输入名称')],
        pages: [required('请输入界面路径')],
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
                const formDataParam = cloneDeep(formData.value)
				formDataParam.module = moduleId.value
                mobileMenuApi
                    .mobileMenuSubmitForm(formDataParam, !formDataParam.id)
                    .then(() => {
                        onClose()
                        emit('successful')
                    })
                    .finally(() => {
                        submitLoading.value = false
                    })
            })
    }
    // 抛出函数
    defineExpose({
        onOpen
    })
</script>
