package me.apqx.raspberry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Calendar;

/**用于建立网络连接的类
 * Created by chang on 2016/6/30.
 */
public class Communicate {
    private boolean isStopCommunicate;
    private boolean check;
    private boolean isDS3218Stop;
    private boolean isHandMG995Stop;
    private int valueOfDS3218=70;
    private Socket socket;
    private int devices;
    private RaspberryPi raspberryPi;
    private PrintStream printStream;
    private BufferedReader bufferedReader;
    private Thread currentThread;
    private Thread checkThread;
    Communicate(Socket socket,RaspberryPi raspberryPi,int devices){
        //设置pwm参数,之所以不再主程序里设置是考虑到此程序启动的过早，参数设置是无效的，故该在客户端连接时设置
        com.pi4j.wiringpi.Gpio.pwmSetMode(com.pi4j.wiringpi.Gpio.PWM_MODE_MS);
        com.pi4j.wiringpi.Gpio.pwmSetRange(1000);
        com.pi4j.wiringpi.Gpio.pwmSetClock(400);
        this.socket=socket;
        this.raspberryPi=raspberryPi;
        this.devices=devices;
        currentThread=Thread.currentThread();
        startCommunicate();
    }
    private void startCommunicate(){
        try {
            printStream=new PrintStream(socket.getOutputStream());
            bufferedReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            checkConnection();
            raspberryPi.setHandDS3218(valueOfDS3218);
            String string;
            while ((string=bufferedReader.readLine())!=null){
                switch (string){
                    case RaspberryAction.FORWARD:
                        raspberryPi.forward();
                        break;
                    case RaspberryAction.BACK:
                        raspberryPi.back();
                        break;
                    case RaspberryAction.TURN_RIGHT:
                        raspberryPi.turnRight();
                        break;
                    case RaspberryAction.TURN_LEFT:
                        raspberryPi.turnLeft();
                        break;
                    case RaspberryAction.STOP:
                        raspberryPi.stop();
                        break;
                    case RaspberryAction.EXIT:
                        stopCommunicate();
                        checkThread.interrupt();
                        return;
                    case RaspberryAction.SHUTDOWN:
                        stopCommunicate();
                        Thread.currentThread().sleep(500);
                        Runtime.getRuntime().exec("shutdown -h now");
                        System.out.println("Shutdown from remote controller");
                        break;
                    case RaspberryAction.CHECK:
                        sendText(RaspberryAction.CHECK_BACK);
                        break;
                    case RaspberryAction.CHECK_BACK:
                        check=true;
                        break;
                    case RaspberryAction.CAMERA_ON:
                        System.out.println("open camera");
                        //注意使用的是绝对路径
                        Runtime.getRuntime().exec("bash /home/pi/RaspberryPi/startCamera.sh");
                        break;
                    case RaspberryAction.CAMERA_OFF:
                        System.out.println("close camera");
                        Runtime.getRuntime().exec("sudo killall mjpg_streamer");
                        break;
                    case RaspberryAction.SERVO_DS3218_CW:
                        checkDS3218(RaspberryAction.SERVO_DS3218_CW);
                        break;
                    case RaspberryAction.SERVO_DS3218_CCW:
                        checkDS3218(RaspberryAction.SERVO_DS3218_CCW);
                        break;
                    case RaspberryAction.SERVO_DS3218_STOP:
                        isDS3218Stop=true;
                        break;
                    case RaspberryAction.SERVO_MG995_HAND_CW:
                        isHandMG995Stop=false;
                        raspberryPi.setHandMG995(46);
                        checkHandMg995IsOverTime();
                        break;
                    case RaspberryAction.SERVO_MG995_HAND_CCW:
                        isHandMG995Stop=false;
                        raspberryPi.setHandMG995(94);
                        checkHandMg995IsOverTime();
                        break;
                    case RaspberryAction.SERVO_MG995_HAND_STOP:
                        raspberryPi.handMG995Stop();
                        isHandMG995Stop=true;
                        break;
                    case RaspberryAction.SERVO_MG995_CAMERA_CW:
                        break;
                    case RaspberryAction.SERVO_MG995_CAMERA_CCW:
                        break;
                    case RaspberryAction.SERVO_MG995_CAMERA_STOP:
                        break;
                }
            }
        }catch (IOException e){
            raspberryPi.stop();
            if (!isStopCommunicate){
                stopCommunicate();
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    private void stopCommunicate(){
        isStopCommunicate=true;
        raspberryPi.setHandDS3218(70);
        raspberryPi.stop();
        System.out.println("Device "+devices+" offline!");
        //舵机归位
        try {
            printStream.close();
            bufferedReader.close();
            if (!socket.isClosed()){
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private void sendText(String string){
        printStream.println(string);
    }
    //每隔5秒发送一次心跳包，判断连接是否断开
    private void checkConnection(){
        new Thread(new Runnable() {
            private int time;
            @Override
            public void run() {
                try {
                    checkThread=Thread.currentThread();
                    while (true){
                        check=false;
                        sendText(RaspberryAction.CHECK);
                        time=Calendar.getInstance().get(Calendar.SECOND);

                        while (!check){
                            if (isOverTime(time,Calendar.getInstance().get(Calendar.SECOND))){
                                stopCommunicate();
                                currentThread.interrupt();
                                System.out.println("心跳检测成功");
                                return;
                            }
                        }
                        checkThread.sleep(5000);
                    }
                }catch (Exception e){
                    //e.printStackTrace();
                    //System.out.println("checkThread因为DISCONNECT关闭");
                }

            }
        }).start();
    }
    //判断时间是否大于2秒
    private boolean isOverTime(int time1,int time2){
        if (time2>=time1&&time2<60){
            if ((time2-time1)<2){
                return false;
            }else {
                return true;
            }
        }else {
            if ((time2+60-time1)<2){
                return false;
            }else {
                return true;
            }
        }
    }
    //DS3218执行命令并判断是否停止
    private void checkDS3218(String order){
        if (order.equals(RaspberryAction.SERVO_DS3218_CCW)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!isDS3218Stop){
                        if (valueOfDS3218<100){
                            valueOfDS3218=valueOfDS3218+3;
                            raspberryPi.setHandDS3218(valueOfDS3218);
                            try {
                                Thread.currentThread().sleep(30);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                }
            },"Thread-DS3218_CCW").start();
        }else if (order.equals(RaspberryAction.SERVO_DS3218_CW)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!isDS3218Stop){
                        if (valueOfDS3218>23){
                            valueOfDS3218=valueOfDS3218-3;
                            raspberryPi.setHandDS3218(valueOfDS3218);
                            try {
                                Thread.currentThread().sleep(30);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                }
            },"Thread-DS3218_CW").start();
        }
        isDS3218Stop=false;
    }
    //检查HAND_MG995执行命令是否超时
    public void checkHandMg995IsOverTime(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                int time=Calendar.getInstance().get(Calendar.SECOND);
                while (!isHandMG995Stop){
                    if (isOverTime(time,Calendar.getInstance().get(Calendar.SECOND))){
                        raspberryPi.handMG995Stop();
                        break;
                    }
                }
            }
        },"checkHandMg995IsOverTime").start();
    }
}
