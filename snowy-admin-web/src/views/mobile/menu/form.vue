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
                    <a-form-item label="父ID：" name="parentId">
                        <a-input v-model:value="formData.parentId" placeholder="请输入父ID" allow-clear />
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
                    <a-form-item label="分类：" name="category">
                        <a-select v-model:value="formData.category" placeholder="请选择分类" :options="categoryOptions" />
                    </a-form-item>
                </a-col>
                <a-col :span="12">
                    <a-form-item label="图标：" name="icon">
                        <a-input v-model:value="formData.icon" placeholder="请输入图标" allow-clear />
                    </a-form-item>
                </a-col>
                <a-col :span="12">
                    <a-form-item label="颜色：" name="color">
                        <a-input v-model:value="formData.color" placeholder="请输入颜色" allow-clear />
                    </a-form-item>
                </a-col>
                <a-col :span="12">
                    <a-form-item label="是否正规则：" name="isRegExp">
                        <a-radio-group v-model:value="formData.isRegExp" placeholder="请选择正规则" :options="isRegExpOptions" />
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
    </a-drawer>
</template>

<script setup name="mobileMenuForm">
    import tool from '@/utils/tool'
    import { cloneDeep } from 'lodash-es'
    import { required } from '@/utils/formRules'
    import mobileMenuApi from '@/api/mobile/mobileMenuApi'
    // 抽屉状态
    const visible = ref(false)
    const emit = defineEmits({ successful: null })
    const formRef = ref()
    // 表单数据
    const formData = ref({})
    const submitLoading = ref(false)
    const categoryOptions = ref([])
    const isRegExpOptions = ref([])
    const statusOptions = ref([])

    // 打开抽屉
    const onOpen = (record) => {
        visible.value = true
        if (record) {
            let recordData = cloneDeep(record)
            formData.value = Object.assign({}, recordData)
        }
        categoryOptions.value = tool.dictList('MOBILE_CATEGORY')
        isRegExpOptions.value = tool.dictList('MOBILE_IS_REG_EXP')
        statusOptions.value = tool.dictList('MOBILE_STATUS')
    }
    // 关闭抽屉
    const onClose = () => {
        formRef.value.resetFields()
        formData.value = {}
        visible.value = false
    }
    // 默认要校验的
    const formRules = {
        parentId: [required('请输入父ID')],
        title: [required('请输入名称')],
        pages: [required('请输入界面路径')],
        category: [required('请输入分类')],
        icon: [required('请输入图标')],
        color: [required('请输入颜色')],
        isRegExp: [required('请输入正规则')],
        status: [required('请输入可用状态')],
    }
    // 验证并提交数据
    const onSubmit = () => {
        formRef.value
            .validate()
            .then(() => {
                submitLoading.value = true
                const formDataParam = cloneDeep(formData.value)
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
