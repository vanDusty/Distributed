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
    public HttpResult saleGoods(Long goodId) {
        GoodDO goodDO = goodMapper.selectByPrimaryKey(goodId);
        int goodStock = goodDO.getGoodCounts();
        if (goodStock >= 1) {
            // 如果库存大于一，再卖一件
            goodMapper.saleOneGood(goodId);
        }
        return HttpResult.success();
    }

    @Override
    public HttpResult saleGoodsLock(Long goodId) {
        GoodDO goodDO = goodMapper.selectByPrimaryKey(goodId);
        int goodStock = goodDO.getGoodCounts();

        String key = goodDO.getGoodName();
        log.info("{}剩余总库存,{}件", key,goodStock);
        // 将商品的实时库存放在redis 中，便于读取
        stringRedisTemplate.opsForValue().set(key, Integer.toString(goodStock));
        // redisson 锁 的key
        String lockKey = goodDO.getId() + "_" + key;
        RLock lock = null;
        try {
            //获取Lock锁，设置锁的名称
            lock = redissonClient.getLock(lockKey);
            // 此步开始，串行执行
            // 设置60秒自动释放锁  （默认是30秒自动过期）
//            lock.lock(60, TimeUnit.SECONDS);
            boolean res = lock.tryLock(100, 10, TimeUnit.SECONDS);
            int stock = Integer.parseInt(stringRedisTemplate.opsForValue().get(key));
            if (stock >= 1) {
                if (res) {
                    log.info( "======获得锁后进行售卖一件======");
                    goodMapper.saleOneGood(goodId);
                    // 减库存成功，将缓存同步
                    stringRedisTemplate.opsForValue().set(key,Integer.toString((stock-1)));
                } else {
                    log.info( "======锁被占用======");
                }
                log.info("{}当前库存:{}件", key,stock);
            }
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            //释放锁
            if (lock != null) {
                log.info("释放锁，lockKey:{}", lockKey);
                lock.unlock();
            }
        }
        return HttpResult.success();
    }
}
