# src/style 全局样式说明

## 文件分工

| 文件              | 内容                                                    |
| ----------------- | ------------------------------------------------------- |
| `index.less`      | 入口（main.js 引入），只负责按顺序 import               |
| `default.less`    | 亮色主题 CSS 变量，挂在 `:root`                         |
| `realdark.less`   | 暗黑主题 CSS 变量，挂在 `body.snowy-theme-dark`         |
| `base.less`       | 重置、页面底色、隐藏式滚动条、灰色模式                  |
| `layout.less`     | 框架骨架、顶栏、logo、双排菜单、右键菜单、页面最大化    |
| `components.less` | antdv 全局微调、弹窗/抽屉、表单设计器与 CodeMirror 适配 |
| `utilities.less`  | `xn-` 前缀工具类                                        |

## 适配暗黑模式的操作

直接 `var(--primary-color)`。

例如：新建个变量，在 `default.less` 跟 `realdark.less` 中设定好，这个时候就会跟着颜色的主题变化。

```less
// default.less
:root {
	--my-card-bg: #ffffff;
}
// realdark.less
.snowy-theme-dark {
	--my-card-bg: #1f1f1f;
}
// 页面里
.my-card {
	background: var(--my-card-bg);
}
```

## 维护约定

- 两个主题文件**变量名一一对应、顺序一致**，新增变量两边都加。暗色类挂在 body 上，`:root` 里写成 `var(--a)` 的变量若暗色不重新声明，会沿用亮色计算结果
- 主题文件只写纯 CSS 值，**不要写 `@xxx` Less 变量或 `fade()`**：Less 变量在编译期会被替换成 antd 亮色固定值，暗黑下不会变
- `--primary-1` ~ `--primary-10`、`--primary-color` 由 `utils/themeUtil.js` 按所选主色生成亮色和暗黑两套变量。进入或退出暗黑时由 `body` 上的类名切换
- 业务自定义页面做暗黑适配，优先用文件末尾「业务通用」那组变量（`--background-color`、`--background-card-color` 等）
- 侧栏明暗风格共用 Ant Design Vue 的亮色主题配置。进入或退出暗黑模式时切换全站主题算法
- 整体风格通过 View Transition 在点击位置做圆形展开或收回。连续点选时立即应用最后一次选择。浏览器不支持该 API 或用户启用了减少动态效果时直接切换

## 调整暗黑配色

暗黑配色由两处共同决定，改色时两边同步：

| 位置                                                                                                                                | 管什么                                                    |
| ----------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------- |
| `realdark.less` 顶部「表面色」：`--surface-layout / container / elevated / muted / hover / border / border-secondary`、`--text-rgb` | Snowy 布局与业务页面的 CSS 变量，其余暗色变量都引用这一组 |
| `src/config/darkTheme.js` 的 `darkPalettes`                                                                                         | antd 组件（卡片、表格、输入框、菜单、侧栏、顶栏）的令牌   |

`darkTheme.js` 里预置了 `soft`（柔和中性灰，默认）、`slate`（蓝灰）、`deep`（深邃黑）三套，改 `darkPaletteName` 并把对应色值抄到 `realdark.less` 顶部即可切换。暗黑下菜单选中项为「主色 20% 透明底 + 提亮主色文字」，跟随用户选的主题色。
