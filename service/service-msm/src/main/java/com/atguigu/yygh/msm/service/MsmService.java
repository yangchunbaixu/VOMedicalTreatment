package com.atguigu.yygh.msm.service;

import com.baomidou.mybatisplus.extension.service.IService;

public interface MsmService  {
    // 发送手机验证码
    boolean send(String phone,String code);
}
