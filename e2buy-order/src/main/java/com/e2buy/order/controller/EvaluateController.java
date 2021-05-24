package com.e2buy.order.controller;

import com.e2buy.order.mapper.EvaluateMapper;
import com.e2buy.order.pojo.Evaluate;
import com.e2buy.order.pojo.OrderStatus;
import com.e2buy.order.service.EvaluateService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
