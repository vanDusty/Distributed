# 高并发限流方案

## 限流
服务限流作为一个核心的自保护机制，能够在高并发的情况下，其他机制都无法保障降级的情况下，保护系统不崩溃，以免产生雪崩效应。

常见的限流方案有：Guava的RateLimiter、基于分布式锁的令牌桶、Alibaba  Sentinel

## 场景

限流的需求出现在许多常见的场景中

秒杀活动，有人使用软件恶意刷单抢货，需要限流防止机器参与活动
某api被各式各样系统广泛调用，严重消耗网络、内存等资源，需要合理限流
淘宝获取ip所在城市接口、微信公众号识别微信用户等开发接口，免费提供给用户时需要限流，更具有实时性和准确性的接口需要付费。

1. [Springboot中使用redis进行api防刷限流](https://www.cnblogs.com/haixiang/p/12012728.html)
1. [Springboot项目的接口防刷](https://blog.csdn.net/weixin_42533856/article/details/82593123#commentBox)
1. [22222](https://www.cnblogs.com/carrychan/p/9435979.html)