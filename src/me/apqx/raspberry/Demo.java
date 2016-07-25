package me.apqx.raspberry;

import com.pi4j.io.gpio.*;

/**
 * Created by chang on 2016/6/30.
 */
public class Demo {
    public static final GpioController gpio= GpioFactory.getInstance();
    public static GpioPinDigitalOutput right_1=gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00,"right_1",PinState.LOW);
    public static GpioPinDigitalOutput right_2=gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01,"right_2",PinState.LOW);
    public static GpioPinDigitalOutput left_1=gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02,"left_1",PinState.LOW);
    public static GpioPinDigitalOutput left_2=gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03,"left_2",PinState.LOW);
    public static void main(String[] args) throws InterruptedException {
        System.out.println("high");
        right_1.high();
        left_1.high();
        Thread.sleep(1000);
        System.out.println("low");
        right_1.low();
        left_1.low();
        Thread.sleep(1000);
        System.out.println("high");
        right_2.high();
        left_2.high();
        Thread.sleep(1000);
        System.out.println("low");
        right_2.low();
        left_2.low();
    }
}
