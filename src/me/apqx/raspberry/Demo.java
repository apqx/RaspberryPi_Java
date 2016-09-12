package me.apqx.raspberry;

import com.pi4j.io.gpio.*;


/**
 * Created by chang on 2016/9/11.
 */
public class Demo {

    public static void main(String[] args)throws Exception{
        GpioController gpio=GpioFactory.getInstance();
        //L298n in1
        GpioPinDigitalOutput RIGHT_1=gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00,"right_1", PinState.LOW);
        //L298n in2
        GpioPinDigitalOutput RIGHT_2=gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02,"right_2",PinState.LOW);
        //L298n in4
        GpioPinDigitalOutput LEFT_1=gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04,"left_1",PinState.LOW);
        //L298n in3
        GpioPinDigitalOutput LEFT_2=gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03,"left_2",PinState.LOW);

        //设置in1为高电平，in2为低电平
        RIGHT_1.high();
        RIGHT_2.low();
        //延时2秒钟
        Thread.currentThread().sleep(2000);
        //设置in1为低电平，in2为低电平
        RIGHT_1.low();
        RIGHT_2.low();
        //设置in3为高电平，in4为低电平
        LEFT_2.high();
        LEFT_1.low();
        //延时2秒钟
        Thread.currentThread().sleep(2000);
        //设置in3为低电平，in4为低电平
        LEFT_2.low();
        LEFT_1.low();
        gpio.shutdown();
    }

}
