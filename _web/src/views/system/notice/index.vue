<template>
  <div>
    <x-card v-if="hasPerm('sysNotice:page')">
      <div slot="content" class="table-page-search-wrapper">
        <a-form layout="inline">
          <a-row :gutter="48">
            <a-col :md="8" :sm="24">
              <a-form-item label="关键词" >
                <a-input v-model="queryParam.searchValue" allow-clear placeholder="请输入标题、内容"/>
              </a-form-item>
            </a-col>
            <a-col :md="8" :sm="24">
              <a-form-item label="类型" >
                <a-select v-model="queryParam.type" placeholder="请选择类型" allow-clear >
                  <a-select-option v-for="(item,index) in typeDictTypeDropDown" :key="index" :value="item.code" >{{ item.name }}</a-select-option>
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
    </x-card>
    <a-card :bordered="false">
      <s-table
        ref="table"
        :columns="columns"
        :data="loadData"
        :alert="false"
        :rowKey="(record) => record.id"
        :rowSelection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
      >
        <template slot="operator" v-if="hasPerm('sysNotice:add')">
          <a-button @click="$refs.addForm.add()" icon="plus" type="primary" v-if="hasPerm('sysNotice:add')" >新增公告</a-button>
        </template>
        <span slot="status" slot-scope="text">
          {{ 'notice_status' | dictType(text) }}
        </span>
        <span slot="type" slot-scope="text">
          {{ 'notice_type' | dictType(text) }}
        </span>
        <span slot="action" slot-scope="text, record">
          <div v-if="record.status == 0">
            <a v-if="hasPerm('sysNotice:detail')" @click="$refs.detailForm.detail(record)">查看</a>
            <a-divider type="vertical" v-if="hasPerm('sysNotice:detail') & hasPerm('sysNotice:edit')"/>
            <a v-if="hasPerm('sysNotice:edit')" @click="$refs.editForm.edit(record)">编辑</a>
            <a-divider type="vertical" v-if="hasPerm('sysNotice:edit') & hasPerm('sysNotice:changeStatus')"/>
            <a-popconfirm v-if="hasPerm('sysNotice:changeStatus')" placement="topRight" title="确认发布该信息？" @confirm="() => editNoticeStatus(1,record)">
              <a>发布</a>
            </a-popconfirm>
          </div>
          <div v-if="record.status == 1">
            <a v-if="hasPerm('sysNotice:detail')" @click="$refs.detailForm.detail(record)">查看</a>
            <a-divider type="vertical" v-if="hasPerm('sysNotice:detail') & hasPerm('sysNotice:changeStatus')"/>
            <a-popconfirm v-if="hasPerm('sysNotice:changeStatus')" placement="topRight" title="确认撤回该信息？" @confirm="() => editNoticeStatus(2,record)">
              <a>撤回</a>
            </a-popconfirm>
          </div>
          <div v-if="record.status == 2">
            <a v-if="hasPerm('sysNotice:detail')" @click="$refs.detailForm.detail(record)">查看</a>
            <a-divider type="vertical" v-if="hasPerm('sysNotice:detail') & hasPerm('sysNotice:delete')"/>
            <a-popconfirm v-if="hasPerm('sysNotice:delete')" placement="topRight" title="确认删除？" @confirm="() => sysNoticeDelete(record)">
              <a>删除</a>
            </a-popconfirm>
          </div>
        </span>
      </s-table>
      <add-form ref="addForm" @ok="handleOk" v-if="hasPerm('sysNotice:add')"/>
      <edit-form ref="editForm" @ok="handleOk" v-if="hasPerm('sysNotice:edit')"/>
      <detail-form ref="detailForm" @ok="handleOk" v-if="hasPerm('sysNotice:detail')"/>
      <div ref="editor"></div>
    </a-card>
  </div>
</template>
<script>
  import { STable, XCard } from '@/components'
  import { sysNoticePage, sysNoticeDelete, sysNoticeChangeStatus } from '@/api/modular/system/noticeManage'
  import addForm from './addForm'
  import editForm from './editForm'
  import detailForm from './detailForm'
  export default {
    components: {
      XCard,
      STable,
      addForm,
      editForm,
      detailForm
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
            title: '标题',
            dataIndex: 'title'
          },
          {
            title: '类型',
            dataIndex: 'type',
            scopedSlots: { customRender: 'type' }
          },
          {
            title: '状态',
            dataIndex: 'status',
            scopedSlots: { customRender: 'status' }
          }
        ],
        // 加载数据方法 必须为 Promise 对象
        loadData: parameter => {
          return sysNoticePage(Object.assign(parameter, this.queryParam)).then((res) => {
            return res.data
          })
        },
        selectedRowKeys: [],
        selectedRows: [],
        statusDictTypeDropDown: [], // 0草稿 1发布 2撤回 3删除
        typeDictTypeDropDown: []// 0通知 1公告
      }
    },
    created () {
      this.sysDictTypeDropDown()// 先注释
      if (this.hasPerm('sysNotice:changeStatus') || this.hasPerm('sysNotice:edit') || this.hasPerm('sysNotice:delete')) {
        this.columns.push({
          title: '操作',
          width: '300px',
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
        this.statusDictTypeDropDown = this.$options.filters['dictData']('notice_status')
        this.typeDictTypeDropDown = this.$options.filters['dictData']('notice_type')
      },
      /**
       * 修改状态
       */
      editNoticeStatus (code, record) {
        sysNoticeChangeStatus({ id: record.id, status: code.toString() }).then(res => {
          if (res.success) {
            this.$message.success('操作成功')
            this.$refs.table.refresh()
          } else {
            this.$message.error('操作失败：' + res.message)
          }
        })
      },
      /**
       * 提交
       */
      sysNoticeDelete (record) {
        sysNoticeDelete(record).then((res) => {
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
