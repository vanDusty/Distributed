package cn.van.redisson.lock.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Copyright (C), 2015-2019, 风尘博客
 * 公众号 : 风尘博客
 * FileName: RedisLockApplication
 *
 * @author: Van
 * Date:     2019-09-17 23:47
 * Description: 家庭奖励记录
 * Version： V1.0
 */
@Data
public class GoodDO {

    private Long id;

    private String goodName;

    private Integer goodCounts;

    private Date createTime;

    private static final long serialVersionUID = 1L;

    public GoodDO(Long id, String goodName, Integer goodCounts, Date createTime) {
        this.id = id;
        this.goodName = goodName;
        this.goodCounts = goodCounts;
        this.createTime = createTime;
    }

    public GoodDO() {
        super();
    }
}