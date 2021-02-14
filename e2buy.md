[toc]

# 开发过程

## 检测服务启动器Actuator

```xml
   <!-- springboot检测服务启动器 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
```

为了测试路由规则是否畅通，我们是不是需要在item-service中编写一个controller接口呢？

其实不需要，SpringBoot提供了一个依赖：actuator

只要我们添加了actuator的依赖，它就会为我们生成一系列的访问接口：

- /info
- /health
- /refresh
- ...

因为我们没有添加信息，所以是一个空的json，但是可以肯定的是：我们能够访问到item-service了。

- spring-boot-starter-actuator：该依赖用来提供常规的微服务管理端点。另外，在Spring Cloud Zuul中还特别提供了/routes端点来返回当前的所有路由规则。

## 第七天

### 忽略autowired爆红

![1612776022589](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1612776022589.png)

### 使用nginx进行反向代理处理端口问题

* 根据域名的不同转发到不同的端口

```conf
  server {
        listen       80;
        server_name  manage.e2buy.com;

        proxy_set_header X-Forwarded-Host $host;
		proxy_set_header X-Forwarded-Server $host;
		proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
		
		location / {
			proxy_pass http://127.0.0.1:9001;
			proxy_connect_timeout 600;
			proxy_read_timeout 600;
		}
    }
	 server {
	        listen       80;
	        server_name  api.e2buy.com;

	        proxy_set_header X-Forwarded-Host $host;
			proxy_set_header X-Forwarded-Server $host;
			proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
			
			location / {
				proxy_pass http://127.0.0.1:10010;
				proxy_connect_timeout 600;
				proxy_read_timeout 600;
			}
	    }

```



### 使用zuul网关的方式解决跨域问题

```java
@Configuration
public class E2BuyCorsConfiguration {

    @Bean
    public CorsFilter corsFilter() {
        //1.添加CORS配置信息
        CorsConfiguration config = new CorsConfiguration();
        //1) 允许的域,不要写*，否则cookie就无法使用了
        config.addAllowedOrigin("http://manage.e2buy.com");
        //2) 是否发送Cookie信息
        config.setAllowCredentials(true);
        //3) 允许的请求方式
       /* config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("HEAD");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("DELETE");*/
        config.addAllowedMethod("*");
        // 4）允许的头信息
        config.addAllowedHeader("*");

        //2.添加映射路径，我们拦截一切请求
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", config);
        //3.返回新的CorsFilter.
        return new CorsFilter(configSource);
    }
}
```



## 



### 关于 e2buy-manage-web中前端代码的一些记录

#### 主体代码结构



![1543399927909](D:\2020IdeajavaE盘分流\乐优商城\leyou\day06-商品分类(vuetify-nginx-cors)\笔记\笔记\assets\1543399927909.png)







#### config.js

* 里面配置了baseUrl

  ```js
  const baseUrl = 'http://api.e2buy.com'
  const config = {
    locale: 'zh-CN', // en-US, zh-CN
    url: baseUrl,
    debug: {
      http: false // http request log
    },
    api: `${baseUrl}/api`,
  ```

  



#### http.js(封装了axios)

* 里面配置了axios的请求路径信息

  ```js
  import Vue from 'vue'
  import axios from 'axios'
  import config from './config'
  //这样每次执行axios请求都会自动添加config.api(config.js中定义了这个值)这个前缀
  axios.defaults.baseURL = config.api; // 设置axios的基础请求路径
  axios.defaults.timeout = 2000; // 设置axios的请求时间
  
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
  ```

  

#### router---index.js

```js
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
        route("/user/statistics",'/item/Statistics',"Statistics"),
        route("/trade/promotion",'/trade/Promotion',"Promotion")
      ]
    }
  ]
})

```



#### brand.vue表格渲染举例

* 页面

