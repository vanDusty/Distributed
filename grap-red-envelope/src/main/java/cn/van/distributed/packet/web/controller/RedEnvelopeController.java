package cn.van.distributed.packet.web.controller;

import cn.van.distributed.packet.service.RedPacketService;
import cn.van.distributed.packet.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (C), 2015-2020, 风尘博客
 * 公众号 : 风尘博客
 * FileName: RedPacketController
 *
 * @author: Van
 * Date:     2020-02-09 22:18
 * Description: ${DESCRIPTION}
 * Version： V1.0
 */
@Api(tags = "模拟抢红包接口")
@RestController
@RequestMapping("/redEnvelope")
@Slf4j
public class RedEnvelopeController {

    private static int corePoolSize = Runtime.getRuntime().availableProcessors();
    /**
     * 创建线程池  调整队列数 拒绝服务
     */
    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, corePoolSize + 1, 10L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000));

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private RedPacketService redPacketService;

    /**
     * 抢红包 拆红包 抢到基本能拆到
     *
     * @param redPacketId
     * @return
     */
    @ApiOperation(value = "抢红包一")
    @PostMapping("/start")
    @ApiImplicitParam(name = "redPacketId", value = "redPacketId", required = true)
    public HttpResult start(Long redPacketId) {
        // 模拟用户数量
        int skillNum = 100;
        // N个抢红包
        final CountDownLatch latch = new CountDownLatch(skillNum);
        /**
         * 初始化红包数据，抢红包拦截
         */
        redisUtil.set(redPacketId + "-num", 10);
        /**
         * 初始化剩余人数，拆红包拦截
         */
        redisUtil.set(redPacketId + "-restPeople", 10);
        /**
         * 初始化红包金额，单位为分
         */
        redisUtil.set(redPacketId + "-money", 20000);
        /**
         * 模拟100个用户抢10个红包
         */
        for (int i = 1; i <= skillNum; i++) {
            int userId = i;
            Runnable task = () -> {
                /**
                 * 抢红包拦截，其实应该分两步，为了演示方便
                 */
                Long count = redisUtil.decr(redPacketId + "-num", 1);
                if (count > 0) {
                    HttpResult result = redPacketService.startSpike(redPacketId, userId);
                    Double amount = DoubleUtil.divide(Double.parseDouble(result.getData().toString()), (double) 100);
                    log.info("用户{}抢红包成功，金额：{}", userId, amount);
                } else {
                    log.info("用户{}抢红包失败", userId);
                }
                latch.countDown();
            };
            executor.execute(task);
        }
        try {
            latch.await();
            Integer restMoney = Integer.parseInt(redisUtil.get(redPacketId + "-money"));
            log.info("剩余金额：{}", restMoney);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return HttpResult.success();
    }

    /**
     * 抢红包 拆红包 抢到不一定能拆到
     *
     * @param redPacketId
     * @return
     */
    @ApiOperation(value = "抢红包二")
    @PostMapping("/startTwo")
    @ApiImplicitParam(name = "redPacketId", value = "redPacketId", required = true)
    public HttpResult startTwo(Long redPacketId) {
        // 模拟用户数量
        int skillNum = 15;
        // N个抢红包
        final CountDownLatch latch = new CountDownLatch(skillNum);
        /**
         * 初始化红包数据，抢红包拦截
         */
        redisUtil.set(redPacketId + "-num", 10);
        /**
         * 初始化红包金额，单位为分
         */
        redisUtil.set(redPacketId + "-money", 20000);
        /**
         * 模拟100个用户抢10个红包
         */
        for (int i = 1; i <= skillNum; i++) {
            int userId = i;
            Runnable task = () -> {
                /**
                 * 抢红包 判断剩余金额
                 */
                Integer money = Integer.valueOf(redisUtil.get(redPacketId + "-money"));
                if (money > 0) {
                    /**
                     * 虽然能抢到 但是不一定能拆到
                     * 类似于微信的 点击红包显示抢的按钮
                     */
                    HttpResult result = redPacketService.startTwoSeckil(redPacketId, userId);
                    if (result.getCode().equals(500)) {
                        log.info("用户{}手慢了，红包派完了", userId);
                    } else {
                        Double amount = DoubleUtil.divide(Double.parseDouble(result.getData().toString()), (double) 100);
                        log.info("用户{}抢红包成功，金额：{}", userId, amount);
                    }
                } else {
                    /**
                     * 直接显示手慢了，红包派完了
                     */
                    log.info("用户{}手慢了，红包派完了", userId);
                }
                latch.countDown();
            };
            executor.execute(task);
        }
        try {
            latch.await();
            Integer restMoney = Integer.parseInt(redisUtil.get(redPacketId + "-money"));
            log.info("剩余金额：{}", restMoney);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return HttpResult.success();
    }

    /**
     * 有人没抢 红包发多了
     * 红包进入延迟队列
     * 实现过期失效
     *
     * @param redPacketId
     * @return
     */
    @ApiOperation(value = "抢红包三")
    @PostMapping("/startThree")
    @ApiImplicitParam(name = "redPacketId", value = "redPacketId")
    public HttpResult startThree(long redPacketId) {
        int skillNum = 9;
        final CountDownLatch latch = new CountDownLatch(skillNum);//N个抢红包
        /**
         * 初始化红包数据，抢红包拦截
         */
        redisUtil.set(redPacketId + "-num", 10);
        /**
         * 初始化红包金额，单位为分
         */
        redisUtil.set(redPacketId + "-money", 20000);
        /**
         * 加入延迟队列 24s秒过期
         */
        RedPacketMessage message = new RedPacketMessage(redPacketId, 24);
        RedPacketQueue.getQueue().produce(message);
        /**
         * 模拟 9个用户抢10个红包
         */
        for (int i = 1; i <= skillNum; i++) {
            int userId = i;
            Runnable task = () -> {
                /**
                 * 抢红包 判断剩余金额
                 */
                Integer money = Integer.valueOf(redisUtil.get(redPacketId + "-money"));
                if (money > 0) {
                    HttpResult result = redPacketService.startTwoSeckil(redPacketId, userId);
                    if (result.getCode().equals(500)) {
                        log.info("用户{}手慢了，红包派完了", userId);
                    } else {
                        Double amount = DoubleUtil.divide(Double.parseDouble(result.getData().toString()), (double) 100);
                        log.info("用户{}抢红包成功，金额：{}", userId, amount);
                    }
                }
                latch.countDown();
            };
            executor.execute(task);
        }
        try {
            latch.await();
            Integer restMoney = Integer.parseInt(redisUtil.get(redPacketId + "-money"));
            log.info("剩余金额：{}", restMoney);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return HttpResult.success();
    }
}
