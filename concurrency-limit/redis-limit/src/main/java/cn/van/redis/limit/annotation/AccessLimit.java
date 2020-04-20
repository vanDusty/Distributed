package cn.van.redis.limit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Copyright (C), 2015-2019, 风尘博客
 * 公众号 : 风尘博客
 * FileName: AccessLimit
 *
 * @author: Van
 * Date:     2019-12-10 15:28
 * Description: ${DESCRIPTION}
 * Version： V1.0
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AccessLimit {

    // 三个参数分别代表有效时间、最大访问次数、是否需要登录，可以理解为 seconds 内最多访问 maxCount 次。
    int seconds();
    int maxCount();
    boolean needLogin()default true;
}
