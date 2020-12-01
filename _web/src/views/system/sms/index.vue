<template>
  <a-card :bordered="false">

    <div class="table-page-search-wrapper" v-if="hasPerm('sysSms:page')">
      <a-form layout="inline">
        <a-row :gutter="48">
          <a-col :md="8" :sm="24">
            <a-form-item label="手机号">
              <a-input v-model="queryParam.phoneNumbers" placeholder="请输入手机号"/>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item label="发送状态">
              <a-select v-model="queryParam.status" placeholder="请选择发送状态" >
                <a-select-option v-for="(item,index) in statusDictTypeDropDown" :key="index" :value="item.code" >{{ item.value }}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <template v-if="advanced">
            <a-col :md="8" :sm="24">
              <a-form-item label="来源">
                <a-select v-model="queryParam.source" placeholder="请选择来源" >
                  <a-select-option v-for="(item,index) in sourceDictTypeDropDown" :key="index" :value="item.code" >{{ item.value }}</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
          </template>
          <a-col :md="!advanced && 8 || 24" :sm="24">
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
      <span slot="source" slot-scope="text">
        {{ sourceFilter(text) }}
      </span>
    </s-table>

  </a-card>
</template>

<script>
  import { STable } from '@/components'
  import { smsPage } from '@/api/modular/system/smsManage'
  import { sysDictTypeDropDown } from '@/api/modular/system/dictManage'

  export default {
    components: {
      STable
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
            title: '手机号',
            dataIndex: 'phoneNumbers'
          },
          {
            title: '短信验证码',
            dataIndex: 'validateCode'
          },
          {
            title: '短信模板ID',
            dataIndex: 'templateCode'
          },
          {
            title: '发送状态',
            dataIndex: 'status',
            scopedSlots: { customRender: 'status' }
          },
          {
            title: '来源',
            dataIndex: 'source',
            scopedSlots: { customRender: 'source' }
          },
          {
            title: '失效时间',
            dataIndex: 'invalidTime'
          }
        ],
        // 加载数据方法 必须为 Promise 对象
        loadData: parameter => {
          return smsPage(Object.assign(parameter, this.queryParam)).then((res) => {
            return res.data
          })
        },
        selectedRowKeys: [],
        selectedRows: [],
        statusDictTypeDropDown: [],
        sourceDictTypeDropDown: []
    }
    },

    created () {
      this.sysDictTypeDropDown()
    },
    methods: {

      sourceFilter (source) {
        // eslint-disable-next-line eqeqeq
        const values = this.sourceDictTypeDropDown.filter(item => item.code == source)
        if (values.length > 0) {
          return values[0].value
        }
      },

      statusFilter (status) {
        // eslint-disable-next-line eqeqeq
        const values = this.statusDictTypeDropDown.filter(item => item.code == status)
        if (values.length > 0) {
          return values[0].value
        }
      },

      /**
       * 获取字典数据
       */
      sysDictTypeDropDown () {
        sysDictTypeDropDown({ code: 'send_type' }).then((res) => {
          this.statusDictTypeDropDown = res.data
        })
        sysDictTypeDropDown({ code: 'sms_send_source' }).then((res) => {
          this.sourceDictTypeDropDown = res.data
        })
      },

      toggleAdvanced () {
        this.advanced = !this.advanced
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
