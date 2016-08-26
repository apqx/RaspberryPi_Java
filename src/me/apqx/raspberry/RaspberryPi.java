package me.apqx.raspberry;

import com.pi4j.io.gpio.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**控制树莓派的主类
 * Created by chang on 2016/6/30.
 */
public class RaspberryPi {
    //多设备连接的客户端数
    private static int devices;
    private static boolean isFirstDevice=true;
    private final static GpioController gpio=GpioFactory.getInstance();
    private static RaspberryPi raspberryPi;
    //L298n in1
    private final static GpioPinDigitalOutput RIGHT_1=gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00,"right_1", PinState.LOW);
    //L298n in2
    private final static GpioPinDigitalOutput RIGHT_2=gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02,"right_2",PinState.LOW);
    //L298n in4
    private final static GpioPinDigitalOutput LEFT_1=gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04,"left_1",PinState.LOW);
    //L298n in3
    private final static GpioPinDigitalOutput LEFT_2=gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03,"left_2",PinState.LOW);
    //DS3218_HAND
    private final static GpioPinPwmOutput HAND_DS3218=gpio.provisionPwmOutputPin(RaspiPin.GPIO_26);
    //MG995_HAND
    private final static GpioPinPwmOutput HAND_MG995=gpio.provisionPwmOutputPin(RaspiPin.GPIO_23);
    //MG995_CAMERA
    private final static GpioPinPwmOutput CAMERA_MG995=gpio.provisionPwmOutputPin(RaspiPin.GPIO_24);
    public static void main(String[] args) {



        raspberryPi=new RaspberryPi();
        checkInput();
        start();
    }
    private static void start(){
        try {
            ServerSocket serverSocket=new ServerSocket(1335);
            if (isFirstDevice){
                System.out.println("Waiting for connection...");
                isFirstDevice=false;
            }
            while (true){
                devices++;
                Socket socket=serverSocket.accept();
                System.out.println("Device "+devices+" connected successfull!");
                multiDevices(socket,raspberryPi,devices);
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Build Server Failed!");
        }

    }
    //多客户端同时连接支持
    private static void multiDevices(Socket socket,RaspberryPi raspberryPi,int devices){
        new Thread(new Runnable() {
            @Override
            public void run() {
                new Communicate(socket,raspberryPi,devices);
            }
        }).start();
    }
    private static void checkInput(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Scanner scanner=new Scanner(System.in);
                while (scanner.hasNextLine()){
                    if (scanner.nextLine().equals("exit")){
                        gpio.shutdown();
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
    /**
     * 控制舵机
     * MG995：可360度旋转，pwm控制旋转速度和方向
     * pwm=70则舵机停转，70<pwm<94则逆时针旋转，46<pwm<70则顺时针旋转，距70偏移量越大则速度越快、力量越大
     * 67和74分别是顺时针和逆时针的最低速度，适用于Camera
     * DS3218：可180度旋转，pwm控制旋转到固定的角度
     * pwm=70则舵机处于中位，pwm=117则舵机相对中位逆时针旋转90度，pwm=23则舵机相对中位顺时针旋转90度
     */
    //HAND_MG995
    public void setHandMG995(int pwm){
        HAND_MG995.setPwm(pwm);
    }
    //CAMERA_MG995
    public void setCameraMG995(int pwm){
        CAMERA_MG995.setPwm(pwm);
    }
    //HAND_DS3218
    public void setHandDS3218(int pwm){
        HAND_DS3218.setPwm(pwm);
    }
    //HAND_MG995停止
    public void handMG995Stop(){
        setHandMG995(70);
    }
    //CAMERA_MG995停止
    public void cameraMG995Stop(){
        setCameraMG995(70);
    }
    //CAMERA_MG995慢速顺时针旋转
    public void cameraMG995SlowCW(){
        setCameraMG995(67);
    }
    //CAMERA_MG995慢速逆时针旋转
    public void cameraMG995SlowCCW(){
        setCameraMG995(74);
    }
}
