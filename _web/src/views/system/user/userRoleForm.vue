<template>
  <a-modal
    title="授权角色"
    :width="800"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleSubmit"
    @cancel="handleCancel"
  >

    <a-card :bordered="false">

      <div>
        <a-table
          :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
          :columns="columns"
          :dataSource="loadData"
          :pagination="false"
          :loading="loading"
          :rowKey="(record) => record.id"
        />
      </div>

    </a-card>

  </a-modal>
</template>

<script>
  import { getRolePage } from '@/api/modular/system/roleManage'
  import { sysUserOwnRole, sysUserGrantRole } from '@/api/modular/system/userManage'

  const columns = [
    {
      title: '角色名称',
      dataIndex: 'name'
    },
    {
      title: '唯一编码',
      dataIndex: 'code'
    }
  ]

  export default {
    name: 'UserRoleIndex',

    data () {
      return {
        columns,
        loadData: [],
        selectedRowKeys: [], // Check here to configure the default column
        loading: true,
        visible: false,
        confirmLoading: false,
        recordEntity: []
      }
    },
    computed: {
      hasSelected () {
        return this.selectedRowKeys.length > 0
      }
    },
    methods: {
      // 初始化方法
      userRole (record) {
        this.recordEntity = record
        this.visible = true
        // 加载已有数据
        this.sysUserOwnRole()
        // 获取全部列表,无需分页
        getRolePage().then((res) => {
          this.loadData = res.data.rows
        })
      },

      /**
       * 获取用户已有角色
       */
      sysUserOwnRole () {
        this.loading = true
        sysUserOwnRole({ id: this.recordEntity.id }).then((res) => {
          // 选中多选框
          this.selectedRowKeys = res.data
          this.loading = false
        })
      },

      onSelectChange (selectedRowKeys) {
        this.selectedRowKeys = selectedRowKeys
      },

      handleSubmit () {
        // eslint-disable-next-line no-unused-expressions
        this.confirmLoading = false
        this.visible = false
        sysUserGrantRole({ id: this.recordEntity.id, grantRoleIdList: this.selectedRowKeys }).then((res) => {
               if (res.success) {
                 this.$message.success('授权成功')
                 this.confirmLoading = false
                 this.$emit('ok', this.recordEntity)
                 this.handleCancel()
               } else {
                 this.$message.error('授权失败：' + res.message)
               }
             }).finally((res) => {
               this.confirmLoading = false
             })
      },
      handleCancel () {
        this.recordEntity = []
        this.selectedRowKeys = []
        this.visible = false
      }
    }
  }
</script>
