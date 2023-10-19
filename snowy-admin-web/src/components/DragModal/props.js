export default {
	props: [
		'afterClose', // Modal 完全关闭后的回调 function 无
		'bodyStyle', // Modal body 样式 object {}
		'cancelText', // 取消按钮文字 string| slot 取消
		'centered', // 垂直居中展示 Modal Boolean false
		'closable', // 是否显示右上角的关闭按钮 boolean true
		'closeIcon', // 自定义关闭图标 VNode | slot - 1.5.0
		'confirmLoading', // 确定按钮 loading boolean 无
		'destroyOnClose', // 关闭时销毁 Modal 里的子元素 boolean false
		'footer', // 底部内容，当不需要默认底部按钮时，可以设为 :footer="null" string|slot 确定取消按钮
		'forceRender', // 强制渲染 Modal boolean false
		'getContainer', // 指定 Modal 挂载的 HTML 节点 (instance): HTMLElement () => document.body
		'keyboard', // 是否支持键盘 esc 关闭 boolean true
		'mask', // 是否展示遮罩 Boolean true
		'maskClosable', // 点击蒙层是否允许关闭 boolean true
		'maskStyle', // 遮罩样式 object {}
		'okText', // 确认按钮文字 string|slot 确定
		'okType', // 确认按钮类型 string primary
		'okButtonProps', // ok 按钮 props, 遵循 jsx规范 {props: ButtonProps, on: {}} -
		'cancelButtonProps', // cancel 按钮 props, 遵循 jsx规范 {props: ButtonProps, on: {}} -
		'title', // 标题 string|slot 无
		// 'visible', // (v-model) 对话框是否可见 boolean 无
		// 'width', // 宽度 string|number 520
		// 'wrapClassName', // 对话框外层容器的类名 string -
		'zIndex', // 设置 Modal 的 z-index Number 1000
		'dialogStyle', // 可用于设置浮层的样式，调整浮层位置等 object - 1.6.1
		'dialogClass' // 可用于设置浮层的类名 string
	]
}
