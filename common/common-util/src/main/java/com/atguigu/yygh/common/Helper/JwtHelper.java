package com.atguigu.yygh.common.Helper;

import com.alibaba.excel.util.StringUtils;
import io.jsonwebtoken.*;
import io.swagger.models.auth.In;

import java.util.Date;

public class JwtHelper {
    // token 的过去时间
    private static final long tokenExpiration = 24 * 60 * 60 * 100;
    // token签名的密钥
    private static final String tokenSingKey = "123456";

    // 根据参数生成token
    public static String createToken(Long userId, String userName) {
        String token = Jwts.builder()
                .setSubject("YYGH-USER")
                .setExpiration(new Date(System.currentTimeMillis()+tokenExpiration))
                .claim("userId",userId)
                .claim("userName",userName)
                .signWith(SignatureAlgorithm.HS256,tokenSingKey)
                .compressWith(CompressionCodecs.GZIP)
                .compact();
        return token;
    }
    // 根据token获取用户id
    public static Long getUserId(String token){
        if(StringUtils.isEmpty(token)){
            return null;
        }
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSingKey).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        Integer userId =(Integer) claims.get("userId");
        return userId.longValue();
    }
    // 根据token获取用户名称
    public static String getUserName(String token){
        if (StringUtils.isEmpty(token)) return "";
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSingKey).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        return (String) claims.get("userName");
    }

    public static void main(String[] args) {
        String token = JwtHelper.createToken(1L, "55");
        System.out.println(token);
        System.out.println(JwtHelper.getUserId(token));
        System.out.println(JwtHelper.getUserName(token));
    }

}
