<template>
  <a-card :bordered="false" v-show="indexCodeViewShow">
    <div class="table-operator">
      <a-button class="but_item" type="dashed" @click="handleCancel" icon="rollback">返回</a-button>
    </div>
    <a-tabs v-model="activeKey" tab-position="left" hide-add type="card" >
      <a-tab-pane v-for="pane in panes" :key="pane.key" :tab="pane.title" closable="closable" >
        <div>
          <a-button type="primary" class="but_item" @click="copyContentCode(pane.content)" icon="copy">复制代码</a-button>
        </div>
        <a-textarea v-model="pane.content" autosize="true" ref="inputText" />
      </a-tab-pane>
    </a-tabs>
  </a-card>
</template>
<script>
  import { codeGenerateRunFileContent } from '@/api/modular/gen/codeGenerateManage'
  export default {
    data () {
      return {
        activeKey: 'null',
        panes: [],
        indexCodeViewShow: false,
        tableLoading: false,
        visible: false
      }
    },
    methods: {
      /**
       * 打开界面
       */
      open (data) {
        this.indexCodeViewShow = true
        this.tableLoading = true
        codeGenerateRunFileContent(data).then((res) => {
          if (res.success) {
            let tabi = 1
            this.loadData = res.data
            this.loadData.forEach(item => {
              this.panes.push({ title: item.fileName, content: item.fileContent, key: 'tab' + tabi++ })
              this.code = item.fileContent
            })
          }
          this.activeKey = this.panes[0].key
          this.tableLoading = false
        })
      },
      copyContentCode (code) {
        var input = document.createElement('textarea')
        input.value = code
        document.body.appendChild(input)
        input.select()
        document.execCommand('Copy')
        document.body.removeChild(input)
      },
      handleCancel () {
        this.$emit('ok')
        this.loadData = []
        this.panes = []
        this.indexCodeViewShow = false
      }
    }
  }
</script>
