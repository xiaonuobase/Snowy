<template>
  <div>
    <div id="editor" ref="myEditor"></div>
    <slot></slot>
  </div>
</template>
<script>
  import WangEditor from 'wangeditor'
  export default {
    name: 'ComponentWangeditor',
    data () {
      return {
        edit: ''
      }
    },
    props: {
      value: {
        type: String,
        default: ''
      },
      config: {
        type: Object,
        default: () => {
          return {}
        }
      },
      uploadConfig: {
        type: Object,
        default: () => {
          return {
            method: 'http', // 支持custom(objurl)和http(服务器)和base64
            url: '/'
          }
        }
      }
    },
    computed: {
      customConfig () {
        return {
          pasteFilterStyle: false, // 关闭掉粘贴样式的过滤
          pasteIgnoreImg: false, // 粘贴时不忽略图片
          ...this.config
        }
      }
    },
    watch: {

    },
    components: {

    },
    methods: {
      readBlobAsDataURL (blob, callback) {
        var a = new FileReader()
        a.onload = function (e) { callback(e.target.result) }
        a.readAsDataURL(blob)
      },
      initEditor () {
        var self = this
        this.editor = new WangEditor(this.$refs.myEditor)
        // 配置 onchange 事件
        this.editor.customConfig = this.customConfig
        this.editor.customConfig.uploadImgMaxLength = 5
        this.editor.change = function () { // 编辑区域内容变化时
          self.$emit('input', this.txt.html())
          self.$emit('onchange', this.txt.html(), this.txt)
          // editor.txt.html('.....') //设置编辑器内容
          // editor.txt.clear() //清空编辑器内容
          // editor.txt.append('<p>追加的内容</p>')//继续追加内容。
          // editor.txt.text()  // 读取 text
          // editor.txt.getJSON()  // 获取 JSON 格式的内容
        }
        this.editor.customConfig.customUploadImg = function (files, insert) {
          if (self.uploadConfig.method === 'custom') {
            files.forEach(file => {
              var fileUrl = URL.createObjectURL(file)
              insert(fileUrl)
            })
          }
          if (self.uploadConfig.method === 'base64') {
            files.forEach(file => {
              self.readBlobAsDataURL(file, function (dataurl) {
                insert(dataurl)
              })
            })
          }
          if (self.uploadConfig.method === 'http') {
            if (self.uploadConfig.callback) {
              self.uploadConfig.callback(files, insert)
            } else {
              var formData = new FormData()
              files.forEach(file => {
                formData.append('file', file)
              })
              self.$axios.post(self.uploadConfig.url, formData).then(({ data }) => {
                if (data.status === 'success') {
                  insert(data.url)
                }
              })
            }
          }
        }

        this.editor.create() // 生成编辑器
        this.editor.txt.text(this.value) // 生成编辑器
        this.$emit('oninit', this.editor)
      }
    },
    beforeCreate () {
    },
    created () {
    },
    beforeMount () {
    },
    mounted () {
      this.initEditor()
    }
  }
</script>

<style >
  .w-e-toolbar{
    flex-wrap:wrap;
  }

</style>
