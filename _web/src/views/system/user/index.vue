<template>

  <a-row :gutter="24" >
    <a-col :md="5" :sm="24">
      <a-card :bordered="false" :loading="treeLoading">
        <div v-if="this.orgTree != ''">
          <a-tree
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

        <div class="table-page-search-wrapper" v-if="hasPerm('sysUser:page')">
          <a-form layout="inline">
            <a-row :gutter="48">
              <a-col :md="8" :sm="24">
                <a-form-item label="关键词" >
                  <a-input v-model="queryParam.searchValue" allow-clear placeholder="请输入姓名、账号、手机号"/>
                </a-form-item>
              </a-col>
              <a-col :md="8" :sm="24">
                <a-form-item label="状态">
                  <a-select v-model="queryParam.searchStatus" allow-clear placeholder="请选择状态" default-value="0">
                    <a-select-option v-for="(item,index) in statusDictTypeDropDown" :key="index" :value="item.code" >{{ item.value }}</a-select-option>
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

        <div class="table-operator" v-if="hasPerm('sysUser:add')" >
          <a-button type="primary" v-if="hasPerm('sysUser:add')" icon="plus" @click="$refs.addForm.add()">新增用户</a-button>
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
          <span slot="sex" slot-scope="text">
            {{ sexFilter(text) }}
          </span>

          <span slot="status" slot-scope="text,record" v-if="hasPerm('sysUser:changeStatus')">
            <a-popconfirm placement="top" :title="text===0? '确定停用该用户？':'确定启用该用户？'" @confirm="() => editUserStatus(text,record)">
              <a>{{ statusFilter(text) }}</a>
            </a-popconfirm>
          </span>
          <span slot="status" v-else>
            {{ statusFilter(text) }}
          </span>

          <span slot="action" slot-scope="text, record">
            <a v-if="hasPerm('sysUser:edit')" @click="$refs.editForm.edit(record)">编辑</a>
            <a-divider type="vertical" v-if="hasPerm('sysUser:edit')" />
            <a-dropdown v-if="hasPerm('sysUser:resetPwd') || hasPerm('sysUser:grantRole') || hasPerm('sysUser:grantData') || hasPerm('sysUser:delete')">
              <a class="ant-dropdown-link">
                更多 <a-icon type="down" />
              </a>
              <a-menu slot="overlay">
                <a-menu-item v-if="hasPerm('sysUser:resetPwd')">
                  <a-popconfirm placement="topRight" title="确认重置密码？" @confirm="() => resetPwd(record)">
                    <a>重置密码</a>
                  </a-popconfirm>
                </a-menu-item>
                <a-menu-item v-if="hasPerm('sysUser:grantRole')">
                  <a @click="$refs.userRoleForm.userRole(record)">授权角色</a>
                </a-menu-item>
                <a-menu-item v-if="hasPerm('sysUser:grantData')">
                  <a @click="$refs.userOrgForm.userOrg(record)">授权数据</a>
                </a-menu-item>
                <a-menu-item v-if="hasPerm('sysUser:delete')">
                  <a-popconfirm placement="topRight" title="确认删除？" @confirm="() => sysUserDelete(record)">
                    <a>删除</a>
                  </a-popconfirm>
                </a-menu-item>
              </a-menu>
            </a-dropdown>
          </span>

        </s-table>

        <add-form ref="addForm" @ok="handleOk" />
        <edit-form ref="editForm" @ok="handleOk" />
        <user-role-form ref="userRoleForm" @ok="handleOk"/>
        <user-org-form ref="userOrgForm" @ok="handleOk"/>

      </a-card>
    </a-col>
  </a-row>

</template>

