package com.e2buy.item.service.impl;

import com.e2buy.common.pojo.PageResult;
import com.e2buy.item.mapper.*;
import com.e2buy.item.pojo.*;
import com.e2buy.item.service.CategoryService;
import com.e2buy.item.service.GoodsService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: zjwawu@163.com
 * @Date: 2021/2/10 21:03
 * @Desc:
 **/
@Service
public class GoodsServiceImpl implements GoodsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsServiceImpl.class);
    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Override
    public PageResult<SpuBo> querySpuBoByPage(String key, Boolean saleable, Integer page, Integer rows) {
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        //搜索条件
        if(StringUtils.isNotBlank(key)){
            criteria.andLike("title","%"+key+"%");
        }
        if(saleable != null){
           criteria.andEqualTo("saleable",saleable);
        }
        //分页条件
        PageHelper.startPage(page,rows);
        //执行查询
        List<Spu> spus = spuMapper.selectByExample(example);
        PageInfo<Spu> pageInfo = new PageInfo<>(spus);

        //spu集合转化为spubo集合 1
//        List<SpuBo> spuBos = new ArrayList<>();
//        spus.forEach(spu -> {
//            SpuBo spuBo = new SpuBo();
//            //copy共同属性到新的对象
//            BeanUtils.copyProperties(spu,spuBo);
//            //查询分类名称
//            List<String> names = categoryService.queryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
//
//            spuBo.setCname(StringUtils.join(names,"/"));
//
//            //查询品牌的名称
//            spuBo.setBname(this.brandMapper.selectByPrimaryKey(spu.getBrandId()).getName());
//            spuBos.add(spuBo);
//        });

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


        return new PageResult<>(pageInfo.getTotal(),spuBos);
    }

    /**
     * 新增商品
     * @param spuBo
     */
    @Transactional
    @Override
    public void saveGoods(SpuBo spuBo) {
        //线新增spu
        spuBo.setId(null);
        spuBo.setSaleable(true);
        spuBo.setValid(true);
        spuBo.setCreateTime(new Date());
        spuBo.setLastUpdateTime(spuBo.getCreateTime());
        spuMapper.insertSelective(spuBo);

        //再去新增spuDetail
        SpuDetail spuDetail = spuBo.getSpuDetail();
        spuDetail.setSpuId(spuBo.getId());
        spuDetailMapper.insertSelective(spuDetail);

        //新增sku
        saveSkuAndStock(spuBo);
        //发送消息
        sendMsg("item.insert",spuBo.getId());
    }

    private void sendMsg(String type,Long id) {
        try {
            amqpTemplate.convertAndSend(type,id);
        }catch (AmqpException e){
            e.printStackTrace();
        }
    }

    private void saveSkuAndStock(SpuBo spuBo) {
        spuBo.getSkus().forEach(sku ->{
            //新增sku
            sku.setId(null);
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            skuMapper.insertSelective(sku);
            //新增stock
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            stockMapper.insertSelective(stock);
        });
    }

    /**
     * 根据spuId查询spuDetail
     * @param spuId
     * @return
     */
    @Override
    public SpuDetail querySpuDetailBySpuId(Long spuId) {
        return spuDetailMapper.selectByPrimaryKey(spuId);
    }


    /**
     * 根据spuid查询sku列表
     * @param spuId
     * @return
     */
    @Override
    public List<Sku> querySkusBySpuId(Long spuId) {

        Sku record= new Sku();
        record.setSpuId(spuId);
        List<Sku> skus= skuMapper.select(record);
        skus.forEach(sku -> {
            Stock stock = stockMapper.selectByPrimaryKey(sku.getId());
            sku.setStock(stock.getStock());
        });

        return skus;
    }

    /**
     * 更新商品信息
     * @param spuBo
     */
    @Transactional
    @Override
    public void updateGoods(SpuBo spuBo) {

        //根据spuId查询要删除的sku
        Sku record= new Sku();
        record.setSpuId(spuBo.getId());
        List<Sku> skus= skuMapper.select(record);
        skus.forEach(sku -> {
            //删除stock
            stockMapper.deleteByPrimaryKey(sku.getId());
        });

        //删除sku
        Sku sku = new Sku();
        sku.setSpuId(spuBo.getId());
        skuMapper.delete(sku);

        //新增sku和stock
        saveSkuAndStock(spuBo);

        //更新spu和spuDetail
        //防止更改createtime
        spuBo.setCreateTime(null);
        spuBo.setLastUpdateTime(new Date());
        spuBo.setCreateTime(null);
        spuBo.setValid(null);
        spuBo.setSaleable(null);
        spuMapper.updateByPrimaryKeySelective(spuBo);

        //更新spu详情
        spuDetailMapper.updateByPrimaryKeySelective(spuBo.getSpuDetail());
        //发送消息
        sendMsg("item.update",spuBo.getId());
    }

    @Override
    public Spu querySpuById(Long id) {
        return spuMapper.selectByPrimaryKey(id);
    }

    @Override
    public Sku querySkuBySkuId(Long skuId) {
        Sku sku = skuMapper.selectByPrimaryKey(skuId);
        return sku;
    }

    /**
     * 商品下架
     * @param id
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void goodsSoldOut(long id) {
    //下架或者上架spu中的商品
        Spu oldSpu = this.spuMapper.selectByPrimaryKey(id);
        Example example = new Example(Sku.class);
        example.createCriteria().andEqualTo("spuId",id);
        List<Sku> skuList = this.skuMapper.selectByExample(example);
        if (oldSpu.getSaleable()){
            //下架
            oldSpu.setSaleable(false);
            this.spuMapper.updateByPrimaryKeySelective(oldSpu);
            //下架sku中的具体商品
            for (Sku sku : skuList){
                sku.setEnable(false);
                this.skuMapper.updateByPrimaryKeySelective(sku);
            }
        }else {
            //上架
            oldSpu.setSaleable(true);
            this.spuMapper.updateByPrimaryKeySelective(oldSpu);
            //上架sku中的具体商品
            for (Sku sku : skuList){
                sku.setEnable(true);
                this.skuMapper.updateByPrimaryKeySelective(sku);
            }
        }
        //发送消息到mq
        this.sendMessage(id,"update");
    }

    /**
     * 商品删除二合一（多个单个）
     * @param id
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteGoods(long id) {
        //删除spu表中的数据
        this.spuMapper.deleteByPrimaryKey(id);
        //删除spu_detail中的数据
        Example example = new Example(SpuDetail.class);
        example.createCriteria().andEqualTo("spuId",id);
        this.spuDetailMapper.deleteByExample(example);

        List<Sku> skuList = this.skuMapper.selectByExample(example);
        for (Sku sku : skuList){
            //删除sku中的数据
            this.skuMapper.deleteByPrimaryKey(sku.getId());
            //删除stock中的数据
            this.stockMapper.deleteByPrimaryKey(sku.getId());
        }
        //发送消息到mq
        this.sendMessage(id,"delete");
    }

    @Override
    public Integer querySkuNum(Long skuId) {
        Stock stock = this.stockMapper.selectByPrimaryKey(skuId);
        return stock.getStock();
    }

    /**
     * 发送消息到mq，生产者
     * @param id
     * @param type
     */
    @Override
    public void sendMessage(Long id, String type) {
        try {
            this.amqpTemplate.convertAndSend("item." + type, id);
        }catch (Exception e){
            LOGGER.error("{}商品消息发送异常，商品id：{}",type,id,e);
        }
    }
}
