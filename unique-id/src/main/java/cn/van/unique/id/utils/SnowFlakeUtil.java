package cn.van.unique.id.utils;

/**
 * Copyright (C), 2015-2019, 风尘博客
 * 公众号 : 风尘博客
 * FileName: SnowFlakeUtil
 *
 * @author: Van
 * Date:     2019-12-01 20:15
 * Description: 雪花ID工具类
 * Version： V1.0
 */
public class SnowFlakeUtil {
    /**
     * 起始的时间戳:这个时间戳自己随意获取，比如自己代码的时间戳
     */
    private final static long START_TIMESTAMP = 1288834974657L;

    // 每一部分占用的位数
    /**
     * 序列号占用的位数
     */
    private final static long SEQUENCE_BIT = 12;
    /**
     * 机器标识占用的位数
     */
    private final static long MACHINE_BIT = 5;
    /**
     * 数据中心占用的位数
     */
    private final static long DATA_CENTER_BIT = 5;

    // 每一部分的最大值：先进行左移运算，再同-1进行异或运算；异或：相同位置相同结果为0，不同结果为1
    /**
     * 用位运算计算出最大支持的数据中心数量：31
     */
    private final static long MAX_DATACENTER_NUM = -1L ^ (-1L << DATA_CENTER_BIT);

    /**
     * 用位运算计算出最大支持的机器数量：31
     */
    private final static long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);

    /**
     * 用位运算计算出12位能存储的最大正整数：4095
     */
    private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);

    // 每一部分向左的位移
    /**
     * 机器标志较序列号的偏移量
     */
    private final static long MACHINE_LEFT = SEQUENCE_BIT;

    /**
     * 数据中心较机器标志的偏移量
     */
    private final static long DATA_CENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;

    /**
     * 时间戳较数据中心的偏移量
     */
    private final static long TIMESTAMP_LEFT = DATA_CENTER_LEFT + DATA_CENTER_BIT;

    /**
     * 数据中心
     */
    private static long datacenterId;
    /**
     * 机器标识
     */
    private static long machineId;
    /**
     * 序列号
     */
    private static long sequence = 0L;
    /**
     * 上一次时间戳
     */
    private static long lastStamp = -1L;

    /**
     * 此处无参构造私有，同时没有给出有参构造，在于避免以下两点问题：
     * 1、私有化避免了通过new的方式进行调用，主要是解决了在for循环中通过new的方式调用产生的id不一定唯一问题问题，因为用于			 记录上一次时间戳的lastStmp永远无法得到比对；
     * 2、没有给出有参构造在第一点的基础上考虑了一套分布式系统产生的唯一序列号应该是基于相同的参数
     */
    private SnowFlakeUtil() {

    }

    /**
     * 获得下一个ID(该方法是线程安全的)
     *
     * @return
     */
    public static synchronized long nextId() {
        /** 获取当前时间戳 */
        long currStamp = timeGen();

        /** 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过，这时候应当抛出异常 */
         if (currStamp < lastStamp) {
            throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
        }
        /** 如果是同一时间（相同毫秒内）生成的，则进行毫秒内序列 */
        if (currStamp == lastStamp) {
            //相同毫秒内，序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            //同一毫秒的序列数已经达到最大
            if (sequence == 0L) {
                /** 阻塞到下一个毫秒,获得新的时间戳赋值给当前时间戳 */
                currStamp = tilNextMillis();
            }
        } else {
            //时间戳改变，毫秒内序列号置为0
            sequence = 0L;
        }
        /** 当前时间戳存档记录，用于下次产生id时对比是否为相同时间戳 */
        lastStamp = currStamp;

        // 移位并通过或运算拼到一起组成64位的ID
        //时间戳部分
        return (currStamp - START_TIMESTAMP) << TIMESTAMP_LEFT
                // 数据中心部分
                | datacenterId << DATA_CENTER_LEFT
                // 机器标识部分
                | machineId << MACHINE_LEFT
                // 序列号部分
                | sequence;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @return 当前时间戳
     */
    private static long tilNextMillis() {
        long timestamp = timeGen();
        while (timestamp <= lastStamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private static long timeGen() {
        return System.currentTimeMillis();
    }

}
