package cn.van.redisson.lock.service;

import cn.van.redisson.lock.util.HttpResult;

/**
 * Copyright (C), 2015-2019, 风尘博客
 * 公众号 : 风尘博客
 * FileName: RedisLockService
 *
 * @author: Van
 * Date:     2019-09-17 19:41
 * Description: ${DESCRIPTION}
 * Version： V1.0
 */
public interface RedissonLockService {
    /**
     * 售卖商品（加锁）
     * @return
     */
    HttpResult saleGoods(Long goodId);


    /**
     * 售卖商品（加锁）
     * @return
     */
    HttpResult saleGoodsLock(Long goodId);

}
