package com.e2buy.order.controller;

import com.e2buy.dto.EvaluateDto;
import com.e2buy.order.mapper.EvaluateMapper;
import com.e2buy.order.pojo.Evaluate;
import com.e2buy.order.pojo.OrderStatus;
import com.e2buy.order.service.EvaluateService;
import io.swagger.annotations.Api;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: zhangjianwu
 * @Date: 2021/5/22 13:02
 * @Desc:
 **/
@RestController
@RequestMapping("order/evaluate")
@Api("评论接口")
public class EvaluateController {

    @Autowired
    private EvaluateService evaluateService;

    @PostMapping("create")
    public ResponseEntity<Void> createEvaluate(@RequestBody Evaluate evaluate){
        evaluateService.createEvaluate(evaluate);
        return ResponseEntity.ok().build();
    }

    @GetMapping("{orderId}")
    public ResponseEntity<List<Evaluate>> queryEvaluateByOrderId(@PathVariable("orderId")Long orderId){
        Evaluate evaluate=evaluateService.queryEvaluateByOrderId(orderId);
        //目前返回的是对象，但后续可能返回多条评论，追评等，方便扩展需求
        List<Evaluate> list= new ArrayList<>();
        list.add(evaluate);
        return ResponseEntity.ok(list);
    }

}