```js
//items:vue中的一个数组,获得axois返回的数据		props:数组中的一个对象  
<template slot="items" slot-scope="props">
        <td class="text-xs-center">{{ props.item.id }}</td>
        <td class="text-xs-center">{{ props.item.name }}</td>
        <td class="text-xs-center">
          <img v-if="props.item.image" :src="props.item.image" width="130" height="40">
          <span v-else>无</span>
        </td>
        <td class="text-xs-center">{{ props.item.letter }}</td>
        <td class="justify-center layout px-0">
          <v-btn icon @click="editBrand(props.item)">
            <i class="el-icon-edit"/>
          </v-btn>
          <v-btn icon @click="deleteBrand(props.item)">
            <i class="el-icon-delete"/>
          </v-btn>
        </td>
      </template>
```

* controller

  ```java
    /**
       * 根据查询条件分页并排序，查询品牌信息
       * @param key
       * @param page
       * @param rows
       * @param sortBy
       * @param desc true:desc  false:asc
       * @return
       */
      @GetMapping("page")
      public ResponseEntity<PageResult<Brand>> queryBrandsByPage(
              @RequestParam(value = "key",required = false) String key,
              @RequestParam(value = "page",defaultValue ="1")Integer page,
              @RequestParam(value = "rows",defaultValue = "5") Integer rows,
              @RequestParam(value = "sortBy",required = false)String sortBy,
              @RequestParam(value = "desc",required = false)Boolean desc
              ){
          PageResult<Brand> result=brandService.queryBrandsByPage(key,page,rows,sortBy,desc);
          if(CollectionUtils.isEmpty(result.getItems())){
              return ResponseEntity.notFound().build();
          }
          return ResponseEntity.ok(result);
      }
  ```

* service

  ```java
    @Override
      public PageResult<Brand> queryBrandsByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc) {
          // 初始化example对象
          Example example = new Example(Brand.class);
          Example.Criteria criteria = example.createCriteria();
  
          // 根据name模糊查询，或者根据首字母查询
          if (StringUtils.isNotBlank(key)) {
              criteria.andLike("name", "%" + key + "%").orEqualTo("letter", key);
          }
          // 添加分页条件
          PageHelper.startPage(page, rows);
          // 添加排序条件
          if (StringUtils.isNotBlank(sortBy)) {
              example.setOrderByClause(sortBy + " " + (desc ? "desc" : "asc"));
          }
          List<Brand> brands = this.brandMapper.selectByExample(example);
          // 包装成pageInfo
          PageInfo<Brand> pageInfo = new PageInfo<>(brands);
          // 包装成分页结果集返回
          return new PageResult<>(pageInfo.getTotal(), pageInfo.getList());
      }
  ```

* vue页面监听对象实现下一页，搜索框模糊查询等

  * pagination:vuetify自动帮填充字段并复制，只需定义一个空的对象即可

    ![1612807102560](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1612807102560.png)

  ```js
     watch: {
        pagination: { // 监视pagination属性的变化
          deep: true, // deep为true，会监视pagination的属性及属性中的对象属性变化
          handler() {
            // 变化后的回调函数，这里我们再次调用getDataFromServer即可
            this.getDataFromServer();
          }
        },
        search: { // 监视搜索字段
          handler() {
            this.getDataFromServer();
          }
        }
      },
  ```

  





### chrome清除网络请求信息

![1612796596582](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1612796596582.png)







## 第八天

### controller接受参数问题

```java
   @PostMapping
    public ResponseEntity<Void> saveBrand(Brand brand,@RequestParam("cids") List<Long> cids){
        this.brandService.saveBrand(brand,cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
```

* 问题？

  * 参数列表为一个Brand对象，和一个List集合。

* 解决办法：

  1. 使用请求字符串的方式进行传参

  * 使用qs.stringify()		QS，即Query String，请求参数字符串。
  * 什么是请求参数字符串？例如： name=jack&age=21，QS工具可以便捷的实现 JS的Object与QueryString的转换。
  *  将一个json对象转换为一个请求参数字符串{name:"张三"，age:"12"}=>name=zhangsan&age=12

  ```js
   this.$http({
              method: this.isEdit ? 'put' : 'post',
              url: '/item/brand',
              data: this.$qs.stringify(params)
            }).then(() => {
              // 关闭窗口
              this.$emit("close");
              this.$message.success("保存成功！");
            })
              .catch(() => {
                this.$message.error("保存失败！");
              });
  ```



   2. 将Brand和ids封装成一个新的对象，继续使用json方式传参

      注意

      * 使用对象接收json对象需要加@requestBody 普通变量不需要

      



