<template>
  <a-modal
    title="参数编辑"
    :width="900"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleSubmit"
    @cancel="handleCancel"
  >
    <a-spin :spinning="formLoading">
      <a-form :form="form">

        <a-form-item
          style="display: none;"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          has-feedback
        >
          <a-input v-decorator="['id']" />
        </a-form-item>

        <a-form-item
          label="参数名称"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          has-feedback
        >
          <a-input placeholder="请输入参数名称" v-decorator="['name', {rules: [{required: true, message: '请输入参数名称！'}]}]" />
        </a-form-item>

        <a-form-item
          label="唯一编码"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          has-feedback
        >
          <a-input placeholder="请输入唯一编码" :disabled="editDisabled" v-decorator="['code', {rules: [{required: true, message: '请输入唯一编码！'}]}]" />
        </a-form-item>

        <a-form-item
          label="系统参数"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
        >
          <a-radio-group :disabled="editDisabled" v-decorator="['sysFlag',{rules: [{ required: true, message: '请选择是否为系统参数！' }]}]" >
            <a-radio-button value="Y" > 是 </a-radio-button>
            <a-radio-button value="N" >  否 </a-radio-button>
          </a-radio-group>
        </a-form-item>

        <a-form-item
          label="所属分类"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          has-feedback
        >
          <a-select :disabled="editDisabled" style="width: 100%" placeholder="请选择所属分类" v-decorator="['groupCode', {rules: [{ required: true, message: '请选择取所属分类！' }]}]" >
            <a-select-option v-for="(item,index) in groupCodeList" :key="index" :value="item.code" >{{ item.value }}</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="参数值"
          has-feedback
        >
          <a-input placeholder="请输入参数值" v-decorator="['value', {rules: [{required: true, message: '请输入参数值！'}]}]" />
        </a-form-item>

        <a-form-item
          label="备注"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          has-feedback
        >
          <a-textarea :rows="4" placeholder="请输入备注" v-decorator="['remark']"></a-textarea>
        </a-form-item>

      </a-form>

    </a-spin>
  </a-modal>
</template>

<script>
  import { sysDictTypeDropDown, sysConfigEdit } from '@/api/modular/system/configManage'

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
        formLoading: true,
        groupCodeList: [],
        editDisabled: false,
        form: this.$form.createForm(this)
      }
    },
    methods: {
      // 初始化方法
      edit (record) {
        this.visible = true
        setTimeout(() => {
          this.form.setFieldsValue(
            {
              id: record.id,
              name: record.name,
              code: record.code,
              groupCode: record.groupCode,
              sysFlag: record.sysFlag,
              value: record.value,
              remark: record.remark
            }
          )
        }, 100)
        // eslint-disable-next-line eqeqeq
        if (record.sysFlag == 'Y') {
           this.editDisabled = true
        }
        this.sysDictTypeDropDown()
      },

      /**
       * 获取所属分类
       */
      sysDictTypeDropDown () {
        sysDictTypeDropDown({ code: 'consts_type' }).then((res) => {
          this.groupCodeList = res.data
          this.formLoading = false
        })
      },

      handleSubmit () {
        const { form: { validateFields } } = this
        this.confirmLoading = true
        validateFields((errors, values) => {
          if (!errors) {
            sysConfigEdit(values).then((res) => {
              this.confirmLoading = false
              if (res.success) {
                this.$message.success('编辑成功')
                this.$emit('ok', values)
                this.handleCancel()
              } else {
                this.$message.error('编辑失败：' + res.message)
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
        this.editDisabled = false
      }
    }
  }
</script>
