package com.e2buy.test.sms;

import com.e2buy.sms.SmsServiceApplication;
import com.e2buy.sms.config.SmsProperties;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20190711.SmsClient;
import com.tencentcloudapi.sms.v20190711.models.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

;import javax.validation.Valid;

@SpringBootTest(classes = {SmsServiceApplication.class})
@RunWith(SpringRunner.class)
public class SendSms
{
    @Autowired
    private SmsProperties prop;

    @Test
    public void test(){
        System.out.println(prop.getSecretId());
    }

    public static void main(String [] args) {
        try{

            Credential cred = new Credential("AKIDEHwHLBcEPKxE0bBr54rMMr76lBhyHPjq", "3lABxv4xZtxxiCQd2BG4i7C0e4FuJ0f5");

            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("sms.tencentcloudapi.com");

            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            SmsClient client = new SmsClient(cred, "", clientProfile);

            SendSmsRequest req = new SendSmsRequest();
            String[] phoneNumberSet1 = {"+8618289339964"};
            req.setPhoneNumberSet(phoneNumberSet1);

            req.setTemplateID("908978");
            req.setSign("张建武个人的大学生活分享");

            String[] templateParamSet1 = {"943235", "5"};
            req.setTemplateParamSet(templateParamSet1);

            req.setSmsSdkAppid("1400502929");

            SendSmsResponse resp = client.SendSms(req);

            System.out.println(SendSmsResponse.toJsonString(resp));
        } catch (TencentCloudSDKException e) {
                System.out.println(e.toString());
        }

    }

}