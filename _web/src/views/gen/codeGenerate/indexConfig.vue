<template>
  <a-card :bordered="false" v-show="indexConfigShow">
    <div class="table-operator">
      <a-button class="but_item" type="dashed" @click="handleCancel" icon="rollback">返回</a-button>
      <a-button type="primary" icon="plus" @click="handleSubmit">保存</a-button>
    </div>
    <a-table
      ref="table"
      size="middle"
      :columns="columns"
      :dataSource="loadData"
      :pagination="false"
      :alert="true"
      :loading="tableLoading"
      :rowKey="(record) => record.id"
    >
      <template slot="columnComment" slot-scope="text, record">
        <a-input v-model="record.columnComment" />
      </template>
      <template slot="javaType" slot-scope="text, record">
        <a-select style="width: 120px" v-model="record.javaType" :disabled="judgeColumns(record)">
          <a-select-option v-for="(item,index) in javaTypeData" :key="index" :value="item.code">{{ item.name }}</a-select-option>
        </a-select>
      </template>
      <template slot="effectType" slot-scope="text, record">
        <a-select style="width: 120px" v-model="record.effectType" :disabled="judgeColumns(record)">
          <a-select-option v-for="(item,index) in effectTypeData" :key="index" :value="item.code">{{ item.name }}</a-select-option>
        </a-select>
      </template>
      <template slot="dictTypeCode" slot-scope="text, record">
        <a-select
          style="width: 120px"
          v-model="record.dictTypeCode"
          :disabled="record.effectType !== 'radio' &&
            record.effectType !== 'select' && record.effectType !== 'checkbox'">
          <a-select-option v-for="(item,index) in dictDataAll" :key="index" :value="item.code">{{ item.name }}</a-select-option>
        </a-select>
      </template>
      <template slot="whetherTable" slot-scope="text, record">
        <a-checkbox v-model="record.whetherTable"/>
      </template>
      <template slot="whetherRetract" slot-scope="text, record">
        <a-checkbox v-model="record.whetherRetract"/>
      </template>
      <template slot="whetherAddUpdate" slot-scope="text, record">
        <a-checkbox v-model="record.whetherAddUpdate" :disabled="judgeColumns(record)"/>
      </template>
      <template slot="whetherRequired" slot-scope="text, record">
        <a-checkbox v-model="record.whetherRequired" :disabled="judgeColumns(record)"/>
      </template>
      <template slot="queryWhether" slot-scope="text, record">
        <a-switch v-model="record.queryWhether">
          <a-icon slot="checkedChildren" type="check" />
          <a-icon slot="unCheckedChildren" type="close" />
        </a-switch>
      </template>
      <template slot="queryType" slot-scope="text, record">
        <a-select style="width: 100px" v-model="record.queryType" :disabled="!record.queryWhether">
          <a-select-option v-for="(item,index) in codeGenQueryTypeData" :key="index" :value="item.code">{{ item.name }}</a-select-option>
        </a-select>
      </template>
    </a-table>
  </a-card>
</template>
<script>
  import { sysCodeGenerateConfigList, sysCodeGenerateConfigEdit } from '@/api/modular/gen/sysCodeGenerateConfigManage'
  export default {
    data () {
      return {
        // 表头
        columns: [
          {
            title: 'java字段',
            dataIndex: 'javaName'
          },
          {
            title: '字段描述',
            dataIndex: 'columnComment',
            scopedSlots: { customRender: 'columnComment' }
          },
          {
            title: '物理类型',
            dataIndex: 'dataType'
          },
          {
            title: 'java类型',
            dataIndex: 'javaType',
            scopedSlots: { customRender: 'javaType' }
          },
          {
            title: '作用类型',
            dataIndex: 'effectType',
            scopedSlots: { customRender: 'effectType' }
          },
          {
            title: '字典',
            dataIndex: 'dictTypeCode',
            scopedSlots: { customRender: 'dictTypeCode' }
          },
          {
            title: '列表显示',
            align: 'center',
            dataIndex: 'whetherTable',
            scopedSlots: { customRender: 'whetherTable' }
          },
          {
            title: '列字段省略',
            align: 'center',
            dataIndex: 'whetherRetract',
            scopedSlots: { customRender: 'whetherRetract' }
          },
          {
            title: '增改',
            align: 'center',
            dataIndex: 'whetherAddUpdate',
            scopedSlots: { customRender: 'whetherAddUpdate' }
          },
          {
            title: '必填',
            align: 'center',
            dataIndex: 'whetherRequired',
            scopedSlots: { customRender: 'whetherRequired' }
          },
          {
            title: '是否是查询',
            align: 'center',
            dataIndex: 'queryWhether',
            scopedSlots: { customRender: 'queryWhether' }
          },
          {
            title: '查询方式',
            dataIndex: 'queryType',
            scopedSlots: { customRender: 'queryType' }
          }
        ],
        indexConfigShow: false,
        tableLoading: false,
        visible: false,
        loadData: [],
        javaTypeData: [],
        effectTypeData: [],
        dictDataAll: [],
        codeGenQueryTypeData: [],
        yesOrNoData: []
      }
    },
    methods: {
      /**
       * 打开界面
       */
      open (data) {
        this.indexConfigShow = true
        this.tableLoading = true
        const dictOption = this.$options
        this.javaTypeData = dictOption.filters['dictData']('code_gen_java_type')
        this.effectTypeData = dictOption.filters['dictData']('code_gen_effect_type')
        this.dictDataAll = dictOption.filters['dictDataAll']()
        this.yesOrNoData = dictOption.filters['dictData']('yes_or_no')
        this.codeGenQueryTypeData = dictOption.filters['dictData']('code_gen_query_type')
        const params = {
          codeGenId: data.id
        }
        sysCodeGenerateConfigList(params).then((res) => {
          this.loadData = res.data
          this.loadData.forEach(item => {
            for (const key in item) {
              if (item[key] === 'Y') {
                item[key] = true
              }
              if (item[key] === 'N') {
                item[key] = false
              }
            }
          })
          this.tableLoading = false
        })
      },
      /**
       * 提交表单
       */
      handleSubmit () {
        this.tableLoading = true
        // 做数组属性转换, 咱先来一个切断双向绑定，学习的童鞋下回记下啊
        // eslint-disable-next-line prefer-const
        let loadDatas = JSON.parse(JSON.stringify(this.loadData))
        loadDatas.forEach(item => {
          // 必填那一项转换
          for (const key in item) {
            if (item[key] === true) {
              item[key] = 'Y'
            }
            if (item[key] === false) {
              item[key] = 'N'
            }
          }
        })
        const param = {
          sysCodeGenerateConfigParamList: loadDatas
        }
        sysCodeGenerateConfigEdit(param).then((res) => {
          this.tableLoading = false
          if (res.success) {
            this.$message.success('编辑成功')
            this.handleCancel()
          } else {
            this.$message.error('编辑失败：' + res.message)
          }
        })
      },
      /**
       * 判断是否（用于是否能选择或输入等）
       */
      judgeColumns (data) {
        if (data.columnName.indexOf('create_user') > -1 ||
            data.columnName.indexOf('create_time') > -1 ||
            data.columnName.indexOf('update_user') > -1 ||
            data.columnName.indexOf('update_time') > -1 ||
            data.columnKey === 'PRI') {
          return true
        }
        return false
      },
      handleCancel () {
        this.$emit('ok')
        this.loadData = []
        this.indexConfigShow = false
      }
    }
  }
</script>
