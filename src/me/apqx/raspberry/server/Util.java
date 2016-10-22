package me.apqx.raspberry.server;

import java.util.Calendar;

/**
 * Created by chang on 2016/10/9.
 */
public class Util {
    //判断时间是否大于指定的时间秒
    public static boolean isOverTime(int time1,int time2,int time){
        if (time2>=time1&&time2<60){
            if ((time2-time1)<time){
                return false;
            }else {
                return true;
            }
        }else {
            if ((time2+60-time1)<time){
                return false;
            }else {
                return true;
            }
        }
    }
    //获取时间作为照片文件名
    public static String getPictureName(){
        Calendar calendar=Calendar.getInstance();
        String string=calendar.get(Calendar.YEAR)+"-"+calendar.get(Calendar.MONTH)+"-"+calendar.get(Calendar.DAY_OF_MONTH)+"-"+calendar.get(Calendar.HOUR_OF_DAY)+"-"+calendar.get(Calendar.MINUTE)+"-"+calendar.get(Calendar.SECOND)+".jpg";
        return string;
    }
}
