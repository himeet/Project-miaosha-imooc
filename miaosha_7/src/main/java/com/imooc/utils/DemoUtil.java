package com.imooc.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DemoUtil {

    public static String converLongTimeToStr(long time) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);

        return formatter.format(calendar.getTime());
    }
}
