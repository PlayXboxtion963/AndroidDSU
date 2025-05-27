package com.plawyue.AndroidDSU;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.CRC32;

public class DsuPacket {
    /* Dsu协议包头部分 */
    public static final int     PROTOCOL_Ver               = 1001;                  //当前版本类型——DEC
    public static final Byte[]  PROTOCOL_Heard             =  {'D','S','U','S'};    //报文固定头
    public static final int     PROTOCOL_ServerID          = 1234;                  //随机的ID
    public static int           PacketCounter              = 0;
    /* Message types 报文类型定义 */
    public static final int PROTOCOL_VERSION_INFO      = 0x100000;
    public static final int CONNECTED_CONTROLLERS_INFO = 0x100001;
    public static final int CONTROLLERS_DATA           = 0x100002;
    public static final int CONTROLLER_MOTORS_INFO     = 0x110001; // Unofficial
    public static final int CONTROLLER_MOTOR_RUMBLE    = 0x110002; // Unofficial
    public static final int PROTOCOL_INVALID           = 0xFFFF;



    public static int ClientResolve(DatagramPacket RecvPack)
    {
        if(RecvPack == null)
        {
            return PROTOCOL_INVALID;
        }
        byte[] receivedData = new byte[RecvPack.getLength()];
        System.arraycopy(RecvPack.getData(), RecvPack.getOffset(), receivedData, 0, RecvPack.getLength());

        if (receivedData.length < 16) {
            return PROTOCOL_INVALID;
        }
        long ClientCrc = 0;
        ClientCrc |= receivedData[8] & 0xFFL;
        ClientCrc |= ((long) receivedData[9] & 0xFF) << 8;
        ClientCrc |= ((long) receivedData[10] & 0xFF) << 16;
        ClientCrc |= ((long) receivedData[11] & 0xFF) << 24;
        byte[] crcte=java.util.Arrays.copyOf(receivedData,receivedData.length);
        crcte[8]=0;
        crcte[9]=0;
        crcte[10]=0;
        crcte[11]=0;
        CRC32 crc32 = new CRC32();
        crc32.update(crcte);
        if (ClientCrc != (crc32.getValue() & 0xFFFFFFFFL)){
            return PROTOCOL_INVALID;
        }

        int MsgType  = 0;
        MsgType |=   receivedData[16] & 0xFF;
        MsgType |= ( receivedData[17]  << 8  );
        MsgType |= ( receivedData[18] << 16 );
        MsgType |= ( receivedData[19] << 24 );

        return  MsgType;
    }

