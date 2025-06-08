package com.plawyue.AndroidDSU;

public class DsuCtrlType {

    //Part 1.  控制器信息部分

    /* Battery State 控制器电池状态定义 */
    public static final int BAT_NOT_APPLICABLE = 0x00;
    public static final int BAT_DYING          = 0x01;
    public static final int BAT_LOW            = 0x02;
    public static final int BAT_MEDIUM         = 0x03;
    public static final int BAT_HIGH           = 0x04;
    public static final int BAT_FULL_OR_ALMOST = 0x05;
    public static final int BAT_CHARGING       = 0xEE;
    public static final int BAT_CHARGED        = 0xEF;

    /* 控制器状态 Slot State */
    public static final Byte NOT_CONNECTED = 0;
    public static final Byte RESERVED      = 1;
    public static final Byte CONNECTED     = 2;

    /* 设备类型 Device model */
    public static final int MODEL_NOT_APPLICABLE        = 0;
    public static final int MODEL_NO_OR_PARTIAL_GYRO    = 1;
    public static final int MODEL_FULL_GYRO             = 2;
    public static final int MODEL_VR_Reserve            = 3;
    // Value 3 exists but should not be used (go with VR, guys).

    // Connection type
    public static final int CONNECTION_NOT_APPLICABLE   = 0;
    public static final int CONNECTION_USB              = 1;
    public static final int CONNECTION_BLUETOOTH        = 2;

    // 控制器震动马达信息
    public static final int RUMBLE_NO_SUPPORT    = 0;
    public static final int RUMBLE_SINGLEMOTOR   = 1;
    public static final int RUMBLE_DOUBLEMOTOR   = 2;
    public static final int  TOUCH_X_AXIS_MAX = 1000;
    public static final int TOUCH_Y_AXIS_MAX = 500;

    //Part 2.  控制器数据部分

    float gyroP = 0;
    float gyroY = 0;
    float gyroR = 0;
    float accelX = 0;
    float accelY = 0;
    float accelZ = 0;
    int Dpad_Left=0x0;
    int Dpad_UP=0x0;
    int Dpad_Right=0x0;
    int Dpad_Down=0x0;
    int A,B,X,Y,PLUS,DEDUCE,HOME,PS,R1,L1,R2,L2;
    int L3,R3,Option,Share;
    int left_stick_x,left_stick_y;
    int right_stick_x,right_stick_y;

    byte ControlMotorType;
    byte[] TouchStruce = new byte[12];
    //Part 3.   外部接口部分
    Byte   SlotNum;           //插槽序号
    Byte   SlotState;         //插槽状态
    Byte   DeviceModel;       //设备模式
    Byte   CntType;           //链接方式
    Byte   DeviceBattery;     //设备电量
    Byte[] DeviceMac ;


}
