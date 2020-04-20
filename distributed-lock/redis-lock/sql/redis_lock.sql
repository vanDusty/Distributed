CREATE TABLE `family_reward_record` (
                                      `id` bigint(10) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                      `family_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '家庭id',
                                      `reward_type` int(10) NOT NULL DEFAULT '1' COMMENT '奖励类型',
                                      `state` int(1) NOT NULL DEFAULT '0' COMMENT '状态',
                                      `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                      PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=270 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='家庭领取奖励表（家庭内多人只能有一个人能领取成功，不能重复领取）';