    /*
        方法说明：用于组成控制器信息的回复包
        消息类型：CONNECTED_CONTROLLERS_INFO
     */
    public static ArrayList<Byte> ControllerInfoPack(DsuCtrlType mDsuCtrl)
    {
        //1. 创建消息包
            ArrayList<Byte> Msgpack = new ArrayList<Byte>();
        //2. 拼接DSU固定消息包头
            //2.1 固定头
            Msgpack.add(PROTOCOL_Heard[0]);
            Msgpack.add(PROTOCOL_Heard[1]);
            Msgpack.add(PROTOCOL_Heard[2]);
            Msgpack.add(PROTOCOL_Heard[3]);
            //2.2 版本号
            Msgpack.add( (byte)  ( (PROTOCOL_Ver >> 0) & 0xFF) );
            Msgpack.add( (byte)  ( (PROTOCOL_Ver >> 8) & 0xFF) );
            //2.3,2.4 长度与CRC暂时跳过 6 Byte
            for(int i=0;i<6;i++)
            {
                Msgpack.add((byte) 0);
            }
            /*******/
            //2.5 服务器id
            Msgpack.add( (byte)  ( (PROTOCOL_ServerID >> 0) & 0xFF) );
            Msgpack.add( (byte)  ( (PROTOCOL_ServerID >> 8) & 0xFF) );
            Msgpack.add( (byte)  ( (PROTOCOL_ServerID >> 16) & 0xFF) );
            Msgpack.add( (byte)  ( (PROTOCOL_ServerID >> 24) & 0xFF) );
            //2.6 消息类型
            Msgpack.add( (byte)  ( (CONNECTED_CONTROLLERS_INFO >> 0) & 0xFF) );
            Msgpack.add( (byte)  ( (CONNECTED_CONTROLLERS_INFO >> 8) & 0xFF) );
            Msgpack.add( (byte)  ( (CONNECTED_CONTROLLERS_INFO >> 16) & 0xFF) );
            Msgpack.add( (byte)  ( (CONNECTED_CONTROLLERS_INFO >> 24) & 0xFF) );
        //3. 拼接控制器信息
            //3.1 插槽序号
            Msgpack.add(mDsuCtrl.SlotNum);
            //3.2 插槽状态
            Msgpack.add(mDsuCtrl.SlotState);
            //3.3 插槽型号
            Msgpack.add(mDsuCtrl.DeviceModel);
            //3.4 链接方式
            Msgpack.add(mDsuCtrl.CntType);
            //3.5 MAC 地址
            Msgpack.addAll(Arrays.asList(mDsuCtrl.DeviceMac).subList(0, 6));
            //3.6 电池状态
            Msgpack.add(mDsuCtrl.DeviceBattery);
            //3.7 空字节
            Msgpack.add((byte) 0);
        //4.计算数据长度
            int PackLen = Msgpack.size() - 16;
            Msgpack.set(6, (byte) (PackLen & 0xFF));
            Msgpack.set(7, (byte) ((PackLen >> 8) & 0xFF));
        //5.计算CRC32数值
            // 将ArrayList转换为byte数组
            byte[] bytes = new byte[Msgpack.size()];
            for (int i = 0; i < Msgpack.size(); i++) {
                if(Msgpack.get(i) != null) {
                    bytes[i] = Msgpack.get(i);
                }
            }
            // 创建CRC32实例
            CRC32 crc = new CRC32();
            crc.update(bytes);
            // 计算并输出CRC值
            long checksum = crc.getValue();

            Msgpack.set(8,  (byte)  ( (checksum >> 0) & 0xFF) );
            Msgpack.set(9,  (byte)  ( (checksum >> 8) & 0xFF) );
            Msgpack.set(10, (byte)  ( (checksum >> 16) & 0xFF) );
            Msgpack.set(11, (byte)  ( (checksum >> 24) & 0xFF) );
        //6.返回结果
            return Msgpack;
    }

