package com.atguigu.yygh.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.util.pattern.PathPatternParser;

@Configuration
public class CorsConfig {
    @Bean
    public CorsWebFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // 设置允许访问的请求头方法
        corsConfiguration.addAllowedHeader("*");
        // 设置允许访问的请求源方法
        corsConfiguration.addAllowedMethod("*");
        // 设置允许访问的请求源HTTP
        corsConfiguration.addAllowedOrigin("*");
        // 基于url的Cors配置源
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsWebFilter(source);

    }
}
