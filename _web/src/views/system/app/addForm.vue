<template>
  <a-modal
    title="新增应用"
    :width="900"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleSubmit"
    @cancel="handleCancel"
  >
    <a-spin :spinning="confirmLoading">

      <a-form :form="form">
        <a-form-item
          style="display: none;"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
        >
          <a-input v-decorator="['active']" />
        </a-form-item>
        <a-form-item
          label="应用名称"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          has-feedback
        >
          <a-input placeholder="请输入应用名称" v-decorator="['name', {rules: [{required: true, message: '请输入应用名称！'}]}]" />
        </a-form-item>

        <a-form-item
          label="唯一编码"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          has-feedback
        >
          <a-input placeholder="请输入唯一编码" v-decorator="['code', {rules: [{required: true, message: '请输入唯一编码！'}]}]" />
        </a-form-item>

      </a-form>

    </a-spin>
  </a-modal>
</template>

<script>
  import { sysAppAdd } from '@/api/modular/system/appManage'

  export default {
    data () {
      return {
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 }
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 15 }
        },
        visible: false,
        confirmLoading: false,
        form: this.$form.createForm(this)
      }
    },
    methods: {

      // 初始化方法
      add () {
        this.visible = true
        this.form.getFieldDecorator('active', { initialValue: 'N' })
      },

      handleSubmit () {
        const { form: { validateFields } } = this
        this.confirmLoading = true
        validateFields((errors, values) => {
          if (!errors) {
            sysAppAdd(values).then((res) => {
              this.confirmLoading = false
              if (res.success) {
                this.$message.success('新增成功')
                this.handleCancel()
                this.$emit('ok', values)
              } else {
                this.$message.error('新增失败：' + res.message)
              }
            }).finally((res) => {
              this.confirmLoading = false
            })
          } else {
            this.confirmLoading = false
          }
        })
      },
      handleCancel () {
        this.form.resetFields()
        this.visible = false
      }
    }
  }
</script>
