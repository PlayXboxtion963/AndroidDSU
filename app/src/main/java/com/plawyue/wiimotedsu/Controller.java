package com.plawyue.wiimotedsu;

public class Controller {
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
    int L3,R3,Option,Share,trueDpadLeft,trueDpadup,trueDpadRight,trueDpadDown;
    Boolean isActive = true;
    int state;
    int model;
    int connType;
    int[] macAddress;
    int battery;
    int left_stick_x,left_stick_y;
    int right_stick_x,right_stick_y;
    public void createcontroller(int state,int model,int connType,int[] macAddress,int battery){
        this.state=state;
        this.model=model;
        this.connType=connType;
        this.macAddress=macAddress;
        this.battery=battery;
    }
}
