<template>
  <div id="userLayout" :class="['user-layout-wrapper', device]">
    <div class="container">
      <div class="top">
        <div class="header">
          <a href="/">
            <img src="~@/assets/logo.png" class="logo" alt="logo">
            <span class="title">XiaoNuo快速开发平台</span>
          </a>
        </div>
        <div class="desc">

        </div>
      </div>

      <route-view></route-view>

      <div class="footer">
        <div class="links">
          <a href="_self">帮助</a>
          <a href="_self">隐私</a>
          <a href="_self">条款</a>
        </div>
        <div class="copyright">
          Copyright © 2020 <a target="_blank" href="https://www.xiaonuo.vip/">XiaoNuo</a> All rights reserved. xiaonuo-vue 1.2.0
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import RouteView from './RouteView'
import { mixinDevice } from '@/utils/mixin'

export default {
  name: 'UserLayout',
  components: { RouteView },
  mixins: [mixinDevice],
  data () {
    return {}
  },
  mounted () {
    document.body.classList.add('userLayout')
  },
  beforeDestroy () {
    document.body.classList.remove('userLayout')
  }
}
</script>

<style lang="less" scoped>
  #userLayout.user-layout-wrapper {
    height: 100%;

    &.mobile {
      .container {
        .main {
          max-width: 368px;
          width: 98%;
        }
      }
    }

    .container {
      width: 100%;
      min-height: 100%;
      background-color: #ffffff;
      background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 1600 900'%3E%3Cpolygon fill='%2340a7cc' points='957 450 539 900 1396 900'/%3E%3Cpolygon fill='%23c6dbe1' points='957 450 872.9 900 1396 900'/%3E%3Cpolygon fill='%2350a7c5' points='-60 900 398 662 816 900'/%3E%3Cpolygon fill='%23c0d7da' points='337 900 398 662 816 900'/%3E%3Cpolygon fill='%235fa6be' points='1203 546 1552 900 876 900'/%3E%3Cpolygon fill='%23bad3d4' points='1203 546 1552 900 1162 900'/%3E%3Cpolygon fill='%236fa6b8' points='641 695 886 900 367 900'/%3E%3Cpolygon fill='%23b3cfcd' points='587 900 641 695 886 900'/%3E%3Cpolygon fill='%237ea5b1' points='1710 900 1401 632 1096 900'/%3E%3Cpolygon fill='%23adcbc7' points='1710 900 1401 632 1365 900'/%3E%3Cpolygon fill='%238ea5aa' points='1210 900 971 687 725 900'/%3E%3Cpolygon fill='%23a7c7c0' points='943 900 1210 900 971 687'/%3E%3C/svg%3E");
      background-attachment: fixed;
      background-size: cover;
      padding: 110px 0 144px;
      position: relative;

      a {
        text-decoration: none;
      }

      .top {
        text-align: center;

        .header {
          height: 44px;
          line-height: 44px;

          .badge {
            position: absolute;
            display: inline-block;
            line-height: 1;
            vertical-align: middle;
            margin-left: -12px;
            margin-top: -10px;
            opacity: 0.8;
          }

          .logo {
            height: 44px;
            vertical-align: top;
            margin-right: 16px;
            border-style: none;
          }

          .title {
            font-size: 33px;
            color: rgba(0, 0, 0, .85);
            font-family: Avenir, 'Helvetica Neue', Arial, Helvetica, sans-serif;
            font-weight: 600;
            position: relative;
            top: 2px;
          }
        }
        .desc {
          font-size: 14px;
          color: rgba(0, 0, 0, 0.45);
          margin-top: 12px;
          margin-bottom: 40px;
        }
      }

      .main {
        min-width: 260px;
        width: 368px;
        margin: 0 auto;
      }

      .footer {
        position: absolute;
        width: 100%;
        bottom: 0;
        padding: 0 16px;
        margin: 48px 0 24px;
        text-align: center;

        .links {
          margin-bottom: 8px;
          font-size: 14px;
          a {
            color: rgba(0, 0, 0, 0.45);
            transition: all 0.3s;
            &:not(:last-child) {
              margin-right: 40px;
            }
          }
        }
        .copyright {
          color: rgba(0, 0, 0, 0.45);
          font-size: 14px;
        }
      }
    }
  }
</style>
