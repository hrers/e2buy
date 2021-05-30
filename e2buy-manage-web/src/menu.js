var menus = [
  {
    action: "home",
    title: "销售额总览",
    path:"/index",
    items: [{ title: "统计", path: "/dashboard" }]
  },
  {
    action: "apps",
    title: "商品后台",
    path:"/item",
    items: [
      { title: "分类管理", path: "/category" },
      { title: "品牌管理", path: "/brand" },
      { title: "商品管理", path: "/list" },
      { title: "规格管理", path: "/specification" }
    ]
  },
{
    action: "attach_money",
    title: "销售管理",
    path:"/trade",
    items: [
      { title: "已支付订单", path: "/statistics" },
      { title: "待发货订单", path: "/sending" },
      { title: "申请退款订单", path: "/requestCancel" },
      { title: "待收货订单", path: "/sended" },
      { title: "交易成功订单", path: "/finished" },
      { title: "已评价订单", path: "/evaluated" },
      { title: "销售统计", path: "/dashboard" },
   /*   { title: "订单详情", path: "/spec"}*/
    ]
  },
  {
    action: "people",
    title: "用户管理",
    path:"/user",
    items: [
      // { title: "权限管理", path: "/list" },
      // { title: "角色管理", path: "/role" },
      { title: "修改密码", path: "/changePassword"},
      { title: "退出登录", path: "/logout" }
    ]
  }
/*  {
    action: "attach_money",
    title: "销售管理",
    path:"/trade",
    items: [
      { title: "交易统计", path: "/statistics" },
      { title: "订单管理", path: "/order" },
      { title: "物流管理", path: "/logistics" },
      { title: "促销管理", path: "/promotion" }
    ]
  },*/
/*  {
    action: "settings",
    title: "权限管理",
    path:"/authority",
    items: [
      { title: "权限管理", path: "/list" },
      { title: "角色管理", path: "/role" },
      { title: "人员管理", path: "/member" }
    ]
  }*/

]

export default menus;
