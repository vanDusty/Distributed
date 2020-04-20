package cn.van.redis.lock.web.controller;

import cn.van.redis.lock.service.RedisLockService;
import cn.van.redis.lock.util.HttpResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (C), 2015-2019, 风尘博客
 * 公众号 : 风尘博客
 * FileName: RedisLockController
 *
 * @author: Van
 * Date:     2019-09-17 19:40
 * Description: 测试控制器
 * Version： V1.0
 */
@RestController
@RequestMapping("/redisLock")
@Slf4j
@Api(tags = "并发测试接口")
public class RedisLockController {


    @Resource
    RedisLockService redisLockService;

    private static int corePoolSize = Runtime.getRuntime().availableProcessors();

    /**
     * 创建线程池  调整队列数 拒绝服务
     */
    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, corePoolSize + 1, 10L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000));


    @ApiOperation(value = "家庭成员领取奖励（不加锁）")
    @PostMapping("/receiveAward")
    public HttpResult receiveAward() {
        for (int i = 1; i <= 3; i++) {
            Runnable task = () -> {
                HttpResult result = redisLockService.receiveAward();
                log.info("result:{}", result);
            };
            executor.execute(task);
        }
        return HttpResult.success();
    }

    @ApiOperation(value = "家庭成员领取奖励（加锁）")
    @PostMapping("/receiveAwardLock")
    public HttpResult receiveAwardLock() {
        for (int i = 1; i <= 3; i++) {
            Runnable task = () -> {
                HttpResult result = redisLockService.receiveAwardLock();
                log.info("result:{}", result);
            };
            executor.execute(task);
        }
        return HttpResult.success();
    }

}
