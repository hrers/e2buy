import Vue from 'vue'
import Router from 'vue-router'



Vue.use(Router)

function route (path, file, name, children) {
  return {
    exact: true,
    path,
    name,
    children,
    component: () => import('../pages' + file)
  }
}

export default new Router({
  routes: [
    route("/login",'/Login',"Login"),// /login路径，路由到登录组件

    {
      path:"/", // 根路径，路由到 Layout组件
      component: () => import('../pages/Layout'),
      redirect:"/index/dashboard",
      children:[ // 其它所有组件都是 Layout的子组件  子路由
        route("/index/dashboard","/Dashboard","Dashboard"),
        route("/item/category",'/item/Category',"Category"),
        route("/item/brand",'/item/Brand',"Brand"),
        route("/item/list",'/item/Goods',"Goods"),
        route("/item/specification",'/item/specification/Specification',"Specification"),
        route("/trade/statistics",'/trade/Statistics',"Statistics"),
        route("/trade/sending",'/trade/Sending',"Sending"),
        route("/trade/requestCancel",'/trade/RequestCancel',"requestCancel"),
        route("/trade/sended",'/trade/Sended',"Sended"),
        route("/trade/finished",'/trade/Finished',"Finished"),
        route("/trade/evaluated",'/trade/Evaluated',"Evaluated"),
        route("/trade/dashboard",'/trade/Dashboard',"Dashboard"),
        route("/trade/promotion",'/trade/Promotion',"Promotion"),
        route("/trade/spec",'/trade/Spec',"Spec"),
        route("/trade/comment",'/trade/Comment',"Comment"),
        route("/user/logout",'/user/Logout',"Logout"),
        route("/user/changePassword",'/user/ChangePassword',"ChangePassword")
      ]
    }
  ]
})
