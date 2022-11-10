package com.atguigu.yygh.msm.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

// 配置类
@Component
public class ConstantPropertiesUtils
      //  implements InitializingBean
{
    /*
    * 因为获取配置文件出来的值为null，所以暂时用这样的先
    * */


    /*@Value("${aliyun.sms.regionId}")
    private String regionId;

    @Value("${aliyun.sms.accessKeyId}")
    private static String accessKeyId;

    @Value("${aliyun.sms.accessKeySecret}")
    private String secret;

    @Value("${aliyun.sms.SignName}")
    private String SignName;

    @Value("${aliyun.sms.TemplateCode}")
    private String TemplateCode;
    */

     public static String REGION_Id ="cn-shenzhen";
     public static String ACCESS_KEY_ID = "LTAI5t82xBAB3cF9C3R4Pdpq";
     public static String SECRECT = "zYkVc2uKpJRBCItbTnIMFEIvYd25Lt";
     public static String SINGNAME ="阿里云短信测试";
     public static String TEMPLATECODE ="SMS_154950909";
    /*public static String REGION_Id;

    public static String ACCESS_KEY_ID;
    public static String SECRECT;
    public static String SINGNAME;
    public static String TEMPLATECODE;*/

    @Override
    public String toString() {
        return super.toString();
    }

 /*   @Override

    public void afterPropertiesSet() throws Exception {
        REGION_Id = regionId;
        ACCESS_KEY_ID = accessKeyId;
        SECRECT = secret;
        SINGNAME = SignName;
        TEMPLATECODE = TemplateCode;
    }*/


}
