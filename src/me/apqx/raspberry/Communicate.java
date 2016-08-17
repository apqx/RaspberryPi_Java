package me.apqx.raspberry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Created by chang on 2016/6/30.
 */
public class Communicate {
    private Socket socket;
    private int devices;
    private RaspberryPi raspberryPi;
    private PrintStream printStream;
    private BufferedReader bufferedReader;
    Communicate(Socket socket,RaspberryPi raspberryPi,int devices){
        this.socket=socket;
        this.raspberryPi=raspberryPi;
        this.devices=devices;
        startCommunicate();
    }
    private void startCommunicate(){
        try {
            printStream=new PrintStream(socket.getOutputStream());
            bufferedReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            printStream.println("Connected Successfull!");
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
                        raspberryPi.stop();
                        return;
                    case RaspberryAction.SHUTDOWN:
                        Runtime.getRuntime().exec("shutdown -h now");
                        System.out.println("Shutdown from remote controller");
                        break;
                }
            }
        }catch (IOException e){
            raspberryPi.stop();
            stopCommunicate();
        }
    }
    private void stopCommunicate(){
        System.out.println("Device "+devices+" offline!");
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
}
