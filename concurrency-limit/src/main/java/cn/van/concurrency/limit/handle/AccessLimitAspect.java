package cn.van.concurrency.limit.handle;

import cn.van.concurrency.limit.annotation.AccessLimit;
import cn.van.concurrency.limit.util.CacheCountUtil;
import cn.van.concurrency.limit.util.IpAddressUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Copyright (C), 2015-2019, 风尘博客
 * 公众号 : 风尘博客
 * FileName: AccessLimitAspect
 *
 * @author: Van
 * Date:     2019-12-10 17:48
 * Description: ${DESCRIPTION}
 * Version： V1.0
 */
@Aspect
@Slf4j
@Component
public class AccessLimitAspect {

    @Resource
    private CacheCountUtil countUtil;

    /**
     * 切入点
     */
    @Pointcut("@annotation(cn.van.concurrency.limit.annotation.AccessLimit)")
    public void accessLimitAspect() {

    }

    /**
     * 切入处理
     *
     * @param joinPoint
     * @return
     */
    @Around("accessLimitAspect()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String ip = IpAddressUtil.getIpAddr();
        //获取方法中的注解,看是否有该注解

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        AccessLimit accessLimit = method.getAnnotation(AccessLimit.class);
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
        log.info("key:{}", key);

        //从redis中获取用户访问的次数
        Integer count = countUtil.getCount(key);
        if (null == count) {
            //第一次访问
            countUtil.initCount(key, seconds);
        }else if (count < maxCount) {
            //加1
            countUtil.addCount(key, seconds);
        }else if (count >= maxCount) {
            //超出访问次数
            throw new RuntimeException("接口访问超出频率限制");
        }
        return joinPoint.proceed();
    }
}
