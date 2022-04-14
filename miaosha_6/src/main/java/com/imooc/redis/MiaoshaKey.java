package com.imooc.redis;

public class MiaoshaKey extends BasePrefix{

    private MiaoshaKey(String predix) {
        super(predix);
    }

    public static MiaoshaKey isGoodsOver = new MiaoshaKey("go");
}
