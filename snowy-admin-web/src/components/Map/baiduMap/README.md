BMap
====

> 百度地图组件，常用于地图展示使用

该组件由 [小诺开源技术](https://www.xiaonuo.vip) 封装

### 使用方式

```text
申请key
```

访问 [百度地图官网](https://lbsyun.baidu.com/apiconsole/center#/home) 注册账号，申请应用获得key

```vue
<template>
	<baidu-map ref="map" api-key="******" @complete="handleComplete"
				   @marker-click="handleMarkerClick" />
</template>

<script setup name="exmAMap">
	import BaiduMap from '@/components/Map/baiduMap/index.vue'

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

| 名称            | 说明         | 类型     | 默认值      |
|---------------|------------|--------|----------|
| mid           | 容器ID       | String | 时间戳      |
| height        | 容器高度       | Number | 800，单位px |
| apiKey        | 地图Key      | String |          |
| center        | 地图中心点      | String | 自动定位     |
| plugins       | 地图控件       | Array  |          |
| viewMode      | 效果：2D，3D   | String | 3D       |
| rotationAngle | 旋转角度       | Number | 60       |
| tiltAngle     | 倾斜角度       | Number | 70       |
| zoom          | 地图缩放比例     | Number | 12       |
| mapStyle      | 地图样式：个性化地图 | String |          |

#### 地图控件

- BMap.ScaleControl：比例尺
- BMap.ZoomControl：缩放
- BMap.LocationControl：定位
- BMap.NavigationControl3D：3D控件

### 事件

| 名称          | 说明            | 参数       | 参数类型  |
|-------------|---------------|----------|-------|
| complete    | 当地图初始化完成时触发   | -        | -     |
| markerClick | 当点击了点覆盖物时触发   | position | Array |

### 方法

| 名称                   | 说明        | 参数                     | 参数类型              |
|----------------------|-----------|------------------------|-------------------|
| renderMarker         | 渲染 点标记    | dataArr                | Array             |
| renderIconMarker     | 渲染 图标标记   | dataArr                | Array             |
| render3DCircleMarker | 渲染 3D圆点标记 | dataArr                | Array             |
| render3DIconMarker   | 渲染 3D图标标记 | dataArr                | Array             |
| renderPolyline       | 渲染 线      | dataArr,option         | Array,JSON        |
| renderCircle         | 渲染 圆      | position,radius,option | Array,Number,JSON |
| renderPolygon        | 渲染 面      | dataArr,option         | Array,JSON        |
| renderInfoWindow     | 渲染 信息窗体   | dataArr                | Array             |
| openInfoWindow       | 打开 信息窗体   | position               | Array             |
| clearOverlay         | 清理 覆盖物    |                        |                   |

### 方法参数```dataArr```结构

> 点标记

```json
[{
	"position": "坐标数组",
	"title": "鼠标滑过点标记时的文字提示"
}]
```

> 图标标记

```json
[{
	"position": "坐标数组",
	"title": "鼠标滑过点标记时的文字提示",
	"img": "图片地址",
	"imgWidth": "图片宽度，默认40",
	"imgHeight": "图片高度，默认40"
}]
```

> 3D圆点标记

```json
[{
	"position": "坐标数组",
	"height": "高度，默认8000",
	"size": "大小，默认50",
	"fillColor": "填充颜色，默认#006600"
}]
```

> 3D图标标记

```json
[{
	"position": "坐标数组",
	"height": "高度，默认8000",
	"size": "大小，默认50",
	"img": "图片地址",
	"imgWidth": "图片宽度，默认40",
	"imgHeight": "图片高度，默认40"
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
	"title": "标题",
	"content": "内容，文本数组，会以换行进行连接",
	"width": "窗体宽度",
	"height": "窗体高度"
}]
```

### 方法参数```option```结构

> 线、圆

```json
{
	"strokeColor": "边线颜色，默认blue",
	"strokeWeight": "边线宽度，默认2",
	"strokeOpacity": "边线透明度，默认0.5"
}
```

> 面

```json
{
	"strokeColor": "边线颜色，默认blue",
	"strokeWeight": "边线宽度，默认2",
	"strokeOpacity": "边线透明度，默认0.5",
	"fillColor": "填充颜色，默认blue",
	"fllOpacity": "填充透明度，默认0.5"
}
```
