package me.apqx.raspberry.server;

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
}
