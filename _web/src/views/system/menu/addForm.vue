<template>
  <a-modal
    title="新增菜单"
    :width="1000"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleSubmit"
    @cancel="handleCancel"
    :destroyOnClose="true"
  >
    <a-spin :spinning="formLoading">
      <a-form :form="form" >

        <a-row :gutter="24">
          <a-col :md="12" :sm="24">
            <a-form-item
              label="菜单名称"
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              hasFeedback
            >
              <a-input placeholder="请输入菜单名称" v-decorator="['name',{rules: [{required: true, min: 1, message: '请输入菜单名称！'}]}]" />
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item
              style="width: 100%"
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="菜单编号"
              hasFeedback
            >
              <a-input placeholder="请输入菜单编号" v-decorator="['code', {rules: [{required: true, min: 1, message: '请输入菜单编号！'}]}]" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="24">
          <a-col :md="12" :sm="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="所属应用"
              has-feedback
            >
              <a-select style="width: 100%" placeholder="请选择应用分类" v-decorator="['application', {rules: [{ required: true, message: '请选择应用分类！' }]}]" >
                <a-select-option v-for="(item,index) in appData" :key="index" :value="item.code" @click="changeApplication(item.code)">{{ item.name }}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="菜单层级"
            >
              <a-radio-group v-decorator="['type',{rules: [{ required: true, message: '请选择菜单层级！' }]}]" >
                <a-radio v-for="(item,index) in typeData" :key="index" :value="item.code" @click="meneTypeFunc(item.code)">{{ item.value }}</a-radio>
              </a-radio-group>
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="24">
          <a-col :md="12" :sm="24">
            <div v-show="pidShow">
              <a-form-item
                :labelCol="labelCol"
                :wrapperCol="wrapperCol"
                label="父级菜单"
                has-feedback
              >
                <a-tree-select
                  v-decorator="['pid', {rules: [{ required: true, message: '请选择父级菜单！' }]}]"
                  style="width: 100%"
                  :dropdownStyle="{ maxHeight: '300px', overflow: 'auto' }"
                  :treeData="menuTreeData"
                  placeholder="请选择父级菜单"
                  treeDefaultExpandAll
                >
                  <span slot="title" slot-scope="{ id }">{{ id }}
                  </span>
                </a-tree-select>
              </a-form-item>
            </div>
            <div v-show="redirectShow">
              <a-form-item
                :labelCol="labelCol"
                :wrapperCol="wrapperCol"
              >
                <span slot="label">
                  <a-tooltip title="如需打开首页加载此目录下菜单，请填写加载菜单路由，设为首页后其他设置的主页将被替代">
                    <a-icon type="question-circle-o" />
                  </a-tooltip>&nbsp;
                  重定向
                </span>
                <a-input prop="redirect" placeholder="请输入重定向地址" v-decorator="['redirect']" />
              </a-form-item>
            </div>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
            >
              <span slot="label">
                <a-tooltip title="按钮：无，菜单：内链、外链、组件">
                  <a-icon type="question-circle-o" />
                </a-tooltip>&nbsp;
                打开方式
              </span>
              <a-radio-group :disabled="openTypeDisabled" v-decorator="['openType',{rules: [{ required: true, message: '请选择打开方式！' }]}]">
                <a-radio v-for="(item,index) in openTypeData" :key="index" :value="item.code" @click="meneOpenTypeFunc(item.code)">{{ item.value }}</a-radio>
              </a-radio-group>
            </a-form-item>

          </a-col>
        </a-row>

        <a-divider />

        <a-row :gutter="24" >
          <a-col :md="12" :sm="24">
            <div v-show="componentShow">
              <a-form-item
                :labelCol="labelCol"
                :wrapperCol="wrapperCol"
                hasFeedback
              >
                <span slot="label">
                  <a-tooltip title="前端vue组件 views文件夹下路径，例：system/menu/index。注：目录级填写：RouteView(不带面包屑)，PageView(带面包屑)，菜单级内链打开http链接填写：Iframe">
                    <a-icon type="question-circle-o" />
                  </a-tooltip>&nbsp;
                  前端组件
                </span>
                <a-input placeholder="请输入前端组件" :disabled="componentDisabled" prop="component" v-decorator="['component',{rules: [{required: componentRequired, message: '请输入前端组件'}]}]"/><!-- ,{rules: [{required: componentRequired, min: 1, message: '请输入前端组件！'}]}  -->
              </a-form-item>
            </div>
          </a-col>
          <a-col :md="12" :sm="24">
            <div v-show="routerShow">
              <a-form-item
                :labelCol="labelCol"
                :wrapperCol="wrapperCol"
                hasFeedback
              >
                <span slot="label">
                  <a-tooltip title="浏览器显示的URL，例：/menu，对应打开页面为菜单页面">
                    <a-icon type="question-circle-o" />
                  </a-tooltip>&nbsp;
                  路由地址
                </span>
                <a-input placeholder="请输入路由" v-decorator="['router', {rules: [{required: routerRequired, message: '请输入路由！'}]}]" />
              </a-form-item>
            </div>
            <div v-show="permissionShow">
              <a-form-item
                :labelCol="labelCol"
                :wrapperCol="wrapperCol"
                label="权限标识"
                hasFeedback
              >
                <a-input placeholder="请输入权限标识" v-decorator="['permission', {rules: [{required: permissionRequired, message: '请输入权限标识！'}]}]" />
              </a-form-item>
            </div>
          </a-col>
        </a-row>

        <a-row :gutter="24">
          <a-col :md="12" :sm="24">
            <div v-show="linkShow" >
              <a-form-item
                :labelCol="labelCol"
                :wrapperCol="wrapperCol"
                hasFeedback
              >
                <span slot="label">
                  <a-tooltip title="当选择了需要内链或外链打开的选项，此处输入要打开的链接地址，例：https://www.xiaonuo.vip">
                    <a-icon type="question-circle-o" />
                  </a-tooltip>&nbsp;
                  内外链地址
                </span>
                <a-input placeholder="请输入内链打开地址" :disabled="linkDisabled" v-decorator="['link', {rules: [{required: linkRequired, message: '请输入权限标识！'}]}]" />
              </a-form-item>
            </div>
          </a-col>
          <a-col :md="12" :sm="24">
            <div v-show="iconShow" >
              <a-form-item
                :labelCol="labelCol"
                :wrapperCol="wrapperCol"
                label="图标"
              >
                <a-input placeholder="请选择图标" disabled="disabled" v-decorator="['icon']" >
                  <a-icon slot="addonAfter" @click="openIconSele()" type="setting" />
                </a-input>
              </a-form-item>
            </div>
          </a-col>
        </a-row>

        <a-row :gutter="24">
          <a-col :md="12" :sm="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
            >
              <span slot="label">
                <a-tooltip title="系统权重：菜单可分配给任何角色，业务权重：菜单对超级管理员不可见">
                  <a-icon type="question-circle-o" />
                </a-tooltip>&nbsp;
                权重
              </span>
              <a-radio-group v-decorator="['weight']">
                <a-radio v-for="(item,index) in weightData" :key="index" :value="item.code" >{{ item.value }}</a-radio>
              </a-radio-group>
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="是否可见"
            >
              <a-switch id="visible" checkedChildren="是" unCheckedChildren="否" v-decorator="['visible', { valuePropName: 'checked' }]"/><!-- defaultChecked -->
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="24">
          <a-col :md="12" :sm="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="排序"
            >
              <a-input-number style="width: 100%" v-decorator="['sort', { initialValue: 100 }]" :min="1" :max="1000" />
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="备注"
              hasFeedback
            >
              <a-input placeholder="请输入备注" v-decorator="['remark']"></a-input>
            </a-form-item>
          </a-col>
        </a-row>

      </a-form>
    </a-spin>
    <a-modal
      :width="850"
      :visible="visibleIcon"
      @cancel="handleCancelIcon"
      footer=""
      :mask="false"
      :closable="false"
      :destroyOnClose="true"
    >
      <icon-selector v-model="currentSelectedIcon" @change="handleIconChange"/>
    </a-modal>
  </a-modal>
