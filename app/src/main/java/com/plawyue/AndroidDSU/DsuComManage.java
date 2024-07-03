package com.plawyue.AndroidDSU;

import static com.plawyue.AndroidDSU.MainActivity.gs_DsuCtrlUIData;

import android.content.Context;
import android.os.Vibrator;

import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.Queue;

public class DsuComManage {
    public int DSU_COM_STATEMACHINE = 0;                 //状态机
    public static final int STATE_INIT                   = 0;       //初始化状态
    public static final int STATE_NORMALWORK             = 1;       //正常收发中

    public static final int SERVER_RECV_PORT             = 26760;

    public static final int SERVERTIMEOUT_S              = 5 * 1000;    //5秒未接收到消息视为超时
    /*接收部分网络管理器*/
    DatagramSocket ServerSocket        = null;
    DatagramPacket ServerPacket        = null;
    String ClientIP   ;
    public int    ClientPort            = 0;
    public long   ul_TimeStamp_LstRecv  = 0;        //上次收到消息时的时间戳
    static public int ServerConnectFlag     = 0;        //0未链接   1已链接
    static public int MotorLevel ;
    /*控制器信息*/
    DsuCtrlType[]  mController = new DsuCtrlType[4];



    /*接收报文队列*/
    Queue<DatagramPacket> ClientMsg = new LinkedList<>();


    public DsuComManage()
    {

    }

    public void StartServer()
    {
        //通信发送与处理线程
        new Thread() {
            @Override
            public void run() {
                DSUCOM_TASK();

            }
        }.start();
        //通信接收线程
        new Thread() {
            @Override
            public void run() {
                ClientRecv_Task();

            }
        }.start();
    }

