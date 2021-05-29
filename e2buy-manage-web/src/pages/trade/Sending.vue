<template>
  <v-card>
    <v-divider/>
    <v-data-table
      :headers="headers"
      :items="orders"
      :pagination.sync="pagination"
      :total-items="totalOrders"
      :loading="loading"
      class="elevation-1"
      v-model="selected"
    >
      <template slot="items" slot-scope="props">
<!--        <td class="text-xs-center">
          <v-checkbox v-model="selected" v-show="false" primary hide-details>

          </v-checkbox>
        </td>-->
        <td class="text-xs-center">{{ props.item.buyerNick}}</td>
        <td class="text-xs-center">{{ props.item.orderId }}</td>
        <td class="text-xs-center">{{ $format(props.item.totalPay)}}</td>
        <td class="text-xs-center">{{$format(props.item.actualPay)}}</td>
        <td class="text-xs-center">{{ props.item.createTime}}</td>
        <td class="justify-center layout px-0">
          <v-btn icon small @click="spec(props.item.orderId)">详情</v-btn>
          <v-btn icon small @click="send(props.item.orderId)">发货</v-btn>
          <v-btn icon small @click="cancel(props.item.orderId)">取消</v-btn>
        </td>
      </template>
    </v-data-table>

  </v-card>
</template>

<script>

export default {
  name: "statistics",
  data() {
    return {
      totalOrders: 0, // 总条数
      loading: true, // 是否在加载中
      pagination: {}, // 分页信息
      headers: [
        {text: '用戶名', align: 'center', sortable: false, value: 'nick'},
        {text: '订单编号', align: 'center', sortable: false, value: 'id'},
        {text: '总金额', align: 'center', sortable: false, value: 'title'},
        {text: '实际付款', align: 'center', sortable: false, value: 'cname'},
        {text: '创建时间', align: 'center', value: 'bname', sortable: false,},
        {text: '操作', align: 'center', sortable: false}
      ],
      orders:[],
      step: 1, // 子组件中的步骤线索引，默认为1
      selected:[], //选择的条目
      total:0
    }
  },
  mounted() { // 渲染后执行
    this.adminCheck();
    // 查询数据
    this.getDataFromServer();
  },
  watch:{
    pagination: { // 监视pagination属性的变化
      deep: true, // deep为true，会监视pagination的属性及属性中的对象属性变化
      handler() {
        // 变化后的回调函数，这里我们再次调用getDataFromServer即可
        this.getDataFromServer();
      }
    }
  },
  methods:{
    getDataFromServer() { // 从服务的加载数的方法。
      this.verify().then(() => {
        // 发起请求
        this.$http.get("/order/list/back",{
          params:{
            page:this.pagination.page,//当前页
            rows:this.pagination.rowsPerPage,//每页大小
            status:2 //已支付的订单
          }
        }).then(({data:{items,total}})=> { // 这里使用箭头函数
          this.orders=items;
          this.totalOrders=total;
          // 完成赋值后，把加载状态赋值为false
          this.loading = false;
        })
      }).catch(() => {
        alert("还未登录,请登录");
        this.$router.push("/login");
      });
    },
    previous(){
      if(this.step > 1){
        this.step--
      }
    },
    next(){
      if(this.step < 4 && this.$refs.goodsForm.$refs.basic.validate()){
        this.step++
      }
    },
    closeWindow() {
      console.log(1)
      // 重新加载数据
      this.getDataFromServer();
      // 关闭窗口
      this.show = false;
      // 将步骤调整到1
      this.step = 1;
    },
    send(orderId){
      this.verify().then(() => {
        // 发起请求
        this.$http.put("/order/admin/sendOrder/"+orderId).then(()=> { // 这里使用箭头函数
          this.getDataFromServer();
          // 完成赋值后，把加载状态赋值为false
          this.loading = false;
        })
      }).catch(() => {
        alert("还未登录,请登录");
        this.$router.push("/login");
      });
    },
    cancel(orderId){
      this.verify().then(() => {
        // 发起请求
        this.$http.delete("/order/admin/cancelOrder/"+orderId).then(()=> { // 这里使用箭头函数
          this.getDataFromServer();
          // 完成赋值后，把加载状态赋值为false
          this.loading = false;
        })
      }).catch(() => {
        alert("还未登录,请登录");
        this.$router.push("/login");
      });
    },
    spec(orderId){
      // this.$router.push("/trade/spec/${orderId}");
      this.$router.push({
        path:'/trade/spec',
        query:{
          orderId:orderId
        }
      })
    }
  }
}

</script>

<style scoped>

</style>