</template>

<script>
  import { getAppList } from '@/api/modular/system/appManage'
  import { getMenuTree, sysMenuAdd } from '@/api/modular/system/menuManage'
  import IconSelector from '@/components/IconSelector'
  import { sysDictTypeDropDown } from '@/api/modular/system/dictManage'
  export default {
    components: { IconSelector },

    data () {
      return {
        labelCol: {
          xs: { span: 24 },
          sm: { span: 6 }
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 }
        },
        visibleIcon: false,
        visible: false,
        confirmLoading: false,
        appData: [],
        menuTreeData: [],
        redirectShow: true,
        componentShow: true,
        componentDisabled: false,
        componentRequired: true,
        routerRequired: true,
        routerShow: true,
        iconShow: true,
        openTypeShow: true,
        pidShow: true,
        permissionShow: true,
        permissionRequired: true,
        // 图标组件
        currentSelectedIcon: 'pause-circle',
        typeData: [],
        openTypeData: [],
        weightData: [],
        formLoading: true,
        linkShow: true,
        openTypeDisabled: false,
        openTypeDefault: [],
        openType: '',
        linkRequired: true,
        linkDisabled: false,
        type: '',
        form: this.$form.createForm(this)
      }
    },

    methods: {
      // 打开页面初始化
      add (type) {
        this.visible = true
        // 图标
        this.currentSelectedIcon = type

        // 默认选中菜单项，并初始化
        this.form.getFieldDecorator('type', { valuePropName: 'checked', initialValue: '1' })
        this.meneTypeFunc('1')

        // 默认选中的单选框
        // this.form.getFieldDecorator('type',{valuePropName:'checked',initialValue:'1'})
        this.form.getFieldDecorator('weight', { valuePropName: 'checked', initialValue: '2' })
        this.form.getFieldDecorator('visible', { initialValue: true })

        // 获取系统应用列表
        this.getSysApplist()
        this.sysDictTypeDropDown()
      },

      /**
       * 获取字典数据
       */
      sysDictTypeDropDown () {
        this.formLoading = true
        // 菜单类型
        sysDictTypeDropDown({ code: 'menu_type' }).then((res) => {
          this.typeData = res.data
        })
        // 权重
        sysDictTypeDropDown({ code: 'menu_weight' }).then((res) => {
          this.weightData = res.data
        })
        // 内外链
        sysDictTypeDropDown({ code: 'open_type' }).then((res) => {
          this.openTypeData = res.data
          this.formLoading = false
        })
      },

      getSysApplist () {
        return getAppList().then((res) => {
          if (res.success) {
            this.appData = res.data
          } else {
            this.$message.warning(res.message)
          }
        })
      },
      changeApplication (value) {
        getMenuTree({ 'application': value }).then((res) => {
          if (res.success) {
            this.form.resetFields(`pid`, [])
            this.menuTreeData = [{
              'id': '-1',
              'parentId': '0',
              'title': '顶级',
              'value': '0',
              'pid': '0',
              'children': res.data
            }]
          } else {
            this.$message.warning(res.message)
          }
        })
      },

      /**
       * 选择菜单类型执行初始化表单变量
       */
      meneTypeFunc (type) {
        this.type = type
        // eslint-disable-next-line eqeqeq
        if (type == '0' || type == '1') {
          // 内外链地址显示，给空值
          this.linkShow = true
          this.form.resetFields(`link`, [])
          // 图标选择显示
          this.iconShow = true
          // 路由必填，设置空值，并显示
          this.routerRequired = true
          this.form.getFieldDecorator('router', { initialValue: '' })
          this.routerShow = true
          // 权限标识框隐藏，选填，给空值
          this.permissionShow = false
          this.permissionRequired = false
          this.form.getFieldDecorator('permission', { initialValue: '' })
          // 打开方式设置为组件 ，禁用选择方式
          this.openType = '1'
          this.form.getFieldDecorator('openType', { initialValue: this.openType = '1' })
          this.openTypeDisabled = false
        }
        // eslint-disable-next-line eqeqeq
        if (type == '0') {
          // 重定向展示，并给空
          this.redirectShow = true
          this.form.resetFields(`redirect`, [])
          // 组件默认为显示，设置可输入，给默认组件 PageView，验证必填
          this.componentShow = true
          this.componentDisabled = false
          this.form.getFieldDecorator('component', { initialValue: 'PageView' })
          this.componentRequired = true
          // 父级初始化顶级，并将其隐藏
          this.form.getFieldDecorator('pid', { initialValue: '0' })
          this.pidShow = false
        } else {
          // eslint-disable-next-line eqeqeq
          if (type == '1') {
            // 组件可以手输，取消值
            this.componentDisabled = false
            this.form.getFieldDecorator('component', { initialValue: '' })
          }
          // 重定向输入隐藏，并给空值
          this.redirectShow = false
          this.form.getFieldDecorator('redirect', { initialValue: '' })
          // 父级选择放开
          this.pidShow = true
        }
        // eslint-disable-next-line eqeqeq
        if (type == '2') {
          // 组件设置不填，不可输入，并给空（手输的跟设置的）
          this.componentRequired = false
          this.componentDisabled = true
          this.form.resetFields(`component`, [])
          this.form.getFieldDecorator('component', { initialValue: '' })
          // 路由选填，设置空值，并隐藏
          this.routerRequired = true
          this.form.getFieldDecorator('router', { initialValue: '' })
          this.routerShow = false
          // 内外链地址隐藏，给空值
          this.linkShow = false
          this.form.getFieldDecorator('link', { initialValue: '' })
          // 权限标识框显示，必填，给空值
          this.permissionShow = true
          this.permissionRequired = true
          this.form.getFieldDecorator('permission', { initialValue: '' })
          // 图标选择隐藏,并给空
          this.iconShow = false
          this.form.getFieldDecorator('icon', { initialValue: '' })
          // 打开方式设置为无 ，禁用选择方式
          this.openType = '0'
          this.form.getFieldDecorator('openType', { initialValue: this.openType })
          this.openTypeDisabled = true
          // 取消icon
          this.form.getFieldDecorator('icon', { initialValue: '' })
        }
        this.meneOpenTypeFunc(this.openType)
      },

      /**
       * 选择打开方式执行方法
       */
      meneOpenTypeFunc (openType) {
         this.form.resetFields(`openType`, openType)
         // eslint-disable-next-line eqeqeq
         if (openType == '2' || openType == '3') {
           // 点击内外链的时候保留原值，其他清空
           if (this.linkDisabled === false) {
             this.form.resetFields(`link`, [])
           }
            // 设置内外链可手输，加验证
           this.linkDisabled = false
           this.linkRequired = true
         } else {
            // 设置内外链不可手输，取消值，取消验证
            this.linkDisabled = true
            this.form.resetFields(`link`, [])
            this.linkRequired = false
         }
         // 另起一个分支
        // eslint-disable-next-line eqeqeq
        if (openType == '3') {
          this.componentRequired = false
          this.componentDisabled = true
          this.form.resetFields(`component`, [])
          this.form.getFieldDecorator('component', { initialValue: '' })
        } else {
          this.componentRequired = true
          // eslint-disable-next-line eqeqeq
          if (this.type == '1' || this.type == '2') {
            this.form.getFieldDecorator('component', { initialValue: '' })
          } else {
            this.form.resetFields(`component`, [])
            this.form.getFieldDecorator('component', { initialValue: 'PageView' })
          }
          // eslint-disable-next-line eqeqeq
          if (openType == '2') {
            // 组件设置为 iframe
            this.form.resetFields(`component`, [])
            this.form.getFieldDecorator('component', { initialValue: 'Iframe' })
          }
        }
        // eslint-disable-next-line eqeqeq
        if (this.type == '2') {
          // eslint-disable-next-line eqeqeq
          if (openType == '0') {
            this.componentRequired = false
            this.routerRequired = false
          }
        }
      },

      openIconSele () {
        this.visibleIcon = true
      },
      handleIconChange (icon) {
        this.form.getFieldDecorator('icon', { initialValue: icon })
        this.visibleIcon = false
      },
      handleCancelIcon () {
        this.visibleIcon = false
      },
      handleSubmit () {
        const { form: { validateFields } } = this
        this.confirmLoading = true
        validateFields((errors, values) => {
          if (!errors) {
            if (values.visible) {
              values.visible = 'Y'
            } else {
              values.visible = 'N'
            }
            sysMenuAdd(values).then((res) => {
              this.confirmLoading = false
              if (res.success) {
                this.$message.success('新增成功')
                this.$emit('ok', values)
                this.handleCancel()
              } else {
                this.$message.error('新增失败：' + res.message)
              }
            }).finally((res) => {
              this.confirmLoading = false
            })
          } else {
            this.confirmLoading = false
          }
        })
      },
      handleCancel () {
        this.form.resetFields()
        this.confirmLoading = false
        this.visible = false
      }
    }

  }
</script>
