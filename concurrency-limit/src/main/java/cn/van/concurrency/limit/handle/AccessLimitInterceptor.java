package cn.van.concurrency.limit.handle;

import cn.van.concurrency.limit.annotation.AccessLimit;
import cn.van.concurrency.limit.util.CacheCountUtil;
import cn.van.concurrency.limit.util.IpAddressUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * Copyright (C), 2015-2019, 风尘博客
 * 公众号 : 风尘博客
 * FileName: AccessLimtInterceptor
 *
 * @author: Van
 * Date:     2019-12-10 15:33
 * Description: 拦截器限流：适用于全局限流
 * Version： V1.0
 */
@Slf4j
public class AccessLimitInterceptor implements HandlerInterceptor {


    @Resource
    CacheCountUtil countUtil;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("进入拦截器");
        String ip = IpAddressUtil.getIpAddr();
        //判断请求是否属于方法的请求
        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            //获取方法中的注解,看是否有该注解
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if (null == accessLimit) {
                return true;
            }
            // 有效时间
            int seconds = accessLimit.seconds();
            // 最大访问次数
            int maxCount = accessLimit.maxCount();
            // 是否需要登录
            boolean needLogin = accessLimit.needLogin();
            //如果需要登录
            if (needLogin) {
                //判断是否登录
            }

            String key = request.getContextPath() + ":" + request.getServletPath() + ":" + ip;
            log.info("key:{}", key );

            //从redis中获取用户访问的次数
            Integer count = countUtil.getCount(key);
            if (null == count) {
                //第一次访问
                countUtil.initCount(key, seconds);
                return true;
            }

            if (count < maxCount) {
                //加1
                countUtil.addCount(key, seconds);
                return true;
            }
            //超出访问次数
            if (count >= maxCount) {
               // response 返回 校验不通过信息
                response.setContentType("application/json;charset=UTF-8");
                OutputStream out = response.getOutputStream();
                out.write("请求过于频繁请稍后再试".getBytes("UTF-8"));
                out.flush();
                out.close();
                return false;
            }
        }
        return true;
    }
}
