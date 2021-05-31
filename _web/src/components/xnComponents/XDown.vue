<template>
  <a-tooltip placement="top">
    <template slot="title">
      <span>导出所有数据</span>
    </template>
    <!-- 正常来说，这里加个插槽最好了，但是这个就是为导出准备的，一般这两个字不会变 -->
    <a-button type="dashed" @click="batchExport" :loading="batchExportLoading"><a-icon type="export"/>导出</a-button>
  </a-tooltip>
</template>

<script>
export default {
  name: 'XDown',
  data () {
    return {
      batchExportLoading: false
    }
  },
  methods: {
    /**
     * 本控件中点击按钮事件
     */
    batchExport () {
      this.batchExportLoading = true
      // 将其传达到上个界面
      this.$emit('batchExport', '')
    },

    /**
     * 通过返回的元素通过浏览器下载
     * 也就是接受使用这个组件的地方吧下载的内容传过来，
     * 但是这个组件本公子编写的时候只想的适用于导出excel来使用，下载文件呀图片方面的重新整个组件即可
     */
    downloadfile (res) {
      this.batchExportLoading = false
      var blob = new Blob([res.data], { type: 'application/octet-stream;charset=UTF-8' })
      var contentDisposition = res.headers['content-disposition']
      var patt = new RegExp('filename=([^;]+\\.[^\\.;]+);*')
      var result = patt.exec(contentDisposition)
      var filename = result[1]
      var downloadElement = document.createElement('a')
      var href = window.URL.createObjectURL(blob) // 创建下载的链接
      var reg = /^["](.*)["]$/g
      downloadElement.style.display = 'none'
      downloadElement.href = href
      downloadElement.download = decodeURI(filename.replace(reg, '$1')) // 下载后文件名
      document.body.appendChild(downloadElement)
      downloadElement.click() // 点击下载
      document.body.removeChild(downloadElement) // 下载完成移除元素
      window.URL.revokeObjectURL(href)
    }
  }
}
</script>
