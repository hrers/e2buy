package com.e2buy.goods.service;

import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;


/**
 * @Author: zhangjianwu
 * @Date: 2021/3/27 21:48
 * @Desc:
 **/
@Service
public class GoodsHtmlService{

    @Autowired
    private TemplateEngine engine;

    @Autowired
    private GoodsService goodsService;



    public void createHtml(Long spuId){

        //初始化运行上下文
        Context context = new Context();
        //设置数据模型
        context.setVariables(goodsService.loadData(spuId));
        PrintWriter printWriter =null;
        try {
            File file = new File("C:\\Users\\Admini\\Desktop\\2020javaUtils\\nginx-1.14.0\\nginx-1.14.0\\html\\item\\" + spuId + ".html");
            printWriter = new PrintWriter(file);
            engine.process("item",context,printWriter);

        }catch (FileNotFoundException e){
           e.printStackTrace();
        }finally {
            if(printWriter!=null){
               printWriter.close();
            }
        }
    }

    public void deleteHtml(Long id) {
        File file = new File("C:\\Users\\Admini\\Desktop\\2020javaUtils\\nginx-1.14.0\\nginx-1.14.0\\html\\item\\" + id + ".html");
        file.deleteOnExit();
    }
}
