/* eslint-disable no-undef */
<!--onlyoffice 编辑器-->
<template>
  <div id="editorDiv"></div>
</template>

<script>
  import { handleDocType } from '@/utils/onlyofficeUtil'

  export default {
    name: 'Editor',
    props: {
      option: {
        type: Object,
        default: () => {
          return {}
        }
      }
    },
    data() {
      return {
        doctype: ''
      }
    },
    mounted() {
      if (this.option.url) {
        this.setEditor(this.option)
      }
    },
    methods: {
      setEditor(option) {
        this.doctype = handleDocType(option.fileType)
        const config = {
          document: {
            fileType: option.fileType,
            key: option.key,
            title: option.title,
            permissions: {
              comment: true,
              download: true,
              modifyContentControl: true,
              modifyFilter: true,
              print: false,
              edit: option.isEdit,
              fillForms: true
              // review: false
            },
            url: option.url

          },
          type: option.type,
          documentType: this.doctype,
          editorConfig: {
            callbackUrl: option.callbackUrl,
            lang: 'zh',
            customization: {
              commentAuthorOnly: false,
              comments: true,
              compactHeader: false,
              compactToolbar: true,
              feedback: false,
              plugins: true
            },
            user: {
              id: option.user.id,
              name: option.user.name
            }
            // mode: option.mode
          },
          width: '100%',
          height: '100%',
          position: 'absolute',
          token: option.token
        }
        // eslint-disable-next-line no-unused-vars
        let docEditor = null
        // eslint-disable-next-line no-undef
        docEditor = new DocsAPI.DocEditor('editorDiv', config)
      }
    },
    watch: {
      option: {
        handler: function (n, o) {
          this.setEditor(n)
          this.doctype = handleDocType(n.fileType)
        },
        deep: true
      }
    }
  }
</script>

<style scoped>

</style>
