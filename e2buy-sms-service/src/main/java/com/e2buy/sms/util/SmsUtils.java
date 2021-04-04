package com.e2buy.sms.util;

import com.e2buy.sms.config.SmsProperties;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20190711.SmsClient;
import com.tencentcloudapi.sms.v20190711.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20190711.models.SendSmsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: zhangjianwu
 * @Date: 2021/4/1 22:29
 * @Desc:
 **/
@Component
@EnableConfigurationProperties(SmsProperties.class)
public class SmsUtils {

    @Autowired
    private SmsProperties smsProperties;

    public void send(String phone,String code){
        try{

//            Credential cred = new Credential("AKIDEHwHLBcEPKxE0bBr54rMMr76lBhyHPjq", "3lABxv4xZtxxiCQd2BG4i7C0e4FuJ0f5");
            Credential cred = new Credential(smsProperties.getSecretId(), smsProperties.getSecretKey());


            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("sms.tencentcloudapi.com");

            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            SmsClient client = new SmsClient(cred, "", clientProfile);

            SendSmsRequest req = new SendSmsRequest();
            String[] phoneNumberSet1 = {"+86"+phone};
            req.setPhoneNumberSet(phoneNumberSet1);

//            req.setTemplateID("908978");
            req.setTemplateID(smsProperties.getTemplateld());
            req.setSign("张建武个人的大学生活分享");

//            String[] templateParamSet1 = {"943235", "5"};
            String[] templateParamSet1 = {code, "5"};
            req.setTemplateParamSet(templateParamSet1);

//            req.setSmsSdkAppid("1400502929");
            req.setSmsSdkAppid(smsProperties.getSmsSdkAppld());

            SendSmsResponse resp = client.SendSms(req);

            System.out.println(SendSmsResponse.toJsonString(resp));
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }
    }

}
