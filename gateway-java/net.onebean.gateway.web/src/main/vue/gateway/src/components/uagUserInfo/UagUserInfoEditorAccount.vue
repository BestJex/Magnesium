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
        <Form ref="uagUserInfoFrom"
          :model="uagUserInfoFrom"
          :rules="uagUserInfoFromValidate"
          :label-width="110">
          <FormItem label="账号用户名"
            prop="username">
            <i-input v-model="uagUserInfoFrom.username"
              disabled
              placeholder="请输入账号用户名"></i-input>
          </FormItem>

          <FormItem label="账号名称"
            prop="nickName">
            <i-input v-model="uagUserInfoFrom.nickName"
              placeholder="请输入账号名称"></i-input>
          </FormItem>

          <FormItem>
            <Button type="primary"
              :disabled="globalButtonLoding"
              @click="handleSubmit('uagUserInfoFrom')">提交</Button>
            <Button @click="handleReset('uagUserInfoFrom')"
              style="margin-left: 8px">重置</Button>
          </FormItem>
        </Form>
      </i-col>
    </Row>
  </div>
</template>


<script>
export default {
  data() {
    return {
      routerPath: this.$route.path,
      uagUserInfoFrom: {
        username: '',
        nickName: '',
        id: this.$route.params.userId,
        appId: this.$route.params.appId
      },
      uagUserInfoFromValidate: {
        username: [
          {
            required: true,
            message: '应用名只能为有效的手机号码',
            pattern: /^[0-9]{11}$/,
            trigger: 'blur'
          }
        ],
        nickName: [
          {
            required: true,
            type: 'string',
            min: 1,
            max: 10,
            message: '账号名称为长度为1-10之间的字符',
            trigger: 'blur'
          }
        ],
        password: [
          {
            required: true,
            type: 'string',
            min: 5,
            max: 30,
            message: '账号名称为长度为5-30之间的字符',
            trigger: 'blur'
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
        this.API_PTAH.uagUserInfoFindById,
        { data: this.uagUserInfoFrom },
        response => {
          this.uagUserInfoFrom = response.data.data
        }
      )
    },
    commitData() {
      this.$store.commit('statusGlobalButtonLoding')
      this.uagUserInfoFrom.appId = this.$route.params.appId
      this.utils.netUtil.post(
        this.API_PTAH.uagUserInfoModify,
        this.uagUserInfoFrom,
        () => {
          this.$store.commit('statusGlobalButtonLoding')
          this.$Message.success('提交成功!')
          this.$store.commit('setUserInfoListAppId', this.uagUserInfoFrom.appId)
          this.$router.push('/uag-userinfo-list/')
        },
        () => {
          this.$store.commit('statusGlobalButtonLoding')
        }
      )
    }
  }
}
</script>