    /*
        方法说明：用于组成控制器实际数据的回复
        消息类型：CONTROLLERS_DATA
     */
    public static ArrayList<Byte> ControllerDataPack(DsuCtrlType mDsuCtrl) {
        //1. 创建消息包
        ArrayList<Byte> Msgpack = new ArrayList<Byte>();
        //2. 拼接DSU固定消息包头
        //2.1 固定头
        Msgpack.add(PROTOCOL_Heard[0]);
        Msgpack.add(PROTOCOL_Heard[1]);
        Msgpack.add(PROTOCOL_Heard[2]);
        Msgpack.add(PROTOCOL_Heard[3]);
        //2.2 版本号
        Msgpack.add((byte) ((PROTOCOL_Ver >> 0) & 0xFF));
        Msgpack.add((byte) ((PROTOCOL_Ver >> 8) & 0xFF));
        //2.3,2.4 长度与CRC暂时跳过 6 Byte
        for (int i = 0; i < 6; i++) {
            Msgpack.add((byte) 0);
        }
        /*******/
        //2.5 服务器id
        Msgpack.add((byte) ((PROTOCOL_ServerID >> 0) & 0xFF));
        Msgpack.add((byte) ((PROTOCOL_ServerID >> 8) & 0xFF));
        Msgpack.add((byte) ((PROTOCOL_ServerID >> 16) & 0xFF));
        Msgpack.add((byte) ((PROTOCOL_ServerID >> 24) & 0xFF));
        //2.6 消息类型
        Msgpack.add((byte) ((CONTROLLERS_DATA >> 0) & 0xFF));
        Msgpack.add((byte) ((CONTROLLERS_DATA >> 8) & 0xFF));
        Msgpack.add((byte) ((CONTROLLERS_DATA >> 16) & 0xFF));
        Msgpack.add((byte) ((CONTROLLERS_DATA >> 24) & 0xFF));
        //3. 拼接控制器信息
        //3.1 插槽序号
        Msgpack.add(mDsuCtrl.SlotNum);
        //3.2 插槽状态
        Msgpack.add(mDsuCtrl.SlotState);
        //3.3 插槽型号
        Msgpack.add(mDsuCtrl.DeviceModel);
        //3.4 链接方式
        Msgpack.add(mDsuCtrl.CntType);
        //3.5 MAC 地址
        Msgpack.addAll(Arrays.asList(mDsuCtrl.DeviceMac).subList(0, 6));
        //3.6 电池状态
        Msgpack.add(mDsuCtrl.DeviceBattery);
        //3.7 已链接
        Msgpack.add((byte) 1);
        //3.8 包号
        Msgpack.add((byte) ((PacketCounter >> 0 ) & 0xFF));
        Msgpack.add((byte) ((PacketCounter >> 8 ) & 0xFF));
        Msgpack.add((byte) ((PacketCounter >> 16 ) & 0xFF));
        Msgpack.add((byte) ((PacketCounter >> 24 ) & 0xFF));
        PacketCounter++;
        //实际控制器数据
        {
            byte MidVar = 0;
            {
                // Part 1 DPAD left, down, right, up, options,R3, L3 ,share
                if (mDsuCtrl.Dpad_Left == 255) {
                    MidVar |= (1 << 7);
                }
                if (mDsuCtrl.Dpad_Down == 255) {
                    MidVar |= (1 << 6);
                }
                if (mDsuCtrl.Dpad_Right == 255) {
                    MidVar |= (1 << 5);
                }
                if (mDsuCtrl.Dpad_UP == 255) {
                    MidVar |= (1 << 4);
                }
                if (mDsuCtrl.Option == 1) {
                    MidVar |= (1 << 3);
                }
                if (mDsuCtrl.R3 == 1) {
                    MidVar |= (1 << 2);
                }
                if (mDsuCtrl.L3 == 1) {
                    MidVar |= (1 << 1);
                }
                if (mDsuCtrl.Share == 1) {
                    MidVar |= (1 << 0);
                }
                Msgpack.add(MidVar);
            }
            MidVar = 0;
            {
                //Part 2  X, A, B, Y, R1, L1, R2, L2
                if(mDsuCtrl.Y==255){
                    MidVar |= (1 << 7);
                }
                if(mDsuCtrl.B==255){
                    MidVar |= (1 << 6);
                }
                if(mDsuCtrl.A==255){
                    MidVar |= (1 << 5);
                }
                if(mDsuCtrl.X==255){
                    MidVar |= (1 << 4);
                }
                if(mDsuCtrl.R1==255){
                    MidVar |= (1 << 3);
                }
                if(mDsuCtrl.L1==255){
                    MidVar |= (1 << 2);
                }
                if(mDsuCtrl.R2==255){
                    MidVar |= (1 << 1);
                }
                if(mDsuCtrl.L2==255){
                    MidVar |= (1 << 0);
                }
                Msgpack.add(MidVar);
            }
            {
                Msgpack.add( (byte) mDsuCtrl.PS)  ;//# button.PS
                Msgpack.add((byte) 0x0)  ;//# button.touch
                Msgpack.add((byte) mDsuCtrl.left_stick_x); //# position.left.x
                Msgpack.add((byte) mDsuCtrl.left_stick_y);//# position.left.y
                Msgpack.add( (byte) mDsuCtrl.right_stick_x) ; //# position.right.x
                Msgpack.add((byte) mDsuCtrl.right_stick_y ); //# position.right.y

                Msgpack.add( (byte) mDsuCtrl.Dpad_Left);//# DPAD left
                Msgpack.add((byte) mDsuCtrl.Dpad_Down );//# DPAD down
                Msgpack.add( (byte) mDsuCtrl.Dpad_Right)  ;//# DPAD right
                Msgpack.add((byte) mDsuCtrl.Dpad_UP ); //# DPAD up
                Msgpack.add( (byte) mDsuCtrl.X ); //# X
                Msgpack.add((byte) mDsuCtrl.A)  ;//# A
                Msgpack.add((byte) mDsuCtrl.B) ;//# B
                Msgpack.add((byte) mDsuCtrl.Y)  ;//# Y
                Msgpack.add((byte) mDsuCtrl.R1);//# R1
                Msgpack.add( (byte) mDsuCtrl.L1);//# L1
                Msgpack.add((byte) mDsuCtrl.R2);  ;//# R2
                Msgpack.add((byte) mDsuCtrl.L2); //# L2


                //无触摸数据
                for(int i = 0 ; i < 12; i ++){
                    Msgpack.add((byte) 0);
                }
            }
            {
                //体感部分
                //1.时间戳
                long u64_SystemTimeStamp = System.currentTimeMillis()*1000;
                Msgpack.add((byte) ((u64_SystemTimeStamp >> 0) & 0xFF));
                Msgpack.add((byte) ((u64_SystemTimeStamp >> 8) & 0xFF));
                Msgpack.add((byte) ((u64_SystemTimeStamp >> 16) & 0xFF));
                Msgpack.add((byte) ((u64_SystemTimeStamp >> 24) & 0xFF));
                Msgpack.add((byte) ((u64_SystemTimeStamp >> 32) & 0xFF));
                Msgpack.add((byte) ((u64_SystemTimeStamp >> 40) & 0xFF));
                Msgpack.add((byte) ((u64_SystemTimeStamp >> 48) & 0xFF));
                Msgpack.add((byte) ((u64_SystemTimeStamp >> 56) & 0xFF));

                //2.加速度
                ByteBuffer buffer = ByteBuffer.allocate(4); // 分配4个字节的缓冲区
                buffer.putFloat(0, mDsuCtrl.accelX); // 在索引0处填充float值
                for (int i = 3; i >= 0; i--) {
                    Msgpack.add(buffer.get(i));
                }

                buffer.putFloat(0, mDsuCtrl.accelY); // 在索引0处填充float值
                for (int i = 3; i >= 0; i--) {
                    Msgpack.add(buffer.get(i));
                }

                buffer.putFloat(0, mDsuCtrl.accelZ); // 在索引0处填充float值
                for (int i = 3; i >= 0; i--) {
                    Msgpack.add(buffer.get(i));
                }

                buffer.putFloat(0, mDsuCtrl.gyroP); // 在索引0处填充float值
                for (int i = 3; i >= 0; i--) {
                    Msgpack.add(buffer.get(i));
                }

                buffer.putFloat(0, mDsuCtrl.gyroY); // 在索引0处填充float值
                for (int i = 3; i >= 0; i--) {
                    Msgpack.add(buffer.get(i));
                }

                buffer.putFloat(0, mDsuCtrl.gyroR); // 在索引0处填充float值
                for (int i = 3; i >= 0; i--) {
                    Msgpack.add(buffer.get(i));
                }

            }

        }
        //4.计算数据长度
        int PackLen = Msgpack.size() - 16;
        Msgpack.set(6, (byte) (PackLen & 0xFF));
        Msgpack.set(7, (byte) ((PackLen >> 8) & 0xFF));
        //5.计算CRC32数值
        // 将ArrayList转换为byte数组
        byte[] bytes = new byte[Msgpack.size()];
        for (int i = 0; i < Msgpack.size(); i++) {
            if(Msgpack.get(i) != null) {
                bytes[i] = Msgpack.get(i);
            }
        }
        // 创建CRC32实例
        CRC32 crc = new CRC32();
        crc.update(bytes);


        // 计算并输出CRC值
        long checksum = crc.getValue();

        Msgpack.set(8,  (byte)  ( (checksum >> 0) & 0xFF) );
        Msgpack.set(9,  (byte)  ( (checksum >> 8) & 0xFF) );
        Msgpack.set(10, (byte)  ( (checksum >> 16) & 0xFF) );
        Msgpack.set(11, (byte)  ( (checksum >> 24) & 0xFF) );
        //6.返回结果
        return Msgpack;
    }



