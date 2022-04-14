package com.imooc.redis;

public class OrderKey extends BasePrefix{
    public OrderKey(int expireSeconds, String predix) {
        super(expireSeconds, predix);
    }
}
