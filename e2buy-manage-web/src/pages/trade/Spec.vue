<template>
  <v-card>
    <v-divider/>
    <v-data-table
      :headers="headers"
      :items="order.orderDetails"
      :loading="loading"
      class="elevation-1"
    >
      <template slot="items" slot-scope="props">
        <!--        <td class="text-xs-center">
                  <v-checkbox v-model="selected" v-show="false" primary hide-details>

                  </v-checkbox>
                </td>-->
        <td class="text-xs-center">{{ props.item.title}}</td>
        <td class="text-xs-center">{{ props.item.num}}</td>
        <td class="text-xs-center">{{ props.item.ownSpec}}</td>
        <!--   <td class="text-xs-center">{{ $format(props.item.totalPay)}}</td>
          <td class="text-xs-center">{{$format(props.item.actualPay)}}</td>
          <td class="text-xs-center">{{ props.item.createTime}}</td>
          <td class="justify-center layout px-0">
            <v-btn icon small @click="cancel(props.item.orderId)">取消订单</v-btn>
            <v-btn icon small @click="spec(props.item.orderId)">详情</v-btn>
          </td>-->
      </template>
    </v-data-table>

  </v-card>
</template>

<script>

export default {
  name: "statistics",
  data() {
    return {
      loading: true, // 是否在加载中
      headers: [
        {text: '商品', align: 'center', sortable: false, value: 'goods'},
        {text: '数量', align: 'center', sortable: false, value: 'num'},
        {text: '规格参数', align: 'center', sortable: false, value: 'spec'},
        /* {text: '总金额', align: 'center', sortable: false, value: 'title'},
       {text: '实际付款', align: 'center', sortable: false, value: 'cname'},
       {text: '创建时间', align: 'center', value: 'bname', sortable: false,},
       {text: '操作', align: 'center', sortable: false}*/
      ],
      order:{},
      total:0,
      orderId:''
    }
  },
  mounted() { // 渲染后执行
    // 查询数据
    //alert(this.$route.query.orderId);
    this.adminCheck();
    this.getDataFromServer();
  },
  methods:{
    getDataFromServer() { // 从服务的加载数的方法。
      this.verify().then(() => {
        // 发起请求
        this.$http.get("/order/"+this.$route.query.orderId).then(resp=> { // 这里使用箭头函数
          this.order=resp.data;
          // 完成赋值后，把加载状态赋值为false
          this.loading = false;
        })
      }).catch(() => {
        alert("还未登录,请登录");
        this.$router.push("/login");
      });
    }
  }
}

</script>

<style scoped>

</style>