    /*
        方法说明：震动马达请求信息的回复
        消息类型：CONTROLLER_MOTORS_INFO
     */
    public static ArrayList<Byte> ControllerMotorInfoPack(DsuCtrlType mDsuCtrl)
    {
        //1. 创建消息包
        ArrayList<Byte> Msgpack = new ArrayList<Byte>();
        //2. 拼接DSU固定消息包头
        //2.1 固定头
        Msgpack.add(PROTOCOL_Heard[0]);
        Msgpack.add(PROTOCOL_Heard[1]);
        Msgpack.add(PROTOCOL_Heard[2]);
        Msgpack.add(PROTOCOL_Heard[3]);
        //2.2 版本号
        Msgpack.add( (byte)  ( (PROTOCOL_Ver >> 0) & 0xFF) );
        Msgpack.add( (byte)  ( (PROTOCOL_Ver >> 8) & 0xFF) );
        //2.3,2.4 长度与CRC暂时跳过 6 Byte
        for(int i=0;i<6;i++)
        {
            Msgpack.add((byte) 0);
        }
        /*******/
        //2.5 服务器id
        Msgpack.add( (byte)  ( (PROTOCOL_ServerID >> 0) & 0xFF) );
        Msgpack.add( (byte)  ( (PROTOCOL_ServerID >> 8) & 0xFF) );
        Msgpack.add( (byte)  ( (PROTOCOL_ServerID >> 16) & 0xFF) );
        Msgpack.add( (byte)  ( (PROTOCOL_ServerID >> 24) & 0xFF) );
        //2.6 消息类型
        Msgpack.add( (byte)  ( (CONTROLLER_MOTORS_INFO >> 0) & 0xFF) );
        Msgpack.add( (byte)  ( (CONTROLLER_MOTORS_INFO >> 8) & 0xFF) );
        Msgpack.add( (byte)  ( (CONTROLLER_MOTORS_INFO >> 16) & 0xFF) );
        Msgpack.add( (byte)  ( (CONTROLLER_MOTORS_INFO >> 24) & 0xFF) );
        //3. 拼接控制器信息
        //3.1 插槽序号
        Msgpack.add(mDsuCtrl.SlotNum);
        //3.2 插槽状态
        Msgpack.add(mDsuCtrl.SlotState);
        //3.3 插槽型号
        Msgpack.add(mDsuCtrl.DeviceModel);
        //3.4 链接方式
        Msgpack.add(mDsuCtrl.CntType);
        //3.5 MAC 地址
        Msgpack.addAll(Arrays.asList(mDsuCtrl.DeviceMac).subList(0, 6));
        //3.6 电池状态
        Msgpack.add(mDsuCtrl.DeviceBattery);
        //3.7 控制器马达支持
        Msgpack.add(mDsuCtrl.ControlMotorType);
        //4.计算数据长度
        int PackLen = Msgpack.size() - 16;
        Msgpack.set(6, (byte) (PackLen & 0xFF));
        Msgpack.set(7, (byte) ((PackLen >> 8) & 0xFF));
        //5.计算CRC32数值
        // 将ArrayList转换为byte数组
        byte[] bytes = new byte[Msgpack.size()];
        for (int i = 0; i < Msgpack.size(); i++) {
            if(Msgpack.get(i) != null) {
                bytes[i] = Msgpack.get(i);
            }
        }
        // 创建CRC32实例
        CRC32 crc = new CRC32();
        crc.update(bytes);
        // 计算并输出CRC值
        long checksum = crc.getValue();

        Msgpack.set(8,  (byte)  ( (checksum >> 0) & 0xFF) );
        Msgpack.set(9,  (byte)  ( (checksum >> 8) & 0xFF) );
        Msgpack.set(10, (byte)  ( (checksum >> 16) & 0xFF) );
        Msgpack.set(11, (byte)  ( (checksum >> 24) & 0xFF) );
        //6.返回结果
        return Msgpack;
    }

}
