<style>
</style>


<template>
  <div>
    <Row>
      <i-col span="24"
        class="bread-crumb">
        <Breadcrumb>
          <BreadcrumbItem v-for="(item,index) in breadcrumbList"
            :to="item.path"
            :key="index">
            <Icon :type="item.icon"></Icon> {{item.name}}
          </BreadcrumbItem>
        </Breadcrumb>
      </i-col>

    </Row>
    <Divider />
    <Row>
      <i-col span="12"
        offset="6">
        <Form ref="appInfoFrom"
          :model="appInfoFrom"
          :rules="fromValidate"
          :label-width="120">
          <FormItem label="应用名称"
            prop='appName'>
            <i-input v-model="appInfoFrom.appName"
              placeholder="请输入应用名称"></i-input>
          </FormItem>

          <FormItem label="应用状态"
            prop='appStatus'>
            <Select v-model="appInfoFrom.appStatus"
              placeholder="请输入应用状态">
              <Option v-for="item in appStatusEunmArr"
                :value="item.value"
                :key="item.value">{{ item.label }}</Option>
            </Select>
          </FormItem>

          <FormItem label="API授权模式"
            prop='authType'>
            <Select v-model="appInfoFrom.authType"
              placeholder="请输入API授权模式"
              disabled>
              <Option v-for="item in authTypeEunmArr"
                :value="item.value"
                :key="item.value">{{ item.label }}</Option>
            </Select>
            <ipWhiteList v-show="showOnInnerApi()" />
            <unLoginAccessWhiteList :app-id="appInfoFrom.id"
              v-show="showOnPrivateTokenAuthType()" />
          </FormItem>

          <FormItem label="账号授权模式"
            prop="loginType"
            v-if="showOnPrivateTokenAuthType()">
            <Select v-model="appInfoFrom.loginType"
              placeholder="选择账号授权模式">
              <Option v-for="item in loginTypeEunmArr"
                :value="item.value"
                :key="item.value">{{ item.label }}</Option>
            </Select>
          </FormItem>

          <FormItem label="SSO回调地址"
            prop="oauthBaseUrl"
            v-if="showOnSso()">
            <i-input v-model="appInfoFrom.oauthBaseUrl"
              placeholder="请输入SSO登陆成功回调地址"></i-input>
          </FormItem>

          <FormItem label="应用唯一ID"
            prop='openId'>
            <i-input v-model="appInfoFrom.openId"
              disabled
              placeholder="请输入创建时间"></i-input>
          </FormItem>

          <FormItem label="应用秘钥"
            prop='secret'
            v-show="showOnOpenApi()">
            <i-input v-model="appInfoFrom.secret"
              disabled
              placeholder="请输入应用秘钥"></i-input>
          </FormItem>

          <FormItem label="访问令牌"
            prop='innerApiToken'
            v-show="showOnInnerApi()">
            <i-input v-model="appInfoFrom.innerApiToken"
              disabled
              placeholder="请输入访问令牌"></i-input>
          </FormItem>

          <FormItem label="创建时间"
            prop='createTime'>
            <i-input v-model="appInfoFrom.createTime"
              disabled
              placeholder="请输入创建时间"></i-input>
          </FormItem>

          <FormItem label="	修改时间"
            prop='updateTime'>
            <i-input v-model="appInfoFrom.updateTime"
              disabled
              placeholder="请输入修改时间"></i-input>
          </FormItem>

          <FormItem label="	操作人"
            prop='operatorName'>
            <i-input v-model="appInfoFrom.operatorName"
              disabled
              placeholder="请输入操作人"></i-input>
          </FormItem>

          <FormItem>
            <Button type="primary"
              :disabled="globalButtonLoding"
              @click="handleSubmit('appInfoFrom')">提交</Button>
          </FormItem>
        </Form>
      </i-col>
    </Row>
  </div>

</template>


<script>
import ipWhiteList from '../ipWhiteList/ipWhiteList'
import unLoginAccessWhiteList from '../unLoginAccessWhiteList/unLoginAccessWhiteList'

