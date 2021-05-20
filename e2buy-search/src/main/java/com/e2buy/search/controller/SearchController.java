package com.e2buy.search.controller;

import com.e2buy.common.pojo.PageResult;
import com.e2buy.item.pojo.Spu;
import com.e2buy.item.pojo.SpuBo;
import com.e2buy.search.client.GoodsClient;
import com.e2buy.search.pojo.Goods;
import com.e2buy.search.pojo.SearchRequest;
import com.e2buy.search.pojo.SearchResult;
import com.e2buy.search.repository.GoodsRepository;
import com.e2buy.search.service.SearchService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: zjwawu@163.com
 * @Date: 2021/2/14 3:00
 * @Desc:
 **/
@Controller
public class SearchController {
    //public class SearchController implements InitializingBean {

    @Autowired
    private SearchService searchService;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private GoodsClient goodsClient;

    /**
     * 商品页面查询
     * 使用post请求方便传递更多的请求参数
     * @param request
     * @return
     */
    @PostMapping("page")
    public ResponseEntity<SearchResult> search(@RequestBody SearchRequest request){
        SearchResult result=searchService.search(request);
        if(result==null|| CollectionUtils.isEmpty(result.getItems())){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }


    public void afterPropertiesSet() throws Exception {
        // 创建索引
        this.elasticsearchTemplate.createIndex(Goods.class);
        // 配置映射
        this.elasticsearchTemplate.putMapping(Goods.class);
        Integer page = 1;
        Integer rows = 100;

        do {
            // 分批查询spuBo
            PageResult<SpuBo> pageResult = this.goodsClient.querySpuBoByPage(null, true, page, rows);

            // 遍历spubo集合转化为List<Goods>
            List<Goods> goodsList = pageResult.getItems().stream().map(spuBo -> {
                try {
                    return this.searchService.buildGoods((Spu) spuBo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());
            this.goodsRepository.saveAll(goodsList);

            // 获取当前页的数据条数，如果是最后一页，没有100条
            rows = pageResult.getItems().size();
            // 每次循环页码加1
            page++;
        } while (rows == 100);
    }
}