###  Required request part 'file' is not 

两边的文件名称不对应，导致这个问题，所以controller

文件上传参数名key为file









### nginx rewrite

```conf
	# 上传路径的映射
		location /api/upload {	
			proxy_pass http://127.0.0.1:8082;
			proxy_connect_timeout 600;
			proxy_read_timeout 600;
			rewrite "^/api/(.*)$" /$1 break; 
        }
```





### 配置fastDFS常用linux命令

* tracker( storage同理)

```shell
安装fastdfs
tar -zxvf FastDFS_v5.08.tar.gz
cd FastDFS
./make.sh 
./make.sh install

关闭防火墙
chkconfig iptables off

开启服务/停止服务
1
    启动tracker服务器:     `/etc/init.d/fdfs_trackerd start`
    停止tracker服务器:     `/etc/init.d/fdfs_trackerd stop
2
    service fdfs_trackerd start # 启动fdfs_trackerd服务，停止用stop
    
开机自启动
chkconfig fdfs_trackerd on

```



### 在腾讯云主机上安装FastDFS服务器

* fastDFS服务 storage.conf 和tracker.conf的位置：/root/FastDFS/conf
* nginx.conf的位置： /opt/nginx/conf/nginx.conf

![1612886180878](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1612886180878.png)

```conf
 server{ 
        listen       9999; 
        server_name  106.52.158.23; 
        
        location ~/group([0-9])/ {
            ngx_fastdfs_module;
        }

        location / {
            root   html;
            index  index.html index.htm;
        }
        }
