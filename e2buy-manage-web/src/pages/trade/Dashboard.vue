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

      <v-flex xs10 md6>
        <v-card>
          <v-card-text className="px2">
            <div ref="places" style="width: 100%;height:350px"></div>
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
      sales:[],
      places:[]
    }
  },
  watch:{
  },
  mounted() {
    this.getDataFromServer();
    setTimeout(() => {
      this.$nextTick(() => {
        var sale = echarts.init(this.$refs.sale);
        // 指定图表的配置项和数据
        var option = {
          title: {
            text: '销售统计',
          },
          tooltip: {},
          legend: {
            data: ['销量']
          },
          xAxis: {
            data: ["今日", "本周", "本月", "今年销售额", "总销售额"]
          },
          yAxis: {
            type: 'value',
            axisLabel: {
              formatter: '{value}(万)'
            }
          },
          series: [{
            name: '销量',
            type: 'bar',
            data: [this.todayMoney, this.toweekMoney, this.tomonthMoney, this.toyearMoney, this.totalMoney],
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
            text: '季度统计'
          },
          series: [
            {
              name: '季度统计',
              type: 'pie',
              radius: '55%',
              label: {
                normal: {
                  show: true,
                  formatter: '{b}:({d}%)' //自定义显示格式(b:name, c:value, d:百分比)
                }
              },
              data: [
                {value: this.sales[0], name: '第一季度'},
                {value: this.sales[1], name: '第二季度'},
                {value: this.sales[2], name: '第三季度'},
                {value: this.sales[3], name: '第四季度'}
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
        var placesOption = {
          title: {
            text: '销售统计',
          },
          tooltip: {},
          legend: {
            data: ['销量']
          },
          xAxis: {
            data: [this.places[0].city, this.places[1].city, this.places[2].city, this.places[3].city, this.places[4].city]
          },
          yAxis: {
            type: 'value',
            axisLabel: {
              formatter: '{value}(单)'
            }
          },
          series: [{
            name: '销量',
            type: 'bar',
            data: [this.places[0].num, this.places[1].num, this.places[2].num, this.places[3].num, this.places[4].num],
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
        var place= echarts.init(this.$refs.places);
        place.setOption(placesOption);
      })
    }, 500)
  },
  beforeDestroy() {
    clearInterval(this.timer);
  },
  methods:{
    getDataFromServer(){
      this.$http.get("/order/getSaleResult").then(resp=>{
        this.todayMoney=this.$format(resp.data.todayMoney)/10000;
        this.toweekMoney=this.$format(resp.data.toweekMoney)/10000;
        this.tomonthMoney=this.$format(resp.data.tomonthMoney)/10000;
        this.toyearMoney=this.$format(resp.data.toyearMoney)/10000;
        this.totalMoney=this.$format(resp.data.totalMoney)/10000;
        //季度统计
        this.sales[0]=this.$format(resp.data.sales[0]);
        this.sales[1]=this.$format(resp.data.sales[1]);
        this.sales[2]=this.$format(resp.data.sales[2]);
        this.sales[3]=this.$format(resp.data.sales[3]);
        //地点统计
        this.places=resp.data.places;
      })
    }
  },
}
</script>

<style scoped>

</style>
