<template>
  <a-config-provider :locale="locale">
    <div id="app" class="app app1">
      <router-view class="scrollbar"/>
    </div>
  </a-config-provider>
</template>

<script>
import zhCN from 'ant-design-vue/lib/locale-provider/zh_CN'
import { AppDeviceEnquire } from '@/utils/mixin'

export default {
  mixins: [AppDeviceEnquire],
  data () {
    return {
      locale: zhCN
    }
  },
  mounted () {

  }
}
</script>
<style>
  .app {
    overflow: auto;
    border  : none;
  }
  .scrollbar {
    margin: 0 auto;
  }
  .app1::-webkit-scrollbar {
    /*滚动条整体样式*/
    width : 8px;  /*高宽分别对应横竖滚动条的尺寸*/
  }
  .app1::-webkit-scrollbar-thumb {
    /*滚动条里面小方块*/
    border-radius: 6px;
    background   : #aaa;
  }
  .app1::-webkit-scrollbar-track {
    /*滚动条里面轨道*/
    border-radius: 8px;
    background   : #FFFFFF;
  }
</style>
