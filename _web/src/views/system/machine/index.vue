<template>
  <div v-if="hasPerm('sysMachine:query')">

    <!-- 系统信息  Java信息-->
    <a-row :gutter="24">
      <a-col :md="12" :sm="24">
        <a-card :loading="loading" title="系统信息" style="margin-bottom: 20px" :bordered="false">

          <table class="sysInfo_table" >
            <tr >
              <td class="sysInfo_td">系统名称：</td>
              <td class="sysInfo_td">{{ this.sysOsInfo.osName }}</td>
            </tr>

            <tr >
              <td class="sysInfo_td">系统架构：</td>
              <td class="sysInfo_td">{{ this.sysOsInfo.osArch }}</td>
            </tr>

            <tr >
              <td class="sysInfo_td">系统版本：</td>
              <td class="sysInfo_td">{{ this.sysOsInfo.osVersion }}</td>
            </tr>

            <tr >
              <td class="sysInfo_td">主机名称：</td>
              <td class="sysInfo_td">{{ this.sysOsInfo.osHostName }}</td>
            </tr>

            <tr >
              <td >主机IP地址：</td>
              <td >{{ this.sysOsInfo.osHostAddress }}</td>
            </tr>
          </table>
        </a-card>
      </a-col>

      <a-col :md="12" :sm="24">
        <a-card :loading="loading" title="Java信息" style="margin-bottom: 20px">
          <table class="sysInfo_table" >

            <tr >
              <td class="sysInfo_td">虚拟机名称：</td>
              <td class="sysInfo_td">{{ this.sysJavaInfo.jvmName }}</td>
            </tr>

            <tr >
              <td class="sysInfo_td">虚拟机版本：</td>
              <td class="sysInfo_td">{{ this.sysJavaInfo.jvmVersion }}</td>
            </tr>

            <tr >
              <td class="sysInfo_td">虚拟机供应商：</td>
              <td class="sysInfo_td">{{ this.sysJavaInfo.jvmVendor }}</td>
            </tr>

            <tr >
              <td class="sysInfo_td">java名称：</td>
              <td class="sysInfo_td">{{ this.sysJavaInfo.javaName }}</td>
            </tr>

            <tr >
              <td >java版本：</td>
              <td >{{ this.sysJavaInfo.javaVersion }}</td>
            </tr>
          </table>
        </a-card>
      </a-col>
    </a-row>

    <a-card :loading="loading" title="JVM内存信息" >
      <table class="sysInfo_table" >

        <tr >
          <td class="sysInfo_td">最大内存：</td>
          <td class="sysInfo_td">{{ this.sysJvmMemInfo.jvmMaxMemory }}</td>
          <td class="sysInfo_td">可用内存：</td>
          <td class="sysInfo_td">{{ this.sysJvmMemInfo.jvmUsableMemory }}</td>
        </tr>
        <tr >
          <td class="sysInfo_td">总内存：</td>
          <td class="sysInfo_td">{{ this.sysJvmMemInfo.jvmTotalMemory }}</td>
          <td class="sysInfo_td">已使用内存：</td>
          <td class="sysInfo_td">{{ this.sysJvmMemInfo.jvmUsedMemory }}</td>
        </tr>
        <tr class="sysInfo_tr">
          <td >空余内存：</td>
          <td >{{ this.sysJvmMemInfo.jvmFreeMemory }}</td>
          <td >使用率：</td>
          <td >{{ this.sysJvmMemInfo.jvmMemoryUsedRate }}</td>
        </tr>

      </table>
    </a-card>
  </div>

</template>

<script>
  import { sysMachineQuery } from '@/api/modular/system/machineManage'

  export default {
    data () {
      return {
        loading: true,
        sysOsInfo: [],
        sysJavaInfo: [],
        sysJvmMemInfo: []
      }
    },

    // 进页面加载
    created () {
      this.loadDataList()
    },

    methods: {
      // 加载数据方法
      loadDataList () {
        sysMachineQuery().then((res) => {
          this.loading = false
          this.sysOsInfo = res.data.sysOsInfo
          this.sysJavaInfo = res.data.sysJavaInfo
          this.sysJvmMemInfo = res.data.sysJvmMemInfo
        })
      }
    }

  }
</script>

<style lang="less">
  .sysInfo_table{
    width: 100%; min-height: 45px; line-height: 45px; text-align: center;
  }
  .sysInfo_td {
  border-bottom:1px solid #e8e8e8;
  }
</style>
