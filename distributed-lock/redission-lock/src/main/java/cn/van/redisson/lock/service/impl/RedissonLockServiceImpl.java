package cn.van.redisson.lock.service.impl;

import cn.van.redisson.lock.entity.GoodDO;
import cn.van.redisson.lock.mapper.GoodMapper;
import cn.van.redisson.lock.service.RedissonLockService;
import cn.van.redisson.lock.util.HttpResult;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (C), 2015-2019, 风尘博客
 * 公众号 : 风尘博客
 * FileName: RedisLockServiceImpl
 *
 * @author: Van
 * Date:     2019-09-17 22:42
 * Description: ${DESCRIPTION}
 * Version： V1.0
 */
@Service
@Slf4j
public class RedissonLockServiceImpl implements RedissonLockService {

    @Resource
    RedissonClient redissonClient;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    GoodMapper goodMapper;

    @Override
    public HttpResult saleGoods() {
        // 以指定goodId = 1：哇哈哈为例
        Long goodId = 1L;
        GoodDO goodDO = goodMapper.selectByPrimaryKey(goodId);
        int goodStock = goodDO.getGoodCounts();
        if (goodStock >= 1) {
            // 如果库存大于一，再卖一件
            goodMapper.saleOneGood(goodId);
        }
        return HttpResult.success();
    }

    @Override
    public HttpResult saleGoodsLock() {
        // 以指定goodId = 2：卫龙为例
        Long goodId = 2L;
        GoodDO goodDO = goodMapper.selectByPrimaryKey(goodId);
        int goodStock = goodDO.getGoodCounts();
        String key = goodDO.getGoodName();
        log.info("{}剩余总库存,{}件", key,goodStock);
        // 将商品的实时库存放在redis 中，便于读取
        stringRedisTemplate.opsForValue().set(key, Integer.toString(goodStock));

        // redisson 锁 的key
        String lockKey = goodDO.getId() +"_" + key;
        RLock lock = redissonClient.getLock(lockKey);
        // 设置60秒自动释放锁  （默认是30秒自动过期）
        lock.lock(60, TimeUnit.SECONDS);
        // 此步开始，串行销售
        int stock = Integer.parseInt(stringRedisTemplate.opsForValue().get(key));
        // 如果缓存中库存量大于1，可以继续销售
        if (stock >= 1) {
            goodDO.setGoodCounts(stock - 1);
            int num = goodMapper.saleOneGood(goodId);
            if (num == 1) {
                // 减库存成功，将缓存同步
                stringRedisTemplate.opsForValue().set(key,Integer.toString((stock-1)));
            }
            log.info("{},当前库存,{}件", key,stock);
        }
        lock.unlock();
        return HttpResult.success();
    }
}
