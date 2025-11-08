# ResizablePanel 可拖拽调整大小面板组件

一个基于 Vue 3 的可拖拽调整大小的面板组件，支持水平和垂直分割布局。

## 特性

- 🎯 **灵活布局**：支持水平（row）和垂直（column）分割
- 📏 **尺寸控制**：可设置初始大小、最小值和最大值
- 🎨 **平滑体验**：流畅的拖拽动画和视觉反馈
- 📱 **响应式设计**：自适应不同屏幕尺寸
- 🔧 **易于集成**：简单的 API 设计，易于在项目中使用

## 安装

将 `ResizablePanel` 组件文件复制到你的项目中：

```
src/components/ResizablePanel/index.vue
```

## 基本用法

### 水平分割布局

```vue
<template>
  <ResizablePanel
    direction="row"
    :initial-size="300"
    :min-size="200"
    :max-size="500"
    @resize="handleResize"
  >
    <template #first>
      <div class="left-panel">
        左侧内容
      </div>
    </template>
    <template #second>
      <div class="right-panel">
        右侧内容
      </div>
    </template>
  </ResizablePanel>
</template>

<script setup>
import ResizablePanel from '@/components/ResizablePanel/index.vue'

const handleResize = (size) => {
  console.log('面板大小变化:', size)
}
</script>
```

### 垂直分割布局

```vue
<template>
  <ResizablePanel
    direction="column"
    :initial-size="200"
    :min-size="100"
    :max-size="400"
  >
    <template #first>
      <div class="top-panel">
        顶部内容
      </div>
    </template>
    <template #second>
      <div class="bottom-panel">
        底部内容
      </div>
    </template>
  </ResizablePanel>
</template>
```

## API

### Props

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `direction` | `String` | `'row'` | 分割方向，可选值：`'row'`（水平）、`'column'`（垂直） |
| `initial-size` | `Number` | `300` | 第一个面板的初始大小（px） |
| `min-size` | `Number` | `100` | 第一个面板的最小大小（px） |
| `max-size` | `Number` | `500` | 第一个面板的最大大小（px） |

### Events

| 事件名 | 参数 | 说明 |
|--------|------|------|
| `resize` | `(size: number)` | 面板大小变化时触发，参数为第一个面板的当前大小 |

### Slots

| 插槽名 | 说明 |
|--------|------|
| `first` | 第一个面板的内容（水平布局时为左侧，垂直布局时为顶部） |
| `second` | 第二个面板的内容（水平布局时为右侧，垂直布局时为底部） |

## 样式定制

组件使用了 CSS 变量，你可以通过覆盖这些变量来自定义样式：

```css
.resizable-panel {
  /* 分割线颜色 */
  --divider-color: #e8e8e8;
  
  /* 分割线悬停颜色 */
  --divider-hover-color: #1890ff;
  
  /* 分割线宽度 */
  --divider-width: 4px;
  
  /* 过渡动画时间 */
  --transition-duration: 0.2s;
}
```

## 高级用法

### 嵌套使用

```vue
<template>
  <ResizablePanel direction="row" :initial-size="250">
    <template #first>
      <div class="sidebar">侧边栏</div>
    </template>
    <template #second>
      <ResizablePanel direction="column" :initial-size="200">
        <template #first>
          <div class="header">头部</div>
        </template>
        <template #second>
          <div class="content">主要内容</div>
        </template>
      </ResizablePanel>
    </template>
  </ResizablePanel>
</template>
```

### 动态控制

```vue
<template>
  <div>
    <button @click="resetSize">重置大小</button>
    <ResizablePanel
      ref="panelRef"
      direction="row"
      :initial-size="panelSize"
      @resize="handleResize"
    >
      <!-- 内容 -->
    </ResizablePanel>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const panelSize = ref(300)
const panelRef = ref()

const handleResize = (size) => {
  panelSize.value = size
}

const resetSize = () => {
  panelSize.value = 300
  // 如果需要强制更新组件，可以使用 key 或其他方法
}
</script>
```

## 注意事项

1. **容器高度**：确保父容器有明确的高度，否则垂直布局可能无法正常工作
2. **最小/最大值**：合理设置 `min-size` 和 `max-size`，避免内容被过度压缩
3. **性能优化**：在大量数据或复杂布局中，考虑使用 `v-show` 而不是 `v-if` 来控制面板显示
4. **移动端适配**：在移动设备上，建议增大分割线的触摸区域

## 浏览器兼容性

- Chrome 60+
- Firefox 55+
- Safari 12+
- Edge 79+

## 更新日志

### v1.0.0
- 初始版本发布
- 支持水平和垂直分割
- 支持拖拽调整大小
- 支持最小/最大值限制
