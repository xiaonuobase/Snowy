<template>
  <a-modal
    title="通知公告详情"
    :width="1000"
    :confirmLoading="confirmLoading"
    :visible="visible"
    :footer="null"
    @cancel="handleCancel"
  >
    <a-spin :spinning="confirmLoading">

      <div style="text-align: center;font-size: 30px">{{ this.contentRecord.title }}</div>
      <br>
      <div style="text-align: right;font-size: 10px">
        <span>（发布人：{{ this.contentRecord.publicUserName }}）</span>
        <span>发布时间：{{ this.contentRecord.publicTime }} </span>
      </div>
      <a-divider style="margin-top: 5px"/>
      <div >
        <label v-html="this.contentRecord.content"></label>
      </div>

    </a-spin>
  </a-modal>
</template>

<script>
  import { sysNoticeDetail } from '@/api/modular/system/noticeManage'

  export default {
    name: 'DetailForm',
    components: {
    },

    data () {
      return {
        visible: false,
        confirmLoading: false,
        contentRecord: ''
      }
    },

    methods: {

      // 初始化方法
      detail (record) {
        this.confirmLoading = true
        this.visible = true
        this.sysNoticeDetail(record.id)
      },

      /**
       * 查看详情
       */
      sysNoticeDetail (id) {
        sysNoticeDetail({ id: id }).then((res) => {
          this.confirmLoading = false
          this.contentRecord = res.data
        })
      },

      handleCancel () {
        this.visible = false
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
