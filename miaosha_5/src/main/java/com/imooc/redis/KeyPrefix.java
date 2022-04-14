package com.imooc.redis;

public interface KeyPrefix {

    public int expireSeconds();

    public String getPredix();

}
