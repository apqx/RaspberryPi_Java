package me.apqx.raspberry;

import com.pi4j.io.gpio.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by chang on 2016/6/30.
 */
public class RaspberryPi {
    private static final GpioController GPIO= GpioFactory.getInstance();
    private static RaspberryPi raspberryPi;
    private static final GpioPinDigitalOutput RIGHT_1=GPIO.provisionDigitalOutputPin(RaspiPin.GPIO_00,"right_1", PinState.LOW);
    private static final GpioPinDigitalOutput RIGHT_2=GPIO.provisionDigitalOutputPin(RaspiPin.GPIO_01,"right_2",PinState.LOW);
    private static final GpioPinDigitalOutput LEFT_1=GPIO.provisionDigitalOutputPin(RaspiPin.GPIO_03,"left_1",PinState.LOW);
    private static final GpioPinDigitalOutput LEFT_2=GPIO.provisionDigitalOutputPin(RaspiPin.GPIO_02,"left_2",PinState.LOW);
    public static void main(String[] args) {
        raspberryPi=new RaspberryPi();
        CheckInput();
        start();
    }
    private static void start(){
        try {
            ServerSocket serverSocket=new ServerSocket(1335);
            System.out.println("Waiting for connection...");
            Socket socket=serverSocket.accept();
            System.out.println("Connected Successfull!");
            new Communicate(socket,raspberryPi);
            serverSocket.close();
            start();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Build Server Failed!");
        }

    }
    private static void CheckInput(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Scanner scanner=new Scanner(System.in);
                while (scanner.hasNextLine()){
                    if (scanner.nextLine().equals("exit")){
                        GPIO.shutdown();
                        System.exit(0);
                    }
                }
            }
        }).start();
    }

    //假设RIGHT_1高为向前，RIGHT_2高为向后
    private void rightRunForward(){
        RIGHT_1.high();
        RIGHT_2.low();
    }
    private void rightRunBack(){
        RIGHT_1.low();
        RIGHT_2.high();
    }
    //假设LEFT_1高为向前，LEFT_2高为向后
    private void leftRunForward(){
        LEFT_1.high();
        LEFT_2.low();
    }
    private void leftRunBack(){
        LEFT_1.low();
        LEFT_2.high();
    }
    private void rightStop(){
        if (RIGHT_1.getState()==PinState.HIGH){
            RIGHT_1.low();
        }
        if (RIGHT_2.getState()==PinState.HIGH){
            RIGHT_2.low();
        }
    }
    private void leftStop(){
        if (LEFT_1.getState()==PinState.HIGH){
            LEFT_1.low();
        }
        if (LEFT_2.getState()==PinState.HIGH){
            LEFT_2.low();
        }
    }
    //前进
    public void forward(){
        rightRunForward();
        leftRunForward();
    }
    //后退
    public void back(){
        rightRunBack();
        leftRunBack();
    }
    //右转
    public void turnRight(){
        rightRunBack();
        leftRunForward();
    }
    //左转
    public void turnLeft(){
        rightRunForward();
        leftRunBack();
    }
    //停止
    public void stop(){
        rightStop();
        leftStop();
    }
}
