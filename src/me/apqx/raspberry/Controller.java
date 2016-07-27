package me.apqx.raspberry;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by chang on 2016/6/30.
 */
public class Controller {
    private Socket socket;
    private Frame frame;
    private Button button;
    private TextField textIp;
    private TextField textState;
    private PrintStream printStream;
    private boolean upPressed;
    private boolean downPressed;
    private boolean rightPressed;
    private boolean leftPressed;
    public static void main(String[] args){
        new Controller();
    }
    private Controller(){
        init();
    }
    private void init(){
        frame=new Frame("me.apqx.raspberry.RaspberryPi Controller");
        frame.setBounds(1366/4,768/4,800,400);
        frame.setLayout(new FlowLayout());
        textIp=new TextField(10);
        textIp.setText("192.168.3.6");
        frame.add(textIp);
        button=new Button("Connect");
        frame.add(button);
        textState=new TextField(10);
        frame.add(textState);
        myEvent();
        frame.setVisible(true);
    }
    private void myEvent(){
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.exit(0);
            }
        });
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                startCommunicate();
            }
        });
        button.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()){
                    case KeyEvent.VK_UP:
                        upPressed=true;
                        sendText(RaspberryAction.FORWARD);
                        break;
                    case KeyEvent.VK_DOWN:
                        downPressed=true;
                        sendText(RaspberryAction.BACK);
                        break;
                    case KeyEvent.VK_RIGHT:
                        rightPressed=true;
                        sendText(RaspberryAction.TURN_RIGHT);
                        break;
                    case KeyEvent.VK_LEFT:
                        leftPressed=true;
                        sendText(RaspberryAction.TURN_LEFT);
                        break;
                    default:break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()){
                    case KeyEvent.VK_UP:
                        upPressed=false;
                        sendText(RaspberryAction.STOP);
                        break;
                    case KeyEvent.VK_DOWN:
                        downPressed=false;
                        sendText(RaspberryAction.STOP);
                        break;
                    case KeyEvent.VK_RIGHT:
                        rightPressed=false;
                        sendText(RaspberryAction.STOP);
                        break;
                    case KeyEvent.VK_LEFT:
                        leftPressed=false;
                        sendText(RaspberryAction.STOP);
                        break;
                    default:break;
                }
                checkAndDo();
            }

            @Override
            public void keyTyped(KeyEvent e) {

            }
        });
    }
    private void checkAndDo(){
        if (upPressed){
            sendText(RaspberryAction.FORWARD);
        }else if (downPressed){
            sendText(RaspberryAction.BACK);
        }else if (rightPressed){
            sendText(RaspberryAction.TURN_RIGHT);
        }else if (leftPressed){
            sendText(RaspberryAction.TURN_LEFT);
        }
    }
    private void startCommunicate(){
        try {
            if (socket==null){
                socket=new Socket(InetAddress.getByName(textIp.getText()),1335);
                textState.setText("Connected Successfull");
                printStream=new PrintStream(socket.getOutputStream());
            }
        } catch (IOException e) {
            e.printStackTrace();
            textState.setText("Connected Failed");
        }
    }
    private void sendText(String string){
        if (socket.isConnected()){
            printStream.println(string);
        }
    }
}
