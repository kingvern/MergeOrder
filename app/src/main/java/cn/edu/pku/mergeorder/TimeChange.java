package cn.edu.pku.mergeorder;

import android.net.ParseException;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kingvern on 17/12/19.
 */

public class TimeChange {

    /*
* 将时分秒转为秒数
* */
    public static long minuteTurnSecond(String time) {
        String s = time;
        int index = s.indexOf(":");
        int mi = Integer.parseInt(s.substring(0, index));
        int ss = Integer.parseInt(s.substring(index + 1));

        Log.d("TAG", "formatTurnSecond: 时间== " + mi * 60 + ss);
        return  mi * 60 + ss;
    }

    /*
        * 将秒数转为时分秒
        * */
    public static String secondTurnMinute(int second) {

        int d = 0;
        int s = 0;

            d = second / 60;
            if (second % 60 != 0) {
                s = second % 60;
            }

        return  d + ":" + s + "";
    }


}
