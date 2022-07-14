package com.plawyue.wiimotedsu;



import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.zip.CRC32;

import org.apache.commons.lang3.ArrayUtils;
public class Packet {
    int DSUC_Invalid = 0x000000;
    int DSUC_VersionReq = 0x100000;
    int DSUS_VersionRsp = 0x100000;
    int DSUC_ListPorts = 0x100001;
    int DSUS_PortInfo = 0x100001;
    int DSUC_PadDataReq = 0x100002;
    int DSUS_PadDataRsp = 0x100002;
    int protocolVersion = 1001;
    int PressOption=00001000;
    int PressShare=00000100;
    int PressOptionandShare=00001100;
    int size=60;
    long crc=0;
    long clientId;
    long type;
    int registrationFlags,registrationId,registrationMac;
    Random r = new Random();
    int serverId = r.nextInt(9999) + 0;
    int counter = 0;
    public void init(byte[] msg, String data,int length) {//msg是原始字符数组，data是字符串
        if (length < 16 || (data.substring(0,4).equals("DSUC")==false)) {
            return;
        }
        this.protocolVersion=(int)byteArrayToLong(msg,4,6);
        this.size=(int)byteArrayToLong(msg,6,8);
        this.crc=byteArrayToLong(msg,8,12);
        msg[8]=0;
        msg[9]=0;
        msg[10]=0;
        msg[11]=0;
        byte[] crcte=java.util.Arrays.copyOf(msg,length);
        CRC32 crc32 = new CRC32();
        crc32.update(crcte);
        if (this.crc != crc32.getValue()){
            return;
        }
        this.type=byteArrayToLong(msg,16,20);
        }
    long startTime;
    public byte[] answer(Controller mcontrol,int flag){
        if(flag==1){
            byte[] mout = new byte[84];
            for(int i=0;i!=4;i++){
                mout[0+i]=Int2Bytes_LE(this.DSUS_PadDataRsp)[0+i];
            }//
            mout[4]=(byte) 0;
            mout[5]= (byte) 2;
            mout[6]= (byte) 2;
            mout[7]= (byte) 0;
            mout[8]=0x34;
            mout[9]=0x56;
            mout[10]=0x78;
            mout[11]= (byte) 0x9a;
            mout[12]= (byte) 0xbc;
            mout[13]= (byte) 0xdf;
            mout[14]= (byte) mcontrol.battery;
            mout[15]=1;
            this.counter+=1;
            for(int i=0;i!=4;i++){
                mout[16+i]=Int2Bytes_LE(this.counter)[0+i];
            }
            mout[20] =(byte)KeyToBitmask(mcontrol); //DPAD left, down, right, up, options,R3, L3 ,share
            mout[21] = 0 ;//# X, A, B, Y, R1, L1, R2, L2
            mout[22] = (byte) mcontrol.PS  ;//# button.PS
            mout[23] = 0x0  ;//# button.touch
            mout[24] = 0x0 ; //# position.left.x
            mout[25] = 0x0 ;//# position.left.y
            mout[26] = 0x0 ; //# position.right.x
            mout[27] = 0x0 ; //# position.right.y
            mout[28] = (byte) mcontrol.Dpad_Left;//# DPAD left
            mout[29] = (byte) mcontrol.Dpad_Down ;//# DPAD down
            mout[30] = (byte) mcontrol.Dpad_Right  ;//# DPAD right
            mout[31] = (byte) mcontrol.Dpad_UP ; //# DPAD up
            mout[32] = (byte) mcontrol.X ; //# X
            mout[33] =(byte) mcontrol.A  ;//# A
            mout[34] =(byte) mcontrol.B ;//# B
            mout[35] = (byte) mcontrol.Y  ;//# Y
            mout[36] = (byte) mcontrol.R1;//# R1
            mout[37] = (byte) mcontrol.L1;//# L1
            mout[38] = (byte) mcontrol.R2;  ;//# R2
            mout[39] = (byte) mcontrol.L2; //# L2
            for(int i=0;i!=12;i++){
                mout[40+i]=0;
            }
            long motion=System.currentTimeMillis()*1000;
            for(int i=0;i!=8;i++){
               // mout[52+i]=LongToBytes(motion)[i];
                mout[52+i]= (byte) ((motion>>i*8)&0xFF);

            }
            float buffer = mcontrol.accelX;
            ByteBuffer bbuf = ByteBuffer.allocate(4);
            bbuf.putFloat(buffer);
            byte[] bBuffer = bbuf.array();
            bBuffer=this.dataValueRollback(bBuffer);
            for(int i=0;i!=4;i++){
                mout[60+i]=bBuffer[i];
            }
            buffer = mcontrol.accelY;
            bbuf = ByteBuffer.allocate(4);
            bbuf.putFloat(buffer);
            bBuffer = bbuf.array();
            bBuffer=this.dataValueRollback(bBuffer);
            for(int i=0;i!=4;i++){
                mout[64+i]=bBuffer[i];
            }
            buffer = mcontrol.accelZ;
            bbuf = ByteBuffer.allocate(4);
            bbuf.putFloat(buffer);
            bBuffer = bbuf.array();
            bBuffer=this.dataValueRollback(bBuffer);
            for(int i=0;i!=4;i++){
                mout[68+i]=bBuffer[i];
            }
            buffer = mcontrol.gyroP;
            bbuf = ByteBuffer.allocate(4);
            bbuf.putFloat(buffer);
            bBuffer = bbuf.array();
            bBuffer=this.dataValueRollback(bBuffer);
            for(int i=0;i!=4;i++){
                mout[72+i]=bBuffer[i];
            }
            buffer = mcontrol.gyroY;
            bbuf = ByteBuffer.allocate(4);
            bbuf.putFloat(buffer);
            bBuffer = bbuf.array();
            bBuffer=this.dataValueRollback(bBuffer);
            for(int i=0;i!=4;i++){
                mout[76+i]=bBuffer[i];
            }
            buffer = mcontrol.gyroR;
            bbuf = ByteBuffer.allocate(4);
            bbuf.putFloat(buffer);
            bBuffer = bbuf.array();
            bBuffer=this.dataValueRollback(bBuffer);
            for(int i=0;i!=4;i++){
                mout[80+i]=bBuffer[i];
            }
            return this.generate(mout);
        }
        else if(flag==0){
           byte[] mout = new byte[16];
               for(int i=0;i!=4;i++){
                   mout[0+i]=Int2Bytes_LE(this.DSUS_PortInfo)[0+i];
               }//
               mout[4]= (byte) 0;//slot
               mout[5]= (byte) 2;
               mout[6]= (byte) 0;
               mout[7]= (byte) 0;
               mout[8]=0x34;
               mout[9]=0x56;
               mout[10]=0x78;
               mout[11]= (byte) 0x9a;
               mout[12]= (byte) 0xbc;
               mout[13]= (byte) 0xdf;
               mout[14]= (byte) 0x04;
               mout[15]=0;
               return this.generate(mout);

       }

        byte[] mout = new byte[0];
        return this.generate(mout);
    }

