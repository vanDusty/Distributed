package cn.van.distributed.packet.service.impl;

import cn.van.distributed.packet.entity.RedRacketRecordDO;
import cn.van.distributed.packet.mapper.RedRacketRecordMapper;
import cn.van.distributed.packet.service.RedPacketService;
import cn.van.distributed.packet.util.HttpResult;
import cn.van.distributed.packet.util.Result;
import cn.van.distributed.packet.util.RedisUtil;
import cn.van.distributed.packet.util.RedissLockUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (C), 2015-2020, 风尘博客
 * 公众号 : 风尘博客
 * FileName: RedPacketServiceImpl
 *
 * @author: Van
 * Date:     2020-02-09 22:25
 * Description: ${DESCRIPTION}
 * Version： V1.0
 */

@Service("redPacketService")
public class RedPacketServiceImpl implements RedPacketService {

    @Autowired
    private RedisUtil redisUtil;
    @Resource
    RedRacketRecordMapper redPacketRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HttpResult startSpike(Long redPacketId, Integer userId) {
        Integer money = 0;
        boolean res = false;
        try {
            /**
             * 获取锁
             */
            res = RedissLockUtil.tryLock(redPacketId + "", TimeUnit.SECONDS, 3, 10);
            if (res) {
                Long restPeople = redisUtil.decr(redPacketId + "-restPeople", 1);
                /**
                 * 如果是最后一人,剩余的钱全给他
                 */
                if (restPeople == 1) {
                    money = Integer.parseInt(redisUtil.get(redPacketId + "-money"));
                } else {
                    // 不是最后一个人，随机分配金额
                    money = restMoney(redPacketId, restPeople);
                }
                redisUtil.decr(redPacketId + "-money", money);
                /**
                 * 异步入库
                 */
                saveRecord(money, redPacketId, userId);
                /**
                 * 异步入账
                 */
            } else {
                /**
                 * 获取锁失败相当于抢红包失败，红包个数加一
                 */
                redisUtil.incr(redPacketId + "-num", 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //释放锁
            if (res) {
                RedissLockUtil.unlock(redPacketId + "");
            }
        }
        return HttpResult.success(money);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HttpResult startTwoSeckil(Long redPacketId, Integer userId) {
        Integer money = 0;
        boolean res = false;
        try {
            /**
             * 获取锁 保证红包数量和计算红包金额的原子性操作
             */
            res = RedissLockUtil.tryLock(redPacketId + "", TimeUnit.SECONDS, 3, 10);
            if (res) {
                Long restPeople = redisUtil.decr(redPacketId + "-num", 1);
                if (restPeople < 0) {
                    return HttpResult.failure(500,"手慢了，红包派完了");
                } else {
                    /**
                     * 如果是最后一人
                     */
                    if (restPeople == 0) {
                        money = Integer.parseInt(redisUtil.get(redPacketId + "-money"));
                    } else {
                        // 不是最后一个人，随机分配金额
                        money = restMoney(redPacketId, restPeople);
                    }
                    redisUtil.decr(redPacketId + "-money", money);
                    /**
                     * 异步入库
                     */
                    saveRecord(money, redPacketId, userId);
                    /**
                     * 异步入账
                     */
                }
            } else {
                /**
                 * 获取锁失败相当于抢红包失败
                 */
                return HttpResult.failure(500, "手慢了，红包派完了");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //释放锁
            if (res) {
                RedissLockUtil.unlock(redPacketId + "");
            }
        }
        return HttpResult.success(money);
    }

    private int restMoney(Long redPacketId, Long restPeople) {
        Integer restMoney = Integer.parseInt(redisUtil.get(redPacketId + "-money"));
        Random random = new Random();
        //随机范围：[1,剩余人均金额的两倍]
        return random.nextInt((int) (restMoney / (restPeople + 1) * 2 - 1)) + 1;
    }

    void saveRecord(Integer money, Long redPacketId, Integer userId) {
        RedRacketRecordDO record = new RedRacketRecordDO();
        record.setAmount(money);
        record.setRedPacketId(redPacketId);
        record.setUid(userId);
        record.setCreateTime(new Timestamp(System.currentTimeMillis()));
        saveRecord(record);
    }

    @Async
    void saveRecord(RedRacketRecordDO record) {
        redPacketRecordMapper.insert(record);
    }
}

