package com.e2buy.order.service;

import com.e2buy.order.mapper.EvaluateMapper;
import com.e2buy.order.pojo.Evaluate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;

/**
 * @Author: zhangjianwu
 * @Date: 2021/5/22 13:06
 * @Desc:
 **/
@Service
public class EvaluateService {

    @Autowired
    private EvaluateMapper evaluateMapper;

    public void createEvaluate(Evaluate evaluate) {
        evaluate.setCreateTime(new Date());
        evaluateMapper.insertSelective(evaluate);
    }
}
