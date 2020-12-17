# CloudPlay
nacos的简单实践

# Snowflake with nacos
使用nacos获得WorkerId实现雪花算法
https://github.com/alibaba/nacos/issues/4467
在 1.4.1 之前 再次 register 同样的 ip:port 会得到一个新的instanceId
暂时没有考虑太多极端情况，例如掉线之类的问题

参考：
[美团Leaf](https://github.com/Meituan-Dianping/Leaf)、
[不知名大佬](https://github.com/yangq918/health-cloud)

感谢：[一位贡献nacos的大佬](https://github.com/horizonzy)
