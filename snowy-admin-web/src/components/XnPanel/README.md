XnPanel 面板容器
====


封装说明
----

> 与 Ant Design Vue 的 `a-card` 类似，用于承载页面内容的通用面板容器。
>
> 特性：支持系统暗黑主题、默认主体 `padding: 24px`、高度自适应父容器、内容溢出内部滚动且隐藏滚动条、可控阴影/圆角/分割线（默认右对齐的 footer），以及容器外部底部留白避免贴底；圆角可跟随系统设置自动调整。


例子1
----
（基础使用）

```vue
<template>
  <xn-panel title="标题">
    内容区域
  </xn-panel>
</template>

<script setup>
import XnPanel from '@/components/XnPanel/'
</script>
```


例子2
----
（标题扩展与底部操作）

```vue
<template>
  <xn-panel title="项目概览">
    <template #extra>
      <a-space>
        <a-button type="link">刷新</a-button>
        <a-button type="primary">新增</a-button>
      </a-space>
    </template>

    主体内容...

    <template #footer>
      <a-space>
        <a-button>取消</a-button>
        <a-button type="primary">提交</a-button>
      </a-space>
    </template>
  </xn-panel>
</template>
```


例子3
----
（无阴影、圆角与分割线控制、底部留白、系统圆角）

```vue
<template>
  <xn-panel
    :shadow="false"
    radius="system"
    :headerDivider="false"
    :footerDivider="true"
    :bottomGap="16"
    :padding="24"
  >
    内容较多时将内部滚动，滚动条隐藏。
  </xn-panel>
</template>
```


内置属性
----

| 属性           | 说明                                       | 类型             | 默认值      |
|----------------|--------------------------------------------|------------------|-------------|
| title          | 标题文本（也可用 `title` 插槽替代）        | String           | ''          |
| bordered       | 是否显示外边框                             | Boolean          | true        |
| padding        | 主体内边距（Number 自动转 px，或 String）  | Number/String    | 24          |
| headerPadding  | 头部内边距                                 | Number/String    | 0           |
| footerPadding  | 底部内边距                                 | Number/String    | 10          |
| shadow         | 是否显示阴影                               | Boolean          | false       |
| radius         | 圆角（Number/String，`system` 跟随系统）   | Number/String    | 'system'    |
| headerDivider  | 头部分割线开关                             | Boolean          | true        |
| footerDivider  | 底部分割线开关                             | Boolean          | true        |
| bottomGap      | 容器外部底部留白（避免卡片贴底）           | Number/String    | 10          |


插槽
----

| 插槽名  | 说明                   |
|---------|------------------------|
| title   | 标题区域（替代 `title` 属性） |
| extra   | 标题右侧扩展区         |
| 默认    | 主体内容               |
| footer  | 底部区域（操作/信息）  |


注意事项
----

> 1. `bottomGap` 通过在容器外部设置 `margin-bottom` 实现，并同步减少容器高度，确保不与父容器尺寸冲突。
>
> 2. 若希望面板内部滚动生效，请确保父容器有明确的高度约束（例如页面布局使用 `flex` 或固定高度）。
>
> 3. 暗黑主题依赖项目的 CSS 变量：`--snowy-background-color`、`--component-background`、`--border-color-split`、`--card-shadow-soft`、`--heading-color` 等。
>
> 4. 圆角跟随系统：当系统的“圆角风格”开启时，组件圆角为 `8px`，关闭时为 `2px`；也可通过 `radius` 指定具体值进行覆盖。
>
> 5. footer 默认右对齐，且背景与分割线使用与整体保持一致的主题变量（背景：`--snowy-background-color`，分割线：`--border-color-split`）。


更新时间
----

该文档最后更新于： 2025-11-30
