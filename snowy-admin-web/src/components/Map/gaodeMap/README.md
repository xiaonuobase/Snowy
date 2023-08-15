AMap
====

> 高德地图组件，常用于地图展示使用

该组件由 [小诺开源技术](https://www.xiaonuo.vip) 封装

### 使用方式

```text
申请key
```

访问 [高德地图官网](https://console.amap.com/dev/index) 注册账号，申请应用获得key

```vue

<template>
	<gaode-map ref="map" api-key="******" @complete="handleComplete"
				   @marker-click="handleMarkerClick" />
</template>

<script setup name="exmAMap">
	import GaodeMap from '@/components/Map/gaodeMap/index.vue'

	const map = ref(null)

	const handleComplete = () => {
		// 渲染 点标记
		map.value.renderMarker(
			[
				{
					position: [116.39, 39.9],
					title: 'TA',
					content: 'CA',
					label: {
						content: 'LCA'
					}
				},
				{
					position: [116.33, 39.5],
					title: 'TB',
					icon: '//vdata.amap.com/icons/b18/1/2.png'
				}
			]
		)
	}

	const handleMarkerClick = (position) => {
		map.value.openInfoWindow(position)
	}
</script>
```

### Prop属性

| 名称            | 说明                | 类型       | 默认值       |
|---------------|-------------------|----------|-----------|
| mid           | 容器ID              | String   | 时间戳       |
| height        | 容器高度              | Number   | 800，单位px  |
| apiKey        | 地图Key             | String   |           |
| center        | 地图中心点             | String   | 自动定位      |
| plugins       | 地图控件              | Array    |           |
| viewMode      | 效果：2D，3D          | String   | 3D        |
| zoom          | 地图缩放比例            | Number   | 12        |
| pitch         | 地图俯仰角度，有效范围 0-83  | String   | 50        |
| mapStyle      | 地图样式              | String   | normal    |
| markerCluster | 点聚合               | Boolean  | true      |

#### 地图控件

- AMap.ToolBar：缩放工具条
- AMap.Scale：比例尺
- AMap.HawkEye：鹰眼
- AMap.MapType：图层切换
- AMap.Geolocation：定位
- AMap.MarkerCluster：点聚合

#### 地图样式

- normal
- macaron
- dark
- fresh
- grey

### 事件

| 名称          | 说明            | 参数       | 参数类型  |
|-------------|---------------|----------|-------|
| complete    | 当地图初始化完成时触发   | -        | -     |
| markerClick | 当点击了点覆盖物时触发   | position | Array |

### 方法

| 名称                  | 说明       | 参数                  | 参数类型              |
|---------------------|----------|---------------------|-------------------|
| renderMarker        | 渲染 点标记   | dataArr             | Array             |
| renderCircleMarker  | 渲染 圆点标记  | dataArr             | Array             |
| renderSimpleMarker  | 渲染 简单点标记 | dataArr, theme      | Array, String     |
| renderAwesomeMarker | 渲染 字体点标记 | dataArr             | Array             |
| renderPolyline      | 渲染 线     | dataArr,opt         | Array,JSON        |
| renderCircle        | 渲染 圆     | position,radius,opt | Array,Number,JSON |
| renderPolygon       | 渲染 面     | dataArr,opt         | Array,JSON        |
| renderInfoWindow    | 渲染 信息窗体  | dataArr             | Array             |
| openInfoWindow      | 打开 信息窗体  | position            | Array             |
| clearOverlay        | 清理 覆盖物   |                     |                   |

### 方法参数```dataArr```结构

> 点标记

```json
[{
	"position": "坐标数组",
	"title": "鼠标滑过点标记时的文字提示",
	"content": "显示内容，content有效时，icon属性将被覆盖",
	"icon": "图标",
	"label": {
		"content": "文本标注"
	}
}]
```

> 圆点标记

```json
[{
	"position": "坐标数组，圆心位置",
	"radius": "圆点半径",
	"strokeColor": "线条颜色，默认#006600",
	"fillColor": "填充颜色，默认#006600"
}]
```

> 简单点标记

```json
[{
	"position": "坐标数组",
	"label": "前景文字",
	"labelStyle": {
		"color": "颜色",
		"fontSize": "字体大小"
	},
	"style": "背景图标样式"
}]
```

> 字体点标记

```json
[{
	"position": "坐标数组",
	"awesomeIcon": "图标，参见：http://fontawesome.io/icons/",
	"labelStyle": {
		"color": "颜色",
		"fontSize": "字体大小"
	},
	"style": "背景图标样式"
}]
```

> 线、面

```json
[{
	"position": "坐标数组"
}]
```

> 信息窗体

```json
[{
	"position": "坐标数组",
	"content": "显示内容，文本数组，会以换行进行连接"
}]
```

### 方法参数```opt```结构

> 线

```json
{
	"strokeColor": "边线颜色，默认blue",
	"strokeWeight": "边线宽度，默认2",
	"strokeOpacity": "边线透明度，默认0.5",
	"isOutline": "是否显示描边，默认false",
	"borderWeight": "描边宽度，默认1"
}
```

> 圆、面

```json
{
	"strokeColor": "边线颜色，默认blue",
	"strokeWeight": "边线宽度，默认2",
	"strokeOpacity": "边线透明度，默认0.5",
	"fillColor": "填充颜色，默认blue",
	"fllOpacity": "填充透明度，默认0.5"
}
```
