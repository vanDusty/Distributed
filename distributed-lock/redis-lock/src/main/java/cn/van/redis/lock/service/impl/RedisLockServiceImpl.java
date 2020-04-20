package cn.van.redis.lock.service.impl;

import cn.van.redis.lock.entity.FamilyRewardRecordDO;
import cn.van.redis.lock.mapper.FamilyRewardRecordMapper;
import cn.van.redis.lock.service.RedisLockService;
import cn.van.redis.lock.util.HttpResult;
import cn.van.redis.lock.util.RedisDistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
public class RedisLockServiceImpl implements RedisLockService {

    @Resource
    RedisDistributedLock redisLock;

    @Resource
    FamilyRewardRecordMapper familyRewardRecordMapper;



    @Override
    public HttpResult receiveAward() {
        // 模拟 familyId = 1 的家庭同时领取奖励
        Long familyId = 1L;
        Map<String, Object> params = new HashMap<String, Object>(16);
        params.put("familyId", familyId);
        params.put("rewardType", 1);
        int count = familyRewardRecordMapper.selectCountByFamilyIdAndRewardType(params);
        if (count == 0) {
            // 没有记录则创建领取记录
            FamilyRewardRecordDO recordDO = new FamilyRewardRecordDO(familyId,1,0,LocalDateTime.now());
            int num = familyRewardRecordMapper.insert(recordDO);
            if (num == 1) {
                return HttpResult.success();
            }
            return HttpResult.failure(-1, "记录插入失败");
        }
        return HttpResult.success("该记录已存在");
    }

    @Override
    public HttpResult receiveAwardLock() {
        // 模拟 familyId = 2 的家庭同时领取奖励
        Long familyId = 2L;
        Map<String, Object> params = new HashMap<String, Object>(16);
        params.put("familyId", familyId);
        params.put("rewardType", 1);
        int count = familyRewardRecordMapper.selectCountByFamilyIdAndRewardType(params);
        if (count == 0) {
            // 没有记录则创建领取记录
            FamilyRewardRecordDO recordDO = new FamilyRewardRecordDO(familyId,1,0,LocalDateTime.now());
            // 分布式锁的key(familyId + rewardType)
            String lockKey = recordDO.getFamilyId() + "_" + recordDO.getRewardType();
            // 分布式锁的value(唯一值)
            String lockValue = createUUID();
            boolean lockStatus = redisLock.tryLock(lockKey, lockValue);
            // 锁被占用
            if (!lockStatus) {
                log.info("锁已经占用了");
                return HttpResult.failure(-1,"失败");
            }
            // 不管多个请求，加锁之后，只会有一个请求能拿到锁，进行插入操作
            log.info("拿到了锁，当前时刻:{}",System.currentTimeMillis());

            int num = familyRewardRecordMapper.insert(recordDO);
            if (num != 1) {
                log.info("数据插入失败！");
                return HttpResult.failure(-1, "数据插入失败！");
            }
            log.info("数据插入成功！准备解锁...");
            boolean unLockState = redisLock.unLock(lockKey,lockValue);
            if (!unLockState) {
                log.info("解锁失败！");
                return HttpResult.failure(-1, "解锁失败！");
            }
            log.info("解锁成功！");
            return HttpResult.success();
        }
        log.info("该记录已存在");
        return HttpResult.success("该记录已存在");
    }

    private String createUUID() {
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString().replace("-", "_");
        return str;
    }

}
