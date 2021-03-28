package com.e2buy.user.enums;

import com.e2buy.common.enums.IEnum;

/**
 * @Author: zhangjianwu
 * @Date: 2021/3/28 23:07
 * @Desc:
 **/
public enum CheckTypeEnum {
    USERNAME(1),
    PHONE(2),
    ;
    private Integer type;

    CheckTypeEnum(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