```

* service实现

  ```java
  
      @Override
      public String uploadImage(MultipartFile file) {
          String originalFilename = file.getOriginalFilename();
          // 校验文件的类型
          String contentType = file.getContentType();
          if (!CONTENT_TYPES.contains(contentType)){
              // 文件类型不合法，直接返回null
              LOGGER.info("文件类型不合法：{}", originalFilename);
              return null;
          }
          try {
              // 校验文件的内容
              BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
              if (bufferedImage == null){
                  LOGGER.info("文件内容不合法：{}", originalFilename);
                  return null;
              }
  
              // 保存到服务器
              //
              //file.transferTo(new File("C:\\Users\\Administrator\\Desktop\\image\\" + originalFilename));
              String ext = StringUtils.substringAfter(originalFilename, ".");
              StorePath storePath = storageClient.uploadFile(file.getInputStream(),file.getSize(),ext,null);
  
              // 生成url地址，返回
              return "http://image.e2buy.com:9999/" + storePath.getFullPath();
          } catch (IOException e) {
              LOGGER.info("服务器内部错误：{}", originalFilename);
              e.printStackTrace();
          }
          return null;
      }
  ```

  



### 关于springboot-test 测试类所处包不符合要求报错

报错java.lang.IllegalStateException: Unable to find a @SpringBootConfiguration, you need to use @ContextConfiguration or @SpringBootTest(classes=...) with your test

springboot单元测试时出现“java.lang.IllegalStateException: Unable to find a @SpringBootConfiguration, you need to use @Context”异常，之所以出现下面的异常，发现原因为src/test/java目录结构跟src/main/java下面的目录结构不一致，这里给出两种解决方案：

1.将src/test/java目录结构跟src/main/java下面的目录结构调整成一样

2.@SpringBootTest指定ApplicationMain的具体位置



### 关于Pagination

* Vue提供的分页组件，只要在页面添加一个Pagination对象，赋空值，Vue就会自动赋值相应的分页操作的参数

  ```vue
      
  //pagination定义
  data() {
        return {
          search: '', // 搜索过滤字段
          totalBrands: 0, // 总条数
          brands: [], // 当前页品牌数据
          loading: true, // 是否在加载中
          pagination: {}, // 分页信息
          headers: [
            {text: 'id', align: 'center', value: 'id'},
            {text: '名称', align: 'center', sortable: false, value: 'name'},
            {text: 'LOGO', align: 'center', sortable: false, value: 'image'},
            {text: '首字母', align: 'center', value: 'letter', sortable: true,},
            {text: '操作', align: 'center', value: 'id', sortable: false}
          ],
          show: false,// 控制对话框的显示
          oldBrand: {}, // 即将被编辑的品牌数据
          isEdit: false, // 是否是编辑
        }
      },
  
  //监听pagination实现分页效果
     watch: {
        pagination: { // 监视pagination属性的变化
          deep: true, // deep为true，会监视pagination的属性及属性中的对象属性变化
          handler() {
            // 变化后的回调函数，这里我们再次调用getDataFromServer即可
            this.getDataFromServer();
          }
        },
  
  
  //分页请求的方法，携带pagination自带的信息
  getDataFromServer() { // 从服务的加载数的方法。
          // 发起请求
          this.$http.get("/item/brand/page", {
            params: {
              key: this.search, // 搜索条件
              page: this.pagination.page,// 当前页
              rows: this.pagination.rowsPerPage,// 每页大小
              sortBy: this.pagination.sortBy,// 排序字段
              desc: this.pagination.descending// 是否降序
            }
          }).then(resp => { // 这里使用箭头函数
            this.brands = resp.data.items;
            this.totalBrands = resp.data.total;
            // 完成赋值后，把加载状态赋值为false
            this.loading = false;
          })
        },
  ```

  



## 第九天



### Pojo类中的@Transient和@Column(name = "`numeric`")

* 扩展pojo类，但是表中无此字段，需要加此注解

```java
//忽略该字段：表中无此字段
    @Transient
    private List<SpecParam> params;
```



* @Column注解

  numeric为Mysql关键字



### Vue初始化参数常用方法

* 钩子函数：created
* 监听：watch



### 一种集合转化另一种集合实现

#### 常规转换

```java
//spu集合转化为spubo集合 1
        List<SpuBo> spuBos = new ArrayList<>();
        spus.forEach(spu -> {
            SpuBo spuBo = new SpuBo();
            //copy共同属性到新的对象
            BeanUtils.copyProperties(spu,spuBo);
            //查询分类名称
            List<String> names = categoryService.queryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));

            spuBo.setCname(StringUtils.join(names,"/"));

            //查询品牌的名称
            spuBo.setBname(this.brandMapper.selectByPrimaryKey(spu.getBrandId()).getName());
            spuBos.add(spuBo);
        });
```

#### 使用stream().map()方式

```java
//spu集合转化为spuBo集合 2
        List<SpuBo> spuBos = spus.stream().map(spu -> {
            SpuBo spuBo = new SpuBo();
            BeanUtils.copyProperties(spu, spuBo);
            //查询品牌名称
            Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());
            spuBo.setBname(brand.getName());
            //查询分类名称
            List<String> names = categoryService.queryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            spuBo.setCname(StringUtils.join(names, "/"));
            return spuBo;
        }).collect(Collectors.toList());

```



## 第十天

### js中的异步与同步

```js
   //async 将js默认的异步操作改为同步操作
	//同步 顺序执行，一条执行不成功后面都不执行
      async editGoods(oldGoods) {
        // 发起请求，查询商品详情和skus
        oldGoods.spuDetail = await this.$http.loadData("/item/spu/detail/" + oldGoods.id);
        oldGoods.skus = await this.$http.loadData("/item/sku/list?id=" + oldGoods.id);
        // 修改标记
        this.isEdit = true;
        // 控制弹窗可见：
        this.show = true;
        // 获取要编辑的goods
        this.oldGoods = oldGoods;
      },
