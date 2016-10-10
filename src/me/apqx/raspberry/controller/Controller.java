package me.apqx.raspberry.controller;


import me.apqx.raspberry.controller.listener.DirectionListener;
import me.apqx.raspberry.server.RaspberryAction;

import javax.swing.*;
import java.awt.event.*;

/**
 * Created by chang on 2016/9/29.
 */
public class Controller extends JFrame{
    private ScreenValues screenValues;
    //控制机械手
    private ControllerPanel handController;
    //控制方向
    private ControllerPanel moveController;
    //控制摄像头
    private ControllerPanel cameraController;
    //集合方向控制器和开关按钮的集合控制面板
    private JPanel rightPanel;
    //开关
    private JButton btnConnect;
    private JButton btnDisconnect;
    private JButton btnShutdown;
    //状态指示器
    private JTextArea imageState;

    private Communicate communicate;
    //判断是否连接上的标识
    private boolean isBuildConnected;
    //判断摄像头云台是否停止的标识
    private boolean isStop;
    //消除键盘控制方向的按键冲突
    private boolean upIsOn;
    private boolean downIsOn;
    private boolean rightIsOn;
    private boolean leftIsOn;
    //当按下键时会持续发送命令，需要判断键是否已经按下
    private boolean wIsOn;
    private boolean aIsOn;
    private boolean sIsOn;
    private boolean dIsOn;
    private boolean iIsOn;
    private boolean kIsOn;
    private boolean jIsOn;
    private boolean lIsOn;

