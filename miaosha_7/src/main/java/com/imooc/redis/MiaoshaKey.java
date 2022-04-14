package com.imooc.redis;

public class MiaoshaKey extends BasePrefix{

    private MiaoshaKey(int expireSeconds, String predix) {
        super(expireSeconds, predix);
    }

    public static MiaoshaKey isGoodsOver = new MiaoshaKey(0, "go");
    public static MiaoshaKey getMiaoshaPath = new MiaoshaKey(60, "mp");
    public static MiaoshaKey getMiaoshaVerifyCode = new MiaoshaKey(300, "vc");
}
