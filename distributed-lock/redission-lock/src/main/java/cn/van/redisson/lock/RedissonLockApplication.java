package cn.van.redisson.lock;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Copyright (C), 2015-2019, 风尘博客
 * 公众号 : 风尘博客
 * FileName: RedisLockApplication
 *
 * @author: Van
 * Date:     2019-09-17 19:47
 * Description: ${DESCRIPTION}
 * Version： V1.0
 */
@SpringBootApplication
@Slf4j
@MapperScan("cn.van.redisson.lock.mapper")
public class RedissonLockApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedissonLockApplication.class);
        log.info("RedissonLockApplication start!");
    }
}
