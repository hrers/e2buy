package com.e2buy.auth.client;

import com.e2buy.user.api.UserApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author: zhangjianwu
 * @Date: 2021/4/5 0:50
 * @Desc:
 **/
@FeignClient(value = "user-service")
public interface UserClient extends UserApi {
}
