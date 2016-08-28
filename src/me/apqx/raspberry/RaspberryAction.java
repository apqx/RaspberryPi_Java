package me.apqx.raspberry;

/**
 * Created by chang on 2016/6/30.
 */
public class RaspberryAction {
    public static final String FORWARD="forward";
    public static final String BACK="back";
    public static final String TURN_LEFT="turnLeft";
    public static final String TURN_RIGHT="turnRight";
    public static final String STOP="stop";
    public static final String EXIT="exit";
    public static final String SHUTDOWN="shutdown";
    public static final String CHECK="check";
    public static final String CHECK_BACK="checkBack";
    public static final String CAMERA_ON="turnOnCamera";
    public static final String CAMERA_OFF="turnOffCamera";
    //SERVO_DS3218顺时针
    public static final String SERVO_DS3218_CW="ds3218CW";
    //SERVO_DS3218逆时针
    public static final String SERVO_DS3218_CCW="ds3218CCW";
    //SERVO_DS3218停止
    public static final String SERVO_DS3218_STOP="ds3218Stop";
    //SERVO_MG995_HAND顺时针
    public static final String SERVO_MG995_HAND_CW="mg995HandCW";
    //SERVO_MG995_HAND逆时针
    public static final String SERVO_MG995_HAND_CCW="mg995HandCCW";
    //SERVO_MG995_HAND停止
    public static final String SERVO_MG995_HAND_STOP="mg995HandStop";
    //SERVO_MG995_CAMERA顺时针
    public static final String SERVO_MG995_CAMERA_CW="mg995CamCW";
    //SERVO_MG995_CAMERA逆时针
    public static final String SERVO_MG995_CAMERA_CCW="mg995CamCCW";
    //SERVO_MG995_CAMERA停止
    public static final String SERVO_MG995_CAMERA_STOP="mg995CamStop";
    //发送照片之前发送确认指令
    public static final String SEND_PICTURE="sendPicture";
    //拍摄照片
    public static final String TAKE_PICTURE="takePicture";
    //照片传输结束标志
    public static final String SEND_PICTURE_OVER="sendPictureOver";
    //连接Android失败
    public static final String CONNECT_ANDROID_FAILED="connectAndroidFailed";
}
