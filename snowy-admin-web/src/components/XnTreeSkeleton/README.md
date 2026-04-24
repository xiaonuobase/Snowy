# XnTreeSkeleton 树形骨架屏组件

树形结构的加载占位动画组件，支持 6 种动画风格和自定义主色调。

## 基本用法

```vue
<!-- 默认脉冲节点风格，蓝色主题 -->
<xn-tree-skeleton />

<!-- 指定动画类型 -->
<xn-tree-skeleton type="glow" />

<!-- 自定义主色调 -->
<xn-tree-skeleton type="pulse" color="#722ed1" />
```

## Props

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `type` | `String` | `'pulse'` | 动画类型，见下方可选值 |
| `color` | `String` | `'#1890ff'` | 主色调（十六进制），影响圆点、光效、波纹等颜色 |

## 动画类型 (type)

| 值 | 名称 | 效果描述 |
|----|------|----------|
| `pulse` | 脉冲节点 | 蓝色圆点逐个出现并脉冲跳动，简洁有活力 |
| `skeleton` | 树形骨架屏 | 灰色骨架条 + 微光扫过（shimmer），最贴近真实内容 |
| `bounce` | 弹性节点 | 节点带弹性动画逐个弹入，圆形指示器脉冲 |
| `glow` | 呼吸光效 | 骨架节点带呼吸光效 + 光带扫过，高级感强 |
| `ripple` | 波纹扩散 | 居中图标 + 波纹扩散效果，适合极简风格 |
| `folder` | 文件夹骨架 | 带文件夹图标和展开箭头的骨架屏，蓝色主题微光 |

## 在页面中使用

组件通过 `unplugin-vue-components` 自动注册，无需手动 import。

典型用法（配合 `v-if/v-else-if` 条件链）：

```vue
<div ref="treeContainerRef" style="height: 100%">
    <xn-tree-skeleton v-if="treeLoading && treeData.length === 0" />
    <a-tree
        v-else-if="treeData.length > 0"
        :tree-data="treeData"
        :height="treeHeight"
    />
    <a-empty v-else :image="Empty.PRESENTED_IMAGE_SIMPLE" />
</div>
```
