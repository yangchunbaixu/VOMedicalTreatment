package com.atguigu.yygh.user.service.impl;

import com.alibaba.excel.util.StringUtils;
import com.atguigu.yygh.common.Helper.JwtHelper;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.result.ResultCodeEnum;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.model.user.UserLoginRecord;
import com.atguigu.yygh.user.Mapper.UserInfoMapper;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.vo.user.LoginVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserInfoServiceImpl  extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    /**
     * 登录方法
     * @param loginVo
     * @return
     */
    @Override
    public Map<String, Object> login(LoginVo loginVo) {
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();

        // 检验参数
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }


        //校验校验验证码   开启验证码
        String mobleCode = redisTemplate.opsForValue().get(phone);
        if(!code.equals(mobleCode)) {
            throw new YyghException(ResultCodeEnum.CODE_ERROR);
        }
        //绑定手机号码
        UserInfo userInfo = null;
        if(!StringUtils.isEmpty(loginVo.getOpenid())) {
              userInfo = this.getByOpenid(loginVo.getOpenid());
            if(null != userInfo) {
                userInfo.setPhone(loginVo.getPhone());
                this.updateById(userInfo);
            } else {
                throw new YyghException(ResultCodeEnum.DATA_ERROR);
            }
        }

        // 为null说明手机号直接注册
        if (userInfo == null){
            // 手机号被使用
            QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("phone", phone);
            // 获取会员
             userInfo = baseMapper.selectOne(queryWrapper);

            if ( userInfo   == null ) {
                userInfo = new UserInfo();
                userInfo.setName("");
                userInfo.setStatus(1);
                userInfo.setPhone(phone);
                this.save(userInfo);
            }
        }

        // 检验是否被禁用了
        if (userInfo.getStatus() == 0) {
            throw new YyghException(ResultCodeEnum.LOGIN_DISABLED_ERROR);
        }


        // 记录登记
      /*  UserLoginRecord userLoginRecord = new UserLoginRecord();
        userLoginRecord.setUserId(userInfo.getId());
        userLoginRecord.setIp(log);*/
        //返回页面显示名称
        Map<String, Object> map = new HashMap<>();
        String name = userInfo.getName();
        if (StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        if (StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }
        map.put("name", name);
        // 登录会生成token
        String token = JwtHelper.createToken(userInfo.getId(), name);
        map.put("token", token);

        return map;
    }

    @Override
    public UserInfo getByOpenid(String openId) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid",openId);
        UserInfo userInfo = baseMapper.selectOne(queryWrapper);
        return userInfo;
    }
}
