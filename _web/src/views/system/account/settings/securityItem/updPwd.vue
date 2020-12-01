<template>
  <a-modal
    title="修改密码"
    :visible="visible_updPwd"
    :confirm-loading="confirmLoading"
    @ok="handleOkUpdPwd"
    @cancel="handleCancel"
  >
    <a-form :form="formUpdPwd">
      <a-form-item
        label="原密码"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        has-feedback
      >
        <a-input placeholder="请输入原密码" type="password" v-decorator="['password', {rules: [{required: true, message: '请输入原密码！'}]}]" />
      </a-form-item>
      <a-form-item
        label="新密码"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        has-feedback
      >
        <a-input
          placeholder="请输入新密码"
          type="password"
          v-decorator="['newPassword', {rules: [{required: true, message: '请输入新密码！'},{
            validator: validateToNextPassword,
          },]}]" />
      </a-form-item>
      <a-form-item
        label="重复新密码"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        has-feedback
      >
        <a-input
          placeholder="请再次输入新密码"
          type="password"
          v-decorator="['confirm', {rules: [{required: true, message: '请再次输入新密码！'},
                                            {
                                              validator: compareToFirstPassword,
                                            }]}]" />
      </a-form-item>
    </a-form>
  </a-modal>

</template>

<script>
  import { sysUserUpdatePwd } from '@/api/modular/system/userManage'
  export default {
    data () {
      return {
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 }
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 }
        },
        confirmLoading: false,
        visible_updPwd: false,
        userId: '',
        formUpdPwd: this.$form.createForm(this)
      }
    },
    methods: {
      open (id) {
        this.userId = id
        this.visible_updPwd = true
      },
      handleOkUpdPwd () {
        const { formUpdPwd: { validateFields } } = this
        validateFields((errors, values) => {
          if (!errors) {
            this.confirmLoading = true
            values.id = this.userId
            sysUserUpdatePwd(values).then((res) => {
              if (res.success) {
                this.$message.success('修改成功')
                this.handleCancel()
              } else {
                this.$message.error('修改失败：' + res.message)
              }
            // eslint-disable-next-line handle-callback-err
            }).finally((err) => {
              this.confirmLoading = false
            })
          }
        })
      },
      handleCancel () {
        this.visible_updPwd = false
      },
      compareToFirstPassword (rule, value, callback) {
        const formUpdPwd = this.formUpdPwd
        if (value && value !== formUpdPwd.getFieldValue('newPassword')) {
          // eslint-disable-next-line standard/no-callback-literal
          callback('请确认两次输入密码的一致性！')
        } else {
          callback()
        }
      },
      validateToNextPassword (rule, value, callback) {
        const formUpdPwd = this.formUpdPwd
        if (value && this.confirmDirty) {
          formUpdPwd.validateFields(['confirm'], { force: true })
        }
        callback()
      }
    }
  }
</script>
