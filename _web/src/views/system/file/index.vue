<template>
  <a-spin :spinning="cardLoading">
    <x-card v-if="hasPerm('sysFileInfo:page')">
      <div slot="content" class="table-page-search-wrapper">
        <a-form layout="inline">
          <a-row :gutter="48">
            <a-col :md="8" :sm="24">
              <a-form-item label="存储位置">
                <a-select v-model="queryParam.fileLocation" placeholder="请选择存储位置" >
                  <a-select-option v-for="(item,index) in fileLocationDictTypeDropDown" :key="index" :value="item.code" >{{ item.name }}</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :md="8" :sm="24">
              <a-form-item label="文件仓库">
                <a-input v-model="queryParam.fileBucket" placeholder="请输入文件仓库"/>
              </a-form-item>
            </a-col>
            <template v-if="advanced">
              <a-col :md="8" :sm="24">
                <a-form-item label="文件名称">
                  <a-input v-model="queryParam.fileOriginName" placeholder="请输入文件名称（上传时候的文件名）"/>
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
        <template slot="operator" v-if="hasPerm('sysFileInfo:upload')">
          <a-upload
            :customRequest="customRequest"
            :multiple="true"
            :showUploadList="false"
            name="file"
            v-if="hasPerm('sysFileInfo:upload')"
          >
            <a-button> <a-icon type="upload" />上传文件</a-button>
          </a-upload>
        </template>
        <span slot="fileOriginName" slot-scope="text">
          <ellipsis :length="10" tooltip>{{ text }}</ellipsis>
        </span>
        <span slot="fileObjectName" slot-scope="text">
          <ellipsis :length="10" tooltip>{{ text }}</ellipsis>
        </span>
        <span slot="fileLocation" slot-scope="text">
          {{ 'file_storage_location' | dictType(text) }}
        </span>
        <span slot="fileSuffix" slot-scope="text">
          <a-tag color="blue">{{ text }}</a-tag>
        </span>
        <span slot="action" slot-scope="text, record">
          <a v-if="hasPerm('sysFileInfo:download')" @click="sysFileInfoDownload(record)">下载</a>
          <a-divider type="vertical" v-if="hasPerm('sysFileInfo:download') & hasPerm('sysFileInfo:detail')"/>
          <a v-if="hasPerm('sysFileInfo:detail')" @click="$refs.detailForm.detail(record)">详情</a>
          <a-divider type="vertical" v-if="hasPerm('sysFileInfo:detail') & hasPerm('sysFileInfo:delete')"/>
          <a-popconfirm v-if="hasPerm('sysFileInfo:delete')" placement="topRight" title="确认删除？" @confirm="() => sysFileInfoDelete(record)">
            <a>删除</a>
          </a-popconfirm>
          <a-divider type="vertical" v-if="(hasPerm('sysFileInfo:preview') & record.fileSuffix === 'png' || record.fileSuffix === 'jpeg' || record.fileSuffix === 'jpg'|| record.fileSuffix === 'gif'|| record.fileSuffix === 'tif' || record.fileSuffix === 'bmp' ) & hasPerm('sysFileInfo:delete')"/>
          <a v-if="(hasPerm('sysFileInfo:preview') & record.fileSuffix === 'png' || record.fileSuffix === 'jpeg'|| record.fileSuffix === 'jpg'|| record.fileSuffix === 'gif'|| record.fileSuffix === 'tif' || record.fileSuffix === 'bmp' )" @click="$refs.previewForm.preview(record)">预览</a>
          <a-divider type="vertical" v-if="(hasPerm('sysFileInfo:preview') & record.fileSuffix === 'doc' || record.fileSuffix === 'docx'|| record.fileSuffix === 'xls'|| record.fileSuffix === 'xlsx') & hasPerm('sysFileInfo:delete')"/>
          <a v-if="(hasPerm('sysFileInfo:preview') & record.fileSuffix === 'doc' || record.fileSuffix === 'docx'|| record.fileSuffix === 'xls'|| record.fileSuffix === 'xlsx')" @click="previewMicrosoft(record)">预览</a>
        </span>
      </s-table>
      <detail-form ref="detailForm" @ok="handleOk" v-if="hasPerm('sysFileInfo:detail')"/>
      <preview-form ref="previewForm" v-if="hasPerm('sysFileInfo:preview')"/>
    </a-card>
  </a-spin>
