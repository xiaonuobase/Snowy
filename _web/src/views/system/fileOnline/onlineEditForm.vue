<template>
  <a-modal
    title="在线编辑"
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
          isEdit: true,
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
          review: false,
          type: 'desktop'
        }
      }
    },
    methods: {
      /**
       * 初始化
       */
      onlineEdit(record) {
        this.visible = true
        const data = record.data.sysOnlineFileInfoResult
        this.option.user.id = data.editorConfig.user.id
        this.option.user.name = data.editorConfig.user.name
        this.option.fileType = data.document.fileType
        this.option.title = data.document.title
        this.option.key = data.document.key
        this.option.url = process.env.VUE_APP_API_BASE_URL + data.document.url // res.data.docServiceApiUrl
        this.callbackUrl = process.env.VUE_APP_API_BASE_URL + data.editorConfig.callbackUrl
        // this.option.type = type
        this.option.review = false
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
      }
    }
  }
</script>
<style>
  .editorview iframe{
    position: absolute !important;
  }
</style>
