<template>
  <div>
    <x-card v-if="hasPerm('sysPos:page')">
      <div slot="content" class="table-page-search-wrapper">
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
            <a-col :md="8" :sm="24">
              <a-button type="primary" @click="$refs.table.refresh(true)">查询</a-button>
              <a-button style="margin-left: 8px" @click="() => queryParam = {}">重置</a-button>
            </a-col>
          </a-row>
        </a-form>
      </div>
    </x-card>
    <a-card :bordered="false">
      <s-table
        ref="table"
        :columns="columns"
        :data="loadData"
        :alert="options.alert"
        :rowKey="(record) => record.id"
        :rowSelection="options.rowSelection"
      >
        <template slot="operator" v-if="hasPerm('sysPos:add')">
          <a-button @click="$refs.addForm.add()" icon="plus" type="primary" v-if="hasPerm('sysPos:add')">新增职位</a-button>
          <a-button type="danger" :disabled="selectedRowKeys.length < 1" v-if="hasPerm('sysPos:delete')" @click="batchDelete"><a-icon type="delete"/>批量删除</a-button>
          <x-down
            v-if="hasPerm('sysPos:export')"
            ref="batchExport"
            @batchExport="batchExport"
          />
        </template>
        <span slot="action" slot-scope="text, record">
          <a v-if="hasPerm('sysPos:edit')" @click="$refs.editForm.edit(record)">编辑</a>
          <a-divider type="vertical" v-if="hasPerm('sysPos:edit') & hasPerm('sysPos:delete')"/>
          <a-popconfirm v-if="hasPerm('sysPos:delete')" placement="topRight" title="确认删除？" @confirm="() => singleDelete(record)">
            <a>删除</a>
          </a-popconfirm>
        </span>
      </s-table>
      <add-form ref="addForm" @ok="handleOk" />
      <edit-form ref="editForm" @ok="handleOk" />
    </a-card>
  </div>
</template>

<script>
import { STable, XCard, XDown } from '@/components'
import { sysPosPage, sysPosDelete, sysPosExport } from '@/api/modular/system/posManage'
import addForm from './addForm'
import editForm from './editForm'

export default {
  components: {
    XDown,
    XCard,
    STable,
    addForm,
    editForm
  },

  data () {
    return {
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
      selectedRows: [],
      options: {
        alert: { show: true, clear: () => { this.selectedRowKeys = [] } },
        rowSelection: {
          selectedRowKeys: this.selectedRowKeys,
          onChange: this.onSelectChange
        }
      }
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
    /**
     * 单个删除
     */
    singleDelete (record) {
      const param = [{ 'id': record.id }]
      this.sysPosDelete(param)
    },
    /**
     * 批量删除
     */
    batchDelete () {
      const paramIds = this.selectedRowKeys.map((d) => {
        return { 'id': d }
      })
      this.sysPosDelete(paramIds)
    },
    /**
     * 删除
     */
    sysPosDelete (param) {
      sysPosDelete(param).then((res) => {
        if (res.success) {
          this.$message.success('删除成功')
          this.$refs.table.clearRefreshSelected()
        } else {
          this.$message.error('删除失败：' + res.message)
        }
      }).catch((err) => {
        this.$message.error('删除错误：' + err.message)
      })
    },
    /**
     * 批量导出
     */
    batchExport () {
      const paramIds = this.selectedRowKeys.map((d) => {
        return { 'id': d }
      })
      sysPosExport(paramIds).then((res) => {
        this.$refs.batchExport.downloadfile(res)
      })
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
