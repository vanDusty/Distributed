package cn.van.concurrency.limit.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (C), 2015-2019, 风尘博客
 * 公众号 : 风尘博客
 * FileName: RedsiUtil
 *
 * @author: Van
 * Date:     2019-12-10 17:23
 * Description: ${DESCRIPTION}
 * Version： V1.0
 */
@Component
@Slf4j
public class CacheCountUtil {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public Integer getCount(String key) {
        String count = redisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(count)) {
            return null;
        }
        return Integer.parseInt(count);
    }

    public void initCount(String key, Integer seconds) {
        redisTemplate.opsForValue().set(key, "1", seconds, TimeUnit.SECONDS);
    }

    public void addCount(String key, Integer seconds) {
        redisTemplate.opsForValue().increment(key, 1);
        redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
    }
    public static String getIpAddress() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                    continue;
                } else {
                    Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        ip = addresses.nextElement();
                        if (ip != null && ip instanceof Inet4Address) {
                            log.info("获取到的ip地址：{}", ip.getHostAddress());
                            return ip.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取ip地址失败，{}",e);
        }
        return null;
    }
}
