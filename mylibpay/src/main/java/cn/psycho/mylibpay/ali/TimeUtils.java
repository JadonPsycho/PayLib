package cn.psycho.mylibpay.ali;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Psycho on 2018/4/4.
 */

public class TimeUtils {

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getCurrentTime(){
        Date date = new Date(System.currentTimeMillis());
        String str_time = sdf.format(date);
        return str_time;
    }
}
