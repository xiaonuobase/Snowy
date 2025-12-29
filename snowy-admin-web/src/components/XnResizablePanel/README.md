# XnResizablePanel 可拖拽调整大小面板组件

一个基于 Vue 3 的可拖拽调整大小的面板组件，支持水平和垂直分割布局；并可在小屏自动隐藏左侧与拖拽条（按 `md` 配置）。

## 特性

- 🎯 **灵活布局**：支持水平（row）和垂直（column）分割
- 📏 **尺寸控制**：可设置初始大小、最小值和最大值
- 🎨 **平滑体验**：流畅的拖拽动画和视觉反馈
- 📱 **响应式设计**：按 `md` 配置在 `<768px` 自动隐藏左侧与拖拽条
- 🔧 **易于集成**：简单的 API 设计，易于在项目中使用

## 安装

将 `XnResizablePanel` 组件文件复制到你的项目中：

```
src/components/XnResizablePanel/index.vue
```

## 基本用法

### 水平分割布局（row）

```vue
<template>
	<XnResizablePanel direction="row" :initial-size="300" :min-size="200" :max-size="500" :md="0">
		<template #left>
			左侧内容
		</template>
		<template #right>
			右侧内容
		</template>
	</XnResizablePanel>
</template>

<script setup>
import XnResizablePanel from '@/components/XnResizablePanel/index.vue'

const handleResize = (size) => {
  console.log('面板大小变化:', size)
}
</script>
```

### 垂直分割布局（column）

```vue
<template>
  <XnResizablePanel direction="column" :initial-size="220" :min-size="120" :max-size="480">
    <template #left>
      顶部内容
    </template>
    <template #right>
      底部内容
    </template>
  </XnResizablePanel>
</template>
```

### 小屏隐藏（`md` 配置）

当 `md` 为 `0` 时，在视口宽度 `<768px` 时自动隐藏左侧与拖拽条，仅显示右侧内容：

```vue
<XnResizablePanel :md="0">
  <template #left>左侧内容</template>
  <template #right>右侧内容</template>
</XnResizablePanel>
```

## API

### Props

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `direction` | `String` | `'row'` | 分割方向，可选值：`'row'`（水平）、`'column'`（垂直） |
| `initial-size` | `Number` | `200` | 左侧（或顶部）面板的初始大小（px） |
| `min-size` | `Number` | `100` | 左侧（或顶部）面板的最小大小（px） |
| `max-size` | `Number` | `500` | 左侧（或顶部）面板的最大大小（px） |
| `md` | `Number` | `null` | 响应式开关：当为 `0` 时，在 `<768px` 隐藏左侧与拖拽条 |

### Events

| 事件名 | 参数 | 说明 |
|--------|------|------|
| `resize` | `(size: number)` | 面板大小变化时触发，参数为左侧（或顶部）面板的当前大小 |

### Slots

| 插槽名 | 说明 |
|--------|------|
| `left` | 左侧（或顶部）面板内容 |
| `right` | 右侧（或底部）面板内容 |

### 暴露方法（Expose）

通过组件引用可以调用以下方法：

```vue
<script setup>
import { ref } from 'vue'
import XnResizablePanel from '@/components/XnResizablePanel/index.vue'

const panelRef = ref()

function setTo300() {
  panelRef.value?.setSize(300)
}

function logSize() {
  console.log(panelRef.value?.getSize())
}
</script>

<template>
  <XnResizablePanel ref="panelRef">
    <template #left>左侧</template>
    <template #right>右侧</template>
  </XnResizablePanel>
  <button @click="setTo300">设置为 300px</button>
  <button @click="logSize">打印当前大小</button>
  
</template>
```

## 样式与定制

默认样式要点：

- 左/右（或上/下）面板默认内边距：`24px`
- 拖拽条（横向）宽度：`8px`；（纵向）高度：`8px`
- 拖拽握把居中，胶囊形，内含三点提示；悬停/按下有视觉反馈

可覆盖的 CSS 变量（在主题中使用）：

```css
/* 组件内部使用的变量名称 */
.resizable-panel {
  --component-background: #fff;
  --border-color-base: #e5e7eb; /* Gray-200 */
  --primary-color: #1677ff;     /* Ant Design 默认蓝 */
}

/* 调整拖拽条尺寸或样式 */
.resizer-horizontal { width: 8px; }
.resizer-vertical { height: 8px; }
.resizer-handle { box-shadow: 0 2px 10px rgba(0,0,0,0.06), inset 0 0 0 1px var(--border-color-base); }

/* 修改面板内边距 */
.panel-left, .panel-right { padding: 24px; }

/* 示例：让拖拽条更显眼 */
.resizer:hover .resizer-handle {
  box-shadow: 0 4px 14px rgba(0,0,0,0.10), inset 0 0 0 1px var(--primary-color);
}

/* 示例：增大横向握把高度 */
.resizer-horizontal .resizer-handle { height: 32px; }
```
