package com.imooc.redis;

public class GoodsKey extends BasePrefix{


    private GoodsKey(int expireSeconds, String predix) {
        super(expireSeconds, predix);
    }

    // 页面缓存的过期时间设置为60秒
    public static GoodsKey getGoodsList = new GoodsKey(60, "gl");

    public static GoodsKey getGoodsDetail = new GoodsKey(60, "gd");

}
