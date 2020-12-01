<template>
  <a-card :bordered="false">

    <div class="table-page-search-wrapper" v-if="hasPerm('sysNotice:received')">
      <a-form layout="inline">
        <a-row :gutter="48">
          <a-col :md="8" :sm="24">
            <a-form-item label="关键词" v-if="hasPerm('sysNotice:received')">
              <a-input v-model="queryParam.searchValue" allow-clear placeholder="请输入标题、内容"/>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item label="类型" v-if="hasPerm('sysNotice:received')">
              <a-select v-model="queryParam.type" placeholder="请选择类型" allow-clear >
                <a-select-option v-for="(item,index) in typeDictTypeDropDown" :key="index" :value="item.code" >{{ item.value }}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="!advanced && 8 || 24" :sm="24">
            <span class="table-page-search-submitButtons" >
              <a-button type="primary" @click="$refs.table.refresh(true)">查询</a-button>
              <a-button style="margin-left: 8px" @click="() => queryParam = {}">重置</a-button>
            </span>
          </a-col>
        </a-row>

      </a-form>
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

      <span slot="status" slot-scope="text">
        {{ statusFilter(text) }}
      </span>

      <span slot="type" slot-scope="text">
        {{ typeFilter(text) }}
      </span>

      <span slot="action" slot-scope="text, record">
        <a v-if="hasPerm('sysNotice:received')" @click="$refs.detailForm.detail(record)">查看</a>
      </span>

    </s-table>

    <detail-form ref="detailForm" @ok="handleOk" />
    <div ref="editor"></div>
  </a-card>
</template>

<script>
  import { STable } from '@/components'
  // eslint-disable-next-line no-unused-vars
  import { sysNoticePage } from '@/api/modular/system/noticeManage'
  import { sysNoticeReceived } from '@/api/modular/system/noticeReceivedManage'
  import { sysDictTypeDropDown } from '@/api/modular/system/dictManage'
  import detailForm from './detailForm'

  export default {
    components: {
      STable,
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
          return sysNoticeReceived(Object.assign(parameter, this.queryParam)).then((res) => {
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
      if (this.hasPerm('sysNotice:received')) {
        this.columns.push({
          title: '操作',
          width: '200px',
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
        sysDictTypeDropDown({ code: 'notice_status' }).then((res) => {
          this.statusDictTypeDropDown = res.data
        })
        sysDictTypeDropDown({ code: 'notice_type' }).then((res) => {
          this.typeDictTypeDropDown = res.data
        })
      },

      statusFilter (status) {
        // eslint-disable-next-line eqeqeq
        const values = this.statusDictTypeDropDown.filter(item => item.code == status)
        if (values.length > 0) {
          return values[0].value
        }
      },
      typeFilter (type) {
        // eslint-disable-next-line eqeqeq
        const values = this.typeDictTypeDropDown.filter(item => item.code == type)
        if (values.length > 0) {
          return values[0].value
        }
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
