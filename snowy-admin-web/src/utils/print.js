/**
 *  Copyright [2022] [https://www.xiaonuo.vip]
 *	Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：
 *	1.请不要删除和修改根目录下的LICENSE文件。
 *	2.请不要删除和修改Snowy源码头部的版权声明。
 *	3.本项目代码可免费商业使用，商业使用请保留源码和相关描述文件的项目出处，作者声明等。
 *	4.分发源码时候，请注明软件出处 https://www.xiaonuo.vip
 *	5.不可二次分发开源参与同类竞品，如有想法可联系团队xiaonuobase@qq.com商议合作。
 *	6.若您的项目无法满足以上几点，需要更多功能代码，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
/* eslint-disable */
// 打印类属性、方法定义
const Print = function(dom, options) {
  if (!(this instanceof Print)) return new Print(dom, options)

  this.options = this.extend({
    noPrint: '.no-print',
  }, options)

  if ((typeof dom) === 'string') {
    try {
      this.dom = document.querySelector(dom)
    }
    catch {
      const createDom = document.createElement('div')
      createDom.innerHTML = dom
      this.dom = createDom
    }
  }
  else {
    this.isDOM(dom)
    this.dom = this.isDOM(dom) ? dom : dom.$el
  }

  this.init()
}
Print.prototype = {
  init() {
    const content = this.getStyle() + this.getHtml()
    this.writeIframe(content)
  },
  extend(obj, obj2) {
    for (const k in obj2) {
      obj[k] = obj2[k]
    }
    return obj
  },

  getStyle() {
    let str = ''
    const styles = document.querySelectorAll('style,link')
    for (let i = 0; i < styles.length; i++) {
      str += styles[i].outerHTML
    }
    str += `<style>${ this.options.noPrint ? this.options.noPrint : '.no-print'
			 }{display:none;}</style>`
    str += '<style>html,body{background-color:#fff;}</style>'
    return str
  },

  getHtml() {
    const inputs = document.querySelectorAll('input')
    const textareas = document.querySelectorAll('textarea')
    const selects = document.querySelectorAll('select')

    for (let k = 0; k < inputs.length; k++) {
      if (inputs[k].type == 'checkbox' || inputs[k].type == 'radio') {
        if (inputs[k].checked == true) {
          inputs[k].setAttribute('checked', 'checked')
        }
        else {
          inputs[k].removeAttribute('checked')
        }
      }
      else if (inputs[k].type == 'text') {
        inputs[k].setAttribute('value', inputs[k].value)
      }
      else {
        inputs[k].setAttribute('value', inputs[k].value)
      }
    }

    for (let k2 = 0; k2 < textareas.length; k2++) {
      if (textareas[k2].type == 'textarea') {
        textareas[k2].innerHTML = textareas[k2].value
      }
    }

    for (let k3 = 0; k3 < selects.length; k3++) {
      if (selects[k3].type == 'select-one') {
        const child = selects[k3].children
        for (const i in child) {
          if (child[i].tagName == 'OPTION') {
            if (child[i].selected == true) {
              child[i].setAttribute('selected', 'selected')
            }
            else {
              child[i].removeAttribute('selected')
            }
          }
        }
      }
    }

    return this.dom.outerHTML
  },

  writeIframe(content) {
    let w; let doc; const iframe = document.createElement('iframe')
    const f = document.body.appendChild(iframe)
    iframe.id = 'myIframe'
    // iframe.style = "position:absolute;width:0;height:0;top:-10px;left:-10px;";
    iframe.setAttribute('style', 'position:absolute;width:0;height:0;top:-10px;left:-10px;')
    w = f.contentWindow || f.contentDocument
    doc = f.contentDocument || f.contentWindow.document
    doc.open()
    doc.write(content)
    doc.close()
    const _this = this
    iframe.onload = function() {
      _this.toPrint(w)
      setTimeout(() => {
        document.body.removeChild(iframe)
      }, 100)
    }
  },

  toPrint(frameWindow) {
    try {
      setTimeout(() => {
        frameWindow.focus()
        try {
          if (!frameWindow.document.execCommand('print', false, null)) {
            frameWindow.print()
          }
        }
        catch (e) {
          frameWindow.print()
        }
        frameWindow.close()
      }, 10)
    }
    catch (err) {
      console.log('err', err)
    }
  },
  isDOM: (typeof HTMLElement === 'object')
    ? function(obj) {
      return obj instanceof HTMLElement
    }
    : function(obj) {
      return obj && typeof obj === 'object' && obj.nodeType === 1 && typeof obj.nodeName === 'string'
    },
}

export default Print
