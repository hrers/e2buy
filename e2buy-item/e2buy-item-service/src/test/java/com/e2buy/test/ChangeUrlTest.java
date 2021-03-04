package com.e2buy.test;

import com.e2buy.item.mapper.SkuMapper;
import com.e2buy.item.pojo.Sku;
import com.e2buy.item.service.GoodsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ChangeUrlTest {

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private SkuMapper skuMapper;

    @Test
    public void test(){
        List<Sku> skus =skuMapper.selectAll();
        skus.forEach(sku -> {
            String images = sku.getImages();
            //String replace = images.replace("image.leyou.com", "image.e2buy.com");
            //不知道是不是switchhosts的bug还是，域名访问不了图片
            String replace = images.replace("image.e2buy.com", "106.52.158.23");
            System.out.println(replace);


            Sku record = new Sku();
            record.setId(sku.getId());
            record.setImages(replace);
            skuMapper.updateByPrimaryKeySelective(record);

        });
    }

}
