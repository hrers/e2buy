<template>
  <v-app>
    <v-content>
      <v-container fluid fill-height>
        <v-layout align-center justify-center>
          <v-flex xs12 sm8 md4>
            <v-card class="elevation-12">
              <v-toolbar dark color="primary">
                <v-toolbar-title>修改密码</v-toolbar-title>
                <v-spacer></v-spacer>
              </v-toolbar>
              <v-card-text>
                <v-form>
                  <v-text-field prepend-icon="person" v-model="user.username" readonly="readonly"  label="用户名"  type="text"/>
                  <v-text-field
                    prepend-icon="lock"
                    v-model="oldPassword"
                    label="原密码"
                    id="oldPassword"
                    :append-icon="e1 ? 'visibility' : 'visibility_off'"
                    :append-icon-cb="() => (e1 = !e1)"
                    :type="e1 ? 'text' : 'password'"
                  ></v-text-field>
                  <v-text-field
                    prepend-icon="lock"
                    v-model="newPassword"
                    label="新密码"
                    id="newPassword"
                    :append-icon="e1 ? 'visibility' : 'visibility_off'"
                    :append-icon-cb="() => (e1 = !e1)"
                    :type="e1 ? 'text' : 'password'"
                  ></v-text-field>
                  <v-text-field
                    prepend-icon="lock"
                    v-model="newPassword2"
                    label="新密码"
                    id="newPassword2"
                    :append-icon="e1 ? 'visibility' : 'visibility_off'"
                    :append-icon-cb="() => (e1 = !e1)"
                    :type="e1 ? 'text' : 'password'"
                  ></v-text-field>
                </v-form>
              </v-card-text>
              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="primary" @click="doChange">提交</v-btn>
              </v-card-actions>
            </v-card>
          </v-flex>
        </v-layout>
      </v-container>
    </v-content>
    <v-dialog v-model="dialog" width="300px">
      <v-alert icon="warning" color="error" :value="true">
        用户名和密码不能为空
      </v-alert>
    </v-dialog>
  </v-app>
</template>

<script>
export default {
  data(){
    return{
      user:{},
      oldPassword: '',
      newPassword: '',
      newPassword2: '',
      e1:false,
      backPath:''
    }
  },
  mounted() {
    this.verify().then(resp => {
      // 查询用户信息
      this.user= resp.data;
    }).catch(() => {
      alert("还未登录,请登录");
      this.$router.push("/login");
    });
  },
  beforeRouteEnter(to,from,next){
    next(vm => {
      vm._data.backPath = from.path;
    });
  },
  methods: {
    doChange() {
      const form ={};
      form.userId = this.user.id;
      form.username = this.user.username;
      form.oldPassword = this.oldPassword;
      form.newPassword = this.newPassword;
      if(this.newPassword!=this.newPassword2){
        alert("两次密码输入不一致");
      }else {
        this.$http.put("/user/changePassword", this.$qs.stringify(form)).then(resp => {
          if (resp.status === 200) {
            //密码修改成功
            this.$message.success("密码修改成功！");
          }
        }).catch(() => {
          this.$message.error("原密码输入有误！");
        });
      }
    }
  }
};
</script>

