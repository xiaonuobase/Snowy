<template>
  <a-card :bordered="false">

    <div class="table-page-search-wrapper" v-if="hasPerm('sysDictType:page')">
      <a-form layout="inline">
        <a-row :gutter="48">
          <a-col :md="8" :sm="24">
            <a-form-item label="类型名称" >
              <a-input v-model="queryParam.name" allow-clear placeholder="请输入类型名称"/>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item label="唯一编码" v-if="hasPerm('sysDictType:page')">
              <a-input v-model="queryParam.code" allow-clear placeholder="请输入唯一编码"/>
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

    <div class="table-operator" v-if="hasPerm('sysDictType:add')" >
      <a-button type="primary" v-if="hasPerm('sysDictType:add')" icon="plus" @click="$refs.addForm.add()">新增类型</a-button>
    </div>

    <s-table
      ref="table"
      size="default"
      :columns="columns"
      :data="loadData"
      :alert="false"
      :rowKey="(record) => record.code"
      :rowSelection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
    >
      <span slot="status" slot-scope="text">
        {{ statusFilter(text) }}
      </span>

      <span slot="action" slot-scope="text, record">
        <a @click="$refs.dataIndex.index(record)">字典</a>
        <a-divider type="vertical" v-if="hasPerm('sysDictType:edit') || hasPerm('sysDictType:delete')"/>
        <a-dropdown v-if="hasPerm('sysDictType:edit') || hasPerm('sysDictType:delete')">
          <a class="ant-dropdown-link">
            更多 <a-icon type="down" />
          </a>
          <a-menu slot="overlay">

            <a-menu-item v-if="hasPerm('sysDictType:edit')">
              <a @click="$refs.editForm.edit(record)">编辑</a>
            </a-menu-item>

            <a-menu-item v-if="hasPerm('sysDictType:delete')">
              <a-popconfirm placement="topRight" title="确认删除？" @confirm="() => sysDictTypeDelete(record)">
                <a>删除</a>
              </a-popconfirm>
            </a-menu-item>
          </a-menu>
        </a-dropdown>
      </span>

    </s-table>

    <add-form ref="addForm" @ok="handleOk" />
    <edit-form ref="editForm" @ok="handleOk" />
    <data-index ref="dataIndex" @ok="handleOk" />

  </a-card>
</template>

<script>
  import { STable } from '@/components'
  import { sysDictTypePage, sysDictTypeDelete, sysDictTypeDropDown } from '@/api/modular/system/dictManage'
  import addForm from './addForm'
  import editForm from './editForm'
  import dataIndex from './dictdata/index'

  export default {
    components: {
      STable,
      addForm,
      editForm,
      dataIndex
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
            title: '类型名称',
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
            dataIndex: 'remark',
            width: 200
          },
          {
            title: '状态',
            dataIndex: 'status',
            scopedSlots: { customRender: 'status' }
          }, {
            title: '操作',
            width: '150px',
            dataIndex: 'action',
            scopedSlots: { customRender: 'action' }
          }
        ],
        // 加载数据方法 必须为 Promise 对象
        loadData: parameter => {
          return sysDictTypePage(Object.assign(parameter, this.queryParam)).then((res) => {
            return res.data
          })
        },
        selectedRowKeys: [],
        selectedRows: [],
        statusDict: []
      }
    },
    created () {
      this.sysDictTypeDropDown()
    },

    methods: {

      statusFilter (status) {
        // eslint-disable-next-line eqeqeq
        const values = this.statusDict.filter(item => item.code == status)
        if (values.length > 0) {
          return values[0].value
        }
      },

      /**
       * 获取字典数据
       */
      sysDictTypeDropDown () {
        sysDictTypeDropDown({ code: 'common_status' }).then((res) => {
          this.statusDict = res.data
        })
      },

      sysDictTypeDelete (record) {
        sysDictTypeDelete(record).then((res) => {
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
