<template>
  <div class="account-settings-info-view">
    <a-row :gutter="16">
      <a-col :md="24" :lg="16">

        <a-form layout="vertical" :form="form">
          <a-form-item
            label="昵称"
          >
            <a-input placeholder="给自己起个昵称吧" v-decorator="['nickName']"/>
          </a-form-item>
          <a-form-item
            label="生日"
          >
            <a-date-picker placeholder="请选择生日" @change="onChange" style="width: 100%" v-decorator="['birthday', {rules: [{required: true, message: '请选择生日！'}]}]" />
          </a-form-item>
          <a-form-item
            label="性别"
          >
            <a-radio-group v-decorator="['sex',{rules: [{ required: true, message: '请选择性别！' }]}]" >
              <a-radio v-for="(item,index) in sexData" :key="index" :value="item.code">{{ item.name }}</a-radio>
            </a-radio-group>
          </a-form-item>
          <a-form-item
            label="手机"
          >
            <a-input placeholder="请输入手机号" v-decorator="['phone', {rules: [{required: true, message: '请输入手机号！'}]}]"/>
          </a-form-item>
          <a-form-item
            label="电话"
          >
            <a-input placeholder="请输入电话" v-decorator="['tel', {rules: [{required: true, message: '请输入电话！'}]}]"/>
          </a-form-item>
          <a-form-item
            label="电子邮件"
            :required="false"
          >
            <a-input placeholder="请输入电子邮件地址" v-decorator="['email', {type: 'email',message: '请输入正确的邮箱号',rules: [{required: true, message: '请输入正确的邮箱号！'}]}]"/>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="submitUserInfo">更新基本信息</a-button>
          </a-form-item>
        </a-form>

      </a-col>
      <a-col :md="24" :lg="8" :style="{ minHeight: '180px' }">
        <div class="ant-upload-preview" @click="$refs.modal.edit(userInfo.id)" >
          <a-icon type="cloud-upload-o" class="upload-icon"/>
          <div class="mask">
            <a-icon type="plus" />
          </div>
          <img :src="option.img"/>
        </div>
      </a-col>

    </a-row>

    <avatar-modal ref="modal" @ok="setavatar"/>

  </div>
</template>

<script>
  import store from '@/store'
  import AvatarModal from './AvatarModal'
  import { mapGetters } from 'vuex'
  import moment from 'moment'
  import { sysUserUpdateInfo } from '@/api/modular/system/userManage'
// mapActions
export default {
  components: {
    AvatarModal
  },
  data () {
    return {
      // cropper
      preview: {},
      form: this.$form.createForm(this),
      sexData: [],
      option: {
        img: '/avatar2.jpg',
        info: true,
        size: 1,
        outputType: 'jpeg',
        canScale: false,
        autoCrop: true,
        // 只有自动截图开启 宽度高度才生效
        autoCropWidth: 180,
        autoCropHeight: 180,
        fixedBox: true,
        // 开启宽度和高度比例
        fixed: true,
        fixedNumber: [1, 1],
        // userInfo
        birthdayString: ''
      }
    }
  },
  computed: {
    ...mapGetters(['userInfo'])
  },
  mounted () {
    this.initUserInfo()
  },
  methods: {
    // ...mapActions(['GetInfo']),
    /**
     * 初始化用户信息
     */
    initUserInfo () {
      setTimeout(() => {
        this.form.setFieldsValue(
          {
            birthday: moment(this.userInfo.birthday, 'YYYY-MM-DD'),
            nickName: this.userInfo.nickName,
            sex: this.userInfo.sex.toString(),
            email: this.userInfo.email,
            phone: this.userInfo.phone,
            tel: this.userInfo.tel
          }
        )
        this.birthdayString = moment(this.userInfo.birthday).format('YYYY-MM-DD')
        this.option.img = process.env.VUE_APP_API_BASE_URL + '/sysFileInfo/preview?id=' + this.userInfo.avatar
        this.getSexData()
      }, 100)
    },
    /**
     * 日期需单独转换
     */
    onChange (date, dateString) {
      this.birthdayString = dateString
    },
    submitUserInfo () {
      const { form: { validateFields } } = this
      validateFields((err, values) => {
        if (!err) {
          values.birthday = this.birthdayString
          values.id = this.userInfo.id
          sysUserUpdateInfo(values).then((res) => {
            if (res.success) {
              this.$message.success('个人信息更新成功')
              store.dispatch('GetInfo').then(() => {})
            } else {
              this.$message.error(res.message)
            }
          })
        }
      })
    },
    getSexData () {
      this.sexData = this.$options.filters['dictData']('sex')
    },
    setavatar (url) {
      this.option.img = process.env.VUE_APP_API_BASE_URL + '/sysFileInfo/preview?id=' + url
      store.dispatch('GetInfo').then(() => {})
    }
  }
}
</script>

<style lang="less" scoped>

  .avatar-upload-wrapper {
    height: 200px;
    width: 100%;
  }

  .ant-upload-preview {
    position: relative;
    margin: 0 auto;
    width: 100%;
    max-width: 180px;
    border-radius: 50%;
    box-shadow: 0 0 4px #ccc;

    .upload-icon {
      position: absolute;
      top: 0;
      right: 10px;
      font-size: 1.4rem;
      padding: 0.5rem;
      background: rgba(222, 221, 221, 0.7);
      border-radius: 50%;
      border: 1px solid rgba(0, 0, 0, 0.2);
    }
    .mask {
      opacity: 0;
      position: absolute;
      background: rgba(0,0,0,0.4);
      cursor: pointer;
      transition: opacity 0.4s;

      &:hover {
        opacity: 1;
      }

      i {
        font-size: 2rem;
        position: absolute;
        top: 50%;
        left: 50%;
        margin-left: -1rem;
        margin-top: -1rem;
        color: #d6d6d6;
      }
    }

    img, .mask {
      width: 100%;
      max-width: 180px;
      height: 100%;
      border-radius: 50%;
      overflow: hidden;
    }
  }
</style>
