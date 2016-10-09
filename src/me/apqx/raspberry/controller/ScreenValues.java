package me.apqx.raspberry.controller;

import java.awt.*;

/**
 * Created by chang on 2016/9/29.
 */
public class ScreenValues {
    public int screenWidth;
    public int screenHeight;
    //窗口大小
    public int frameWidth;
    public int frameHeight;
    //窗口位置
    public int frameX;
    public int frameY;
    //webView大小
    public int webViewWidth;
    public int webViewHeight;
    //webView位置
    public int webViewX;
    public int webViewY;
    //方向控制按钮面板大小
    public int directionPanelLength;
    //左方向控制面板位置
    public int leftDirectionPanelX;
    public int leftDirectionPanelY;
    //右方向控制面板位置
    public int rightDirectionPanelX;
    public int rightDirectionPanelY;
    //摄像头控制按钮面板大小
    public int cameraPanelLength;
    //摄像头控制面板位置
    public int cameraPanelX;
    public int cameraPanelY;
    //集合控制面板的大小
    public int rightPanelWidth;
    public int rightPanelHeight;
    //集合控制面板的位置
    public int rightPanelX;
    public int rightPanelY;
    //连接、断开、关机按钮大小
    public int btnWidth;
    public int btnHeight;
    //连接、断开、关机按钮位置
    public int btnConnectX;
    public int btnConnectY;
    public int btnShutdownX;
    public int btnShutdownY;
    public int btnDisconnectX;
    public int btnDisconnectY;
    //连接状态指示器大小
    public int imageStateWidth;
    public int imageStateHeight;
    //连接状态指示器位置
    public int imageStateX;
    public int imageStateY;
    //状态颜色
    public Color stateConnect;
    public Color stateDisconnect;
    public ScreenValues(){
        Dimension dimension=Toolkit.getDefaultToolkit().getScreenSize();
        screenHeight=(int)dimension.getHeight();
        screenWidth=(int)dimension.getWidth();
        frameHeight=screenHeight*8/9;
        frameWidth=screenWidth*8/9;
        frameX=screenWidth/18;
        frameY=screenHeight/18;
        rightPanelWidth=frameWidth/3;
        rightPanelHeight=frameHeight*14/15;
        rightPanelX=frameWidth-rightPanelWidth-frameWidth/25;
        rightPanelY=0;

        directionPanelLength=rightPanelWidth/2;
        leftDirectionPanelX=0;
        leftDirectionPanelY=rightPanelHeight-directionPanelLength;
        rightDirectionPanelX=directionPanelLength;
        rightDirectionPanelY=rightPanelHeight-directionPanelLength;

        cameraPanelLength=directionPanelLength;
        cameraPanelX=rightPanelWidth/2-cameraPanelLength/2;
        cameraPanelY=rightPanelHeight-cameraPanelLength-directionPanelLength;

        imageStateWidth=rightPanelWidth;
        imageStateHeight=directionPanelLength/3;
        imageStateX=0;
        imageStateY=cameraPanelY-imageStateHeight*5/3;

        btnWidth=rightPanelWidth/3;
        btnHeight=imageStateHeight;
        btnConnectX=0;
        btnConnectY=imageStateY-btnHeight*5/4;
        btnShutdownX=btnWidth;
        btnShutdownY=btnConnectY;
        btnDisconnectX=btnWidth*2;
        btnDisconnectY=btnConnectY;

        stateConnect=Color.green;
        stateDisconnect=Color.RED;
    }
}
