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
    public ScreenValues(){
        Dimension dimension=Toolkit.getDefaultToolkit().getScreenSize();
        screenHeight=(int)dimension.getHeight();
        screenWidth=(int)dimension.getWidth();
        frameHeight=screenHeight*8/9;
        frameWidth=screenWidth*8/9;
        frameX=screenWidth/18;
        frameY=screenHeight/18;
        directionPanelLength=frameWidth/5;
        cameraPanelLength=frameWidth/5;
        leftDirectionPanelX=frameWidth/10;
        leftDirectionPanelY=frameHeight-directionPanelLength-frameHeight/10;
        rightDirectionPanelX=frameWidth-directionPanelLength-frameWidth/10;
        rightDirectionPanelY=frameHeight-directionPanelLength-frameHeight/10;
        cameraPanelX=frameWidth/2-cameraPanelLength/2;
        cameraPanelY=frameHeight-cameraPanelLength-frameHeight/10;
    }
}
