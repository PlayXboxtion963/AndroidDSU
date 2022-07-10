package com.plawyue.wiimotedsu;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.sql.Time;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class MontionServer {
    DatagramPacket packet = null;
    DatagramSocket socketsend=null;
    DatagramSocket socket = null;
    float accX, accY, accZ;
    float gyroR,gyroY,gyroP;
    int A,B,X,Y,OPKEY,SHARE,PS,R1=0,L1=0,R2=0,L2=0,L3,R3;
    int Dpad_Left=0x0,Dpad_UP=0x0,Dpad_Right=0x0,Dpad_Down=0x0;
    String ip = null;
    int port=0;
    int battery;
    Boolean cansend=false;
    Boolean threadlock=false;
    Packet receivedPacket = new Packet();
    int cansendflag=0;
    Controller mcontrol = new Controller();
    public void start() throws InterruptedException {
        Thread.sleep(1000);
        mcontrol.createcontroller(2, 2, 0, new int[]{0x34, 0x56, 0x78, 0x9a, 0xbc, 0xdf}, 250);
        try {
            socketsend=new DatagramSocket(43521);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        try {
            socket = new DatagramSocket(26760);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        new Thread() {
            @Override
            public void run() {
                while (true){
                mcontrol.accelY = accY;
                mcontrol.accelZ = accZ;
                mcontrol.accelX = accX;
                mcontrol.gyroR= gyroR;
                mcontrol.gyroY=gyroY;
                mcontrol.gyroP=gyroP;
                mcontrol.X=X;
                mcontrol.Y=Y;
                mcontrol.A=A;
                mcontrol.B=B;
                mcontrol.R1=R1;
                mcontrol.R2=R2;
                mcontrol.L1=L1;
                mcontrol.L2=L2;
                mcontrol.PS=PS;
                mcontrol.R3=R3;
                mcontrol.L3=L3;
                mcontrol.Option=OPKEY;
                mcontrol.Share=SHARE;
                mcontrol.Dpad_UP=Dpad_UP;
                mcontrol.Dpad_Left=Dpad_Left;
                mcontrol.Dpad_Down=Dpad_Down;
                mcontrol.Dpad_Right=Dpad_Right;
                mcontrol.battery=battery;
                try {
                        socket.send(bytesztopack(receivedPacket.answer(mcontrol, 1), ip, port));
                        Thread.sleep(20);
                } catch (Exception exception) {
                }
                }
            }
        }.start();
        String data = "1";
        int len = 0;
        byte[] buf = new byte[1024];
        packet = new DatagramPacket(buf, buf.length);
        while (true) {
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ip = packet.getAddress().getHostAddress();
            buf = packet.getData();
            len = packet.getLength();
            data = new String(buf, 0, len);
            receivedPacket.init(buf, data, len);
            if (receivedPacket.type == 1048577) {
                try {
                    socket.send(bytesztopack(receivedPacket.answer(mcontrol, 0), ip, packet.getPort()));
                    socket.send(bytesztopack(receivedPacket.answernocon(1), ip, packet.getPort()));
                    socket.send(bytesztopack(receivedPacket.answernocon(2), ip,packet.getPort()));
                    socket.send(bytesztopack(receivedPacket.answernocon(3), ip,packet.getPort()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(receivedPacket.type==1048578){
                port=packet.getPort();
            }
        }
    }

    public static long byteArrayToLong(byte[] array,int start,int end) {

        byte[] lowArray = Arrays.copyOfRange(array,start,end);
        ArrayUtils.reverse(lowArray);//java中默认的字节排列顺序为big_endian,用工具类将字节数组翻转改成little_endian
        BigInteger lowBigInt =new BigInteger(1,lowArray);
        return lowBigInt.longValue();//返回的就是无符号的long类型的字符串
    }
    private DatagramPacket bytesztopack(byte[] mdata,String ip,int port){
        DatagramPacket packetx = null;
            try {
                    packetx= new DatagramPacket(mdata, mdata.length, InetAddress.getByName(ip), port);
                return packetx;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            return packetx;
        }
}