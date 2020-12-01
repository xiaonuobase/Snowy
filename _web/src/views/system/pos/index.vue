<template>
  <a-card :bordered="false">

    <div class="table-page-search-wrapper" v-if="hasPerm('sysPos:page')">
      <a-form layout="inline">
        <a-row :gutter="48">
          <a-col :md="8" :sm="24">
            <a-form-item label="职位名称" >
              <a-input v-model="queryParam.name" allow-clear placeholder="请输入职位名称"/>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item label="唯一编码" >
              <a-input v-model="queryParam.code" allow-clear placeholder="请输入唯一编码" />
            </a-form-item>
          </a-col>
          <a-col :md="!advanced && 8 || 24" :sm="24">
            <span class="table-page-search-submitButtons" :style="advanced && { float: 'right', overflow: 'hidden' } || {} ">
              <a-button type="primary" @click="$refs.table.refresh(true)">查询</a-button>
              <a-button style="margin-left: 8px" @click="() => queryParam = {}">重置</a-button>
            </span>
          </a-col>
        </a-row>

      </a-form>
    </div>

    <div class="table-operator" v-if="hasPerm('sysPos:add')" >
      <a-button type="primary" v-if="hasPerm('sysPos:add')" icon="plus" @click="$refs.addForm.add()">新增职位</a-button>
    </div>

    <s-table
      ref="table"
      size="default"
      :columns="columns"
      :data="loadData"
      :alert="true"
      :rowKey="(record) => record.code"
      :rowSelection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
    >
      <span slot="action" slot-scope="text, record">
        <a v-if="hasPerm('sysPos:edit')" @click="$refs.editForm.edit(record)">编辑</a>
        <a-divider type="vertical" v-if="hasPerm('sysPos:edit') & hasPerm('sysPos:delete')"/>
        <a-popconfirm v-if="hasPerm('sysPos:delete')" placement="topRight" title="确认删除？" @confirm="() => sysPosDelete(record)">
          <a>删除</a>
        </a-popconfirm>
      </span>

    </s-table>

    <add-form ref="addForm" @ok="handleOk" />
    <edit-form ref="editForm" @ok="handleOk" />

  </a-card>
</template>

<script>
  import { STable } from '@/components'
  import { sysPosPage, sysPosDelete } from '@/api/modular/system/posManage'
  import addForm from './addForm'
  import editForm from './editForm'

  export default {
    components: {
      STable,
      addForm,
      editForm
    },

    data () {
      return {

        // 高级搜索 展开/关闭
        advanced: false,
        // 查询参数
        queryParam: {},
        // 表头
        columns: [
          {
            title: '职位名称',
            dataIndex: 'name'
          },
          {
            title: '唯一编码',
            dataIndex: 'code'
          },
          {
            title: '排序',
            dataIndex: 'sort'
          },
          {
            title: '备注',
            dataIndex: 'remark'
          }
        ],
        // 加载数据方法 必须为 Promise 对象
        loadData: parameter => {
          return sysPosPage(Object.assign(parameter, this.queryParam)).then((res) => {
            return res.data
          })
        },
        selectedRowKeys: [],
        selectedRows: []
    }
    },

    created () {
      if (this.hasPerm('sysPos:edit') || this.hasPerm('sysPos:delete')) {
        this.columns.push({
          title: '操作',
          width: '150px',
          dataIndex: 'action',
          scopedSlots: { customRender: 'action' }
        })
      }
    },

    methods: {
      sysPosDelete (record) {
        sysPosDelete(record).then((res) => {
          if (res.success) {
            this.$message.success('删除成功')
            this.$refs.table.refresh()
          } else {
            this.$message.error('删除失败：' + res.message)
          }
        }).catch((err) => {
          this.$message.error('删除错误：' + err.message)
        })
      },

      toggleAdvanced () {
        this.advanced = !this.advanced
      },
      handleOk () {
        this.$refs.table.refresh()
      },
      onSelectChange (selectedRowKeys, selectedRows) {
        this.selectedRowKeys = selectedRowKeys
        this.selectedRows = selectedRows
      }
    }

  }
</script>

<style lang="less">
  .table-operator {
    margin-bottom: 18px;
  }
  button {
    margin-right: 8px;
  }

</style>
