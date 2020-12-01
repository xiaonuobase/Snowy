<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper" v-if="hasPerm('sysTimers:page')">
      <a-form layout="inline">
        <a-row :gutter="48">
          <a-col :md="8" :sm="24">
            <a-form-item label="任务名称">
              <a-input v-model="queryParam.timerName" allow-clear placeholder="请输入任务名称"/>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item label="任务状态">
              <a-select v-model="queryParam.jobStatus" placeholder="请选择状态" >
                <a-select-option v-for="(item,index) in jobStatusDictTypeDropDown" :key="index" :value="item.code" >{{ item.value }}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-button type="primary" @click="$refs.table.refresh(true)">查询</a-button>
            <a-button style="margin-left: 8px" @click="() => queryParam = {}">重置</a-button>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div class="table-operator" v-if="hasPerm('sysTimers:add')" >
      <a-button type="primary" v-if="hasPerm('sysTimers:add')" icon="plus" @click="$refs.addForm.add()">新增定时器</a-button>
    </div>
    <s-table
      ref="table"
      size="default"
      :columns="columns"
      :data="loadData"
      :alert="true"
      :rowKey="(record) => record.id"
      :rowSelection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
    >
      <span slot="actionClass" slot-scope="text">
        <ellipsis :length="10" tooltip>{{ text }}</ellipsis>
      </span>
      <span slot="remark" slot-scope="text">
        <ellipsis :length="10" tooltip>{{ text }}</ellipsis>
      </span>
      <span slot="jobStatus" slot-scope="text,record" v-if="hasPerm('sysTimers:start') || hasPerm('sysTimers:stop')">
        <a-popconfirm placement="top" :title="text===1? '确定停止该任务？':'确定启动该任务？'" @confirm="() => editjobStatusStatus(text,record)">
          <a-badge :status="text===1? 'processing':'default'" />
          <a>{{ jobStatusFilter(text) }}</a>
        </a-popconfirm>
      </span>
      <span slot="jobStatus" v-else>
        <a-badge :status="text===1? 'processing':'default'" />
        {{ jobStatusFilter(text) }}
      </span>
      <span slot="action" slot-scope="text, record">
        <a v-if="hasPerm('sysTimers:edit')" @click="$refs.editForm.edit(record)">编辑</a>
        <a-divider type="vertical" v-if="hasPerm('sysTimers:edit') & hasPerm('sysTimers:delete')"/>
        <a-popconfirm v-if="hasPerm('sysTimers:delete')" placement="topRight" title="确认删除？" @confirm="() => sysTimersDelete(record)">
          <a>删除</a>
        </a-popconfirm>
      </span>
    </s-table>
    <add-form ref="addForm" @ok="handleOk" />
    <edit-form ref="editForm" @ok="handleOk" />
  </a-card>
</template>
<script>
  import { STable, Ellipsis } from '@/components'
  import { sysTimersPage, sysTimersDelete, sysTimersStart, sysTimersStop } from '@/api/modular/system/timersManage'
  import addForm from './addForm'
  import editForm from './editForm'
  import { sysDictTypeDropDown } from '@/api/modular/system/dictManage'
  export default {
    name: 'PosIndex',
    components: {
      STable,
      Ellipsis,
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
            title: '任务名称',
            dataIndex: 'timerName'
          },
          {
            title: '任务class类名',
            dataIndex: 'actionClass',
            scopedSlots: { customRender: 'actionClass' }
          },
          {
            title: '定时任务表达式',
            dataIndex: 'cron'
          },
          {
            title: '备注',
            dataIndex: 'remark',
            scopedSlots: { customRender: 'remark' }
          },
          {
            title: '状态',
            dataIndex: 'jobStatus',
            scopedSlots: { customRender: 'jobStatus' }
          }
        ],
        // 加载数据方法 必须为 Promise 对象
        loadData: parameter => {
          return sysTimersPage(Object.assign(parameter, this.queryParam)).then((res) => {
            return res.data
          })
        },
        selectedRowKeys: [],
        selectedRows: [],
        jobStatusDictTypeDropDown: []
      }
    },
    created () {
      this.sysDictTypeDropDown()// 注释掉
      if (this.hasPerm('sysTimers:edit') || this.hasPerm('sysTimers:delete')) {
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
        sysDictTypeDropDown({ code: 'run_status' }).then((res) => {
          this.jobStatusDictTypeDropDown = res.data
        })
      },
      jobStatusFilter (jobStatus) {
        // eslint-disable-next-line eqeqeq
        const values = this.jobStatusDictTypeDropDown.filter(item => item.code == jobStatus)
        if (values.length > 0) {
          return values[0].value
        }
      },
      /**
       * 启动停止
       */
      editjobStatusStatus (code, record) {
        // eslint-disable-next-line eqeqeq
        if (code == 1) {
          sysTimersStop({ id: record.id }).then(res => {
            if (res.success) {
              this.$message.success('停止成功')
              this.$refs.table.refresh()
            } else {
              this.$message.error('停止失败：' + res.message)
            }
          })
        // eslint-disable-next-line eqeqeq
        } else if (code == 2) {
          sysTimersStart({ id: record.id }).then(res => {
            if (res.success) {
              this.$message.success('启动成功')
              this.$refs.table.refresh()
            } else {
              this.$message.error('启动失败：' + res.message)
            }
          })
        }
      },
      sysTimersDelete (record) {
        sysTimersDelete(record).then((res) => {
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
