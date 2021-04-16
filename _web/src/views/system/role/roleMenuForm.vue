<template>
  <a-modal
    title="授权菜单"
    :width="600"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleSubmit"
    @cancel="handleCancel"
  >
    <a-spin :spinning="formLoading">
      <a-form :form="form">

        <a-form-item
          label="菜单权限"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol">

          <a-tree
            v-model="checkedKeys"
            multiple
            checkable
            :auto-expand-parent="autoExpandParent"
            :expanded-keys="expandedKeys"
            :tree-data="menuTreeData"
            :selected-keys="selectedKeys"
            :replaceFields="replaceFields"
            @expand="onExpand"
            @check="onCheck"
          />
        </a-form-item>

      </a-form>

    </a-spin>
  </a-modal>
</template>

<script>
  import { SysMenuTreeForGrant } from '@/api/modular/system/menuManage'
  import { sysRoleOwnMenu, sysRoleGrantMenu } from '@/api/modular/system/roleManage'

  export default {
    data() {
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
        menuTreeData: [],
        expandedKeys: [],
        checkedKeys: [],
        visible: false,
        confirmLoading: false,
        formLoading: true,
        autoExpandParent: true,
        selectedKeys: [],
        subValues: [],
        roleEntity: [],
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
      roleMenu(record) {
        this.formLoading = true
        this.roleEntity = record
        this.visible = true
        this.getMenuTree()
      },

      /**
       * 获取菜单列表
       */
      getMenuTree() {
        const _this = this
        SysMenuTreeForGrant().then((res) => {
          if (res.success) {
            _this.menuTreeData = res.data
            _this.getLeastChilds(res.data)
            // 默认展开目录级
            _this.menuTreeData.forEach(item => {
              _this.expandedKeys.push(item.id)
            })

            _this.expandedMenuKeys(_this.roleEntity)
          }
        })
      },

      getLeastChilds(data) {
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

      /**
       * 此角色已有菜单权限
       */
      expandedMenuKeys(record) {
        const _this = this
        sysRoleOwnMenu({ id: record.id }).then((res) => {
          if (res.success) {
            _this.pickCheckedKeys(res.data)
            _this.commitKeys = res.data
          }
          _this.formLoading = false
        })
      },

      pickCheckedKeys(data) {
        for (let i = 0; i < data.length; i++) {
          if (this.leastChilds.includes(data[i])) {
            this.checkedKeys.push(data[i])
          }
        }
      },

      onExpand(expandedKeys) {
        this.expandedKeys = expandedKeys
        this.autoExpandParent = false
      },

      onCheck(checkedKeys, info) {
        this.checkedKeys = checkedKeys
        this.commitKeys = checkedKeys.concat(info.halfCheckedKeys)
      },

      onSelect(selectedKeys, info) {
        console.log(selectedKeys)
        console.log(info)
        this.selectedKeys = selectedKeys
      },

      handleSubmit() {
        const _this = this
        const { form: { validateFields } } = this
        this.confirmLoading = true
        validateFields((errors, values) => {
          if (!errors) {
            sysRoleGrantMenu({ id: _this.roleEntity.id, grantMenuIdList: _this.commitKeys }).then((res) => {
              if (res.success) {
                _this.$message.success('授权成功')
                _this.confirmLoading = false
                _this.$emit('ok', values)
                _this.handleCancel()
              } else {
                _this.$message.error('授权失败：' + res.message)
              }
            }).finally((res) => {
              _this.confirmLoading = false
            })
          } else {
            _this.confirmLoading = false
          }
        })
      },
      handleCancel() {
        // 清空已选择的
        this.checkedKeys = []
        // 清空已展开的
        this.expandedKeys = []
        this.visible = false
      }
    }
  }
</script>
