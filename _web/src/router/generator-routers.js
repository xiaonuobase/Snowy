
import { BasicLayout, BlankLayout, PageView, RouteView, Iframe } from '@/layouts'

// 前端路由表
const constantRouterComponents = {
  // 基础页面 layout 必须引入
  BasicLayout: BasicLayout,
  BlankLayout: BlankLayout,
  RouteView: RouteView,
  PageView: PageView,
  Iframe: Iframe,
  '403': () => import('@/views/system/exception/403'),
  '404': () => import('@/views/system/exception/404'),
  '500': () => import('@/views/system/exception/500'),

  'Workplace': () => import('@/views/system/dashboard/Workplace'),
  // account
  'AccountCenter': () => import('@/views/system/account/center/Index'),
  'AccountSettings': () => import('@/views/system/account/settings/Index'),
  'BaseSettings': () => import('@/views/system/account/settings/BaseSetting'),
  'SecuritySettings': () => import('@/views/system/account/settings/Security'),
  'CustomSettings': () => import('@/views/system/account/settings/Custom'),
  'BindingSettings': () => import('@/views/system/account/settings/Binding'),
  'NotificationSettings': () => import('@/views/system/account/settings/Notification'),

  // 默认首页
  'Console': () => import('@/views/system/index/welcome')
}

// 前端未找到页面路由（固定不用改）、原来为 /404
const notFoundRouter = {
  path: '*', redirect: '/welcome', hidden: true
}
// 个人中心页面
const userAccount = [
  // account
  {
    'name': 'account',
    'pid': 0,
    'id': 10028,
    'meta': {
      'title': '个人页',
      'icon': 'user',
      'show': false
    },
    'redirect': '/account/center',
    'component': 'RouteView'
  },
  {
    'name': 'center',
    'pid': 10028,
    'id': 10029,
    'meta': {
      'title': '个人中心',
      'show': false
    },
    'component': 'AccountCenter'
  },
  // 特殊三级菜单
  {
    'name': 'settings',
    'pid': '10028',
    'id': '10030',
    'meta': {
      'title': '个人设置',
      'hideHeader': true,
      'hideChildren': true,
      'show': false
    },
    'redirect': '/account/settings/base',
    'component': 'AccountSettings'
  },
  {
    'name': 'BaseSettings',
    'path': '/account/settings/base',
    'pid': 10030,
    'id': 10031,
    'meta': {
      'title': '基本设置',
      'show': false
    },
    'component': 'BaseSettings'
  },
  {
    'name': 'SecuritySettings',
    'path': '/account/settings/security',
    'pid': 10030,
    'id': 10032,
    'meta': {
      'title': '安全设置',
      'show': false
    },
    'component': 'SecuritySettings'
  },
  {
    'name': 'CustomSettings',
    'path': '/account/settings/custom',
    'pid': 10030,
    'id': 10033,
    'meta': {
      'title': '个性化设置',
      'show': false
    },
    'component': 'CustomSettings'
  },
  {
    'name': 'BindingSettings',
    'path': '/account/settings/binding',
    'pid': 10030,
    'id': 10034,
    'meta': {
      'title': '账户绑定',
      'show': false
    },
    'component': 'BindingSettings'
  },
  {
    'name': 'NotificationSettings',
    'path': '/account/settings/notification',
    'pid': 10030,
    'id': 10034,
    'meta': {
      'title': '新消息通知',
      'show': false
    },
    'component': 'NotificationSettings'
  },
  {
    'name': 'Console',
    'path': '/welcome',
    'pid': 0,
    'id': 183183,
    'meta': {
      'title': '首页',
      'show': false
    },
    'component': 'Console'
  }

]

// 根级菜单
const rootRouter = {
  key: '',
  name: 'MenuIndex.vue',
  path: '',
  component: 'BasicLayout',
  redirect: '/welcome',
  meta: {
    title: '首页'
  },
  children: []
}

/**
 * 动态生成菜单
 * @param data
 * @returns {Promise<Router>}
 */
export const generatorDynamicRouter = (data) => {
  return new Promise((resolve, reject) => {
    const resNav = data.antDesignmenus
    const menuNav = []
    const childrenNav = []
    //      后端数据, 根级树数组,  根级 PID
    listToTree(resNav, childrenNav, 0)

    /**
     * 增加静态网页
     */
    listToTree(userAccount, childrenNav, 0)
    rootRouter.children = childrenNav
    menuNav.push(rootRouter)
    const routers = generator(menuNav)
    routers.push(notFoundRouter)
    resolve(routers)
  }).catch(err => {
    // reject('加载菜单失败')
    return Promise.reject(err)
  })
}

/**
 * 格式化树形结构数据 生成 vue-router 层级路由表
 *
 * @param routerMap
 * @param parent
 * @returns {*}
 */
export const generator = (routerMap, parent) => {
  return routerMap.map(item => {
    // eslint-disable-next-line no-unused-vars
    const { title, show, hideChildren, hiddenHeaderContent, target, icon, link } = item.meta || {}
    const currentRouter = {
      // 如果路由设置了 path，则作为默认 path，否则 路由地址 动态拼接生成如 /dashboard/workplace
      path: item.path || `${parent && parent.path || ''}/${item.key}`,
      // 路由名称，建议唯一
      name: item.name || item.key || '',
      // 该路由对应页面的 组件 :方案1
      // component: constantRouterComponents[item.component || item.key],
      // 该路由对应页面的 组件 :方案2 (动态加载)
      component: (constantRouterComponents[item.component || item.key]) || (() => import(`@/views/${item.component}`)),
      // meta: 页面标题, 菜单图标, 页面权限(供指令权限用，可去掉)
      meta: {
        title: title,
        icon: icon || undefined,
        // hiddenHeaderContent: hiddenHeaderContent,
        target: target,
        link: link
      }
    }
    // 是否设置了隐藏菜单
    if (show === false) {
      currentRouter.hidden = true
    }
    // 是否设置了隐藏子菜单
    if (hideChildren) {
      currentRouter.hideChildrenInMenu = true
    }
    // 为了防止出现后端返回结果不规范，处理有可能出现拼接出两个 反斜杠
    if (!currentRouter.path.startsWith('http')) {
      currentRouter.path = currentRouter.path.replace('//', '/')
    }
    // 重定向
    item.redirect && (currentRouter.redirect = item.redirect)
    // 是否有子菜单，并递归处理
    if (item.children && item.children.length > 0) {
      // Recursion
      currentRouter.children = generator(item.children, currentRouter)
    }
    return currentRouter
  })
}

/**
 * 数组转树形结构
 * @param list 源数组
 * @param tree 树
 * @param parentId 父ID
 */
const listToTree = (list, tree, parentId) => {
  list.forEach(item => {
    // 判断是否为父级菜单
    // eslint-disable-next-line eqeqeq
    if (item.pid == parentId) {
      const child = {
        ...item,
        key: item.key || item.name,
        children: []
      }
      // 迭代 list， 找到当前菜单相符合的所有子菜单
      listToTree(list, child.children, item.id)
      // 删掉不存在 children 值的属性
      if (child.children.length <= 0) {
        delete child.children
      }
      // 加入到树中
      tree.push(child)
    }
  })
}
