package com.atguigu.yygh.msm.service.impl;

import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.atguigu.yygh.msm.config.ConstantPropertiesUtils;
import com.atguigu.yygh.msm.service.MsmService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class MsmServiceImpl implements MsmService {

    @Override
    public boolean send(String phone, String code) {
        // 判断手机号是否为空
        if (StringUtils.isEmpty(phone)) {
            return false;
        }
        //整合阿里云短信服务
        //设置相关参数

        DefaultProfile defaultProfile = DefaultProfile.getProfile(
                ConstantPropertiesUtils.REGION_Id,
                ConstantPropertiesUtils.ACCESS_KEY_ID,
                ConstantPropertiesUtils.SECRECT);
        IAcsClient client = new DefaultAcsClient(defaultProfile);
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setAction("SendSms");
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");


        // 手机号
        request.putQueryParameter("PhoneNumbers", phone);
        //签名名称
        request.putQueryParameter("SignName", ConstantPropertiesUtils.SINGNAME);
        //模板code
        request.putQueryParameter("TemplateCode", ConstantPropertiesUtils.TEMPLATECODE);

        // 验证码json格式
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        request.putQueryParameter("TemplateParam", JSONObject.toJSONString(map));
        //调用方法进行短信发送
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
            String json = response.getData();
            Gson g = new Gson();
            HashMap res = g.fromJson(json, HashMap.class);
            if ("OK".equals(res.get("Message"))) {
                log.info("短信发送成功：" + res.get("Message") + "code=" + code);
                return response.getHttpResponse().isSuccess();
            } else {
                log.info("短信发送失败，原因：" + res.get("Message"));
                return false;
            }
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }

        return false;
    }


}
