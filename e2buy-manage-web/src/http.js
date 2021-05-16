import Vue from 'vue'
import axios from 'axios'
import config from './config'
//这样每次执行axios请求都会自动添加config.api(config.js中定义了这个值)这个前缀
axios.defaults.baseURL = config.api; // 设置axios的基础请求路径
axios.defaults.timeout = 2000; // 设置axios的请求时间

// 允许携带cookie
axios.defaults.withCredentials=true

// axios.interceptors.request.use(function (config) {
//   // console.log(config);
//   return config;
// })

axios.loadData = async function (url) {
  const resp = await axios.get(url);
  return resp.data;
}
//Vue.prototype:vue对象的原型，追加一个$http属性 属性值为axios,以后就不用每次都引用axios.使用的时候就直接用$http就可以了
Vue.prototype.$http = axios;// 将axios添加到 Vue的原型，这样一切vue实例都可以使用该对象


