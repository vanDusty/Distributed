package cn.van.distributed.packet.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (C), 2015-2020, 风尘博客
 * 公众号 : 风尘博客
 * FileName: RedisUtil
 *
 * @author: Van
 * Date:     2020-02-09 22:20
 * Description: ${DESCRIPTION}
 * Version： V1.0
 */
@Component
@Slf4j
public class RedisUtil {
    
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 前缀
     */
    public static final String KEY_PREFIX_VALUE = "itstyle:seckill:value:";

    /**
     * 缓存value操作
     * @param k
     * @param v
     * @return
     */
    public void set(String k, Object v) {
        String key = KEY_PREFIX_VALUE + k;
        redisTemplate.opsForValue().set(key, String.valueOf(v));
    }

    /**
     * 获取缓存
     * @param k
     * @return
     */
    public String get(String k) {
        String key = KEY_PREFIX_VALUE + k;
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 递增
     * @param k
     * @param delta 要增加几(大于0)
     * @return
     */
    public Long incr(String k, Integer delta) {
        String key = KEY_PREFIX_VALUE + k;
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     * @param k 键
     * @param delta 要减少几(小于0)
     * @return
     */
    public Long decr(String k, Integer delta) {
        String key = KEY_PREFIX_VALUE + k;
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }
}

