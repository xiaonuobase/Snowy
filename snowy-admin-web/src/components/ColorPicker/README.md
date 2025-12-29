# ColorPicker 颜色选择器

基于 vue3-colorpicker 封装的颜色选择器组件，支持纯色、渐变色选择，并提供智能文字颜色适配功能。

## 功能特性

- 🎨 支持纯色、渐变色、混合模式选择
- 🔧 多种颜色格式支持（hex、rgb）
- 🎯 圆形/方形选择器形状
- 📍 颜色值显示位置可配置（内联/底部）
- 🎭 智能文字颜色适配（根据背景色自动调整文字颜色）
- 🚫 可选择禁用透明度通道

## 安装依赖

```bash
npm install vue3-colorpicker
```

## 基础用法

```vue
<template>
  <ColorPicker v-model:value="color" />
</template>

<script setup>
import { ref } from 'vue'
import ColorPicker from '@/components/ColorPicker/index.vue'

const color = ref('#1677FF')
</script>
```

## 属性配置

| 属性名 | 类型 | 默认值 | 可选值 | 说明 |
|--------|------|--------|--------|------|
| `value` | String | `'#1677FF'` | - | 颜色值，支持双向绑定 |
| `useType` | String | `'pure'` | `'pure'` \| `'gradient'` \| `'both'` | 颜色选择类型 |
| `valuePosition` | String | `'inline'` | `'inline'` \| `'bottom'` | 颜色值显示位置 |
| `format` | String | `'hex'` | `'hex'` \| `'rgb'` | 颜色格式 |
| `shape` | String | `'square'` | `'circle'` \| `'square'` | 选择器形状 |
| `disableAlpha` | Boolean | `true` | `true` \| `false` | 是否禁用透明度通道 |

## 事件

| 事件名 | 参数 | 说明 |
|--------|------|------|
| `update:value` | `(value: string)` | 颜色值变化时触发，统一处理纯色和渐变色 |

## 使用示例

### 基础纯色选择器

```vue
<template>
  <ColorPicker 
    v-model:value="pureColor" 
    useType="pure"
    format="hex"
    shape="square"
  />
</template>

<script setup>
import { ref } from 'vue'
import ColorPicker from '@/components/ColorPicker/index.vue'

const pureColor = ref('#FF5722')
</script>
```

### 渐变色选择器

```vue
<template>
  <ColorPicker 
    v-model:value="gradientColor" 
    useType="gradient"
    @update:value="handleColorChange"
  />
</template>

<script setup>
import { ref } from 'vue'
import ColorPicker from '@/components/ColorPicker/index.vue'

const gradientColor = ref('linear-gradient(90deg, #FF5722 0%, #2196F3 100%)')

const handleColorChange = (value) => {
  console.log('颜色变化:', value)
}
</script>
```

### 混合模式（纯色+渐变）

```vue
<template>
  <ColorPicker 
    v-model:value="mixedColor" 
    useType="both"
    valuePosition="bottom"
    @update:value="handleColorChange"
  />
</template>

<script setup>
import { ref } from 'vue'
import ColorPicker from '@/components/ColorPicker/index.vue'

const mixedColor = ref('#9C27B0')

const handleColorChange = (value) => {
  console.log('颜色变化:', value)
  // 根据 useType 配置，这里可能接收到纯色值或渐变色值
  // 组件会根据用户的选择自动返回对应类型的颜色值
}
</script>
```

### 圆形选择器 + RGB 格式

```vue
<template>
  <ColorPicker 
    v-model:value="rgbColor" 
    format="rgb"
    shape="circle"
    :disableAlpha="false"
  />
</template>

<script setup>
import { ref } from 'vue'
import ColorPicker from '@/components/ColorPicker/index.vue'

const rgbColor = ref('rgb(33, 150, 243)')
</script>
```

## 高级功能

### 智能文字颜色适配

组件会根据选择的背景色自动调整文字颜色，确保文字在任何背景下都有良好的可读性：

- 浅色背景：使用黑色文字
- 深色背景：使用白色文字
- 渐变色背景：使用白色文字

### 颜色值显示位置

- `inline`：颜色值显示在选择器内部
- `bottom`：颜色值显示在选择器底部

## 样式定制

组件提供了以下 CSS 类名供样式定制：

```less
.snowy-color-picker {
  width: 100%;
  
  .vc-color-wrap {
    width: 100%;
  }
  
  .current-color {
    padding: 0 10px;
  }
  
  .remark {
    font-size: 12px;
    color: #6c6c6c;
    padding: 0 2px;
  }
}
```

## 注意事项

1. 组件基于 `vue3-colorpicker` 库，请确保已正确安装依赖
2. 渐变色功能仅在 `useType` 为 `'gradient'` 或 `'both'` 时可用
3. 组件会自动生成唯一 ID，避免多个实例冲突
4. 智能文字颜色适配功能依赖于 `@/utils/color` 工具函数

## 依赖

- `vue3-colorpicker`: 核心颜色选择器库
- `@/utils/tool`: 工具函数（UUID 生成）
- `@/utils/color`: 颜色处理工具函数