    /*服务器信息发送管理器*/
    public void DSUCOM_TASK()
    {
        while (true) {
            //10  ms Task
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            //Part 1 交互信息处理
            switch (DSU_COM_STATEMACHINE) {
                //初始化
                case STATE_INIT:
                    //1.1 创建接收数据包的端口
                    try {
                        ServerSocket = new DatagramSocket(SERVER_RECV_PORT);
                    } catch (SocketException e) {

                    }
                    //1.2 初始化四个控制器信息
                    for (int i = 0; i < mController.length; i++) {
                        mController[i] = new DsuCtrlType();
                    }
                    for (int i = 0; i < 4; i++) {
                        mController[i].SlotNum = (byte) i;
                        mController[i].SlotState = DsuCtrlType.NOT_CONNECTED;
                        mController[i].DeviceModel = DsuCtrlType.MODEL_NOT_APPLICABLE;
                        mController[i].CntType = DsuCtrlType.CONNECTION_NOT_APPLICABLE;
                        mController[i].DeviceBattery = DsuCtrlType.BAT_NOT_APPLICABLE;
                        mController[i].DeviceMac = new Byte[]{0, 0, 0, 0, 0, 0};
                    }
                    //1.3 第 0 个控制器已链接
                    mController[0].SlotNum = (byte) 0;
                    mController[0].SlotState = DsuCtrlType.CONNECTED;
                    mController[0].DeviceModel = DsuCtrlType.MODEL_FULL_GYRO;
                    mController[0].CntType = DsuCtrlType.CONNECTION_BLUETOOTH;
                    mController[0].DeviceBattery = DsuCtrlType.BAT_HIGH;
                    mController[0].DeviceMac = new Byte[]{1, 2, 3, 4, 5, 6};
                    mController[0].ControlMotorType = DsuCtrlType.RUMBLE_DOUBLEMOTOR;
                    //1.4 状态机切换到正常工作
                    DSU_COM_STATEMACHINE = STATE_NORMALWORK;
                    break;
                case STATE_NORMALWORK:
                    if(!ClientMsg.isEmpty())        //接收队列不为空
                    {
                        //取出一个数据包进行处理
                        DatagramPacket ClientPacket = ClientMsg.poll();
                        //处理数据包数据
                        switch (DsuPacket.ClientResolve(ClientPacket))
                        {
                            case DsuPacket.CONNECTED_CONTROLLERS_INFO:
                                //控制器信息请求
                                //1.1 逐个回复控制器信息包
                                for(int i = 0 ; i < 4 ;i ++)
                                {
                                    // 创建一个ByteArrayOutputStream实例
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                                    // 将ArrayList中的每个Byte对象写入到ByteArrayOutputStream中
                                    for (Byte b : DsuPacket.ControllerInfoPack(mController[i])) {
                                        baos.write(b);
                                    }
                                    // 使用toByteArray()方法获取内部维护的byte数组
                                    byte[] ServerPack = baos.toByteArray();

                                    try {
                                        ServerSocket.send(new DatagramPacket(
                                                ServerPack,
                                                ServerPack.length,
                                                InetAddress.getByName(ClientPacket.getAddress().getHostAddress()),
                                                ClientPacket.getPort()
                                        )
                                        );
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                break;
                            case DsuPacket.CONTROLLERS_DATA:
                                //1.1 刷新客户端ip与端口
                                ClientIP = ClientPacket.getAddress().getHostAddress();
                                ClientPort = ClientPacket.getPort();
                                //数据请求
                                break;
                            case DsuPacket.CONTROLLER_MOTORS_INFO:
                                //逐个回复控制器信息包
                                for(int i = 0 ; i < 4 ;i ++) {
                                    // 创建一个ByteArrayOutputStream实例
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    // 将ArrayList中的每个Byte对象写入到ByteArrayOutputStream中
                                    for (Byte b : DsuPacket.ControllerMotorInfoPack(mController[i])) {
                                        baos.write(b);
                                    }
                                    // 使用toByteArray()方法获取内部维护的byte数组
                                    byte[] ServerPack = baos.toByteArray();

                                    try {
                                        ServerSocket.send(new DatagramPacket(
                                                        ServerPack,
                                                        ServerPack.length,
                                                        InetAddress.getByName(ClientPacket.getAddress().getHostAddress()),
                                                        ClientPacket.getPort()
                                                )
                                        );
                                    } catch (IOException e) {

                                    }
                                }
                                break;
                            case DsuPacket.CONTROLLER_MOTOR_RUMBLE:
                                //马达数据
                                byte MotorIndex = ClientPacket.getData()[19 + 8 + 1];
                                MotorLevel = ClientPacket.getData()[19 + 8 + 2] & 0xff;
                                break;
                        }
                    }
                    break;
            }
           ;
            //Part 2 定时发送控制器数据包
            if( (ClientPort != 0 )
            && (!ClientIP.isEmpty())
            && (ServerConnectFlag == 1))
            {
                //1.刷新控制器数据
                mController[0].accelY = gs_DsuCtrlUIData.accelY;
                mController[0].accelZ = gs_DsuCtrlUIData.accelZ;
                mController[0].accelX = gs_DsuCtrlUIData.accelX;
                mController[0].gyroR= gs_DsuCtrlUIData.gyroR;
                mController[0].gyroY= gs_DsuCtrlUIData.gyroY;
                mController[0].gyroP= gs_DsuCtrlUIData.gyroP;
                mController[0].X= gs_DsuCtrlUIData.X;
                mController[0].Y= gs_DsuCtrlUIData.Y;
                mController[0].A= gs_DsuCtrlUIData.A;
                mController[0].B= gs_DsuCtrlUIData.B;
                mController[0].R1= gs_DsuCtrlUIData.R1;
                mController[0].R2= gs_DsuCtrlUIData.R2;
                mController[0].L1= gs_DsuCtrlUIData.L1;
                mController[0].L2= gs_DsuCtrlUIData.L2;
                mController[0].PS= gs_DsuCtrlUIData.PS;
                mController[0].R3= gs_DsuCtrlUIData.R3;
                mController[0].L3= gs_DsuCtrlUIData.L3;
                mController[0].Option= gs_DsuCtrlUIData.Option;
                mController[0].Share= gs_DsuCtrlUIData.Share;
                mController[0].Dpad_UP= gs_DsuCtrlUIData.Dpad_UP;
                mController[0].Dpad_Left= gs_DsuCtrlUIData.Dpad_Left;
                mController[0].Dpad_Down= gs_DsuCtrlUIData.Dpad_Down;
                mController[0].Dpad_Right= gs_DsuCtrlUIData.Dpad_Right;
                mController[0].DeviceBattery= gs_DsuCtrlUIData.DeviceBattery;
                mController[0].left_stick_y= gs_DsuCtrlUIData.left_stick_y;
                mController[0].left_stick_x= gs_DsuCtrlUIData.left_stick_x;
                mController[0].right_stick_y= gs_DsuCtrlUIData.right_stick_y;
                mController[0].right_stick_x= gs_DsuCtrlUIData.right_stick_x;

                //2.网络组包与回复

                // 创建一个ByteArrayOutputStream实例
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                // 将ArrayList中的每个Byte对象写入到ByteArrayOutputStream中
                for (Byte b : DsuPacket.ControllerDataPack(mController[0])) {
                    baos.write(b);
                }
                // 使用toByteArray()方法获取内部维护的byte数组
                byte[] ServerPack = baos.toByteArray();

                try {
                    ServerSocket.send(new DatagramPacket(
                                    ServerPack,
                                    ServerPack.length,
                                    InetAddress.getByName(ClientIP),
                                    ClientPort
                            )
                    );
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }


                //3.超时检测管理器
                if( ( System.currentTimeMillis() -  ul_TimeStamp_LstRecv ) > SERVERTIMEOUT_S)
                {
                    ServerConnectFlag = 0;
                    MotorLevel = 0;
                }
            }


        }
    }



    /*客户端信息接收管理器*/
    public void ClientRecv_Task()
    {
        while(true)
        {
            //10ms Task
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            //1.接收部分管理器
            try {
                //接收到消息后加入队列
                if(ServerSocket != null)
                {
                    byte[] buffer = new byte[1024]; // 每次循环都创建一个新的缓冲区
                    ServerPacket = new DatagramPacket(buffer, buffer.length);
                    ServerSocket.receive(ServerPacket);
                    ClientMsg.add(ServerPacket);
                    //客户端链接标识位管理
                    ServerConnectFlag = 1;
                    ul_TimeStamp_LstRecv = System.currentTimeMillis();      //刷新时间戳
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }
    }

}
