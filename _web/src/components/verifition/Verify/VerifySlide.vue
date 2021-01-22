<template>
  <div style="position: relative;">
    <div
      :style="{height: (parseInt(setSize.imgHeight) + vSpace) + 'px'}"
      class="verify-img-out"
      v-if="type === '2'"
    >
      <div
        :style="{width: setSize.imgWidth,
                 height: setSize.imgHeight,}"
        class="verify-img-panel">
        <img :src="'data:image/png;base64,'+backImgBase" alt="" style="width:100%;height:100%;display:block">
        <div @click="refresh" class="verify-refresh" v-show="showRefresh"><i class="iconfont icon-refresh"></i>
        </div>
        <transition name="tips">
          <span :class="passFlag ?'suc-bg':'err-bg'" class="verify-tips" v-if="tipWords">{{ tipWords }}</span>
        </transition>
      </div>
    </div>
    <!-- 公共部分 -->
    <div
      :style="{width: setSize.imgWidth,
               height: barSize.height,
               'line-height':barSize.height}"
      class="verify-bar-area">
      <span class="verify-msg" v-text="text"></span>
      <div
        :style="{width: (leftBarWidth!==undefined)?leftBarWidth: barSize.height, height: barSize.height, 'border-color': leftBarBorderColor, transaction: transitionWidth}"
        class="verify-left-bar">
        <span class="verify-msg" v-text="finishText"></span>
        <div
          :style="{width: barSize.height, height: barSize.height, 'background-color': moveBlockBackgroundColor, left: moveBlockLeft, transition: transitionLeft}"
          @mousedown="start"
          @touchstart="start"
          class="verify-move-block">
          <i
            :class="['verify-icon iconfont', iconClass]"
            :style="{color: iconColor}"></i>
          <div
            :style="{'width':Math.floor(parseInt(setSize.imgWidth)*47/310)+ 'px',
                     'height': setSize.imgHeight,
                     'top':'-' + (parseInt(setSize.imgHeight) + vSpace) + 'px',
                     'background-size': setSize.imgWidth + ' ' + setSize.imgHeight,
            }"
            class="verify-sub-block"
            v-if="type === '2'">
            <img :src="'data:image/png;base64,'+blockBackImgBase" alt="" style="width:100%;height:100%;display:block">
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script type="text/babel">
    /**
     * VerifySlide
     * @description 滑块
     * */
    import { aesEncrypt } from './../utils/ase'
    import { resetSize } from './../utils/util'
    import { reqGet, reqCheck } from '@/api/modular/system/loginManage'

    //  "captchaType":"blockPuzzle",
    export default {
        name: 'VerifySlide',
        props: {
            // eslint-disable-next-line vue/require-default-prop
            captchaType: {
                type: String
            },
            type: {
                type: String,
                default: '1'
            },
            // 弹出式pop，固定fixed
            mode: {
                type: String,
                default: 'fixed'
            },
            vSpace: {
                type: Number,
                default: 5
            },
            explain: {
                type: String,
                default: '向右滑动完成验证'
            },
            imgSize: {
                type: Object,
                default() {
                    return {
                        width: '310px',
                        height: '155px'
                    }
                }
            },
            blockSize: {
                type: Object,
                default() {
                    return {
                        width: '50px',
                        height: '50px'
                    }
                }
            },
            barSize: {
                type: Object,
                default() {
                    return {
                        width: '310px',
                        height: '40px'
                    }
                }
            }
        },
        data() {
            return {
                secretKey: '', // 后端返回的加密秘钥 字段
                passFlag: '', // 是否通过的标识
                backImgBase: '', // 验证码背景图片
                blockBackImgBase: '', // 验证滑块的背景图片
                backToken: '', // 后端返回的唯一token值
                startMoveTime: '', // 移动开始的时间
                endMovetime: '', // 移动结束的时间
                tipsBackColor: '', // 提示词的背景颜色
                tipWords: '',
                text: '',
                finishText: '',
                setSize: {
                    imgHeight: 0,
                    imgWidth: 0,
                    barHeight: 0,
                    barWidth: 0
                },
                top: 0,
                left: 0,
                moveBlockLeft: undefined,
                leftBarWidth: undefined,
                // 移动中样式
                moveBlockBackgroundColor: undefined,
                leftBarBorderColor: '#ddd',
                iconColor: undefined,
                iconClass: 'icon-right',
                status: false, // 鼠标状态
                isEnd: false,		// 是够验证完成
                showRefresh: true,
                transitionLeft: '',
                transitionWidth: ''
            }
        },
        computed: {
            barArea() {
                return this.$el.querySelector('.verify-bar-area')
            },
            resetSize() {
                return resetSize
            }
        },
        methods: {
            init() {
                this.text = this.explain
                this.getPictrue()
                this.$nextTick(() => {
                    const setSize = this.resetSize(this)	// 重新设置宽度高度
                    for (const key in setSize) {
                        this.$set(this.setSize, key, setSize[key])
                    }
                    this.$parent.$emit('ready', this)
                })

                var _this = this

                window.removeEventListener('touchmove', function (e) {
                    _this.move(e)
                })
                window.removeEventListener('mousemove', function (e) {
                    _this.move(e)
                })

                // 鼠标松开
                window.removeEventListener('touchend', function () {
                    _this.end()
                })
                window.removeEventListener('mouseup', function () {
                    _this.end()
                })

                window.addEventListener('touchmove', function (e) {
                    _this.move(e)
                })
                window.addEventListener('mousemove', function (e) {
                    _this.move(e)
                })

                // 鼠标松开
                window.addEventListener('touchend', function () {
                    _this.end()
                })
                window.addEventListener('mouseup', function () {
                    _this.end()
                })
            },

            // 鼠标按下
            start: function (e) {
                e = e || window.event
                if (!e.touches) { // 兼容PC端
                    var x = e.clientX
                } else { // 兼容移动端
                    // eslint-disable-next-line no-redeclare
                    var x = e.touches[0].pageX
                }
                this.startLeft = Math.floor(x - this.barArea.getBoundingClientRect().left)
                this.startMoveTime = +new Date() // 开始滑动的时间
                // eslint-disable-next-line eqeqeq
                if (this.isEnd == false) {
                    this.text = ''
                    this.moveBlockBackgroundColor = '#337ab7'
                    this.leftBarBorderColor = '#337AB7'
                    this.iconColor = '#fff'
                    e.stopPropagation()
                    this.status = true
                }
            },
            // 鼠标移动
            move: function (e) {
                e = e || window.event
                // eslint-disable-next-line eqeqeq
                if (this.status && this.isEnd == false) {
                    if (!e.touches) { // 兼容PC端
                        var x = e.clientX
                    } else { // 兼容移动端
                        // eslint-disable-next-line no-redeclare
                        var x = e.touches[0].pageX
                    }
                    // eslint-disable-next-line camelcase
                    var bar_area_left = this.barArea.getBoundingClientRect().left
                    // eslint-disable-next-line camelcase
                    var move_block_left = x - bar_area_left // 小方块相对于父元素的left值
                    // eslint-disable-next-line camelcase
                    if (move_block_left >= this.barArea.offsetWidth - parseInt(parseInt(this.blockSize.width) / 2) - 2) {
                        // eslint-disable-next-line camelcase
                        move_block_left = this.barArea.offsetWidth - parseInt(parseInt(this.blockSize.width) / 2) - 2
                    }
                    // eslint-disable-next-line camelcase
                    if (move_block_left <= 0) {
                        // eslint-disable-next-line camelcase
                        move_block_left = parseInt(parseInt(this.blockSize.width) / 2)
                    }
                    // 拖动后小方块的left值
                    // eslint-disable-next-line camelcase
                    this.moveBlockLeft = (move_block_left - this.startLeft) + 'px'
                    // eslint-disable-next-line camelcase
                    this.leftBarWidth = (move_block_left - this.startLeft) + 'px'
                }
            },

            // 鼠标松开
            end: function () {
                this.endMovetime = +new Date()
                var _this = this
                // 判断是否重合
                // eslint-disable-next-line eqeqeq
                if (this.status && this.isEnd == false) {
                    var moveLeftDistance = parseInt((this.moveBlockLeft || '').replace('px', ''))
                    moveLeftDistance = moveLeftDistance * 310 / parseInt(this.setSize.imgWidth)
                    const data = {
                        captchaType: this.captchaType,
                        'pointJson': this.secretKey ? aesEncrypt(JSON.stringify({ x: moveLeftDistance, y: 5.0 }), this.secretKey) : JSON.stringify({ x: moveLeftDistance, y: 5.0 }),
                        'token': this.backToken
                    }
                    reqCheck(data).then(res => {
                        // eslint-disable-next-line eqeqeq
                        if (res.repCode == '0000') {
                            this.moveBlockBackgroundColor = '#5cb85c'
                            this.leftBarBorderColor = '#5cb85c'
                            this.iconColor = '#fff'
                            this.iconClass = 'icon-check'
                            this.showRefresh = false
                            this.isEnd = true
                            // eslint-disable-next-line eqeqeq
                            if (this.mode == 'pop') {
                                setTimeout(() => {
                                    this.$parent.clickShow = false
                                    this.refresh()
                                }, 1500)
                            }
                            this.passFlag = true
                            this.tipWords = `${((this.endMovetime - this.startMoveTime) / 1000).toFixed(2)}s验证成功`
                            var captchaVerification = this.secretKey ? aesEncrypt(this.backToken + '---' + JSON.stringify({ x: moveLeftDistance, y: 5.0 }), this.secretKey) : this.backToken + '---' + JSON.stringify({ x: moveLeftDistance, y: 5.0 })
                            setTimeout(() => {
                                this.tipWords = ''
                                this.$parent.closeBox()
                                this.$parent.$emit('success', { captchaVerification })
                            }, 1000)
                        } else {
                            this.moveBlockBackgroundColor = '#d9534f'
                            this.leftBarBorderColor = '#d9534f'
                            this.iconColor = '#fff'
                            this.iconClass = 'icon-close'
                            this.passFlag = false
                            setTimeout(function () {
                                _this.refresh()
                            }, 1000)
                            this.$parent.$emit('error', this)
                            this.tipWords = '验证失败'
                            setTimeout(() => {
                                    this.tipWords = ''
                            }, 1000)
                        }
                    })
                    this.status = false
                }
            },

            refresh: function () {
                this.showRefresh = true
                this.finishText = ''

                this.transitionLeft = 'left .3s'
                this.moveBlockLeft = 0

                this.leftBarWidth = undefined
                this.transitionWidth = 'width .3s'

                this.leftBarBorderColor = '#ddd'
                this.moveBlockBackgroundColor = '#fff'
                this.iconColor = '#000'
                this.iconClass = 'icon-right'
                this.isEnd = false

                this.getPictrue()
                setTimeout(() => {
                    this.transitionWidth = ''
                    this.transitionLeft = ''
                    this.text = this.explain
                }, 300)
            },

            // 请求背景图片和验证图片
            getPictrue() {
                const data = {
                    captchaType: this.captchaType
                }
                reqGet(data).then(res => {
                    // eslint-disable-next-line eqeqeq
                    if (res.repCode == '0000') {
                        this.backImgBase = res.repData.originalImageBase64
                        this.blockBackImgBase = res.repData.jigsawImageBase64
                        this.backToken = res.repData.token
                        this.secretKey = res.repData.secretKey
                    } else {
                        this.tipWords = res.repMsg
                    }
                })
            }
        },
        watch: {
            // type变化则全面刷新
            type: {
                immediate: true,
                handler() {
                    this.init()
                }
            }
        },
        mounted() {
            // 禁止拖拽
            this.$el.onselectstart = function () {
                return false
            }
        }
    }
</script>
