package com.e2buy.sms.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: zhangjianwu
 * @Date: 2021/3/29 23:14
 * @Desc:
 **/

@Component
@ConfigurationProperties(prefix = "e2buy.sms")
public class SmsProperties {

    String secretId;

    String secretKey;

    String templateld;

    String smsSdkAppld;

    String sine;

    public String getSine() {
        return sine;
    }

    public void setSine(String sine) {
        this.sine = sine;
    }

    public String getSecretId() {
        return secretId;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getTemplateld() {
        return templateld;
    }

    public void setTemplateld(String templateld) {
        this.templateld = templateld;
    }

    public String getSmsSdkAppld() {
        return smsSdkAppld;
    }

    public void setSmsSdkAppld(String smsSdkAppld) {
        this.smsSdkAppld = smsSdkAppld;
    }
}