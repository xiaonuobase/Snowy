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
            @select="onSelect"
          />
        </a-form-item>

      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
  import { getOrgTree } from '@/api/modular/system/orgManage'
  import { sysUserOwnData, sysUserGrantData } from '@/api/modular/system/userManage'

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
        userEntity: [],
        replaceFields: {
          key: 'id'
        },
        form: this.$form.createForm(this)
      }
    },

    methods: {
      // 初始化方法
      userOrg (record) {
        this.userEntity = record
        this.visible = true
        // 获取机构树
        this.getOrgTree()
        // 已关联数据
        this.sysUserOwnData(this.userEntity)
      },

      /**
       * 获取机构树
       */
      getOrgTree () {
        this.formLoading = true
        getOrgTree().then((res) => {
           if (res.success) {
             this.orgTreeData = res.data
             // 默认展开
             this.orgTreeData.forEach(item => {
               this.expandedKeys.push(item.id)
             })
           }
        })
      },

      /**
       * 此用户已有数据列表
       */
      sysUserOwnData (record) {
        sysUserOwnData({ id: record.id }).then((res) => {
          if (res.success) {
            this.checkedKeys = res.data
          }
          this.formLoading = false
        })
      },

      onExpand (expandedKeys) {
        this.expandedKeys = expandedKeys
        this.autoExpandParent = false
      },
      onCheck (checkedKeys) {
        this.checkedKeys = checkedKeys
      },
      onSelect (selectedKeys, info) {
        this.selectedKeys = selectedKeys
      },

      handleSubmit () {
        const { form: { validateFields } } = this
        this.confirmLoading = true
        validateFields((errors, values) => {
          if (!errors) {
            sysUserGrantData({ id: this.userEntity.id, grantOrgIdList: this.checkedKeys }).then((res) => {
              if (res.success) {
                this.$message.success('授权成功')
                this.confirmLoading = false
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
      }
    }
  }
</script>
