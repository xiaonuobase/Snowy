/* eslint-disable */
<template>
  <div>
    <x-card v-if="hasPerm('sysMenu:list')">
      <div slot="content" class="table-page-search-wrapper">
        <a-form layout="inline">
          <a-row :gutter="48">
            <a-col :md="8" :sm="24">
              <a-form-item label="菜单名称">
                <a-input v-model="queryParam.name" allow-clear placeholder="请输入菜单名称"/>
              </a-form-item>
            </a-col>
            <a-col :md="8" :sm="24">
              <a-form-item label="选择应用">
                <a-select v-model="queryParam.application" placeholder="请选择选择应用" allow-clear>
                  <a-select-option v-for="(item,index) in this.userInfo.apps" :key="index" :value="item.code" >{{ item.name }}</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :md="8" :sm="24">
              <span class="table-page-search-submitButtons">
                <a-button type="primary" @click="$refs.table.refresh(true)">查询</a-button>
                <a-button style="margin-left: 8px" @click="() => queryParam = {}">重置</a-button>
              </span>
            </a-col>
          </a-row>
        </a-form>
      </div>
    </x-card>
    <a-card :bordered="false">
      <s-table
        ref="table"
        :rowKey="(record) => record.id"
        :columns="columns"
        :alert="false"
        :data="loadData"
        :showPagination="false"
        :expandRowByClick="true"
      >
        <template slot="operator">
          <a-button type="primary" v-if="hasPerm('sysMenu:add')" icon="plus" @click="$refs.addForm.add()">新增菜单</a-button>
        </template>
        <span slot="type" slot-scope="text">
          <a-tag color="cyan" v-if="text === 0">
            {{ 'menu_type' | dictType(text) }}
          </a-tag>
          <a-tag color="blue" v-if="text === 1">
            {{ 'menu_type' | dictType(text) }}
          </a-tag>
          <a-tag color="purple" v-if="text === 2">
            {{ 'menu_type' | dictType(text) }}
          </a-tag>
        </span>
        <span slot="icon" slot-scope="text">
          <div v-if="text != null && text != ''">
            <a-icon :type="text"/>
          </div>
        </span>
        <span slot="action" slot-scope="text, record">
          <template>
            <a v-if="hasPerm('sysMenu:edit')" @click="$refs.editForm.edit(record)">编辑</a>
            <a-divider type="vertical" v-if="hasPerm('sysMenu:edit') & hasPerm('sysMenu:delete')"/>
            <a-popconfirm v-if="hasPerm('sysMenu:delete')" placement="topRight" title="删除本菜单与下级？" @confirm="() => handleDel(record)">
              <a>删除</a>
            </a-popconfirm>
          </template>
        </span>
      </s-table>
      <add-form ref="addForm" @ok="handleOk"/>
      <edit-form ref="editForm" @ok="handleOk"/>
    </a-card>
  </div>
</template>

<script>
import { STable, XCard } from '@/components'
import { getMenuList, sysMenuDelete } from '@/api/modular/system/menuManage'
import addForm from './addForm'
import editForm from './editForm'
import { mapGetters } from 'vuex'

export default {
  components: {
    STable,
    XCard,
    addForm,
    editForm
  },
  data () {
    return {
      data: [],
      queryParam: {},
      loading: true,
      columns: [
        {
          title: '菜单名称',
          dataIndex: 'name',
          width: '20%'
        },
        {
          title: '菜单类型',
          dataIndex: 'type',
          scopedSlots: { customRender: 'type' }
        },
        {
          title: '图标',
          dataIndex: 'icon',
          scopedSlots: { customRender: 'icon' }
        },
        {
          title: '组件',
          dataIndex: 'component',
          width: '20%',
          ellipsis: true
        },
        {
          title: '路由地址',
          dataIndex: 'router',
          key: 'router',
          ellipsis: true
        },
        {
          title: '排序',
          dataIndex: 'sort'
        }
      ],
      loadData: parameter => {
        return getMenuList(Object.assign(parameter, this.queryParam)).then((res) => {
          this.removeEmptyChildren(res.data)
          return res.data
        })
      }
    }
  },
  computed: {
    ...mapGetters(['userInfo'])
  },
  created () {
    if (this.hasPerm('sysMenu:edit') || this.hasPerm('sysMenu:delete')) {
      this.columns.push({
        title: '操作',
        dataIndex: 'action',
        width: '150px',
        scopedSlots: { customRender: 'action' }
      })
    }
  },
  methods: {
    /**
     * 去掉无用的支节点
     */
    removeEmptyChildren(data) {
      if (data == null || data.length === 0) return
      for (let i = 0; i < data.length; i++) {
        const item = data[i]
        if (item.children != null && item.children.length === 0) {
          item.children = null
        } else {
          this.removeEmptyChildren(item.children)
        }
      }
    },
    handleDel (record) {
      sysMenuDelete(record).then((res) => {
        if (res.success) {
          this.$message.success('删除成功')
          this.$refs.table.refresh()
        } else {
          this.$message.error('删除失败：' + res.message)
        }
      })
    },
    handleOk () {
      this.$refs.table.refresh()
    }
  }
}

</script>
<style scoped>
.table-operator {
  margin-bottom: 18px;
}
button {
  margin-right: 8px;
}
</style>
