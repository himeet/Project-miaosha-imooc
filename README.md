## 项目介绍👑
- 项目来源：该项目来源于慕课网的实战视频 《L019-Java秒杀系统方案优化，掌握海量访问通用解决方案（旧版本）》
- 项目类型：该项目为「Project」项目，「Project」项目为制作一个成品的软件项目
- 项目主要内容：通过「秒杀」这一业务场景，提供针对于高并发场景下的通用系统设计思想

## 运行环境🕹
- jdk 1.8
- Redis 6.2.6
- RabbitMQ 3.9.14
- MySQL 5.7.36
- SpringBoot 1.5.9.RELEASE

## 导航🧭
### miaosha_1
本模块中包含的知识点：
- Spring Boot集成Thymeleaf
- Spring Boot集成Mybatis（注解形式写sql）
- Spring Boot集成Druid连接池
- Spring Boot集成Redis（借助Jedis）
- 封装Redis
  - 封装Redis储存对象的Key前缀「com.imooc.redis.KeyPrefix」和「com.imooc.redis.BasePrefix」
  - 封装Redis操作类「com.imooc.redis.RedisService」
- 封装通用错误码「com.imooc.result.CodeMsg」
- 封装通用返回结果「com.imooc.result.Result」

### miaosha_2
本模块中包含的知识点：
- 实现用户登录功能
  - 明文密码两次MD5处理
  - JSR303参数校验
    - 自定义注解「com.imooc.validator.IsMobile」以及注解所对应的validator「com.imooc.validator.IsMobileValidator」
  - 借助Redis实现分布式session
- 封装全局异常
  - 自定义业务异常类「com.imooc.exception.GlobalException」
  - 封装全局异常处理器「com.imooc.exception.GlobalExceptionHandler」

### miaosha_3
本模块中包含的知识点：
- 实现商品列表页的开发
- 实现商品详情页的开发
- 实现秒杀功能的开发
- 实现订单详情页的开发

### miaosha_4
本模块中包含的知识点：
- Jmeter压测
  - 使用Jmeter图形界面对接口进行压测
  - Jmeter自定义变量，模拟多用户
  - 使用Jmeter命令行对服务器上的项目接口进行压测
- Redis压测
- Spring Boot导出war包

### miaosha_5
本模块中包含的知识点：
- 页面缓存
- URL缓存
- 对象缓存
- 页面静态化
- 解决「卖超」问题
  - 在「减少库存」的操作中，update的SQL语句加上判断条件「stock_count > 0」
  - 在「创建订单」的操作中，为了防止同一个人在短时间内提交了2个请求，在数据库层面为「user_id」和「goods_id」加上「唯一索引」，确保同一个人只能秒杀一件商品
- 静态资源优化
  - JS/CSS压缩
  - 多个JS/CSS组合
  - CDN就近访问

### miaosha_6
- 秒杀接口优化
  - Redis预减库存减少数据库访问
  - 内存标记从而减少Redis访问
  - 请求先入队缓冲，异步下单，增强用户体验（借助RabbitMQ）
- Spring Boot集成RabbitMQ
  - 四种交换机模式
- Ngnix
  - Ngnix配置讲解

### miaosha_7
- 隐藏秒杀接口的地址
- 数学公式验证码
  - 使用ScriptEngine验证验证码
- 接口限流防刷
  - 使用拦截器实现通用的接口限流