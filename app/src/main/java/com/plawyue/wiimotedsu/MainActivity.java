package com.plawyue.wiimotedsu;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private SensorManager sensorManager;
    private Sensor sensor;
    MontionServer ms=new MontionServer();
    static double PI = 3.1415926535897932;
    Button L3,R3,OPkey,SHARE,Colorx;
    Boolean locked=false;
    Button button_A,button_B,button_DUP,button_DDOWN,button_DLEFT,button_DRight,button_PLUS,button_DEDUCE,BUTTON_home,L2,R2,Touch,button_X,button_Y;
    EditText Sensitive;
    Boolean isEditMode=false;
    static float METER_PER_SECOND_SQUARED_TO_G = (float) 9.8066;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerReceiver(mBatInfoReveiver, new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED));
        Sensitive=findViewById(R.id.Sensitive);
        Switch editmodeswitch=findViewById(R.id.editmode);
        editmodeswitch.setOnCheckedChangeListener(this);
        Touch=findViewById(R.id.Button_touchlock);

        Touch.setOnClickListener(this);
        button_A=findViewById(R.id.button_A);
        button_X=findViewById(R.id.Square);
        button_Y=findViewById(R.id.Tri);
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
        button_Y.setOnTouchListener(this);
        button_X.setOnTouchListener(this);
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

        button_Y.setOnClickListener(this);
        button_X.setOnClickListener(this);
        L3.setOnClickListener(this);
        R3.setOnClickListener(this);
        SHARE.setOnClickListener(this);
        OPkey.setOnClickListener(this);
        L2.setOnClickListener(this);
        R2.setOnClickListener(this);
        button_A.setOnClickListener(this);
        button_B.setOnClickListener(this);
        button_DUP.setOnClickListener(this);
        button_DDOWN.setOnClickListener(this);
        button_DLEFT.setOnClickListener(this);
        button_DRight.setOnClickListener(this);
        button_PLUS.setOnClickListener(this);
        button_DEDUCE.setOnClickListener(this);
        BUTTON_home.setOnClickListener(this);

        SharedPreferences userInfo = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
        SharedPreferences.Editor editor = userInfo.edit();//获取Editor
        if(userInfo.contains("Sensitivesave")==false){
            editor.putString("Sensitivesave","0.9");
            editor.commit();
        }
        Sensitive.setText(userInfo.getString("Sensitivesave","0"));
        Sensitive.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                editor.putString("Sensitivesave",Sensitive.getText().toString());
                editor.commit();
                Toast.makeText(MainActivity.this,"Sensitive has saved", Toast.LENGTH_LONG).show();
                return false;
            }
        });
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
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

        loadbutton();

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
                case R.id.button_A: button_A.setBackgroundColor(Color.GREEN);ms.A=255;break;
                case R.id.button_B: button_B.setBackgroundColor(Color.GREEN); ms.B=255;break;
                case R.id.Square: button_X.setBackgroundColor(Color.GREEN); ms.X=255;break;
                case R.id.Tri: button_Y.setBackgroundColor(Color.GREEN); ms.Y=255;break;
                case R.id.button_dUp: button_DUP.setBackgroundColor(Color.GREEN); ms.Dpad_UP=255;break;
                case R.id.button_Ddown: button_DDOWN.setBackgroundColor(Color.GREEN); ms.Dpad_Down=255;break;
                case R.id.button_Dleft: button_DLEFT.setBackgroundColor(Color.GREEN); ms.Dpad_Left=255;break;
                case R.id.button_Dright:button_DRight.setBackgroundColor(Color.GREEN); ms.Dpad_Right=255;break;
                case R.id.button_PLUS:button_PLUS.setBackgroundColor(Color.GREEN); ms.R1=255;break;
                case R.id.button_DEDUCE:button_DEDUCE.setBackgroundColor(Color.GREEN); ms.L1=255;break;
                case R.id.button_home:BUTTON_home.setBackgroundColor(Color.GREEN);ms.PS=1;break;
                case R.id.button_L2:L2.setBackgroundColor(Color.GREEN);ms.L2=255;break;
                case R.id.button_R2:R2.setBackgroundColor(Color.GREEN);ms.R2=255;break;
                case R.id.buttonR3:R3.setBackgroundColor(Color.GREEN);ms.R3=1;break;
                case R.id.buttonL3:L3.setBackgroundColor(Color.GREEN);ms.L3=1;break;
                case R.id.buttonOption:OPkey.setBackgroundColor(Color.GREEN);ms.OPKEY=1;break;
                case R.id.buttonShare:SHARE.setBackgroundColor(Color.GREEN);ms.SHARE=1;break;
            }
        }
        else if( motionEvent.getAction() == MotionEvent.ACTION_UP){
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
            switch (view.getId()){
                case R.id.button_A: button_A.setBackgroundColor(Color.parseColor("#497367"));ms.A=0;break;
                case R.id.button_B: button_B.setBackgroundColor(Color.parseColor("#497367")); ms.B=0;break;
                case R.id.Square: button_X.setBackgroundColor(Color.parseColor("#497367")); ms.X=0;break;
                case R.id.Tri: button_Y.setBackgroundColor(Color.parseColor("#497367")); ms.Y=0;break;
                case R.id.button_dUp: button_DUP.setBackgroundColor(Color.parseColor("#497367")); ms.Dpad_UP=0;break;
                case R.id.button_Ddown: button_DDOWN.setBackgroundColor(Color.parseColor("#497367")); ms.Dpad_Down=0;break;
                case R.id.button_Dleft: button_DLEFT.setBackgroundColor(Color.parseColor("#497367")); ms.Dpad_Left=0;break;
                case R.id.button_Dright: button_DRight.setBackgroundColor(Color.parseColor("#497367")); ms.Dpad_Right=0;break;
                case R.id.button_PLUS:button_PLUS.setBackgroundColor(Color.parseColor("#497367")); ms.R1=0;break;
                case R.id.button_DEDUCE:button_DEDUCE.setBackgroundColor(Color.parseColor("#497367")); ms.L1=0;break;
                case R.id.button_home:BUTTON_home.setBackgroundColor(Color.parseColor("#497367"));ms.PS=0;break;
                case R.id.button_L2:L2.setBackgroundColor(Color.parseColor("#497367"));ms.L2=0;break;
                case R.id.button_R2:R2.setBackgroundColor(Color.parseColor("#497367"));ms.R2=0;break;
                case R.id.buttonR3:R3.setBackgroundColor(Color.parseColor("#497367"));ms.R3=0;break;
                case R.id.buttonL3:L3.setBackgroundColor(Color.parseColor("#497367"));ms.L3=0;break;
                case R.id.buttonOption:OPkey.setBackgroundColor(Color.parseColor("#497367"));ms.OPKEY=0;break;
                case R.id.buttonShare:SHARE.setBackgroundColor(Color.parseColor("#497367"));ms.SHARE=0;break;
            }
        }
        }
        return false;
    }

    @Override
    public void onClick(View view) {

        if(view.getId()==R.id.Button_touchlock){
            getWindow().getDecorView().performHapticFeedback(HapticFeedbackConstants.CONFIRM);
            if(locked==true){
                Touch.setText("LOCK");
                Sensitive.setVisibility(View.VISIBLE);
                findViewById(R.id.LOCK).setVisibility(View.INVISIBLE);
                findViewById(R.id.editmode).setVisibility(View.VISIBLE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }else{
                Touch.setText("UNLOCK");
                Sensitive.setVisibility(View.INVISIBLE);
                findViewById(R.id.LOCK).setVisibility(View.VISIBLE);
                findViewById(R.id.editmode).setVisibility(View.INVISIBLE);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
            locked=!locked;
        }

        if(isEditMode) {
            switch (view.getId()) {
                case R.id.button_A:Inputbox(button_A,"ButtonA");break;
                case R.id.button_B:Inputbox(button_B,"ButtonB");break;
                case R.id.Square: Inputbox(button_X,"ButtonX");break;
                case R.id.Tri: Inputbox(button_Y,"ButtonY");break;
                case R.id.button_dUp: Inputbox(button_DUP,"ButtonDup");break;
                case R.id.button_Ddown: Inputbox(button_DDOWN,"ButtonDdown");break;
                case R.id.button_Dleft: Inputbox(button_DLEFT,"ButtonDleft");break;
                case R.id.button_Dright:Inputbox(button_DRight,"ButtonDright");break;
                case R.id.button_PLUS:Inputbox(button_PLUS,"Buttonplus");break;
                case R.id.button_DEDUCE:Inputbox(button_DEDUCE,"Buttondeduce");break;
                case R.id.button_home:Inputbox(BUTTON_home,"Buttonhome");break;
                case R.id.button_L2:Inputbox(L2,"ButtonL2");break;
                case R.id.button_R2:Inputbox(R2,"ButtonR2");break;
                case R.id.buttonR3:Inputbox(R3,"ButtonR3");break;
                case R.id.buttonL3:Inputbox(L3,"ButtonL3");break;
                case R.id.buttonOption:Inputbox(OPkey,"ButtonOP");break;
                case R.id.buttonShare:Inputbox(SHARE,"ButtonSh");break;
            }
        }
    }
    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
        getWindow().getDecorView().performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        switch (keyCode) {
// 音量减小
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                button_A.setBackgroundColor(Color.GREEN);ms.A=255;
                return true;
// 音量增大
            case KeyEvent.KEYCODE_VOLUME_UP:
                button_B.setBackgroundColor(Color.GREEN); ms.B=255;
                return true;
            case KeyEvent.KEYCODE_BACK:
                if(locked){
                return true;}
        }
        return super.onKeyDown (keyCode, event);
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event){
        getWindow().getDecorView().performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                button_A.setBackgroundColor(Color.parseColor("#497367"));ms.A=0;
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                button_B.setBackgroundColor(Color.parseColor("#497367")); ms.B=0;
                return true;
        }
        return super.onKeyUp (keyCode, event);
    }
    private void onBatteryInfoReceiver(int intLevel, int intScale) {
        // TODO Auto-generated method stub
        int percent = intLevel*100/ intScale;
        if(percent>90){
            ms.battery=0x05;
        }
        else if(percent>80&&percent<90){
            ms.battery=0x04;
        }
        else if(percent<80&&percent>40){
            ms.battery=0x03;
        }else if(percent<40&&percent>20){
            ms.battery=0x02;
        }else if(percent<20){
            ms.battery=0x01;
        }
    };
    //创建BroadcastReceiver
    private BroadcastReceiver mBatInfoReveiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //如果捕捉到的Action是ACTION_BATTERY_CHANGED则运行onBatteryInforECEIVER()
            if(intent.ACTION_BATTERY_CHANGED.equals(action))
            {
                //获得当前电量
                int intLevel = intent.getIntExtra("level",0);
                //获得手机总电量
                int intScale = intent.getIntExtra("scale",100);
                // 在下面会定义这个函数，显示手机当前电量
                onBatteryInfoReceiver(intLevel, intScale);
            }
        }
    };


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(b){
            isEditMode=true;
        }else{
            isEditMode=false;
            Toast.makeText(this,"Button Text has saved",Toast.LENGTH_LONG).show();
        }
    }
    private void Inputbox(Button mbutton,String Buttonname){
        final EditText inputServer = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Input Button Text").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                .setNegativeButton("Cancel", null);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String result =inputServer.getText().toString();
                mbutton.setText(result);
                SharedPreferences userInfo = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
                SharedPreferences.Editor editor = userInfo.edit();//获取Editor
                editor.putString(Buttonname,result);
                editor.commit();
            }
        });
        builder.show();
    }
    private void loadbutton(){
        loadbuttontext(button_A,"ButtonA");
        loadbuttontext(button_B,"ButtonB");
        loadbuttontext(button_X,"ButtonX");
        loadbuttontext(button_Y,"ButtonY");
        loadbuttontext(button_DUP,"ButtonDup");
        loadbuttontext(button_DDOWN,"ButtonDdown");
        loadbuttontext(button_DLEFT,"ButtonDleft");
        loadbuttontext(button_DRight,"ButtonDright");
        loadbuttontext(button_PLUS,"Buttonplus");
        loadbuttontext(button_DEDUCE,"Buttondeduce");
        loadbuttontext(BUTTON_home,"Buttonhome");
        loadbuttontext(L2,"ButtonL2");
        loadbuttontext(R2,"ButtonR2");
        loadbuttontext(R3,"ButtonR3");
        loadbuttontext(L3,"ButtonL3");
        loadbuttontext(OPkey,"ButtonOP");
        loadbuttontext(SHARE,"ButtonSh");
    }
    private void loadbuttontext(Button mbutton,String Buttonname){
        SharedPreferences userInfo = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
        String text;
        text=userInfo.getString(Buttonname,"NonexistFLAG@");
        String Dont="NonexistFLAG@";
        if(text.equals(Dont)){
            return;
        }
        mbutton.setText(text);
    }
}