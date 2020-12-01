<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper" v-if="hasPerm('sysConfig:page')">
      <a-form layout="inline">
        <a-row :gutter="48">
          <a-col :md="8" :sm="24">
            <a-form-item label="参数名称">
              <a-input v-model="queryParam.name" allow-clear placeholder="请输入参数名称"/>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item label="唯一编码">
              <a-input v-model="queryParam.code" allow-clear placeholder="请输入唯一编码"/>
            </a-form-item>
          </a-col>
          <template v-if="advanced">
            <a-col :md="8" :sm="24">
              <a-form-item label="所属分类">
                <a-select v-model="queryParam.groupCode" placeholder="请选择所属分类" allow-clear>
                  <a-select-option v-for="(item,index) in groupCodeDictTypeDropDown" :key="index" :value="item.code" >{{ item.value }}</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
          </template>
          <a-col :md="!advanced && 8 || 24" :sm="24" >
            <span class="table-page-search-submitButtons" :style="advanced && { float: 'right', overflow: 'hidden' } || {} ">
              <a-button type="primary" @click="$refs.table.refresh(true)" >查询</a-button>
              <a-button style="margin-left: 8px" @click="() => queryParam = {}">重置</a-button>
              <a @click="toggleAdvanced" style="margin-left: 8px">
                {{ advanced ? '收起' : '展开' }}
                <a-icon :type="advanced ? 'up' : 'down'"/>
              </a>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div class="table-operator" v-if="hasPerm('sysConfig:add')" >
      <a-button type="primary" v-if="hasPerm('sysConfig:add')" icon="plus" @click="$refs.addForm.add()">新增配置</a-button>
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
      <span slot="name" slot-scope="text">
        <ellipsis :length="20" tooltip>{{ text }}</ellipsis>
      </span>
      <span slot="code" slot-scope="text">
        <ellipsis :length="10" tooltip>{{ text }}</ellipsis>
      </span>
      <span slot="value" slot-scope="text">
        <ellipsis :length="16" tooltip>{{ text }}</ellipsis>
      </span>
      <span slot="remark" slot-scope="text">
        <ellipsis :length="16" tooltip>{{ text }}</ellipsis>
      </span>
      <span slot="groupCode" slot-scope="text">
        {{ groupCodeFilter(text) }}
      </span>
      <span slot="action" slot-scope="text, record">
        <a v-if="hasPerm('sysConfig:edit')" @click="$refs.editForm.edit(record)">编辑</a>
        <a-divider type="vertical" v-if="hasPerm('sysConfig:edit') & hasPerm('sysConfig:delete')"/>
        <a-popconfirm v-if="hasPerm('sysConfig:delete')" placement="topRight" title="确认删除？" @confirm="() => sysConfigDelete(record)">
          <a>删除</a>
        </a-popconfirm>
      </span>
    </s-table>
    <add-form ref="addForm" @ok="handleOk" v-if="hasPerm('sysConfig:add')"/>
    <edit-form ref="editForm" @ok="handleOk" v-if="hasPerm('sysConfig:edit')"/>
  </a-card>
</template>
<script>
  import { STable, Ellipsis } from '@/components'
  import { sysConfigPage, sysConfigDelete } from '@/api/modular/system/configManage'
  import { sysDictTypeDropDown } from '@/api/modular/system/dictManage'
  import addForm from './addForm'
  import editForm from './editForm'
  export default {
    components: {
      STable,
      Ellipsis,
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
            title: '参数名称',
            dataIndex: 'name',
            scopedSlots: { customRender: 'name' }
          },
          {
            title: '唯一编码',
            dataIndex: 'code',
            scopedSlots: { customRender: 'code' }
          },
          {
            title: '参数值',
            dataIndex: 'value',
            scopedSlots: { customRender: 'value' }
          },
          {
            title: '所属分类',
            dataIndex: 'groupCode',
            scopedSlots: { customRender: 'groupCode' }
          },
          {
            title: '备注',
            dataIndex: 'remark',
            scopedSlots: { customRender: 'remark' }
          }
        ],
        // 加载数据方法 必须为 Promise 对象
        loadData: parameter => {
          return sysConfigPage(Object.assign(parameter, this.queryParam)).then((res) => {
            return res.data
          })
        },
        selectedRowKeys: [],
        selectedRows: [],
        groupCodeDictTypeDropDown: []
      }
    },
    /**
     * 初始化判断按钮权限是否拥有，没有则不现实列
     */
    created () {
      this.sysDictTypeDropDown()
      if (this.hasPerm('sysConfig:edit') || this.hasPerm('sysConfig:delete')) {
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
       * 获取字典数据
       */
      sysDictTypeDropDown () {
        sysDictTypeDropDown({ code: 'consts_type' }).then((res) => {
          this.groupCodeDictTypeDropDown = res.data
        })
      },
      groupCodeFilter (groupCode) {
        // eslint-disable-next-line eqeqeq
        const values = this.groupCodeDictTypeDropDown.filter(item => item.code == groupCode)
        if (values.length > 0) {
          return values[0].value
        }
      },
      sysConfigDelete (record) {
        sysConfigDelete(record).then((res) => {
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
