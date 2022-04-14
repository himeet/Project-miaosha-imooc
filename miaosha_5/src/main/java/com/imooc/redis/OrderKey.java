package com.imooc.redis;

public class OrderKey extends BasePrefix{
    public OrderKey(String predix) {
        super(predix);
    }

    public static OrderKey getMiaoshaOrderByUidGid = new OrderKey("miaosha_uid_gid-");
}
