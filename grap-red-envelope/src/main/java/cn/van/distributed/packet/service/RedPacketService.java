package cn.van.distributed.packet.service;


import cn.van.distributed.packet.util.HttpResult;
import cn.van.distributed.packet.util.Result;

/**
 * Copyright (C), 2015-2020, 风尘博客
 * 公众号 : 风尘博客
 * FileName: RedPacketServiceImpl
 *
 * @author: Van
 * Date:     2020-02-09 22:22
 * Description: ${DESCRIPTION}
 * Version： V1.0
 */
public interface RedPacketService {
    /**
     * 抢红包业务实现
     * @param redPacketId 红包ID
     * @param userId 用户ID
     * @return
     */
    HttpResult startSpike(Long redPacketId, Integer userId);

    /**
     * 微信抢红包业务实现
     * @param redPacketId
     * @param userId
     * @return
     */
    HttpResult startTwoSeckil(Long redPacketId, Integer userId);
}
