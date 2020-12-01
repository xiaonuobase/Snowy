import Vue from 'vue'
import router from './router'
import store from './store'

import NProgress from 'nprogress' // progress bar
import '@/components/NProgress/nprogress.less' // progress bar custom style
import { setDocumentTitle, domTitle } from '@/utils/domUtil'
import { ACCESS_TOKEN, ALL_APPS_MENU } from '@/store/mutation-types'

import { Modal, notification } from 'ant-design-vue' // NProgress Configuration
import { timeFix } from '@/utils/util'/// es/notification
NProgress.configure({ showSpinner: false })
const whiteList = ['login', 'register', 'registerResult'] // no redirect whitelist
// 无默认首页的情况
const defaultRoutePath = '/welcome'

router.beforeEach((to, from, next) => {
  NProgress.start() // start progress bar
  to.meta && (typeof to.meta.title !== 'undefined' && setDocumentTitle(`${to.meta.title} - ${domTitle}`))
  if (Vue.ls.get(ACCESS_TOKEN)) {
    /* has token */
    if (to.path === '/user/login') {
      next({ path: defaultRoutePath })
      NProgress.done()
    } else {
      if (store.getters.roles.length === 0) {
        store
          .dispatch('GetInfo')
          .then(res => {
            if (res.menus.length < 1) {
              Modal.error({
                title: '提示：',
                content: '无菜单权限，请联系管理员',
                okText: '确定',
                onOk: () => {
                  store.dispatch('Logout').then(() => {
                    window.location.reload()
                  })
                }
              })
              return
            }
            // eslint-disable-next-line camelcase
            const all_app_menu = Vue.ls.get(ALL_APPS_MENU)
            let antDesignmenus
            // eslint-disable-next-line camelcase
            if (all_app_menu == null) {
              const applocation = []
              res.apps.forEach(item => {
                const apps = { 'code': '', 'name': '', 'active': '', 'menu': '' }
                if (item.active) {
                  apps.code = item.code
                  apps.name = item.name
                  apps.active = item.active
                  apps.menu = res.menus
                  antDesignmenus = res.menus
                } else {
                  apps.code = item.code
                  apps.name = item.name
                  apps.active = item.active
                  apps.menu = ''
                }
                applocation.push(apps)
              })
              Vue.ls.set(ALL_APPS_MENU, applocation, 7 * 24 * 60 * 60 * 1000)
              // 延迟 1 秒显示欢迎信息
              setTimeout(() => {
                notification.success({
                  message: '欢迎',
                  description: `${timeFix()}，欢迎回来`
                })
              }, 1000)
            } else {
              antDesignmenus = Vue.ls.get(ALL_APPS_MENU)[0].menu
            }
            store.dispatch('GenerateRoutes', { antDesignmenus }).then(() => {
              // 动态添加可访问路由表
              router.addRoutes(store.getters.addRouters)
              // 请求带有 redirect 重定向时，登录自动重定向到该地址
              const redirect = decodeURIComponent(from.query.redirect || to.path)
              if (to.path === redirect) {
                next({ path: redirect })
                // hack方法 确保addRoutes已完成 ,set the replace: true so the navigation will not leave a history record
                next({ ...to, replace: true })
              } else {
                // 跳转到目的路由
                next({ path: redirect })
              }
            })
          })
          .catch(() => {
            store.dispatch('Logout').then(() => {
              next({ path: '/user/login', query: { redirect: to.fullPath } })
            })
          })
      } else {
        next()
      }
    }
  } else {
    if (whiteList.includes(to.name)) {
      // 在免登录白名单，直接进入
      next()
    } else {
      next({ path: '/user/login', query: { redirect: to.fullPath } })
      NProgress.done() // if current page is login will not trigger afterEach hook, so manually handle it
    }
  }
})

router.afterEach(() => {
  NProgress.done() // finish progress bar
})