</template>
<script>
  import { STable, Ellipsis, XCard } from '@/components'
  import { sysFileInfoPage, sysFileInfoDelete, sysFileInfoUpload, sysFileInfoDownload } from '@/api/modular/system/fileManage'
  import detailForm from './detailForm'
  import previewForm from './previewForm'
  export default {
    components: {
      XCard,
      STable,
      Ellipsis,
      detailForm,
      previewForm
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
            title: '存储位置',
            dataIndex: 'fileLocation',
            scopedSlots: { customRender: 'fileLocation' }
          },
          {
            title: '文件仓库',
            dataIndex: 'fileBucket'
          },
          {
            title: '文件名称',
            dataIndex: 'fileOriginName',
            scopedSlots: { customRender: 'fileOriginName' }
          },
          {
            title: '文件后缀',
            dataIndex: 'fileSuffix',
            scopedSlots: { customRender: 'fileSuffix' }
          },
          {
            title: '文件大小',
            dataIndex: 'fileSizeInfo'
          },
          {
            title: '唯一标识id',
            dataIndex: 'fileObjectName',
            scopedSlots: { customRender: 'fileObjectName' }
          }

        ],
        // 加载数据方法 必须为 Promise 对象
        loadData: parameter => {
          return sysFileInfoPage(Object.assign(parameter, this.queryParam)).then((res) => {
            return res.data
          })
        },
        cardLoading: false,
        fileLocationDictTypeDropDown: [],
        selectedRowKeys: [],
        selectedRows: []
      }
    },
    created () {
      this.sysDictTypeDropDown()
      if (this.hasPerm('sysPos:edit') || this.hasPerm('sysPos:delete')) {
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
       * 预览文件（微软插件）
       */
      previewMicrosoft (record) {
        window.open('https://view.officeapps.live.com/op/view.aspx?src=' + process.env.VUE_APP_API_BASE_URL + '/sysFileInfo/download?id=' + record.id)
      },
      /**
       * 获取字典数据
       */
      sysDictTypeDropDown () {
        this.fileLocationDictTypeDropDown = this.$options.filters['dictData']('file_storage_location')
      },
      /**
       * 下载文件（所有文件）
       */
      sysFileInfoDownload (record) {
        this.cardLoading = true
        sysFileInfoDownload({ id: record.id }).then((res) => {
          this.cardLoading = false
          this.downloadfile(res)
        // eslint-disable-next-line handle-callback-err
        }).catch((err) => {
          this.cardLoading = false
          this.$message.error('下载错误：获取文件流错误')
        })
      },
      downloadfile (res) {
        var blob = new Blob([res.data], { type: 'application/octet-stream;charset=UTF-8' })
        var contentDisposition = res.headers['content-disposition']
        var patt = new RegExp('filename=([^;]+\\.[^\\.;]+);*')
        var result = patt.exec(contentDisposition)
        var filename = result[1]
        var downloadElement = document.createElement('a')
        var href = window.URL.createObjectURL(blob) // 创建下载的链接
        var reg = /^["](.*)["]$/g
        downloadElement.style.display = 'none'
        downloadElement.href = href
        downloadElement.download = decodeURI(filename.replace(reg, '$1')) // 下载后文件名
        document.body.appendChild(downloadElement)
        downloadElement.click() // 点击下载
        document.body.removeChild(downloadElement) // 下载完成移除元素
        window.URL.revokeObjectURL(href)
      },
      sysFileInfoDelete (record) {
        sysFileInfoDelete(record).then((res) => {
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
      /**
       * 上传文件
       */
      customRequest (data) {
        const formData = new FormData()
        formData.append('file', data.file)
        sysFileInfoUpload(formData).then((res) => {
          if (res.success) {
            this.$message.success('上传成功')
            this.$refs.table.refresh()
          } else {
            this.$message.error('上传失败：' + res.message)
          }
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
