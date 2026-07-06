# Hooks 使用说明

## useLoading

轻量级 loading 状态管理 hook，适用于需要手动控制加载状态的场景。

### 参数

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| name | string | 是 | - | 命名前缀，用于生成动态变量名 |
| initialValue | boolean | 否 | false | loading 初始值 |

### 返回值

以 `name = 'user'` 为例：

| 返回值 | 类型 | 说明 |
|--------|------|------|
| userLoading | Ref\<boolean\> | 响应式 loading 状态 |
| startUserLoading | () => void | 开启 loading |
| stopUserLoading | () => void | 关闭 loading |

### 使用示例

```vue
<template>
  <div v-if="userLoading">加载中...</div>
  <button :disabled="userLoading" @click="handleFetch">获取数据</button>
</template>

<script setup>
import { useLoading } from '@/hooks/useLoading'
import userApi from '@/api/sys/userApi'

const { userLoading, startUserLoading, stopUserLoading } = useLoading('user')

const handleFetch = () => {
  startUserLoading()
  userApi.userPage().then((data) => {
    // 处理数据
  }).finally(() => {
    stopUserLoading()
  })
}
</script>
```

---

## useRequest

请求状态管理 hook，自动管理 loading、data、error 状态。

### 参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | string | 是 | 命名前缀，用于生成动态变量名 |
| service | Function | 是 | 返回 Promise 的请求函数 |

### 返回值

以 `name = 'user'` 为例：

| 返回值 | 类型 | 说明 |
|--------|------|------|
| userData | Ref | 请求返回的数据 |
| userError | Ref | 请求错误信息 |
| userLoading | Ref\<boolean\> | loading 状态（仅 fetchXxx 会自动管理） |
| fetchUser | (...args) => Promise | 发起请求，自动管理 loading/data/error |
| fetchUserAsync | (...args) => Promise | 发起请求，不管理状态，返回原始 Promise |

### 使用示例

#### 自动管理状态模式（fetchXxx）

```vue
<template>
  <a-spin :spinning="userLoading">
    <div>{{ userData }}</div>
  </a-spin>
</template>

<script setup>
import { useRequest } from '@/hooks/useRequest'
import userApi from '@/api/sys/userApi'

const { fetchUser, userData, userLoading, userError } = useRequest('user', userApi.userPage)

// 触发请求，loading/data/error 自动管理
fetchUser({ page: 1, size: 10 })
</script>
```

#### 纯 Promise 模式（fetchXxxAsync）

```vue
<script setup>
import { useRequest } from '@/hooks/useRequest'
import userApi from '@/api/sys/userApi'

const { fetchUserAsync } = useRequest('user', userApi.userPage)

// 仅执行请求，不修改内部状态，适合在已有 loading 控制的场景中使用
fetchUserAsync({ page: 1 }).then((data) => {
  // 自行处理数据
})
</script>
```

---

## useLoading 与 useRequest 的选择

| 场景 | 推荐 |
|------|------|
| 单个接口请求，需要自动管理状态 | useRequest |
| 多个接口串行/并行，需要统一 loading | useLoading |
| 非接口场景的 loading（如文件上传进度） | useLoading |
