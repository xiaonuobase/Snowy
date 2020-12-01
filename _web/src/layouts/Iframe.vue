
<script>
  import Vue from 'vue'
  import { ACCESS_TOKEN } from '@/store/mutation-types'

    export default {
      name: 'Iframe',
      data () {
          return {
          }
      },
      render () {
        const { $route: { meta: { link } } } = this
        if ({ link }.link === '') {
           return '404'
        }
        let url = ''
        if ({ link }.link.indexOf('token=') > -1) {
          url = { link }.link + Vue.ls.get(ACCESS_TOKEN)
        } else {
          url = { link }.link
        }
        let height = ''
        const deviceHeight = document.documentElement.clientHeight
        height = (Number(deviceHeight) - 260) + 'px'
        return <iframe id="iframe" height={height} src={url} style="width:100%;overflow:hidden;" frameBorder="0"></iframe>
      }
   }
</script>
