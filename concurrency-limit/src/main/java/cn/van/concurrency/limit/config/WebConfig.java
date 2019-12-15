package cn.van.concurrency.limit.config;

import cn.van.concurrency.limit.handle.AccessLimitInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Copyright (C), 2015-2019, 风尘博客
 * 公众号 : 风尘博客
 * FileName: WebConfig
 *
 * @author: Van
 * Date:     2019-12-10 15:34
 * Description: ${DESCRIPTION}
 * Version： V1.0
 */
// @Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessLimitInterceptor())
                // 拦截路径（限流路径）
                .addPathPatterns("/**")
                // 不被拦截路径,通常为登录注册或者首页
                .excludePathPatterns("/login");
        ;
    }

    /**
     * 限流拦截器
     * @return
     */
    @Bean
    public AccessLimitInterceptor accessLimitInterceptor() {
        return new AccessLimitInterceptor();
    }
}
