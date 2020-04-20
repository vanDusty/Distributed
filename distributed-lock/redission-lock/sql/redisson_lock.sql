CREATE TABLE `good` (
                      `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                      `good_name` varchar(255) NOT NULL COMMENT '商品名称',
                      `good_counts` int(255) NOT NULL COMMENT '商品库存',
                      `create_time` timestamp NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
                      PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

INSERT INTO `good` VALUES (1, '哇哈哈', 5, '2019-09-20 17:39:04');
INSERT INTO `good` VALUES (2, '卫龙', 5, '2019-09-20 17:39:06');