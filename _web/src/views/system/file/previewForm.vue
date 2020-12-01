<template>
  <a-modal
    title="预览图片"
    :footer="null"
    :width="900"
    :visible="visible"
    @cancel="handleCancel"
  >
    <a-spin :spinning="divLoading">
      <div style="text-align: center">
        <img :src="src" style="max-width: 99%">
      </div>
    </a-spin>
  </a-modal>
</template>
<script>
  import { sysFileInfoPreview } from '@/api/modular/system/fileManage'
  export default {
    data () {
      return {
        visible: false,
        src: '',
        divLoading: false
      }
    },
    methods: {
      /**
       * 初始化
       */
      preview (record) {
        this.visible = true
        this.divLoading = true
        this.sysFileInfoPreview(record)
      },
      /**
       * 获取图片并转为链接
       */
      sysFileInfoPreview (record) {
        sysFileInfoPreview({ id: record.id }).then((res) => {
          this.divLoading = false
          this.downloadfile(res)
        }).catch((err) => {
          this.divLoading = false
          this.$message.error('预览错误：' + err.message)
        })
      },
      /**
       * 转图片类型
       */
      downloadfile (res) {
        const blob = new Blob([res])
        this.src = window.URL.createObjectURL(blob)
      },
      handleCancel () {
        this.src = ''
        this.visible = false
      }
    }
  }
</script>