    private Controller(String title){
        super(title);
        screenValues=new ScreenValues();

        btnConnect=new JButton("CONNECT");
        btnDisconnect=new JButton("DISCONNECT");
        btnShutdown=new JButton("SHUTDOWN");
        imageState=new JTextArea();
        imageState.setFocusable(false);
        imageState.setBackground(screenValues.stateDisconnect);
        imageState.setBounds(screenValues.imageStateX,screenValues.imageStateY,screenValues.imageStateWidth,screenValues.imageStateHeight);
        //不允许这三个按钮获得焦点
        btnConnect.setFocusable(false);
        btnDisconnect.setFocusable(false);
        btnShutdown.setFocusable(false);
        btnConnect.setBounds(screenValues.btnConnectX,screenValues.btnConnectY,screenValues.btnWidth,screenValues.btnHeight);
        btnDisconnect.setBounds(screenValues.btnDisconnectX,screenValues.btnDisconnectY,screenValues.btnWidth,screenValues.btnHeight);
        btnShutdown.setBounds(screenValues.btnShutdownX,screenValues.btnShutdownY,screenValues.btnWidth,screenValues.btnHeight);
        handController=new ControllerPanel(screenValues.leftDirectionPanelX,screenValues.leftDirectionPanelY,screenValues.directionPanelLength,screenValues.directionPanelLength);
        moveController=new ControllerPanel(screenValues.rightDirectionPanelX,screenValues.rightDirectionPanelY,screenValues.directionPanelLength,screenValues.directionPanelLength);
        cameraController=new ControllerPanel(screenValues.cameraPanelX,screenValues.cameraPanelY,screenValues.cameraPanelLength,screenValues.cameraPanelLength);


        rightPanel=new JPanel();
        rightPanel.setLayout(null);
        rightPanel.setVisible(true);
        rightPanel.setBounds(screenValues.rightPanelX,screenValues.rightPanelY,screenValues.rightPanelWidth,screenValues.rightPanelHeight);


        rightPanel.add(handController);
        rightPanel.add(moveController);
        rightPanel.add(cameraController);
        rightPanel.add(imageState);
        rightPanel.add(btnConnect);
        rightPanel.add(btnShutdown);
        rightPanel.add(btnDisconnect);
//        在这里添加控件会出现定位失效的问题
//        add(handController);
//        add(moveController);
//        add(cameraController);
    }
    private void start(){
        setListener();
    }
    private void setListener(){
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
//                System.out.println(e.getKeyChar()+" Pressed");
                switch (e.getKeyCode()){
                    case KeyEvent.VK_UP:
                        //前
                        if (!upIsOn){
                            communicate.sendText(RaspberryAction.FORWARD);
                            upIsOn=true;
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        //后退
                        if (!downIsOn){
                            communicate.sendText(RaspberryAction.BACK);
                            downIsOn=true;
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        //右转
                        if (!rightIsOn){
                            communicate.sendText(RaspberryAction.TURN_RIGHT);
                            rightIsOn=true;
                        }
                        break;
                    case KeyEvent.VK_LEFT:
                        //左转
                        if (!leftIsOn){
                            communicate.sendText(RaspberryAction.TURN_LEFT);
                            leftIsOn=true;
                        }
                        break;
                    case KeyEvent.VK_W:
                        //机械臂抬起
                        if (!wIsOn){
                            communicate.sendText(RaspberryAction.SERVO_DS3218_CW);
                            wIsOn=true;
                        }
                        break;
                    case KeyEvent.VK_S:
                        //机械臂放下
                        if (!sIsOn){
                            communicate.sendText(RaspberryAction.SERVO_DS3218_CCW);
                            sIsOn=true;
                        }
                        break;
                    case KeyEvent.VK_A:
                        //机械臂松开
                        if (!aIsOn){
                            communicate.sendText(RaspberryAction.SERVO_MG995_HAND_CW);
                            aIsOn=true;
                        }
                        break;
                    case KeyEvent.VK_D:
                        //机械臂夹紧
                        if (!dIsOn){
                            communicate.sendText(RaspberryAction.SERVO_MG995_HAND_CCW);
                            dIsOn=true;
                        }
                        break;
                    case KeyEvent.VK_I:
                        //摄像头向上
                        if (!iIsOn){
                            sendCameraText(RaspberryAction.SERVO_SG90_VERTICAL_CCW);
                            iIsOn=true;
                        }
                        break;
                    case KeyEvent.VK_K:
                        //摄像头向下
                        if (!kIsOn){
                            sendCameraText(RaspberryAction.SERVO_SG90_VERTICAL_CW);
                            kIsOn=true;
                        }
                        break;
                    case KeyEvent.VK_J:
                        //摄像头向左
                        if (!jIsOn){
                            sendCameraText(RaspberryAction.SERVO_SG90_HORIZONTAL_CCW);
                            jIsOn=true;
                        }
                        break;
                    case KeyEvent.VK_L:
                        //摄像头向右
                        if (!lIsOn){
                            sendCameraText(RaspberryAction.SERVO_SG90_HORIZONTAL_CW);
                            lIsOn=true;
                        }
                        break;
                    default:break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
//                System.out.println(e.getKeyChar()+" Released");
                switch (e.getKeyCode()){
                    case KeyEvent.VK_UP:
                        //前进
                        communicate.sendText(RaspberryAction.STOP);
                        upIsOn=false;
                        checkAndDo();
                        break;
                    case KeyEvent.VK_DOWN:
                        //后退
                        communicate.sendText(RaspberryAction.STOP);
                        downIsOn=false;
                        checkAndDo();
                        break;
                    case KeyEvent.VK_RIGHT:
                        //右转
                        communicate.sendText(RaspberryAction.STOP);
                        rightIsOn=false;
                        checkAndDo();
                        break;
                    case KeyEvent.VK_LEFT:
                        //左转
                        communicate.sendText(RaspberryAction.STOP);
                        leftIsOn=false;
                        checkAndDo();
                        break;
                    case KeyEvent.VK_W:
                        //机械臂抬起
                        communicate.sendText(RaspberryAction.SERVO_DS3218_STOP);
                        wIsOn=false;
                        break;
                    case KeyEvent.VK_S:
                        //机械臂放下
                        sIsOn=false;
                        communicate.sendText(RaspberryAction.SERVO_DS3218_STOP);
                        break;
                    case KeyEvent.VK_A:
                        //机械臂松开
                        communicate.sendText(RaspberryAction.SERVO_MG995_HAND_STOP);
                        aIsOn=false;
                        break;
                    case KeyEvent.VK_D:
                        //机械臂夹紧
                        communicate.sendText(RaspberryAction.SERVO_MG995_HAND_STOP);
                        dIsOn=false;
                        break;
                    case KeyEvent.VK_I:
                        //摄像头向上
                        iIsOn=false;
                        isStop=true;
                        break;
                    case KeyEvent.VK_K:
                        //摄像头向下
                        kIsOn=false;
                        isStop=true;
                        break;
                    case KeyEvent.VK_J:
                        jIsOn=false;
                        isStop=true;
                        break;
                        //摄像头向左
                    case KeyEvent.VK_L:
                        //摄像头向右
                        lIsOn=false;
                        isStop=true;
                        break;
                    default:break;
                }
            }
        });
        btnConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isBuildConnected){
                    communicate.start();
                }
                //点击后将焦点返回到JFrame上，因为它监听着键盘事件
                Controller.this.requestFocus();
            }
        });
        btnDisconnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isBuildConnected){
                    communicate.sendText(RaspberryAction.EXIT);
                    communicate.stop();
                }
                Controller.this.requestFocus();
            }
        });
        btnShutdown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isBuildConnected){
                    communicate.sendText(RaspberryAction.SHUTDOWN);
                    communicate.stop();
                }
                Controller.this.requestFocus();
            }
        });
        handController.setDirectionListener(new DirectionListener() {
            @Override
            public void up() {
//                System.out.println("handUp");
                communicate.sendText(RaspberryAction.SERVO_DS3218_CW);
                Controller.this.requestFocus();
            }

            @Override
            public void down() {
//                System.out.println("handDown");
                communicate.sendText(RaspberryAction.SERVO_DS3218_CCW);
                Controller.this.requestFocus();
            }

            @Override
            public void right() {
//                System.out.println("handRight");
                communicate.sendText(RaspberryAction.SERVO_MG995_HAND_CCW);
                Controller.this.requestFocus();
            }

            @Override
            public void left() {
//                System.out.println("handLeft");
                communicate.sendText(RaspberryAction.SERVO_MG995_HAND_CW);
                Controller.this.requestFocus();
            }

            @Override
            public void stop() {
//                System.out.println("handStop");
                communicate.sendText(RaspberryAction.SERVO_DS3218_STOP);
                communicate.sendText(RaspberryAction.SERVO_MG995_HAND_STOP);
                Controller.this.requestFocus();
            }
        });
        moveController.setDirectionListener(new DirectionListener() {
            @Override
            public void up() {
//                System.out.println("moveUp");
                communicate.sendText(RaspberryAction.FORWARD);
                Controller.this.requestFocus();
            }

            @Override
            public void down() {
//                System.out.println("moveDown");
                communicate.sendText(RaspberryAction.BACK);
                Controller.this.requestFocus();
            }

            @Override
            public void right() {
//                System.out.println("moveRight");
                communicate.sendText(RaspberryAction.TURN_RIGHT);
                Controller.this.requestFocus();
            }

            @Override
            public void left() {
//                System.out.println("moveLeft");
                communicate.sendText(RaspberryAction.TURN_LEFT);
                Controller.this.requestFocus();
            }

            @Override
            public void stop() {
//                System.out.println("moveStop");
                communicate.sendText(RaspberryAction.STOP);
                Controller.this.requestFocus();
            }
        });
        cameraController.setDirectionListener(new DirectionListener() {
            @Override
            public void up() {
                sendCameraText(RaspberryAction.SERVO_SG90_VERTICAL_CCW);
                Controller.this.requestFocus();
            }

            @Override
            public void down() {
                sendCameraText(RaspberryAction.SERVO_SG90_VERTICAL_CW);
                Controller.this.requestFocus();
            }

            @Override
            public void right() {
                sendCameraText(RaspberryAction.SERVO_SG90_HORIZONTAL_CW);
                Controller.this.requestFocus();
            }

            @Override
            public void left() {
                sendCameraText(RaspberryAction.SERVO_SG90_HORIZONTAL_CCW);
                Controller.this.requestFocus();
            }

            @Override
            public void stop() {
//                System.out.println("cameraStop");
                Controller.this.requestFocus();
                isStop=true;
            }
        });
    }
    //不断发送摄像头云台指令，直至停止
    private void sendCameraText(String string){
        new Thread(new Runnable() {
            @Override
            public void run() {
                isStop=false;
                while (!isStop){
                    communicate.sendText(string);
//                    System.out.println(string);
                    try {
                        Thread.currentThread().sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    //暴露给外界的用于将界面变成连接成功或断开状态
    public void setConnectState(boolean isConnected){
        if (isConnected){
            imageState.setBackground(screenValues.stateConnect);
            isBuildConnected=true;
        }else {
            imageState.setBackground(screenValues.stateDisconnect);
            isBuildConnected=false;
        }
    }
    //解决按键冲突
    private void checkAndDo(){
        if (upIsOn){
            communicate.sendText(RaspberryAction.FORWARD);
        }
        if (downIsOn){
            communicate.sendText(RaspberryAction.BACK);
        }
        if (rightIsOn){
            communicate.sendText(RaspberryAction.TURN_RIGHT);
        }
        if (leftIsOn){
            communicate.sendText(RaspberryAction.TURN_LEFT);
        }
    }
    public static void main(String[] args){
        Controller jFrame=new Controller("Controller");
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setBounds(jFrame.screenValues.frameX,jFrame.screenValues.frameY,jFrame.screenValues.frameWidth,jFrame.screenValues.frameHeight);
        jFrame.setVisible(true);
        jFrame.setLayout(null);
        jFrame.setResizable(false);
        //添加控件
        jFrame.add(jFrame.rightPanel);
        jFrame.start();

        jFrame.communicate=new Communicate(jFrame);

    }
}
