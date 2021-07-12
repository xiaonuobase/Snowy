<template>
  <a-modal
    title="在线预览"
    :footer="null"
    :width="1500"
    :visible="visible"
    @cancel="handleCancel"
    :destroyOnClose="true"
  >
    <a-spin :spinning="divLoading">
      <div class="editorview" style="height: 800px;">
        <Editor :option="option"/>
      </div>
    </a-spin>
  </a-modal>
</template>
<script>
  import Editor from '../../../components/xnComponents/EditorDiv'
  import Vue from 'vue'
  import { ACCESS_TOKEN } from '@/store/mutation-types'

  export default {
    components: {
      Editor
    },
    data () {
      return {
        visible: false,
        divLoading: false,
        sysOnlineFileInfoResult: {},
        option: {
          url: '',
          isEdit: false,
          fileType: '',
          title: '',
          token: Vue.ls.get(ACCESS_TOKEN),
          user: {
            id: '',
            name: ''
          },
          mode: '',
          callbackUrl: '',
          key: '',
          review: false
        }
      }
    },
    created () {

    },
    methods: {
      /**
       * 初始化
       */
      preview(record, type) {
        this.visible = true
        const data = record.data.sysOnlineFileInfoResult
        this.option.user.id = '1265476890672672808' // data.editorConfig.user.id
        this.option.user.name = '超级管理员' // data.editorConfig.user.name
        this.option.fileType = data.document.fileType
        this.option.title = data.document.title
        this.option.key = data.document.key
        this.option.url = process.env.VUE_APP_API_BASE_URL + data.document.url // res.data.docServiceApiUrl
        this.callbackUrl = process.env.VUE_APP_API_BASE_URL + data.editorConfig.callbackUrl
        this.option.type = type
      },
      handleCancel () {
        this.visible = false
        this.option = {
          url: '',
            isEdit: false,
            fileType: '',
            title: '',
            token: Vue.ls.get(ACCESS_TOKEN),
            user: {
            id: '',
              name: ''
          },
          mode: '',
            callbackUrl: '',
            key: '',
            review: false
        }
        const oScript = document.createElement('script')
        oScript.type = 'text/javascript'
        oScript.src = ''
        document.body.appendChild(oScript)
      }
    }
  }
</script>
<style>
  .editorview iframe{
    position: absolute !important;
  }
</style>