<script>
  import { STable } from '@/components'
  import { Empty } from 'ant-design-vue'
  import { getOrgTree } from '@/api/modular/system/orgManage'
  import { getUserPage, sysUserDelete, sysUserChangeStatus, sysUserResetPwd } from '@/api/modular/system/userManage'
  import { sysDictTypeDropDown } from '@/api/modular/system/dictManage'
  import addForm from './addForm'
  import editForm from './editForm'
  import userRoleForm from './userRoleForm'
  import userOrgForm from './userOrgForm'

  export default {
    components: {
      STable,
      addForm,
      editForm,
      userRoleForm,
      userOrgForm
      // sysDictTypeDropDown,
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
            title: '账号',
            dataIndex: 'account'
          },
          {
            title: '姓名',
            dataIndex: 'name'
          },
          {
            title: '性别',
            dataIndex: 'sex',
            scopedSlots: { customRender: 'sex' }
          }, {
            title: '手机',
            dataIndex: 'phone'
          },
          {
            title: '状态',
            dataIndex: 'status',
            scopedSlots: { customRender: 'status' }
          }
        ],
        // 加载数据方法 必须为 Promise 对象
        loadData: parameter => {
          return getUserPage(Object.assign(parameter, this.queryParam)).then((res) => {
            return res.data
          })
        },
        orgTree: [],
        selectedRowKeys: [],
        selectedRows: [],
        defaultExpandedKeys: [],
        sexDictTypeDropDown: [],
        statusDictTypeDropDown: [],
        treeLoading: true,
        simpleImage: Empty.PRESENTED_IMAGE_SIMPLE,
        replaceFields: {
          key: 'id'
        }

    }
    },
    created () {
      /**
       * 获取到机构树，展开顶级下树节点，考虑到后期数据量变大，不建议全部展开
       */
      getOrgTree(Object.assign(this.queryParam)).then(res => {
        this.treeLoading = false
        if (!res.success) {
          return
        }
        this.orgTree = res.data
        for (var item of res.data) {
          // eslint-disable-next-line eqeqeq
          if (item.parentId == 0) {
            this.defaultExpandedKeys.push(item.id)
          }
        }
      })
      this.sysDictTypeDropDown()
      if (this.hasPerm('sysUser:edit') || this.hasPerm('sysUser:resetPwd') || this.hasPerm('sysUser:grantRole') || this.hasPerm('sysUser:grantData') || this.hasPerm('sysUser:delete')) {
        this.columns.push({
          title: '操作',
          width: '150px',
          dataIndex: 'action',
          scopedSlots: { customRender: 'action' }
        })
      }
    },

    methods: {

      sexFilter (sex) {
        // eslint-disable-next-line eqeqeq
        const values = this.sexDictTypeDropDown.filter(item => item.code == sex)
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
      sysDictTypeDropDown (text) {
         sysDictTypeDropDown({ code: 'sex' }).then((res) => {
           this.sexDictTypeDropDown = res.data
        })
        sysDictTypeDropDown({ code: 'common_status' }).then((res) => {
          this.statusDictTypeDropDown = res.data
        })
      },

      /**
       * 修改用户状态
       */
      editUserStatus (code, record) {
        // eslint-disable-next-line no-unused-vars
        const status = 0
        // eslint-disable-next-line eqeqeq
        if (code == 0) {
          this.status = 1
        // eslint-disable-next-line eqeqeq
        } else if (code == 1) {
          this.status = 0
        }
        sysUserChangeStatus({ id: record.id, status: this.status }).then(res => {
          if (res.success) {
            this.$message.success('操作成功')
            this.$refs.table.refresh()
          } else {
            this.$message.error('操作失败：' + res.message)
          }
        })
      },

      /**
       * 重置密码
       */
      resetPwd (record) {
        sysUserResetPwd({ id: record.id }).then(res => {
          if (res.success) {
            this.$message.success('重置成功')
            // this.$refs.table.refresh()
          } else {
            this.$message.error('重置失败：' + res.message)
          }
        })
      },

      /**
       * 删除用户
       * @param record
       */
      sysUserDelete (record) {
        sysUserDelete(record).then((res) => {
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

      /**
       * 点击左侧机构树查询列表
       */
      handleClick (e) {
        this.queryParam = {
          'sysEmpParam.orgId': e.toString()
        }
        this.$refs.table.refresh(true)
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
