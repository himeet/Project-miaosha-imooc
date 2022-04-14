package com.imooc.redis;

public class UserKey extends BasePrefix{

    private UserKey(String predix) {
        super(predix);
    }

    private UserKey(int expireSeconds, String predix) {
        super(expireSeconds, predix);
    }

    public static UserKey getById = new UserKey("id");
    public static UserKey getByName = new UserKey("name");
}
