<template>
  <a-modal
    title="授权数据"
    :width="600"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleSubmit"
    @cancel="handleCancel"
  >
    <a-spin :spinning="formLoading">
      <a-form :form="form">
        <a-form-item
          label="授权范围"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          has-feedback
        >
          <a-select style="width: 100%" placeholder="请选择授权范围" v-decorator="['dataScopeType', {rules: [{ required: true, message: '请选择授权范围！' }]}]" >
            <a-select-option v-for="(item,index) in dataScopeTypeData" :key="index" :value="item.code" @click="handleChange(item.code)">{{ item.value }}</a-select-option>
          </a-select>
        </a-form-item>
        <div v-show="orgTreeShow">
          <a-form-item
            label="选择机构"
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
          >
            <a-tree
              v-model="checkedKeys"
              checkable
              :auto-expand-parent="autoExpandParent"
              :expanded-keys="expandedKeys"
              :tree-data="orgTreeData"
              :selected-keys="selectedKeys"
              :replaceFields="replaceFields"
              @expand="onExpand"
              @check="onCheck"
            />
          </a-form-item>
        </div>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
  import { getOrgTree } from '@/api/modular/system/orgManage'
  import { sysRoleOwnData, sysRoleGrantData } from '@/api/modular/system/roleManage'
  import { sysDictTypeDropDown } from '@/api/modular/system/dictManage'

  export default {
    data () {
      return {
        labelCol: {
          style: { 'padding-right': '20px' },
          xs: { span: 24 },
          sm: { span: 5 }
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 15 }
        },
        orgTreeData: [],
        expandedKeys: [],
        checkedKeys: [],
        visible: false,
        confirmLoading: false,
        formLoading: true,
        autoExpandParent: true,
        selectedKeys: [],
        subValues: [],
        roleEntity: [],
        dataScopeTypeData: [],
        orgTreeShow: false,
        replaceFields: {
          key: 'id'
        },
        form: this.$form.createForm(this),
        commitKeys: [],
        leastChilds: []
      }
    },

    methods: {
      // 初始化方法
      roleOrg (record) {
        this.roleEntity = record
        this.visible = true
        this.formLoading = true
        this.sysDictTypeDropDown()
        this.form.getFieldDecorator('dataScopeType', { initialValue: record.dataScopeType.toString() })
        this.handleChange(record.dataScopeType)
      },

      /**
       * 获取字典数据
       */
      sysDictTypeDropDown () {
        // 数据范围
        sysDictTypeDropDown({ code: 'data_scope_type' }).then((res) => {
          this.dataScopeTypeData = res.data
          this.formLoading = false
        })
      },

      // 范围下拉框事件
      handleChange (value) {
        // eslint-disable-next-line eqeqeq
        if (value == '5') {
          this.formLoading = true
          this.orgTreeShow = true
          // 获取机构树
          this.getOrgTree()
        } else {
          this.orgTreeShow = false
          // 清理已选中机构
          this.checkedKeys = []
        }
      },

      /**
       * 获取机构树
       */
      getOrgTree () {
        const _this = this
        getOrgTree().then((res) => {
           if (res.success) {
             this.orgTreeData = res.data

             _this.getLeastChilds(res.data)
             // 默认展开
             this.orgTreeData.forEach(item => {
               this.expandedKeys.push(item.id)
             })

             // 已关联数据
             this.sysRoleOwnData(_this.roleEntity)
           }
        })
      },

      /**
       * 此角色已有数据列表
       */
      sysRoleOwnData (record) {
        const _this = this
        sysRoleOwnData({ id: record.id }).then((res) => {
          if (res.success) {
            _this.pickCheckedKeys(res.data)
            _this.commitKeys = res.data
          }
          this.formLoading = false
        })
      },

      getLeastChilds(data){
        for (let i = 0; i < data.length; i++) {
          this.pushLeastChilds(data[i])
        }
      },

      pushLeastChilds(e) {
        if (e.children.length > 0) {
          this.getLeastChilds(e.children)
          return
        }
        this.leastChilds.push(e.id)
      },

      pickCheckedKeys(data){
        for (let i = 0; i < data.length; i++) {
          if (this.leastChilds.includes(data[i])) {
            this.checkedKeys.push(data[i])
          }
        }
      },

      onExpand (expandedKeys) {
        this.expandedKeys = expandedKeys
        this.autoExpandParent = false
      },
      onCheck (checkedKeys, info) {
        this.checkedKeys = checkedKeys
        this.commitKeys = checkedKeys.concat(info.halfCheckedKeys);
      },
      onSelect (selectedKeys, info) {
        this.selectedKeys = selectedKeys
      },

      handleSubmit () {
        const _this = this
        const { form: { validateFields } } = this
        this.confirmLoading = true
        validateFields((errors, values) => {
          if (!errors) {
            sysRoleGrantData({ id: this.roleEntity.id, grantOrgIdList: _this.commitKeys, dataScopeType: values.dataScopeType }).then((res) => {
              this.confirmLoading = false
              if (res.success) {
                this.$message.success('授权成功')
                this.$emit('ok', values)
                this.handleCancel()
              } else {
                this.$message.error('授权失败：' + res.message)
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
        // 清空已选择的
        this.checkedKeys = []
        // 清空已展开的
        this.expandedKeys = []
        this.visible = false
        // 隐藏机构树
        this.orgTreeShow = false
      }
    }
  }
</script>