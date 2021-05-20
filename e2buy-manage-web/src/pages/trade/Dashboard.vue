<template>
  <v-container fluid grid-list-md>
    <v-layout row wrap>
      <v-flex xs10 md6>
        <v-card>
          <v-card-text class="px2">
            <div ref="sale" style="width: 100%;height:350px"></div>
          </v-card-text>
        </v-card>
      </v-flex>

      <v-flex xs10 md6>
        <v-card >
          <v-card-text class="px2">
            <div ref="pie" style="width: 100%;height:350px"></div>
          </v-card-text>
        </v-card>
      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
  // 引入 ECharts 主模块
  var echarts = require('echarts/lib/echarts');
  require('echarts/lib/chart/bar');
  require('echarts/lib/chart/pie');

  export default {
    name: "dashboard",
    data(){
      return {
        todayMondy:0,
        totalMoney:0,
        sales:[]
      }
    },
    mounted(){
        this.getDataFromServer();
      setTimeout(() => {
        this.$nextTick(() => {
          var sale = echarts.init(this.$refs.sale);

          // 指定图表的配置项和数据
          var option = {
            title: {
              text: '销售统计'
            },
            tooltip: {},
            legend: {
              data:['销量']
            },
            xAxis: {
              data: ["今日销售额","总销售额"]
            },
            yAxis: {
              type:'value',
              axisLabel:{
                formatter:'{value}(k)'
              }
            },
            series: [{
              name: '销量',
              type: 'bar',
              data: [this.todayMoney,this.totalMoney],
              itemStyle: {
                normal: {
                  label: {
                    show: true, //开启显示
                    position: 'top', //在上方显示
                    textStyle: { //数值样式
                      color: 'black',
                      fontSize: 16
                    }
                  }
                }
              }
            }]
          };

          // 使用刚指定的配置项和数据显示图表。
          sale.setOption(option);

          const pie = echarts.init(this.$refs.pie);

          pie.setOption({
            roseType: 'angle',
            title: {
              text: '访问来源'
            },
            series : [
              {
                name: '访问来源',
                type: 'pie',
                radius: '55%',
                label: {
                  normal: {
                    show: true,
                    formatter: '{b}:({d}%)' //自定义显示格式(b:name, c:value, d:百分比)
                  }
                },
                data:[
                  {value:this.sales[0], name:'今天'},
                  {value:this.sales[1], name:'昨天'},
                  {value:this.sales[2], name:'前天'},
                  {value:this.sales[3], name:'大前天'},
                  {value:this.sales[4], name:'其他'}
                ]
              }
            ],
            itemStyle: {
              emphasis: {
                // 阴影的大小
                shadowBlur: 200,
                // 阴影水平方向上的偏移
                shadowOffsetX: 0,
                // 阴影垂直方向上的偏移
                shadowOffsetY: 0,
                // 阴影颜色
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          })
        })
      }, 500)

    },
    created() {

    },
    methods:{
      getDataFromServer(){
        this.$http.get("/order/getSaleResult").then(resp=>{
          //今天和总共
          this.todayMoney=this.$format(resp.data.todayMoney);
          this.totalMoney=this.$format(resp.data.totalMoney);
          this.todayMoney=this.todayMoney/1000;
          this.totalMoney=this.totalMoney/1000;


          //过去5天
          this.sales[0]=this.$format(resp.data.sales[0]);
          this.sales[1]=this.$format(resp.data.sales[1]);
          this.sales[2]=this.$format(resp.data.sales[2]);
          this.sales[3]=this.$format(resp.data.sales[3]);
          this.sales[4]=this.$format(resp.data.sales[4]);


        })
      }
    }
  }
</script>

<style scoped>

</style>
