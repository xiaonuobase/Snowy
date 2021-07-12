<template>
  <a-modal
    title="文件信息详情"
    :footer="null"
    :width="900"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @cancel="handleCancel"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-form-item v-show="false">
          <a-input v-decorator="['id']" />
        </a-form-item>
        <a-form-item
          label="文件存储位置"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
        >
          {{ fileDetail.fileLocation }}
        </a-form-item>
        <a-form-item
          label="文件仓库"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
        >
          {{ fileDetail.fileBucket }}
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="文件名称"
        >
          {{ fileDetail.fileOriginName }}
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="文件后缀"
        >
          {{ fileDetail.fileSuffix }}
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="文件大小"
        >
          {{ fileDetail.fileSizeKb }}
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="唯一标识"
        >
          {{ fileDetail.fileObjectName }}
        </a-form-item>

        <a-form-item
          label="存储路径"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
        >
          {{ fileDetail.filePath }}
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>
<script>
  export default {
    data () {
      return {
        labelCol: {
          xs: { span: 24 },
          sm: { span: 8 }
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 15 }
        },
        fileDetail: [],
        visible: false,
        confirmLoading: false,
        form: this.$form.createForm(this)
      }
    },
    methods: {
      // 初始化方法
      detail (record) {
        this.fileDetail = record
        this.visible = true
      },
      handleCancel () {
        this.form.resetFields()
        this.visible = false
      }
    }
  }
</script>
