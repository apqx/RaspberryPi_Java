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
    private Socket socket;
    private int devices;
    private RaspberryPi raspberryPi;
    private PrintStream printStream;
    private BufferedReader bufferedReader;
    private Thread currentThread;
    private Thread checkThread;
    Communicate(Socket socket,RaspberryPi raspberryPi,int devices){
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
                        Runtime.getRuntime().exec("shutdown -h now");
                        System.out.println("Shutdown from remote controller");
                        break;
                    case RaspberryAction.CHECK:
                        sendText(RaspberryAction.CHECK_BACK);
                        break;
                    case RaspberryAction.CHECK_BACK:
                        check=true;
                        break;
                }
            }
        }catch (IOException e){
            raspberryPi.stop();
            if (!isStopCommunicate){
                stopCommunicate();
            }
        }
    }
    private void stopCommunicate(){
        isStopCommunicate=true;
        System.out.println("Device "+devices+" offline!");
        raspberryPi.stop();
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
}
