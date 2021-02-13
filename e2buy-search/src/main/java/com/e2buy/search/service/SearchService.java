package com.e2buy.search.service;

import com.e2buy.search.client.BrandClient;
import com.e2buy.search.client.CategoryClient;
import com.e2buy.search.client.GoodsClient;
import com.e2buy.search.client.SpecificationClient;
import com.e2buy.item.pojo.*;
import com.e2buy.search.pojo.Goods;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * @Author: zjwawu@163.com
 * @Date: 2021/2/13 15:51
 * @Desc:
 **/
@Service
public class SearchService {


    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private SpecificationClient specificationClient;

    private static final ObjectMapper MAPPER=new ObjectMapper();


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
}
