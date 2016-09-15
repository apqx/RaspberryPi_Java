package me.apqx.raspberry;

import com.pi4j.io.gpio.*;


/**
 * Created by chang on 2016/9/11.
 * 此程序中的PWM值仅适用于DS3218舵机，其他型号舵机可以自行尝试PWM值
 * DS3218：可180度旋转，pwm控制旋转到固定的角度
 * pwm=70则舵机处于中位，pwm=117则舵机相对中位逆时针旋转90度，pwm=23则舵机相对中位顺时针旋转90度
 */
public class Demo {
    private static GpioPinPwmOutput DS3218_1;
    private static GpioPinPwmOutput DS3218_2;
    private static GpioController gpio;
    public static void main(String[] args)throws Exception{
        gpio=GpioFactory.getInstance();
        initServo();
        //DS3218
        DS3218_1=gpio.provisionPwmOutputPin(RaspiPin.GPIO_26);
        DS3218_2=gpio.provisionPwmOutputPin(RaspiPin.GPIO_01);

        //DS3218_1输出前停止DS3218_2输出
        stopDS3218_2();
        DS3218_1.setPwm(50);

        //延时1秒等待舵机到达指定角度
        Thread.currentThread().sleep(1000);

        //DS3218_2输出前停止DS3218_1输出，并恢复DS3812_2输出
        stopDS3218_1();
        startDS3218_2();
        DS3218_2.setPwm(50);

        //延时1秒等待舵机到达指定角度
        Thread.currentThread().sleep(1000);

        gpio.shutdown();

    }
    //停止DS3218_1输出
    public static void stopDS3218_1(){
        DS3218_1.unexport();
        gpio.unprovisionPin(DS3218_1);
    }
    //恢复DS3218_1输出
    public static void startDS3218_1(){
        DS3218_1=gpio.provisionPwmOutputPin(RaspiPin.GPIO_26);
        initServo();

    }
    //停止DS3218_2输出
    public static void stopDS3218_2(){
        DS3218_2.unexport();
        gpio.unprovisionPin(DS3218_2);
    }
    //恢复DS3218_2输出
    public static void startDS3218_2(){
        DS3218_2=gpio.provisionPwmOutputPin(RaspiPin.GPIO_01);
        initServo();

    }
    private static void initServo(){
        com.pi4j.wiringpi.Gpio.pwmSetMode(com.pi4j.wiringpi.Gpio.PWM_MODE_MS);
        com.pi4j.wiringpi.Gpio.pwmSetRange(1000);
        com.pi4j.wiringpi.Gpio.pwmSetClock(400);
    }

}