    public byte[] answernocon(int slot){

            byte[] mout = new byte[16];
            for(int i=0;i!=4;i++){
                mout[0+i]=Int2Bytes_LE(this.DSUS_PortInfo)[0+i];
            }
            mout[4]= (byte) slot;
            mout[5]= (byte) 0;
            mout[6]= (byte) 0;
            mout[7]= (byte) 0;
            mout[8]=0;
            mout[9]=0;
            mout[10]=0;
            mout[11]=0;
            mout[12]=0;
            mout[13]=0;
            mout[14]=0;
            mout[15]=0;
            return this.generate(mout);
    }
    private byte[] dataValueRollback(byte[] data) {
        ArrayList<Byte> al = new ArrayList<Byte>();
        for (int i = data.length - 1; i >= 0; i--) {
            al.add(data[i]);
        }

        byte[] buffer = new byte[al.size()];
        for (int i = 0; i <= buffer.length - 1; i++) {
            buffer[i] = al.get(i);
        }
        return buffer;
    }
    public byte[] generate(byte[] data){
        byte[] buffer = new byte[16];
        buffer[0]=(int) 'D';
        buffer[1]=(int)'S';
        buffer[2]=(int)'U';
        buffer[3]=(int)'S';//DSUS
        buffer[8]=0;
        buffer[9]=0;
        buffer[10]=0;
        buffer[11]=0;
        for(int i=0;i!=2;i++){
            buffer[4+i]=Int2Bytes_LE(1001)[0+i];
        }//protocolVersion
        for(int i=0;i!=2;i++){
            buffer[6+i]=Int2Bytes_LE(data.length)[0+i];

        }//len
        for(int i=0;i!=4;i++){
            buffer[12+i]=Int2Bytes_LE(this.serverId)[0+i];
        }//serverid
            byte[] btZ = new byte[buffer.length + data.length];
            System.arraycopy(buffer, 0, btZ, 0, buffer.length);
            System.arraycopy(data, 0, btZ, buffer.length, data.length);
            CRC32 c = new CRC32();
            c.reset();//Resets CRC-32 to initial value.
            c.update(btZ, 0, btZ.length);//将数据丢入CRC32解码器
            int crcvalue = (int) c.getValue();//获取CRC32 的值  默认返回值类型为long
            for(int i=0;i!=4;i++){
                btZ[8+i]=Int2Bytes_LE(crcvalue)[0+i];
            }//serverid

            return btZ;
        }
    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        result[0] = (byte)((i >> 24) & 0xFF);
        result[1] = (byte)((i >> 16) & 0xFF);
        result[2] = (byte)((i >> 8) & 0xFF);
        result[3] = (byte)(i & 0xFF);
        return result;
    }
    public static long byteArrayToLong(byte[] array,int start,int end) {

        byte[] lowArray = Arrays.copyOfRange(array,start,end);
        ArrayUtils.reverse(lowArray);//java中默认的字节排列顺序为big_endian,用工具类将字节数组翻转改成little_endian
        BigInteger lowBigInt =new BigInteger(1,lowArray);
        return lowBigInt.longValue();//返回的就是无符号的long类型的字符串
    }
    public byte[] Int2Bytes_LE(int iValue){
        byte[] rst = new byte[4];
        // 先写int的最后一个字节
        rst[0] = (byte)(iValue & 0xFF);
        // int 倒数第二个字节
        rst[1] = (byte)((iValue & 0xFF00) >> 8 );
        // int 倒数第三个字节
        rst[2] = (byte)((iValue & 0xFF0000) >> 16 );
        // int 第一个字节
        rst[3] = (byte)((iValue & 0xFF000000) >> 24 );
        return rst;
    }
    public static String substring(String str, int f, int t) {
        if (f > str.length())
            return null;
        if (t > str.length()) {
            return str.substring(f, str.length());
        } else {
            return str.substring(f, t);
        }
    }
    public int KeyToBitmask(Controller mcontrol){
        int a=00000000;
        if(mcontrol.Option==1){
            a |= (1 << 3);
        }
        if(mcontrol.R3==1){
        a |= (1 << 2);
        }
        if(mcontrol.L3==1){
            a |= (1 << 1);
        }
        if(mcontrol.Share==1){
            a |= (1 << 0);
        }
        return a;
    }

}
