<template>
  <div>
    <a-list
      itemLayout="horizontal"
      :dataSource="data"
    >
      <a-list-item slot="renderItem" slot-scope="item, MenuIndex" :key="MenuIndex">
        <a-list-item-meta>
          <a slot="title">{{ item.title }}</a>
          <span slot="description">
            <span class="security-list-description">{{ item.description }}</span>
            <span v-if="item.value"> : </span>
            <span class="security-list-value">{{ item.value }}</span>
          </span>
        </a-list-item-meta>
        <template v-if="item.actions">
          <a slot="actions" @click="item.actions.callback">{{ item.actions.title }}</a>
        </template>
      </a-list-item>
    </a-list>
    <upd-pwd ref="updPwd"/>
  </div>
</template>

<script>
  import { mapGetters } from 'vuex'
  import UpdPwd from './securityItem/updPwd'
  export default {
    components: {
      UpdPwd
    },
    data () {
      return {
        data: []
      }
    },
    created () {
      if (this.hasPerm('sysUser:updatePwd')) {
        const updPwdMenu = {
          title: '账户密码',
          description: '当前密码强度',
          value: '强',
          actions: { title: '修改',
            callback: () => {
              this.$refs.updPwd.open(this.userInfo.id)
            }
          }
        }
        this.data.push(updPwdMenu)
      }
      const encryptedPhone = { title: '密保手机', description: '已绑定手机', value: '138****8293', actions: { title: '修改', callback: () => { this.$message.success('This is a message of success') } } }
      const encryptedProblem = { title: '密保问题', description: '未设置密保问题，密保问题可有效保护账户安全', value: '', actions: { title: '设置', callback: () => { this.$message.error('This is a message of error') } } }
      const encryptedEmail = { title: '备用邮箱', description: '已绑定邮箱', value: 'ant***sign.com', actions: { title: '修改', callback: () => { this.$message.warning('This is message of warning') } } }
      const encryptedMfa = { title: 'MFA 设备', description: '未绑定 MFA 设备，绑定后，可以进行二次确认', value: '', actions: { title: '绑定', callback: () => { this.$message.info('This is a normal message') } } }
      this.data.push(encryptedPhone)
      this.data.push(encryptedProblem)
      this.data.push(encryptedEmail)
      this.data.push(encryptedMfa)
    },
    computed: {
      ...mapGetters(['userInfo'])
    },
    methods: {
    }
  }
</script>

<style scoped>

</style>
