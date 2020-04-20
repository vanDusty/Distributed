# 高并发下浏览量入库设计

> 文章浏览量统计，low的做法是：用户每次浏览，前端会发送一个`GET`请求获取一篇文章详情时，会把这篇文章的浏览量`+1`，存进数据库里。

### 技术交流

1. [风尘博客](https://www.dustyblog.cn/)
1. [风尘博客-博客园](https://www.cnblogs.com/vandusty)
1. [风尘博客-掘金](https://juejin.im/user/5d5ea68e6fb9a06afa328f56/posts)

公众号：

![风尘博客](https://camo.githubusercontent.com/f87b5554de0af5d0be7e814c122c7a7fa0d82dea/68747470733a2f2f692e6c6f6c692e6e65742f323031392f30382f31382f6a695842416f74386645575a6433702e706e67)