package com.plawyue.wiimotedsu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    private SensorManager sensorManager;
    private Sensor sensor;
    MontionServer ms=new MontionServer();
    Button button_A,button_B,button_DUP,button_DDOWN,button_DLEFT,button_DRight,button_PLUS,button_DEDUCE,BUTTON_home;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button_A=findViewById(R.id.button_A);
        button_B=findViewById(R.id.button_B);
        button_DUP=findViewById(R.id.button_dUp);
        button_DDOWN=findViewById(R.id.button_Ddown);
        button_DLEFT=findViewById(R.id.button_Dleft);
        button_DRight=findViewById(R.id.button_Dright);
        button_PLUS=findViewById(R.id.button_PLUS);
        button_DEDUCE=findViewById(R.id.button_DEDUCE);
        BUTTON_home=findViewById(R.id.button_home);
        button_A.setOnTouchListener(this);
        button_B.setOnTouchListener(this);
        button_DUP.setOnTouchListener(this);
        button_DDOWN.setOnTouchListener(this);
        button_DLEFT.setOnTouchListener(this);
        button_DRight.setOnTouchListener(this);
        button_PLUS.setOnTouchListener(this);
        button_DEDUCE.setOnTouchListener(this);
        BUTTON_home.setOnTouchListener(this);

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
        float[] gravity = {0,0,0};
        float[] linear_acceleration = {0,0,0};
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        SensorEventListener sensorListener = new SensorEventListener() {
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
            public void onSensorChanged(SensorEvent event) {

                final float alpha = (float) 0.9;
                // Isolate the force of gravity with the low-pass filter.
                gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
                gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
                gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
                // Remove the gravity contribution with the high-pass filter.
                linear_acceleration[0] = event.values[0] - gravity[0];
                linear_acceleration[1] = event.values[1] - gravity[1];
                linear_acceleration[2] = event.values[2] - gravity[2];
                DecimalFormat df = new DecimalFormat("#.00");
                ms.accX= Float.parseFloat(df.format(linear_acceleration[0]));
                ms.accY=Float.parseFloat(df.format(linear_acceleration[1]));
                ms.accZ=Float.parseFloat(df.format(linear_acceleration[2]));
//                System.out.println("x轴"+String.valueOf(ms.accX));
//                System.out.println("y轴"+String.valueOf(ms.accY));
//                System.out.println("z轴"+String.valueOf(ms.accZ));


            }
        };
        SensorEventListener gyrolinster=new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                ms.gyroR=sensorEvent.values[0];
                ms.gyroY=sensorEvent.values[1];
                ms.gyroP=sensorEvent.values[2];
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        sensorManager.registerListener(sensorListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
                SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(gyrolinster,
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_UI);
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
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

            }
        }else if( motionEvent.getAction() == MotionEvent.ACTION_UP){
            switch (view.getId()){
                case R.id.button_A: button_A.setBackgroundColor(Color.BLUE);ms.A=0;break;
                case R.id.button_B: button_B.setBackgroundColor(Color.BLUE); ms.B=0;break;
                case R.id.button_dUp: button_DUP.setBackgroundColor(Color.BLUE); ms.Dpad_UP=0;break;
                case R.id.button_Ddown: button_DDOWN.setBackgroundColor(Color.BLUE); ms.Dpad_Down=0;break;
                case R.id.button_Dleft: button_DLEFT.setBackgroundColor(Color.BLUE); ms.Dpad_Left=0;break;
                case R.id.button_Dright: button_DRight.setBackgroundColor(Color.BLUE); ms.Dpad_Right=0;break;
                case R.id.button_PLUS:button_PLUS.setBackgroundColor(Color.BLUE); ms.R1=0;break;
                case R.id.button_DEDUCE:button_DEDUCE.setBackgroundColor(Color.BLUE); ms.L1=0;break;
                case R.id.button_home:BUTTON_home.setBackgroundColor(Color.BLUE);ms.PS=0;break;
            }
        }

        return false;
    }
}