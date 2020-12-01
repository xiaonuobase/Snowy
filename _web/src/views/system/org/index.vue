<template>

  <a-row :gutter="24" >
    <a-col :md="5" :sm="24" >
      <a-card :bordered="false" :loading="treeLoading">
        <div v-if="this.orgTree!='' ">
          <a-tree
            style="scroll:true"
            :treeData="orgTree"
            v-if="orgTree.length"
            @select="handleClick"
            :defaultExpandAll="true"
            :defaultExpandedKeys="defaultExpandedKeys"
            :replaceFields="replaceFields" />
        </div>
        <div v-else>
          <a-empty :image="simpleImage" />
        </div>
      </a-card>
    </a-col>

    <a-col :md="19" :sm="24">
      <a-card :bordered="false">
        <div class="table-page-search-wrapper" v-if="hasPerm('sysOrg:page')">
          <a-form layout="inline">
            <a-row :gutter="48">
              <a-col :md="8" :sm="24">
                <a-form-item label="机构名称" >
                  <a-input v-model="queryParam.name" allow-clear placeholder="请输入机构名称"/>
                </a-form-item>
              </a-col>
              <a-col :md="8" :sm="24">
                <a-button type="primary" @click="$refs.table.refresh(true)">查询</a-button>
                <a-button style="margin-left: 8px" @click="() => queryParam = {}">重置</a-button>
              </a-col>
              <a-col :md="8" :sm="24">

              </a-col>

            </a-row>
          </a-form>
        </div>

        <div class="table-operator" v-if="hasPerm('sysOrg:add')" >
          <a-button type="primary" v-if="hasPerm('sysOrg:add')" icon="plus" @click="$refs.addForm.add()">新增机构</a-button>
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
          <span slot="action" slot-scope="text, record">
            <a v-if="hasPerm('sysOrg:edit')" @click="$refs.editForm.edit(record)">编辑</a>
            <a-divider type="vertical" v-if="hasPerm('sysOrg:edit') & hasPerm('sysOrg:delete')"/>
            <a-popconfirm v-if="hasPerm('sysOrg:delete')" placement="topRight" title="确认删除？" @confirm="() => sysOrgDelete(record)">
              <a>删除</a>
            </a-popconfirm>

          </span>

        </s-table>

        <add-form ref="addForm" @ok="handleOk" />
        <edit-form ref="editForm" @ok="handleOk" />

      </a-card>
    </a-col>
  </a-row>

</template>

<script>
  import { STable } from '@/components'
  import { Empty } from 'ant-design-vue'
  import { getOrgPage, sysOrgDelete, getOrgTree } from '@/api/modular/system/orgManage'
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
            title: '机构名称',
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
          return getOrgPage(Object.assign(parameter, this.queryParam)).then((res) => {
            return res.data
          })
        },
        orgTree: [],
        selectedRowKeys: [],
        selectedRows: [],
        defaultExpandedKeys: [],
        // 搜索的三个参数
        expandedKeys: [],
        searchValue: '',
        autoExpandParent: true,
        treeLoading: true,
        simpleImage: Empty.PRESENTED_IMAGE_SIMPLE,
        replaceFields: {
          key: 'id'
        }
    }
    },
    created () {
      this.getOrgTree()
      if (this.hasPerm('sysOrg:edit') || this.hasPerm('sysOrg:delete')) {
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
       * 获取到机构树，展开顶级下树节点，考虑到后期数据量变大，不建议全部展开
       */
      getOrgTree () {
        getOrgTree(Object.assign(this.queryParam)).then(res => {
          this.treeLoading = false
          if (!res.success) {
            return
          }
          this.orgTree = res.data
          this.queryParam.parentId = this.orgTree[0].id
          // 全部展开，上面api方法提供的不生效，先用此方法
          for (var item of res.data) {
            // eslint-disable-next-line eqeqeq
            if (item.parentId == 0) {
              this.defaultExpandedKeys.push(item.id)
            }
          }
          this.$refs.table.refresh()
        })
      },
      /**
       * 删除
       * @param record
       */
      sysOrgDelete (record) {
        sysOrgDelete(record).then((res) => {
          if (res.success) {
            this.$message.success('删除成功')
            this.getOrgTree()
            this.$refs.table.refresh()
          } else {
            this.$message.error('删除失败：' + res.message)
          }
        }).catch((err) => {
          this.$message.error('删除错误：' + err.message)
        })
      },

      handleClick (e) {
        this.queryParam = {
          pid: e.toString()
        }
        this.$refs.table.refresh(true)
      },
      toggleAdvanced () {
        this.advanced = !this.advanced
      },
      handleOk () {
        this.getOrgTree()
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
