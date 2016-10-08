package me.apqx.raspberry.controller;


import me.apqx.raspberry.controller.listener.DirectionListener;

import javax.swing.*;

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
    public Controller(String title){
        super(title);
        screenValues=new ScreenValues();
        handController=new ControllerPanel(screenValues.leftDirectionPanelX,screenValues.leftDirectionPanelY,screenValues.directionPanelLength,screenValues.directionPanelLength);
        moveController=new ControllerPanel(screenValues.rightDirectionPanelX,screenValues.rightDirectionPanelY,screenValues.directionPanelLength,screenValues.directionPanelLength);
        cameraController=new ControllerPanel(screenValues.cameraPanelX,screenValues.cameraPanelY,screenValues.cameraPanelLength,screenValues.cameraPanelLength);
//        在这里添加控件会出现第三个控件定位失效的问题
//        add(handController);
//        add(moveController);
//        add(cameraController);
    }
    private void start(){
        setListener();
    }
    private void setListener(){
        handController.setDirectionListener(new DirectionListener() {
            @Override
            public void up() {
                System.out.println("handUp");

            }

            @Override
            public void down() {
                System.out.println("handDown");

            }

            @Override
            public void right() {
                System.out.println("handRight");

            }

            @Override
            public void left() {
                System.out.println("handLeft");
            }

            @Override
            public void stop() {
                System.out.println("handStop");
            }
        });
        moveController.setDirectionListener(new DirectionListener() {
            @Override
            public void up() {
                System.out.println("moveUp");

            }

            @Override
            public void down() {
                System.out.println("moveDown");

            }

            @Override
            public void right() {
                System.out.println("moveRight");

            }

            @Override
            public void left() {
                System.out.println("moveLeft");
            }

            @Override
            public void stop() {
                System.out.println("moveStop");
            }
        });
        cameraController.setDirectionListener(new DirectionListener() {
            @Override
            public void up() {
                System.out.println("cameraUp");

            }

            @Override
            public void down() {
                System.out.println("cameraDown");

            }

            @Override
            public void right() {
                System.out.println("cameraRight");

            }

            @Override
            public void left() {
                System.out.println("cameraLeft");
            }

            @Override
            public void stop() {
                System.out.println("cameraStop");
            }
        });
    }
    public static void main(String[] args){
        Controller jFrame=new Controller("Controller");
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setBounds(jFrame.screenValues.frameX,jFrame.screenValues.frameY,jFrame.screenValues.frameWidth,jFrame.screenValues.frameHeight);
        jFrame.setVisible(true);
        jFrame.setLayout(null);
        jFrame.setResizable(false);
        //添加控件
        jFrame.add(jFrame.handController);
        jFrame.add(jFrame.moveController);
        jFrame.add(jFrame.cameraController);
        jFrame.start();
    }
}
