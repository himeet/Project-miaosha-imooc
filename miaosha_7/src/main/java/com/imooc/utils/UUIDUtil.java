package com.imooc.utils;

import java.util.UUID;

public class UUIDUtil {

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");  //  原生的uuid是带有"-"的，replace的目的是去掉"-"
    }

}
