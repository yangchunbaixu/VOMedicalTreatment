package com.atguigu.yygh.user.api;

import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.Helper.JwtHelper;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.result.ResultCodeEnum;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.config.ConstantPropertiesUtils;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.user.util.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
@RequestMapping("/api/ucenter/wx")
public class WeixinApiController {


    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 获取微信登录的参数
     *
     * @return
     * @throws Exception
     */
    @GetMapping("getLoginParam")
    @ResponseBody
    public Result genQrConnect() {
        try {
            String redirectUri = URLEncoder.encode(ConstantPropertiesUtils.WX_OPEN_REDIRECT_URL, "UTF-8");
            Map<String, Object> map = new HashMap<>();
            map.put("appid", ConstantPropertiesUtils.WX_OPEN_APP_ID);
            map.put("redirectUri", redirectUri);
            map.put("scope", "snsapi_login");
            map.put("state", System.currentTimeMillis() + "");//System.currentTimeMillis()+""
            log.info("redirectUri : " + redirectUri);
            return Result.ok(map);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 微信登录回调
     * 微信扫描登录会默认跳转到 http://ip:8105/api/ucenter/wx
     *
     * @param code
     * @param state
     * @return
     */
    @RequestMapping("callback")
    public String callback(String code, String state) {
        // 获取code，请求微信提供的地址，得到地址返回的access_token 和 openid

        //  1.获取临时code
        log.info("code : " + code);

        if (StringUtils.isEmpty(state)){
            log.error("----------------非法回调--------------------");
            throw new YyghException(ResultCodeEnum.ILLEGAL_CALLBACK_REQUEST_ERROR);
        }

        // 2.拿着code和微信id和秘钥请求微信的固定地址得到 access_toke 和 openid
        StringBuffer baseAccessTokenUrl = new StringBuffer()
                .append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&code=%s")
                .append("&grant_type=authorization_code");
        log.info("baseAccessTokenUrl： " + baseAccessTokenUrl);
        String accessTokenUrl = String.format(baseAccessTokenUrl.toString(),
                ConstantPropertiesUtils.WX_OPEN_APP_ID,  // 占位符拼接参数
                ConstantPropertiesUtils.WX_OPEN_APP_SECRET);
        log.info("accessTokenUrl： " + accessTokenUrl);

        String resultJson = null;
        try {
            resultJson = HttpClientUtils.get(accessTokenUrl);

            // result 获取出来是json形式，要获取里面的access_token 和 openid
            JSONObject jsnObject = JSONObject.parseObject(resultJson);

            if (jsnObject.getString("errcode") != null) {
                log.error("获取access_token失败：" + jsnObject.getString("errcode") + jsnObject.getString("errmsg"));
                throw new YyghException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
            }

            String accessToken = jsnObject.getString("access_token");
            String openId = jsnObject.getString("openid");
            log.info(accessToken);
            log.info(openId);

            //根据access_token获取微信用户的基本信息
            //先根据openid进行数据库查询
            UserInfo userInfo = userInfoService.getByOpenid(openId);

            // 如果没有查到用户信息,那么调用微信个人信息获取的接口
            if (null == userInfo) {
                //如果查询到个人信息，那么直接进行登录
                // 使用access_token换取受保护的资源：微信的个人信息
                String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                        "?access_token=%s" +
                        "&openid=%s";

                String userInfoUrl = String.format(baseUserInfoUrl, accessToken, openId);

                String resultUserInfo = null;
                try {
                    resultUserInfo = HttpClientUtils.get(userInfoUrl);
                } catch (Exception e) {
                    throw new YyghException((ResultCodeEnum.FETCH_USERINFO_ERROR));
                }
                log.info("使用access_token获取用户信息的结果 = " + resultUserInfo);


                JSONObject resultUserInfoJson = JSONObject.parseObject(resultUserInfo);
                if (resultUserInfoJson.getString("errcode") != null) {
                    log.error("获取用户信息失败：" + resultUserInfoJson.getString("errcode") + resultUserInfoJson.getString("errmsg"));
                    throw new YyghException(ResultCodeEnum.FETCH_USERINFO_ERROR);
                }
                // 解析用户信息
                String nickname = resultUserInfoJson.getString("nickname");
                String headimgurl = resultUserInfoJson.getString("headimgurl");

                log.info(accessToken);
                log.info(headimgurl);

                UserInfo userInfo2 = new UserInfo();
                userInfo2.setOpenid(openId);
                userInfo2.setNickName(nickname);
                userInfo2.setStatus(1);
                userInfoService.save(userInfo2);

            }

            Map<String, String> map = new HashMap<>();

            String name = userInfo.getName();
            if (StringUtils.isEmpty(name)) {
                name = userInfo.getNickName();
            }
            if (StringUtils.isEmpty(name)) {
                name = userInfo.getPhone();
            }
            map.put("name", name);
            // 碰到userinfo是否有手机号，如果手机号为空返回openid
            // 手机号不为空返回空字符串
            // 前端的判定 ： 为空绑定手机号，不为空不绑定
            if (StringUtils.isEmpty(userInfo.getPhone())) {
                map.put("openid", userInfo.getOpenid());
            } else {
                map.put("openid", "");
            }
            String token = JwtHelper.createToken(userInfo.getId(), name);
            map.put("token", token);
            log.info("token : " + token);
            return "redirect:" + ConstantPropertiesUtils.YYGH_BASE_URL +
                    "/weixin/callback?token=" +   // 会找page页面
                    map.get("token") + "&openid=" +
                    map.get("openid") + "&name=" +
                    URLEncoder.encode( map.get("name"), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
