package cn.van.redisson.lock.web.controller;

import cn.van.redisson.lock.service.RedissonLockService;
import cn.van.redisson.lock.util.HttpResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
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
 * FileName: RedissonLockController
 *
 * @author: Van
 * Date:     2019-09-17 19:40
 * Description: 测试控制器
 * Version： V1.0
 */
@RestController
@RequestMapping("/redissonLock")
@Slf4j
@Api(tags = "并发测试接口")
public class RedissonLockController {


    @Resource
    RedissonLockService redissonLockService;

    private static int corePoolSize = Runtime.getRuntime().availableProcessors();

    /**
     * 创建线程池  调整队列数 拒绝服务
     */
    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, corePoolSize + 1, 10L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000));


    @ApiOperation(value = "售卖商品（不加锁）")
    @PostMapping("/saleGoods")
    public HttpResult saleGoods() {
        // 销售十次，不加锁会导致库存减少到负数
        for (int i = 1; i <= 10; i++) {
            Runnable task = () -> {
                HttpResult result = redissonLockService.saleGoods();
                log.info("result:{}", result);
            };
            executor.execute(task);
        }
        return HttpResult.success();
    }

    @ApiOperation(value = "售卖商品（加锁）")
    @PostMapping("/saleGoodsLock")
    public HttpResult saleGoodsLock() {
        for (int i = 1; i <= 10; i++) {
            Runnable task = () -> {
                HttpResult result = redissonLockService.saleGoodsLock();
                log.info("result:{}", result);
            };
            executor.execute(task);
        }
        return HttpResult.success();
    }

}
