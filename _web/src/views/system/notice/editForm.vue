<template>
  <a-modal
    title="编辑通知公告"
    :width="1000"
    :footer="null"
    :visible="visible"
    @cancel="handleCancel"
  >
    <a-spin :spinning="formLoading">
      <a-form :form="form">
        <a-form-item v-show="false">
          <a-input v-decorator="['id']" />
        </a-form-item>
        <a-form-item
          label="标题"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
        >
          <a-input placeholder="请输入标题" v-decorator="['title', {rules: [{required: true, message: '请输入标题！'}]}]" />
        </a-form-item>
        <a-form-item
          label="类型"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
        >
          <a-radio-group v-decorator="['type',{rules: [{ required: true, message: '请选择类型！' }]}]" >
            <a-radio-button v-for="(item,index) in typeDictTypeDropDown" :key="index" :value="item.code">{{ item.value }}</a-radio-button>
          </a-radio-group>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="内容"
        >
          <antd-editor :uploadConfig="editorUploadConfig" v-model="editorContent" @onchange="changeEditor" @oninit="getEditor" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="通知到的人"
        >
          <a-transfer
            :data-source="mockData"
            show-search
            :list-style="{
              width: '40%',
              height: '300px',
            }"
            :filter-option="filterOption"
            :target-keys="targetKeys"
            :render="item => item.title"
            @change="handleChange"
          />
        </a-form-item>
        <a-divider />
        <a-form-item class="subForm-item">
          <a-button type="primary" class="subButton" @click="handleSubmit('1')">发布</a-button>
          <a-button type="danger" class="subButton" @click="handleSubmit('0')">存为草稿</a-button>
          <a-button class="subButton" @click="handleCancel">取消</a-button>
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>
<script>
  import { sysNoticeEdit, sysNoticeDetail } from '@/api/modular/system/noticeManage'
  import { sysDictTypeDropDown } from '@/api/modular/system/dictManage'
  import { sysFileInfoUpload } from '@/api/modular/system/fileManage'
  import { AntdEditor } from '@/components'
  import { sysUserSelector } from '@/api/modular/system/userManage'
  export default {
    name: 'AddForm',
    components: {
      AntdEditor
    },
    data () {
      return {
        labelCol: {
          xs: { span: 24 },
          sm: { span: 3 }
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 18 }
        },
        visible: false,
        form: this.$form.createForm(this),
        typeDictTypeDropDown: [], // 0通知 1公告
        editorContent: '',
        editorContentText: '',
        editorUploadConfig: {
          method: 'http',
          callback: this.editorUploadImage
        },
        mockData: [],
        targetKeys: [],
        noticeDetail: [],
        formLoading: true
      }
    },
    methods: {
      // 初始化方法
      edit (record) {
        this.visible = true
        this.sysNoticeDetail(record.id)
        this.sysDictTypeDropDown()
        setTimeout(() => {
          this.form.setFieldsValue(
            {
              id: record.id,
              title: record.title,
              type: record.type.toString()
            }
          )
          this.editor.txt.html(record.content)
          this.editorContent = record.content
        }, 100)
      },
      /**
       * 获取字典数据
       */
      sysDictTypeDropDown () {
        sysDictTypeDropDown({ code: 'notice_type' }).then((res) => {
          this.typeDictTypeDropDown = res.data
        })
      },
      /**
       * 编辑器回调上传及回传图片url
       */
      editorUploadImage (files, insert) {
        const formData = new FormData()
        files.forEach(file => {
          formData.append('file', file)
        })
        sysFileInfoUpload(formData).then((res) => {
          if (res.success) {
            insert(process.env.VUE_APP_API_BASE_URL + '/sysFileInfo/preview?id=' + res.data)
          } else {
            this.$message.error('编辑器上传图片失败：' + res.message)
          }
        })
      },
      getEditor (editor) {
        this.editor = editor
      },
      changeEditor (html, ele) {
        this.editorContent = html
        this.editorContentText = ele.text()
      },
      /**
       * 编辑时获取全部信息
       */
      sysNoticeDetail (id) {
        sysNoticeDetail({ id: id }).then((res) => {
          this.noticeDetail = res.data
          this.getMock(this.noticeDetail)
        })
      },
      /**
       * 穿梭框
       */
      getMock (noticeDetail) {
        const targetKeys = []
        const mockData = []
          sysUserSelector().then((res) => {
            this.formLoading = false
            for (let i = 0; i < res.data.length; i++) {
              const data = {
                key: res.data[i].id.toString(),
                title: res.data[i].name,
                description: `description of ${res.data[i].name}`
              }
              for (let j = 0; j < noticeDetail.noticeUserIdList.length; j++) {
                if (data.key === noticeDetail.noticeUserIdList[j]) {
                  targetKeys.push(noticeDetail.noticeUserIdList[j])
                }
              }
              mockData.push(data)
            }
          })
        this.mockData = mockData
        this.targetKeys = targetKeys
      },
      filterOption (inputValue, option) {
        return option.description.indexOf(inputValue) > -1
      },
      handleChange (targetKeys, direction, moveKeys) {
        this.targetKeys = targetKeys
      },
      handleSubmit (types) {
        const { form: { validateFields } } = this
        // eslint-disable-next-line eqeqeq
        if (this.editorContent == '') {
          this.$message.error('请填写内容')
          return
        }
        if (this.targetKeys.length < 1) {
          this.$message.error('请选择通知到的人')
          return
        }
        validateFields((errors, values) => {
          if (!errors) {
            this.formLoading = true
            values.content = this.editorContent
            values.status = types
            values.noticeUserIdList = this.targetKeys
            sysNoticeEdit(values).then((res) => {
              if (res.success) {
                this.$message.success('编辑成功')
                this.visible = false
                this.$emit('ok', values)
                this.handleCancel()
              } else {
                this.$message.error('编辑失败：' + res.message)
              }
            }).finally((res) => {
              this.formLoading = false
            })
          }
        })
      },
      handleCancel () {
        this.editor.txt.clear()
        this.targetKeys = []
        this.editorContent = ''
        this.form.resetFields()
        this.visible = false
        this.formLoading = true
      }
    }
  }
</script>
<style>
  .subButton{
    float: right;
  }
  .subForm-item{
    margin-bottom: 0px;
  }
</style>