```

* 在http.js中进行了get同步封装

  ```js
  axios.loadData = async function (url) {
    const resp = await axios.get(url);
    return resp.data;
  }
  ```

  











## 第十一天



#### 我的服务器上的elasticsearch没有改，filter文件，因为我的是正常启动的



### 腾讯云安装elasticsearch

* java环境

* 安装修改配置（看报错对应教程）

* 开启端口

  ​	![1613113515908](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1613113515908.png)





# 一些优化写法

## 判断一个集合是否为空

```java
1.if判断是否为null和size是否为0
2.if(CollectionUtils.isEmpty(categories)) * 推荐
```



## controller标准一些的写法

```java
//restful风格 
@GetMapping("list")
    public ResponseEntity<List<Category>> queryCategorysById(@RequestParam(value = "pid",defaultValue = "0")Long pid){
        if(pid==null|| pid<0){
            // 400：参数不合法
            //return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            //return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().build();
            
        }
        List<Category> categories=categoryService.queryCategoriesById(pid);

        if(CollectionUtils.isEmpty(categories)){
            // 404 资源服务器未找到
            //return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            return ResponseEntity.notFound().build();
        }
        // 200 查询成功
        return ResponseEntity.ok(categories);
        //500 不用try-catch响应，默认遇到错误就会响应500 
    }
```





## 第十二天



#### springboot test测试类报错 @SpringBootConfiguration, you need to use @ContextConfiguration or @SpringBootTest(classes=...) wit

* 最有可能是测试包防止错误，没有与被测试的类报名一致

  如下才正确

  * com/e2buy/search/E2buySearchApplication.java

  * com/e2buy/search/client/ElasticSearchTest.java





#### 注意String尽量使用类似JavaUtils包这样的工具操作，显得专业一点,数字使用NumberUtils等

```java
 public Goods buildGoods(Spu spu) throws IOException {
        Goods goods = new Goods();
        //根据分类的id查询分类名称
        List<String> names = categoryClient.queryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));

        //根据品牌id查询品牌
        Brand brand = brandClient.queryBrandById(spu.getBrandId());
        
        //根据spuId查询所有的sku
        List<Sku> skus = goodsClient.querySkusBySpuId(spu.getId());
        //搜集所有sku价格
        ArrayList<Long> prices= new ArrayList<>();
        //搜集sku的必要信息
        ArrayList<Map<String,Object>> skuMapList= new ArrayList<>();

        skus.forEach(sku -> {
            prices.add(sku.getPrice());

            HashMap<String, Object> map= new HashMap<>();
            map.put("id",sku.getId());
            map.put("title",sku.getTitle());
            map.put("price",sku.getPrice());
            //获取sku中的图片，数据库的图片可能是多张，多张是以“，”分隔，获取第一张图片
            map.put("image",StringUtils.isBlank(sku.getImages())?"":StringUtils.split(sku.getImages(),",")[0]);

            skuMapList.add(map);
        });

        //根据spu中的cid3查询出所有的搜索规格参数
        List<SpecParam> params = specificationClient.queryParams(null, spu.getCid3(), null, true);


        //根据spuId查询spuDetail
        SpuDetail spuDetail = goodsClient.querySpuDetailBySpuId(spu.getId());
        //把通用的规格参数值,反序列化
        Map<String,Object> genericSpecMap=MAPPER.readValue(spuDetail.getGenericSpec(),new TypeReference<Map<String,Object>>(){});
        //对特殊的规格参数值，分序列化
        Map<String,Object> specialSpecMap=MAPPER.readValue(spuDetail.getSpecialSpec(),new TypeReference<Map<String,Object>>(){});

        HashMap<String, Object> specs= new HashMap<>();
        params.forEach(param->{
            // 判断规格参数的类型，是否是通用的规格参数
            if(param.getGeneric()){
                //如果是通用类型的参数，从genericSpecMap获取规格参数值
                String value= genericSpecMap.get(param.getId().toString()).toString();
                //判断是否是数值类型，如果是数值类型，应该返回一个区间
                if(param.getNumeric()){
                    value = chooseSegment(value, param);
                }
                specs.put(param.getName(),value);
            }else {
                //如果是特殊的规格参数，从specialSpecMap中获取值
                specs.put(param.getName(),specialSpecMap.get(param.getId().toString()));
            }

        });
        goods.setId(spu.getId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setBrandId(spu.getBrandId());
        goods.setCreateTime(spu.getCreateTime());
        goods.setSubTitle(spu.getSubTitle());
        //拼接all字段，需要分类名称以及品牌名称
        goods.setAll(spu.getTitle()+" "+ StringUtils.join(names," ")+" "+brand.getName());
        //获取spu下的所有sku的价格
        goods.setPrice(prices);
        //获取spu下的sku并转化成json字符串
        goods.setSkus(MAPPER.writeValueAsString(skuMapList));
        //获取所有查询的规格参数
        goods.setSpecs(specs);


        return goods;
    }
