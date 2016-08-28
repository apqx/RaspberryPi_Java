package me.apqx.raspberry;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import com.sun.org.apache.xerces.internal.impl.xs.identity.Selector;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**用于建立网络连接的类
 * Created by chang on 2016/6/30.
 */
public class Communicate {
    private boolean isStopCommunicate;
    private boolean check;
    private boolean isDS3218Stop;
    private boolean isHandMG995Stop;
    //DS3218初始值70，使舵机处于中位
    private int valueOfDS3218=70;
    private Socket socket;
    private int devices;
    private RaspberryPi raspberryPi;
    private PrintStream printStream;
    private BufferedReader bufferedReader;
    private Thread currentThread;
    private Thread checkThread;
    private FileInputStream fileInputStream;
    private OutputStream outputStream;
    private File fileDir;
    //用于存储图片文件夹内的文件信息
    private ArrayList<File> arrayList;
    private int numOfPicture;
    //用于传输数据而不是字符串命令的连接
    private Socket fileSocket;
    private Matcher matcher;
    private String ip;
    //要发送的文件长度
    private long fileLength;
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
            fileDir=new File("/home/pi/RaspberryPi/Picture");
            arrayList=new ArrayList<File>();

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
                        stopConnectAndroid();
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
                    case RaspberryAction.TAKE_PICTURE:
                        //收到拍照指令
                        System.out.println("get Picture order");
                        takePicture();
                        break;
                    case RaspberryAction.SEND_PICTURE_OVER:
                        //收到此消息表示Android已经接受完成，可以断开连接了
                        stopConnectAndroid();
                        //System.out.println("连接断开");
                        break;
                    default:
                        matcher=Pattern.compile("ip\\+(\\S+)").matcher(string);
                        if (matcher.matches()){
                            ip=matcher.group(1);
                            System.out.println();
                        }
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
        //舵机归位
        raspberryPi.setHandDS3218(23);
        raspberryPi.stop();
        System.out.println("Device "+devices+" offline!");
        stopConnectAndroid();
        try {
            //关闭摄像头
            Runtime.getRuntime().exec("sudo killall mjpg_streamer");
            printStream.close();
            bufferedReader.close();
            if (fileInputStream!=null){
                fileInputStream.close();
            }
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
                            if (isOverTime(time,Calendar.getInstance().get(Calendar.SECOND),2)){
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
    //判断时间是否大于指定的时间秒
    private boolean isOverTime(int time1,int time2,int time){
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
                    if (isOverTime(time,Calendar.getInstance().get(Calendar.SECOND),2)){
                        raspberryPi.handMG995Stop();
                        break;
                    }
                }
            }
        },"checkHandMg995IsOverTime").start();
    }
    //程序执行时检查图片文件夹内容，并将文件信息存入ArrayList中
    private void initArrayList(File file){
        if (file.exists()&&file.isDirectory()){
            File[] files=file.listFiles();
            for (File tempFile:files){
                arrayList.add(tempFile);
            }
            numOfPicture=files.length;
            System.out.println("File of direction "+arrayList.size());
        }else {
            System.out.println("get direction infor field");
        }
    }
    //检查图片文件夹是否有新内容，有的话就发送
    private void checkAndSendFile(File file){
        if (file.exists()&&file.isDirectory()){
            File[] files=file.listFiles();
            for (File tempFile:files){
                if (!arrayList.contains(tempFile)){
                    arrayList.add(tempFile);
                    try {
                        //延时0.01秒
                        Thread.currentThread().sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //获取文件长度并发送给客户端
                    fileLength=tempFile.length();
                    System.out.println("fileLength="+fileLength);
                    sendText(RaspberryAction.SEND_PICTURE);
                    sendText("length+"+fileLength);
                    sendFile(tempFile);

                    System.out.println("send file "+tempFile.getPath());
                }
            }
        }else {
            System.out.println("get direction infor field");
        }
    }
    //发送文件
    private void sendFile(File file){
        try {
            fileInputStream=new FileInputStream(file);
            byte[] temp=new byte[1024];
            int length;
            while ((length=fileInputStream.read(temp))!=-1){
                outputStream.write(temp,0,length);
            }
        }catch (IOException e){
            e.printStackTrace();
//            System.out.println("---File translate field---");
        }
    }
    //检查8秒内照片文件夹是否有新文件出现
    private void checkNumOfPicDir(File file){
        int time=Calendar.getInstance().get(Calendar.SECOND);
        while (true){
            if (file.listFiles().length==numOfPicture){
                checkAndSendFile(fileDir);
//                System.out.println("we have take a new picture");
                break;
            }

            if (isOverTime(time,Calendar.getInstance().get(Calendar.SECOND),2)){
//                System.out.println("take picture overtime");
                break;
            }
        }
    }
    //处理拍照流程
    private void takePicture(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                initArrayList(fileDir);
                try {
                    fileSocket=new Socket(ip,1336);
                    outputStream=fileSocket.getOutputStream();
//                    System.out.println("connect android succeed");
                }catch (UnknownHostException e) {
//                    System.out.println("connect android failed");
                    sendText(RaspberryAction.CONNECT_ANDROID_FAILED);
                    e.printStackTrace();
                    return;
                } catch (IOException e) {
//                    System.out.println("connect android failed");
                    sendText(RaspberryAction.CONNECT_ANDROID_FAILED);
                    e.printStackTrace();
                    return;
                }
                try {
                    Runtime.getRuntime().exec("sudo killall mjpg_streamer");
                    //延时1秒再启动相机拍照
                    Thread.currentThread().sleep(1000);
                    Runtime.getRuntime().exec("raspistill -rot 180 -o /home/pi/RaspberryPi/Picture/picture-"+numOfPicture+".jpg");
                    sendText(RaspberryAction.TAKE_PICTURE);
                    numOfPicture++;
                    //摄像头默认拍照时间为5秒，故延时5秒
                    System.out.println("start 8s pause");
                    Thread.currentThread().sleep(8000);
                    System.out.println("stop 8s pause");
                    checkNumOfPicDir(fileDir);
                    //重新启动摄像头
                    System.out.println("restart camera");
                    Runtime.getRuntime().exec("bash /home/pi/RaspberryPi/startCamera.sh");
                    System.out.println("take picture over");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"Thread-takePicture").start();
    }
    //发送完照片后关掉和Android的数据连接
    private void stopConnectAndroid(){
//        System.out.println("stopConnectAndroid out");
        if (fileSocket!=null&&!fileSocket.isClosed()){
//        System.out.println("stopConnectAndroid in");
            try {
                outputStream.close();
                fileSocket.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
