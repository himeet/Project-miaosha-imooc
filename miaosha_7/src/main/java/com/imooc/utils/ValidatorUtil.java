package com.imooc.utils;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatorUtil {

    private static final Pattern MOBILE_PATTERN = Pattern.compile("1\\d{10}");

    public static boolean isMobile(String mobile) {
        if (StringUtils.isEmpty(mobile)) {
            return false;
        }
        Matcher m = MOBILE_PATTERN.matcher(mobile);

        return m.matches();
    }

    public static void main(String[] args) {
        System.out.println(isMobile("13789009099"));
        System.out.println(isMobile("1378900909"));
    }

}
