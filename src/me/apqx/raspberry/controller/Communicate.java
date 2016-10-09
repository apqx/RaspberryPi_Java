package me.apqx.raspberry.controller;

import me.apqx.raspberry.server.RaspberryAction;
import me.apqx.raspberry.server.Util;

import javax.swing.*;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Calendar;
import java.util.Scanner;

/**
 * Created by chang on 2016/10/8.
 */
public class Communicate {
    private Socket socket;
    private Scanner scanner;
    private PrintStream printStream;
    private Controller controller;
    private Thread checkThread;
    //心跳包标志位
    private boolean check;
    public Communicate(Controller controller){
        this.controller=controller;
    }
    public void start(){
        try {
            socket=new Socket("192.168.0.1",1335);
            scanner=new Scanner(socket.getInputStream());
            printStream=new PrintStream(socket.getOutputStream());
            controller.setConnectState(true);
            receiveAndPrint();
            checkConnection();
        } catch (IOException e) {
//            e.printStackTrace();
            //此处弹出无法连接的弹窗
            JOptionPane.showMessageDialog(controller,"Connect failed");
        }
    }
    public void receiveAndPrint(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String string;
                try {
                    while ((string=scanner.nextLine())!=null){
                        switch (string){
                            case RaspberryAction.CHECK:
                                sendText(RaspberryAction.CHECK_BACK);
                                break;
                            case RaspberryAction.CHECK_BACK:
                                check=true;
                                break;
                        }
                    }
                }catch (Exception e){

                }
            }
        },"Thread-startCommunicate").start();
    }
    //定时发送心跳包判断连接是否断开
    private void checkConnection(){
        new Thread(new Runnable() {
            private int time;
            private int currentTime;
            @Override
            public void run() {
                checkThread=Thread.currentThread();
                try {
                    while (true){
                        check=false;
                        sendText(RaspberryAction.CHECK);
                        time=Calendar.getInstance().get(Calendar.SECOND);
                        while (!check){
                            currentTime=Calendar.getInstance().get(Calendar.SECOND);
                            if (Util.isOverTime(time,currentTime,2)){
                                if (!socket.isClosed()){
                                    stop();
                                    System.out.println("心跳检测成功");
                                }
                                return;
                            }
                        }
                        checkThread.sleep(5000);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        },"Thread-checkConnection").start();

    }
    public void sendText(String string){
        if (socket!=null&&socket.isConnected()){
            printStream.println(string);
        }
    }
    //关闭连接并回收资源
    public void stop(){
        controller.setConnectState(false);
        scanner.close();
        printStream.close();
        try {
            if (!socket.isClosed()){
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
