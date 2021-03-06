// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
//目录下的index.js可以省略文件名
import router from './router'
import Vuetify from 'vuetify'
import config from './config'
import MyComponent from './components/MyComponent'
import './http';
import 'vuetify/dist/vuetify.min.css'
import qs from 'qs'
import 'element-ui/lib/theme-chalk/index.css';
import './assets/material.css'

Vue.use(Vuetify, { theme: config.theme})
Vue.use(MyComponent)
Vue.prototype.$qs = qs;

Vue.config.productionTip = false

Vue.prototype.verify = function () {
  return this.$http.get("/auth/verify")
};
Vue.prototype.adminCheck=function () {
  this.verify().then(resp => {
    //暂时使用这个方式验证管理员，后期优化
    if (resp.data.username !== "admin") {
      alert("当前用户不是管理员,请以管理员账号登录");
      this.$router.push("/login");
    }
  }).catch(() => {
    alert("还未登录,请登录");
    this.$router.push("/login");
  });
};
Vue.prototype.logout= function () {
  Cookies.remove("E2BUY_TOKEN",{
    path:"/",
    domain:"e2buy.com"
  });
  this.$router.push("/");
}
/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  components: { App },
  template: '<App/>'
})
