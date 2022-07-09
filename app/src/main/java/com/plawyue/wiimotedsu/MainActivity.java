package com.plawyue.wiimotedsu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener {
    private SensorManager sensorManager;
    private Sensor sensor;
    MontionServer ms=new MontionServer();
    static double PI = 3.1415926535897932;
    Button L3,R3,OPkey,SHARE;
    Boolean locked=false;
    Button button_A,button_B,button_DUP,button_DDOWN,button_DLEFT,button_DRight,button_PLUS,button_DEDUCE,BUTTON_home,L2,R2,Touch;
    EditText Sensitive;
    static float METER_PER_SECOND_SQUARED_TO_G = (float) 9.8066;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Sensitive=findViewById(R.id.Sensitive);
        Sensitive.setText("0.9");
        Touch=findViewById(R.id.Button_touchlock);
        Touch.setOnClickListener(this);
        button_A=findViewById(R.id.button_A);
        button_B=findViewById(R.id.button_B);
        button_DUP=findViewById(R.id.button_dUp);
        button_DDOWN=findViewById(R.id.button_Ddown);
        button_DLEFT=findViewById(R.id.button_Dleft);
        button_DRight=findViewById(R.id.button_Dright);
        button_PLUS=findViewById(R.id.button_PLUS);
        button_DEDUCE=findViewById(R.id.button_DEDUCE);
        BUTTON_home=findViewById(R.id.button_home);
        L3=findViewById(R.id.buttonL3);
        R3=findViewById(R.id.buttonR3);
        SHARE=findViewById(R.id.buttonShare);
        OPkey=findViewById(R.id.buttonOption);
        L3.setOnTouchListener(this);
        R3.setOnTouchListener(this);
        SHARE.setOnTouchListener(this);
        OPkey.setOnTouchListener(this);
        L2=findViewById(R.id.button_L2);
        R2=findViewById(R.id.button_R2);
        L2.setOnTouchListener(this);
        R2.setOnTouchListener(this);
        button_A.setOnTouchListener(this);
        button_B.setOnTouchListener(this);
        button_DUP.setOnTouchListener(this);
        button_DDOWN.setOnTouchListener(this);
        button_DLEFT.setOnTouchListener(this);
        button_DRight.setOnTouchListener(this);
        button_PLUS.setOnTouchListener(this);
        button_DEDUCE.setOnTouchListener(this);
        BUTTON_home.setOnTouchListener(this);
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = (ipAddress & 0xff) + "." + (ipAddress>>8 & 0xff) + "." + (ipAddress>>16 & 0xff) + "." + (ipAddress >> 24 & 0xff);
        TextView IPT=findViewById(R.id.YouIP);
        IPT.setText(ip);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ms.start();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        SensorEventListener sensorListener = new SensorEventListener() {
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
            public void onSensorChanged(SensorEvent event) {
                float accSensitivity= (float) 0.9;
                String sensi=Sensitive.getText().toString().trim();
                if(isNumeric(sensi)){
                accSensitivity= Float.parseFloat(Sensitive.getText().toString());
                }
                Boolean conver=true;
                Boolean noconver=false;
                float accX = -accSensitivity * event.values[2] * METER_PER_SECOND_SQUARED_TO_G / 100;
                float accY =  -accSensitivity * event.values[0] * METER_PER_SECOND_SQUARED_TO_G / 100;
                float accZ =accSensitivity * event.values[1] * METER_PER_SECOND_SQUARED_TO_G / 100;
                DecimalFormat df = new DecimalFormat("#.00");
                ms.accX= Float.parseFloat(df.format(accY));
                ms.accY=Float.parseFloat(df.format(accX));
                ms.accZ=Float.parseFloat(df.format(accZ));


            }
        };
        SensorEventListener gyrolinster=new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {

                ms.gyroR= (float) (radToDeg(sensorEvent.values[1]) * 0.8);
                ms.gyroY= (float) (-radToDeg(sensorEvent.values[2]) * 0.8);
                ms.gyroP= (float) (radToDeg(sensorEvent.values[0]) * 0.8);
            }
            double radToDeg(double radians) {
                return radians * 180 / PI;
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        sensorManager.registerListener(sensorListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(gyrolinster,
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_UI);
    }

    public static boolean isNumeric(String str){

        Pattern pattern = Pattern.compile("[0-9]*\\.?[0-9]+");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;


    }
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
    if(locked==false){
        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            switch (view.getId()){
                case R.id.button_A: button_A.setBackgroundColor(Color.RED);ms.A=255;break;
                case R.id.button_B: button_B.setBackgroundColor(Color.RED); ms.B=255;break;
                case R.id.button_dUp: button_DUP.setBackgroundColor(Color.RED); ms.Dpad_UP=255;break;
                case R.id.button_Ddown: button_DDOWN.setBackgroundColor(Color.RED); ms.Dpad_Down=255;break;
                case R.id.button_Dleft: button_DLEFT.setBackgroundColor(Color.RED); ms.Dpad_Left=255;break;
                case R.id.button_Dright:button_DRight.setBackgroundColor(Color.RED); ms.Dpad_Right=255;break;
                case R.id.button_PLUS:button_PLUS.setBackgroundColor(Color.RED); ms.R1=255;break;
                case R.id.button_DEDUCE:button_DEDUCE.setBackgroundColor(Color.RED); ms.L1=255;break;
                case R.id.button_home:BUTTON_home.setBackgroundColor(Color.RED);ms.PS=1;break;
                case R.id.button_L2:L2.setBackgroundColor(Color.RED);ms.L2=255;break;
                case R.id.button_R2:R2.setBackgroundColor(Color.RED);ms.R2=255;break;
                case R.id.buttonR3:R3.setBackgroundColor(Color.RED);ms.R3=1;break;
                case R.id.buttonL3:L3.setBackgroundColor(Color.RED);ms.L3=1;break;
                case R.id.buttonOption:OPkey.setBackgroundColor(Color.RED);ms.OPKEY=1;break;
                case R.id.buttonShare:SHARE.setBackgroundColor(Color.RED);ms.SHARE=1;break;


            }
        }else if( motionEvent.getAction() == MotionEvent.ACTION_UP){
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
            switch (view.getId()){
                case R.id.button_A: button_A.setBackgroundColor(Color.WHITE);ms.A=0;break;
                case R.id.button_B: button_B.setBackgroundColor(Color.WHITE); ms.B=0;break;
                case R.id.button_dUp: button_DUP.setBackgroundColor(Color.WHITE); ms.Dpad_UP=0;break;
                case R.id.button_Ddown: button_DDOWN.setBackgroundColor(Color.WHITE); ms.Dpad_Down=0;break;
                case R.id.button_Dleft: button_DLEFT.setBackgroundColor(Color.WHITE); ms.Dpad_Left=0;break;
                case R.id.button_Dright: button_DRight.setBackgroundColor(Color.WHITE); ms.Dpad_Right=0;break;
                case R.id.button_PLUS:button_PLUS.setBackgroundColor(Color.WHITE); ms.R1=0;break;
                case R.id.button_DEDUCE:button_DEDUCE.setBackgroundColor(Color.WHITE); ms.L1=0;break;
                case R.id.button_home:BUTTON_home.setBackgroundColor(Color.WHITE);ms.PS=0;break;
                case R.id.button_L2:L2.setBackgroundColor(Color.WHITE);ms.L2=0;break;
                case R.id.button_R2:R2.setBackgroundColor(Color.WHITE);ms.R2=0;break;
                case R.id.buttonR3:R3.setBackgroundColor(Color.WHITE);ms.R3=0;break;
                case R.id.buttonL3:L3.setBackgroundColor(Color.WHITE);ms.L3=0;break;
                case R.id.buttonOption:OPkey.setBackgroundColor(Color.WHITE);ms.OPKEY=0;break;
                case R.id.buttonShare:SHARE.setBackgroundColor(Color.WHITE);ms.SHARE=0;break;
            }
        }
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.Button_touchlock){
            if(locked==true){
                Touch.setText("LOCK");
                Sensitive.setVisibility(View.VISIBLE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }else{
                Touch.setText("UNLOCK");
                Sensitive.setVisibility(View.INVISIBLE);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


            }
            locked=!locked;
        }
    }
}