export default {
  components: {
    ipWhiteList,
    unLoginAccessWhiteList
  },
  props: {
    query: { userId: 0 }
  },
  data() {
    return {
      routerPath: this.$route.path,
      loginTypeEunmArr: [
        {
          value: '0',
          label: 'API (短信验证码登录注册/短信验证码登录/密码登录)'
        },
        {
          value: '1',
          label: 'SSO (短信验证码登录)'
        },
        {
          value: '2',
          label: 'SSO (密码登录)'
        }
      ],
      authTypeEunmArr: [
        {
          value: '0',
          label: '授权码 (共享令牌)'
        },
        {
          value: '1',
          label: '私有令牌  (一设备一令牌 需要登录)'
        },
        {
          value: '2',
          label: 'IP白名单+通行令牌 (管理后台模式)'
        },
        {
          value: '3',
          label: '私有免登陆令牌 (一设备一令牌 免登陆)'
        }
      ],
      appStatusEunmArr: [
        {
          value: '0',
          label: '连接'
        },
        {
          value: '1',
          label: '断开'
        }
      ],
      appInfoFrom: {
        id: this.$route.params.id,
        createTime: '',
        updateTime: '',
        operatorName: '',
        appName: '',
        authType: '',
        appStatus: '',
        appCategory: '',
        oauthBaseUrl: '',
        loginType: ''
      },
      fromValidate: {
        loginType: [
          {
            required: true,
            message: '登录模式不能为空',
            trigger: 'blur'
          }
        ],
        oauthBaseUrl: [
          {
            required: true,
            message: '请正确填写url格式',
            pattern: /^(http|ftp|https){1}[:]{1}[/]{2}([0-9a-zA-Z]|[-]|[_])+\S+$/,
            trigger: 'blur'
          }
        ],
        appName: [
          {
            required: true,
            message: '应用名不能为空',
            trigger: 'blur'
          }
        ],
        appCategory: [
          {
            required: true,
            message: '应用分类不能为空',
            trigger: 'change'
          }
        ]
      }
    }
  },
  mounted: function() {
    this.loadData()
  },
  computed: {
    breadcrumbList: function() {
      return this.utils.routerUtil.initRouterTreeNameArr(this.routerPath)
    },
    globalScreenLoding: function() {
      return this.$store.state.globalScreenLoding
    },
    globalButtonLoding: function() {
      return this.$store.state.globalButtonLoding
    }
  },
  methods: {
    handleSubmit(name) {
      this.$refs[name].validate(valid => {
        if (valid) {
          this.commitData()
        } else {
          this.$Message.error('请完善表单信息!')
        }
      })
    },
    loadData() {
      this.utils.netUtil.post(
        this.API_PTAH.appInfoFindById,
        { data: this.appInfoFrom },
        response => {
          this.appInfoFrom = response.data.data
        }
      )
    },
    commitData() {
      this.$store.commit('statusGlobalButtonLoding')
      this.appInfoFrom.id = this.$route.params.id
      this.utils.netUtil.post(
        
        this.API_PTAH.appInfoUpdate,
        this.appInfoFrom,
        () => {
          this.$Message.success('提交成功!')
          this.$store.commit('statusGlobalButtonLoding')
          this.$router.push('/app-info-list')
        },
        () => {
          this.$store.commit('statusGlobalButtonLoding')
        }
      )
    },
    showOnOpenApi() {
      return this.appInfoFrom.authType === '3' ||
        this.appInfoFrom.authType === '1' ||
        this.appInfoFrom.authType === '0'
        ? true
        : false
    },
    showOnPrivateTokenAuthType() {
      return this.appInfoFrom.authType === '1' ? true : false
    },
    showOnInnerApi() {
      return this.appInfoFrom.authType === '2' ? true : false
    },
    showOnSso() {
      return this.appInfoFrom.loginType === '1' ||
        this.appInfoFrom.loginType === '2'
        ? true
        : false
    }
  }
}
</script>
