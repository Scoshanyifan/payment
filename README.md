# payment

支付模块
- 流水记录表

订单模块
- 订单表
- 订单明细表
- 用户表
- 商品表
- 商户表
    
    
下单支付流程：
1. 用户下单
    1. 创建订单
    2. mq通知支付模块
2. 发起支付
    1. 查询订单，调用sdk
        1. 支付宝
        2. 微信
        3. 银行
    2. 返回支付信息
3. 前端支付
    1. APP
    2. H5
4. 监听支付回调
    1. 查询订单，记录流水
    2. mq通知订单模块
5. 更新订单状态
``