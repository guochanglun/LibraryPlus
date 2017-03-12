package com.gcl.library.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by gcl on 2017/3/8.
 */

public class DateFormatUtil {

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public static String getCurrentDate() {
        Date data = new Date();
        return format.format(data);
    }
}
