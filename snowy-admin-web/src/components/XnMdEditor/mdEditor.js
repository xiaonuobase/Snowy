/**
 * 该插件采用：https://code-farmer-i.github.io/vue-markdown-editor/zh/
 *
 * 使用时按需引入即可：import { XnMdEditor, XnMdPreview } from '@/components/XnMdEditor/mdEditor'
 * 如果需要参照，参考消息推送模块
 *
 * @author yubaoshan
 * @date 2025年5月12日19:28:03
 */
import VMdEditor from '@kangc/v-md-editor/lib/codemirror-editor'
import '@kangc/v-md-editor/lib/style/codemirror-editor.css'
import githubTheme from '@kangc/v-md-editor/lib/theme/github'
import '@kangc/v-md-editor/lib/theme/style/github.css'

// highlightjs
import 'highlight.js/styles/atom-one-dark.css'
import 'highlight.js/lib/common'
import hljsVuePlugin from '@highlightjs/vue-plugin'

// codemirror 编辑器的相关资源
import Codemirror from 'codemirror'
// mode
import 'codemirror/mode/markdown/markdown'
import 'codemirror/mode/javascript/javascript'
import 'codemirror/mode/css/css'
import 'codemirror/mode/htmlmixed/htmlmixed'
import 'codemirror/mode/vue/vue'
// edit
import 'codemirror/addon/edit/closebrackets'
import 'codemirror/addon/edit/closetag'
import 'codemirror/addon/edit/matchbrackets'
// placeholder
import 'codemirror/addon/display/placeholder'
// active-line
import 'codemirror/addon/selection/active-line'
// scrollbar
import 'codemirror/addon/scroll/simplescrollbars'
import 'codemirror/addon/scroll/simplescrollbars.css'
// style
import 'codemirror/lib/codemirror.css'

import VMdPreview from '@kangc/v-md-editor/lib/preview'
import '@kangc/v-md-editor/lib/style/preview.css'

const XnMdEditor = VMdEditor
XnMdEditor.Codemirror = Codemirror

VMdPreview.use(githubTheme)
const XnMdPreview = VMdPreview

XnMdEditor.use(githubTheme, {
	Hljs: hljsVuePlugin
})

export { XnMdEditor, XnMdPreview }