```



```java
private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }
```















#### elasticsearch 后台自己关闭问题

* xshell连接断开后，前台启动的elasticsearch也会关闭，
  * 解决办法：使用后台启动elasticsearch
    *  ./elasticsearch -d 



#### feign调动也要确保被调用的服务启动并注册到注册中心，不然会报错



#### Json和对象转换常用方法

```java
 //获取spu下的sku并转化成json字符串
        goods.setSkus(MAPPER.writeValueAsString(skuMapList));
 //把通用的规格参数值,反序列化
        Map<String,Object> genericSpecMap=MAPPER.readValue(spuDetail.getGenericSpec(),new TypeReference<Map<String,Object>>(){});
       
```



#### 注意解决跨域问题













## 第十三天

### 页面不显示json中为null的数据

```js
jackson:  default-property-inclusion: non_null
```



### 商品展示前台代码

```
       //vue只监控一开始就有值的属性变化，goodslist要增加属性载赋值给goodslist
        loadData(){
                //查询条件较多，使用post请求（请求参数参请求体中）
                e2b.http.post("/search/page",this.search).then(({data})=>{
                    //成功操作
                    data.items.forEach(goods=>{
                        goods.skus=JSON.parse(goods.skus);
                        //默认第一个被选中
                        goods.selected=goods.skus[0];
                    });
                    this.goodsList=data.items;
                }).catch(err=>{
                    //失败操作
                });
            }
```



* 字符串截取(substring)还有一个subst也可以用

  ```jsx
  <em>
  {{goods.selected.title.length>20?goods.selected.title.substring(0,20):goods.selected.title}}
  </em>
  <em>{{goods.subTitle.length>17?goods.subTitle.substring(0,17):goods.subTitle}}</em>
  ```

  

* 想要在html模板中使用已定义的一些实例，要在当前页面的vue实例引入才能使用，在js文件中不需要

  ```js
   data: {
              e2b,//映入e2b对象
              search: {
                  key: ""
              },
              goodsList: []
          },
      <i>{{e2b.formatPrice(goods.selected.price)}}</i>
  ```

  







* 分页

  ```html
  //获取地址栏参数后吧location(只有一个key字段)覆盖了，需要重新给search增加page字段
  const search=e2b.parse(location.search.substring(1));
  search.page = 1;
  
  data: {
          e2b,
      search: {
          key: "",
          page:1
      },
          goodsList: [],
          totalPage: 1
  },
  
          
  index(i){
      if(this.search.page<=3||this.totalPage<=5){
          return i;
      } else if (this.search.page>=this.totalPage-2){
          return this.totalPage-5+i;
      }else {
          return this.search.page-3+i;
      }
  }
              
  <li class="active" v-for="i in Math.min(5,totalPage)">
      <a href="#">{{index(i)}}</a>
  </li>
  ```

  

  

  



### 在html页面上打断点

![1613308223926](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1613308223926.png)











































## 

# 一些待解决的问题



### 八种请求方式总结

get post put ....



### reponsebody和reponseentity返回Json注解的原理



### 总结状态吗

404 405 。。。。。









## 未完成

* 品牌的改，删未完成