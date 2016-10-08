package me.apqx.raspberry.controller;

import me.apqx.raspberry.controller.listener.DirectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**包含控制按钮的面板
 * Created by chang on 2016/10/8.
 */
public class ControllerPanel extends JPanel{
    private int width;
    private int height;
    private JButton up;
    private JButton down;
    private JButton right;
    private JButton left;
    private DirectionListener directionListener;
    //按钮边长
    private int buttonLength;
    public ControllerPanel(int x,int y,int width,int height){
        this.width=width;
        this.height=height;
        setLayout(null);
        setVisible(true);
        setBounds(x,y,width,height);
        buttonLength=Math.min(width,height)/3;

        //初始化按钮
        up=new JButton();
        up.setBounds(buttonLength,height/2-buttonLength*3/2,buttonLength,buttonLength);
        add(up);

        down=new JButton();
        down.setBounds(buttonLength,height/2+buttonLength/2,buttonLength,buttonLength);
        add(down);

        left=new JButton();
        left.setBounds(0,height/2-buttonLength/2,buttonLength,buttonLength);
        add(left);

        right=new JButton();
        right.setBounds(buttonLength*2,height/2-buttonLength/2,buttonLength,buttonLength);
        add(right);
        setListener();
    }
    public void setDirectionListener(DirectionListener directionListener){
        this.directionListener=directionListener;
    }
    private void setListener(){
        up.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (directionListener!=null){
                    directionListener.up();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (directionListener!=null){
                    directionListener.stop();
                }
            }
        });
        down.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (directionListener!=null){
                    directionListener.down();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (directionListener!=null){
                    directionListener.stop();
                }
            }
        });
        right.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (directionListener!=null){
                    directionListener.right();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (directionListener!=null){
                    directionListener.stop();
                }
            }
        });
        left.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (directionListener!=null){
                    directionListener.left();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (directionListener!=null){
                    directionListener.stop();
                }
            }
        });
    }
    public static void main(String[] args){
        ControllerPanel controllerPanel=new ControllerPanel(0,0,300,300);
        ControllerPanel controllerPanel2=new ControllerPanel(400,400,300,300);
        ControllerPanel controllerPanel3=new ControllerPanel(500,400,300,300);
        JFrame jFrame=new JFrame("Controller");
        jFrame.add(controllerPanel);
        jFrame.add(controllerPanel2);
        jFrame.add(controllerPanel3);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setLayout(null);
        jFrame.setBounds(1366*1/9,768*1/9,1366*7/9,768*7/9);
        jFrame.setResizable(false);
        jFrame.setVisible(true);
        controllerPanel.setDirectionListener(new DirectionListener() {
            @Override
            public void up() {
                System.out.println("up");
            }

            @Override
            public void down() {
                System.out.println("down");
            }

            @Override
            public void right() {
                System.out.println("right");
            }

            @Override
            public void left() {
                System.out.println("left");
            }

            @Override
            public void stop() {
                System.out.println("stop");
            }
        });
    }
}
