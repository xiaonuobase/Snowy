/* eslint-disable */
<template>

  <a-card :bordered="false">

    <a-table
      ref="table"
      size="middle"
      :rowKey="(record) => record.id"
      :pagination="false"
      :defaultExpandAllRows="true"
      :columns="columns"
      :dataSource="data"
      :loading="loading"
      showPagination="auto"
      @expand="onExpand">
    </a-table>

  </a-card>

</template>

<script>
  import { getAreaList } from '@/api/modular/system/areaManage'

  export default {

    data () {
      return {
        queryParam: {},
        data: [],
        loading: true,
        // 定义展开过的节点的id数组
        expandedData: [],
        columns: [
          {
            title: '名称',
            dataIndex: 'name'
          },
          {
            title: '层级',
            dataIndex: 'levelCode'
          },
          {
            title: '简称',
            dataIndex: 'shortName'
          },
          {
            title: '组合名',
            dataIndex: 'mergerName'
          },
          {
            title: '拼音',
            dataIndex: 'pinyin'
          },
          {
            title: '邮编',
            dataIndex: 'zipCode'
          },
          {
            title: '经度',
            dataIndex: 'lng'
          },
          {
            title: '纬度',
            dataIndex: 'lat'
          }
        ],
        selectedRowKeys: []
      }
    },

    created () {
      this.loadData()
    },

    methods: {
      loadData () {
        this.loading = true
        getAreaList(this.queryParam).then((res) => {
          if (res.success) {
            this.data = res.data
            this.removeEmptyChildren(this.data)
          }
        }).finally(() => {
          this.loading = false
        })
      },

      removeEmptyChildren(data) {
        if (data == null || data.length === 0) return
        for (let i = 0; i < data.length; i++) {
          const item = data[i]
          // 如果为最终子节点或其为“市辖区”，则其没有子节点
          if (item.levelCode === 4 || (item.levelCode === 2 && item.name === '市辖区')) {
             item.children = null
          }
        }
      },
      onSelectChange (selectedRowKeys) {
        this.selectedRowKeys = selectedRowKeys
      },
      onExpand(expanded, record) {
        if (expanded) {
          // 判断其是否已经展开过
          const index = this.expandedData.indexOf(record.id)
          // 如果没展开过，则请求接口
          if (index === -1) {
            this.queryParam.parentCode = record.areaCode
            getAreaList(this.queryParam).then((res) => {
              if (res.success) {
                // 设置为其子节点
                record.children = res.data
                this.removeEmptyChildren(record.children)
                // 将其放入展开过的id集合
                this.expandedData.push(record.id)
              }
            }).finally(() => {
              this.loading = false
            })
          }
        }
      }
    }
  }

</script>
<style scoped>

</style>
